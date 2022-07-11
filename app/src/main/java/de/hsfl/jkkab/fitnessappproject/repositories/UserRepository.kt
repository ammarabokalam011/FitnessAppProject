package de.hsfl.jkkab.fitnessappproject.repositories

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import de.hsfl.jkkab.fitnessappproject.models.database.OnUserUpdated
import de.hsfl.jkkab.fitnessappproject.models.database.User
import de.hsfl.jkkab.fitnessappproject.models.database.UserDao
import de.hsfl.jkkab.fitnessappproject.models.database.UserDatabase

class UserRepository(application: Application?) {

    private var onUserUpdated: OnUserUpdated? = null

    private var userDao: UserDao? = null
    private var user: LiveData<User>? = null

    init {
        val db = UserDatabase.getDatabase(application!!)
        userDao = db.userDao()
        user = userDao!!.user

        user!!.observeForever { user ->
            println("User Observer")
            if (user != null) {
                triggerListener(user)
            }
        }
    }

    fun getUser(): LiveData<User>? {
        return user
    }

    fun insert(user: User?) {
        insertAsyncTask(userDao).execute(user)
    }

    fun update(user: User?) {
        updateAsyncTask(userDao).execute(user)
    }

    private fun triggerListener(user: User) {
        if (onUserUpdated != null) {
            println("User updated: $user")
            onUserUpdated!!.onUserUpdated(user)
        }
    }


    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: UserDao?) :
        AsyncTask<User?, Void?, Void?>() {
        protected override fun doInBackground(vararg params: User?): Void? {
            mAsyncTaskDao!!.insert(params[0])
            return null
        }
    }

    private class updateAsyncTask internal constructor(private val mAsyncTaskDao: UserDao?) :
        AsyncTask<User?, Void?, Void?>() {
        protected override fun doInBackground(vararg params: User?): Void? {
            mAsyncTaskDao!!.update(params[0])
            return null
        }
    }

    fun setOnUserUpdated(onUserUpdated: OnUserUpdated?) {
        this.onUserUpdated = onUserUpdated
    }
}