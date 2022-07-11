package de.hsfl.jkkab.fitnessappproject.fragments

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.preference.PreferenceManager
import de.hsfl.jkkab.fitnessappproject.MainActivity
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentTrackBinding
import de.hsfl.jkkab.fitnessappproject.viewmodels.TrackViewModel
import org.osmdroid.util.GeoPoint
import java.lang.NullPointerException
import java.text.DecimalFormat
import java.util.ArrayList
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory


class TrackFragment : Fragment(), View.OnClickListener, PagerFragment {
    private var mMapController: MapController? = null
    private var myLocationMarker: Marker? = null
    private var path: Polyline? = null
    private var trackViewModel: TrackViewModel? = null
    private var geoPoints: MutableList<GeoPoint>? = null
    private var binding: FragmentTrackBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geoPoints = ArrayList<GeoPoint>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        trackViewModel = ViewModelProvider(this).get(TrackViewModel::class.java)
        (activity as MainActivity?)?.repository?.let { trackViewModel!!.setRepository(it) }
        binding = FragmentTrackBinding.inflate(inflater, container, false)
        trackViewModel?.location?.observe(viewLifecycleOwner, locationObserver)
        Configuration.getInstance().load(
            context, context?.let {
                PreferenceManager.getDefaultSharedPreferences(
                    it
                )
            }
        )
        binding!!.map.setTileSource(TileSourceFactory.MAPNIK)
        mMapController = binding!!.map.getController() as MapController
        mMapController!!.setCenter(GeoPoint(54.7741, 9.4494))
        mMapController!!.setZoom(18)
        path = Polyline()
        path!!.setPoints(geoPoints)
        path!!.getOutlinePaint().setColor(Color.RED)
        binding!!.map.getOverlayManager().add(path)
        binding!!.map.setOnTouchListener { view, motionEvent ->
            trackViewModel!!.isSyncCenter()?.setValue(false)
            false
        }
        myLocationMarker = Marker(binding!!.map)
        myLocationMarker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding!!.map.getOverlays().add(myLocationMarker)

        //ViewBinding
        trackViewModel?.speed?.observe(viewLifecycleOwner, speedObserver)
        binding!!.bCenter.setOnClickListener(this)
        binding!!.bStartStop.setOnClickListener(this)
        binding!!.bPause.setOnClickListener(this)
        trackViewModel!!.isStopwatchStarted?.observe(viewLifecycleOwner, startStopTextObserver)
        trackViewModel!!.isStopwatchRunning?.observe(viewLifecycleOwner, runningTextObserver)
        return binding!!.getRoot()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(view: View) {
        if (view.id == binding!!.bCenter.getId()) {
            if (geoPoints!!.size > 0) {
                mMapController?.animateTo(geoPoints!![geoPoints!!.size - 1])
            }
            trackViewModel?.syncCenter()
        } else if (view.id == binding!!.bStartStop.getId()) {
            trackViewModel?.toggleStartStopwatch()
        } else if (view.id == binding!!.bPause.getId()) {
            trackViewModel?.togglePauseResumeStopwatch()
        }
    }

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
    private val speedObserver: Observer<Float> =
        Observer { aFloat -> binding!!.tvSpeed.setText(df2.format(aFloat)) }
    private val locationObserver = Observer<Location> { location ->
        if (path != null) {
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            geoPoints!!.add(geoPoint)
            try {
                path!!.setPoints(geoPoints)
                if (trackViewModel?.isSyncCenter()?.getValue() == true) {
                    mMapController?.animateTo(geoPoint)
                }
                myLocationMarker?.setPosition(GeoPoint(location.latitude, location.longitude))
                binding?.map?.invalidate()
            } catch (e: NullPointerException) {
                Log.d("TrackFragment", "Map destroyed")
            }
        }
    }

    override fun onBackPressed() {
        Log.d("TrackFragment", "onBackPressed()")
        view?.let { findNavController(it).navigateUp() }
    }

    companion object {
        private val df2 = DecimalFormat("#.##")
        fun newInstance(): TrackFragment {
            return TrackFragment()
        }
    }
}