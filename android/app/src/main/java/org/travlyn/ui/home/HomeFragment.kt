package org.travlyn.ui.home

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.travlyn.MainActivity
import org.travlyn.R
import org.travlyn.api.CityApi
import org.travlyn.api.model.City


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private val animationSpeed: Long = 300

    private lateinit var currentLocation: GeoPoint
    private lateinit var cityApi: CityApi

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.cityApi = CityApi(application = activity as MainActivity)

        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mapView.setTileSource(TileSourceFactory.MAPNIK)

        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.setMultiTouchControls(true)

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)

        val mCompassOverlay =
            CompassOverlay(context, InternalCompassOrientationProvider(context), mapView)
        mCompassOverlay.enableCompass()
        mapView.overlays.add(mCompassOverlay)

        val mapController = mapView.controller
        mapController.setZoom(9.5)
        mapController.setCenter(GeoPoint(48.8583, 2.2944))

        currentLocationFab.setOnClickListener {
            focusCurrentLocation()
        }

        searchBarHome.setOnSearchListener(object: FloatingSearchView.OnSearchListener{
            override fun onSearchAction(currentQuery: String?) {
                if (currentQuery != null) {
                    handleSearchCity(currentQuery)
                }
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                handleSearchCity(searchSuggestion.toString())
            }
        })

        setLocationListener()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun handleSearchCity(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val city: City? = cityApi.getCity(query)
            if (city != null) {
                TODO("show detailed bottom dialog and and zoom to city")
            } else {
                TODO("show toast")
            }
        }
    }

    private fun focusCurrentLocation() {
        mapView.controller.animateTo(
            this.currentLocation
        )
    }

    private fun setLocationListener() {
        if (context != null) {
            val locationManager: LocationManager =
                context!!.getSystemService(LOCATION_SERVICE) as LocationManager
            if (checkSelfPermission(
                    context!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            if (location != null) {
                                currentLocation = GeoPoint(
                                    location.latitude,
                                    location.longitude
                                )
                            } else {
                                Log.v(tag, "Location could not be found.")
                            }
                        }

                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: Bundle?
                        ) {
                            when (status) {
                                LocationProvider.OUT_OF_SERVICE -> {

                                }
                                LocationProvider.TEMPORARILY_UNAVAILABLE -> {

                                }
                                LocationProvider.AVAILABLE -> {

                                }
                            }
                        }

                        override fun onProviderEnabled(provider: String?) {
                            toggleLocationButton(true)
                        }

                        override fun onProviderDisabled(provider: String?) {
                            toggleLocationButton(false)
                        }

                    })
            }
        }
    }

    private fun toggleLocationButton(show: Boolean) {
        val interpolator: Interpolator = if (show) {
            OvershootInterpolator()
        } else {
            AnticipateInterpolator()
        }

        val scale: Float = if (show) {
            1f
        } else {
            0f
        }

        currentLocationFab.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(animationSpeed)
            .setInterpolator(interpolator)
            .setListener(null)
    }
}