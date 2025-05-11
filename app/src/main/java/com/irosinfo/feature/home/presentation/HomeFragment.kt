package com.irosinfo.feature.home.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.bases.bases_module.base_fragment.BaseFragment
import com.camera.camera_module.camerax.image_capture.ImageRequest
import com.camera.camera_module.camerax.model.SerializableBitmap
import com.camera.camera_module.camerax.scan_qrcode.qrcode_manager.QrcodeManager
import com.extensions.extensions_module.extensions.bitmapToByteArray
import com.extensions.extensions_module.fragment_extensions.navigate
import com.irosinfo.R
import com.irosinfo.core.iros_scan_handler.IrosScanHandler
import com.irosinfo.databinding.FragmentHomeBinding
import com.irosinfo.feature.home.presentation.adapter.PreviewCaptureImageAdapter
import com.irosinfo.feature.home.presentation.viewmodel.HomeViewModel
import com.utils.utils_module.CommonUtils.load

class HomeFragment : BaseFragment<FragmentHomeBinding>(), IrosScanHandler {

    override fun layoutInflater() = FragmentHomeBinding.inflate(layoutInflater)


    private val viewModel: HomeViewModel by viewModels()

    private var previewCaptureImageAdapter: PreviewCaptureImageAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setUpViews()
    }

    private fun FragmentHomeBinding.setUpViews() {
        setOnScanIrosBtnClicked()
        setOnCaptureImageBtnClicked()
        handleImagePreviewGroupVisibility(isShow = viewModel.byteArrayList.isEmpty())
        setUpPreviewCaptureImageAdapter(byteArray = viewModel.byteArrayList)
        setOnClearBtnClicked()
        setOnCloseIvClicked()
        setOnSaveBtnClicked()
    }

    private fun FragmentHomeBinding.setOnScanIrosBtnClicked() = scanBtn.setOnClickListener {
        (activity as QrcodeManager).startScanQrcode()
    }


    override fun setIrosNumber(irosNumber: String) {
        binding.irosNumberEt.setText(irosNumber)
    }


    private fun FragmentHomeBinding.setOnCaptureImageBtnClicked() =
        captureImagerBtn.setOnClickListener {
            if (irosNumberEt.text.isNullOrEmpty()) {
                showShortToast(getString(R.string.pleaseEntroIrosNumber))
                return@setOnClickListener
            }
            navigateToCaptureImage()
        }

    private fun navigateToCaptureImage() {
        setFragmentResultForImageCapture()
        navigate(R.id.imageCaptureFragment)
    }

    private fun setFragmentResultForImageCapture() {
        setFragmentResultListener(ImageRequest.IMAGE_REQUEST_KEY) { _, bundle ->
            val serializableBitmap = getSerializableBitmap(bundle)
            serializableBitmap?.let { onImageCaptured(it) }
            clearFragmentResultListener(ImageRequest.IMAGE_REQUEST_KEY)
        }
    }

    private fun getSerializableBitmap(bundle: Bundle): SerializableBitmap? {
        val serializableBitmap = getSerializableBitmapFromBundle(bundle)
        return serializableBitmap as SerializableBitmap?
    }

    private fun getSerializableBitmapFromBundle(bundle: Bundle) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(ImageRequest.IMAGE_KEY, SerializableBitmap::class.java)
        } else {
            bundle.getSerializable(ImageRequest.IMAGE_KEY)
        }

    private fun onImageCaptured(serializableBitmap: SerializableBitmap) {
        val byteArray = serializableBitmap.bitmap.bitmapToByteArray()
        viewModel.setByteArrayList(byteArray = byteArray)
        binding.handleImagePreviewGroupVisibility(isShow = viewModel.byteArrayList.isEmpty())
    }


    private fun FragmentHomeBinding.handleImagePreviewGroupVisibility(isShow: Boolean) {
        emptyImageTv.isVisible = isShow
        imagePreviewGroup.isVisible = !isShow
    }

    private fun FragmentHomeBinding.setUpPreviewCaptureImageAdapter(byteArray: MutableList<ByteArray> = mutableListOf()) {
        previewCaptureImageAdapter =
            PreviewCaptureImageAdapter(byteArray, onItemClicked = { bitmap ->
                setOnImageClicked(bitmap = bitmap)
            })
        imagePreviewRV.adapter = previewCaptureImageAdapter
    }

    private fun FragmentHomeBinding.setOnClearBtnClicked() = clearBtn.setOnClickListener {
        clearData()
    }

    private fun FragmentHomeBinding.setOnImageClicked(bitmap: Bitmap) {
        previewImageIv.load(context = requireContext(), image = bitmap)
        presentImageGroup.isVisible = true
    }

    private fun FragmentHomeBinding.setOnCloseIvClicked() = closeIv.setOnClickListener {
        presentImageGroup.isVisible = false
    }

    private fun FragmentHomeBinding.setOnSaveBtnClicked() = saveBtn.setOnClickListener {
        checkAndRequestStoragePermission()
    }

    private fun checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePhoto()
            return
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            savePhoto()
        } else {
            requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                savePhoto()
            } else {
                showShortToast("Permission denied")
            }
        }


    private fun savePhoto() {
        openDocumentTreeLauncher.launch(null)
    }

    private val openDocumentTreeLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) {
                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)
                val iros = binding.irosNumberEt.text.toString()
                val imageDataList = viewModel.byteArrayList
                savePhotosToSdCardUsingSAF(
                    imageDataList = imageDataList, folderName = "IROS$iros", uri = uri
                )
            } else {
                showShortToast("No folder selected")
            }
        }

    private fun savePhotosToSdCardUsingSAF(
        imageDataList: List<ByteArray>, folderName: String, uri: Uri?
    ) {
        val baseUriString = uri ?: run {
            showShortToast("SD card folder not selected")
            return
        }

        val baseTreeUri = Uri.parse(baseUriString.toString())
        val pickedDir = DocumentFile.fromTreeUri(requireContext(), baseTreeUri)

        if (isFolderExistsInSdCard(baseUriString, folderName = folderName)) {
            showShortToast("Folder already exist")
            return
        }

        val newFolder = pickedDir?.createDirectory(folderName)
        if (newFolder == null) {
            showShortToast("Unable to create folder on SD card")
            return
        }

        imageDataList.forEachIndexed { index, imageData ->
            val fileName = "$folderName-${index + 1}.jpg"
            val imageFile = newFolder.createFile("image/jpeg", fileName)
            imageFile?.uri?.let { uri ->
                try {
                    requireContext().contentResolver.openOutputStream(uri)?.use {
                        it.write(imageData)
                    }
                } catch (e: Exception) {
                    showShortToast("Failed to save image: ${e.message}")
                }
            }
        }
        showShortToast("Images saved to SD card")
    }

    private fun isFolderExistsInSdCard(sdCardRootUri: Uri, folderName: String): Boolean {
        val pickedDir = DocumentFile.fromTreeUri(requireContext(), sdCardRootUri) ?: return false
        return pickedDir.findFile(folderName)?.isDirectory == true
    }


    private fun FragmentHomeBinding.clearData() {
        irosNumberEt.setText("")
        viewModel.clearByteArrayList()
        previewCaptureImageAdapter?.resetPreviewCaptureAdapter()
        handleImagePreviewGroupVisibility(isShow = viewModel.byteArrayList.isEmpty())
    }
}