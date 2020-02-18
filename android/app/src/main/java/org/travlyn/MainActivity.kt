package org.travlyn

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.travlyn.api.UserApi
import org.travlyn.api.model.User
import org.travlyn.local.Application
import org.travlyn.local.Formatter
import org.travlyn.local.LocalStorage
import org.travlyn.ui.login.LoginActivity
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope, Application {
    val tag: String = "MainActivity"

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    var api: UserApi = UserApi(application = this)

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var user: User? = null
    private var formatter: Formatter = Formatter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        user = LocalStorage(this).readObject<User>("user")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            // show sign in when user is not signed in and hide otherwise
            menu.findItem(R.id.menu_sign_in).isVisible = (user == null)
            // show logout when user is logged in and hide otherwise
            menu.findItem(R.id.menu_logout).isVisible = (user != null)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_sign_in -> {
                openLoginActivity()
                true
            }
            R.id.menu_logout -> {
                handleLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(tag, "Permission is granted")
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
            Log.v(tag, "Permission is revoked")
        }

        user = LocalStorage(this).readObject("user")
        if (user != null) {
//            emailNavTextView.text = user!!.email
//            nameNavTextView.text = user!!.name
        }
        invalidateOptionsMenu()
    }

    private fun openLoginActivity() {
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun handleLogout() {
        val context = this
        val snackBar = Snackbar.make(
            window.decorView,
            getString(R.string.successfully_logged_out),
            2000
        ).setAction(getString(R.string.undo)) {}
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)

                    Log.v(tag, "Logging user out")
                    launch {
                        handleLogoutRequest()
                        user = null
                    }
                    LocalStorage(context).deleteObject("user")
                    invalidateOptionsMenu()
                }
            })
        snackBar.show()
    }

    private suspend fun handleLogoutRequest() {
        api.logoutUser(this.user!!)
    }

    override fun showErrorDialog(throwable: Throwable) {
        Log.e(tag, throwable.message, throwable)
        AlertDialog.Builder(this)
            .setTitle("Travlyn")
            .setMessage(formatter.format(throwable))
            .setPositiveButton(R.string.ok, null)
            .setIcon(R.drawable.ic_error)
            .show()
    }

    override fun getContext(): Context {
        return this
    }

    override fun getFormatter(): Formatter {
        return formatter
    }
}