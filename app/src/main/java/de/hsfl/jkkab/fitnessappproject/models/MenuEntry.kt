package de.hsfl.jkkab.fitnessappproject.models

class MenuEntry(menuTitle: String?, icon: Int, navigationEndpoint: Int) {
    var menuTitle: String? = null
    var icon = 0
    var navigationEndpoint = 0

    init {
        this.menuTitle = menuTitle
        this.icon = icon
        this.navigationEndpoint = navigationEndpoint
    }
}