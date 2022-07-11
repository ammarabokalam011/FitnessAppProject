package de.hsfl.jkkab.fitnessappproject.models.database

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class], version = 4, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao



    private class PopulateDbAsync internal constructor(db: UserDatabase) :
        AsyncTask<Void?, Void?, Void?>() {
        private val dao: UserDao
        var names = arrayOf("alex", "max", "michelle")
        override fun doInBackground(vararg p0: Void?): Void? {
            /*if (dao.getUser() == null) {
                for( int i = 0; i <= names.length - 1; i++) {
                    User user = new User(names[i], (int)(Math.random() * 100), 9);
                    dao.insert(user);
                }
            }*/
            return null
        }

        init {
            dao = db.userDao()
        }
    }

    companion object {
        private var instance: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java, "users.db"
                ).build()
            }
            return instance as UserDatabase
        }

        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d("UserDatabase", "onOpen")
                //new PopulateDbAsync(instance).execute();
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                Log.d("UserDatabase", "onCreate")
                super.onCreate(db)
            }
        }
    }
}
