package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*
import ru.skillbranch.gameofthrones.repositories.ListConverter

@Entity(tableName = "houses")
data class House(
        @PrimaryKey
    val id: String,
    val name: String,
    val region: String,
    val coatOfArms: String,
    val words: String,
        @field:TypeConverters(ListConverter::class)
    val titles: List<String>,
        @field:TypeConverters(ListConverter::class)
    val seats: List<String>,
    val currentLord: String? = null, //rel
    val heir: String? = null, //rel
    val overlord: String,
    val founded: String,
    val founder: String? = null, //rel
    val diedOut: String,
        @field:TypeConverters(ListConverter::class)
    val ancestralWeapons: List<String>
)

@Dao
interface HouseDao {

    @Insert
    fun insertAll(houses : List<House>)

    @Insert
    fun insert(house: House)

    @Query("SELECT * FROM houses")
    fun getHauses() : List<House>

    @Query("SELECT words FROM houses WHERE id = :id")
    fun getWords(id : String?) : String?

    @Query("DELETE FROM houses")
    fun deleteAll()

    @Query("UPDATE houses SET currentLord = :currentLord, heir = :heir, founder = :founder WHERE id = :id")
    fun updateCharacters(currentLord: String?, heir: String?, founder: String?, id: String)

}