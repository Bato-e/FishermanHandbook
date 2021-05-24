package com.example.fishermanhandbook

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream


//скоуп нужен для асинхронных запросов в базу данных
class MyAdapter(var listArrayR:ArrayList<ListItem> = ArrayList(), var contextR: Context,var scope:LifecycleCoroutineScope)
    : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    companion object{
        const val CHARS_TO_SHOW_NUM=20
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(contextR)
        return ViewHolder(inflater.inflate(R.layout.item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = listArrayR[position]
        holder.bind(listItem,contextR)
    }

    override fun getItemCount(): Int {
        return listArrayR.size
    }

    fun updateAdapter(listArray: List<ListItem>)
    {
        listArrayR.clear()
        listArrayR.addAll(listArray)
        notifyDataSetChanged()
    }

    suspend fun deleteItemFromAdapter(listItem: ListItem){
        //удаление итема из адапера
        val pos = listArrayR.indexOf(listItem)
        listArrayR.remove(listItem)
        notifyItemRemoved(pos)

        try {
            //обращение в базу данных для удаления данных
            withContext(Dispatchers.IO){ DatabaseManager.deleteItem(listItem,contextR)}
        }catch (e:Exception){
            Toast.makeText(contextR,"Error while deleting from db",Toast.LENGTH_SHORT).show()
        }
    }

    fun addItemToAdapter(listItem: ListItem){


        //добавление итема в адаптер
        listArrayR.add(listItem)
        notifyItemChanged(listArrayR.size-1)

        //Добавление итема в бд
        scope.launch(Dispatchers.IO) {
            DatabaseManager.insertItem(listItem,contextR)
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val im = view.findViewById<ImageView>(R.id.im)
    fun bind(listItem:ListItem, context: Context)
        {
            specifyTheTexts(listItem, context)

            setTheImage(listItem, context)

            setOnClickListeners(context, listItem)
        }

        private fun setOnClickListeners(
            context: Context,
            listItem: ListItem
        ) {
            itemView.setOnClickListener() {
                Toast.makeText(context, "pressed: ${tvTitle.text}", Toast.LENGTH_SHORT).show()
                val i = Intent(context, ContentActivity::class.java).apply {
                    putExtra("title", listItem.titleText)
                    putExtra("content", listItem.contentText)
                    putExtra("image", listItem.imageName)
                    putExtra("type",listItem.contentType.toString())
                }
                context.startActivity(i)
            }
            itemView.setOnLongClickListener {
                scope.launch(Dispatchers.Main) {
                    //удаление итема из массива
                    deleteItemFromAdapter(listItem)
                }
                true
            }
        }

        private fun setTheImage(
            listItem: ListItem,
            context: Context
        ) {
            //Установка иконок в зависимости от типа итема
            if (listItem.contentType == ListItem.ContentType.StandardIconItem)
                im.setImageResource(
                    context.resources.getIdentifier(
                        listItem.imageName,
                        "drawable",
                        context.packageName
                    )
                )
            else if (listItem.contentType == ListItem.ContentType.NewIconItem) {
                val uri = Uri.parse(listItem.imageName)
                val imageStream: InputStream? = context.contentResolver.openInputStream(uri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                im.setImageBitmap(selectedImage)
            }
        }

        private fun specifyTheTexts(
            listItem: ListItem,
            context: Context
        ) {
            tvTitle.text = listItem.titleText
            if (listItem.contentText.length < CHARS_TO_SHOW_NUM)
                tvContent.text = listItem.contentText
            else {
                val subStr = "${
                    listItem.contentText.subSequence(
                        0,
                        CHARS_TO_SHOW_NUM
                    )
                }${context.resources.getString(R.string.dots)}"
                tvContent.text = subStr
            }
        }
    }
}