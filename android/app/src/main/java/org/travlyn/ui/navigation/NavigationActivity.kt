package org.travlyn.ui.navigation

import android.animation.ObjectAnimator
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.coroutines.*
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
import org.travlyn.api.model.Step
import org.travlyn.api.model.Trip
import org.travlyn.api.model.Waypoint
import java.util.*
import kotlin.math.*


class NavigationActivity : AppCompatActivity() {

    // if distance between current location and way point is less than threshold, the way point is
    // marked as passed
    private val wayPointThreshold = 10

    private val defaultVelocity = 5.0

    // distance that the user has passed
    private var distancePassed = 0.0

    // current step that is shown
    private var currentStep = 0

    // way point with index 0 is start position
    private var currentWayPoint = 1

    private lateinit var trip: Trip
    private lateinit var executionInfo: ExecutionInfo
    private val currentLocation: GeoPoint = GeoPoint(0.0, 0.0)
    private lateinit var locationMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        initStartNavigationButton()
        initMapView()
        initCancelButton()

        val bundle = intent.extras
        if (bundle != null && bundle.containsKey("trip")) {
            trip = Gson().fromJson(bundle.getString("trip"), Trip::class.java)
            executionInfo =
                Gson().fromJson(
                    bundle.getString("executionInfo"),
                    ExecutionInfo::class.java
                )

            initTripInformation(trip, executionInfo)

            showRoute(executionInfo)
            showStops(trip)
            CoroutineScope(Dispatchers.Main).launch {
                updateCurrentLocation()
            }
        }
    }

    private fun startNavigationTimer() = CoroutineScope(Dispatchers.Default).launch {
        while (currentStep < executionInfo.steps.size && currentWayPoint < executionInfo.waypoints.size) {
            updateNavigation()
            delay(1000 / 10)
        }
    }

    private suspend fun updateNavigation() {
        val oldLocation = GeoPoint(currentLocation.latitude, currentLocation.longitude)
        updateCurrentLocation()
        val step: Step = executionInfo.steps[currentStep]
        val wayPoint: Waypoint = executionInfo.waypoints[currentWayPoint]

        val distanceToNextWayPoint = distance(
            currentLocation, GeoPoint(
                wayPoint.latitude!!,
                wayPoint.longitude!!
            )
        )

        distancePassed += distance(oldLocation, currentLocation)
        val remainingDistance = executionInfo.distance!! - (distancePassed / 1000)

        withContext(Dispatchers.Main) {
            navigationMapView.mapOrientation = getAngle(oldLocation, currentLocation).toFloat() - 90
            navigationMapView.controller.animateTo(currentLocation)
            navigationInstructionTextView.text = step.instruction
            navigationInstructionDistanceTextView.text =
                getString(R.string.distance_unit_meters, distanceToNextWayPoint.roundToInt())
            navigationRemainingDistanceTextView.text =
                getString(R.string.distance_unit, remainingDistance)

            val hours: Int = (remainingDistance / defaultVelocity).toInt()
            val minutes: Int = (((remainingDistance / defaultVelocity) - hours) * 60).toInt()
            navigationRemainingTimeTextView.text = getString(R.string.time_unit, hours, minutes)
        }

        // if way point is reach (or the distance to the way point is less than a threshold)
        if (distanceToNextWayPoint < wayPointThreshold) {
            currentWayPoint++
        }

        // if the last way point of the step is reached
        if (currentWayPoint >= step.waypoints.last()) {
            currentStep++
        }
    }

    private fun getAngle(geoPoint1: GeoPoint, geoPoint2: GeoPoint): Double {
        val boxX: Double = geoPoint1.latitude
        val boxY: Double = geoPoint1.longitude
        val touchX: Double = geoPoint2.latitude
        val touchY: Double = geoPoint2.longitude

        return 180.0 / Math.PI * atan2(boxX - touchX, touchY - boxY)
    }

    private fun distance(geoPoint1: GeoPoint, geoPoint2: GeoPoint): Double {
        val earthRadius = 6371 // Radius of the earth
        val latDistance = Math.toRadians(geoPoint2.latitude - geoPoint1.latitude)
        val lonDistance = Math.toRadians(geoPoint2.longitude - geoPoint1.longitude)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(geoPoint1.latitude)) * cos(Math.toRadians(geoPoint2.latitude))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = earthRadius * c * 1000 // convert to meters
        distance = distance.pow(2.0)
        return sqrt(distance)
    }

    private fun initTripInformation(trip: Trip, executionInfo: ExecutionInfo) {
        navigationTripInformationNameTextView.text = trip.name
        if (!trip.private!!) {
            navigationTripInformationNameTextView.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, 0
            )
        }

        if (trip.city != null) {
            navigationTripInformationCityTextView.text = trip.city.name
        }

        if (trip.user != null) {
            navigationTripInformationCreatedByTextView.text = trip.user.name
        }

        if (trip.stops != null) {
            navigationTripInformationNumberOfStopsTextView.text = trip.stops!!.size.toString()
        }

        val hours: Int = (executionInfo.duration!! / 60.0).toInt()
        val minutes: Int = (executionInfo.duration % 60.0).toInt()
        navigationRemainingTimeTextView.text = getString(R.string.time_unit, hours, minutes)
        navigationRemainingDistanceTextView.text =
            getString(R.string.distance_unit, executionInfo.distance)
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
        val boundingBox = BoundingBox.fromGeoPoints(wayPoints)
        navigationMapView.focusBoundingBox(boundingBox, borderSize = 64)
        navigationMapView.invalidate()
    }

    private fun initStartNavigationButton() {
        startNavigationButton.setOnClickListener {
            startNavigationButton.visibility = View.GONE
            skipNextStopButton.visibility = View.GONE
            navigationHeaderLayout.visibility = View.VISIBLE

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels

            val outAnimation: ObjectAnimator =
                ObjectAnimator.ofFloat(navigationHeaderLayout, "x", -width.toFloat(), 16f)
            outAnimation.duration = 500
            outAnimation.interpolator = DecelerateInterpolator()
            outAnimation.start()

            val inAnimation: ObjectAnimator =
                ObjectAnimator.ofFloat(
                    navigationHeaderTripInformationLayout,
                    "x",
                    16f,
                    width.toFloat()
                )
            inAnimation.duration = 500
            inAnimation.interpolator = DecelerateInterpolator()
            inAnimation.start()

            navigationMapView.controller.zoomTo(19.0)
            navigationMapView.controller.animateTo(currentLocation)

            startNavigationTimer()
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

    private suspend fun updateCurrentLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location = locationManager.fetchLocation(this@NavigationActivity)

        currentLocation.latitude = location.latitude
        currentLocation.longitude = location.longitude

        locationMarker.position = currentLocation
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
}
