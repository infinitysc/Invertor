package com.build.invertor.mainModule.start

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.build.Invertor.R
import com.build.Invertor.databinding.StartLayoutBinding
import com.build.invertor.mainModule.application.App
import com.build.invertor.mainModule.application.appComponent

import com.build.invertor.mainModule.camera.CameraFragmentNew
import com.build.invertor.mainModule.settings.LoaderFragment
import com.build.invertor.mainModule.settings.LoaderViewModel
import com.build.invertor.mainModule.viewModelFactory.DaggerViewModelFactory
import com.build.invertor.model.database.data.UserEntity
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.build.invertor.model.modelOld.json.csv.User
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import javax.inject.Inject
import kotlin.getValue

class StartFragmentNew : Fragment() {


    private val binding : StartLayoutBinding by lazy {
        StartLayoutBinding.inflate(layoutInflater)
    }

    private var user : UserEntity? = null
    @Inject
    lateinit var factory : DaggerViewModelFactory

    private val viewModel: StartViewModel by viewModels { factory }

    private val TagUser = "User"
    private var spinAdr = ""

    private val listAdress : List<String> by lazy {
        viewModel.txtToList(requireContext().assets.open("iDAdress.txt"))
    }

    override fun onAttach(context: Context) {
        context.appComponent.injectStartFragment(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.departament.observe(viewLifecycleOwner) { collector ->
            binding.departament.text = collector
        }
        viewModel.concreteUser.observe(viewLifecycleOwner){
            user = it
        }


        val array = ArrayAdapter(requireContext(),R.layout.spinner, mutableListOf<String>())
        binding.searchText.setAdapter(array)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userFlow.collect { users ->
                    array.clear()
                    array.addAll(users)
                    array.notifyDataSetChanged()
                }
            }
        }
        viewModel.getListUserNamesFlow()

    }


    override fun onStart() {
        super.onStart()


        binding.adressSpinner.adapter = ArrayAdapter(requireContext(),R.layout.spinner,listAdress)

        if(viewModel.checkData()){
            useToast("Данные с пользователями загружены")
            Log.d("FileWork","data download to internal storage $this")

        }else{
            useToast("Загрузите данные с пользователями")
            Log.d("FileWork","data doesn't download to internal storage $this")
        }

        binding.adressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

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
        binding.searchText.setOnClickListener {
            binding.searchText.setText("")
        }

        binding.searchText.onItemClickListener = object : AdapterView.OnItemClickListener{

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ){
                val userName = binding.searchText.text.toString()
                viewModel.searchDepartament(userName)
                viewModel.getUser(userName)
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
                useToast("Выбран пользователь $userName")
                Log.d(TagUser,"Selected by the user $this")
            }
        }

        binding.startWorkButton.setOnClickListener(){
            launchCameraFragment(200)
        }

        binding.imageButton.setOnClickListener{
            Log.d("FragmentReplace","Cast to SettingsFragment")
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, LoaderFragment())
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

    //change
    private fun launchCameraFragment(reqCode : Int) {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),reqCode)
        }
        else{
            if(spinAdr != "Выберите адрес"){
                if(binding.cabinetEdit.text.toString() == "")
                {
                    useToast("Введите кабинет(окно)")
                }
                else {
                    if(user != null){
                        //TODO: Нужна проверка на то что пользователь вообще есть такой.
                        val newAbsUser = NewUser(
                            user = User(id = user!!.id ,
                                departament = user!!.departament,
                                userName = user!!.user
                            ),
                            cabinet = binding.cabinetEdit.text.toString(),
                            adress = spinAdr )
                        val bundle = Bundle()
                        bundle.putParcelable("user",newAbsUser)

                        //TODO: Jetpack Navigation
                        val newFragment : CameraFragmentNew = CameraFragmentNew.newInstance(bundle)
                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.mainFrameLayout,newFragment)
                            .addToBackStack("StartFragment")
                            .commit()
                    }
                }
            }
        }
    }
    /*
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
    }*/
}