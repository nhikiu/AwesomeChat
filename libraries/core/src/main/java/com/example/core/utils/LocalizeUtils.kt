package com.example.core.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*


fun Context.setLanguage(language: String) {
//    val locale = Locale(language)
//    Locale.setDefault(locale)
//    val configuration: Configuration = resources.configuration
//    configuration.setLocale(locale)
//    createConfigurationContext(configuration)
//    resources.updateConfiguration(configuration, resources.displayMetrics)
    val config: Configuration = this.getResources().getConfiguration()
    var sysLocale: Locale? = null
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
        sysLocale = getSystemLocale(config)
    } else {
        sysLocale = getSystemLocaleLegacy(config)
    }
    if (!language.equals("") && sysLocale!!.language != language) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale)
        } else {
            setSystemLocaleLegacy(config, locale)
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         this.createConfigurationContext(config)
    } else {
        this.getResources()
            .updateConfiguration(config, this.getResources().getDisplayMetrics())
    }
}


fun getSystemLocaleLegacy(config: Configuration): Locale? {
    return config.locale
}

@TargetApi(Build.VERSION_CODES.N)
fun getSystemLocale(config: Configuration): Locale? {
    return config.locales[0]
}

fun setSystemLocaleLegacy(config: Configuration, locale: Locale?) {
    config.locale = locale
}

@TargetApi(Build.VERSION_CODES.N)
fun setSystemLocale(config: Configuration, locale: Locale?) {
    config.setLocale(locale)
}