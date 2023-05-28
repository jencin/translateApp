package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TranslateViewModelFactory(private val mode: Int, private val flip: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TranslateViewModel(mode,flip) as T
    }
}