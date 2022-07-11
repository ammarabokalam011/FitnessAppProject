package de.hsfl.jkkab.fitnessappproject.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import de.hsfl.jkkab.fitnessappproject.MainActivity
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentCardioBinding
import de.hsfl.jkkab.fitnessappproject.viewmodels.CardioViewModel
import java.text.DecimalFormat

class CardioFragment : Fragment(), View.OnClickListener, PagerFragment {
    private var cardioViewModel: CardioViewModel? = null
    private var binding: FragmentCardioBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cardioViewModel = ViewModelProvider(this).get(CardioViewModel::class.java)
        (activity as MainActivity?)?.repository?.let { cardioViewModel!!.setRepository(it) }
        binding = FragmentCardioBinding.inflate(inflater, container, false)
        cardioViewModel?.heartRate?.observe(viewLifecycleOwner, heartRateObserver)
        cardioViewModel?.heartRateAverage?.observe(viewLifecycleOwner, heartRateAverageObserver)
        cardioViewModel?.time?.observe(viewLifecycleOwner, timeObserver)
        cardioViewModel?.elapsedTime?.observe(viewLifecycleOwner, timeElapsedObserver)
        cardioViewModel?.isStopwatchStarted?.observe(viewLifecycleOwner, startStopTextObserver)
        cardioViewModel?.isStopwatchRunning?.observe(viewLifecycleOwner, runningTextObserver)
        cardioViewModel?.elapsedSeconds?.observe(viewLifecycleOwner, secondsObserver)
        cardioViewModel?.getCalories()?.observe(viewLifecycleOwner, caloriesObserver)
        binding!!.bPause.setOnClickListener(this)
        binding!!.bStartStop.setOnClickListener(this)
        return binding!!.getRoot()
    }

    private val caloriesObserver: Observer<Double> = Observer { calories ->
        binding!!.tvCalories.setText(
            df1.format(calories)
        )
    }
    private val heartRateObserver = Observer<Int> { heartRate ->
        binding!!.tvHeartRate.setText(heartRate.toString())
        binding!!.heartRateGraph.addHeartRate(heartRate)
    }
    private val heartRateAverageObserver =
        Observer<Double> { heartRateAverage -> binding!!.tvHeartAverage.setText(heartRateAverage.toString()) }
    private val timeObserver: Observer<String> = Observer { time -> binding!!.tvClock.setText(time) }
    private val timeElapsedObserver: Observer<String> =
        Observer { time -> binding!!.tvElapsedTime.setText(time) }
    private val startStopTextObserver = Observer<Boolean> { isStarted ->
        binding!!.bStartStop.setText(
            if (isStarted) getString(R.string.stop) else getString(R.string.start)
        )
    }
    private val runningTextObserver = Observer<Boolean> { isRunning ->
        binding!!.bPause.setText(if (isRunning) getString(R.string.pause) else getString(R.string.resume))
        if (isRunning) {
            binding!!.heartRateGraph.startTracking()
        } else {
            binding!!.heartRateGraph.stopTracking()
        }
    }
    private val secondsObserver: Observer<Long> =
        Observer { seconds -> binding!!.heartRateGraph.setCorridorOffsetSeconds(seconds) }

    override fun onClick(view: View) {
        if (view.id == binding!!.bPause.getId()) {
            cardioViewModel?.togglePauseResumeStopwatch()
        } else if (view.id == binding!!.bStartStop.getId()) {
            cardioViewModel?.toggleStartStopwatch()
        }
    }

    override fun onBackPressed() {
        Log.d("CardioFragment", "onBackPressed()")
        view?.let { findNavController(it).navigateUp() }
    }

    companion object {
        private val df1 = DecimalFormat("#.#")
        fun newInstance(): CardioFragment {
            return CardioFragment()
        }
    }
}