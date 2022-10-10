package com.example.contentproviderdemo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contentproviderdemo.util.Contact
import com.example.contentproviderdemo.modal.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddContactFragmentViewModel(val contacRepo : ContactRepository, val context : Context) : ViewModel() {

    fun storeContactInDB(contact: Contact){
        viewModelScope.launch(Dispatchers.IO){
            contacRepo.storeinDB(contact,null)
        }
    }
}