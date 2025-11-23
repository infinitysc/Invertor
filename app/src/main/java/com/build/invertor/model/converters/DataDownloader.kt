package com.build.invertor.model.converters

import com.build.invertor.model.database.data.UserEntity
import com.build.invertor.model.modelOld.json.csv.User
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.iterator

class DataDownloader constructor(
    private val path : InputStream,
) {


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
                            listEditedString[2]
                        )
                    )
                    listDb.add(
                        UserEntity(
                            listEditedString[0].toInt(),
                            listEditedString[1],
                            listEditedString[2]
                        )
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

    fun getListDb() : List<UserEntity> {
        return this.listDb.ifEmpty {
            emptyList<UserEntity>()
        }
    }


}