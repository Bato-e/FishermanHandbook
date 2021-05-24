package com.example.fishermanhandbook

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment


//Диалог для описания нового объекта
class AddSubjectDialog : DialogFragment() {

    private var type:Int=0
    private var uri:Uri?=null
    private lateinit var adapterUpdater: AdapterUpdater
    private lateinit var img:ImageView
    private lateinit var imgButton:Button

    companion object{
        const val KEY_TO_TYPE = "key_to_type"
        const val PICK_IMAGE_REQUEST_CODE = 123
        const val REQUEST_PERMS_REQUEST_CODE = 456


        //Метод для вызова диалога с передачей нужного типа данных (чтобы объект пошел в нужный список)
        fun newInstance(type:Int):AddSubjectDialog{
            val bundle = Bundle()
            bundle.putInt(KEY_TO_TYPE,type)
            val dialog=AddSubjectDialog()
            dialog.arguments=bundle
            return dialog
        }
    }

    //Присоединение интерфейса для передачи данных в конце
    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapterUpdater = context as AdapterUpdater
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        getElemsFromArgs()


        val v = LayoutInflater.from(context).inflate(R.layout.dialog_add_item,null)
        val titleEditText = v.findViewById<EditText>(R.id.title_edit_text)
        val contentEditText = v.findViewById<EditText>(R.id.content_edit_text)
        imgButton = v.findViewById(R.id.pick_image_btn)
        img = v.findViewById(R.id.image)




        // Отправляем заявку на выбор картинки из галереи с учетом дальнейшего использования
        imgButton.setOnClickListener {
            requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val builder = AlertDialog.Builder(context)
        return builder
            .setView(v)
            .setTitle(R.string.add_title)
                //Слушатель на кнопку ОК
            .setPositiveButton(getString(R.string.ok)
            ) { _, _ ->
                if (titleEditText.text == null || titleEditText.text.isEmpty()) {
                    Toast.makeText(context, getString(R.string.dia_title_error), Toast.LENGTH_SHORT).show()
                    dismiss()
                    return@setPositiveButton
                } else if (contentEditText.text == null || contentEditText.text.isEmpty()) {
                    Toast.makeText(context, getString(R.string.dia_content_error), Toast.LENGTH_SHORT).show()
                    dismiss()
                    return@setPositiveButton
                }

                    val item = if (uri!=null) ListItem(
                        titleText = titleEditText.text.toString(),
                        contentText = contentEditText.text.toString(),
                        itemType = type,
                        imageName = uri.toString(),
                        contentType = ListItem.ContentType.NewIconItem
                    ) else ListItem(
                        titleText = titleEditText.text.toString(),
                        contentText = contentEditText.text.toString(),
                        itemType = type,
                        contentType = ListItem.ContentType.StandardIconItem
                    )

                adapterUpdater.updateAdapter(item)
                dismiss()

            }
                //Слушатель на кнопку Cancel
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }
            .create()
    }


    //Метод,который вызывается после обработки заявки (на выход: номер запроса,код результата,результат )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when (requestCode){
                PICK_IMAGE_REQUEST_CODE -> {
                    //Разрешаем uri (коду) картинки быть задействованным долго
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    data?.data?.let {  context?.contentResolver?.takePersistableUriPermission(it, takeFlags) }
                    uri = data?.data
                    img.setImageURI(uri)
                }
            }
        }
    }

    //Метод для инициализации типа согласну переданным данным
    private fun getElemsFromArgs() {
        type = arguments?.getInt(KEY_TO_TYPE)!!
    }

    //Запрашиваем разрешение на использование медиафайлов
    private fun requestPermissions(permission:String) {
        if (context?.let { ContextCompat.checkSelfPermission(it, permission) }
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission), REQUEST_PERMS_REQUEST_CODE)
            }
        else sendIntentForPhoto()
    }


    //Смотрим на результат запроса на разрешение
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("tut","request")
        if (requestCode == REQUEST_PERMS_REQUEST_CODE){
            Log.d("tut","insideIf")
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendIntentForPhoto()
            }
            else Toast.makeText(context,getString(R.string.req_give_access_to_memory),Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendIntentForPhoto() {
        Log.d("tut","intent_sent")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.picture_chooser_title)
            ), PICK_IMAGE_REQUEST_CODE
        )
    }
}