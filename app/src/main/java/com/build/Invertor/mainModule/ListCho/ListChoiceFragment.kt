package com.build.Invertor.mainModule.ListCho

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.build.Invertor.R
import com.build.Invertor.mainModule.Card.CardFragment
import com.build.Invertor.model.NewUser
import com.build.Invertor.model.json.CardInventory
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class ListChoiceFragment : Fragment() {


    private var flag = false
    private var newList : List<CardInventory>? = null
    private val gsonEngine = GsonBuilder()
        .serializeNulls()
        .create()

    private lateinit var time : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var  textCod1C : TextView
    private var fileName = ""

    private var list : List<CardInventory>? = null
    private var user : NewUser? = null
    private  var bundle : Bundle = Bundle()
    private val manager : FragmentManager? by lazy {
        activity?.supportFragmentManager
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recycler_layout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            fileName = this.requireArguments().getString("json").toString()
        }catch (e : java.lang.IllegalStateException){
            e.printStackTrace()
            Log.d("CacheFile","не удалось получить имя файла так как бандл пуст")
        }

        recyclerView = view.findViewById(R.id.recyclerMama)
        textCod1C = view.findViewById(R.id.cod1c)
        textCod1C.setText("Код 1С : ${list?.get(0)?.Cod1C ?: ""}")
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        bundle.putString("json",fileName)
        recyclerView.adapter = Adapter(this.list!!.toMutableList(),this.user!!) { card: CardInventory, user_: NewUser ->
            manager?.beginTransaction()
                ?.replace(R.id.mainFrameLayout, CardFragment.newInstance(user_,card,bundle))
                ?.addToBackStack("list")
                ?.commit()
        }
    }

    override fun onStart() {
        super.onStart()

        if (flag) {
            newList = updateDataFromCache(fileName)
            flag = false
            val adapter = recyclerView.adapter as Adapter
            adapter.updateData(newList!!)
            Toast.makeText(requireContext(),"Адаптер успешно обновлен",Toast.LENGTH_SHORT).show()
            Log.d("CacheFile","${this.newList}")
        }
    }

    override fun onStop() {
        super.onStop()
        flag = true
    }

    override fun onPause() {
        super.onPause()
    }
    //вовремя onStop вызывается чтобы обновить данные во вью
    private fun updateDataFromCache(fileName : String): List<CardInventory>? {
        val file = checkFile(fileName)
        if(file != null){
            val type = object : TypeToken<List<CardInventory>>() {}.type
            val rawJsonString = file.readText()
            val jsonList= gsonEngine.fromJson<List<CardInventory>>(rawJsonString,type)
            if(jsonList.isNotEmpty()){
                return jsonList
            }
            else {
                Log.d("CacheFile","Json массив пустой $this")
                return null
            }
        }
        else {
            Log.d("CacheFile","Ошибка связанная с файлом,его не существует либо имя неверное $this")
            return null
        }
    }

    private fun updateDataList(file : File) : List<CardInventory>? {
        val type = object : TypeToken<List<CardInventory>>() {}.type
        val rawJsonString = file.readText()
        val jsonList= gsonEngine.fromJson<List<CardInventory>>(rawJsonString,type)
        if(jsonList.isNotEmpty()){
            return jsonList
        }
        else {
            Log.d("CacheFile","Json массив пустой")
            return null
        }
    }

    private fun checkFile(fileName: String) : File? {
        val cache = requireContext().cacheDir
        val file = File(cache,fileName)

        return if(file.exists()){
            file
        } else {
            Log.d("CacheFile","Файл не был найден в кеше")
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        File(requireContext().cacheDir,fileName).delete()
        Log.d("CacheFile","Файл уничтожен")
    }

    fun setList(list_ : List<CardInventory>) {
        this.list = list_
    }
    fun setUser(user_ : NewUser){
        this.user = user_
    }

    companion object {
        fun newInstance(list_ : List<CardInventory>,user_ : NewUser,bundle : Bundle) : ListChoiceFragment {
            val newFragment = ListChoiceFragment()
            newFragment.arguments = bundle
            newFragment.setList(list_)
            newFragment.setUser(user_)
            return newFragment
        }
    }
}