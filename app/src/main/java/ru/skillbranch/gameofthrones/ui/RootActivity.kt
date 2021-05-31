package ru.skillbranch.gameofthrones.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.character_screen.*
import kotlinx.android.synthetic.main.root_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.toRelativeCharacter
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.repositories.AppDatabase
import ru.skillbranch.gameofthrones.ui.adapters.CharactersAdapter
import ru.skillbranch.gameofthrones.ui.fragments.CharacterScreenFragment
import ru.skillbranch.gameofthrones.ui.fragments.HousesScreenFragment
import ru.skillbranch.gameofthrones.ui.fragments.SplashFragment
import ru.skillbranch.gameofthrones.ui.fragments.ViewPagerFragment
import ru.skillbranch.gameofthrones.viewmodel.InitViewModel

class RootActivity : AppCompatActivity(), CharactersAdapter.IShowInfo {

    val TAG = "RootActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.root_layout)
        supportFragmentManager.beginTransaction().addToBackStack(null).add(R.id.root_container, SplashFragment()).commit()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
           onBackPressed()
        }

        return true
    }

    override fun showInfo(character: CharacterItem, housePosition : Int) {
        Log.d(TAG, "showInfo: ${character}")
        supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.root_container, CharacterScreenFragment.newInstance(character.toRelativeCharacter(), housePosition)).commit()
    }

    override fun onBackPressed() {
        invalidateOptionsMenu()
        Log.d(TAG, "onBackPressed: ${supportFragmentManager.backStackEntryCount}")
        val countStack = supportFragmentManager.backStackEntryCount

        if (countStack <= 2) {
            Log.d(TAG, "onBackPressed: super")
            finish()
        } else {
            super.onBackPressed()
        }

    }

}