package ru.skillbranch.gameofthrones.data.remote.res

import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.repositories.urlToId


data class CharacterRes(
    val url: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String,
    val mother: String,
    val spouse: String,
    val allegiances: List<String> = listOf(),
    val books: List<String> = listOf(),
    val povBooks: List<Any> = listOf(),
    val tvSeries: List<String> = listOf(),
    val playedBy: List<String> = listOf()
)

fun CharacterRes.toCharacter(father : Character?, mother : Character?) = Character(
        id = url.urlToId().toString(),
name = name,
gender = gender,
culture = culture,
born = born,
died = died,
titles = titles,
aliases = aliases,
father = father?.id, //rel
mother = mother?.id, //rel
spouse = spouse,
houseId = null //rel
)



