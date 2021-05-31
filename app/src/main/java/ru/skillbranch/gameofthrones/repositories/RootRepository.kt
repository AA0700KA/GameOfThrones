package ru.skillbranch.gameofthrones.repositories

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.*
import ru.skillbranch.gameofthrones.data.remote.res.*
import ru.skillbranch.gameofthrones.viewmodel.CharactersViewModel
import java.net.ConnectException
import java.util.concurrent.CountDownLatch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object RootRepository {

    val db = Room.databaseBuilder(App.applicationContext(),
            AppDatabase::class.java,
            "game-of-thrones").build()

    val api = NetworkService.getApi()

    val TAG = "RootRepository"



    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result : (houses : List<HouseRes>) -> Unit) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val range = 1..9
                val housesResult = mutableListOf<HouseRes>()

                for (number in range) {
                    val houses = api.getHouses(page = number).await()
                    housesResult.addAll(houses)
                }

                result(housesResult)
            } catch (e : Exception) {
                result(emptyList())
            }
        }

    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети 
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result : (houses : List<HouseRes>) -> Unit) {

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val range = 1..9
                val housesResult = mutableListOf<HouseRes>()

                for (number in range) {
                    val houses = api.getHouses(page = number).await()
                    val resultData = houses.filter { house -> houseNames.contains(house.name) }
                    housesResult.addAll(resultData)

                    if (housesResult.size == houseNames.size) {
                        break
                    }
                }

                result(housesResult)
            } catch (e : Exception) {
                result(emptyList())
            }
        }

    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(vararg houseNames: String, result : (houses : List<Pair<HouseRes, List<CharacterRes>>>) -> Unit) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val range = 1..9
            val housesResult = mutableListOf<HouseRes>()

            for (number in range) {
                val houses = api.getHouses(page = number).await()
                val resultData = houses.filter { house -> houseNames.contains(house.name) }
                housesResult.addAll(resultData)

                if (housesResult.size == houseNames.size) break
            }

            val pairList = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
            Log.d(TAG, "onResponse: houses count ${housesResult.size}")

            for (house in housesResult) {
                Log.d(TAG, "onResponse: child count ${house.swornMembers.size}")
                val characterList = mutableListOf<CharacterRes>()

                for (url in house.swornMembers) {
                    val id = url.urlToId()
                    val character =  api.getCharacter(id!!).await()
                    characterList.add(character)
                }

                pairList.add(Pair(house, characterList))
            }

            result(pairList)
        }

    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun insertHouses(houses : List<HouseRes>, complete: () -> Unit) {

        val houseDao = db.getHouseDao()
        val charactersDao = db.getCharactersDao()

        for (houseRes in houses) {
            val currentLord = getAndInsertCharacter(houseRes.currentLord.urlToId())
            val heir = getAndInsertCharacter(houseRes.heir.urlToId())
            val founder = getAndInsertCharacter(houseRes.founder.urlToId())
            val house = houseRes.toHouse(currentLord, heir, founder)
            houseDao.insert(house)

            currentLord?.let {
                charactersDao.updateHouseId(house.id, it)
            }

            heir?.let {
                charactersDao.updateHouseId(house.id, it)
            }

            founder?.let {
                charactersDao.updateHouseId(house.id, it)
            }

        }

        complete()

    }

    private suspend fun getAndInsertCharacter(characterId : Int?) : Character? {
        characterId?.let {
             val characterRes = api.getCharacter(it).await()
             val father = getAndInsertCharacter(characterRes.father.urlToId())
             val mother = getAndInsertCharacter(characterRes.mother.urlToId())
             val result = characterRes.toCharacter(father, mother)

             db.getCharactersDao().addCharacter(result)
             return result
        }

        return null

    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun insertCharacters(characters : List<CharacterRes>, complete: () -> Unit) {
        for (characterRes in characters) {
            getAndInsertCharacter(characterRes.url.urlToId())
        }

        complete()
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun dropDb(complete: () -> Unit) {
        val houseDao = db.getHouseDao()
        val charactersDao = db.getCharactersDao()

        houseDao.deleteAll()
        charactersDao.deleteAll()
        val loaders = AppConfig.NEED_LOADERS

        for (loader in loaders) {
            loader.isLoadingCharacters = false
        }

        complete()
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun findCharactersByHouseName(name : String, result: (characters : List<CharacterItem>) -> Unit) {
        val charactersDao = db.getCharactersDao()
        val resultData = charactersDao.getCharactersItemByHouse(name.getHouseId())
        result(resultData)
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun findCharacterFullById(id : String, result: (character : CharacterFull) -> Unit) {
        val characterDao = db.getCharactersDao()
        val houseDao = db.getHouseDao()
        val character = characterDao.getCharacter(id)

        character?.let {
            val father = characterDao.getRelativeCharacter(it.father)
            val mother = characterDao.getRelativeCharacter(it.mother)
            val words = houseDao.getWords(it.houseId)
            val characterFull = it.toCharacterFull(words, father, mother)
            result(characterFull)
        }
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    suspend fun isNeedUpdate(result: (isNeed : Boolean) -> Unit){
        val houseDao = db.getHouseDao()
        val houses = houseDao.getHauses()
        result(houses.size == 0)

    }



}

fun String.urlToId() : Int? {
    if (!isEmpty()) {
        val array = this.split("/")
        return array[array.size - 1].toInt()
    } else return null
}



interface Api {

    @GET("houses")
    fun getHouses(@Query("pageSize")pageSize : Int = 50,
                  @Query("page") page : Int) : Deferred<List<HouseRes>>

    @GET("characters/{id}")
    fun getCharacter(@Path("id") id : Int) : Deferred<CharacterRes>

}


object NetworkService {

    private var retrofit: Retrofit

    init {
        retrofit =  Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
    }

    fun getApi() : Api {
        return retrofit.create(Api::class.java)
    }

}

@Database(entities = [House::class, Character::class, CharacterItem::class, RelativeCharacter::class],
        version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getHouseDao() : HouseDao

    abstract fun getCharactersDao() : CharactersDao

}


class ListConverter {

    @TypeConverter
    fun toString(list : List<String>) : String {
        return list.joinToString(" ")
    }

    @TypeConverter
    fun toList(str : String) : List<String> {
        return str.split(" ")
    }

}

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "settings")

class HouseLoadPrefManager(houseName : String, context: Context = App.applicationContext()) {

    val TAG = "PrefManager"
    val dataStore = context.dataStore

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "err ${th.message}")
    }

    internal val scope = CoroutineScope(SupervisorJob() + errorHandler)

    var isLoadingCharacters by PrefDelegate(false, houseName)

}

class PrefDelegate<T>(private val defaultValue : T, private val customKey : String? = null) {

    operator fun provideDelegate(thisRef : HouseLoadPrefManager, prop : KProperty<*>) : ReadWriteProperty<HouseLoadPrefManager, T> {
        val key = createKey(customKey ?: prop.name, defaultValue)

        return object : ReadWriteProperty<HouseLoadPrefManager, T> {

            private var _storedValue : T? = null

            override fun setValue(thisRef: HouseLoadPrefManager, property: KProperty<*>, value: T) {
                _storedValue = value

                thisRef.scope.launch {
                    thisRef.dataStore.edit { pref ->
                        pref[key] = value
                    }
                }
            }

            override fun getValue(thisRef: HouseLoadPrefManager, property: KProperty<*>): T {
                if (_storedValue == null) {
                    val flowValue = thisRef.dataStore.data
                        .map { pref ->
                            pref[key] ?: defaultValue
                        }

                    _storedValue = runBlocking(Dispatchers.IO) { flowValue.first() }
                }

                return _storedValue!!
            }

        }

    }

    @Suppress("UNCHECKED_CAST")
    fun createKey(name : String, value : T) : Preferences.Key<T> =
        when (value) {
            is Int -> intPreferencesKey(name)
            is Long -> longPreferencesKey(name)
            is Boolean -> booleanPreferencesKey(name)
            is Double -> doublePreferencesKey(name)
            is Float -> floatPreferencesKey(name)
            is String -> stringPreferencesKey(name)
            else -> error("this type can't be stored into Preferences")
        }.run { this as Preferences.Key<T> }

}