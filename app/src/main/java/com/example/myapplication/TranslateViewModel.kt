package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TranslateViewModel(mm: Int, flib: Int) : ViewModel() {
    val mode : LiveData<Int>
        get() = _mode

    val flag : LiveData<Int?>
        get() = _flib

    private val _mode = MutableLiveData<Int>()

    private val _flib = MutableLiveData<Int?>()

    init{
        _mode.value = mm
        _flib.value = flib
    }

    fun change(){
        val c = _flib.value?.xor(1)
        _flib.value = c
    }

}