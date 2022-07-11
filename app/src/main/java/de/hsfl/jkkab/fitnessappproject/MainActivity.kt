package de.hsfl.jkkab.fitnessappproject

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import de.hsfl.jkkab.fitnessappproject.fragments.PagerFragment
import de.hsfl.jkkab.fitnessappproject.repositories.Repository

class MainActivity : AppCompatActivity() {
    var repository: Repository? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repository = Repository(this)
        ActivityCompat.requestPermissions(
            this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSION_ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        repository!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        tellFragments()
    }

    private fun tellFragments() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val fragments = navHostFragment!!.childFragmentManager.fragments
        for (current in fragments) if (current != null && current is PagerFragment) (current as PagerFragment).onBackPressed()
    }

    companion object {
        const val REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 42
    }
}