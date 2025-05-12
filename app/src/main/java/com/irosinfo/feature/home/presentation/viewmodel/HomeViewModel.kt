package com.irosinfo.feature.home.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irosinfo.feature.domain.enums.SponsorshipType

class HomeViewModel : ViewModel() {

    var sponsorshipType = MutableLiveData(SponsorshipType.IROS)

    private val byteArrayImageList = mutableListOf<ByteArray>()


    val byteArrayList get() = this.byteArrayImageList

    val byteArrayListSize get() = this.byteArrayList.size

    fun setByteArrayList(byteArray: ByteArray) {
        byteArrayImageList.add(byteArray)
    }

    fun clearByteArrayList() = this.byteArrayList.clear()

}