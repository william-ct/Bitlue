package com.carlosmesquita.technicaltest.n26.bitlue.ui.bitcoin_value

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.carlosmesquita.technicaltest.n26.bitlue.BuildConfig
import com.carlosmesquita.technicaltest.n26.bitlue.R
import com.carlosmesquita.technicaltest.n26.bitlue.databinding.ActivityLoginBinding
import com.carlosmesquita.technicaltest.n26.bitlue.ui.MainActivity
import com.carlosmesquita.technicaltest.n26.bitlue.utils.action
import com.carlosmesquita.technicaltest.n26.bitlue.utils.snack
import com.clevertap.android.geofence.CTGeofenceAPI
import com.clevertap.android.geofence.CTGeofenceSettings
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.product_config.CTProductConfigListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

private const val PERMISSIONS_REQUEST_CODE = 34

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

        binding.requestLocation.setOnClickListener {
            when {
                !checkPermissions() -> requestPermissions()
                else -> initCTGeofenceApi(cleverTapAPI)
            }
        }
    }

    private fun initClevertapApi() {
        cleverTapAPI = CleverTapAPI.getDefaultInstance(applicationContext)!!
        CleverTapAPI.setDebugLevel(3)
        cleverTapAPI.apply {
            enableDeviceNetworkInfoReporting(true)
            setCTProductConfigListener(this@LoginActivity)
        }
        setProductConfigDefaults()
    }

    private fun initCTGeofenceApi(cleverTapInstance: CleverTapAPI) {

//        CTGeofenceAPI.getInstance(this).apply {
//            init(
//                CTGeofenceSettings.Builder()
//                    .enableBackgroundLocationUpdates(true)
//                    .setLogLevel(com.clevertap.android.geofence.Logger.DEBUG)
//                    .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)
//                    .setLocationFetchMode(CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC)
//                    .setGeofenceMonitoringCount(99)
//                    .setInterval(3600000) // 1 hour
//                    .setFastestInterval(1800000) // 30 minutes
//                    .setSmallestDisplacement(1000f) // 1 km
//                    .setGeofenceNotificationResponsiveness(300000) // 5 minute
//                    .build(), cleverTapInstance
//            )
//            setOnGeofenceApiInitializedListener {
//                Toast.makeText(this@LoginActivity, "Geofence API initialized", Toast.LENGTH_SHORT).show()
//            }
//            setCtGeofenceEventsListener(object : CTGeofenceEventsListener {
//                override fun onGeofenceEnteredEvent(jsonObject: JSONObject) {
//                    Toast.makeText(this@LoginActivity, "Geofence Entered", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onGeofenceExitedEvent(jsonObject: JSONObject) {
//                    Toast.makeText(this@LoginActivity, "Geofence Exited", Toast.LENGTH_SHORT).show()
//                }
//            })
//            setCtLocationUpdatesListener { Toast.makeText(this@LoginActivity, "Location updated", Toast.LENGTH_SHORT).show() }
//        }

        val ctGeofenceSettings = CTGeofenceSettings.Builder()
            .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
            .setLogLevel(com.clevertap.android.geofence.Logger.DEBUG)//Log Level
            .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)//byte value for Location Accuracy
            .setLocationFetchMode(CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC)//byte value for Fetch Mode
            .setGeofenceMonitoringCount(99)//int value for number of Geofences CleverTap can monitor
            .setInterval(3600000)//long value for interval in milliseconds
            .setFastestInterval(1800000)//long value for fastest interval in milliseconds
            .setSmallestDisplacement(1000f)//float value for smallest Displacement in meters
            .setGeofenceNotificationResponsiveness(300000)// int value for geofence notification responsiveness in milliseconds
            .build()

        CTGeofenceAPI.getInstance(applicationContext).init(ctGeofenceSettings, cleverTapInstance)

        CTGeofenceAPI.getInstance(applicationContext)
            .setOnGeofenceApiInitializedListener {
                //App is notified on the main thread that CTGeofenceAPI is initialized
                Toast.makeText(this@LoginActivity, "Geofence API initialized", Toast.LENGTH_SHORT).show()
            }

        CTGeofenceAPI.getInstance(applicationContext)
            .setCtGeofenceEventsListener(object: CTGeofenceEventsListener {
                override fun onGeofenceEnteredEvent(jsonObject:JSONObject) {
                    //Callback on the main thread when the user enters Geofence with info in jsonObject
                    Toast.makeText(this@LoginActivity, "Geofence Entered", Toast.LENGTH_SHORT).show()
                }
                override fun onGeofenceExitedEvent(jsonObject:JSONObject) {
                    //Callback on the main thread when user exits Geofence with info in jsonObject
                    Toast.makeText(this@LoginActivity, "Geofence Exited", Toast.LENGTH_SHORT).show()
                }
            })
    }


    @SuppressLint("InlinedApi")
    private fun requestPermissions() {
        val permissionAccessFineLocationApproved = (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)

        val backgroundLocationPermissionApproved = (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)

        val shouldProvideRationale = permissionAccessFineLocationApproved && backgroundLocationPermissionApproved

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            binding.root.snack(R.string.permission_rationale) {
                action(R.string.ok) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        PERMISSIONS_REQUEST_CODE
                    )
                }
            }
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> println("user permission interaction was interrupted")
                grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                -> initCTGeofenceApi(cleverTapAPI)
                else -> {
                    binding.root.snack(R.string.permission_denied_explanation) {
                        action(R.string.settings) {

                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })

                        }
                    }
                }

            }
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val fineLocationPermissionState = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val backgroundLocationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        else PackageManager.PERMISSION_GRANTED

        return fineLocationPermissionState == PackageManager.PERMISSION_GRANTED &&
                backgroundLocationPermissionState == PackageManager.PERMISSION_GRANTED
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