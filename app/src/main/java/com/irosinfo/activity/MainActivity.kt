package com.irosinfo.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.bases.bases_module.base_activity.BaseActivity
import com.camera.camera_module.camerax.scan_qrcode.qrcode_manager.QrcodeManager
import com.camera.camera_module.camerax.utils.Constants
import com.common.common_module.error.AppError
import com.common.common_module.error.QrCodeScanException
import com.irosinfo.R
import com.irosinfo.core.iros_scan_handler.SponsorshipNumberScanHandler
import com.irosinfo.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, Int>(), QrcodeManager {

    override val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override val navHostResourceId = R.id.navHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

    }

    override fun startScanQrcode() {
        getCurrentFragment()?.setFragmentResultListener(Constants.FRAGMENT_QR_RESULT_REQUEST_KEY) { _, bundle ->
            val result = bundle.getString(Constants.FRAGMENT_QR_CODE_KEY, "")
            getScanQrcodeLogException(result)
            handleScanIros(result)
            getCurrentFragment()?.clearFragmentResultListener(Constants.FRAGMENT_QR_RESULT_REQUEST_KEY)
        }
        getCurrentFragment()?.findNavController()?.navigate(com.camera.camera_module.R.id.qr_scan_nav_graph)
    }


    private fun getScanQrcodeLogException(serialNumber: String) {
        val error = AppError.E(
            exception = QrCodeScanException(),
            logMessage = "Scan card from Qrcode",
            extraData = serialNumber
        )
        handleError(error)
    }

    private fun handleScanIros(irosNumber: String) {
        (getCurrentFragment() as? SponsorshipNumberScanHandler)?.setSponsorshipNumber(irosNumber)
    }
}