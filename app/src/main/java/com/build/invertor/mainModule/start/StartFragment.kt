package com.build.invertor.mainModule.start

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.build.Invertor.R
import com.build.invertor.mainModule.camera.CameraFragment
import com.build.invertor.mainModule.settings.Settings
import com.build.invertor.model.csv.NewUser
import com.build.invertor.model.csv.DataDownloader
import com.build.invertor.model.csv.User
import com.build.invertor.model.json.JsonDownloader
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*

class StartFragment  : Fragment(){
    private lateinit var imageButtton : ImageButton
    private lateinit var searchEditText : AutoCompleteTextView
    private lateinit var button : Button
    private lateinit var cabinetEditor : TextInputEditText
    private lateinit var departament : TextView
    private lateinit var adressSpinner : Spinner

    private var data : DataDownloader? = null
    private var json : JsonDownloader? = null
    private val TagUser = "User"
    private var spinAdr = ""
    private var positionIndex = 0
    private val activityFragmentManager : FragmentManager by lazy { activity?.supportFragmentManager!! }
    private val list : List<User>? by lazy {
      data?.getList()
    }
    private val listStr : List<String> by lazy {
        txtToList(requireContext().assets.open("iDAdress.txt"))
    }
    private val listUserName : List<String>? by lazy {
        createListUserName(data!!)
    }

    private fun txtToList(input : InputStream) : List<String> {
        return try {
            BufferedReader(InputStreamReader(input)).use { reader ->
                reader.lineSequence().toList()
            }
        } catch (e : IOException){
            e.printStackTrace()
            listOf<String>()
        }
    }

    private fun searchData() {
        val fileName = "data.xlsx"
        val file = File(requireContext().filesDir,fileName)
        if(file.exists()){
            data = DataDownloader(file.inputStream())
        }
        else {
            useToast("data файл не существует")
            Log.d("FileWork","Эксель файл не существует и не может быть найдет")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button = view.findViewById(R.id.start_work_button)
        searchEditText = view.findViewById(R.id.search_text)
        departament = view.findViewById(R.id.departament)
        imageButtton = view.findViewById(R.id.imageButton)
        cabinetEditor = view.findViewById(R.id.cabinetEdit)
        adressSpinner = view.findViewById(R.id.adressSpinner)
        adressSpinner.adapter = ArrayAdapter(requireContext(),R.layout.spinner,listStr)

    }

    override fun onStart() {
        super.onStart()

        searchData()
        if(data != null){
            val array : ArrayAdapter<String> = ArrayAdapter(requireContext(),R.layout.spinner,listUserName!!)
            searchEditText.setAdapter(array)
            useToast("Данные с пользователями загружены")
            Log.d("FileWork","excel data загружена в внутренее хранилище $this")
        }
        else{
            useToast("Загрузите данные с пользователями")
            Log.d("FileWork","Excel data не загружена во внутреннее хранилище $this")
        }


        adressSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinAdr = listStr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        searchEditText.setOnClickListener {
            searchEditText.setText("")
        }
        searchEditText.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val tempUser = searchEditText.text.toString()
                departament.text = searchDepartament(tempUser,list!!)
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                useToast("Выбран пользователь $tempUser")
                Log.d(TagUser,"Выбран пользователь $this")
            }
        }

        button.setOnClickListener(){
            check(200)
        }

        imageButtton.setOnClickListener{
            Log.d("FragmentReplace","Вызван следующий фрагмент SettingsFragment")
            activityFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, Settings())
                .addToBackStack("start")
                .commit()
        }
    }

    private fun searchDepartament(str : String,list : List<User>) : String{
        for(i in list.iterator()){
            if(i.userName == str){
                Log.d("DebugSearchDepartament","${i.id}|${i.userName}|${i.departament}")
                return i.departament
            }
        }
        return ""
    }

    private fun installJsonFile(fileName : String = "jso.json") : JsonDownloader? {
        try {
            val inputStream = requireContext().openFileInput(fileName)
            return JsonDownloader(inputStream).apply {
                this.updateIndexList()
                updateListAfterIndex(requireContext(),this)
            }
        }  catch (e : Exception){
            e.printStackTrace()
            return null
        }
    }

    private fun updateListAfterIndex(context : Context,jsonDownloader: JsonDownloader) {
        if(jsonDownloader.getFlagIndex()) {
            val gson = Gson().newBuilder()
                .serializeNulls()
                .create()
            val fileDir = context.filesDir
            val file = File(fileDir,"jso.json")
            val str = gson.toJson(this.list)
            val cacheDir = context.cacheDir
            File(cacheDir,"max.txt")

            val max = jsonDownloader.getMaxUEID()

            if(max > 0){
                FileWriter(File(cacheDir,"max.txt")).use {
                    it.write(max.toString())
                    it.flush()
                }
            }

            FileOutputStream(file).use {
                it.write(str.toByteArray())
                it.flush()
            }
        }
    }

    //tag:Model
    private fun createListUserName(data : DataDownloader) : List<String> {
        val tempMap = data.getList()!!
        val tempList = mutableListOf<String>()

        for (i in tempMap.iterator()) {
            tempList.add(i.userName)
        }

        return tempList
    }

    private fun searchUser() : User? {
        val chooseStr = searchEditText.text.toString()

        //tag:Model
        if(chooseStr != "") {
            val tempMap = data!!.createLinkedMap()
            return if (tempMap.containsKey(chooseStr)) {
                tempMap.get(chooseStr)
            } else {
                useToast("Пользователь не найден")
                null
            }
        }
        else {
            useToast("Выберите пользователя")
            return null
        }
    }

    private fun useToast(text : String) {
        Toast.makeText(requireContext(),text,Toast.LENGTH_SHORT).show()
    }

    private fun checkPermissionCamera(reqCode : Int ) {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),reqCode)
        }
    }

    private fun check(reqCode : Int) {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),reqCode)
        }
        else {
            json = installJsonFile()
            if(json != null){
                if(spinAdr != "Выберите адрес")
                {
                    if(cabinetEditor.text.toString() == ""){
                        useToast("Введите кабинет(окно)")
                    }
                    else {
                        val singleUser = searchUser()
                        val newAbsUser = NewUser(singleUser,cabinetEditor.text.toString(),spinAdr)
                        if(singleUser != null){
                            val newFragment = CameraFragment.newInstance(newAbsUser, json)
                            Log.d("FragmentReplace","Вызван следующий фрагмент CameraFragment")
                            activityFragmentManager.beginTransaction()
                                .replace(R.id.mainFrameLayout,newFragment)
                                .addToBackStack("MainFragment")
                                .commit()
                        }
                        else {
                            useToast("Укажите ФИО сотрудника или его кабинет")
                        }
                    }
                }
                else {
                    useToast("Выберите адрес")
                }
            }
            else {
                useToast("Вы не загрузили данные")
            }
        }
    }

}