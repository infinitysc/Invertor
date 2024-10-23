package com.build.Invertor.mainModule.Card

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.build.Invertor.R
import com.build.Invertor.model.NewUser
import com.build.Invertor.model.json.CardInventory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter




/**
 * Фрагмент в котором идет заполенение карточки устройства и привязка его к сотруднику.
 *
 * **/
class CardFragment : Fragment(){

    private val sound : MediaPlayer by lazy { MediaPlayer.create(requireContext(),R.raw.scanner_beep) }
    private var user : NewUser? = null
    private var card : CardInventory? = null
    private lateinit var serialInputLayoyt : TextInputLayout
    private lateinit var activityContext : Context
    private lateinit var adress : TextView
    private lateinit var dest : TextView
    private lateinit var sNEditor : TextInputEditText
    private lateinit var spinnerStatus : Spinner
    private lateinit var userWidget : TextView
    private lateinit var ps : TextInputEditText
    private lateinit var saveButton : Button
    private var saveJsonString : String = ""
    private var saveCard : CardInventory? = null
    private var itemSelected : String = ""
    private val listSpin = listOf<String>("","В эксплуатации","Требуется ремонт","Находится на консервации",
        "Не соответствует требованиям эксплуатации","Не введен в эксплуатацию","Списание","Утилизация")
    private lateinit var oldUser : TextView
    private lateinit var inventNumb : TextView
    private var oldSnNumber : String = ""

    private var nameFile = "" // this is fileName from bundle(ListChoiceFragment) cameraFragment set null into bundle

    private val activityFragmentManager : FragmentManager by lazy { activity?.supportFragmentManager!! }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.card_tech_layout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Зона где находим вьюшки
        adress = view.findViewById(R.id.adress)
        dest = view.findViewById(R.id.description)
        sNEditor = view.findViewById(R.id.serialNumberEdit)
        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        userWidget = view.findViewById(R.id.user)
        ps = view.findViewById(R.id.PSEditor)
        saveButton = view.findViewById(R.id.saveButton)
        oldUser = view.findViewById(R.id.oldUser)
        inventNumb = view.findViewById(R.id.invenNumb)
        serialInputLayoyt = view.findViewById(R.id.serialNimberEditLayout)

