package de.hsfl.jkkab.fitnessappproject.repositories

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.hsfl.jkkab.fitnessappproject.models.database.OnSportUpdated
import de.hsfl.jkkab.fitnessappproject.models.database.Sport
import de.hsfl.jkkab.fitnessappproject.models.database.SportDao
import de.hsfl.jkkab.fitnessappproject.models.database.UserDatabase

class SportRepository(application: Application?) {

    private var onSportUpdated: OnSportUpdated? = null

    private var sportDao: SportDao? = null
    private var sports: LiveData<List<Sport>>? = null

    init {
        val db = UserDatabase.getDatabase(application!!)
        sportDao = db.sportDao()
        sports = MutableLiveData(sportDao!!.allSports)

        sports!!.observeForever { sports ->
            println("Sport Observer")
            if (sports != null) {
                triggerListener(sports)
            }
        }
    }

    fun getSports(): LiveData<List<Sport>>? {
        return sports
    }

    fun insert(sport: Sport?) {
        insertAsyncTask(sportDao).execute(sport)
    }

    fun update(sport: Sport?) {
        updateAsyncTask(sportDao).execute(sport)
    }

    private fun triggerListener(sports: List<Sport>) {
        if (onSportUpdated != null) {
            println("Sport updated: $sports")
            onSportUpdated!!.onSportUpdated(sports)
        }
    }

    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: SportDao?) :
        AsyncTask<Sport?, Void?, Void?>() {
        override fun doInBackground(vararg params: Sport?): Void? {
            mAsyncTaskDao!!.insert(params[0])
            return null
        }
    }

    private class updateAsyncTask(private val mAsyncTaskDao: SportDao?) : AsyncTask<Sport?, Void?, Void?>() {
        override fun doInBackground(vararg params: Sport?): Void? {
            for(sp in params)
                mAsyncTaskDao!!.update(sport = sp)
            return null
        }
    }

    fun setOnUserUpdated(onSportUpdated: OnSportUpdated?) {
        this.onSportUpdated = onSportUpdated
    }
}