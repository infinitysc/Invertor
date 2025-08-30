package com.build.invertor.mainModule.Card

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.build.Invertor.R
import com.build.invertor.mainModule.application.App
import com.build.invertor.model.modelOld.json.json.CardInventory
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
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
    private lateinit var controller : CardFragmentController

    private val listSpin = listOf<String>("","В эксплуатации","Требуется ремонт","Находится на консервации",
        "Не соответствует требованиям эксплуатации","Не введен в эксплуатацию","Списание","Утилизация")

    private val sound : MediaPlayer by lazy {(MediaPlayer.create(requireContext(),R.raw.scanner_beep))}

    private var user : NewUser? = null
    private var card : CardInventory? =  null

    @Inject
    fun injectController(cardFragmentController: CardFragmentController) {
        this.controller = cardFragmentController
    }


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

        (requireActivity().application as App).dagger.injectCardFragment(this)
    }

    private fun Spinner.setDefaultStatus(listForSpinner : List<String>,status : String) {
        this.setSelection(listForSpinner.indexOf(status))
    }

    @Throws(NullPointerException::class)
    private fun checkCardAndUser() {
        if(card == null || user == null){

            Log.e("Null","Card || User in ${this@CardFragmentNew} null")

            throw NullPointerException("Card or User is null")
        }
    }




    override fun onStart() {
        controller.apply {
            setCard(this@CardFragmentNew.card!!)
            setUser(this@CardFragmentNew.user!!)
        }
        super.onStart()
        var itemSelected : String = ""
        checkCardAndUser()
        var max : Int = 0
        location.text = card?.Adress
        cardDescription.text = card?.UEDescription
        oldUser.text = "Старый пользователь : ${card?.UserName?.substringAfter("|")}"
        userWidget.text = "Новый пользователь : ${user?.user?.userName}"
        inventNumber.text = "Инвентарный номер : ${card?.inventNumb}"

        createAlertDialogWithScanner()

        statusSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                itemSelected = listSpin[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //IO
        saveButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                max = controller.getMax()
            }
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    private fun createAlertDialogWithScanner() : AlertDialog {

        var value : String = ""
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.serial_camera_layout,null)
        val scopeScanner = dialogView.findViewById<BarcodeView>(R.id.alertScanner)
        val torch = dialogView.findViewById<SwitchCompat>(R.id.torch)

        scopeScanner.apply {
            decodeContinuous { BarcodeCallback {
                if(it != null) {
                    scopeScanner.setTorch(false)
                    sound.start()
                    value = it.text
                    scopeScanner.pause()
                }
            } }
            decoderFactory = DefaultDecoderFactory(listOf(
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.CODE_39,
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13,
                BarcodeFormat.QR_CODE,
                BarcodeFormat.ITF,
                BarcodeFormat.UPC_EAN_EXTENSION,
                BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_A,
                BarcodeFormat.CODABAR
            ))
        }

        scopeScanner.resume()

        torch.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                scopeScanner.setTorch(true)
            }else {
                scopeScanner.setTorch(false)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("ок"){dialog, which ->
                if(value != ""){
                    serialNumberEdit.setText(value)
                }
                dialog.dismiss()
            }
            .setNegativeButton("отмена") {dialog, which ->
                dialog.dismiss()
            }
            .create()
    }



    companion object {
        fun newInstance(bundle : Bundle) : CardFragmentNew {
            return CardFragmentNew().apply {
                arguments = bundle
            }

        }
    }
}