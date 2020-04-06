package org.travlyn.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_city_information.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.travlyn.MainActivity
import org.travlyn.R
import org.travlyn.api.CityApi
import org.travlyn.api.model.City
import org.travlyn.api.model.Stop
import org.travlyn.api.model.Trip
import org.travlyn.ui.trips.TripInformationActivity


class CityInformationFragment : SuperBottomSheetFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_city_information, container, false)
    }

    private lateinit var cityApi: CityApi

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cityApi = CityApi(application = activity as MainActivity)

        context?.getColor(R.color.white)
            ?.let { cityCollapsingToolbar.setCollapsedTitleTextColor(it) }

        cityDescriptionTextView.setOnClickListener {
            cityDescriptionTextView.maxLines = if (cityDescriptionTextView.maxLines == 4) {
                cityDescriptionTextView.ellipsize = null
                Integer.MAX_VALUE
            } else {
                cityDescriptionTextView.ellipsize = TextUtils.TruncateAt.END
                4
            }
        }

        val city: City? = Gson().fromJson(arguments?.get("city") as String?, City::class.java)

        cityShowStopsButton.setOnClickListener {
            val intent = Intent(context, StopsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("city", Gson().toJson(city))
            intent.putExtras(bundle)
            startActivity(intent)
        }

        if (city != null) {
            cityInformationToolbar.title = city.name
            cityCollapsingToolbar.title = city.name
            cityDescriptionTextView.text = city.description
            initializeStops(city)

            fetchThumbnail(city)
            fetchTrips(city)
        }
    }

    private fun fetchThumbnail(city: City) {
        CoroutineScope(Dispatchers.IO).launch {
            if (city.image != null) {
                withContext(Dispatchers.Main) {
                    cityThumbnailImageView.setImageBitmap(
                        cityApi.getImage(
                            city.image
                        )
                    )
                }
            }
        }
    }

    private fun fetchTrips(city: City) {
        CoroutineScope(Dispatchers.IO).launch {
            if (city.id != null) {
                initializeTrips(
                    cityApi.getPublicTripsForCity(cityId = city.id).asList()
                )
            }
        }
    }

    private suspend fun initializeTrips(trips: List<Trip>) {
        withContext(Dispatchers.Main) {
            tripRecyclerViewProgressBar.visibility = View.GONE

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            tripsListRecyclerView.layoutManager = layoutManager

            if (context != null && trips.isNotEmpty()) {
                val adapter = TripCardViewAdapter(trips, context!!)
                tripsListRecyclerView.adapter = adapter
            } else {
                emptyTripsTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun initializeStops(city: City) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        stopCardRecyclerView.layoutManager = layoutManager

        if (context != null && city.stops != null) {
            val adapter = StopCardViewAdapter(city.stops.toList(), context!!)
            stopCardRecyclerView.adapter = adapter
        }
    }

    override fun getDim(): Float {
        return 0f
    }

    companion object {
        @JvmStatic
        fun newInstance(city: City?) = CityInformationFragment().apply {
            arguments = Bundle().apply {
                putString("city", Gson().toJson(city))
            }
        }
    }

    private class StopCardViewAdapter(private val stops: List<Stop>, private val context: Context) :
        RecyclerView.Adapter<StopCardViewAdapter.ViewHolder>() {

        private val cityApi = CityApi(application = context as MainActivity)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.stop_card_view, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return stops.size;
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val stop: Stop = stops[position]

            holder.stopCardName.text = stop.name
            holder.stopCardDescription.text = stop.description

            if (holder.stopCardImageView.drawable == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (stop.image != null) {
                        withContext(Dispatchers.Main) {
                            val bitmap: Bitmap? = cityApi.getImage(stop.image)
                            if (bitmap != null) {
                                holder.stopCardImageView.setImageBitmap(bitmap)
                                holder.stopCardProgressIndicator.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        private inner class ViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var stopCardImageView: ImageView = itemView.findViewById(R.id.stopCardImageView)
            var stopCardName: TextView = itemView.findViewById(R.id.stopCardName)
            var stopCardDescription: TextView = itemView.findViewById(R.id.stopCardDescription)
            var stopCardProgressIndicator: ProgressBar =
                itemView.findViewById(R.id.stopCardProgressIndicator)
        }
    }

}

open class TripCardViewAdapter(
    protected val trips: List<Trip>,
    private val context: Context
) : RecyclerView.Adapter<TripCardViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context)
                .inflate(R.layout.city_information_trip_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip: Trip = trips[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(context, TripInformationActivity::class.java)
            intent.putExtras(bundleOf("trip" to Gson().toJson(trip)))
            context.startActivity(intent)
        }

        holder.tripCardNameTextView.text = trip.name
        holder.tripCardNumberOfStopsTextView.text = trip.stops?.size.toString()

        if (trip.user != null) {
            holder.tripCardCreatedByTextView.text =
                context.getString(R.string.created_by, trip.user.name)
        } else {
            holder.tripCardCreatedByTextView.visibility = View.GONE
        }

        if (trip.averageRating == null || trip.averageRating <= 0) {
            holder.tripCardRatingTextView.text = context.getString(R.string.no_value)
        } else {
            holder.tripCardRatingBar.rating = trip.averageRating.toFloat() * 5f
            holder.tripCardRatingTextView.text =
                context.getString(R.string.rating_value, trip.averageRating * 5f)
        }
    }

    open inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tripCardNameTextView: TextView = itemView.findViewById(R.id.tripCardNameTextView)
        var tripCardRatingBar: RatingBar = itemView.findViewById(R.id.tripCardRatingBar)
        var tripCardRatingTextView: TextView =
            itemView.findViewById(R.id.tripCardRatingTextView)
        var tripCardCreatedByTextView: TextView =
            itemView.findViewById(R.id.tripCardCreatedByTextView)
        val tripCardNumberOfStopsTextView: TextView =
            itemView.findViewById(R.id.tripCardNumberOfStopsTextView)
    }
}
