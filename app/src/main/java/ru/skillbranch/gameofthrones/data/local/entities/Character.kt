package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*
import ru.skillbranch.gameofthrones.repositories.ListConverter


@Entity(tableName = "characters",
        foreignKeys = [ForeignKey(
                onDelete = ForeignKey.SET_NULL,
                entity = House::class,
                parentColumns = ["id"],
                childColumns = ["houseId"]),
            ForeignKey(
                    onDelete = ForeignKey.SET_NULL,
                    entity = RelativeCharacter::class,
                    parentColumns = ["id"],
                    childColumns = ["father"]
            ),
            ForeignKey(
                    onDelete = ForeignKey.SET_NULL,
                    entity = RelativeCharacter::class,
                    parentColumns = ["id"],
                    childColumns = ["mother"]
            )
        ]
)
data class Character(
        @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
        @field:TypeConverters(ListConverter::class)
    val titles: List<String> = listOf(),
        @field:TypeConverters(ListConverter::class)
    val aliases: List<String> = listOf(),
    val father: String? = null, //rel
    val mother: String? = null, //rel
    val spouse: String,
    val houseId: String? = null //rel
)

@Entity(tableName = "characters_item",
        foreignKeys = [ForeignKey(
                onDelete = ForeignKey.SET_NULL,
                entity = House::class,
                parentColumns = ["id"],
                childColumns = ["house"])]
)
data class CharacterItem(
        @PrimaryKey
    val id: String,
    val house: String? = null, //rel
    val name: String,
        @field:TypeConverters(ListConverter::class)
    val titles: List<String>,
        @field:TypeConverters(ListConverter::class)
    val aliases: List<String>
) {

    val simpleTitles : String
        get() = if (titles.size > 0 && !titles[0].isEmpty())
            titles.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(",", "•")
        else "Information unknown"

}


data class CharacterFull(
    val id: String,
    val name: String,
    val words: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    val house:String, //rel
    val father: RelativeCharacter?,
    val mother: RelativeCharacter?
)

@Entity(tableName = "relative_characters",
        foreignKeys = [ForeignKey(
                onDelete = ForeignKey.SET_NULL,
                entity = House::class,
                parentColumns = ["id"],
                childColumns = ["house"])])
data class RelativeCharacter(
        @PrimaryKey
    val id: String,
    val name: String,
    val house:String? = null //rel
)

fun Character.toCharacterItem() = CharacterItem(
        id = this.id,
        name = this.name,
        titles = this.titles,
        aliases = this.aliases,
        house = this.houseId
)

fun CharacterItem.toRelativeCharacter() = RelativeCharacter(
        id = id,
        name = name,
        house = house
)

fun Character.toRelativeCharacter() = RelativeCharacter(
        id = this.id,
        name = this.name,
        house = this.houseId
)

fun Character.toCharacterFull(words: String?, father: RelativeCharacter?, mother: RelativeCharacter?) = CharacterFull(
        id = id,
        name = name,
        words = words ?: "",
        born = born,
        died = died,
        titles = titles,
        aliases = aliases,
        father = father?:null,
        house = houseId ?: "", //rel
        mother = mother?: null
)

fun List<Character>.toCharacterItems() = map { it.toCharacterItem() }

fun List<Character>.toRelativeCharacters() = map { it.toRelativeCharacter() }

@Dao
abstract class CharactersDao {

    @Transaction
    open fun addAll(characters: List<Character>) {
        insertRelativeCharacters(characters.toRelativeCharacters())
        insertCharactersItem(characters.toCharacterItems())
        insertCharacters(characters)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacters(characters : List<Character>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharactersItem(charactersItems : List<CharacterItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRelativeCharacters(relativeCharacters : List<RelativeCharacter>)

    @Transaction
    open fun addCharacter(character : Character) {
        insertRelativeCharacter(character.toRelativeCharacter())
        insertCharacterItem(character.toCharacterItem())
        insertCharacter(character)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacter(character : Character)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterItem(charactersItem : CharacterItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRelativeCharacter(relativeCharacter : RelativeCharacter)

    @Transaction
    open fun updateHouseId(houseId: String, character: Character) {
        updateCharacterRelativeHouseId(houseId, character.id)
        updateCharacterItemHouseId(houseId, character.id)
        updateCharacterHouseId(houseId, character.id)
    }

    @Query("UPDATE characters SET houseId = :houseId WHERE id = :id")
    abstract fun updateCharacterHouseId(houseId: String, id : String)

    @Query("UPDATE characters_item SET house = :houseId WHERE id = :id")
    abstract fun updateCharacterItemHouseId(houseId: String, id : String)

    @Query("UPDATE relative_characters SET house = :houseId WHERE id = :id")
    abstract fun updateCharacterRelativeHouseId(houseId: String, id : String)

    @Transaction
    open fun updateHouseIdIfNull(houseId: String) {
        updateCharacterReleativeHouseIdIfNull(houseId)
        updateCharacterItemHouseIdIfNull(houseId)
        updateCharacterHouseIdIfNull(houseId)
    }

    @Query("UPDATE characters SET houseId = :houseId WHERE houseId is null")
    abstract fun updateCharacterHouseIdIfNull(houseId: String)

    @Query("UPDATE characters_item SET house = :houseId WHERE house is null")
    abstract fun updateCharacterItemHouseIdIfNull(houseId: String)

    @Query("UPDATE relative_characters SET house = :houseId WHERE house is null")
    abstract fun updateCharacterReleativeHouseIdIfNull(houseId: String)

    @Query("SELECT * FROM characters_item WHERE house = :houseId")
    abstract fun getCharactersItemByHouse(houseId: String) : List<CharacterItem>

    @Query("SELECT * FROM characters WHERE id = :id")
    abstract fun getCharacter(id : String) : Character?

    @Query("SELECT * FROM relative_characters WHERE id = :id")
    abstract fun getRelativeCharacter(id: String?) : RelativeCharacter?

    @Query("DELETE FROM relative_characters")
    abstract fun deleteAllRelativeCharacters()

    @Query("DELETE FROM characters_item")
    abstract fun deleteAllCharactersItems()

    @Query("DELETE FROM characters")
    abstract fun deleteAllCharacters()

    @Transaction
    open fun deleteAll() {
        deleteAllRelativeCharacters()
        deleteAllCharactersItems()
        deleteAllCharacters()
    }


}