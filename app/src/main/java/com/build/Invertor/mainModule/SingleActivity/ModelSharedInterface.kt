package com.build.Invertor.mainModule.SingleActivity

import com.build.Invertor.model.Model

interface ModelSharedInterface {

    fun getModel() : Model?

    fun updateModel(flag : Boolean)

    fun haveFiles() : Boolean

}