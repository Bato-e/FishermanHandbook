package com.example.fishermanhandbook

import androidx.room.Entity
import androidx.room.PrimaryKey

//Обозначением Entity мы говорим,что поля этого класса будут в бд,а также указываем название таблицы
@Entity(tableName = "Items")
data class ListItem (
        //Ключ для базы данных,он уникальный и заполняется сам
        @PrimaryKey(autoGenerate = true)
        var id:Int=0,
        var titleText: String,
        var contentText: String,
        //В какое поле (fish/bites) заносить итем
        var itemType:Int,
        //Материал для загрузки картинки
        //По умолчанию тут указываются картинки для каждого из итемов.
        //Указывать нужно названия новых картинок БЕЗ .png
        //TODO здесь можно менять картинки итемов в списке по умолчанию
        var imageName:String = if (itemType == FISH_ITEM) "ic_fish" else "ic_bite",
        //Тип итема - презагруженный или новый
        //Разница заключается в методах работы с imageName
        var contentType:ContentType){

        companion object {
                const val FISH_ITEM=0
                const val BITES_ITEM=1
        }


        //Тип иконки - стандартная и новопоставленная (предустановленные картинки - первый пункт)
        enum class ContentType{
                StandardIconItem,
                NewIconItem
        }
}