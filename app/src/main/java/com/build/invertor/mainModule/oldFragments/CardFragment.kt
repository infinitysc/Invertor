package com.build.invertor.mainModule.oldFragments

import android.Manifest
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.build.Invertor.R
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.build.invertor.model.modelOld.json.json.CardInventory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.iterator

/**
 * Фрагмент в котором идет заполенение карточки устройства и привязка его к сотруднику.
 *
 * **/
class CardFragment : Fragment(){
    private lateinit var serialInputLayoyt : TextInputLayout
    private lateinit var activityContext : Context
    private lateinit var adress : TextView
    private lateinit var dest : TextView
    private lateinit var serNumberEditor : TextInputEditText
    private lateinit var spinnerStatus : Spinner
    private lateinit var userWidget : TextView
    private lateinit var ps : TextInputEditText
    private lateinit var saveButton : Button
    private lateinit var dependencyButton : Button
    private lateinit var oldUser : TextView
    private lateinit var inventNumb : TextView

    // controller
    private var max : Int = 0

    private val sound : MediaPlayer by lazy { MediaPlayer.create(requireContext(), R.raw.scanner_beep) }

    // controller
    private var user : NewUser? = null
    private var card : CardInventory? = null
    private var saveJsonString : String = ""


    private var saveCard : CardInventory? = null
    private var itemSelected : String = ""
    private val listSpin = listOf<String>("","В эксплуатации","Требуется ремонт","Находится на консервации",
        "Не соответствует требованиям эксплуатации","Не введен в эксплуатацию","Списание","Утилизация")
    private var oldSnNumber : String = ""
    // controller
    private var nameFile = "" // this is fileName from bundle(ListChoiceFragment) cameraFragment set null into bundle

    //delete
    private val activityFragmentManager : FragmentManager by lazy { activity?.supportFragmentManager!! }

