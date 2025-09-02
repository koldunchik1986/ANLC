package com.abclient.activities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FormMainPagerAdapter(activity: FragmentActivity, private val tabTitles: List<String>) : FragmentStateAdapter(activity) {
	override fun getItemCount(): Int = tabTitles.size

	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> GameFragment()
			1 -> ChatFragment()
			2 -> InventoryFragment()
			3 -> ContactsFragment()
			else -> Fragment()
		}
	}
}
