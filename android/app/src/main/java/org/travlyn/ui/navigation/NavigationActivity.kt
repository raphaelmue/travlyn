package org.travlyn.ui.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.travlyn.R
import org.travlyn.api.model.ExecutionInfo
import org.travlyn.api.model.Trip

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val bundle = intent.extras
        if (bundle != null && bundle.containsKey("trip")) {
            val trip: Trip = Gson().fromJson(bundle.getString("trip"), Trip::class.java)
            val executionInfo =
                Gson().fromJson(bundle.getString("executionInfo"), ExecutionInfo::class.java)
        }
    }

}
