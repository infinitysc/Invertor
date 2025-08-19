package com.build.invertor.mainModule.listFragment.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.build.Invertor.R
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.build.invertor.model.modelOld.json.CardInventory

class Adapter(
    private var list : MutableList<CardInventory>,
    private val user : NewUser,
    private val onClickText : (CardInventory, NewUser) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val adress = itemView.findViewById<TextView>(R.id.choiceAdress)
        val description : TextView = itemView.findViewById(R.id.choiceDescription)
        val invent : TextView= itemView.findViewById(R.id.choiceInvent)
        val textContinut : TextView= itemView.findViewById(R.id.cont)
        val time : TextView = itemView.findViewById(R.id.time)
    }

    fun updateData(newData : List<CardInventory>) {
        val diffCallback = Diff(list,newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list.clear()
        list.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.time.text = "время инвентр. : ${list[position].ActionDateTime}"
        holder.adress.text = list[position].Adress

        holder.description.text = list[position].UEDescription

        holder.invent.text = list[position].inventNumb

        holder.textContinut.setOnClickListener(){
            onClickText(list[position],user)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_inside,parent,false)
        return ViewHolder(view)
    }


}