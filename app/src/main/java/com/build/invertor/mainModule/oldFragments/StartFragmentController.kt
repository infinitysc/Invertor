package com.build.invertor.mainModule.oldFragments

import android.content.Context
import android.util.Log
import com.build.invertor.mainModule.oldFragments.AbstractController
import com.build.invertor.model.modelOld.json.csv.DataDownloader
import com.build.invertor.model.modelOld.json.csv.User
import com.build.invertor.model.modelOld.json.json.JsonDownloader
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.collections.forEach

class StartFragmentController @Inject constructor(
    private val context : Context
) : AbstractController(context) {

    private val dataOld : DataDownloader? = this.getDataFile()
    private val jsonOld : JsonDownloader? = this.getJsonFile()

    fun getJson() = jsonOld



    fun createListUserName() : List<String>{
        val list = dataOld?.getList()!!
        val tempList = mutableListOf<String>()

        list.forEach {
            tempList.add(it.userName)
        }

        return tempList
    }

    fun searchUser(str : String,toast: (String) -> Unit ) : User? {
        val tempList = dataOld!!.createLinkedMap()
        if (str != "") {
            return if (tempList.containsKey(str)) {
                tempList[str]
            } else {
                toast.invoke("Пользователь не найден")
                null
            }
        }else {
            toast.invoke("Выберите пользователя")
            return null

        }
    }

    fun searchDepartament(str : String) : String {
        val list = createListUsers()

        list.forEach {
            if(it.userName == str){
                return it.departament
            }
        }

        return ""
    }

    private fun createListUsers() : List<User> {
        return dataOld?.getList()!!
    }

    fun checkDataIsNull() : Boolean {
        if(dataOld != null) {
            Log.i("CheckData","Data is't null")
            return true
        }
        Log.i("CheckData","Data is'n null")
        return false
    }

    fun checkJsonIsNull() : Boolean {
        if(jsonOld != null){
            Log.i("CheckJson","Json is't null")
            return true
        }
        Log.i("CheckJson","Json is null")
        return false
    }

    fun fileFromAssetsToList(input: InputStream) : List<String> {

        return try {
            BufferedReader(InputStreamReader(input)).use { reader ->
                reader.lineSequence().toList()
            }
        } catch (e : IOException){
            e.printStackTrace()
            listOf<String>()
        }
    }

}