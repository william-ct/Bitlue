package com.carlosmesquita.technicaltest.n26.bitlue.ui.bitcoin_value

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.carlosmesquita.technicaltest.n26.bitlue.BuildConfig
import com.carlosmesquita.technicaltest.n26.bitlue.R
import com.carlosmesquita.technicaltest.n26.bitlue.databinding.FragmentBitcoinValueBinding
import com.carlosmesquita.technicaltest.n26.bitlue.ui.MainActivity
import com.carlosmesquita.technicaltest.n26.bitlue.ui.MainViewModel
import com.carlosmesquita.technicaltest.n26.bitlue.ui.actions.MainEvents.BitcoinValueEvents
import com.carlosmesquita.technicaltest.n26.bitlue.ui.actions.MainStates.BitcoinValueStates
import com.clevertap.android.sdk.CTFeatureFlagsListener
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CTInboxStyleConfig
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.displayunits.DisplayUnitListener
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class BitcoinValueFragment : Fragment(R.layout.fragment_bitcoin_value), CTInboxListener,
    CTFeatureFlagsListener,DisplayUnitListener {

    private val viewModel: MainViewModel by activityViewModels()

    private val binding: FragmentBitcoinValueBinding
        get() = _binding!!
    private var _binding: FragmentBitcoinValueBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBitcoinValueBinding.bind(view).apply {
            lifecycleOwner = this@BitcoinValueFragment.viewLifecycleOwner
            viewModel = this@BitcoinValueFragment.viewModel
        }

        collectUIStates()

        val inboxUnreadCount = (activity as? MainActivity)?.clevertapDefaultInstance?.inboxMessageUnreadCount
        if (inboxUnreadCount != null) {
            if (inboxUnreadCount >= 0)
               binding.appInboxUnreadCountIndicator.visibility = View.VISIBLE
            else
                binding.appInboxUnreadCountIndicator.visibility = View.GONE
        }

        viewModel.bitcoinValues.observe(viewLifecycleOwner) {
            binding.valuesChart.show(it)
        }

        binding.themeToggle.setOnClickListener {
            if ((activity as? MainActivity)?.clevertapDefaultInstance?.featureFlag()?.get("isDarkTheme", false) == true){
                sendEvent(BitcoinValueEvents.OnForcedDarkThemeToggle)
            }else {
                sendEvent(BitcoinValueEvents.OnThemeToggleClicked)
            }
//            (activity as? MainActivity)?.clevertapDefaultInstance?.decrementValue("double_score1",10.25)
        }

        binding.fab.setOnClickListener {
            sendEvent(BitcoinValueEvents.OnFabClicked)
        }

        binding.appInbox.setOnClickListener {
            initAppInbox((activity as? MainActivity)?.clevertapDefaultInstance)
//            (activity as? MainActivity)?.clevertapDefaultInstance?.incrementValue("double_score1",10.25)
        }
    }

    private fun initAppInbox(cleverTapInstance: CleverTapAPI?) {
        cleverTapInstance.apply {
          this!!.ctNotificationInboxListener = this@BitcoinValueFragment
            initializeInbox()

            setCTFeatureFlagsListener(this@BitcoinValueFragment)

            setDisplayUnitListener(this@BitcoinValueFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun collectUIStates() = lifecycleScope.launchWhenStarted {
        viewModel.states.collect {
            Timber.i("State received => ${it::class.java.name}")

            if (it !is BitcoinValueStates) {
                return@collect
            }

            when (it) {
                BitcoinValueStates.OpenFilterSettings -> {
                    findNavController().navigate(R.id.to_filterSettingsDialogFragment)
                }

                BitcoinValueStates.ShowLoading -> {
                    binding.loadingBar.isVisible = true
                }

                BitcoinValueStates.HideLoading -> {
                    binding.loadingBar.isVisible = false
                }

                BitcoinValueStates.ShowFAB -> {
                    binding.fab.show()
                }

                BitcoinValueStates.HideFAB -> {
                    binding.fab.hide()
                }

                else -> {
                    if (BuildConfig.DEBUG) {
                        throw IllegalStateException(
                            "Unknown BitcoinValueStates instance: ${it::class.java.simpleName}"
                        )
                    }
                }
            }
        }
    }

    private fun sendEvent(event: BitcoinValueEvents) = lifecycleScope.launch {
        viewModel.eventsChannel.send(event)
    }

    private fun showErrorDialog(detailsMessage: String) {
        MaterialAlertDialogBuilder(context ?: return)
            .setTitle(R.string.title_unexpected_error)
            .setMessage(getString(R.string.message_unexpected_error, detailsMessage))
            .setPositiveButton(R.string.action_ok, null)
            .show()
    }

    override fun inboxDidInitialize() {
        Timber.i("inboxDidInitialize() called")

        val tabs = ArrayList<String>()
        tabs.add("Promotions")
        val styleConfig = CTInboxStyleConfig()
        styleConfig.tabs = tabs//Do not use this if you don't want to use tabs
        styleConfig.tabBackgroundColor = "#6f3a4b"//provide Hex code in string ONLY
        styleConfig.selectedTabIndicatorColor = "#ffffff"
        styleConfig.selectedTabColor = "#ffffff"
        styleConfig.unselectedTabColor = "#ea9aae"
        styleConfig.backButtonColor = "#121212"
        styleConfig.navBarTitleColor = "#121212"
        styleConfig.navBarTitle = "My Inbox"
        styleConfig.navBarColor = "#ffffff"
        styleConfig.inboxBackgroundColor = "#C8C8C8"
        (activity as? MainActivity)?.clevertapDefaultInstance?.showAppInbox(styleConfig)
    }

    override fun inboxMessagesDidUpdate() {
        Timber.wtf("inboxMessagesDidUpdate() called")
    }

    override fun featureFlagsUpdated() {
        Timber.wtf("featureFlagsUpdated() called")
    }

    override fun onDisplayUnitsLoaded(units: java.util.ArrayList<CleverTapDisplayUnit>?) {
        Timber.wtf("onDisplayUnitsLoaded() called"+units?.size)
    }
}
