package com.example.sns_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SnsViewModelFactory(private val email:String):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SnsViewModel::class.java)){
            return SnsViewModel(email) as T
        }
        throw IllegalAccessException("Unkown ViewModel class")
    }

}