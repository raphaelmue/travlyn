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
import org.travlyn.local.LocalStorage
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs


class LoginActivity : AppCompatActivity(), CoroutineScope {
    private val tag: String = "LoginActivity"

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val animationTime: Long = 500

    var api: UserApi = UserApi()

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: MaterialButton
    private lateinit var progressIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        Log.v(tag, "Checking credentials...")
        toggleProgressIndicator()
        launch {
            val user: User? = handleLoginRequest()
            if (user != null) {
                Log.v(tag, "Credentials are approved. User [${user.id}] is logged into the system.")
                LocalStorage(context).writeObject("user", user)
                toggleProgressIndicator()
                context.finish()
            } else {
                Log.v(tag, "Credentials are invalid.")
                toggleProgressIndicator()
            }
        }
    }

    private suspend fun handleLoginRequest(): User {
        val user: User = api.loginUser(etEmail.text.toString(), etPassword.text.toString())
        delay(2000)
        return user
    }

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

