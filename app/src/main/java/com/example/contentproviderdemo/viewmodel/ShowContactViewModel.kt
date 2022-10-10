package com.example.contentproviderdemo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.contentproviderdemo.modal.ContactRepository

class ShowContactViewModel(var contactRepo : ContactRepository , var context : Context) : ViewModel() {


    fun getDeleteUser(uid :Long){
        contactRepo.deleteUser(uid)
    }
}