package com.example.contentproviderdemo.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PhoneNumber")
data class PhoneNumberEntity(
    @PrimaryKey(autoGenerate = true) val phoneId : Int,
    @ColumnInfo(name = "id")  val id: Long,
    @ColumnInfo(name ="type") val phonetype : String,
    @ColumnInfo(name = "phone") val phoneNumber : String
)
