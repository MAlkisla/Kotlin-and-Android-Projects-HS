package org.hyperskill.simplebankmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperskill.simplebankmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BankManager {
    lateinit var mainBinding: ActivityMainBinding
    lateinit var username: String
    lateinit var password: String
    var balance: Double = 100.0
    lateinit var exchangeMap: Map<String, Map<String, Double>>
    lateinit var billInfo: Map<String, Triple<String, String, Double>>

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = intent.extras?.getString("username") ?: "Lara"
        password = intent.extras?.getString("password") ?: "1234"
        balance = intent.extras?.getDouble("balance", 100.0) ?: 100.0

        val defaultExchangeMap: Map<String, Map<String, Double>> = mapOf(
            "EUR" to mapOf(
                "GBP" to 0.5,
                "USD" to 2.0
            ),
            "GBP" to mapOf(
                "EUR" to 2.0,
                "USD" to 4.0
            ),
            "USD" to mapOf(
                "EUR" to 0.5,
                "GBP" to 0.25
            )
        )

        exchangeMap = intent.extras
            ?.getSerializable("exchangeMap") as? Map<String, Map<String, Double>>
            ?: defaultExchangeMap

        val defaultBillInfoMap =
            mapOf(
                "ELEC" to Triple("Electricity", "ELEC", 45.0),
                "GAS" to Triple("Gas", "GAS", 20.0),
                "WTR" to Triple("Water", "WTR", 25.5)
            )

        billInfo = intent.extras
            ?.getSerializable("billInfo") as? Map<String, Triple<String, String, Double>>
            ?: defaultBillInfoMap

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
    }

    override fun isValidLogin(username: String, password: String): Boolean {
        return this.username == username
                && this.password == password
    }

    override fun currentBalance(): Double {
        return balance
    }

    override fun hasFunds(amount: Double): Boolean {
        return balance >= amount
    }

    override fun subtractBalance(amount: Double) {
        balance -= amount
    }

    override fun isValidAccount(account: String): Boolean {
        return "^[sc]a\\d{4}$".toRegex().matches(account)
    }

    override fun calculateExchange(from: String, to: String, amount: Double): Double {
        return amount * exchangeMap[from]!![to]!!
    }

    override fun getBillInfoByCode(code: String): Triple<String, String, Double>? {
        return if (billInfo[code] != null) billInfo[code] else null
    }
}