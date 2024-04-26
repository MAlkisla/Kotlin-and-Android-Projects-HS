package org.hyperskill.simplebankmanager

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.hyperskill.simplebankmanager.databinding.FragmentPayBillsBinding
import android.widget.EditText
import android.widget.Button
import android.widget.Toast

class PayBillsFragment : Fragment() {
    private lateinit var binding: FragmentPayBillsBinding
    private lateinit var codeInputEditText: EditText
    private lateinit var showBillInfoButton: Button
    var balanceSetter: BalanceSetter? = null
    var billInfoSupplier: BillInfoSupplier? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPayBillsBinding.inflate(layoutInflater, container, false)

        codeInputEditText = binding.payBillsCodeInputEditText
        showBillInfoButton = binding.payBillsShowBillInfoButton

        showBillInfoButton.setOnClickListener {
            val codeInput: String = codeInputEditText.text.toString()
            openDialog(codeInput)
        }
        return binding.root
    }

    private fun openDialog(billCodeString: String) {
        val billInfo =
            billInfoSupplier?.getBillInfoByCode(billCodeString)

        if (billInfo != null) {
            val billName = billInfo.first
            val billCode = billInfo.second
            val billAmount = billInfo.third
            showConfirmBillInfoDialog(billName, billCode, billAmount)
        } else {
            showErrorDialog("Wrong code")
        }

    }

    private fun showConfirmBillInfoDialog(billName: String, billCode: String, billAmount: Double) {
        AlertDialog.Builder(context)
            .setTitle("Bill info")
            .setMessage("Name: $billName\nBillCode: $billCode\nAmount: $%.2f".format(billAmount))
            .setPositiveButton("Confirm") { _, _ ->
                if (balanceSetter!!.hasFunds(billAmount)) {
                    balanceSetter!!.subtractBalance(billAmount)
                    Toast.makeText(
                        context,
                        "Payment for bill $billName, was successful",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showErrorDialog("Not enough funds")
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Ok") { _, _ -> }
            .create()
            .show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        balanceSetter = context as? BalanceSetter
        billInfoSupplier = context as? BillInfoSupplier
    }

    override fun onDetach() {
        super.onDetach()
        balanceSetter = null
    }
}