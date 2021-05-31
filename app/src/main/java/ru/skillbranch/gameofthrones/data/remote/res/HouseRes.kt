package ru.skillbranch.gameofthrones.data.remote.res

import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.Character

data class HouseRes(
    val url: String,
    val name: String,
    val region: String,
    val coatOfArms: String,
    val words: String,
    val titles: List<String> = listOf(),
    val seats: List<String> = listOf(),
    val currentLord: String,
    val heir: String,
    val overlord: String,
    val founded: String,
    val founder: String,
    val diedOut: String,
    val ancestralWeapons: List<String> = listOf(),
    val cadetBranches: List<Any> = listOf(),
    val swornMembers: List<String> = listOf()
)

fun String.getHouseId() : String {
    val array = split("of")
    val currentHouse = array[0]
    return currentHouse.split(" ")[1].trim()
}

fun HouseRes.toHouse(currentLord: Character?, heir: Character?, founder : Character?) = House(
         id = name.getHouseId(),
name = name,
region = region,
coatOfArms = coatOfArms,
words = words,
titles = titles,
seats = seats,
currentLord = currentLord?.id, //rel
heir = heir?.id, //rel
overlord = overlord,
founded = founded ,
 founder = founder?.id, //rel
diedOut = diedOut,
ancestralWeapons = ancestralWeapons
        )