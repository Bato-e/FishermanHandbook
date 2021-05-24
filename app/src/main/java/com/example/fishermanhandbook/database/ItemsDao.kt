package com.example.fishermanhandbook.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fishermanhandbook.ListItem

//Список всех доступынх для базы данных команд с указанием запроса SQL,если он нужен
//Часть тех.реализации бд
@Dao
interface ItemsDao {

    @Query("SELECT * FROM Items WHERE itemType =:type")
    suspend fun getItems(type:Int):List<ListItem>

    @Insert
    suspend fun insertItem(item:ListItem)

    @Delete
    suspend fun deleteItem(item:ListItem)
}