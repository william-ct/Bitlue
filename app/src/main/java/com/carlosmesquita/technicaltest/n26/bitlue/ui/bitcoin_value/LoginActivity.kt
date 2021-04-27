package com.carlosmesquita.technicaltest.n26.bitlue.ui.bitcoin_value

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carlosmesquita.technicaltest.n26.bitlue.databinding.ActivityLoginBinding
import com.carlosmesquita.technicaltest.n26.bitlue.ui.MainActivity
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.product_config.CTProductConfigListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), CTProductConfigListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var cleverTapAPI: CleverTapAPI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClevertapApi()

        binding.loginUser.setOnClickListener {
            updateProfileData()
        }
    }

    private fun initClevertapApi() {
        cleverTapAPI = CleverTapAPI.getDefaultInstance(applicationContext)!!
        CleverTapAPI.setDebugLevel(3)
        cleverTapAPI.apply {
            enableDeviceNetworkInfoReporting(true)
            //Set the Product Config Listener
            setCTProductConfigListener(this@LoginActivity)
            //Set Feature Flags Listener
//            setCTFeatureFlagsListener(this@HomeScreenActivity)
        }
        setProductConfigDefaults()
    }

    private fun setProductConfigDefaults() {
        val map: HashMap<String, Any> = HashMap()
        map["welcomeMessageCaps"] = false
        map["welcomeMessage"] = "Welcome to Bitblue"
        map["loginBtnTxt"] = "Login"
        cleverTapAPI.productConfig().setDefaults(map)

        binding.welcomeTitle.text = map["welcomeMessage"].toString()
        binding.welcomeTitle.isAllCaps = map["welcomeMessageCaps"] as Boolean
        binding.loginUser.text = map["loginBtnTxt"].toString()
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

    override fun onActivated() {
        Timber.wtf("onActivated() called")
        binding.welcomeTitle.text = cleverTapAPI.productConfig().getString("welcomeMessage")
        binding.welcomeTitle.isAllCaps = cleverTapAPI.productConfig().getBoolean("welcomeMessageCaps")

        binding.loginUser.text = cleverTapAPI.productConfig().getString("loginBtnTxt")
    }

    override fun onFetched() {
        Timber.wtf("onFetched() called")
    }

    override fun onInit() {
        Timber.wtf("onInit() called")
        cleverTapAPI.productConfig().fetchAndActivate()
    }

}