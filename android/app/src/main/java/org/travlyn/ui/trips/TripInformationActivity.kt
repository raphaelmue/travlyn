package org.travlyn.ui.trips

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_trip_information.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.travlyn.R
import org.travlyn.api.CityApi
import org.travlyn.api.TripApi
import org.travlyn.api.model.ExecutionInfo
import org.travlyn.api.model.Stop
import org.travlyn.api.model.Trip
import org.travlyn.api.model.User
import org.travlyn.local.Application
import org.travlyn.local.LocalStorage
import org.travlyn.ui.navigation.NavigationActivity
import org.travlyn.ui.navigation.fetchLocation


class TripInformationActivity : AppCompatActivity(), Application {

    lateinit var tripApi: TripApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_information)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        tripApi = TripApi(this)

        val bundle = intent.extras
        if (bundle != null && bundle.containsKey("trip")) {
            val trip: Trip = Gson().fromJson(bundle.getString("trip"), Trip::class.java)

            startTripNavigationFab.setOnClickListener {
                showStartNavigationDialog(trip)
            }

            supportActionBar?.title = trip.name + " - " + getString(R.string.trip)

            tripInformationNameTextView.text = trip.name
            if (!trip.private!!) {
                tripInformationNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            if (trip.city != null) {
                tripInformationCityTextView.text = trip.city.name
            } else {
                tripInformationCityTextView.visibility = View.GONE
            }

            if (trip.user?.id != LocalStorage(this).readObject<User>("user")?.id) {
                tripInformationCityTextView.append(" | " + trip.user!!.name)
            }
            if (trip.averageRating == null || trip.averageRating <= 0) {
                tripCardRatingTextView.text = getString(R.string.no_value)
            } else {
                tripCardRatingBar.rating = trip.averageRating.toFloat() * 5f
                tripCardRatingTextView.text =
                    getString(R.string.rating_value, trip.averageRating * 5f)
            }

            val tripStopCardView: RecyclerView = findViewById(R.id.tripStopCardView)
            tripStopCardView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            tripStopCardView.adapter = TripInformationStopAdapter(trip.stops!!.toList(), this)
        }
    }

    private suspend fun startNavigationActivity(trip: Trip, executionInfo: ExecutionInfo) =
        withContext(Dispatchers.Main) {
            val intent = Intent(this@TripInformationActivity, NavigationActivity::class.java)
            intent.putExtras(bundleOf("trip" to Gson().toJson(trip)))
            intent.putExtras(bundleOf("executionInfo" to Gson().toJson(executionInfo)))

            startActivity(intent)
        }

    private fun showStartNavigationDialog(trip: Trip) {
        val dialogLayout: View = layoutInflater.inflate(R.layout.navigation_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.navigation))
            .setView(dialogLayout)
            .setPositiveButton(getString(R.string.ok), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val reorderLayout: LinearLayout =
                    dialogLayout.findViewById(R.id.navigationStartDialogReorderLayout)
                val roundTripLayout: LinearLayout =
                    dialogLayout.findViewById(R.id.navigationStartDialogRoundTripLayout)
                val progressBar: ProgressBar =
                    dialogLayout.findViewById(R.id.navigationStartDialogProgressBar)
                val reorderSwitch: Switch = dialogLayout.findViewById(R.id.navigationReorderSwitch)
                val roundTripSwitch: Switch =
                    dialogLayout.findViewById(R.id.navigationRoundTripSwitch)

                reorderLayout.visibility = View.GONE
                roundTripLayout.visibility = View.GONE
                progressBar.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    fetchRoute(trip, reorderSwitch.isChecked, roundTripSwitch.isChecked)
                    withContext(Dispatchers.Main) { dialog.dismiss() }
                }
            }
        }

        dialog.show()
    }

    private suspend fun fetchRoute(trip: Trip, reorderAllowed: Boolean, roundTrip: Boolean) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location = locationManager.fetchLocation(this@TripInformationActivity)

        val executionInfo = tripApi.getTripExecutionInfo(
            reorderAllowed,
            roundTrip,
            location.latitude,
            location.longitude,
            trip.id!!,
            LocalStorage(this@TripInformationActivity).readObject<User>("user")!!.id!!
        )
        if (executionInfo != null) {
            startNavigationActivity(trip, executionInfo)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun getContext(): Context {
        return this
    }

    private inner class TripInformationStopAdapter(
        private val stops: List<Stop>,
        private val application: Application
    ) : RecyclerView.Adapter<TripInformationStopAdapter.ViewHolder>() {

        private val cityApi = CityApi(application as TripInformationActivity)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(application.getContext())
                    .inflate(R.layout.trip_information_stop_list_view, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return stops.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val stop: Stop = stops[position]

            holder.stopListName.text = stop.name
            holder.stopListDescription.text = stop.description

            if (position == 0) {
                holder.stopListIndicatorView.setImageDrawable(
                    application.getContext()
                        .getDrawable(R.drawable.stop_list_indicator_start)
                )
            }

            if (position == stops.size - 1) {
                holder.stopListIndicatorView.setImageDrawable(
                    application.getContext()
                        .getDrawable(R.drawable.stop_list_indicator_end)
                )
            }

            val stringIdentifier = application.getContext().resources.getIdentifier(
                "category_" + stop.category?.name, "string", application.getContext().packageName
            )
            holder.stopListCategoryTextView.text =
                if (stringIdentifier > 0) application.getContext()
                    .getString(stringIdentifier) else stop.category?.name

            if (stop.averageRating == null || stop.averageRating <= 0) {
                holder.stopListRatingTextView.text =
                    application.getContext().getString(R.string.no_value)
            } else {
                holder.stopListRatingBar.rating = stop.averageRating.toFloat() * 5f
                holder.stopListRatingTextView.text =
                    application.getContext()
                        .getString(R.string.rating_value, stop.averageRating * 5f)
            }

            if (stop.timeEffort == null || stop.timeEffort <= 0) {
                holder.stopListTimeEffortTextView.text =
                    application.getContext().getString(R.string.no_value)
            } else {
                holder.stopListTimeEffortTextView.text =
                    application.getContext()
                        .getString(R.string.hours_unit, stop.timeEffort)
            }

            if (stop.pricing == null || stop.pricing <= 0) {
                holder.stopListPricingTextView.text =
                    application.getContext().getString(R.string.no_value)
            } else {
                holder.stopListPricingTextView.text = stop.pricing.toString()
            }

            if (holder.stopListImageView.drawable == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (stop.image != null) {
                        withContext(Dispatchers.Main) {
                            val bitmap: Bitmap? = cityApi.getImage(stop.image)
                            if (bitmap != null) {
                                holder.stopListImageView.setImageBitmap(bitmap)
                                holder.stopListProgressIndicator.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        private inner class ViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var stopListImageView: ImageView = itemView.findViewById(R.id.stopListImageView)
            var stopListIndicatorView: ImageView = itemView.findViewById(R.id.stopListIndicatorView)
            var stopListName: TextView = itemView.findViewById(R.id.stopListName)
            var stopListRatingBar: RatingBar = itemView.findViewById(R.id.stopListRatingBar)
            var stopListRatingTextView: TextView =
                itemView.findViewById(R.id.stopListRatingTextView)
            var stopListDescription: TextView = itemView.findViewById(R.id.stopListDescription)
            var stopListProgressIndicator: ProgressBar =
                itemView.findViewById(R.id.stopListProgressIndicator)
            var stopListCategoryTextView: TextView =
                itemView.findViewById(R.id.stopListCategoryTextView)
            var stopListTimeEffortTextView: TextView =
                itemView.findViewById(R.id.stopListTimeEffortTextView)
            var stopListPricingTextView: TextView =
                itemView.findViewById(R.id.stopListPricingTextView)


        }
    }
}
