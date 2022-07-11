package de.hsfl.jkkab.fitnessappproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentMainMenuBinding
import de.hsfl.jkkab.fitnessappproject.models.MenuEntry
import de.hsfl.jkkab.fitnessappproject.views.MenuRecyclerViewAdapter
import java.util.ArrayList

class MainMenuFragment : Fragment(), MenuRecyclerViewAdapter.ItemClickListener {
    private var binding: FragmentMainMenuBinding? = null
    private var adapter: MenuRecyclerViewAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        val items: MutableList<MenuEntry> = ArrayList<MenuEntry>()
        items.add(
            MenuEntry(
                getString(R.string.bikeRun),
                R.drawable.baseline_directions_bike_24,
                R.id.action_navigation_main_menu_to_bikeRunPagerFragment
            )
        )
        items.add(
            MenuEntry(
                getString(R.string.windsurf),
                R.drawable.baseline_kitesurfing_24,
                R.id.action_navigation_main_menu_to_windsurfPagerFragment
            )
        )
        items.add(
            MenuEntry(
                getString(R.string.sup),
                R.drawable.baseline_surfing_24,
                R.id.action_navigation_main_menu_to_navigation_cardio
            )
        )
        items.add(
            MenuEntry(
                getString(R.string.sail),
                R.drawable.baseline_sailing_24,
                R.id.action_navigation_main_menu_to_navigation_cardio
            )
        )
        items.add(
            MenuEntry(
                getString(R.string.cardio),
                R.drawable.baseline_favorite_24,
                R.id.action_navigation_main_menu_to_navigation_cardio
            )
        )
        items.add(
            MenuEntry(
                getString(R.string.track),
                R.drawable.baseline_share_location_24,
                R.id.action_navigation_main_menu_to_navigation_track
            )
        )
        items.add(
            MenuEntry(
                getString(R.string.settings),
                R.drawable.baseline_settings_24,
                R.id.action_navigation_main_menu_to_navigation_settings
            )
        )
        val recyclerView: RecyclerView = binding!!.rvMenuList
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        adapter = context?.let { MenuRecyclerViewAdapter(it, items) }
        adapter?.setClickListener(this)
        recyclerView.setAdapter(adapter)
        return binding!!.getRoot()
    }

    override fun onItemClick(view: View?, position: Int) {
        if (view != null) {
            adapter?.getItem(position)
                ?.let { findNavController(view).navigate(it.navigationEndpoint) }
        }
    }
}