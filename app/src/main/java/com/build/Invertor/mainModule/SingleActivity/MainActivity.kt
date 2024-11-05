package com.build.Invertor.mainModule.SingleActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.build.Invertor.R
import com.build.Invertor.mainModule.Start.StartFragment
import com.build.Invertor.model.Model

class MainActivity : AppCompatActivity(), ModelSharedInterface{


    private val fragmentSupp = supportFragmentManager
    private var model : Model? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hub_layout)

        model = Model.getModel(applicationContext)

        val mainFragment = StartFragment()
        fragmentSupp.beginTransaction()
            .replace(R.id.mainFrameLayout,mainFragment)
            .commit()
    }

    override fun getModel(): Model? {
        return this.model
    }

    override fun haveFiles(): Boolean {
        return this.model?.checkFiles() ?: false
    }

    override fun updateModel(flag : Boolean) {

        if(flag == true){
            this.model = Model.getModel(mContexnt = applicationContext)
        }
        else{

        }

    }


}