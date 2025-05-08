package com.irosinfo.feature.home.presentation

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.bases.bases_module.base_fragment.BaseFragment
import com.camera.camera_module.camerax.image_capture.ImageRequest
import com.camera.camera_module.camerax.model.SerializableBitmap
import com.camera.camera_module.camerax.scan_qrcode.qrcode_manager.QrcodeManager
import com.extensions.extensions_module.fragment_extensions.navigate
import com.irosinfo.R
import com.irosinfo.core.IrosScanHandler
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
        setAppBarImage()
        setOnScanIrosBtnClicked()
        setOnCaptureImageBtnClicked()
        handleImagePreviewGroupVisibility(isShow = viewModel.bitmapList.isEmpty())
        setUpPreviewCaptureImageAdapter()
        setOnClearBtnClicked()
        setOnCloseIvClicked()
    }

    private fun FragmentHomeBinding.setAppBarImage() {
        appBarView.setTitleBarTv(R.string.paymentsDistribution)
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
        viewModel.setBitmapImage(serializableBitmap.bitmap)
        previewCaptureImageAdapter?.addBitmaps(viewModel.bitmapList)
        binding.handleImagePreviewGroupVisibility(isShow = viewModel.bitmapList.isEmpty())
    }

    private fun FragmentHomeBinding.handleImagePreviewGroupVisibility(isShow: Boolean) {
        emptyImageTv.isVisible = isShow
        imagePreviewGroup.isVisible = !isShow
    }

    private fun FragmentHomeBinding.setUpPreviewCaptureImageAdapter() {
        previewCaptureImageAdapter =
            PreviewCaptureImageAdapter(mutableListOf(), onItemClicked = { bitmap ->
                setOnImageClicked(bitmap = bitmap)
            })
        imagePreviewRV.adapter = previewCaptureImageAdapter
    }

    private fun FragmentHomeBinding.setOnClearBtnClicked() = clearBtn.setOnClickListener {
        irosNumberEt.setText("")
        viewModel.clearBitmapList()
        previewCaptureImageAdapter?.resetPreviewCaptureAdapter()
        handleImagePreviewGroupVisibility(isShow = viewModel.bitmapList.isEmpty())
    }

    private fun FragmentHomeBinding.setOnImageClicked(bitmap: Bitmap) {
        previewImageIv.load(context = requireContext(), image = bitmap)
        presentImageGroup.isVisible = true
    }

    private fun FragmentHomeBinding.setOnCloseIvClicked() = closeIv.setOnClickListener {
        presentImageGroup.isVisible = false
    }
}