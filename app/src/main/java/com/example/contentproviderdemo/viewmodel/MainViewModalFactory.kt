package com.example.contentproviderdemo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contentproviderdemo.modal.ContactRepository
import com.example.contentproviderdemo.util.ViewModelTypes

class MainViewModalFactory(val repo : ContactRepository, val context : Context, val arg: ViewModelTypes) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when(arg.name){
             ViewModelTypes.ADDCONTACTFRAGMENTVIEWMODEL.name ->{
                return AddContactFragmentViewModel(repo,context) as T
            }
            ViewModelTypes.MAINVIEWMODEL.name ->{
                return MainViewModel(repo,context) as T
            }
            ViewModelTypes.EDITCONTACTVIEWMODEL.name ->{
                return EditContactViewModel(repo,context) as T
            }
            ViewModelTypes.SHOWCONTACTVIEWMODEL.name->{
                return ShowContactViewModel(repo,context) as T
            }
            else-> {
                return MainViewModel(repo,context) as T
            }
        }
    }
}