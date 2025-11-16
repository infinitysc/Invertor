package com.build.invertor.mainModule.oldFragments

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.build.Invertor.R
import com.build.invertor.mainModule.oldFragments.CardFragment
import com.build.invertor.mainModule.oldFragments.ListChoiceFragment
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.build.invertor.model.modelOld.json.json.CardInventory
import com.build.invertor.model.modelOld.json.json.JsonDownloader
import com.build.invertor.model.modelOld.json.json.JsonFileOpener
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.collections.iterator

class CameraFragment : Fragment(){

    private lateinit var buttonToNextFragment : Button
    private lateinit var valueText : TextView
    private lateinit var userText : TextView
    private lateinit var barcodeView : BarcodeView
    private lateinit var switch : SwitchCompat
    private lateinit var refresh : ImageView

    private val sound : MediaPlayer by lazy { MediaPlayer.create(requireContext(), R.raw.scanner_beep) }
    private var singleList : NewUser? = null
    private var jsonDownloader: JsonDownloader? = null
    private var valueString : String = "defaultStringValue"
    private val formats = listOf(
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_93,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
    )
    private val activityFragmentManager : FragmentManager by lazy { activity?.supportFragmentManager!! }
    private val listInCode : List<String> by lazy  {jsonDownloader?.getListCode()!!}
    private val callback = BarcodeCallback { result ->
        if (result != null) {
            sound.start()
            valueText.text = result.text
            valueString = result.text
            barcodeView.pause()
        }
    }
    private val gsonEngineSerNulls = GsonBuilder()
        .serializeNulls()
        .create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("camera","$this create")
        return inflater.inflate(R.layout.new_camera_fragment_layout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonToNextFragment = view.findViewById(R.id.button_to_next_fragment)
        valueText = view.findViewById(R.id.value)
        userText = view.findViewById(R.id.userValue)
        barcodeView = view.findViewById(R.id.barcode_view)
        switch = view.findViewById(R.id.switch_torch)
        refresh = view.findViewById(R.id.refreshButton)
        userText.text = singleList!!.user?.userName
        barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.decodeContinuous(callback)
    }


    override fun onStart() {
        super.onStart()

        valueText.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Редактирование текста")

            val array : ArrayAdapter<String?> =
                ArrayAdapter(requireContext(), R.layout.spinner, listInCode)
            val autocom = AutoCompleteTextView(requireContext())
            autocom.setAdapter(array)

            val editText = EditText(requireContext())
            editText.setText(valueText.text)

            alert.setView(autocom)

            alert.setPositiveButton("OK") { dialog, which ->
                valueText.setText(autocom.text.toString())
                valueString = autocom.text.toString()
                dialog.dismiss()
            }
            alert.setNegativeButton("Отмена") { dialog,which->
                dialog.dismiss()
            }
            val dialog = alert.create()
            dialog.show()
        }

        updateData()

        barcodeView.resume()

        refresh.setOnClickListener{
            barcodeView.pause()
            barcodeView.resume()
            valueText.text = ""
        }

        switch.setOnCheckedChangeListener{_ , isCheked ->
            Log.d("Scanner","фонарик $this")
            if(isCheked){
                barcodeView.setTorch(true)
            }
            else {
                barcodeView.setTorch(false)
            }

        }

        buttonToNextFragment.setOnClickListener{
            if (jsonDownloader != null) {
                val card = searchDataByNumber(valueString)

                checkCard(card)
            } else {
                useToast("Импортируйте список с данными")
            }

        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
        valueString = "defaultStringValue"

    }

    override fun onDestroy() {
        super.onDestroy()
        sound.release()
    }

    private fun useToast(text : String) {
        Toast.makeText(requireContext(),text, Toast.LENGTH_SHORT).show()
    }

