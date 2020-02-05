package org.travlyn.ui.login

import android.os.Bundle
import android.util.Log
import android.view.animation.AnticipateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import org.travlyn.R
import org.travlyn.api.UserApi
import org.travlyn.api.model.User
import org.travlyn.infrastructure.ServerException
import org.travlyn.local.LocalStorage
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs


class LoginActivity : AppCompatActivity(), CoroutineScope {
    private val tag: String = "LoginActivity"

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private val animationTime: Long = 500

    var api: UserApi = UserApi()

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: MaterialButton
    private lateinit var progressIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initialize elements on view
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnSignIn = findViewById(R.id.btn_sign_in)
        progressIndicator = findViewById(R.id.pb_login)

        val context = this
        btnSignIn.setOnClickListener {
            handleLogin(context)
        }
    }

    private fun handleLogin(context: AppCompatActivity) {
        var error: Boolean = false

        // check whether fields are empty
        if (etEmail.text == null || etEmail.text!!.isEmpty()) {
            etEmail.error = getString(R.string.error_field_required)
            error = true
        }
        if (etPassword.text == null || etPassword.text!!.isEmpty()) {
            etPassword.error = getString(R.string.error_field_required)
            error = true
        }

        // stop processing if error occurred
        if (error) return

        Log.v(tag, "Checking credentials...")
        toggleProgressIndicator()
        launch {
            val user: User? = handleLoginRequest()
            if (user != null) {
                Log.v(tag, "Credentials are approved. User [${user.id}] is logged into the system.")
                LocalStorage(context).writeObject("user", user)
                context.finish()
            } else {
                Log.v(tag, "Credentials are invalid.")
                etEmail.error = getString(R.string.error_invalid_credentials)
                etPassword.error = getString(R.string.error_invalid_credentials)

            }
            toggleProgressIndicator()
        }
    }

    private suspend fun handleLoginRequest(): User? {
        delay(2000)
        return try {
            api.loginUser(etEmail.text.toString(), etPassword.text.toString())
        } catch (e: ServerException) {
            null
        }
    }

    /**
     * Toggles whether to show the progress indicator and hide the sign in button and vice versa.
     *
     * @return void
     */
    private fun toggleProgressIndicator() {
        val interpolator: Interpolator = if (btnSignIn.scaleX <= 0f) {
            OvershootInterpolator()
        } else {
            AnticipateInterpolator()
        }

        btnSignIn.animate()
            .scaleX(abs(btnSignIn.scaleX - 1))
            .scaleY(abs(btnSignIn.scaleY - 1))
            .setDuration(animationTime)
            .setInterpolator(interpolator)
            .setListener(null)

        progressIndicator.animate()
            .scaleX(abs(progressIndicator.scaleX - 1))
            .scaleY(abs(progressIndicator.scaleY - 1))
            .setDuration(animationTime)
            .setInterpolator(interpolator)
            .setListener(null)
    }
}

