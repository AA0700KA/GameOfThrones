package ru.skillbranch.gameofthrones.ui.fragments

import android.app.Activity
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.root_layout.*
import kotlinx.android.synthetic.main.splash_layout.*
import kotlinx.android.synthetic.main.splash_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.viewmodel.InitViewModel
import ru.skillbranch.gameofthrones.viewmodel.LoadDataState

class SplashFragment : Fragment() {

    private lateinit var viewModel: InitViewModel
    val TAG = "SplashFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(InitViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.splash_layout, container, false)
        val animationDrawable = view.splash_layout.drawable as AnimationDrawable
        animationDrawable.start()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadDbState.observe(this, Observer {
            if (!it) {
                nextScreen()
            }
        })
        viewModel.loadDataState.observe(this, Observer {
            renderNetworkData(it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    private fun nextScreen() {
        activity?.let {
            val activity = it as AppCompatActivity
            activity.supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.root_container, ViewPagerFragment()).commit()
        }

    }

    private fun renderNetworkData(state : LoadDataState) {

            if (state.isNetworkAviable) {
                viewModel.insertIntoDb(state.resultNetwork) {
                    val appScope = CoroutineScope(Dispatchers.Main)
                    appScope.launch {
                        nextScreen()
                    }
                }

            } else {
                Snackbar.make(splash_layout,
                        "Интернет недоступен - приложение не может быть запущенно. Подключитесь к интернету и перезапустите приложение",
                        Snackbar.LENGTH_INDEFINITE)
                        .show()
                Log.d(TAG, "Интернет недоступен - приложение не может быть запущенно. Подключитесь к интернету и перезапустите приложение")
            }
    }

}