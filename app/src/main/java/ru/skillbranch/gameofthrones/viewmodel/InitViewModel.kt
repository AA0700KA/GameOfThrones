package ru.skillbranch.gameofthrones.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.repositories.RootRepository
import java.net.ConnectException

class InitViewModel : ViewModel() {

    val TAG = "InitData"

    val loadDataState = MutableLiveData<LoadDataState>()
    val loadDbState = MutableLiveData<Boolean>()

    fun loadDataFromNetwork() {
            AppConfig.repo.getNeedHouses(
                AppConfig.NEED_HOUSES[0],
                AppConfig.NEED_HOUSES[1],
                AppConfig.NEED_HOUSES[2],
                AppConfig.NEED_HOUSES[3],
                AppConfig.NEED_HOUSES[4],
                AppConfig.NEED_HOUSES[5],
                AppConfig.NEED_HOUSES[6]) {

                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    Log.d(TAG, "Init data: ${it}")
                    loadDataState.value =  if (it.size > 0)  LoadDataState(true,  it)
                                       else LoadDataState(false)
                }

            }

    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            AppConfig.repo.isNeedUpdate {
                if (!it) {
                    CoroutineScope(Dispatchers.Main).launch {
                        loadDbState.value = it
                    }
                } else {
                      loadDataFromNetwork()
                }
            }
        }
    }

//    fun loadDataFromDb() {
//         viewModelScope.launch (Dispatchers.IO) {
//             val housesList = AppConfig.repo.db.getHouseDao().getHauses()
//             housesList.forEach {
//                 Log.d(TAG, "House $it")
//             }
//             val mainScope = CoroutineScope(Dispatchers.Main)
//             mainScope.launch {
//                 loadDataState.value = LoadDataState(false, housesList.size > 0, resultDb = housesList)
//             }
//         }
//    }

    fun insertIntoDb(data : List<HouseRes>, complete : () -> Unit) {
         viewModelScope.launch(Dispatchers.IO) {
             AppConfig.repo.insertHouses(data, complete)
         }
    }

}

data class LoadDataState(val isNetworkAviable : Boolean, val resultNetwork : List<HouseRes> = emptyList())