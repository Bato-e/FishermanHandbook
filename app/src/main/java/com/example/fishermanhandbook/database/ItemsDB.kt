package com.example.fishermanhandbook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fishermanhandbook.ListItem

//An object that specifies which class objects will be stored in the database and the database version.
@Database(entities = [ListItem::class],version = 1)
abstract class ItemsDB :RoomDatabase() {
    abstract fun getDao():ItemsDao
}