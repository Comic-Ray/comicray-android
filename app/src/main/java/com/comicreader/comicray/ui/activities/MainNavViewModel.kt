package com.comicreader.comicray.ui.activities

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comicreader.comicray.ui.fragments.more.MoreFragment
import com.comicreader.comicray.ui.fragments.main.MainFragment
import com.kpstv.navigation.*
import com.comicreader.comicray.extensions.AbstractNavigationOptions
import kotlin.reflect.KClass

typealias FragClazz = KClass<out Fragment>

class MainNavViewModel : ViewModel() {
    internal val navigation = MutableLiveData<NavigationOptions>()
    fun navigateTo(
        screen: MainRoutes,
        navOptions: FragmentNavigator.NavOptions,
    ) {
        navigation.value = NavigationOptions(
            clazz = screen.clazz,
            navOptions = navOptions
        )
    }

    data class NavigationOptions(
        val clazz: FragClazz,
        val navOptions: FragmentNavigator.NavOptions
    ) : AbstractNavigationOptions()
}

enum class MainRoutes(val clazz: FragClazz) {
    MAIN(MainFragment::class),
    MORE(MoreFragment::class),
}