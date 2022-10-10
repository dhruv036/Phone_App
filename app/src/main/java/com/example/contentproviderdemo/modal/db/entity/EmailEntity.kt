package com.example.contentproviderdemo.modal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Email")
data class EmailEntity (
    @PrimaryKey(autoGenerate = true) val emailId : Int,
    @ColumnInfo(name = "id")  val id: Long,
    @ColumnInfo(name ="type") val emailType : String,
    @ColumnInfo(name = "email") val email : String
    )