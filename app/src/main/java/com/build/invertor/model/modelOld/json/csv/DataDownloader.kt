package com.build.invertor.model.modelOld.json.csv

import android.content.Context
import com.build.invertor.model.database.data.UserEntity
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.io.FileWriter
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named

class DataDownloader @Inject constructor(
    @Named("Excel") private val path : InputStream,
) : DataExcel {


    private val workBook : Workbook = XSSFWorkbook(path)
    private val list : MutableList<User> = mutableListOf()
    private val listDb : MutableList<UserEntity> = mutableListOf()

    init {
        inExcel(workSheet = workBook.getSheetAt(0),)
    }

    private fun inExcel(workSheet : Sheet) {
        val iterator = workSheet.iterator()

        while (iterator.hasNext()){
            val str = checkStringInCell(iterator.next().getCell(0))

            if(str != ""){
                val listEditedString = regexSplitterString(str)

                if(listEditedString.isNotEmpty()){
                    list.add(
                        User(
                        listEditedString[0].toInt(),
                        listEditedString[1],
                        listEditedString[2])
                    )
                    listDb.add(
                        UserEntity(
                            listEditedString[0].toInt(),
                            listEditedString[1],
                            listEditedString[2])
                    )
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

    override fun getList() : List<User>?  {
        return this.list.ifEmpty {
            null
        }
    }
    fun getListDb() : List<UserEntity> {
        return this.listDb.ifEmpty {
            emptyList<UserEntity>()
        }
    }

    override fun createLinkedMap() : Map<String, User> {
        val tempMap = mutableMapOf<String, User>()
        for(i in this.list.iterator()) {
            tempMap[i.userName] = i
        }
        return tempMap
    }

    override fun createIdLinkedMap() : Map<String, User>{
        val tempMap = mutableMapOf<String, User>()
        for(i in this.list.iterator()){
            tempMap[i.id.toString()] = i
        }
        return tempMap
    }

}