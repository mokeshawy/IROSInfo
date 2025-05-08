package com.irosinfo.feature.home.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val bitmapImageList = mutableListOf<Bitmap>()


    val bitmapList get() = this.bitmapImageList

    val bitMabListSize get() = this.bitmapList.size

    fun setBitmapImage(bitmap: Bitmap) {
        bitmapImageList.add(bitmap)
    }

    fun clearBitmapList() = this.bitmapList.clear()

}