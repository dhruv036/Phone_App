package com.example.contentproviderdemo.modal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contact")
data class ContactEntity (
    @PrimaryKey(autoGenerate = true)
    val cid : Int,
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "Name")
    val name: String?,
    @ColumnInfo(name = "image")
    val image: ByteArray?
    )