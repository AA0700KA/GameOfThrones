package ru.skillbranch.gameofthrones.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.data.remote.res.getHouseId
import ru.skillbranch.gameofthrones.repositories.NetworkService

class CharactersViewModel : ViewModel() {

    companion object {
        val TAG = "CharactersViewModel"
    }

    val charactersListState : MutableLiveData<CharactersHousesState> = MutableLiveData<CharactersHousesState>()

    val currentState : CharactersHousesState?
                get() = charactersListState.value

    fun loadCharactersByPosition(position : Int) {
        if (AppConfig.NEED_LOADERS[position].isLoadingCharacters) {
            loadCharacterFromDb(position)
        } else {
            loadCharactersFromNetwork(position)
        }
    }

    private fun loadCharacterFromDb(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            AppConfig.repo.findCharactersByHouseName(AppConfig.NEED_HOUSES[position]) {
                Log.d(TAG, "CharactersViewModel: All data from db $it")
                val mainScope = CoroutineScope(Dispatchers.Main)
                mainScope.launch {
                    charactersListState.value = CharactersHousesState(false, position = position, data = it)
                }
            }
        }
    }

    private fun loadCharactersFromNetwork(position : Int) {
        AppConfig.repo.getNeedHouseWithCharacters(AppConfig.NEED_HOUSES[position], result = {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val result = it.map { it.second }
                Log.d(TAG, "CharactersViewModel: ${result.first()} from $it")
                AppConfig.repo.insertCharacters(result.first()) {
                    AppConfig.repo.db.getCharactersDao().updateHouseIdIfNull(AppConfig.NEED_HOUSES[position].getHouseId())

                    CoroutineScope(Dispatchers.Main).launch {
                        AppConfig.NEED_LOADERS[position].isLoadingCharacters = true
                        charactersListState.value = CharactersHousesState(true, position = position)
                    }
                }
            }

        })
    }

    fun handleSearch(query : String?) {
        query?.let { search ->
            var result = currentState?.data
            if (!query.isEmpty())
                 result = currentState?.data?.filter { it.name.contains(search) || it.simpleTitles.contains(search)  }
            charactersListState.value = currentState?.copy(searchQuery = search, searchResults = result!!)
        }

    }

    fun handleSearchMode(searchMode : Boolean) {
        charactersListState.value = currentState?.copy(isSearchMode = searchMode)
    }


}

data class CharactersHousesState(
        val isLoadedFromNetwork : Boolean = false,
        val isSearchMode : Boolean = false,
        val position : Int = 0,
        val searchQuery : String = "",
        val data : List<CharacterItem> = emptyList(),
        val searchResults : List<CharacterItem> = emptyList())