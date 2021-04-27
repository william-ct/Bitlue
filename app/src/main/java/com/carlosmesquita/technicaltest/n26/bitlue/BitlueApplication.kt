package com.carlosmesquita.technicaltest.n26.bitlue

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BitlueApplication : Application() {

    override fun onCreate() {
        ActivityLifecycleCallback.register(this)
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        //Enable this for getting FCM token ID.
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("App", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            // Log and toast
//            val msg = ""+ token
//            Log.d("App", msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//        })

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}