    private fun createAlertDialog() {
        val alert = AlertDialog.Builder(requireContext())
        val array = ArrayAdapter<String>(requireContext(), R.layout.spinner, listInCode)
        val autoComText = AutoCompleteTextView(requireContext())

        alert.setTitle("")
        autoComText.setAdapter(array)
        alert.setView(autoComText)

        setPositiveButton(alert,autoComText)
        setNegotiveButton(alert)

        val dialog = alert.create()
        dialog.show()
    }

    private fun setPositiveButton(alert : AlertDialog.Builder, autocom : AutoCompleteTextView) {
        alert.setPositiveButton("OK") { dialog, which ->
            valueText.setText(autocom.text.toString())
            valueString = autocom.text.toString()
            dialog.dismiss()
        }
    }

    private fun setNegotiveButton(alert : AlertDialog.Builder) {
        alert.setNegativeButton("Отмена") { dialog,which->
            dialog.dismiss()
        }
    }

    private fun updateData()  {
        Log.d("FileWork","update json")
        try {
            val file = requireContext().openFileInput("jso.json")
            this.jsonDownloader =
                JsonDownloader(JsonFileOpener(file, gson = GsonBuilder().create()))
        }
        catch (e : FileNotFoundException) {
            e.printStackTrace()
            Log.d("FileWork","File not found")
            useToast("Ошибка: файл не был найден")
        }
    }

    private fun multipleChoice(list : List<CardInventory>) {
        cacheSaver(list)
        val bundle : Bundle = Bundle()
        bundle.putString("json","${singleList?.user?.userName}.json")

        useToast("Обьектов найдено ${list.size}")

        activityFragmentManager.beginTransaction()
            .replace(
                R.id.mainFrameLayout, ListChoiceFragment.Companion.newInstance(
                list,
                this.singleList!!,
                bundle
                ))
            .addToBackStack("camera")
            .commit()
    }

    private fun cacheSaver(list : List<CardInventory>){
        val cacheDir = requireContext().cacheDir

        if(cacheContainsFile()){
            requireContext().cacheDir.listFiles()?.forEach {
                it.deleteRecursively()
            }
        }

        val newCacheFile = File(cacheDir, "${singleList?.user?.userName}.json")
        val jsonList = gsonEngineSerNulls.toJson(list)

        try {
            FileOutputStream(newCacheFile).use {
                it.write(jsonList.toByteArray())
                it.flush()
            }
            Log.i("CacheFile", "file saved in cache")
        }
        catch (E : IOException){
            E.printStackTrace()
            Log.e("CacheFile","file doesn't save")
        }

    }

    private fun checkCard(card : List<CardInventory>) {
        when {
            card.isEmpty() -> useToast("Сканируйте или введите значение")
            card.size == 1 -> activityFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, CardFragment.Companion.newInstance(singleList, card[0], null))
                .addToBackStack("papa")
                .commit()

            card.size >= 2 -> multipleChoice(card)
            else -> {
                useToast("Значение не найдено")
            }
        }
    }

    private fun cacheContainsFile() : Boolean{
        if(requireContext().cacheDir.listFiles()!!.isEmpty()){
            return true
        }
        else {
            return false
        }
    }

    //tag:Controller
    private fun searchDataByNumber(nomer : String) : List<CardInventory> {
        val mutList : MutableList<CardInventory> = mutableListOf()
        val mapTemp = jsonDownloader!!.getxDoubleLink()

        for(i in mapTemp.iterator()){
            val key = i.key
            if(key.second == nomer || key.first == nomer){
                for(j in i.value.iterator()){
                    mutList.add(j)
                }
            }
        }

        return mutList
    }

    fun setJson(js : JsonDownloader?)  {
        this.jsonDownloader = js
    }

    fun setSingleList(list : NewUser?) {
        this.singleList = list
    }

    companion object {
        fun newInstance(
            list : NewUser?,
            json : JsonDownloader?
        ) : CameraFragment {
            val newFragment= CameraFragment()
            newFragment.setJson(json)
            newFragment.setSingleList(list)
            return newFragment
        }
    }

}