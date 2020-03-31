package org.travlyn.ui.trips

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import org.travlyn.api.model.Stop
import org.travlyn.api.model.Trip


class TripInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_information)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val bundle = intent.extras
        if (bundle != null && bundle.containsKey("trip")) {
            val trip: Trip = Gson().fromJson(bundle.getString("trip"), Trip::class.java)

            tripInformationNameTextView.text = trip.name
            if (!trip.private!!) {
                tripInformationNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            tripInformationCityTextView.text = trip.city!!.name
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class TripInformationStopAdapter(
        private val stops: List<Stop>,
        private val context: Context
    ) : RecyclerView.Adapter<TripInformationStopAdapter.ViewHolder>() {

        private val cityApi = CityApi()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(context)
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
                holder.stopListIndicatorView.setImageDrawable(context.getDrawable(R.drawable.stop_list_indicator_start))
            }

            if (position == stops.size - 1) {
                holder.stopListIndicatorView.setImageDrawable(context.getDrawable(R.drawable.stop_list_indicator_end))
            }

            val stringIdentifier = context.resources.getIdentifier(
                "category_" + stop.category?.name, "string", context.packageName
            )
            holder.stopListCategoryTextView.text =
                if (stringIdentifier > 0) context.getString(stringIdentifier) else stop.category?.name

            if (stop.averageRating == null || stop.averageRating <= 0) {
                holder.stopListRatingTextView.text = context.getString(R.string.no_value)
            } else {
                holder.stopListRatingBar.rating = stop.averageRating.toFloat() * 5f
                holder.stopListRatingTextView.text =
                    context.getString(R.string.rating_value, stop.averageRating * 5f)
            }

            if (stop.timeEffort == null || stop.timeEffort <= 0) {
                holder.stopListTimeEffortTextView.text = context.getString(R.string.no_value)
            } else {
                holder.stopListTimeEffortTextView.text =
                    context.getString(R.string.hours_unit, stop.timeEffort.toString())
            }

            if (stop.pricing == null || stop.pricing <= 0) {
                holder.stopListPricingTextView.text = context.getString(R.string.no_value)
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
