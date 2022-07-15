package de.hsfl.jkkab.fitnessappproject.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.Navigation
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import de.hsfl.jkkab.fitnessappproject.MainActivity
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentPagerBinding
import de.hsfl.jkkab.fitnessappproject.repositories.Repository

/**
 * A simple [Fragment] subclass.
 * Use the [JoggingPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JoggingPagerFragment : Fragment(), PagerFragment {
    private var mPager: ViewPager? = null
    private var pagerAdapter: JoggingPagerFragment.PagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPagerBinding = FragmentPagerBinding.inflate(inflater, container, false)
        mPager = binding.pager
        pagerAdapter = PagerAdapter(
            childFragmentManager
        )
        mPager!!.adapter = pagerAdapter
        return binding.getRoot()
    }

    override fun onBackPressed() {
        val repository: Repository? = (activity as MainActivity?)?.repository
        if (mPager?.getCurrentItem()!! > 0) {
            mPager?.setCurrentItem(mPager!!.getCurrentItem() - 1)
        } else if (repository != null) {
            if (repository.stopwatchRepository?.isStopwatchRunning?.value == true) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.cancel_training)).setPositiveButton(
                    getString(R.string.yes)
                ) { _: DialogInterface?, i: Int ->
                    repository.stopwatchRepository!!.stopStopwatch()
                    view?.let { Navigation.findNavController(it).navigateUp() }
                }
                    .setNegativeButton(
                        getString(R.string.no)
                    ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                    .show()
            } else {
                view?.let { Navigation.findNavController(it).navigateUp() }
            }
        }
    }

    private inner class PagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> {
                    Log.d("JoggingPagerFragment", "creating bikerun")
                    JoggingFragment.newInstance(true)
                }
                1 -> {
                    Log.d("JoggingPagerFragment", "creating track")
                    TrackFragment.newInstance()
                }
                2 -> {
                    Log.d("JoggingPagerFragment", "creating cardio")
                    CardioFragment.newInstance()
                }
                else -> {
                    Log.d("BikeRunPagerFragment", "unknown position")
                    CardioFragment.newInstance()
                }
            }
        }

        override fun getCount(): Int {
            return 3;
        }
    }


}