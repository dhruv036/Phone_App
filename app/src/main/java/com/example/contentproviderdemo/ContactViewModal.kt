package com.example.contentproviderdemo

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
import androidx.lifecycle.viewModelScope
import com.example.contentproviderdemo.db.database.ContactDatabase
import com.example.contentproviderdemo.db.entity.AddressEntity
import com.example.contentproviderdemo.db.entity.ContactEntity
import com.example.contentproviderdemo.db.entity.EmailEntity
import com.example.contentproviderdemo.db.entity.PhoneNumberEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class ContactViewModal(val context : Context) : ViewModel() {
    private var contactList = MutableLiveData<MutableList<Contact>>()
    val db = ContactDatabase.getInstance(context.applicationContext)
    val contactDao = db.contactDao()

    fun getContacts() : MutableLiveData<MutableList<Contact>> {
        return contactList
    }

    fun launch(){
        val db = ContactDatabase.getInstance(context.applicationContext)
        val userDao = db.contactDao()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(userDao.fetchAll() != null && userDao.fetchAll().size > 0) {
                    var dbData = userDao.fetchAll()
                    var list4 : MutableList<Contact> = mutableListOf()

                    var uid:Long?
                    for (it in dbData) {
                        var name: String? = null
                        var number: MutableMap<String, String>? = mutableMapOf()
                        var email: MutableMap<String, String>? = mutableMapOf()
                        var address: MutableMap<String, String>? = mutableMapOf()
                        name = it.name
                        uid =  it.id
                        var list = userDao.fetchNumberWithUID(it.id)
                        for (i in list){
                            number!![i.phonetype] = i.phoneNumber
                        }
                        var list2 = userDao.fetchEmailWithUID(it.id)

                        for (i in list2){
                            email!![i.emailType] = i.email
                        }

                        var list3 = userDao.fetchAddressWithUID(it.id)

                        for (i in list3){
                            address!![i.addressType] = i.address
                        }

                        var photoPath = "" + R.drawable.ic_btn_speak_now
                        it.image?.let{ abc->
                            if(abc.size > 0) {
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
                            }
                        }

                        list4.apply {
                            add(Contact(uid,name,number, email,address,photoPath))

                        }
                    }
                    contactList.postValue(list4!!)
                }
                else{
                    fetchData()
                }
            }catch (e: Exception){
                println(e.localizedMessage)
            }

        }
    }



    @SuppressLint("Range")
    fun fetchData(){
        var list : MutableList<Contact> = mutableListOf()
        var resolver :ContentResolver = context.contentResolver
        var uri : Uri = ContactsContract.Contacts.CONTENT_URI
        val SORT_ORDER: String = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        var cursor :Cursor? = resolver.query(uri, null, null, null, SORT_ORDER)

        if (cursor!!.moveToFirst()){
            do{
                val contctId: Long = cursor.getLong(cursor.getColumnIndex("_ID")) // Get contact ID
                val dataUri = ContactsContract.Data.CONTENT_URI // URI to get
                val dataCursor = context.contentResolver.query(dataUri, null, ContactsContract.Data.CONTACT_ID + " = " + contctId, null, null) // Retrun data cusror represntative to contact ID
                var name=""
                var phone=mutableMapOf<String,String>()
                var email=mutableMapOf<String,String>()
                var address= mutableMapOf<String,String>()
                var photoPath = "" + R.drawable.ic_btn_speak_now
                var photoByte: ByteArray? = null

                if(dataCursor!!.moveToFirst()){

                    name  = cursor?.getString(cursor?.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)!!).toString()
                    Log.e("Mainactivity",name.toString())
                }

                do{

                    phone.putAll(getNumber(dataCursor))
                    email.putAll(getEmail(dataCursor))
                    address.putAll(getAddress(dataCursor))


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

                        }
                    }

                }while (dataCursor.moveToNext())
                list.apply {
                    add(Contact(contctId,name,phone, email,address,photoPath))
                }

                for((key,value) in phone){
                    contactDao.insertPhoneNumber(PhoneNumberEntity(0,contctId,key,value))
                }
                for((key,value) in email){
                    contactDao.insertEmail(EmailEntity(0,contctId,key,value))
                }
                for((key,value) in address){
                    contactDao.insertAddress(AddressEntity(0,contctId,key,value))
                }
                contactDao.insertContact(ContactEntity(0,contctId,name,photoByte))

            }while (cursor!!.moveToNext())
        }

        contactList.postValue(list!!)

        cursor.close()

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
    fun getImage(dataCursor: Cursor,  contctId: Long) : String{

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

            }
        }
        return photoPath
    }
}