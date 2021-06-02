package ru.skillbranch.gameofthrones.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull

class CharacterViewModel : ViewModel() {

    val stateData : MutableLiveData<CharacterState> = MutableLiveData<CharacterState>()

    fun updateState(characterId : String, housePosition: Int) {

            AppConfig.repo.findCharacterFullById(characterId) {
                CoroutineScope(Dispatchers.Main).launch {
                    stateData.value = CharacterState(it, housePosition)
                }
            }

    }

}

data class CharacterState(val characterFull: CharacterFull, val housePosition : Int)