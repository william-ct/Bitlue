package com.carlosmesquita.technicaltest.n26.bitlue.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carlosmesquita.technicaltest.n26.bitlue.databinding.ActivityMainBinding
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CleverTapAPI
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity()/*, CTInboxListener*/ {

  private lateinit var binding: ActivityMainBinding
  lateinit var clevertapDefaultInstance: CleverTapAPI

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)


    initClevertap()
  }

  private fun initClevertap() {
    //Set Debug level for CleverTap
    CleverTapAPI.setDebugLevel(3)
    clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)!!
//    clevertapDefaultInstance.apply {
//      ctNotificationInboxListener = this@MainActivity
//      //Initialize the inbox and wait for callbacks on overridden methods
//      initializeInbox()
//    }
  }

//  override fun inboxDidInitialize() {
//    Timber.i("inboxDidInitialize() called")
////    clevertapDefaultInstance.showAppInbox()
//  }
//
//  override fun inboxMessagesDidUpdate() {
//    Timber.i("inboxMessagesDidUpdate() called")
//  }
}