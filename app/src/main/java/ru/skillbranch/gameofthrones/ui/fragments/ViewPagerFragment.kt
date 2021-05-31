package ru.skillbranch.gameofthrones.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.view_pager_layout.*
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.ui.RootActivity
import ru.skillbranch.gameofthrones.ui.adapters.CharactersAdapter
import ru.skillbranch.gameofthrones.ui.adapters.ViewPagerAdapter
import ru.skillbranch.gameofthrones.viewmodel.CharactersViewModel
import ru.skillbranch.gameofthrones.viewmodel.ViewPagerViewModel

class ViewPagerFragment : Fragment() {

    private lateinit var viewModel: ViewPagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Pager", "Pager onCreate: ")
        viewModel = ViewModelProviders.of(this).get(ViewPagerViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_pager_layout, container, false)
        Log.d("Pager", "Pager onCreateView: ")
        return view
    }

    override fun onStart() {
        Log.d("Pager", "Pager onStart: ")
        super.onStart()
    }

    override fun onResume() {
        Log.d("Pager", "Pager onResume: ")
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("Pager", "Pager onActivityCreated: ")

        activity?.let {
            val activity = it as AppCompatActivity

            activity.setSupportActionBar(toolbar)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            val adapter = ViewPagerAdapter(activity)
            viewpager.adapter = adapter
           // tablayout.setupWithViewPager(viewpager)
            TabLayoutMediator(tablayout, viewpager) { tab, position ->
                  tab.text = AppConfig.NEED_HOUSES[position]
            }.attach()

            viewModel.positionState.observe(this, Observer {
                Log.d("Pager", "Position ${it} ")
                viewpager.setCurrentItem(it, true)
                tablayout.setBackgroundColor(activity.resources.getColor(AppConfig.COLORS[it]))
                toolbar.setBackgroundColor(activity.resources.getColor(AppConfig.COLORS[it]))
            })



            tablayout.setTabTextColors(activity.resources.getColor(R.color.white), activity.resources.getColor(R.color.stark_accent))

            viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    Log.d("Pager", "onPageSelected: ${position} ")
                    viewModel.updatePosition(position)
                }

            })

//            viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
//                override fun onPageScrollStateChanged(state: Int) {
//
//                }
//
//                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//
//                }
//
//                override fun onPageSelected(position: Int) {
//                    Log.d("Tab", "onPageSelected: ${position} ")
//                    viewModel.updatePosition(position)
//                }
//
//            })

        }

    }


    override fun onPause() {
        Log.d("Pager", "Pager onPause: ")
        super.onPause()
    }

    override fun onStop() {
        Log.d("Pager", "Pager onStop: ")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("Pager", "Pager onDestroyView: ")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("Pager", "Pager onDestroy: ")
        super.onDestroy()
    }

}