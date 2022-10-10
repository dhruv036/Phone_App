package com.example.contentproviderdemo.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contentproviderdemo.util.Contact
import com.example.contentproviderdemo.modal.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(val contacRepo : ContactRepository, val context : Context) : ViewModel() {

    var contactListFromContentProvider = MutableLiveData<MutableList<Contact>>()
    var isFetchFromDb = false


    fun fetchDataFromContentProvider(){
        if(!isFetchFromDb){
            viewModelScope.launch(Dispatchers.IO) {
                contactListFromContentProvider.postValue(contacRepo.fetchDataFromContentProvider(context))
            }
        }

    }

    fun storeContactInDB(contact: Contact){
        viewModelScope.launch(Dispatchers.IO){
            contacRepo.storeinDB(contact,null)
        }
    }
}