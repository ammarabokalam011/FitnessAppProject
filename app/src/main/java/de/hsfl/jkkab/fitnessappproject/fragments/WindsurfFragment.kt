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
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentWindsurfBinding
import de.hsfl.jkkab.fitnessappproject.models.CaloriesCalculator
import de.hsfl.jkkab.fitnessappproject.repositories.Repository
import de.hsfl.jkkab.fitnessappproject.viewmodels.WindsurfViewModel
import java.text.DecimalFormat
import kotlin.math.roundToInt

class WindsurfFragment private constructor() : Fragment(), View.OnClickListener {
    private var binding: FragmentWindsurfBinding? = null
    private var windsurfViewModel: WindsurfViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository: Repository? = (activity as MainActivity?)?.repository
        val isActivity = requireArguments().getBoolean(IS_ACTIVITY)
        if (isActivity) {
            if (repository != null) {
                repository.caloriesRepository
                    ?.changeSportActivity(CaloriesCalculator.SportActivity.WINDSURFING)
            }
        }
        windsurfViewModel = ViewModelProvider(this).get(WindsurfViewModel::class.java)
        if (repository != null) {
            windsurfViewModel!!.setRepository(repository)
        }
        binding = FragmentWindsurfBinding.inflate(inflater, container, false)
        windsurfViewModel?.speed?.observe(viewLifecycleOwner, speedObserver)
        windsurfViewModel?.bearing?.observe(viewLifecycleOwner, bearingObserver)
        windsurfViewModel?.averageBearing?.observe(viewLifecycleOwner, bearingAverageObserver)
        windsurfViewModel?.distanceToStart?.observe(viewLifecycleOwner, distanceObserver)
        windsurfViewModel?.time?.observe(viewLifecycleOwner, timeObserver)
        windsurfViewModel?.elapsedTime?.observe(viewLifecycleOwner, timeElapsedObserver)
        windsurfViewModel?.isStopwatchStarted?.observe(viewLifecycleOwner, startStopTextObserver)
        windsurfViewModel?.isStopwatchRunning?.observe(viewLifecycleOwner, runningTextObserver)
        binding!!.bPause.setOnClickListener(this)
        binding!!.bStartStop.setOnClickListener(this)
        return binding!!.getRoot()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val speedObserver =
        Observer<Float> { speed -> binding!!.tvSpeed.setText(df1.format(speed / 1.852)) }
    private val bearingObserver: Observer<Float> = Observer { bearing ->
        binding!!.tvBearing.setText(
            (bearing!!).roundToInt()
        )
    }
    private val bearingAverageObserver: Observer<Double> = Observer { average ->
        binding!!.tvAverageBearing.setText(
            (average!!).roundToInt()
        )
    }
    private val distanceObserver =
        Observer<Float> { distance -> binding!!.tvDistance.setText(df1.format(distance / 1000.0 / 1.852)) }
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
            windsurfViewModel?.togglePauseResumeStopwatch()
        } else if (view.id == binding!!.bStartStop.getId()) {
            windsurfViewModel?.toggleStartStopwatch()
        }
    }

    companion object {
        private const val IS_ACTIVITY = "is_activity"
        private val df1 = DecimalFormat("#.#")
        fun newInstance(isActivity: Boolean): WindsurfFragment {
            val bikeRunFragment = WindsurfFragment()
            val args = Bundle()
            args.putBoolean(IS_ACTIVITY, isActivity)
            bikeRunFragment.arguments = args
            return bikeRunFragment
        }
    }
}