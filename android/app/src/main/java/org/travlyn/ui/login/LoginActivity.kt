package org.travlyn.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.animation.AnticipateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_login.*
import kotlinx.coroutines.*
import org.travlyn.R
import org.travlyn.api.UserApi
import org.travlyn.api.model.User
import org.travlyn.local.Application
import org.travlyn.local.LocalStorage
import kotlin.math.abs


class LoginActivity : AppCompatActivity(), Application {
    private val animationTime: Long = 500

    var api: UserApi = UserApi(context = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val context = this
        signInBtn.setOnClickListener {
            handleLogin(context)
        }
    }

    private fun handleLogin(context: AppCompatActivity) {
        // close virtual keyboard
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            passwordTextEdit.windowToken,
            InputMethodManager.RESULT_UNCHANGED_SHOWN
        )

        var error = false

        // check whether fields are empty
        if (emailTextEdit.text == null || emailTextEdit.text!!.isEmpty()) {
            emailTextEdit.error = getString(R.string.error_field_required)
            error = true
        }
        if (passwordTextEdit.text == null || passwordTextEdit.text!!.isEmpty()) {
            passwordTextEdit.error = getString(R.string.error_field_required)
            error = true
        }

        // stop processing if error occurred
        if (error) return

        Log.v(tag, "Checking credentials...")
        toggleProgressIndicator()
        CoroutineScope(Dispatchers.IO).launch {
            val user: User? = handleLoginRequest()
            if (user != null) {
                Log.v(tag, "Credentials are approved. User [${user.id}] is logged into the system.")
                LocalStorage(context).writeObject("user", user)
                context.finish()
            } else {
                Log.v(tag, "Credentials are invalid.")
                withContext(Dispatchers.Main) {
                    emailTextEdit.error = getString(R.string.error_invalid_credentials)
                    passwordTextEdit.error = getString(R.string.error_invalid_credentials)
                    toggleProgressIndicator()
                }
            }
        }
    }

    private suspend fun handleLoginRequest(): User? {
        delay(2000)
        return api.loginUser(emailTextEdit.text.toString(), passwordTextEdit.text.toString())
    }

    /**
     * Toggles whether to show the progress indicator and hide the sign in button and vice versa.
     *
     * @return void
     */
    private fun toggleProgressIndicator() {
        val interpolator: Interpolator = if (signInBtn.scaleX <= 0f) {
            OvershootInterpolator()
        } else {
            AnticipateInterpolator()
        }

        signInBtn.animate()
            .scaleX(abs(signInBtn.scaleX - 1))
            .scaleY(abs(signInBtn.scaleY - 1))
            .setDuration(animationTime)
            .setInterpolator(interpolator)
            .setListener(null)

        loginProgressBar.animate()
            .scaleX(abs(loginProgressBar.scaleX - 1))
            .scaleY(abs(loginProgressBar.scaleY - 1))
            .setDuration(animationTime)
            .setInterpolator(interpolator)
            .setListener(null)
    }

    override fun getContext(): Context {
        return this
    }

}

