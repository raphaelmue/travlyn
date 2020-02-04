package org.travlyn.ui.login

import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import org.travlyn.R
import org.travlyn.api.UserApi
import org.travlyn.api.model.User
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs


class LoginActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val animationTime: Long = 500

    lateinit var api: UserApi

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: MaterialButton
    private lateinit var progressIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        api = UserApi()

        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnSignIn = findViewById(R.id.btn_sign_in)
        progressIndicator = findViewById(R.id.pb_login)

        btnSignIn.setOnClickListener {
            toggleProgressIndicator()
            launch {
                val user = handleLogin()
                if (user != null) {
                    println("login user in ")
                } else {
                    println("wrong credentials")
                }
                toggleProgressIndicator()
            }
        }
    }

    private suspend fun handleLogin(): User {
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

