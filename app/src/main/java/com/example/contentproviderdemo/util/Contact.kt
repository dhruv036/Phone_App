package com.example.contentproviderdemo.util

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    var id:Long?,
    var name:String? = null,
    var number: MutableMap<String, String>? = null,
    var email: MutableMap<String, String>? =null,
    var address: MutableMap<String, String>? =null,
    var image: String?= null
    ) : Parcelable