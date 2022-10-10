package com.example.contentproviderdemo.modal.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contentproviderdemo.modal.db.entity.AddressEntity
import com.example.contentproviderdemo.modal.db.entity.ContactEntity
import com.example.contentproviderdemo.modal.db.entity.EmailEntity
import com.example.contentproviderdemo.modal.db.entity.PhoneNumberEntity

@Dao
interface ContactDao {

    @Query("select * from contact order by Name")
    fun fetchAllContact() : LiveData<List<ContactEntity>>

    @Query("select count(*) from contact")
    fun countOFContact() : Int

    @Query("update contact set Name=:name where id=:id")
    fun updateUsername(name :String , id :Long)

    @Query("select count(*) from PhoneNumber where  id = :uid and type = :type")
    fun checkPhoneumberCount(uid: Long,type :String) : Int

    @Query("select count(*) from Email where  id = :uid and type = :type")
    fun checkEmailCount(uid: Long,type :String) : Int

    @Query("select count(*) from Address where  id = :uid and type = :type")
    fun checkAddressCount(uid: Long,type :String) : Int

    @Query("update phonenumber set phone =:number where id = :uid and type = :type")
    fun updatPhoneNumber(uid: Long,type :String,number :String) : Int

    @Query("update Email set email =:email where id = :uid and type = :type")
    fun updatEmail(uid: Long,type :String, email :String) : Int

    @Query("update Address set address =:addrress where id = :uid and type = :type")
    fun updatAddress(uid: Long,type :String,addrress :String) : Int

    @Query("delete from PhoneNumber where id =:uid and type =:type")
    fun deltePhoneNumberOfType(uid: Long,type :String)

    @Query("delete from Email where id =:uid and type =:type")
    fun delteEmailOfType(uid: Long,type :String)

    @Query("delete from Address where id =:uid and type =:type")
    fun delteAddressOfType(uid: Long,type :String)

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

    @Query("delete from contact where id =:uid")
    fun deleteUser(uid : Long)

    @Query("delete from phonenumber where id =:uid")
    fun deletePhonenumber(uid : Long)

    @Query("delete from email where id =:uid")
    fun deleteEmail(uid : Long)

    @Query("delete from email where id =:uid")
    fun deleteAddress(uid : Long)



}