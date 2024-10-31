package com.build.Invertor.model.csv

import android.content.Context
import android.renderscript.ScriptGroup.Input
import android.util.Log
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStream

class DataDownloader(private val path : InputStream,private val mContext: Context? = null) {

    private val workBook : Workbook
    private var list : MutableList<User> = mutableListOf()

    init {

        workBook = XSSFWorkbook(path)
        inExcel(workSheet = workBook.getSheetAt(0),mContext)
    }

    private fun inExcel(workSheet : Sheet,mContext : Context? = null) {
        val iter = workSheet.iterator()
        while (iter.hasNext()){
            val str = checkStringInFile(iter.next())
            //experimental
            if(str != ""){
                val listEditedString = regexSplitterString(str)
                if(listEditedString.isNotEmpty()){
                    /*val debFun :(list : List<String>) -> String = {
                        var strings = ""
                        it.forEach(){
                            strings="$str,$it"
                        }
                        strings
                    }*/
                    //Log.d("DataWork","${debFun.invoke(listEditedString)}")
                    addToList(createUser(listEditedString[0].toInt(),listEditedString[1],listEditedString[2]))

                }
            }
            //stable(use in last Updates(0.1.5.A)
            /*
            if(str != ""){
                val listEditedString = editStr(str)
                addToList(createUser(listEditedString[0].toInt(),listEditedString[1],listEditedString[2]))
            }*/
        }
    }
    private fun checkStringInFile(row : Row, cell : Cell = row.getCell(0)) : String {
        return if(cell.stringCellValue is String && cell.stringCellValue.isNotEmpty()) {
            return cell.stringCellValue
        } else if(cell.stringCellValue.isEmpty()){""}
        else{
            cell.numericCellValue.toString()
        }
    }
    private fun delete(str : String) : String {
        var newStr = str.replace("\"","").also {
            it.replace("(КВОТА-ИНВАЛИД)","")
            it.replace(" (КВОТА - ИНВАЛИД) ","")
        }
        return newStr
    }
    private fun editStr(str: String) : List<String> {
        val newList : MutableList<String> =splitterString(str)
        newList.forEach() {
            it.replace("\"","")
            it.replace("(КВОТА-ИНВАЛИД)","")
        }

        return newList
    }

    //дебаг версия нового делителя , из startFragment в будущем (если там она еще осталась) улетит в тестирование
    fun debugStr(str : String,mContext : Context) {
        val newList = regexSplitterString(str)

        val file = File(mContext.cacheDir,"debugData.txt")
        FileWriter(file).use { writer ->
            writer.write(newList.size.toString())
            writer.write("\n")
            newList.forEach{
                writer.write("$it,")

            }

        }
    }

    //плохо делит
    private fun splitterString(str : String ) : MutableList<String> {
        return str.split(",").toMutableList()
    }

    private fun regexSplitterString(str : String ) : MutableList<String>{

        val regex = Regex("(\\d+),\"(.*?)\",\"(.*?)\"")
        val result = regex.find(str)
        if(result!=null){
            val (number,name,departamet) = result.destructured
            return mutableListOf<String>(number,name,departamet)
        }else{
            return mutableListOf()
        }
    }

    fun getList() : List<User>?  {
        return if(this.list.isNotEmpty()) {
            this.list
        } else {
            null
        }
    }

    fun createLinkedMap() : Map<String,User> {
        var tempMap = mutableMapOf<String,User>()
        for(i in this.list.iterator()) {
            tempMap.put(i.userName,i)
        }
        return tempMap
    }

    private fun addToList(user : User) {
        this.list.add(user)
    }

     private fun createUser(id : Int , userName : String , departament : String) : User {
        return User(id,userName,departament)
    }

}