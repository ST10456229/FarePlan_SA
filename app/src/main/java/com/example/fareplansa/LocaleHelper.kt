package com.example.fareplansa

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Applies the user's chosen language to the app's configuration.
 * Call LocaleHelper.applyLanguage(this) in every Activity's attachBaseContext.
 */
object LocaleHelper {

    fun applyLanguage(context: Context): Context {
        val prefs = context.getSharedPreferences("fareplan_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("language", "en") ?: "en"
        return updateResources(context, langCode)
    }

    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}