package ru.skillbranch.gameofthrones

import ru.skillbranch.gameofthrones.repositories.HouseLoadPrefManager
import ru.skillbranch.gameofthrones.repositories.RootRepository

object AppConfig {

    val NEED_HOUSES = arrayOf(
            "House Stark of Winterfell",
            "House Lannister of Casterly Rock",
            "House Targaryen of King's Landing",
            "House Greyjoy of Pyke",
            "House Tyrell of Highgarden",
            "House Baratheon of Dragonstone",
            "House Nymeros Martell of Sunspear"
    )

    val NEED_LOADERS = arrayOf(
        HouseLoadPrefManager("House Stark of Winterfell"),
        HouseLoadPrefManager("House Lannister of Casterly Rock"),
        HouseLoadPrefManager( "House Targaryen of King's Landing"),
        HouseLoadPrefManager("House Greyjoy of Pyke"),
        HouseLoadPrefManager("House Tyrell of Highgarden"),
        HouseLoadPrefManager("House Baratheon of Dragonstone"),
        HouseLoadPrefManager("House Nymeros Martell of Sunspear")
    )

    val ICONS = arrayOf(
            R.drawable.stark_icon,
            R.drawable.lanister_icon,
            R.drawable.targaryen_icon,
            R.drawable.greyjoy_icon,
            R.drawable.tyrel_icon,
            R.drawable.baratheon_icon,
            R.drawable.martel_icon
    )

    val SPLASHES = arrayOf(
            R.drawable.stark_splash,
            R.drawable.lanister_splash,
            R.drawable.targaryen_splash,
            R.drawable.greyjoy_splash,
            R.drawable.tyrell_splash,
            R.drawable.baratheon_splash,
            R.drawable.martell_splash
    )

    val COLORS = arrayOf(
            R.color.stark_primary,
            R.color.lannister_primary,
            R.color.targaryen_primary,
            R.color.greyjoy_primary,
            R.color.tyrel_primary,
            R.color.baratheon_primary,
            R.color.martel_primary
    )

    const val BASE_URL = "https://www.anapioficeandfire.com/api/"

    val repo = RootRepository


}