
package org.hyperskill.secretdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged

class LoginActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val textInput = findViewById<EditText>(R.id.etPin)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        var pin = ""

        textInput.doAfterTextChanged {
            pin = it.toString()
        }

        loginButton.setOnClickListener {
            if (pin == "1234") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                textInput.error = "Wrong PIN!"
            }
        }
    }
}
