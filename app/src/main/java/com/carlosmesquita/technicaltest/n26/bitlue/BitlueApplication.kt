package com.carlosmesquita.technicaltest.n26.bitlue

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BitlueApplication : Application() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        ActivityLifecycleCallback.register(this)

//        StrictMode.setThreadPolicy(
//            ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork() // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build()
//        )
//        StrictMode.setVmPolicy(
//            VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build()
//        )
        super.onCreate()
        CleverTapAPI.createNotificationChannel(
            this, "General", "General",
            "All Offers", NotificationManager.IMPORTANCE_MAX, true
        )
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
