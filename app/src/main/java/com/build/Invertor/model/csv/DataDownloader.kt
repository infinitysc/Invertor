package com.build.Invertor.model.csv

import android.content.Context
import android.renderscript.ScriptGroup.Input
import android.util.Log
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStream

class DataDownloader(
    private val path : InputStream,
    private val mContext: Context? = null
) {
    private val workBook : Workbook
    private val list : MutableList<User> = mutableListOf()

    init {
        workBook = XSSFWorkbook(path)
        inExcel(workSheet = workBook.getSheetAt(0),mContext)
    }

    private fun inExcel(workSheet : Sheet,mContext : Context? = null) {
        val iterator = workSheet.iterator()

        while (iterator.hasNext()){
            val str = checkStringInCell(iterator.next().getCell(0))

            if(str != ""){
                val listEditedString = regexSplitterString(str)

                if(listEditedString.isNotEmpty()){
                    list.add(User(
                        listEditedString[0].toInt(),
                        listEditedString[1],
                        listEditedString[2]
                    ))
                }
            }
        }
    }

    private fun checkStringInCell(cell : Cell) : String{
        return when(cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toString()
            else -> cell.stringCellValue ?: ""
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

    @TestOnly
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
        return this.list.ifEmpty {
            null
        }
    }

    fun createLinkedMap() : Map<String,User> {
        val tempMap = mutableMapOf<String,User>()
        for(i in this.list.iterator()) {
            tempMap[i.userName] = i
        }
        return tempMap
    }

    fun createIdLinkedMap() : Map<String,User>{
        val tempMap = mutableMapOf<String,User>()
        for(i in this.list.iterator()){
            tempMap[i.id.toString()] = i
        }
        return tempMap
    }

}