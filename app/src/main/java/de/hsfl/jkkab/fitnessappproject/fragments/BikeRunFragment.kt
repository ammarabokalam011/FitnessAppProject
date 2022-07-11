package de.hsfl.jkkab.fitnessappproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.hsfl.jkkab.fitnessappproject.MainActivity
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.models.CaloriesCalculator
import de.hsfl.jkkab.fitnessappproject.repositories.Repository
import de.hsfl.jkkab.fitnessappproject.viewmodels.BikeRunViewModel
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentBikerunBinding
import java.text.DecimalFormat

class BikeRunFragment private constructor() : Fragment(), View.OnClickListener {
    private var binding: FragmentBikerunBinding? = null
    private var bikeRunViewModel: BikeRunViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository: Repository? = (activity as MainActivity?)?.repository
        val isActivity = requireArguments().getBoolean(IS_ACTIVITY)
        if (isActivity) {
            if (repository != null) {
                repository.caloriesRepository
                    ?.changeSportActivity(CaloriesCalculator.SportActivity.BIKING)
            }
        }
        bikeRunViewModel = ViewModelProvider(this).get(BikeRunViewModel::class.java)
        if (repository != null) {
            bikeRunViewModel!!.setRepository(repository)
        }
        binding = FragmentBikerunBinding.inflate(inflater, container, false)
        bikeRunViewModel?.speed?.observe(viewLifecycleOwner, speedObserver)
        bikeRunViewModel?.averageSpeed?.observe(viewLifecycleOwner, speedAverageObserver)
        bikeRunViewModel?.distance?.observe(viewLifecycleOwner, distanceObserver)
        bikeRunViewModel?.getCalories()?.observe(viewLifecycleOwner, caloriesObserver)
        bikeRunViewModel?.heartRate?.observe(viewLifecycleOwner, heartRateObserver)
        bikeRunViewModel?.averageHeartRate?.observe(viewLifecycleOwner, heartRateAverageObserver)
        bikeRunViewModel?.time?.observe(viewLifecycleOwner, timeObserver)
        bikeRunViewModel?.elapsedTime?.observe(viewLifecycleOwner, timeElapsedObserver)
        bikeRunViewModel?.isStopwatchStarted?.observe(viewLifecycleOwner, startStopTextObserver)
        bikeRunViewModel?.isStopwatchRunning?.observe(viewLifecycleOwner, runningTextObserver)
        binding!!.bPause.setOnClickListener(this)
        binding!!.bStartStop.setOnClickListener(this)
        return binding!!.getRoot()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val speedObserver: Observer<Float> =
        Observer { speed -> binding!!.tvSpeed.setText(df1.format(speed)) }
    private val speedAverageObserver: Observer<Double> = Observer { average ->
        binding!!.tvAverageSpeed.setText(
            df1.format(average)
        )
    }
    private val distanceObserver =
        Observer<Float> { distance -> binding!!.tvDistance.setText(df1.format(distance / 1000.0)) }
    private val caloriesObserver: Observer<Double> = Observer { calories ->
        binding!!.tvCalories.setText(
            df1.format(calories)
        )
    }
    private val heartRateObserver =
        Observer<Int> { heartRate -> binding!!.tvHeartRate.setText(heartRate.toString()) }
    private val heartRateAverageObserver: Observer<Double> = Observer { heartRateAverage ->
        binding!!.tvAverageHeartRate.setText(
            df1.format(heartRateAverage)
        )
    }
    private val timeObserver: Observer<String> = Observer { time -> binding!!.tvClock.setText(time) }
    private val timeElapsedObserver: Observer<String> =
        Observer { time -> binding!!.tvElapsedTime.setText(time) }
    private val startStopTextObserver = Observer<Boolean> { isStarted ->
        binding!!.bStartStop.setText(
            if (isStarted) getString(R.string.stop) else getString(R.string.start)
        )
    }
    private val runningTextObserver = Observer<Boolean> { isRunning ->
        binding!!.bPause.setText(
            if (isRunning) getString(R.string.pause) else getString(R.string.resume)
        )
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.bPause.getId()) {
            bikeRunViewModel?.togglePauseResumeStopwatch()
        } else if (view.id == binding!!.bStartStop.getId()) {
            bikeRunViewModel?.toggleStartStopwatch()
        }
    }

    companion object {
        private const val IS_ACTIVITY = "is_activity"
        private val df1 = DecimalFormat("#.#")
        fun newInstance(isActivity: Boolean): BikeRunFragment {
            val bikeRunFragment = BikeRunFragment()
            val args = Bundle()
            args.putBoolean(IS_ACTIVITY, isActivity)
            bikeRunFragment.arguments = args
            return bikeRunFragment
        }
    }
}