package android.kotlin.app.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.kotlin.app.R
import android.kotlin.app.databinding.FragmentEggTimerBinding
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
class EggTimerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentEggTimerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_egg_timer, container, false
        )

        val viewModel = ViewModelProviders.of(this).get(EggTimerViewModel::class.java)

        binding.eggTimerViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        createChannel(
            getString(R.string.egg_notification_channel_id),
            getString(R.string.egg_notification_channel_name)
        )

        return binding.root
    }

    private fun createChannel(channelId : String , channelName : String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )// TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.breakfast_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }
    companion object {
        fun newInstance() = EggTimerFragment()
    }
}