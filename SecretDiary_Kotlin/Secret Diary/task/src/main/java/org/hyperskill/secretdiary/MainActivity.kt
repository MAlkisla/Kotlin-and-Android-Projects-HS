package org.hyperskill.secretdiary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textInput = findViewById<TextInputEditText>(R.id.etNewWriting)
        val showedText = findViewById<TextView>(R.id.tvDiary)
        var textToSave = ""
        textInput.doAfterTextChanged { text ->
            textToSave = text.toString()
        }
        val sharedPreferences = getSharedPreferences("PREF_DIARY", Context.MODE_PRIVATE)
        val saveButton = findViewById<Button>(R.id.btnSave)
        val undoButton = findViewById<Button>(R.id.btnUndo)
        showedText.movementMethod = ScrollingMovementMethod()
        val notesList =
            (sharedPreferences.getString("KEY_DIARY_TEXT", "") ?: "").split("\n\n").toMutableList()

        showedText.text = sharedPreferences.getString("KEY_DIARY_TEXT", "") ?: ""

        saveButton.setOnClickListener {
            if (textToSave.isEmpty() || textToSave.isBlank()) {
                Toast.makeText(
                    this@MainActivity,
                    "Empty or blank input cannot be saved",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                notesList += "${
                    kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.of("UTC+2"))
                        .toString().substringBeforeLast('.').replace("T", " ")
                }\n$textToSave"
                notesList.sortDescending()
                sharedPreferences.edit()
                    .putString(
                        "KEY_DIARY_TEXT",
                        notesList.joinToString("\n\n").trim()
                    )
                    .apply()

                showedText.text = sharedPreferences.getString("KEY_DIARY_TEXT", "") ?: ""
                textToSave = ""
                textInput.text?.clear()
            }
        }

        undoButton.setOnClickListener {
            if ((sharedPreferences.getString("KEY_DIARY_TEXT", "") ?: "").isNotBlank()) {
                AlertDialog.Builder(this)
                    .setTitle("Remove last note")
                    .setMessage("Do you really want to remove the last writing? This operation cannot be undone!")
                    .setPositiveButton("Yes") { _, _ ->
                        try {
                            notesList.removeFirst()
                            sharedPreferences.edit()
                                .remove("KEY_DIARY_TEXT")
                                .apply()
                            sharedPreferences.edit()
                                .putString(
                                    "KEY_DIARY_TEXT",
                                    notesList.joinToString("\n\n").trim()
                                )
                                .apply()
                            showedText.text =
                                sharedPreferences.getString("KEY_DIARY_TEXT", "") ?: ""
                        } catch (e: Exception) {
                            Log.e("Exception", e.message ?: "")
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
            else{
                AlertDialog.Builder(this)
                    .setTitle("Doesn't have a note")
                    .setMessage("Please adding a note!")
                    .show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
    }
}
