package com.build.invertor.model.modelOld.json

import com.build.invertor.model.modelOld.json.csv.DataDownloader
import com.build.invertor.model.modelOld.json.csv.DataExcel
import com.build.invertor.model.modelOld.json.json.DataAccesInterface
import com.build.invertor.model.modelOld.json.json.JsonDownloader
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @Binds
    fun getData(dataDownloader: DataDownloader) : DataExcel

    @Binds
    fun getJsonData(jsonDownloader: JsonDownloader) : DataAccesInterface

}