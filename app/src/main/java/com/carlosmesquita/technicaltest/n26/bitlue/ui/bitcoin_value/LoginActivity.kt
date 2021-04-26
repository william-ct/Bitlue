package com.carlosmesquita.technicaltest.n26.bitlue.ui.bitcoin_value

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.carlosmesquita.technicaltest.n26.bitlue.R
import com.carlosmesquita.technicaltest.n26.bitlue.databinding.ActivityLoginBinding
import com.carlosmesquita.technicaltest.n26.bitlue.databinding.ActivityMainBinding
import com.carlosmesquita.technicaltest.n26.bitlue.ui.MainActivity
import com.clevertap.android.sdk.CleverTapAPI
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginUser.setOnClickListener {
            updateProfileData()
        }
    }

    private fun updateProfileData() {
        val profileUpdate = HashMap<String, Any>()
        profileUpdate["Name"] = "William Jo" // String
        profileUpdate["Identity"] = 61026032 // String or number
        profileUpdate["Email"] = "william@clevertap.com" // Email address of the user
        profileUpdate["Phone"] = "+14155551234" // Phone (with the country code, starting with +)
        profileUpdate["Gender"] = "M" // Can be either M or F
        profileUpdate["DOB"] = "10/7/90" // Date of Birth. Set the Date object to the appropriate value first
// optional fields. controls whether the user will be sent email, push etc.
// optional fields. controls whether the user will be sent email, push etc.
        profileUpdate["MSG-email"] = false // Disable email notifications
        profileUpdate["MSG-push"] = true // Enable push notifications
        profileUpdate["MSG-sms"] = false // Disable SMS notifications
        profileUpdate["MSG-whatsapp"] = true // Enable WhatsApp notifications

        CleverTapAPI.getDefaultInstance(applicationContext)?.onUserLogin(profileUpdate)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}