package com.example.contentproviderdemo.modal

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
import androidx.lifecycle.*
import com.example.contentproviderdemo.modal.db.dao.ContactDao
import com.example.contentproviderdemo.modal.db.database.ContactDatabase
import com.example.contentproviderdemo.modal.db.entity.AddressEntity
import com.example.contentproviderdemo.modal.db.entity.ContactEntity
import com.example.contentproviderdemo.modal.db.entity.EmailEntity
import com.example.contentproviderdemo.modal.db.entity.PhoneNumberEntity
import com.example.contentproviderdemo.util.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.CoroutineContext

class ContactRepository(private var contactDao: ContactDao ) : CoroutineScope  {
    private lateinit var mycontext : Context

    //here we define logic whether to use db or call api

    fun countOFContact() : Int {
        return contactDao.countOFContact()
    }
    fun fetchAllContact() :  LiveData<List<ContactEntity>>{
         return contactDao.fetchAllContact()
    }
    fun editContact(contact: Contact) {

        launch {
            contactDao.updateUsername(contact.name!!,contact.id!!)

            for((key,value) in contact.number!!){
                if(!value.isNullOrEmpty()){
                    if(contactDao.checkPhoneumberCount(contact.id!!,key) > 0){
                        contactDao.updatPhoneNumber(contact.id!!,key,value)
                    }else{
                        contactDao.insertPhoneNumber(PhoneNumberEntity(0, contact.id!!,key,value))
                    }
                }else{
                        contactDao.deltePhoneNumberOfType(contact.id!!,key)
                }
            }

            for((key,value) in contact.email!!){
                if(!value.isNullOrEmpty()){
                    if(contactDao.checkEmailCount(contact.id!!,key) > 0){
                        contactDao.updatEmail(contact.id!!,key,value)
                    }else{
                        contactDao.insertEmail(EmailEntity(0, contact.id!!,key,value))
                    }
                }else{
                    contactDao.delteEmailOfType(contact.id!!,key)
                }
            }

            for((key,value) in contact.address!!){
                if(!value.isNullOrEmpty()){
                    if(contactDao.checkAddressCount(contact.id!!,key) > 0){
                        contactDao.updatAddress(contact.id!!,key,value)
                    }else{
                        contactDao.insertAddress(AddressEntity(0, contact.id!!,key,value))
                    }
                }else{
                    contactDao.delteAddressOfType(contact.id!!,key)
                }
            }

        }
    }

    fun fetchNumberWithUID(uid : Long): MutableList<PhoneNumberEntity>{
        return contactDao.fetchNumberWithUID(uid)
    }

    fun deleteUser(uid : Long){
        launch {
            contactDao.deleteUser(uid)
            contactDao.deletePhonenumber(uid)
            contactDao.deleteAddress(uid)
            contactDao.deleteEmail(uid)
        }
    }

    fun addSingleContact(entity : ContactEntity) : Contact {
        val db = ContactDatabase.getInstance(mycontext.applicationContext)
        val userDao = db.contactDao()
        var name: String?
        var number: MutableMap<String, String> = mutableMapOf()
        var email: MutableMap<String, String> = mutableMapOf()
        var address: MutableMap<String, String> = mutableMapOf()
        name = entity.name
        var uid =  entity.id

        launch{
            var numberList = fetchNumberWithUID(uid)
            for (i in numberList){
                number.set(i.phonetype,i.phoneNumber)
            }
            var emailList = userDao.fetchEmailWithUID(uid)
            for (i in emailList){
                email.set(i.emailType,i.email)
            }
            var addressList = userDao.fetchAddressWithUID(uid)
            for (i in addressList){
                address.set(i.addressType,i.address)
            }
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
            val cacheDirectory: File = mycontext.getCacheDir()
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

    fun fetchDataFromDB(context : Context,list :List<ContactEntity>) : MutableList<Contact> {
        mycontext = context
        var listOfAllContacts : MutableList<Contact> = mutableListOf()
            try {
                   //give list of contactEntity
                for (it in list) {
                    listOfAllContacts.add(addSingleContact(it))  //Passing entity
                }
            }catch (e: Exception){
                println(e.localizedMessage)
            }
        return listOfAllContacts
    }

    @SuppressLint("Range")
    fun fetchDataFromContentProvider( context: Context) :  MutableList<Contact> {
        mycontext = context
        var contactList =  mutableListOf<Contact>()
        var list : MutableList<Contact> = mutableListOf()
        var resolver : ContentResolver = context.contentResolver
        var uri : Uri = ContactsContract.Contacts.CONTENT_URI
        val SORT_ORDER: String = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        var cursor : Cursor? = resolver.query(uri, null, null, null, SORT_ORDER)

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
            contactList=list
            it.close()
        }

        return contactList
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
    fun getNumber(dataCursor : Cursor) : MutableMap<String,String>{
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
    fun getImage(dataCursor: Cursor, contctId: Long) : Pair<String,ByteArray?>{

        var photoByte: ByteArray? = null
        var photoPath = "" + R.drawable.ic_btn_speak_now
        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
            photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15")) // get photo in // byte
            if (photoByte != null) {
                val bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.size)
                val cacheDirectory: File = mycontext.getCacheDir()
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

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}