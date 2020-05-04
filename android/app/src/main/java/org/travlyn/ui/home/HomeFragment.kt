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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.travlyn.MainActivity
import org.travlyn.R
import org.travlyn.api.CityApi
import org.travlyn.api.model.City
import org.travlyn.local.LocalStorage
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private val animationSpeed: Long = 300

    private var currentLocation: GeoPoint? = null
    private lateinit var cityApi: CityApi

    private lateinit var suggestions: MutableList<String>

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

        if (context != null) {
            this.cityApi = CityApi(application = context as MainActivity)
        }

        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.setMultiTouchControls(true)

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)

        mapView.controller.setZoom(9.5)
        mapView.controller.setCenter(GeoPoint(48.8583, 2.2944))

        currentLocationFab.setOnClickListener {
            focusCurrentLocation()
        }

        suggestions = LocalStorage(context!!).readObject("searchCitySuggestions")!!

        searchBarHome.attachNavigationDrawerToMenuButton((activity as MainActivity).findViewById(R.id.drawer_layout))
        if (LocalStorage(context!!).contains("user")) {
            searchBarHome.inflateOverflowMenu(R.menu.logout_menu)
        } else {
            searchBarHome.inflateOverflowMenu(R.menu.login_menu)
        }
        searchBarHome.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                searchBarHome.clearSuggestions()
            } else {
                suggestions = LocalStorage(context!!).readObject("searchCitySuggestions")!!
                suggestions = suggestions.filter { f ->
                    f.toLowerCase(Locale.ROOT).contains(newQuery.toLowerCase(Locale.ROOT))
                }.toMutableList()
                updateSuggestions()
            }
        }
        searchBarHome.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSearchAction(currentQuery: String?) {
                if (currentQuery != null) {
                    handleSearchCity(currentQuery)
                }
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                if (searchSuggestion != null) {
                    handleSearchCity(searchSuggestion.body)
                    searchBarHome.setSearchText(searchSuggestion.body)
                    searchBarHome.clearSearchFocus()
                }
            }
        })

        searchBarHome.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.menu_sign_in -> {
                    (activity as MainActivity).openLoginActivity()
                }
                R.id.menu_register -> {
                    (activity as MainActivity).openRegisterActivity()
                }
                R.id.menu_logout -> {
                    (activity as MainActivity).handleLogout()
                }
                else -> super.onOptionsItemSelected(item)
            }
        }

        searchBarHome.setOnCreateContextMenuListener { _, _, _ ->
            updateMenuItems()
        }

        setLocationListener()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun updateSuggestions() {
        val suggestionObjects: MutableList<Suggestion> = mutableListOf()
        suggestions.forEach { s -> suggestionObjects.add(Suggestion(s)) }
        searchBarHome.swapSuggestions(suggestionObjects)
    }

    private fun updateMenuItems() {
        searchBarHome.currentMenuItems.forEach { item ->
            when ((item as MenuItem).itemId) {
                R.id.menu_sign_in -> {
                    (item as MenuItem).isVisible = LocalStorage(context!!).contains("user")
                }
                R.id.menu_logout -> {
                    (item as MenuItem).isVisible = !LocalStorage(context!!).contains("user")

                }
            }
        }
    }

    private fun handleSearchCity(query: String) {
        searchBarHome.showProgress()
        CoroutineScope(Dispatchers.IO).launch {
            val city: City? = cityApi.getCity(query)
            withContext(Dispatchers.Main) { searchBarHome.hideProgress() }
            if (city != null) {
                addCityToSuggestions(city)
                focusCityAndAddMarker(city)

                val sheet = CityInformationFragment.newInstance(city)
                if (activity != null) {
                    sheet.show(activity!!.supportFragmentManager, "CityInformationFragment")
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        getString(R.string.error_no_city_found, query),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun addCityToSuggestions(city: City) {
        val suggestions: MutableList<String>? =
            LocalStorage(context!!).readObject("searchCitySuggestions")
        if (suggestions != null) {
            if (!suggestions.contains(city.name)) {
                city.name?.let { suggestions.add(it) }
            }
        }
        LocalStorage(context!!).writeObject("searchCitySuggestions", suggestions)
    }

    private suspend fun focusCityAndAddMarker(city: City) = withContext(Dispatchers.Main) {
        if (city.longitude != null && city.latitude != null) {
            mapView.overlay.clear()
            val cityLocation = GeoPoint(city.latitude, city.longitude)
            val startMarker = Marker(mapView)
            startMarker.icon = context!!.getDrawable(R.drawable.ic_location_marker)
            startMarker.position = cityLocation
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(startMarker)
            mapView.controller.setZoom(13.0)
            mapView.controller.animateTo(
                GeoPoint(
                    cityLocation.latitude - 0.03,
                    cityLocation.longitude
                )
            )
        }
    }

    private fun focusCurrentLocation() {
        if (this.currentLocation != null) {
            mapView.controller.zoomTo(18, animationSpeed)
            mapView.controller.animateTo(this.currentLocation)
        }
    }

    private fun setLocationListener() {
        if (context != null) {
            val locationManager: LocationManager =
                context!!.getSystemService(LOCATION_SERVICE) as LocationManager
            if (checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
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
            } else {
                toggleLocationButton(false)
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