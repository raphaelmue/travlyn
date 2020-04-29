package org.travlyn.ui.login

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.travlyn.R
import org.travlyn.api.UserApi
import org.travlyn.api.model.User
import org.travlyn.local.Application
import org.travlyn.local.LocalStorage

class RegisterActivity : AppCompatActivity(), Application {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val api = UserApi(application = this)

        registerBtn.setOnClickListener {
            val email = registerEmailTextEdit.text.toString()
            val name = registerNameTextEdit.text.toString()
            val password = registerPasswordTextEdit.text.toString()

            if (email.isBlank()) {
                registerEmailTextEdit.error = getString(R.string.error_field_required)
            }
            if (name.isBlank()) {
                registerNameTextEdit.error = getString(R.string.error_field_required)
            }
            if (password.isBlank()) {
                registerPasswordTextEdit.error = getString(R.string.error_field_required)
            }
            if (password.length <= 4) {
                registerPasswordTextEdit.error = getString(R.string.error_password_too_short)
            }

            CoroutineScope(Dispatchers.IO).launch {
                val user: User? = api.registerUser(email, name, password)
                if (user != null) {
                    withContext(Dispatchers.Main) {
                        LocalStorage(this@RegisterActivity).writeObject("user", user)
                        finish()
                    }
                }
            }
        }
    }

    override fun getContext(): Context {
       return this
    }
}
