package org.hyperskill.simplebankmanager

import android.widget.Toast
import androidx.fragment.app.Fragment

object Extensions {
    fun Fragment.showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}