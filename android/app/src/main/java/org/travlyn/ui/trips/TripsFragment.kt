package org.travlyn.ui.trips

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_trips.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.travlyn.R
import org.travlyn.api.UserApi
import org.travlyn.api.model.Trip
import org.travlyn.api.model.User
import org.travlyn.local.LocalStorage
import org.travlyn.ui.home.TripCardViewAdapter

class TripsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createTripFab.setOnClickListener {
            val intent = Intent(context, CreateTripActivity::class.java)
            startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            initializeTripListView(fetchUsersTrips())
        }
    }

    private suspend fun fetchUsersTrips(): List<Trip> {
        if (context != null) {
            val user: User? = LocalStorage(context!!).readObject("user")
            if (user != null) {
                return UserApi().getTripsByUserId(user.id!!).toList()
            } else {
                userNotSignedInTextView.visibility = View.VISIBLE
            }
        }
        return emptyList()
    }

    private suspend fun initializeTripListView(trips: List<Trip>) = withContext(Dispatchers.Main) {
        if (context != null) {
            myTripsListProgressBar.visibility = View.GONE
            myTripsListRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            myTripsListRecyclerView.adapter = TripListAdapter(trips, context!!)
        }
    }

    private inner class TripListAdapter(
        trips: List<Trip>, context: Context
    ) : TripCardViewAdapter(trips, context) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.trip_list_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val trip: Trip = trips[position]

            holder.itemView.setOnClickListener {
                val intent = Intent(context, TripInformationActivity::class.java)
                intent.putExtras(bundleOf("trip" to Gson().toJson(trip)))
                startActivity(intent)
            }

            if (trip.city?.name == null) {
                holder.tripCardCreatedByTextView.visibility = View.GONE
            } else {
                holder.tripCardCreatedByTextView.text = trip.city.name
            }


            if (!trip.private!!) {
                holder.tripCardNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }
}