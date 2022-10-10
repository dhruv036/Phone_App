package com.example.contentproviderdemo.modal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Address")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val adressID : Int,
    @ColumnInfo(name = "id")  val id: Long,
    @ColumnInfo(name ="type") val addressType : String,
    @ColumnInfo(name = "address") val address : String
)
