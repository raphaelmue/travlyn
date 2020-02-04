package org.travlyn.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.travlyn.R
import org.travlyn.api.model.User
import org.travlyn.local.LocalStorage

class HomeFragment : Fragment() {

    private lateinit var tvWelcome: TextView
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        tvWelcome = root.findViewById(R.id.tv_welcome)

        return root
    }

    override fun onStart() {
        super.onStart()

        val user: User? = context?.let { LocalStorage(it).readObject<User>("user") }
        if (user != null) {
            tvWelcome.text = getString(R.string.welcome, user.name)
        }
    }
}