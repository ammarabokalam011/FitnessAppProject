package de.hsfl.jkkab.fitnessappproject.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.Navigation.findNavController
import androidx.viewpager.widget.ViewPager
import de.hsfl.jkkab.fitnessappproject.MainActivity
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentPagerBinding
import de.hsfl.jkkab.fitnessappproject.repositories.Repository

class WindsurfPagerFragment : Fragment(), PagerFragment {
    private var mPager: ViewPager? = null
    private var pagerAdapter: PagerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPagerBinding = FragmentPagerBinding.inflate(inflater, container, false)
        mPager = binding.pager
        pagerAdapter = PagerAdapter(
            childFragmentManager
        )
        mPager!!.setAdapter(pagerAdapter)
        return binding.getRoot()
    }

    override fun onBackPressed() {
        val repository: Repository? = (activity as MainActivity?)?.repository
        if (mPager!!.getCurrentItem() > 0) {
            mPager!!.setCurrentItem(mPager!!.getCurrentItem() - 1)
        } else if (repository != null) {
            if (repository.stopwatchRepository?.isStopwatchRunning?.value == true) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.cancel_training)).setPositiveButton(
                    getString(R.string.yes),
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
                        repository.stopwatchRepository!!.stopStopwatch()
                        view?.let { findNavController(it).navigateUp() }
                    })
                    .setNegativeButton(
                        getString(R.string.no),
                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() })
                    .show()
            } else {
                view?.let { findNavController(it).navigateUp() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private inner class PagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    Log.d("WindsurfPagerFragment", "creating windsurf")
                    WindsurfFragment.newInstance(true)
                }
                1 -> {
                    Log.d("WindsurfPagerFragment", "creating track")
                    TrackFragment.newInstance()
                }
                else -> {
                    Log.d("WindsurfPagerFragment", "unknown position")
                    TrackFragment.newInstance()
                }
            }
        }

        override fun getCount(): Int {
            return 2;
        }
    }
}