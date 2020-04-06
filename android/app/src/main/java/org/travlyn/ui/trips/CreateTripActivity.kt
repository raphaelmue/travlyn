package org.travlyn.ui.trips

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_trip.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.travlyn.R
import org.travlyn.api.TripApi
import org.travlyn.api.model.Trip
import org.travlyn.local.LocalStorage


class CreateTripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)

        val toolbar: Toolbar = findViewById(R.id.tripToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.trip_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.tripSaveMenuItem -> {
                createTrip()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createTrip() {
        if (tripNameTextEdit.text.isNullOrBlank()) {
            tripNameTextEdit.error = getString(R.string.error_field_required)
            return
        }

        val trip = Trip(
            name = tripNameTextEdit.text.toString(),
            private = !isPublicCheckBox.isChecked,
            user = LocalStorage(this).readObject("user")
        )

        val bundle = intent.extras
        var stopIds: Array<Int> = arrayOf()
        var cityId: Long = -1
        if (bundle != null) {
            if (bundle.containsKey("stopIds")) {
                stopIds = bundle.get("stopIds") as Array<Int>
            }
            if (bundle.containsKey("cityId")) {
                cityId = bundle.getLong("cityId")
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val tripResult = TripApi().generateTrip(
                trip.user?.id!!,
                cityId,
                trip.name!!,
                trip.private!!,
                stopIds
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@CreateTripActivity,
                    getString(R.string.successfully_created_trip),
                    Toast.LENGTH_LONG
                ).show()
                setResult(Activity.RESULT_OK, Intent().putExtra("trip", Gson().toJson(tripResult)))
                finish()
            }
        }
    }
}