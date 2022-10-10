package com.example.contentproviderdemo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.contentproviderdemo.modal.ContactRepository
import com.example.contentproviderdemo.util.Contact

class EditContactViewModel(val contacRepo : ContactRepository, val context : Context)  : ViewModel() {

    fun editContact(contact : Contact){
        contacRepo.editContact(contact)
    }
}