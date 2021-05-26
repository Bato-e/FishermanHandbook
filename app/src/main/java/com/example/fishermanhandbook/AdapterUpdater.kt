package com.example.fishermanhandbook

//Interface that allows you to pass data from a dialog to MainActivity
// It is implemented by the activity,and an object of this interface is created in the dialog and the method is called
interface AdapterUpdater {
    fun updateAdapter(listItem: ListItem)
}