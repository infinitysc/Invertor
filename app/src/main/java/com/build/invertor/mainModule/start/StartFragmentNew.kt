package com.build.invertor.mainModule.start

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.build.Invertor.R
import com.build.invertor.mainModule.application.App

import com.build.invertor.mainModule.camera.CameraFragmentNew
import com.build.invertor.mainModule.settings.Settings
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import javax.inject.Inject

class StartFragmentNew : Fragment() {

    private lateinit var imageButtton : ImageButton
    private lateinit var searchEditText : AutoCompleteTextView
    private lateinit var button : Button
    private lateinit var cabinetEditor : TextInputEditText
    private lateinit var departament : TextView
    private lateinit var adressSpinner : Spinner
    //dagger
    @Inject
    lateinit var controller : StartFragmentController



    private val TagUser = "User"
    private var spinAdr = ""
    private var positionIndex = 0

    private val listAdress : List<String> by lazy {
        txtToList(requireContext().assets.open("iDAdress.txt"))
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

        (requireActivity().application as App).dagger.injectStartFragment(this)
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

    override fun onStart() {
        super.onStart()


        adressSpinner.adapter = ArrayAdapter(requireContext(),R.layout.spinner,listAdress)


        if(controller.checkDataIsNull()){
            val array = ArrayAdapter(requireContext(),R.layout.spinner,controller.createListUserName())

            searchEditText.setAdapter(array)

            useToast("Данные с пользователями загружены")
            Log.d("FileWork","data download to internal storage $this")

        }else{
            useToast("Загрузите данные с пользователями")
            Log.d("FileWork","data doesn't download to internal storage $this")
        }


        adressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
               spinAdr = listAdress[position]
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
            ){
                val userName = searchEditText.text.toString()
                departament.text = controller.searchDepartament(userName)
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                useToast("Выбран пользователь $userName")
                Log.d(TagUser,"Selected by the user $this")
            }
        }

        button.setOnClickListener(){
            launchCameraFragmentOldVersion(200)
        }

        imageButtton.setOnClickListener{
            Log.d("FragmentReplace","Cast to SettingsFragment")
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, Settings())
                .addToBackStack("start")
                .commit()
        }
    }


    private fun useToast(text : String) {
        Toast.makeText(requireContext(),text, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(reqCode : Int) : Boolean{

        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),reqCode)
            return true
        }
        return false
    }

    private fun launchCameraFragmentOldVersion(reqCode: Int) {

        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),reqCode)
        }

        else {
            if(controller.checkJsonIsNull()){
                if(spinAdr != "Выберите адрес")
                {
                    if(cabinetEditor.text.toString() == ""){
                        useToast("Введите кабинет(окно)")
                    }
                    else {
                        val singleUser = controller.searchUser(searchEditText.text.toString(),::useToast)
                        val newAbsUser = NewUser(singleUser,cabinetEditor.text.toString(),spinAdr)
                        if(singleUser != null){
                            val bundle = Bundle()
                            val list = controller.getJson()?.getList()
                            bundle.putParcelableArrayList("listJson", ArrayList())
                            bundle.putParcelable("user",newAbsUser)
                            val newFragment = CameraFragmentNew.newInstance(bundle)
                            Log.d("FragmentReplace","Cast to CameraFragment")
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.mainFrameLayout,newFragment)
                                .addToBackStack(
                                    "CameraFragment"
                                ).commit()
                        }
                        else {
                            useToast("Укажите ФИО сотрудника или его кабинет")
                        }
                    }
                }
            } else {
                useToast("Вы не загрузили данные или не заполнили одно из полей")
                Log.i("UserWorks","Json file is now download to internal storage or SpinnerAdress && CabinetEditor not filled")
            }
        }
    }
    private fun launchCameraFragment(){

        if(checkPermission(200)){
            if(controller.checkJsonIsNull()){
                if(spinAdr != "Выберите адресс"){
                    if(cabinetEditor.text.toString() == ""){
                        useToast("Введите кабинет(окно)")
                    }
                    else{
                        val singleUser = controller.searchUser(searchEditText.text.toString(),::useToast)
                        val newAbsUser = NewUser(singleUser,cabinetEditor.text.toString(),spinAdr)
                        if(singleUser != null){
                            val bundle = Bundle()
                            bundle.putParcelable("user",newAbsUser)
                            val newFragment = CameraFragmentNew.newInstance(bundle)
                            Log.d("FragmentReplace","Cast to CameraFragment")
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.mainFrameLayout,newFragment)
                                .addToBackStack(
                                    "CameraFragment"
                                ).commit()
                        }
                    }
                }else {
                    useToast("Выберите Адресс")
                }
            }
        }
    }
}