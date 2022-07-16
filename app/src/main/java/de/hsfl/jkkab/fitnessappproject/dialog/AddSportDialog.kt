package de.hsfl.jkkab.fitnessappproject.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import de.hsfl.jkkab.fitnessappproject.R

class AddSportDialog : DialogFragment() {

    @Override
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.fragment_about)
                .setPositiveButton(R.string.start
                ) { dialog, id ->
                    // START THE GAME!
                }
                .setNegativeButton(R.string.stop
                ) { dialog, id ->
                    // User cancelled the dialog
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}