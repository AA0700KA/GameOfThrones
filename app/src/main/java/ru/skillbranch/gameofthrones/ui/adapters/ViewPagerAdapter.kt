package ru.skillbranch.gameofthrones.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.ui.fragments.HousesScreenFragment

class ViewPagerAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return AppConfig.NEED_HOUSES.size
    }

    override fun createFragment(position: Int): Fragment {
        return HousesScreenFragment.getInstance(position)
    }

}