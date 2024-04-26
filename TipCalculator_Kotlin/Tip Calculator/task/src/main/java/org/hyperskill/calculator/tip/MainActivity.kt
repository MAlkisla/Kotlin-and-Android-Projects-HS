package org.hyperskill.calculator.tip

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tipPercentTV: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var editText: EditText
    private lateinit var billValueTV: TextView
    private lateinit var tipAmountTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tipPercentTV = findViewById(R.id.tip_percent_tv)
        seekBar = findViewById(R.id.seek_bar)
        editText = findViewById(R.id.edit_text)
        billValueTV = findViewById(R.id.bill_value_tv)
        tipAmountTV = findViewById(R.id.tip_amount_tv)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateTipViews()
            }
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTipViews()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun updateTipViews() {
        val billAmount = editText.text.toString().toDoubleOrNull()
        val tipPercent = seekBar.progress

        if (billAmount != null && billAmount > 0) {
            val tipAmount = (tipPercent.toDouble() / 100) * billAmount
            val formattedBillAmount = String.format("%.2f", billAmount)
            val formattedTipAmount = String.format("%.2f", tipAmount)

            billValueTV.text = "Bill Value: $$formattedBillAmount"
            tipPercentTV.text = "Tip: $tipPercent%"
            tipAmountTV.text = "Tip Amount: $$formattedTipAmount"
        } else {
            billValueTV.text = ""
            tipPercentTV.text = ""
            tipAmountTV.text = ""
        }
    }
}
