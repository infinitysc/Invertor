package com.build.Invertor.mainModule.SingleActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.build.Invertor.R
import com.build.Invertor.debug.DebugLogger
import com.build.Invertor.debug.DebugSharedInterface
import com.build.Invertor.mainModule.Start.StartFragment
import com.build.Invertor.model.Model

class MainActivity : AppCompatActivity(), ModelSharedInterface,DebugSharedInterface{


    private val fragmentSupp = supportFragmentManager
    private var model : Model? = null
    private val debugObj : DebugLogger = DebugLogger()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hub_layout)


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

    override fun clearContainer() {
       this.debugObj.clearContainer()
    }

    override fun getLoggerContainer(): List<String> {
       return this.debugObj.getLoggerContainer()
    }

    override fun putMessage(message: String) {
        this.debugObj.putMessage(message)
    }


}