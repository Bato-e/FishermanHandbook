package com.example.fishermanhandbook

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ContentActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_layout)

        val tvTitle = findViewById<TextView>(R.id.tvTitleC)
        tvTitle.text = intent.getStringExtra("title")

        val tvContent = findViewById<TextView>(R.id.tvContentC)
        tvContent.text = intent.getStringExtra("content")

        val im = findViewById<ImageView>(R.id.imC)

        Log.d("tut",intent.extras?.get("type").toString())

        //Checking whether it is the initial item or a new one (with the std icon)



        if (intent.getStringExtra("type") == ListItem.ContentType.StandardIconItem.toString()){
            //The case of the preset image
             im.setImageResource(resources.getIdentifier(intent.getStringExtra("image") , "drawable", packageName))
        }
             //Case of a picture from the gallery
        else if (intent.getStringExtra("type") == ListItem.ContentType.NewIconItem.toString()){
            im.setImageURI(Uri.parse(intent.getStringExtra("image")))
        }
    }

}