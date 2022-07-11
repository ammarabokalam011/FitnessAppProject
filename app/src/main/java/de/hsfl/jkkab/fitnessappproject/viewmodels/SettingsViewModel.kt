package de.hsfl.jkkab.fitnessappproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hsfl.jkkab.fitnessappproject.models.database.User
import de.hsfl.jkkab.fitnessappproject.repositories.HeartRateRepository
import de.hsfl.jkkab.fitnessappproject.repositories.Repository
import de.hsfl.jkkab.fitnessappproject.repositories.UserRepository

class SettingsViewModel : ViewModel() {
    private var userRepository: UserRepository? = null
    private var hrRepository: HeartRateRepository? = null
    private var user: User? = null
    private var insert = false
    private var isHRConnected: LiveData<Boolean>? = null
    private var name: MutableLiveData<String>? = null
    private var email: MutableLiveData<String>? = null
    private var age: MutableLiveData<Int>? = null
    private var isMale: MutableLiveData<Boolean>? = null
    private var weight: MutableLiveData<Float>? = null
    private var height: MutableLiveData<Int>? = null
    fun setRepository(repository: Repository) {
        userRepository = repository.userRepository
        hrRepository = repository.heartRateRepository
        val liveUser: LiveData<User>? = userRepository?.getUser()
        if (liveUser?.value == null) {
            user = User("Kein Name", "", 30, true,0F, 0)
            insert = true
        } else {
            user = liveUser.getValue()
        }
        name = MutableLiveData(user?.name)
        email = MutableLiveData(user?.email)
        age = MutableLiveData(user?.age)
        isMale = MutableLiveData(user?.isMale)
        weight = MutableLiveData(user?.weight)
        height = MutableLiveData(user?.height)
        isHRConnected = hrRepository?.isConnected
    }

    fun isHRConnected(): LiveData<Boolean>? {
        return isHRConnected
    }

    fun getName(): MutableLiveData<String>? {
        return name
    }

    fun getEmail(): MutableLiveData<String>? {
        return email
    }

    fun getAge(): MutableLiveData<Int>? {
        return age
    }

    fun isMale(): MutableLiveData<Boolean>? {
        return isMale
    }

    fun getWeight(): MutableLiveData<Float>? {
        return weight
    }

    fun getHeight(): MutableLiveData<Int>? {
        return height
    }

    fun setName(name: String) {
        user?.name = name
    }

    fun setEmail(email: String) {
        user?.email = email
    }

    fun setAge(age: Int) {
        user?.age = age
    }

    fun setIsMale(isMale: Boolean) {
        user?.isMale = isMale
    }

    fun setWeight(weight: Float) {
        user?.weight = weight
    }

    fun setHeight(height: Int) {
        user?.height = height
    }

    fun update() {
        if (insert) {
            userRepository?.insert(user)
        } else {
            userRepository?.update(user)
        }
    }

    fun connectHR() {
        hrRepository?.startSimulation()
    }
}