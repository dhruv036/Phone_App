package com.example.contentproviderdemo.viewmodel

import android.R
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contentproviderdemo.util.Contact
import com.example.contentproviderdemo.modal.db.database.ContactDatabase
import com.example.contentproviderdemo.modal.db.entity.AddressEntity
import com.example.contentproviderdemo.modal.db.entity.ContactEntity
import com.example.contentproviderdemo.modal.db.entity.EmailEntity
import com.example.contentproviderdemo.modal.db.entity.PhoneNumberEntity
import java.io.File
import java.io.FileOutputStream


class ContactViewModel(val context : Context) : ViewModel() {
    private var contactList = MutableLiveData<MutableList<Contact>>()
    val db = ContactDatabase.getInstance(context.applicationContext)
    val contactDao = db.contactDao()

    fun getContacts() : MutableLiveData<MutableList<Contact>> {
        return contactList
    }

    fun getDataFromDB(entity : ContactEntity) : Contact {
        val db = ContactDatabase.getInstance(context.applicationContext)
        val userDao = db.contactDao()
        var name: String? = null
        var number: MutableMap<String, String>? = mutableMapOf()
        var email: MutableMap<String, String>? = mutableMapOf()
        var address: MutableMap<String, String>? = mutableMapOf()
        name = entity.name
        val uid =  entity.id

        var list = userDao.fetchNumberWithUID(entity.id)
        for (i in list){
            number!![i.phonetype] = i.phoneNumber
        }
        var list2 = userDao.fetchEmailWithUID(entity.id)

        for (i in list2){
            email!![i.emailType] = i.email
        }
        var list3 = userDao.fetchAddressWithUID(entity.id)
        for (i in list3){
            address!![i.addressType] = i.address
        }
        var photoPath = "" + R.drawable.ic_btn_speak_now
        entity.image?.let{ abc->
           photoPath = byteArraytoString(abc,uid)
        }
        return Contact(uid,name,number,email, address,photoPath)
    }

    fun byteArraytoString(abc : ByteArray,uid : Long): String{
        var photoPath = "" + R.drawable.ic_btn_speak_now
        if(abc.isNotEmpty()) {
            var photoByte: ByteArray? = abc
            val bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte!!.size)
            val cacheDirectory: File = context.getCacheDir()
            val tmp = File(cacheDirectory.getPath().toString() + "/_androhub" +uid  + ".png"
            )
            try {
                val fileOutputStream = FileOutputStream(tmp)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }
            photoPath = tmp.getPath()
            return photoPath
        }
        return "" + R.drawable.ic_btn_speak_now
    }

