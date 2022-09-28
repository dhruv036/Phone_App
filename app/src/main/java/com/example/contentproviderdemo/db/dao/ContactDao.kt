package com.example.contentproviderdemo.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.contentproviderdemo.db.entity.AddressEntity
import com.example.contentproviderdemo.db.entity.ContactEntity
import com.example.contentproviderdemo.db.entity.EmailEntity
import com.example.contentproviderdemo.db.entity.PhoneNumberEntity

@Dao
interface ContactDao {

    @Query("select * from contact")
    fun fetchAll() : MutableList<ContactEntity>

    @Insert()
    fun insertContact(contact : ContactEntity)

    @Query("select * from PhoneNumber where id = :uid")
    fun fetchNumberWithUID(uid : Long) : MutableList<PhoneNumberEntity>

    @Insert
    fun insertPhoneNumber(number : PhoneNumberEntity)

    @Query("select * from Email where id = :uid")
    fun fetchEmailWithUID(uid : Long) : MutableList<EmailEntity>

    @Insert
    fun insertEmail(number : EmailEntity)

    @Query("select * from Address where id = :uid")
    fun fetchAddressWithUID(uid : Long) : MutableList<AddressEntity>

    @Insert
    fun insertAddress(number : AddressEntity)
}