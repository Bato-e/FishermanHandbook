package com.example.fishermanhandbook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fishermanhandbook.ListItem

//Объект,который указывает,объекты каких классов будут храниться в бд и версию базы данных
//Технич.реализация,не вдавайтесь
@Database(entities = [ListItem::class],version = 1)
abstract class ItemsDB :RoomDatabase() {
    abstract fun getDao():ItemsDao
}