package org.travlyn.ui.navigation

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.travlyn.R
import org.travlyn.api.model.ExecutionInfo
import org.travlyn.api.model.Trip
import java.util.*


class NavigationActivity : AppCompatActivity() {

    private val currentGeoPoint: GeoPoint = GeoPoint(0.0, 0.0)
    private lateinit var locationMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        initStartNavigationButton()
        initMapView()
        initCancelButton()

        window.decorView.rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val bundle = intent.extras
            if (bundle != null && bundle.containsKey("trip")) {
                val trip: Trip = Gson().fromJson(bundle.getString("trip"), Trip::class.java)
                val executionInfo =
                    Gson().fromJson(
                        bundle.getString("executionInfo"),
                        ExecutionInfo::class.java
                    )

                updateCurrentLocation()
                showRoute(executionInfo)
                showStops(trip)
            }
        }
    }

    private fun showStops(trip: Trip) {
        for (stop in trip.stops!!) {
            val marker = Marker(navigationMapView)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.position = GeoPoint(stop.latitude!!, stop.longitude!!)
            marker.icon = getDrawable(R.drawable.ic_location_marker)
            marker.title = stop.name
            marker.subDescription = stop.description!!.substring(0, 170) + "..."
            marker.infoWindow.view.background = getDrawable(R.drawable.stop_info_window)
            marker.infoWindow.view.elevation = 10f
            navigationMapView.overlays.add(marker)
        }
    }

    private fun showRoute(executionInfo: ExecutionInfo?) {
        val wayPoints: ArrayList<GeoPoint> = arrayListOf()

        for (wayPoint in executionInfo!!.waypoints) {
            wayPoints.add(GeoPoint(wayPoint.latitude!!, wayPoint.longitude!!))
        }
        val road = Road(wayPoints)
        navigationMapView.overlays.add(
            RoadManager.buildRoadOverlay(
                road,
                getColor(R.color.colorPrimary),
                10f
            )
        )
        navigationMapView.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(wayPoints), false, 64)
        navigationMapView.invalidate()
    }

    private fun initStartNavigationButton() {
        startNavigationButton.setOnClickListener {
            startNavigationButton.visibility = View.GONE
            navigationHeaderLayout.visibility = View.VISIBLE
            updateCurrentLocation()
            navigationMapView.controller.setZoom(18.0)
            navigationMapView.controller.setCenter(currentGeoPoint)
            navigationMapView.invalidate()
        }
    }

    private fun initMapView() {
        Configuration.getInstance()
            .load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        navigationMapView.setTileSource(TileSourceFactory.MAPNIK)
        navigationMapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        navigationMapView.setMultiTouchControls(true)

        locationMarker = Marker(navigationMapView)
        locationMarker.icon = this@NavigationActivity.getDrawable(R.drawable.ic_navigation)
        navigationMapView.overlays.add(locationMarker)
    }

    private fun initCancelButton() {
        cancelNavigationButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.navigation))
                .setMessage(R.string.confirm_cancel_navigation)
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    finish()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show()
        }
    }

    private fun updateCurrentLocation() = CoroutineScope(Dispatchers.Main).launch {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location = locationManager.fetchLocation(this@NavigationActivity)

        currentGeoPoint.latitude = location.latitude
        currentGeoPoint.longitude = location.longitude

        locationMarker.position = currentGeoPoint
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
}
