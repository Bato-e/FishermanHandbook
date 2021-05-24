package com.example.fishermanhandbook

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fishermanhandbook.database.ItemsDB
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

//Класс для управления базой данных,здесь расположены все запросы в базу данных
object DatabaseManager {

    private var db:ItemsDB?=null
    private val completeFlow = MutableStateFlow(false)
    val completeInitFlow = completeFlow.asStateFlow()
    private fun getInstance(context: Context):ItemsDB{
        //Создаем объект для доступа к бд,тех.реализация,указываем также название базы данных
        if (db==null) db = Room.databaseBuilder(context,ItemsDB::class.java,"Database")
            //Добавляем колбек,который вызовется при создании бд
            .addCallback( object :
                RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    GlobalScope.launch(Dispatchers.IO) {
                        readInitItemsFromFile(context)
                                //передаем сигнал об окончании записи
                            .onCompletion {
                                completeFlow.value = true
                            }
                                //Преобразуем строки в элементы итемов
                            .map { str ->
                            val (titleText: String,
                                contentText: String,
                                type: String,
                                image:String) = str.split(";")
                            ListItem(
                                titleText = titleText,
                                contentText = contentText,
                                itemType = type.toInt(),
                                imageName = image,
                                contentType = ListItem.ContentType.StandardIconItem
                            )
                                //Вписываем итемы в бд
                        }.collect { item->
                            getInstance(context).getDao().insertItem(item)
                        }
                    }
                }
            })
            .build()
        return db as ItemsDB
    }


    //Метод для инициализации вставки начальных итемов бд
    //Сам метод тут не важен (ывзывается get,чтобы лишнего не вписать/удалить)
    //Важно только что инициализируется бд
    suspend fun fillTheDb(context: Context){
        if (getInstance(context).getDao().getItems(ListItem.FISH_ITEM).isNotEmpty())
            completeFlow.value=true
    }


    //Считываем строки с файла items.txt
    private fun readInitItemsFromFile(context: Context): Flow<String> {
        val words = mutableListOf<String>()
        val inputStream = context.assets.open("items.txt")
        inputStream.bufferedReader().forEachLine {
            words.add(it)
        }

        //Возвращаем поток данных
        return words.asFlow()
    }


    //считать все Итемы
    suspend fun getItems(type:Int,context: Context):List<ListItem> = withContext(Dispatchers.IO){
        getInstance(context).getDao().getItems(type)
    }

    //ввести новый Итем в бд
    suspend fun insertItem(item: ListItem,context: Context){
        getInstance(context).getDao().insertItem(item)
    }

    //Удалить итем
    suspend fun deleteItem(item: ListItem,context: Context){
        getInstance(context).getDao().deleteItem(item)
    }
}