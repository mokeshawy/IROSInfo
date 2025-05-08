package com.irosinfo.ui_component.custom_views.app_bar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.irosinfo.databinding.LayoutAppBarViewBinding

class AppBarView(context: Context, attributes: AttributeSet? = null) :
    FrameLayout(context, attributes) {

    private val binding by lazy {
        LayoutAppBarViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        binding.root
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun setRightAppBarIcon(@DrawableRes img: Int) {
        binding.rightAppBarIv.isVisible = true
        binding.rightAppBarIv.setImageDrawable(context.getDrawable(img))
    }

    fun setOnRightAppBarIvClicked(onBackClicked: () -> Unit) =
        binding.rightAppBarIv.setOnClickListener { onBackClicked() }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun setLiftAppBarIcon(@DrawableRes img: Int) {
        binding.liftAppBarIv.isVisible = true
        binding.liftAppBarIv.setImageDrawable(context.getDrawable(img))
    }

    fun setOnLiftAppBarIvClicked(onBackClicked: () -> Unit) =
        binding.liftAppBarIv.setOnClickListener { onBackClicked() }


    fun setTitleBarTv(@StringRes title: Int) {
        binding.titleBarTv.setText(title)
    }

    fun setTitleBarTv(title: String) {
        binding.titleBarTv.text = title
    }

    fun setBackground(@ColorRes color: Int) {
        binding.cardRootView.setCardBackgroundColor(ContextCompat.getColor(context, color))
    }
}