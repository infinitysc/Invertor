package com.build.Invertor.mainModule.camera

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.build.Invertor.R
import com.build.Invertor.mainModule.Card.CardFragment
import com.build.Invertor.mainModule.ListCho.ListChoiceFragment
import com.build.Invertor.mainModule.SingleActivity.MainActivity
import com.build.Invertor.mainModule.SingleActivity.ModelSharedInterface
import com.build.Invertor.model.Model
import com.build.Invertor.model.NewUser
import com.build.Invertor.model.json.CardInventory
import com.build.Invertor.model.json.JsonDownloader
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 *
 *  В этом фрагменте происходит сканирование и вывод информации об сотруднике и код который мы получили от сканера
 *
 *  TODO:
 *  Оптимизировать работу данного фрагмента
 * **/
class CameraFragment : Fragment(){


    /**
     * В будущем будет сохранять выбранный JsonObject или JsonArray в кеш и оттуда брать данные начнет
     * так я прежде всего как и планировал буду пользоваться кешем и это будет безопаснее
     * отправил данные -> обработал их -> сохранил в файл -> обновил исходники и почистил кеш
     * **/

    private lateinit var button : Button
    private lateinit var contex : Context
    private lateinit var valueText : TextView
    private lateinit var userText : TextView
    private lateinit var barcodeView : BarcodeView
    private lateinit var switch : SwitchCompat

    private val sound : MediaPlayer by lazy { MediaPlayer.create(requireContext(),R.raw.scanner_beep) }

    private var singleList : NewUser? = null
    private var jsonDownloader: JsonDownloader? = null
    private var valueString : String = "defaultStringValue"
    private val formats = listOf(
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_93,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
        BarcodeFormat.CODABAR,
        BarcodeFormat.UPC_E,
        BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_EAN_EXTENSION
    )
    private val activityFragmentManager : FragmentManager by lazy { activity?.supportFragmentManager!! }
    private val listInCode : List<String> by lazy  {jsonDownloader?.getListCode()!!}
    override fun onAttach(context: Context) {
        this.contex = context
        /*if(context is ModelSharedInterface){
            model = context.getModel()
        }*/
        super.onAttach(context)
    }

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

        button = view.findViewById(R.id.but)
        valueText = view.findViewById(R.id.value)
        userText = view.findViewById(R.id.userValue)
        barcodeView = view.findViewById(R.id.barcode_view)
        switch = view.findViewById(R.id.switch_torch)

        userText.text = singleList!!.user?.userName
        barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.decodeContinuous(callback)


    }

    private val callback = BarcodeCallback { result ->
        if(result != null){
            sound.start()
            valueText.text = result.text
            valueString = result.text
            barcodeView.pause()
        }
    }


    override fun onStart() {
        super.onStart()

        valueText.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Редактирование текста")

            val array : ArrayAdapter<String?> = ArrayAdapter(requireContext(),R.layout.spinner,listInCode)
            val autocom = AutoCompleteTextView(requireContext())
            autocom.setAdapter(array)

            /*val editText = EditText(requireContext())
            editText.setText(valueText.text)*/

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

        switch.setOnCheckedChangeListener{_ , isCheked ->
            Log.d("Scanner","фонарик $this")
            if(isCheked){
                barcodeView.setTorch(true)
            }
            else {
                barcodeView.setTorch(false)
            }

        }
        button.setOnClickListener{

            if (jsonDownloader != null) {
                val card = searcheable(valueString)
                when {
                    card.isEmpty() -> Toast.makeText(requireContext(),"Сканируйте или введите значение",Toast.LENGTH_SHORT).show()
                    card.size == 1 -> activityFragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout, CardFragment.newInstance(singleList, card[0], null))
                        .addToBackStack("papa")
                        .commit()
                    card.size >= 2 -> multipleChoice(card)
                    else -> {
                        Toast.makeText(requireContext(),"Значение не найдено",Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    "Импортируйте список с данными",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
        valueString = "defaultStringValue"

    }

    private fun updateData()  {
        Log.d("FileWork","Апдейт файла json")
        val file = requireContext().openFileInput("jso.json")
        this.jsonDownloader = JsonDownloader(file)
    }

    override fun onDestroy() {
        super.onDestroy()
       // newFileIntoInternalStorage("jso.json",this.jsonDownloader!!) // nullpointerexception когда свайп без импорта?
        sound.release()
    }



    private fun multipleChoice(list : List<CardInventory>) {
        cacheSaver(list)
        val bundle : Bundle = Bundle()
        bundle.putString("json","${singleList?.user?.userName}.json")
        Toast.makeText(requireContext(),"Обьектов найдено ${list.size}",Toast.LENGTH_SHORT).show()
        activityFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, ListChoiceFragment.newInstance(
                list,
                this.singleList!!,
                bundle
                ))
            .addToBackStack("camera")
            .commit()
    }

    private fun cacheSaver(list : List<CardInventory>){
        val cacheDir = requireContext().cacheDir
        val newCacheFile = File(cacheDir,"${singleList?.user?.userName}.json")
        val gsonEngine = GsonBuilder()
            .serializeNulls()
            .create()
        val jsonList = gsonEngine.toJson(list)
        try {
            FileOutputStream(newCacheFile).use {
                it.write(jsonList.toByteArray())
                it.flush()
            }
            Log.e("CacheFile", "Файл в кеше сохранен")
        }
        catch (E : IOException){
            E.printStackTrace()
            Log.e("CacheFile","Файл не сохранен")
        }

    }

    private fun searcheable(nomer : String) : List<CardInventory> {

        val mutList : MutableList<CardInventory> = mutableListOf()
/*
        val mapTemp = jsonDownloader!!.getPairLinkedMap()

        jsonDownloader!!.debugToCache(requireContext())

        for(i in mapTemp.iterator()){
            val key = i.key

            if(key.second == nomer || key.first == nomer){
                mutList.add(i.value)
            }
        }*/

        val maptemp = jsonDownloader!!.exp_createDoubleLink()

        for(i in maptemp.iterator()){
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
            list : NewUser? ,
            json : JsonDownloader?
        ) : CameraFragment {
            val newFragment= CameraFragment()
            newFragment.setJson(json)
            newFragment.setSingleList(list)
            return newFragment
        }
    }

}