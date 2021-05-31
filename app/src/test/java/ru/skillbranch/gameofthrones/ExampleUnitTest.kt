package ru.skillbranch.gameofthrones

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test

import org.junit.Assert.*
import ru.skillbranch.gameofthrones.data.remote.res.getHouseId

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun exapleString() {
        val str = "Value"
        println(str.contains(""))
    }

    @Test
    fun testHouseId() {
        val str = "House Arryn of the Eyrie"
        val result = str.getHouseId()
        assertEquals("Arryn", result)
    }

    @Test
    fun joinToStringTest() {
        val list = listOf<String>("f", "Gosh", "Pasha", "Lexa")
        val str = list.joinToString(" ")
        val expectedValue = "f Gosh Pasha Lexa"
        assertEquals(expectedValue, str)
    }

    @Test
    fun dropBdTest() {
        val scopeIO = CoroutineScope(Dispatchers.IO)
        scopeIO.launch {
            AppConfig.repo.dropDb {
                println("Deleted")
            }
        }

    }

}