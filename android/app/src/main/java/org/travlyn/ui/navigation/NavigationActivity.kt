package org.travlyn.ui.navigation

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.arlib.floatingsearchview.util.Util.getScreenWidth
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
import org.travlyn.api.CityApi
import org.travlyn.api.StopApi
import org.travlyn.api.model.*
import org.travlyn.infrastructure.dpToPx
import org.travlyn.local.Application
import java.util.*
import kotlin.math.*

class NavigationActivity : AppCompatActivity(), Application {

    // if distance between current location and way point is less than threshold, the way point is
    // marked as passed
    private val wayPointThreshold = 20
    private val stopThreshold = 50

    private val defaultVelocity = 5.0

    // distance that the user has passed
    private var distancePassed = 0.0

    // current step that is shown
    private var currentStep = 0

    private var currentStopIndex = -1
    private lateinit var nextStop: Stop

    // way point with index 0 is start position
    private var currentWayPoint = 1

    private var stopReached = false

    private lateinit var trip: Trip
    private lateinit var executionInfo: ExecutionInfo
    private val currentLocation: GeoPoint = GeoPoint(0.0, 0.0)
    private lateinit var locationMarker: Marker
    private lateinit var cityApi: CityApi
    private lateinit var stopApi: StopApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        cityApi = CityApi(this)
        stopApi = StopApi(this)

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

            updateNextStop()

            initTripInformation(trip, executionInfo)

