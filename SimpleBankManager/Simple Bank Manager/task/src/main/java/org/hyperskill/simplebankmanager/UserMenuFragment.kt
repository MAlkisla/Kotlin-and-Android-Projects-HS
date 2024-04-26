package org.hyperskill.simplebankmanager

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.hyperskill.simplebankmanager.databinding.FragmentUserMenuBinding

class UserMenuFragment : Fragment() {
    lateinit var fragmentUserMenuBinding: FragmentUserMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentUserMenuBinding = FragmentUserMenuBinding.inflate(layoutInflater, container, false)

        val username = arguments?.getString("username") ?: "user"
        fragmentUserMenuBinding.userMenuWelcomeTextView.text = "Welcome $username"

        fragmentUserMenuBinding.userMenuViewBalanceButton.setOnClickListener {
            findNavController().navigate(R.id.action_userMenuFragment_to_viewBalanceFragment)
        }

        fragmentUserMenuBinding.userMenuTransferFundsButton.setOnClickListener {
            findNavController().navigate(R.id.action_userMenuFragment_to_transferFundsFragment)
        }

        fragmentUserMenuBinding.userMenuExchangeCalculatorButton.setOnClickListener {
            findNavController().navigate(R.id.action_userMenuFragment_to_calculateExchangeFragment)
        }

        fragmentUserMenuBinding.userMenuPayBillsButton.setOnClickListener {
            findNavController().navigate(R.id.action_userMenuFragment_to_payBillsFragment)
        }

        arguments?.getString("balance")
        Log.d("UsermenuFragment", "arguments: $arguments")
        return fragmentUserMenuBinding.root
    }
}