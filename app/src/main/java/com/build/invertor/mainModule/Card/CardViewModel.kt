package com.build.invertor.mainModule.Card

import androidx.lifecycle.ViewModel
import com.build.invertor.model.database.Repository
import javax.inject.Inject

class CardViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val listSpin = listOf("","В эксплуатации","Требуется ремонт","Находится на консервации",
        "Не соответствует требованиям эксплуатации","Не введен в эксплуатацию","Списание","Утилизация")


}