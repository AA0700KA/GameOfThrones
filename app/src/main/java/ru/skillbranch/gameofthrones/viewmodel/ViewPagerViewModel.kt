package ru.skillbranch.gameofthrones.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.FieldPosition

class ViewPagerViewModel : ViewModel() {

    val positionState = MutableLiveData<Int>().apply {
        value = 0
    }

    fun updatePosition(position: Int) {
        positionState.value = position
    }

}