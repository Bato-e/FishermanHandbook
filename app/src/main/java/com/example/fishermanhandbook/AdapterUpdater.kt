package com.example.fishermanhandbook

//Интерфейс,который позволяет передавать данные из диалога в MainActivity
// Его реализует активность,а в диалоге создается объект этого интерфейса и вызывается метод
interface AdapterUpdater {
    fun updateAdapter(listItem: ListItem)
}