package android.kotlin.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.kotlin.app.R
import android.kotlin.app.util.sendNotification
import android.util.Log
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // TODO: Step 1.10 [Optional] remove toast
        Log.e("_______" , "notification created")
        // TODO: Step 1.9 add call to sendNotification
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        Log.e("_____" , "try to call snedNotification")
        notificationManager.sendNotification(
            context.getText(R.string.eggs_ready).toString(),
            context
        )

    }
}