package com.build.invertor.mainModule.singleActivity

import com.build.invertor.model.Model

interface ModelSharedInterface {

    fun getModel() : Model?

    fun updateModel(flag : Boolean)

    fun haveFiles() : Boolean

}