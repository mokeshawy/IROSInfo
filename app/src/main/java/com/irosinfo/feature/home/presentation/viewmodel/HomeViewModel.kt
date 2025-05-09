package com.irosinfo.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val byteArrayImageList = mutableListOf<ByteArray>()


    val byteArrayList get() = this.byteArrayImageList

    val byteArrayListSize get() = this.byteArrayList.size

    fun setByteArrayList(byteArray: ByteArray) {
        byteArrayImageList.add(byteArray)
    }

    fun clearByteArrayList() = this.byteArrayList.clear()

}