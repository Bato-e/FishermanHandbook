package com.example.fishermanhandbook

import androidx.room.Entity
import androidx.room.PrimaryKey

//By the Entity designation, we say that the fields of this class will be in the database,and also specify the name of the table
@Entity(tableName = "Items")
data class ListItem (
        //The key for the database,it is unique and is filled in by itself
        @PrimaryKey(autoGenerate = true)
        var id:Int=0,
        var titleText: String,
        var contentText: String,
        //In which field (fish/bites) to enter the item
        var itemType:Int,
        //Material for uploading images
        //by default, images are specified here for each of the items.
        //here you can change the images of items in the default list
        var imageName:String = if (itemType == FISH_ITEM) "ic_fish" else "ic_bite",
        //Item type-preloaded or new
        var contentType:ContentType){

        companion object {
                const val FISH_ITEM=0
                const val BITES_ITEM=1
        }


        //Icon type - standard and newly added (pre-installed images are the first item)
        enum class ContentType{
                StandardIconItem,
                NewIconItem
        }
}