        //подключаем все что нужно
        val arrayAdapter = ArrayAdapter(activityContext,R.layout.spinner,listSpin)
        spinnerStatus.adapter = arrayAdapter
        val deffaultPos = listSpin.indexOf(card?.Status)
        spinnerStatus.setSelection(deffaultPos)
        sNEditor.setText(card?.SerialNumb)
        ps.setText(card?.Description)
        try {
            nameFile = this.requireArguments().getString("json").toString()
        }catch (e : java.lang.IllegalStateException){
            e.printStackTrace()
            Log.e("NameFile","bundle пустой")
        }
        serialInputLayoyt.setEndIconOnClickListener {
            alert().show()
        }

    }

    private fun alert() : AlertDialog{
        var value = ""
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.serial_camera_layout,null)
        val scopeScanner = dialogView.findViewById<BarcodeView>(R.id.alertScanner)
        val torch = dialogView.findViewById<SwitchCompat>(R.id.torch)

        val callback = BarcodeCallback {
            if(it != null){
                scopeScanner.setTorch(false)
                sound.start()
                value = it.text
                scopeScanner.pause()
                Toast.makeText(requireContext(),"Успешное сканирование",Toast.LENGTH_SHORT).show()
                Log.d("Scanner","сканирование выполнилось $this")
            }
        }
        scopeScanner.decodeContinuous(callback)
        scopeScanner.decoderFactory = DefaultDecoderFactory(listOf(
            BarcodeFormat.CODE_93,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39,
            BarcodeFormat.EAN_8,
            BarcodeFormat.EAN_13
        ))
        scopeScanner.resume()
        torch.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                scopeScanner.setTorch(true)
            }else {
                scopeScanner.setTorch(false)
            }
        }
        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("ок"){dialog, which ->
                if(value != ""){
                    sNEditor.setText(value)
                }
                dialog.dismiss()
            }
            .setNegativeButton("отмена") {dialog, which ->
                dialog.dismiss()
            }
            .create()

    }

    override fun onStart() {
        super.onStart()

        adress.text = card?.Adress
        dest.text = card?.UEDescription
        oldUser.text = "Старый пользователь : ${card?.UserNаме?.substringAfter("|")}"
        userWidget.text = "Новый пользователь : ${user?.user?.userName}"
        inventNumb.text = "Инвентарный номер : ${card?.inventNumb}"


        alert()
        spinnerStatus.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                itemSelected = listSpin[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //add something
            }
        }

        saveButton.setOnClickListener(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                var change = 0
                oldSnNumber = sNEditor.text.toString()
                if(oldSnNumber != ""){
                    change = 1
                }
                saveCard = createEditedCard(
                    user = this.user!!,
                    card = this.card,
                    newSerialNumberString = sNEditor.text.toString(),
                    "${getCurrentTime()}+03",
                    status = itemSelected,
                    change = change,
                    cabinet = this.user!!.cabinet.toString(), // заглушка
                    ps = ps.text.toString()
                )
                val gson = Gson()
                saveJsonString = gson.toJson(saveCard)
                //workWithCache()
                reSaveDataFile(saveCard!!)
                if(nameFile != ""){
                    getCacheFileToReWrite(nameFile,saveCard!!)
                }
                activityFragmentManager.popBackStack()
            }
            else{ // надо весь доступ заложить еще до момента работы иначе ну тупо болерплейт код получается в некоторых местах, а также предстоит переработка работы с данными в модель!!!
                if(ContextCompat.checkSelfPermission(activityContext,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
                }
                else {
                    var change = 0
                    oldSnNumber = sNEditor.text.toString()
                    if(oldSnNumber != ""){
                        change = 1
                    }
                    saveCard = createEditedCard(
                        user = this.user!!,
                        card = this.card,
                        newSerialNumberString = sNEditor.text.toString(),
                        "${getCurrentTime()}+03",
                        status = itemSelected,
                        change = change,
                        cabinet = this.user!!.cabinet.toString(), // заглушка
                        ps = ps.text.toString()
                    )
                    val gson = Gson()
                    saveJsonString = gson.toJson(saveCard)
                    //workWithCache() // можно булеан знач дать чтобы если ничего не вышло вернуло фалсе и карточка свободная мб
                    reSaveDataFile(saveCard!!)
                    if(nameFile != ""){
                        getCacheFileToReWrite(nameFile,saveCard!!)
                    }
                    activityFragmentManager.popBackStack()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sound.release()
    }

    private fun getCacheFileToReWrite(fileNameFromCache : String,card : CardInventory) {
        val gsonEngine = GsonBuilder()
            .serializeNulls()
            .create()
        val fileCacheDir = requireContext().cacheDir
        val file = File(fileCacheDir,fileNameFromCache)
        if(file.exists()){
            val type = object : TypeToken<List<CardInventory>>() {}.type
            val listData = gsonEngine.fromJson<List<CardInventory>>(file.readText(),type)

            for(i in listData.iterator()){
                if(i.UEID == card.UEID){
                    reWrite(i,card)
                }
            }

            try{
                FileOutputStream(file).use {
                    it.write(gsonEngine.toJson(listData).toByteArray())
                    it.flush()
                }
            }catch(error : IOException){
                error.printStackTrace()
                Log.d("CacheFile","не удалось перезаписать кеш файл $fileNameFromCache")
            }
        }
        else{
            Log.d("CacheFile","Файл не существует$this")
        }
    }

    private fun getCurrentTime(): String {
        val dateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return dateTime.format(formatter)
    }

    private fun reSaveDataFile(newCard : CardInventory,fileName: String = "jso.json") {
        val gsonEngine = GsonBuilder()
            .serializeNulls()
            .create()

        val oldFile = File(requireContext().filesDir, fileName)
        val type = object : TypeToken<List<CardInventory>>() {}.type
        val work = if (oldFile.exists()) {
            val jsonString = oldFile.readText()
            gsonEngine.fromJson<List<CardInventory>>(jsonString, type)
        } else {
            mutableListOf()
        }

        for (i in work.iterator()) {
            if (i.UEID == newCard.UEID) {
                reWrite(i, newCard)
            }
        }
        val updatedJsonString = gsonEngine.toJson(work)
        try {
        requireContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(updatedJsonString.toByteArray())
            it.flush()
        }
            Log.d("FileSave","Файл удалось перезаписать")
        Toast.makeText(requireContext(), "Файл успешно перезаписан", Toast.LENGTH_SHORT).show()
     }catch (E : IOException){
         E.printStackTrace()
         Log.d("FileSave","Файл не удалось перезаписать")
     }

    }
/**
 *  var SID : Int ,
var UEID : Int ,
var UEDescription : String?,
var ActionDateTime : String?,
var Adress : String?,
var Status : String?,
var inventNumb : String?,
var SerialNumb : String,
var IsSNEdited : Int ,
var UserNаме : String?,
var Description : String?,
var Cabinet : String?,
var Cod1C : String?,*/
    private fun reWrite(oldCard : CardInventory,newCard : CardInventory) : CardInventory {
        oldCard.SID = newCard.SID
        oldCard.UEDescription = newCard.UEDescription
        oldCard.ActionDateTime = newCard.ActionDateTime
        oldCard.Adress = newCard.Adress
        oldCard.Status = newCard.Status
        oldCard.inventNumb = newCard.inventNumb
        oldCard.SerialNumb = newCard.SerialNumb
        oldCard.IsSNEdited = newCard.IsSNEdited
        oldCard.UserNаме = newCard.UserNаме // тут баг блять ,мы хотим выбирать заменять ли сотрудника на нового а она просто инстанто меняет upd! не баг а фича
        oldCard.Description = newCard.Description
        oldCard.Cabinet = newCard.Cabinet
        oldCard.Cod1C = newCard.Cod1C
        return oldCard
    }

    //оставлю
    private fun saveJson(jsonStr : String , fileName : String = "${user?.user?.id}_${getCurrentTime().replace(":", "-")}.json"){
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val folder = File(activityContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "json")
            if (!folder.exists()) {
                val dirCreated = folder.mkdirs() // Создаем все необходимые директории
                if (dirCreated) {
                    println("Директория успешно создана: ${folder.absolutePath}")
                } else {
                    println("Не удалось создать директорию: ${folder.absolutePath}")
                }
            }
            val file = File(folder, fileName)

            try {
                FileOutputStream(file).use { output ->
                    output.write(jsonStr.toByteArray())
                    output.flush()
                }
                println("Файл успешно сохранён: ${file.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            println("Внешнее хранилище недоступно.")
        }

    }

    private fun createEditedCard(
        user : NewUser,
        card: CardInventory?,
        newSerialNumberString : String,
        date : String,
        status : String,
        change : Int,
        cabinet : String,
        ps : String
        ) : CardInventory {

        return CardInventory(
            card!!.SID,
            card.UEID,
            card.UEDescription,
            date,
            card.Adress ,
            status,
            card.inventNumb,
            newSerialNumberString,
            change,
            "${user.user?.id}|${user.user?.userName}",//заглушка
            ps,
            cabinet,
            card.Cod1C
        )
    }


    fun setUser(user : NewUser?) {
        this.user = user
    }
    fun setCard(card : CardInventory?){
        this.card = card
    }
    companion object {
        fun newInstance(
            user : NewUser?,
            card : CardInventory?,
            bundle : Bundle?
        ) : CardFragment {
            val newFragment = CardFragment()
            newFragment.arguments = bundle
            newFragment.setUser(user)
            newFragment.setCard(card)
            return newFragment
        }
    }
}