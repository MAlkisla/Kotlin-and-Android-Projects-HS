package org.hyperskill.simplebankmanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import org.hyperskill.simplebankmanager.Extensions.showToast
import org.hyperskill.simplebankmanager.databinding.FragmentCalculateExchangeBinding


class CalculateExchangeFragment : Fragment() {

    companion object {
        val currenciesArray = arrayOf("EUR", "GBP", "USD")
    }

    private lateinit var binding: FragmentCalculateExchangeBinding
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var convertFromSpinner: Spinner
    private lateinit var convertToSpinner: Spinner
    private lateinit var fundsToConvertEt: EditText
    private lateinit var buttonConvertFundsView: Button
    private lateinit var showConvertedAmountTextView: TextView
    private var exchangeCalculator: ExchangeCalculator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculateExchangeBinding
            .inflate(layoutInflater, container, false)

        binding.also {
            convertFromSpinner = it.calculateExchangeFromSpinner
            convertToSpinner = it.calculateExchangeToSpinner
            fundsToConvertEt = it.calculateExchangeAmountEditText
            buttonConvertFundsView = it.calculateExchangeButton
            showConvertedAmountTextView = it.calculateExchangeDisplayTextView
        }

        buttonConvertFundsView.setOnClickListener {
            convert()
        }

        spinnerAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            currenciesArray
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        setSpinner()

        return binding.root
    }


    private fun setSpinner() {
        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (convertFromSpinner.selectedItem == convertToSpinner.selectedItem) {
                    showToast("Cannot convert to same currency")
                    when (convertFromSpinner.selectedItem) {
                        "USD" -> convertToSpinner.setSelection(0)
                        "EUR" -> convertToSpinner.setSelection(1)
                        else -> convertToSpinner.setSelection(2)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        convertFromSpinner.adapter = spinnerAdapter
        convertToSpinner.adapter = spinnerAdapter
        convertFromSpinner.setSelection(0, true)
        convertToSpinner.setSelection(1, true)

        convertFromSpinner.onItemSelectedListener = itemSelectedListener
        convertToSpinner.onItemSelectedListener = itemSelectedListener
    }


    private fun convert() {
        val fundsToConvertEtValue = fundsToConvertEt.text.toString().trim()
        if (fundsToConvertEtValue.isBlank()) {
            showToast("Enter amount")
            return
        }

        val convertFrom = currenciesArray[convertFromSpinner.selectedItemPosition]
        val convertTo = currenciesArray[convertToSpinner.selectedItemPosition]
        val toConvertAmount = fundsToConvertEt.text.toString().toDouble()

        val convertedAmount = exchangeCalculator!!.calculateExchange(
            from = convertFrom,
            to = convertTo,
            amount = toConvertAmount
        )
        val displayText = when {
            convertFrom == "USD" && convertTo == "EUR" -> "$%.2f = €%.2f".format(
                toConvertAmount,
                convertedAmount
            )

            convertFrom == "USD" && convertTo == "GBP" -> "$%.2f = £%.2f".format(
                toConvertAmount,
                convertedAmount
            )

            convertFrom == "EUR" && convertTo == "USD" -> "€%.2f = $%.2f".format(
                toConvertAmount,
                convertedAmount
            )

            convertFrom == "EUR" && convertTo == "GBP" -> "€%.2f = £%.2f".format(
                toConvertAmount,
                convertedAmount
            )

            convertFrom == "GBP" && convertTo == "EUR" -> "£%.2f = €%.2f".format(
                toConvertAmount,
                convertedAmount
            )

            convertFrom == "GBP" && convertTo == "USD" -> "£%.2f = $%.2f".format(
                toConvertAmount,
                convertedAmount
            )

            else -> "%.2f $convertFrom = %.2f $convertTo".format(toConvertAmount, convertedAmount)
        }

        showConvertedAmountTextView.text = displayText
        Log.d("amount", showConvertedAmountTextView.text.toString())
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        exchangeCalculator = context as? ExchangeCalculator
    }

    override fun onDetach() {
        super.onDetach()
        exchangeCalculator = null
        convertFromSpinner.onItemSelectedListener = null
        convertToSpinner.onItemSelectedListener = null
    }
}