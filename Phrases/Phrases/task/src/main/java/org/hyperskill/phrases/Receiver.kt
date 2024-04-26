package org.hyperskill.phrases

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val intent1 = Intent(context, MainActivity::class.java)
        val pIntent =
            PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(context, "org.hyperskill.phrases")
            .setSmallIcon(R.drawable.ic_balance)
            .setContentTitle("Your phrase of the day")
            .setContentText(intent.getStringExtra("phrase"))
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pIntent)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(393939, notificationBuilder.build())
    }
}