//    fun fetchDataFromProviderOrDB(){
//        val db = ContactDatabase.getInstance(context.applicationContext)
//        val userDao = db.contactDao()
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                if(userDao.countOFContact() != null && userDao.countOFContact() > 0) {
//                    var dbData = userDao.fetchAllContact()   //give list of contactEntity
//                    var list4 : MutableList<Contact> = mutableListOf()
//                    for (it in dbData) {
//                        list4.add(getDataFromDB(it))  //Passing entity
//                    }
//                    contactList.postValue(list4!!)
//                }
//                else{
//                    fetchDataFromContentProvider()
//                }
//            }catch (e: Exception){
//                println(e.localizedMessage)
//            }
//        }
//    }

    @SuppressLint("Range")
    fun fetchDataFromContentProvider(){
        var list : MutableList<Contact> = mutableListOf()
        var resolver :ContentResolver = context.contentResolver
        var uri : Uri = ContactsContract.Contacts.CONTENT_URI
        val SORT_ORDER: String = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        var cursor :Cursor? = resolver.query(uri, null, null, null, SORT_ORDER)

        cursor?.let {
            if (it.moveToFirst()){
                do{
                    val contctId: Long = it.getLong(it.getColumnIndex("_ID")) // Get contact ID
                    val dataUri = ContactsContract.Data.CONTENT_URI // URI to get
                    val dataCursor = context.contentResolver.query(dataUri, null, ContactsContract.Data.CONTACT_ID + " = " + contctId, null, null) // Retrun data cusror represntative to contact ID
                    var name=""
                    var phone=mutableMapOf<String,String>()
                    var email=mutableMapOf<String,String>()
                    var address= mutableMapOf<String,String>()
                    var photoPath  = "" + R.drawable.ic_btn_speak_now
                    var photoByte: ByteArray? = null

                    dataCursor?.let{contact ->
                      if(contact.moveToFirst()){
                          name  = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).toString()
                          Log.e("Mainactivity",name.toString())
                      }
                      do{
                          phone.putAll(getNumber(contact))
                          email.putAll(getEmail(contact))
                          address.putAll(getAddress(contact))
                          if(photoPath.equals("" + R.drawable.ic_btn_speak_now)){
                              var data = getImage(contact, contctId)
                              photoPath = data.first
                              photoByte = data.second
                          }
                      }while (contact.moveToNext())
                     }
                    list.apply {
                        add(Contact(contctId,name,phone, email,address,photoPath))
                    }
                    storeinDB(Contact(contctId,name,phone, email,address,photoPath),photoByte)
                }while (it.moveToNext())
            }
            contactList.postValue(list)
            it.close()
        }
    }

    fun storeinDB(single: Contact, photoByte : ByteArray?){

        single.number?.forEach { (key,value) ->
            contactDao.insertPhoneNumber(PhoneNumberEntity(0,single.id!!,key,value))
        }
        single.email?.forEach { (key,value) ->
            contactDao.insertEmail(EmailEntity(0,single.id!!,key,value))
        }
        single.address?.forEach { (key,value) ->
            contactDao.insertAddress(AddressEntity(0,single.id!!,key,value))
        }
        contactDao.insertContact(ContactEntity(0,single.id!!,single.name,photoByte))
    }

    @SuppressLint("Range")
    fun getEmail(dataCursor : Cursor) : MutableMap<String,String>{
        var email=mutableMapOf<String,String>()
        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)){

            when (dataCursor.getInt(dataCursor.getColumnIndex("data2"))){

                ContactsContract.CommonDataKinds.Email.TYPE_MOBILE ->  {
                    if(email["Mobile"] == null){
                        email["Mobile"] =  dataCursor.getString(dataCursor.getColumnIndex("data1"))
                        Log.e("Mainactivity","mobile"+ email["Mobile"])
                    }
                }
                ContactsContract.CommonDataKinds.Email.TYPE_HOME ->  {
                    if(email["Home"] == null){
                        email["Home"] =  dataCursor.getString(dataCursor.getColumnIndex("data1"))
                        Log.e("Mainactivity","home"+email["Home"])
                    }
                }
            }

        }
        return email
    }

    @SuppressLint("Range")
    fun getNumber(dataCursor :Cursor) : MutableMap<String,String>{
        var phone = mutableMapOf<String,String>()
        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)){

            when (dataCursor.getInt(dataCursor.getColumnIndex("data2"))){

                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ->  {
                    if(phone["Mobile"] == null){
                        phone["Mobile"] =  dataCursor.getString(dataCursor.getColumnIndex("data1"))
                        Log.e("Mainactivity","mobile"+phone["Mobile"])
                    }
                }
                ContactsContract.CommonDataKinds.Phone.TYPE_HOME ->  {
                    if(phone["Home"] == null){
                        phone["Home"] =  dataCursor.getString(dataCursor.getColumnIndex("data1"))
                        Log.e("Mainactivity","home"+phone["Home"])
                    }
                }

            }

        }   // FETCH NUMBER
        return phone
    }

    @SuppressLint("Range")
    fun getAddress(dataCursor : Cursor) : MutableMap<String,String>{
        var address = mutableMapOf<String,String>()
        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)){

            when (dataCursor.getInt(dataCursor.getColumnIndex("data2"))){

                ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME ->  {
                    if(address["Home"] == null){
                        address["Home"] =  dataCursor.getString(dataCursor.getColumnIndex("data1"))
                        Log.e("Mainactivity","mobile"+address["Home"])
                    }
                }
            }
        } // FETCH IMAGE

        return address
    }

    @SuppressLint("Range")
    fun getImage(dataCursor: Cursor,  contctId: Long) : Pair<String,ByteArray?>{

        var photoByte: ByteArray? = null
        var photoPath = "" + R.drawable.ic_btn_speak_now
        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
            photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15")) // get photo in // byte
            if (photoByte != null) {
                val bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.size)
                val cacheDirectory: File = context.getCacheDir()
                val tmp = File(cacheDirectory.getPath().toString() + "/_androhub" + contctId + ".png"
                )
                try {
                    val fileOutputStream = FileOutputStream(tmp)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()
                } catch (e: Exception) {
                    // TODO: handle exception
                    e.printStackTrace()
                }
                photoPath = tmp.getPath()
                return Pair(photoPath,photoByte)
            }
        }
      return Pair("" + R.drawable.ic_btn_speak_now,null)
    }
}