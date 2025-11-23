package com.build.invertor.mainModule.card

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.build.Invertor.R
import com.build.Invertor.databinding.CardTechLayoutBinding
import com.build.invertor.mainModule.application.appComponent
import com.build.invertor.mainModule.viewModelFactory.DaggerViewModelFactory
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.Throws

class CardFragmentNew : Fragment() {

    @Inject
    lateinit var factory : DaggerViewModelFactory

    private val alertWithScanner : AlertDialog by lazy { createAlertDialogWithScanner() }
    private val viewModel : CardViewModel by viewModels { factory }
    private val binding : CardTechLayoutBinding by lazy { CardTechLayoutBinding.inflate(layoutInflater) }
    private val sound : MediaPlayer by lazy {(MediaPlayer.create(requireContext(),R.raw.scanner_beep))}

    private var user : NewUser? = null
    private var index : Int = -1
    private var card : CardEntity? = null

    override fun onAttach(context: Context) {
        context.appComponent.injectCardFragment(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = this@CardFragmentNew.arguments?.getParcelable("user")
        index = this@CardFragmentNew.arguments?.getInt("cardIndex") ?: -1

        if(index == -1 ) {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.spinnerStatus.adapter = ArrayAdapter(requireContext(),R.layout.spinner,viewModel.listSpin)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.getFlow(index).collect {
                    card = it

                    if(card?.Status != null) {
                        binding.spinnerStatus.setDefaultStatus(viewModel.listSpin, card!!.Status!!)
                    }else {
                        binding.spinnerStatus.setDefaultStatus(viewModel.listSpin,"В эксплуатации")
                    }

                    binding.adress.text = card?.Adress

                    binding.description.text = card?.UEDescription

                    //getString(Resource str, arg)
                    binding.oldUser.text = "Старый пользователь : ${card?.UserName?.substringAfter("|")}"

                    binding.user.text = "Новый пользователь : ${user?.user?.userName}"

                    binding.invenNumb.text = "Инвентарный номер : ${card?.inventNumb}"

                    binding.serialNumberEdit.setText(card?.SerialNumb)

                    binding.note.setText(card?.Description)
                }
            }
        }

        binding.serialNimberEditLayout.setEndIconOnClickListener {
            alertWithScanner.show()
        }

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
        super.onStart()

        var itemSelected : String = ""

        binding.spinnerStatus.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                itemSelected = viewModel.listSpin[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.saveButton.setOnClickListener {
            checkCardAndUser()

            card?.let { it ->

                val newCard = viewModel.createNewCard(it,user, ElementsCard(
                    descriptionUE = it.UEDescription ?: "",
                    spinPos = itemSelected,
                    serialNumber = binding.serialNumberEdit.text.toString(),
                    description = binding.note.text.toString()
                ))

                viewModel.updateCard(newCard)
            }

            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.dependencyButton.setOnClickListener {
            checkCardAndUser()
            createAlertChildCardDialog().show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sound.release()
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
            //vm
            decoderFactory = viewModel.decoderFactory
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
                    binding.serialNumberEdit.setText(value)
                }
                dialog.dismiss()
            }
            .setNegativeButton("отмена") {dialog, which ->
                dialog.dismiss()
            }
            .create()
    }

    private fun createAlertChildCardDialog() : AlertDialog {

        var spinPos : String = ""

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.baby_card_layout,null)
        val description = dialogView.findViewById<TextInputEditText>(R.id.opi)
        val serialNumber = dialogView.findViewById<TextInputEditText>(R.id.ser)
        val note = dialogView.findViewById<TextInputEditText>(R.id.desc)
        val input = dialogView.findViewById<TextInputLayout>(R.id.testL)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinn)

        spinner.adapter = ArrayAdapter(requireContext(), R.layout.spinner, viewModel.listSpin)
        spinner.setSelection(viewModel.listSpin.indexOf("В эксплуатации"))

        input.setEndIconOnClickListener {
            alertWithScanner.show()
        }
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinPos = viewModel.listSpin[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("ok"){dialog, which ->
                val newCard = viewModel.createNewCard(card,user, ElementsCard(
                    descriptionUE = description.text.toString(),
                    spinPos = spinPos,
                    serialNumber = serialNumber.text.toString(),
                    description =  note.text.toString()
                ))
                viewModel.addToDbNewCard(newCard)
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