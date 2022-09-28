package com.example.contentproviderdemo.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.contentproviderdemo.db.dao.ContactDao
import com.example.contentproviderdemo.db.entity.AddressEntity
import com.example.contentproviderdemo.db.entity.ContactEntity
import com.example.contentproviderdemo.db.entity.EmailEntity
import com.example.contentproviderdemo.db.entity.PhoneNumberEntity


@Database( entities = [ContactEntity::class, PhoneNumberEntity::class, EmailEntity::class, AddressEntity::class] , version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao() : ContactDao


    companion object {
        private var instance : ContactDatabase? = null

        @Synchronized
        fun getInstance(context  : Context) : ContactDatabase {
             if(instance == null){
                 instance = Room.databaseBuilder(context.applicationContext, ContactDatabase::class.java,
                     "note_database")
                     .build()
             }
            return instance!!
        }

    }
}