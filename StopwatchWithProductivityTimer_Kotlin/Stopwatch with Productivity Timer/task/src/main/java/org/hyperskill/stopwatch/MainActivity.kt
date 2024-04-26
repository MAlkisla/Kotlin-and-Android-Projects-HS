package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import org.hyperskill.stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var secondsElapsed: Int = 0
    private var isRunning: Boolean = false
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var progressBarColors: Array<Int>
    private var currentColorIndex: Int = 0
    private var upperLimitSeconds: Int? = null
    private val CHANNEL_ID = "org.hyperskill"
    private val NOTIFICATION_ID = 393939

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    secondsElapsed++
                    updateTimerText()
                    updateProgressBarColor()
                    checkUpperLimit()
                    handler.postDelayed(this, 1000)
                }
            }
        }

        binding.startButton.setOnClickListener {
            toggleTimer()
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }

        binding.settingsButton.setOnClickListener {
            showSettingsDialog()
        }

        updateTimerText()

        progressBarColors = arrayOf(Color.BLUE, Color.GREEN, Color.RED) // You can add more colors if needed

        createNotificationChannel()
    }

    private fun toggleTimer() {
        if (!isRunning) {
            startTimer()
            binding.progressBar.visibility = View.VISIBLE
            binding.settingsButton.isEnabled = false // Disable the settings button
        }
    }

    private fun startTimer() {
        isRunning = true
        handler.postDelayed(runnable, 1000)
    }

    private fun stopTimer() {
        isRunning = false
        binding.settingsButton.isEnabled = true // Enable the settings button
    }

    private fun resetTimer() {
        secondsElapsed = 0
        updateTimerText()
        stopTimer()
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun updateTimerText() {
        val minutes = secondsElapsed / 60
        val seconds = secondsElapsed % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)
        binding.textView.text = timeString
    }

    private fun updateProgressBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.progressBar.indeterminateTintList = android.content.res.ColorStateList.valueOf(progressBarColors[currentColorIndex])
        }
        currentColorIndex = (currentColorIndex + 1) % progressBarColors.size
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val editText = dialogView.findViewById<EditText>(R.id.upperLimitEditText)
        editText.setText(upperLimitSeconds?.toString())

        builder.setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val input = editText.text.toString()
                if (input.isNotBlank()) {
                    upperLimitSeconds = input.toInt()
                } else {
                    upperLimitSeconds = null
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun checkUpperLimit() {
        if (upperLimitSeconds != null && upperLimitSeconds!! > 0 && secondsElapsed >= upperLimitSeconds!!) {
            binding.textView.setTextColor(Color.RED)
            showNotification()
        } else {
            binding.textView.setTextColor(Color.BLACK)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications_icon)
            .setContentTitle("Stopwatch Notification")
            .setContentText("The upper limit has been reached!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setSound(null)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVibrate(longArrayOf(1000, 1000))
            .setOngoing(true)

        val notification = builder.build()

        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
