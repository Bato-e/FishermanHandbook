package com.example.fishermanhandbook

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fishermanhandbook.database.ItemsDB
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

//A class for managing the database, where all queries to the database are located
object DatabaseManager {

    private var db:ItemsDB?=null
    private val completeFlow = MutableStateFlow(false)
    val completeInitFlow = completeFlow.asStateFlow()
    private fun getInstance(context: Context):ItemsDB{
        //Creating an object to access the database, and also specifying the name of the database
        if (db==null) db = Room.databaseBuilder(context,ItemsDB::class.java,"Database")
            //Adding a callback that will be called when creating the database
            .addCallback( object :
                RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    GlobalScope.launch(Dispatchers.IO) {
                        readInitItemsFromFile(context)
                                //transmitting a signal about the end of recording
                            .onCompletion {
                                completeFlow.value = true
                            }
                                //Converting strings to elements of items
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
                                //Entering items in the database
                        }.collect { item->
                            getInstance(context).getDao().insertItem(item)
                        }
                    }
                }
            })
            .build()
        return db as ItemsDB
    }


    //Method for initializing the insertion of initial db items
    //
    //the database is being initialized
    suspend fun fillTheDb(context: Context){
        if (getInstance(context).getDao().getItems(ListItem.FISH_ITEM).isNotEmpty())
            completeFlow.value=true
    }


    //Reading lines from the file items.txt
    private fun readInitItemsFromFile(context: Context): Flow<String> {
        val words = mutableListOf<String>()
        val inputStream = context.assets.open("items.txt")
        inputStream.bufferedReader().forEachLine {
            words.add(it)
        }

        //Returning the data stream
        return words.asFlow()
    }


    //Read all items
    suspend fun getItems(type:Int,context: Context):List<ListItem> = withContext(Dispatchers.IO){
        getInstance(context).getDao().getItems(type)
    }

    //enter a new item in the database
    suspend fun insertItem(item: ListItem,context: Context){
        getInstance(context).getDao().insertItem(item)
    }

    //delete item
    suspend fun deleteItem(item: ListItem,context: Context){
        getInstance(context).getDao().deleteItem(item)
    }
}