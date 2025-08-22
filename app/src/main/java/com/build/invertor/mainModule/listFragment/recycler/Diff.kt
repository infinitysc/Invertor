package com.build.invertor.mainModule.listFragment.recycler

import androidx.recyclerview.widget.DiffUtil
import com.build.invertor.model.modelOld.json.json.CardInventory

class Diff (
    private val oldList : List<CardInventory>,
    private val newList : List<CardInventory>
    ) : DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].UEID == newList[newItemPosition].UEID
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}