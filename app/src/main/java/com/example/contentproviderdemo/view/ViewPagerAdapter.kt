package com.example.contentproviderdemo.view


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.contentproviderdemo.util.Contact

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, var list : List<Contact>) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
          return  list.size
    }
    override fun createFragment(position: Int): Fragment {
        return ShowContactFragment(list.get(position))
    }
}