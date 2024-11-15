package com.build.Invertor.mainModule.Card

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.build.Invertor.R
import com.build.Invertor.model.NewUser
import com.build.Invertor.model.csv.DataDownloader
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
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BabyCardFragment : Fragment() {

    private lateinit var first: TextInputEditText
    private lateinit var second: TextInputEditText
    private lateinit var thrid: TextInputEditText
    private lateinit var spinner: Spinner
    private lateinit var tap: Button
    private lateinit var inputLayoyt: TextInputLayout


    private var papa: NewUser? = null
    private var papaCard: CardInventory? = null
    private var max : Int = 0
    private val sound: MediaPlayer by lazy {
        MediaPlayer.create(
            requireContext(),
            R.raw.scanner_beep
        )
    }
    private var spinPos = ""
    private val listSpin = listOf(
        "",
        "В эксплуатации",
        "Требуется ремонт",
        "Находится на консервации",
        "Не соответствует требованиям эксплуатации",
        "Не введен в эксплуатацию",
        "Списание",
        "Утилизация"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.baby_card_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        first = view.findViewById(R.id.opi)
        second = view.findViewById(R.id.ser)
        thrid = view.findViewById(R.id.desc)
        spinner = view.findViewById(R.id.spinn)
        tap = view.findViewById(R.id.tapButton)
        inputLayoyt = view.findViewById(R.id.testL)

        spinner.adapter = ArrayAdapter(requireContext(), R.layout.spinner, listSpin)

        spinner.setSelection(listSpin.indexOf("В эксплуатации"))

        inputLayoyt.setEndIconOnClickListener {
            alert().show()
        }

    }

    override fun onStart() {
        super.onStart()

        alert()

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinPos = listSpin[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        tap.setOnClickListener {
            //papa user
            //papaCard card
            max = getMax()
            val change = if(second.text.toString() != ""){
                1
            }else {
                0
            }
            val newBabyCard = CardInventory(
                index = max+1,
                SID =  papaCard?.SID!!,
                UEID = null ,
                UEDescription = if(first.text.toString() == ""){null}else {first.text.toString()},
                ActionDateTime = "${getCurrentTime()}+03",
                Adress = papa?.adress!!,
                Status = spinPos,
                inventNumb = papaCard?.inventNumb,
                SerialNumb = if(second.text.toString() == ""){null}else{second.text.toString()},
                IsSNEdited = change,
                UserName = "${papa?.user?.id}|${papa?.user?.userName}",
                Description = if(thrid.text.toString() == ""){null}else{thrid.text.toString()},
                Cabinet = papa?.cabinet,
                Cod1C = papaCard?.Cod1C,
                parentEqueipment = papaCard?.UEID ?: 0
            )
            addToEndJsonFile(newBabyCard)
            updateMax()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sound.release()
    }

    private fun debugSaveCache(card : CardInventory){
        var file = File(requireContext().cacheDir,"test.json")
        val jsStr = Gson().toJson(card)
        FileWriter(file).use {
            it.write(jsStr)
            it.flush()
        }

    }
    private fun addToEndJsonFile(newCard : CardInventory) { // потом поменяю
        val file = File(requireContext().filesDir,"jso.json")
        val gsonBuilder = GsonBuilder()
            .serializeNulls()
            .create()
        val type = object : TypeToken<MutableList<CardInventory>>() {}.type
        val jsonList : MutableList<CardInventory> = gsonBuilder.fromJson(file.readText(),type)

        try {
            jsonList.add(newCard)
            FileOutputStream(file).use {
                it.write(gsonBuilder.toJson(jsonList).toByteArray())
                it.flush()
                Log.d("FileWork","в файл был добавлен зависимый элемент")
            }
        }catch (e : IOException){
            e.printStackTrace()
            Log.e("FileError","$this")
        }
        //}
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
                if(i.index == card.index){
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

    private fun childHasInJsonFile(list : List<CardInventory>,newCard: CardInventory) : Boolean{
        for (i in list.iterator()){
            if(i.index == newCard.index){
                return true
            }
        }
        return false
    }

    private fun getCurrentTime(): String {
        val dateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return dateTime.format(formatter)
    }

    //достаточно дорогое действие? создавать каждый раз экземпляр barcodeView
    private fun alert(): AlertDialog {
        var value = ""
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.serial_camera_layout, null)
        val scopeScanner = dialogView.findViewById<BarcodeView>(R.id.alertScanner)
        val torch = dialogView.findViewById<SwitchCompat>(R.id.torch)

        val callback = BarcodeCallback {
            if (it != null) {
                scopeScanner.setTorch(false)
                sound.start()
                value = it.text
                scopeScanner.pause()
                Toast.makeText(requireContext(), "Успешное сканирование", Toast.LENGTH_SHORT).show()
                Log.d("Scanner", "сканирование выполнилось $this")
            }
        }
        scopeScanner.decodeContinuous(callback)
        scopeScanner.decoderFactory = DefaultDecoderFactory(
            listOf(
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.CODE_39,
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13,
                BarcodeFormat.QR_CODE,
                BarcodeFormat.ITF,
                BarcodeFormat.UPC_EAN_EXTENSION,
                BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_A,
                BarcodeFormat.CODABAR
            )
        )
        scopeScanner.resume()
        torch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scopeScanner.setTorch(true)
            } else {
                scopeScanner.setTorch(false)
            }
        }
        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("ок") { dialog, which ->
                if (value != "") {
                    second.setText(value)
                }
                dialog.dismiss()
            }
            .setNegativeButton("отмена") { dialog, which ->
                dialog.dismiss()
            }
            .create()

    }

    private fun reSaveDataFile(newCard : CardInventory,fileName: String = "jso.json") { // нужно изменить на добавить в файл данные
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
            Log.d("FileSave", "Файл удалось перезаписать")
            Toast.makeText(requireContext(), "Файл успешно перезаписан", Toast.LENGTH_SHORT).show()
        } catch (E: IOException) {
            E.printStackTrace()
            Log.d("FileSave", "Файл не удалось перезаписать")
        }
    }

    private fun reWrite(oldCard : CardInventory,newCard : CardInventory) : CardInventory {
        oldCard.index = newCard.index
        oldCard.SID = newCard.SID
        oldCard.UEDescription = newCard.UEDescription
        oldCard.ActionDateTime = newCard.ActionDateTime
        oldCard.Adress = newCard.Adress
        oldCard.Status = newCard.Status
        oldCard.inventNumb = newCard.inventNumb
        oldCard.SerialNumb = newCard.SerialNumb
        oldCard.IsSNEdited = newCard.IsSNEdited
        oldCard.UserName = newCard.UserName
        oldCard.Description = newCard.Description
        oldCard.Cabinet = newCard.Cabinet
        oldCard.Cod1C = newCard.Cod1C
        return oldCard
    }

    private fun createListUserName(data: DataDownloader): List<String> {
        val tempMap = data.getList()!!
        val tempList = mutableListOf<String>()
        for (i in tempMap.iterator()) {
            tempList.add(delete(i.userName))
        }
        return tempList
    }

    private fun delete(str: String): String {
        var newStr = str.replace("\"", "").also {
            it.replace(" (КВОТА-ИНВАЛИД)", "")
            it.replace(" (КВОТА - ИНВАЛИД) ", "")
        }
        return newStr
    }

    fun setCard(card: CardInventory) {
        this.papaCard = card
    }

    fun setOldUser(papa: NewUser) {
        this.papa = papa
    }

    companion object {
        fun newInstance(card: CardInventory, papa: NewUser): BabyCardFragment {
            val frag = BabyCardFragment()
            frag.setCard(card)
            frag.setOldUser(papa)
            return frag
        }
    }

    private fun getMax() : Int {
        val file = File(requireContext().cacheDir,"max.txt")
        return file.readText().toInt()
    }
    private fun updateMax(){
        FileWriter(File(requireContext().cacheDir,"max.txt")).use {
            it.write((max).toString()) //?
            it.flush()
        }
    }
}