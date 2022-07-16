package de.hsfl.jkkab.fitnessappproject.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.hsfl.jkkab.fitnessappproject.R

/**
 * A simple [Fragment] subclass.
 * Use the [AboutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val tvPolicy:TextView=view.findViewById(R.id.tvPolicy)
        val tvTerm:TextView=view.findViewById(R.id.tvTermOfUse)
        setupHyperLink(tvPolicy)
        setupHyperLink(tvTerm)
        tvPolicy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dailyworkoutapps.com/privacy-policy.html"))
            startActivity(browserIntent)
        }
        tvTerm.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dailyworkoutapps.com/terms-of-use.html"))
            startActivity(browserIntent)
        }
        return view
    }

    private fun setupHyperLink(textview:TextView){
        textview.movementMethod=LinkMovementMethod.getInstance()
        textview.setLinkTextColor(Color.BLUE)
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutFragment()
    }
}