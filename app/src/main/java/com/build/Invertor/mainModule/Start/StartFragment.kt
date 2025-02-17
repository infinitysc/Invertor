package com.build.Invertor.mainModule.Start

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
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
import com.build.Invertor.mainModule.camera.CameraFragment
import com.build.Invertor.mainModule.Settings.Settings
import com.build.Invertor.model.NewUser
import com.build.Invertor.model.csv.DataDownloader
import com.build.Invertor.model.csv.User
import com.build.Invertor.model.json.JsonDownloader
import com.google.android.material.textfield.TextInputEditText
import java.io.*


/**
 * TODO:
 *  Это класс с которого начинается сессия , в этом классе идет выбор сотрудника
 *  в дальнейшем идет передача данных в CameraFragment.
 *
 *  Нужно сделать так чтобы оно передавала сотрудника(уже работает)
 *
 *  B сделать нужно когда выбирается новый сотрудник
 *  мы должны сохранять все изменения в json файл находящийся в ассетах а так же иметь возможность
 *  импортировать этот файл из файловой системы(Внешнее хранилище), а так же экспортировать его во внешнее(в папку Downloads):
 *
 *  для этого в макете нужно создать нажимающиеся икону настроек
 *  в котором будет работа с импортом экспортом файла данных
 *  а так же импорт данных с экселем
 *
 *  сохранение ввиде обращение в кеш после смены фио сотрудника нужно по прошлому сотруднику (его id) искать файл
 *  брать с него данные и создавать копию(либо перезаписывать оригинал) можно будет реализовать класс который изменяет ассеты как вариант
 *  экспорт файла ввиде прошлое имя файла + result формата json.
 *
 *
 *
 *  C ассетов можно только читать а это значит что там мы можем хранить только эксель файл.
 *  Нужно будет перенести тогда дата файл в files
 *
 * **/
class StartFragment  : Fragment(){

    private var data : DataDownloader? = null
    private var json : JsonDownloader? = null
    private val TagUser = "User"
    private var spinAdr = ""
    private lateinit var imageButtton : ImageButton
    private lateinit var searchEditText : AutoCompleteTextView
    private lateinit var button : Button
    private lateinit var cabinetEditor : TextInputEditText
    private lateinit var departament : TextView
    private lateinit var adressSpinner : Spinner
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
            data = DataDownloader(file.inputStream(),requireContext())
            //data!!.debugStr("254,\"Хранение(г. Бахчисарай, ул. Советская, 20)\",\"Отдел склад\"",requireContext())
        }
        else {
            Toast.makeText(requireContext(),"data файл не существует",Toast.LENGTH_LONG).show()
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



        //val array : ArrayAdapter<String> = ArrayAdapter(cont,R.layout.spinner,createListUserName(data!!))
        button = view.findViewById(R.id.start_work_button)
        searchEditText = view.findViewById(R.id.search_text)
        //searchEditText.setAdapter(array)
        departament = view.findViewById(R.id.departament)
        imageButtton = view.findViewById(R.id.imageButton)
        cabinetEditor = view.findViewById(R.id.cabinetEdit)

        adressSpinner = view.findViewById(R.id.adressSpinner)

        val adapter = ArrayAdapter(requireContext(),R.layout.spinner,listStr)
        adressSpinner.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        searchData()
        if(data != null){
            val array : ArrayAdapter<String> = ArrayAdapter(requireContext(),R.layout.spinner,listUserName!!)
            searchEditText.setAdapter(array)
            Toast.makeText(requireContext()," Данные с пользователями загружены",Toast.LENGTH_SHORT).show()
            Log.d("FileWork","excel data загружена в внутренее хранилище $this")
        }
        else{
            Toast.makeText(requireContext(),"Загрузите данные с пользователями",Toast.LENGTH_SHORT).show()
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
                var tempUser = searchEditText.text.toString()
                //positionIndex = position
                departament.text = searchDepartament(tempUser,list!!)
                //departament.text = searchDepartamentExp(tempUser,list!!)//exp
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                Toast.makeText(requireContext(),"Выбран пользователь $tempUser",Toast.LENGTH_SHORT).show()
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

    private fun searchDepartamentExp(str : String,List: List<User>) : String {
        Log.d("DebugSearchDepartament","${positionIndex}|${str}|${List[positionIndex].id}|${List[positionIndex].userName}|${List[positionIndex].departament}")
        return if(List[positionIndex].userName == str){
            List[positionIndex].departament
        }
        else {
            ""
        }
    }

    private fun installJsonFile(fileName : String = "jso.json") : JsonDownloader? {
        try {
            val inputStream = requireContext().openFileInput(fileName)
            return JsonDownloader(inputStream).apply {
                this.updateIndexList()
                this.updateListAfterIndex(requireContext())
            }
        }  catch (e : Exception){
            e.printStackTrace()
            return null
        }
    }

    private fun createListUserName(data : DataDownloader) : List<String> {
        val tempMap = data.getList()!!
        val tempList = mutableListOf<String>()
        for (i in tempMap.iterator()) {
            tempList.add(i.userName)
            //tempList.add("${i.id}|${i.userName}")
            /*if(i.userName =="Шеремет Эдуард Анатольевич"){
                Log.d("DebugSearchDepartament","${i.id}|${i.userName}|${i.departament}")
            }*/
        }
        return tempList
    }


    private fun searchUser() : User? {
        val chooseStr = searchEditText.text.toString()
        if(chooseStr != "") {
            val tempMap = data!!.createLinkedMap()
            return if (tempMap.containsKey(chooseStr)) {
                tempMap.get(chooseStr)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Пользователь не найден",
                    Toast.LENGTH_SHORT
                ).show()
                null
            }
        }
        else {
            Toast.makeText(requireContext(),"Выберите пользователя",Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun searchUserExp() : User? {
        val chooseStr = searchEditText.text.toString() // id|UserName
        if(chooseStr != ""){
            val idStr = chooseStr.substringBefore("|")
            val tempMap = data!!.createIdLinkedMao()
            return if(tempMap.containsKey(idStr)){
                tempMap.get(idStr)
            }else {
                Toast.makeText(
                    requireContext(),
                    "Пользователь не найден",
                    Toast.LENGTH_SHORT
                ).show()
                null
            }
        }
        else {
            Toast.makeText(requireContext(),"Выберите пользователя",Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun check(reqCode : Int) {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){ //работает и на 30+ андроиде
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),reqCode)
        }
        else {
            json = installJsonFile()
            if(json != null){
                if(spinAdr != "Выберите адрес")
                {
                    if(cabinetEditor.text.toString() == ""){
                        Toast.makeText(requireContext(),"Введите кабинет(окно)",Toast.LENGTH_SHORT).show()
                    }
                    else {
                        //this.cacheMax()
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
                            Toast.makeText(requireContext(),"Укажите ФИО сотрудника или его кабинет",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    Toast.makeText(requireContext(),"Выберите адресс",Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(requireContext(),"Вы не загрузили данные",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cacheMax() {
        val max = json?.searchMaxUEID()
        val file = File(requireContext().cacheDir,"max.txt")
        if(max != null){
            FileWriter(file).use{write ->
                write.write(max.toString())
                write.flush()
            }
        }

    }

    companion object {
        fun newInstance(
        ) : StartFragment {
            val fragment = StartFragment()
            return fragment
        }
    }
}