package com.cooling.artifact.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cooling.artifact.utils.StorageUtils

class RecordViewModel : ViewModel() {

    val record = MutableLiveData<Int>()

    fun temperatureRecord() {
        StorageUtils.isAcceptPolicyAndTerms()
    }

}