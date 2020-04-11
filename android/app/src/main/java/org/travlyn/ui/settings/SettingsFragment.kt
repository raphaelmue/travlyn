package org.travlyn.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.*
import org.travlyn.R
import org.travlyn.infrastructure.ApiClient
import org.travlyn.local.LocalStorage

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val localStorage = LocalStorage(context!!)

        if (!localStorage.contains("baseUrl")) {
            localStorage.writeObject("baseUrl", ApiClient.baseUrlDefault)
        }
        settingsHostTextField.setText(localStorage.readObject<String>("baseUrl"))

        saveSettingsBtn.setOnClickListener {
            localStorage.writeObject("baseUrl", settingsHostTextField.text.toString())
            Toast.makeText(context, R.string.successfully_stored_settings, Toast.LENGTH_LONG).show()
        }
    }
}