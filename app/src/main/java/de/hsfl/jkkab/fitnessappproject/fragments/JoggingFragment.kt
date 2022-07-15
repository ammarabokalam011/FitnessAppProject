package de.hsfl.jkkab.fitnessappproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.hsfl.jkkab.fitnessappproject.MainActivity
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentBikerunBinding
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentJoggingBinding
import de.hsfl.jkkab.fitnessappproject.models.CaloriesCalculator
import de.hsfl.jkkab.fitnessappproject.repositories.Repository
import de.hsfl.jkkab.fitnessappproject.viewmodels.BikeRunViewModel
import de.hsfl.jkkab.fitnessappproject.viewmodels.JoggingViewModel
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 * Use the [JoggingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JoggingFragment private constructor() : Fragment(), View.OnClickListener {
    private var binding: FragmentJoggingBinding? = null
    private var joggingViewModel: JoggingViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository: Repository? = (activity as MainActivity?)?.repository
        val isActivity = requireArguments().getBoolean(IS_ACTIVITY)
        if (isActivity) {
            if (repository != null) {
                repository.caloriesRepository?.changeSportActivity(CaloriesCalculator.SportActivity.JOGGING)
            }
        }
        joggingViewModel = ViewModelProvider(this)[JoggingViewModel::class.java]
        if (repository != null) {
            joggingViewModel!!.setRepository(repository)
        }
        binding = FragmentJoggingBinding.inflate(inflater, container, false)
        joggingViewModel?.speed?.observe(viewLifecycleOwner, speedObserver)
        joggingViewModel?.averageSpeed?.observe(viewLifecycleOwner, speedAverageObserver)
        joggingViewModel?.distance?.observe(viewLifecycleOwner, distanceObserver)
        joggingViewModel?.getCalories()?.observe(viewLifecycleOwner, caloriesObserver)
        joggingViewModel?.heartRate?.observe(viewLifecycleOwner, heartRateObserver)
        joggingViewModel?.averageHeartRate?.observe(viewLifecycleOwner, heartRateAverageObserver)
        joggingViewModel?.time?.observe(viewLifecycleOwner, timeObserver)
        joggingViewModel?.elapsedTime?.observe(viewLifecycleOwner, timeElapsedObserver)
        joggingViewModel?.isStopwatchStarted?.observe(viewLifecycleOwner, startStopTextObserver)
        joggingViewModel?.isStopwatchRunning?.observe(viewLifecycleOwner, runningTextObserver)
        binding!!.bPause.setOnClickListener(this)
        binding!!.bStartStop.setOnClickListener(this)
        return binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val speedObserver: Observer<Float> =
        Observer { speed -> binding!!.tvSpeed.text = df1.format(speed) }
    private val speedAverageObserver: Observer<Double> = Observer { average ->
        binding!!.tvAverageSpeed.text = df1.format(average)
    }
    private val distanceObserver =
        Observer<Float> { distance -> binding!!.tvDistance.text = df1.format(distance / 1000.0) }
    private val caloriesObserver: Observer<Double> = Observer { calories ->
        binding!!.tvCalories.text = df1.format(calories)
    }
    private val heartRateObserver =
        Observer<Int> { heartRate -> binding!!.tvHeartRate.text = heartRate.toString() }
    private val heartRateAverageObserver: Observer<Double> = Observer { heartRateAverage ->
        binding!!.tvAverageHeartRate.text = df1.format(heartRateAverage)
    }
    private val timeObserver: Observer<String> = Observer { time -> binding!!.tvClock.text = time }
    private val timeElapsedObserver: Observer<String> =
        Observer { time -> binding!!.tvElapsedTime.text = time }
    private val startStopTextObserver = Observer<Boolean> { isStarted ->
        binding!!.bStartStop.text = if (isStarted) getString(R.string.stop) else getString(R.string.start)
    }
    private val runningTextObserver = Observer<Boolean> { isRunning ->
        binding!!.bPause.text = if (isRunning) getString(R.string.pause) else getString(R.string.resume)
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.bPause.id) {
            joggingViewModel?.togglePauseResumeStopwatch()
        } else if (view.id == binding!!.bStartStop.id) {
            joggingViewModel?.toggleStartStopwatch()
        }
    }

    companion object {
        private const val IS_ACTIVITY = "is_activity"
        private val df1 = DecimalFormat("#.#")
        fun newInstance(isActivity: Boolean): JoggingFragment {
            val joggingFragment = JoggingFragment()
            val args = Bundle()
            args.putBoolean(IS_ACTIVITY, isActivity)
            joggingFragment.arguments = args
            return joggingFragment
        }
    }
}