            showRoute(executionInfo)
            showStops(trip)
            CoroutineScope(Dispatchers.Main).launch {
                updateCurrentLocation()
            }
        }
    }

    private fun startNavigationTimer() = CoroutineScope(Dispatchers.Default).launch {
        while (!stopReached && currentStep < executionInfo.steps.size && currentWayPoint < executionInfo.waypoints.size) {
            updateNavigation()
            delay(1000 / 10)
        }
    }

    private suspend fun updateNavigation() {
        val oldLocation = GeoPoint(currentLocation.latitude, currentLocation.longitude)
        updateCurrentLocation()
        val step: Step = executionInfo.steps[currentStep]
        val wayPoint: Waypoint = executionInfo.waypoints[currentWayPoint]

        val distanceToNextStop = distance(
            currentLocation, GeoPoint(
                nextStop.latitude!!,
                nextStop.longitude!!
            )
        )
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

        // if stop is reached
        if (distanceToNextStop < stopThreshold) {
            stopReached()
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

    private suspend fun stopReached() {
        this@NavigationActivity.stopReached = true
        initStopInformationLayout(nextStop)

        withContext(Dispatchers.Main) {

            skipNextStopButton.visibility = View.GONE
            continueToNextStopButton.visibility = View.VISIBLE

            continueToNextStopButton.setOnClickListener {
                updateNextStop()
                this@NavigationActivity.stopReached = false
                val outAnimation: ObjectAnimator =
                    ObjectAnimator.ofFloat(
                        navigationStopInformationLayout,
                        "x",
                        dpToPx(8).toFloat(),
                        getScreenWidth(this@NavigationActivity).toFloat()
                    )
                outAnimation.duration = 500
                outAnimation.interpolator = DecelerateInterpolator()
                outAnimation.start()

                continueToNextStopButton.visibility = View.GONE
                skipNextStopButton.visibility = View.VISIBLE

                startNavigationTimer()
            }
        }
    }


    private fun updateNextStop() {
        currentStopIndex++
        nextStop =
            trip.stops!!.find { stop -> stop.id == executionInfo.stopIds[currentStopIndex] }!!
    }

    private suspend fun initStopInformationLayout(stop: Stop) = withContext(Dispatchers.Main) {
        navigationStopInformationLayout.visibility = View.VISIBLE

        val inAnimation: ObjectAnimator =
            ObjectAnimator.ofFloat(
                navigationStopInformationLayout,
                "x",
                -getScreenWidth(this@NavigationActivity).toFloat(),
                dpToPx(8).toFloat()
            )
        inAnimation.duration = 500
        inAnimation.interpolator = DecelerateInterpolator()
        inAnimation.start()

        val outAnimation: ObjectAnimator =
            ObjectAnimator.ofFloat(
                navigationHeaderLayout,
                "x",
                dpToPx(8).toFloat(),
                getScreenWidth(this@NavigationActivity).toFloat()
            )
        outAnimation.duration = 500
        outAnimation.interpolator = DecelerateInterpolator()
        outAnimation.start()

        navigationStopNameTextView.text = stop.name

        CoroutineScope(Dispatchers.IO).launch {
            if (stop.image != null) {
                withContext(Dispatchers.Main) {
                    val bitmap: Bitmap? = cityApi.getImage(stop.image)
                    if (bitmap != null) {
                        navigationStopThumbnailImageView.setImageBitmap(bitmap)
                        navigationStopThumbnailProgressIndicator.visibility = View.GONE
                    }
                }
            }
        }

        if (stop.averageRating == null || stop.averageRating <= 0) {
            navigationStopRatingTextView.text = getString(R.string.no_value)
        } else {
            navigationStopRatingBar.rating = stop.averageRating.toFloat() * 5f
            navigationStopRatingTextView.text =
                getString(R.string.rating_value, stop.averageRating * 5f)
        }

        navigationStopDescriptionTextView.text = stop.description

        val stringIdentifier = resources.getIdentifier(
            "category_" + stop.category?.name, "string", packageName
        )
        navigationStopCategoryTextView.text =
            if (stringIdentifier > 0) getString(stringIdentifier) else stop.category?.name


        if (stop.timeEffort == null || stop.timeEffort <= 0) {
            navigationStopTimeEffortTextView.text = getString(R.string.no_value)
        } else {
            navigationStopTimeEffortTextView.text =
                getString(R.string.hours_unit, stop.timeEffort)
        }

        if (stop.pricing == null || stop.pricing <= 0) {
            navigationStopPricingTextView.text = getString(R.string.no_value)
        } else {
            navigationStopPricingTextView.text = stop.pricing.toString()
        }

        navigationStopAddPricingButton.setOnClickListener {
            showPricingDialog()
        }
        navigationStopTimeEffortButton.setOnClickListener {
            showTimeEffortDialog()
        }
    }

    private fun initStartNavigationButton() {
        startNavigationButton.setOnClickListener {
            startNavigationButton.visibility = View.GONE
            skipNextStopButton.visibility = View.GONE
            navigationHeaderLayout.visibility = View.VISIBLE

            val outAnimation: ObjectAnimator =
                ObjectAnimator.ofFloat(
                    navigationHeaderLayout,
                    "x",
                    -getScreenWidth(this@NavigationActivity).toFloat(),
                    dpToPx(8).toFloat()
                )
            outAnimation.duration = 500
            outAnimation.interpolator = DecelerateInterpolator()
            outAnimation.start()

            val inAnimation: ObjectAnimator =
                ObjectAnimator.ofFloat(
                    navigationHeaderTripInformationLayout,
                    "x",
                    dpToPx(8).toFloat(),
                    getScreenWidth(this@NavigationActivity).toFloat()
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

    private fun showTimeEffortDialog() {
        val dialogLayout: View = layoutInflater.inflate(R.layout.dialog_add_time_effort, null)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.navigation))
            .setView(dialogLayout)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val pricingEditText: EditText =
                    dialogLayout.findViewById(R.id.timeEffortDialogEditText)

                CoroutineScope(Dispatchers.IO).launch {
                    stopApi.addTimeEffortToStop(
                        nextStop.id!!,
                        pricingEditText.text.toString().toDouble()
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@NavigationActivity,
                            getString(R.string.successfully_added_time_effort),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .show()
    }

    private fun showPricingDialog() {
        val dialogLayout: View = layoutInflater.inflate(R.layout.dialog_add_pricing, null)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.navigation))
            .setView(dialogLayout)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val pricingEditText: EditText =
                    dialogLayout.findViewById(R.id.pricingDialogEditText)

                CoroutineScope(Dispatchers.IO).launch {
                    stopApi.addPricingToStop(
                        nextStop.id!!,
                        pricingEditText.text.toString().toDouble()
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@NavigationActivity,
                            getString(R.string.successfully_added_pricing),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .show()
    }

    private suspend fun updateCurrentLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location = locationManager.fetchLocation(this@NavigationActivity)

        currentLocation.latitude = location.latitude
        currentLocation.longitude = location.longitude

        locationMarker.position = currentLocation
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }

    override fun getContext(): Context {
        return this
    }
}