    //delete
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.card_tech_layout,container,false)
    }


    //переименовать все имена переменных на более контекстные
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adress = view.findViewById(R.id.adress)
        dest = view.findViewById(R.id.description)
        serNumberEditor = view.findViewById(R.id.serialNumberEdit)
        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        userWidget = view.findViewById(R.id.user)
        ps = view.findViewById(R.id.note)
        saveButton = view.findViewById(R.id.saveButton)
        oldUser = view.findViewById(R.id.oldUser)
        inventNumb = view.findViewById(R.id.invenNumb)
        serialInputLayoyt = view.findViewById(R.id.serialNimberEditLayout)
        dependencyButton = view.findViewById(R.id.dependencyButton)
        spinnerStatus.adapter = ArrayAdapter(activityContext, R.layout.spinner, listSpin)

        if(card?.Status != null) {
            spinnerStatus.setSelection(listSpin.indexOf(card?.Status))
        }else {
            spinnerStatus.setSelection(listSpin.indexOf("В эксплуатации"))
        }

        serNumberEditor.setText(card?.SerialNumb)

        ps.setText(card?.Description)

        try {
            nameFile = this.requireArguments().getString("json").toString()
        }catch (e : IllegalStateException){
            e.printStackTrace()
            Log.e("NameFile","bundle пустой")
        }
        serialInputLayoyt.setEndIconOnClickListener {
            alert().show()
        }

    }

    // controller
    private fun getMax() : Int {
        val file = File(requireContext().cacheDir, "max.txt")
        return file.readText().toInt()
    }
    // controller
    private fun updateMax(){
        FileWriter(File(requireContext().cacheDir, "max.txt")).use {
            it.write((max).toString()) //?
            it.flush()
        }
    }

    private fun alert() : AlertDialog {
        var value = ""
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.serial_camera_layout,null)
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
                    serNumberEditor.setText(value)
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
        oldUser.text = "Старый пользователь : ${card?.UserName?.substringAfter("|")}"
        userWidget.text = "Новый пользователь : ${user?.user?.userName}"
        inventNumb.text = "Инвентарный номер : ${card?.inventNumb}"


        alert()
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                itemSelected = listSpin[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        saveButton.setOnClickListener(){
            // controller
                lifecycleScope.launch {
                    max = getMax()
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    lifecycleScope.launch {
                        var change = 0
                        oldSnNumber = serNumberEditor.text.toString()
                        if(oldSnNumber != ""){
                            change = 1
                        }
                        // controller
                        saveCard = createEditedCard(
                            user = this@CardFragment.user!!,
                            card = this@CardFragment.card,
                            newSerialNumberString = serNumberEditor.text.toString(),
                            date = "${getCurrentTime()}+03",
                            status = itemSelected,
                            change = change,
                            cabinet = this@CardFragment.user!!.cabinet.toString(), // заглушка
                            note = ps.text.toString(),
                        )
                        // controller
                        reSaveDataFile(saveCard!!)
                        if(nameFile != ""){
                            // controller
                            getCacheFileToReWrite(nameFile,saveCard!!)
                        }
                    }
                    activityFragmentManager.popBackStack()
            }
            else{
                if(ContextCompat.checkSelfPermission(activityContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
                }
                else {
                    var change = 0
                    oldSnNumber = serNumberEditor.text.toString()
                    if(oldSnNumber != ""){
                        change = 1
                    }
                    saveCard = createEditedCard(
                        user = this.user!!,
                        card = this.card,
                        newSerialNumberString = serNumberEditor.text.toString(),
                        "${getCurrentTime()}+03",
                        status = itemSelected,
                        change = change,
                        cabinet = this.user!!.cabinet.toString(),
                        note = ps.text.toString(),
                    )
                    reSaveDataFile(saveCard!!)
                    if(nameFile != ""){
                        getCacheFileToReWrite(nameFile,saveCard!!)
                    }
                    activityFragmentManager.popBackStack()
                }
            }
        }


        dependencyButton.setOnClickListener() {
            var change = 0
            oldSnNumber = serNumberEditor.text.toString()
            if(oldSnNumber != ""){
                change = 1
            }

            // controller
            val papaCard = createEditedCard(
                user = this.user!!,
                card = this.card,
                newSerialNumberString = serNumberEditor.text.toString(),
                "${getCurrentTime()}+03",
                status = itemSelected,
                change = change,
                cabinet = this.user!!.cabinet.toString(),
                note = ps.text.toString(),
            )
            if(papaCard.UEID == null){
                //useToast
                Toast.makeText(requireContext(),"Из дочернего обьекта сделать дочерний невозможно!",
                    Toast.LENGTH_SHORT).show()
            }
            else {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.mainFrameLayout,
                        BabyCardFragment.Companion.newInstance(papaCard, this.user!!)
                    )
                    .addToBackStack("dependency_Card")
                    .commit()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sound.release()
    }
    // controller + view
    private fun getCacheFileToReWrite(fileNameFromCache : String,card : CardInventory) {
        val gsonEngine = GsonBuilder()
            .serializeNulls()
            .create()
        val fileCacheDir = requireContext().cacheDir
        val file = File(fileCacheDir, fileNameFromCache)
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

    // controller
    private fun getCurrentTime(): String {
        val dateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return dateTime.format(formatter)
    }

    // controller + view
    private fun reSaveDataFile(newCard : CardInventory, fileName: String = "jso.json") {
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
            if (i.index == newCard.index) {
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
    //delete?
    private fun reWrite(oldCard : CardInventory, newCard : CardInventory) : CardInventory {
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
        oldCard.parentEqueipment = newCard.parentEqueipment
        return oldCard
    }

    //delete
    private fun saveJson(jsonStr : String , fileName : String = "${user?.user?.id}_${getCurrentTime().replace(":", "-")}.json"){
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val folder =
                File(activityContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "json")
            if (!folder.exists()) {
                val dirCreated = folder.mkdirs()
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

    //delete
    private fun createEditedCard(
        user : NewUser,
        card: CardInventory?,
        newSerialNumberString : String,
        date : String,
        status : String,
        change : Int,
        cabinet : String,
        note : String,
        ) : CardInventory {
        this.max = getMax()
        return CardInventory(
            index = card?.index!!,
            SID = card.SID,
            UEID = card.UEID,
            UEDescription = card.UEDescription,
            ActionDateTime = date,
            Adress = user.adress,
            Status = status,
            inventNumb = card.inventNumb,
            SerialNumb = newSerialNumberString,
            IsSNEdited = change,
            UserName = "${user.user?.id}|${user.user?.userName}",
            Description = if (note == "") {
                null
            } else {
                note
            },
            Cabinet = cabinet,
            Cod1C = card.Cod1C,
            parentEqueipment = card.parentEqueipment
        ).apply {
            updateMax()
        }
    }


    //to bundle
    fun setUser(user : NewUser?) {
        this.user = user
    }
    //to bundle
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