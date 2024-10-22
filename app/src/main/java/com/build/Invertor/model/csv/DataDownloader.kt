package com.build.Invertor.model.csv

import android.renderscript.ScriptGroup.Input
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class DataDownloader(private val path : InputStream) {

    private val workBook : Workbook
    private var list : MutableList<User> = mutableListOf()

    init {

        workBook = XSSFWorkbook(path)
        inExcel(workSheet = workBook.getSheetAt(0))
    }

    private fun inExcel(workSheet : Sheet) {
        val iter = workSheet.iterator()
        while (iter.hasNext()){
            val str = checkStringInFile(iter.next())
            val listEditedString = editStr(str)
            addToList(createUser(listEditedString[0].toInt(),listEditedString[1],listEditedString[2]))
        }
    }
    private fun checkStringInFile(row : Row, cell : Cell = row.getCell(0)) : String {
        return if(cell.stringCellValue is String && cell.stringCellValue.isNotEmpty()) {
            return delete(cell.stringCellValue)
        } else {
            return cell.numericCellValue.toString()
        }
    }
    private fun delete(str : String) : String {
        var newStr = str.replace("\"","").also {
            it.replace(" (КВОТА-ИНВАЛИД)","")
            it.replace(" (КВОТА - ИНВАЛИД) ","")
        }
        return newStr
    }
    private fun editStr(str: String) : List<String> {
        val newList : MutableList<String> = splitterString(str)
        newList.forEach() {
            it.replace("\"","")
            it.replace("(КВОТА-ИНВАЛИД)","")
        }
        return newList
    }
    private fun splitterString(str : String ) : MutableList<String> {
        return str.split(",").toMutableList()
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