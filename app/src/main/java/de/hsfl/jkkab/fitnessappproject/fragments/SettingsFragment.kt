package de.hsfl.jkkab.fitnessappproject.fragments

import de.hsfl.jkkab.fitnessappproject.MainActivity
import java.lang.NumberFormatException

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.databinding.FragmentSettingsBinding
import de.hsfl.jkkab.fitnessappproject.viewmodels.SettingsViewModel


class SettingsFragment : Fragment(), PagerFragment {
    private var binding: FragmentSettingsBinding? = null
    private var settingsViewModel: SettingsViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        (activity as MainActivity?)?.repository?.let { settingsViewModel!!.setRepository(it) }
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        //Spinner Adapter Initialisierung
        val adapter: ArrayAdapter<CharSequence>? = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.gender,
                R.layout.spinner_item_white_text
            )
        }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spGender.setAdapter(adapter)
        settingsViewModel!!.isHRConnected()?.observe(viewLifecycleOwner, connectionObserver)
        settingsViewModel!!.getName()?.observe(viewLifecycleOwner, nameObserver)
        settingsViewModel!!.getAge()?.observe(viewLifecycleOwner, ageObserver)
        settingsViewModel!!.getEmail()?.observe(viewLifecycleOwner, emailObserver)
        settingsViewModel!!.getHeight()?.observe(viewLifecycleOwner, heightObserver)
        settingsViewModel!!.getWeight()?.observe(viewLifecycleOwner, weightObserver)
        binding!!.etPersonName.addTextChangedListener(nameWatcher)
        binding!!.etEmail.addTextChangedListener(emailWatcher)
        binding!!.etAge.addTextChangedListener(ageWatcher)
        binding!!.etHeight.addTextChangedListener(heightWatcher)
        binding!!.etWeight.addTextChangedListener(weightWatcher)
        binding!!.spGender.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                settingsViewModel!!.setIsMale(i == 0)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
        binding!!.bSave.setOnClickListener(View.OnClickListener { settingsViewModel!!.update() })
        binding!!.bConnectHR.setOnClickListener(View.OnClickListener { settingsViewModel!!.connectHR() })
        return binding!!.getRoot()
    }

    private val connectionObserver = Observer<Boolean> { isConnected ->
        binding!!.tvStatus.setText(
            if (isConnected) getString(R.string.connected) else getString(R.string.disconnected)
        )
    }
    private val nameObserver: Observer<String> =
        Observer { name -> binding!!.etPersonName.setText(name) }
    private val ageObserver = Observer<Int> { age -> binding!!.etAge.setText(age.toString()) }
    private val emailObserver: Observer<String> =
        Observer { email -> binding!!.etEmail.setText(email) }
    private val heightObserver =
        Observer<Int> { height -> binding!!.etHeight.setText(height.toString()) }
    private val weightObserver =
        Observer<Float> { weight -> binding!!.etWeight.setText(weight.toString()) }
    private val nameWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            settingsViewModel?.setName(editable.toString())
        }
    }
    private val emailWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            settingsViewModel?.setEmail(editable.toString())
        }
    }
    private val ageWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            try {
                settingsViewModel?.setAge(editable.toString().toInt())
            } catch (e: NumberFormatException) {
            }
        }
    }
    private val heightWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            try {
                settingsViewModel?.setHeight(editable.toString().toInt())
            } catch (e: NumberFormatException) {
            }
        }
    }
    private val weightWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            try {
                settingsViewModel?.setWeight(editable.toString().toFloat())
            } catch (e: NumberFormatException) {
            }
        }
    }

    override fun onBackPressed() {
        view?.let { findNavController(it).navigateUp() }
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}