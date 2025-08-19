package com.build.invertor.mainModule.Card

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.build.Invertor.R
import com.build.invertor.model.modelOld.json.CardInventory
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.jvm.Throws

class CardFragmentNew : Fragment() {

    private lateinit var saveButton : Button
    private lateinit var serialNumberInputLayout : TextInputLayout
    private lateinit var location : TextView
    private lateinit var serialNumberEdit : TextInputEditText
    private lateinit var statusSpinner : Spinner
    private lateinit var userWidget : TextView
    private lateinit var cardDescription : TextView
    private lateinit var note : TextInputEditText
    private lateinit var createDependencyButton : Button
    private lateinit var oldUser : TextView
    private lateinit var inventNumber : TextView
    private var controller : CardFragmentController? = null
    private val listSpin = listOf<String>("","В эксплуатации","Требуется ремонт","Находится на консервации",
        "Не соответствует требованиям эксплуатации","Не введен в эксплуатацию","Списание","Утилизация")

    private val sound : MediaPlayer by lazy {(MediaPlayer.create(requireContext(),R.raw.scanner_beep))}

    private var user : NewUser? = null
    private var card : CardInventory? =  null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.card_tech_layout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = this@CardFragmentNew.arguments?.getParcelable("user")
        card = this@CardFragmentNew.arguments?.getParcelable("card")

        location = view.findViewById(R.id.adress)
        cardDescription = view.findViewById(R.id.description)
        serialNumberEdit = view.findViewById(R.id.serialNumberEdit)
        statusSpinner = view.findViewById(R.id.spinnerStatus)
        userWidget = view.findViewById(R.id.user)
        note = view.findViewById(R.id.note)
        saveButton = view.findViewById(R.id.saveButton)
        oldUser = view.findViewById(R.id.oldUser)
        inventNumber = view.findViewById(R.id.invenNumb)
        serialNumberInputLayout = view.findViewById(R.id.serialNimberEditLayout)
        createDependencyButton = view.findViewById(R.id.dependencyButton)
        statusSpinner.adapter = ArrayAdapter(requireContext(),R.layout.spinner,listSpin)

        //change to fun
        if(card?.Status != null) {
            statusSpinner.setDefaultStatus(listSpin, card!!.Status!!)
        }else {
            statusSpinner.setDefaultStatus(listSpin,"В эксплуатации")
        }

        serialNumberEdit.setText(card?.SerialNumb)

        note.setText(card?.Description)

    }



    private fun Spinner.setDefaultStatus(listForSpinner : List<String>,status : String) {
        this.setSelection(listForSpinner.indexOf(status))
    }

    @Throws(NullPointerException::class)
    fun checkCardAndUser() {
        if(card == null || user == null){

            Log.e("Null","Card || User in ${this@CardFragmentNew} null")

            throw NullPointerException("Card or User is null")
        }
    }

    override fun onStart() {
        super.onStart()

        checkCardAndUser()

        controller = CardFragmentController(requireContext().cacheDir,card!!,user!!)




    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        fun newInstance(bundle : Bundle) : CardFragmentNew {
            return CardFragmentNew().apply {
                arguments = bundle
            }

        }
    }
}