package org.hyperskill.simplebankmanager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.hyperskill.simplebankmanager.Extensions.showToast
import org.hyperskill.simplebankmanager.databinding.FragmentTransferFundsBinding

class TransferFundsFragment : Fragment() {
    private lateinit var binding: FragmentTransferFundsBinding
    var accountNumberValidator: AccountNumberValidator? = null
    var balanceSetter: BalanceSetter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentTransferFundsBinding.inflate(layoutInflater, container, false)

        binding.transferFundsButton.setOnClickListener(::onTransferClick)

        return binding.root
    }

    private fun onTransferClick(v: View) {
        val account = binding.transferFundsAccountEditText.text.toString()
        val amount = binding.transferFundsAmountEditText.text.toString().toDoubleOrNull()

        val isValidInput = run {
            var valid = true
            if (accountNumberValidator?.isValidAccount(account) != true) {
                valid = false
                binding.transferFundsAccountEditText.error = "Invalid account number"
            }
            if (amount == null || amount <= 0.0) {
                valid = false
                binding.transferFundsAmountEditText.error = "Invalid amount"
            }
            valid
        }

        if (isValidInput) {
            val hasFunds = balanceSetter!!.hasFunds(amount!!)
            if (hasFunds) {
                balanceSetter!!.subtractBalance(amount)
                showToast("Transferred \$%.2f to account $account".format(amount))
                activity?.onBackPressed()
            } else {
                showToast("Not enough funds to transfer \$%.2f".format(amount))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        balanceSetter = context as? BalanceSetter
        accountNumberValidator = context as? AccountNumberValidator
    }

    override fun onDetach() {
        super.onDetach()
        balanceSetter = null
        accountNumberValidator = null
    }
}