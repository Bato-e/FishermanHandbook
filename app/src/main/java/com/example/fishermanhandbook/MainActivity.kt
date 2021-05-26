package com.example.fishermanhandbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(),AdapterUpdater {
    var adapter: MyAdapter? = null
    lateinit var navView: BottomNavigationView
    private var currentType:Int = ListItem.FISH_ITEM

    companion object {
        const val KEY_TO_ITEM_SELECTED ="key_to_item_selected"
    }



    //Method for saving data before turning the screen
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TO_ITEM_SELECTED,currentType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //During the launch, we wait until the initial elements are filled in
        //then we install all the other elements
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO){
                //initializing the database
            DatabaseManager.fillTheDb(this@MainActivity)
                //Waiting for the signal to end recording
            DatabaseManager.completeInitFlow.collect {
                if (it) {
                    initTheViews(savedInstanceState)
                }
            }
            }
        }
    }

    private suspend fun initTheViews(savedInstanceState: Bundle?) {
        withContext(Dispatchers.Main) {
            //We get data about the button pressed on the bottom panel after turning the screen
            if (savedInstanceState != null && !savedInstanceState.isEmpty) {
                currentType = savedInstanceState.getInt(KEY_TO_ITEM_SELECTED)
            }

            //creating a BottomNavigation
            navView = setupBottomNavigation()

            //add button
            initTheAddButton()

            //creating a recyclerView
            setupRecView()
        }
    }

    private fun initTheAddButton() {
        val button = findViewById<FloatingActionButton>(R.id.add_button)
        button.setOnClickListener {
            val dialog = AddSubjectDialog.newInstance(currentType)
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, null)
        }
    }

    private fun isPermissionsGranted(permission: String) = (ContextCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED)

    private fun setupBottomNavigation(): BottomNavigationView {
        val navView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        setupBottomNavigationListener(navView)
        return navView
    }

    private fun setupRecView() {
        val rcView = findViewById<RecyclerView>(R.id.rcView)
        rcView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(contextR = this, listArrayR = ArrayList(), scope = lifecycleScope)
        rcView.adapter = adapter
    }

    //creating a listener for BottomNavigation
    private fun setupBottomNavigationListener(navView: BottomNavigationView) {
        navView.setOnNavigationItemSelectedListener { menuitem ->

            when (menuitem.itemId) {

                R.id.id_fish -> {
                    currentType=ListItem.FISH_ITEM
                    lifecycleScope.launch(Dispatchers.Main) {
                        //Getting data from the database for the adapter, passing their type
                        val list=DatabaseManager.getItems(ListItem.FISH_ITEM, context = baseContext)
                        adapter?.updateAdapter(list)
                    }
                    true
                }
                R.id.id_bite -> {
                    currentType=ListItem.BITES_ITEM
                    lifecycleScope.launch(Dispatchers.Main) {
                        //Getting data from the database for the adapter, passing their type
                        val list=DatabaseManager.getItems(ListItem.BITES_ITEM, context = baseContext)
                        adapter?.updateAdapter(list)
                    }
                    true
                }
                else -> false
            }
        }
        navView.selectedItemId = navView.menu[if (currentType==ListItem.FISH_ITEM) 0 else 1].itemId
    }

    //The function that is called after the successful completion of the dialog
    override fun updateAdapter(listItem: ListItem) {
        navView.selectedItemId = navView.menu[currentType].itemId
        adapter?.addItemToAdapter(listItem)
    }

}