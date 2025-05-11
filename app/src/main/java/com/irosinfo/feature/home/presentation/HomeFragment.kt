package com.irosinfo.feature.home.presentation

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.irosinfo.ui_component.custom_progress_dialog.ProgressDialog
import com.utils.utils_module.CommonUtils.load
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class HomeFragment : BaseFragment<FragmentHomeBinding>(), IrosScanHandler {

    override fun layoutInflater() = FragmentHomeBinding.inflate(layoutInflater)


    private val viewModel: HomeViewModel by viewModels()

    private var previewCaptureImageAdapter: PreviewCaptureImageAdapter? = null

    private val progressDialog: ProgressDialog by inject { parametersOf(requireActivity()) }

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

    private fun FragmentHomeBinding.checkAndRequestStoragePermission() {
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
                binding.savePhoto()
            } else {
                showShortToast("Permission denied")
            }
        }


    private fun FragmentHomeBinding.savePhoto() {
        val iros = irosNumberEt.text.toString()
        val imageDataList = viewModel.byteArrayList
        savePhotosIncremented(imageDataList = imageDataList, groupName = "IROS$iros")
    }


    private fun savePhotosIncremented(imageDataList: List<ByteArray>, groupName: String) {
        val contentResolver = requireContext().contentResolver
        val relativePath = "${Environment.DIRECTORY_PICTURES}/$groupName"
        if (isFolderExists(contentResolver = contentResolver, relativePath = relativePath)) {
            showShortToast("This folder already exists")
            return
        }
        imageDataList.forEachIndexed { index, imageData ->
            val contentValues = ContentValues().apply {
                val next = index + 1
                put(MediaStore.Images.Media.DISPLAY_NAME, "$groupName-$next.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            }

            val imageUri: Uri? = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
            )
            imageUri?.let {
                try {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(imageData)
                    }
                } catch (e: Exception) {
                    showShortToast("Failed to save photo: ${e.message}")
                }
            }
        }
        showShortToast("Successful saving data")
    }


    private fun isFolderExists(contentResolver: ContentResolver, relativePath: String): Boolean {
        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("$relativePath%")
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null
        )
        val folderExists = cursor?.use { it.count > 0 } ?: false
        return folderExists
    }


    private fun FragmentHomeBinding.clearData() {
        irosNumberEt.setText("")
        viewModel.clearByteArrayList()
        previewCaptureImageAdapter?.resetPreviewCaptureAdapter()
        handleImagePreviewGroupVisibility(isShow = viewModel.byteArrayList.isEmpty())
    }
}