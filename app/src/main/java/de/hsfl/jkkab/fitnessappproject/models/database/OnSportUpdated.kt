package de.hsfl.jkkab.fitnessappproject.models.database

interface OnSportUpdated {
    fun onSportUpdated(sports: List<Sport>)
}