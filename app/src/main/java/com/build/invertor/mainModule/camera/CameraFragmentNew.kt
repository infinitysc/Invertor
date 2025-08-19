package com.build.invertor.mainModule.camera

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.build.Invertor.R
import com.build.invertor.mainModule.Card.CardFragment
import com.build.invertor.mainModule.Card.CardFragmentNew
import com.build.invertor.mainModule.listFragment.ListChoiceFragment
import com.build.invertor.mainModule.start.StartFragmentNew
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.build.invertor.model.modelOld.json.CardInventory
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class CameraFragmentNew : Fragment(){

    private lateinit var buttonToNextFragment : Button
    private lateinit var valueText : TextView
    private lateinit var userText : TextView
    private lateinit var barcodeView : BarcodeView
    private lateinit var switch : SwitchCompat
    private lateinit var refresh : ImageView
    private lateinit var controller: CameraController

    private val sound : MediaPlayer by lazy { MediaPlayer.create(requireContext(), R.raw.scanner_beep) }
    private var valueString : String = "defaultStringValue"
    private var user : NewUser? = null
    private val formats = listOf(
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_93,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
    )


    private val callback = BarcodeCallback { result ->
        if(result != null){
            sound.start()
            valueText.text = result.text
            valueString = result.text
            barcodeView.pause()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("camera","$this create")
        return inflater.inflate(R.layout.new_camera_fragment_layout,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments != null){
            user = arguments?.getParcelable("user")
        }

        buttonToNextFragment = view.findViewById(R.id.button_to_next_fragment)
        valueText = view.findViewById(R.id.value)
        userText = view.findViewById(R.id.userValue)
        barcodeView = view.findViewById(R.id.barcode_view)
        switch = view.findViewById(R.id.switch_torch)
        refresh = view.findViewById(R.id.refreshButton)
        userText.text = user!!.user?.userName
        barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.decodeContinuous(callback)
    }

    override fun onStart() {
        super.onStart()
        controller = CameraController(requireContext().filesDir,requireContext().cacheDir)
        valueText.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Редактирование текста")

            val array : ArrayAdapter<String?> = ArrayAdapter(requireContext(),R.layout.spinner,controller.getJsonFile()!!.getListCode())
            val autocom = AutoCompleteTextView(requireContext())
            autocom.setAdapter(array)

            val editText = EditText(requireContext())
            editText.setText(valueText.text)

            alert.setView(autocom)

            alert.setPositiveButton("OK") { dialog, which ->
                valueText.setText(autocom.text.toString())
                valueString = autocom.text.toString()
                dialog.dismiss()
            }
            alert.setNegativeButton("Отмена") { dialog,which->
                dialog.dismiss()
            }
            val dialog = alert.create()
            dialog.show()
        }
        barcodeView.resume()
        refresh.setOnClickListener{
            barcodeView.pause()
            barcodeView.resume()
            valueText.text = ""
        }
        switch.setOnCheckedChangeListener{_ , isCheked ->
            Log.d("Scanner","фонарик $this")
            if(isCheked){
                barcodeView.setTorch(true)
            }
            else {
                barcodeView.setTorch(false)
            }

        }
        buttonToNextFragment.setOnClickListener{
            val card = controller.searchDataByNumber(valueString)
            when(controller.checkCard(card)){
                StateCard.EMPTY -> useToast("Сканируйте или введите значение")
                StateCard.ONE_ELEMENT -> startCardFragment(card)
                StateCard.MULTIPLY_ELEMENTS -> startListFragment(card)
            }
        }
    }

    private fun startCardFragment(card : List<CardInventory>) {
        val bundle = createBundle(this.user!!,card[0])
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, CardFragmentNew.newInstance(bundle))
            .addToBackStack("papa")
            .commit()
    }

    private fun startListFragment(card : List<CardInventory>) {
        controller.cacheSaver(card, user?.user?.userName ?: "USER IS NULLABLE".apply {
            useToast("ПОЛЬЗОВАТЕЛЬ ПУСТОЙ !!")
        })
        val bundle = Bundle()
        bundle.putString("json","${user?.user?.userName}.json")
        useToast("Обьектов найдено ${card.size}")
        runBlocking {
            delay(1000)
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, ListChoiceFragment.newInstance(
                card,
                this.user!!,
                bundle
            ))
            .addToBackStack("camera")
            .commit()

    }

    private fun createBundle(user : NewUser,card : CardInventory) : Bundle {
        return Bundle().apply {
            putParcelable("user",user)
            putParcelable("card",card)
        }
    }

    private fun useToast(text : String) {
        Toast.makeText(requireContext(),text, Toast.LENGTH_SHORT).show()
    }
    companion object{
        fun newInstance(bundle : Bundle) : CameraFragmentNew {
            val fragment = CameraFragmentNew()
            fragment.arguments = bundle
            return  fragment
        }
    }
}