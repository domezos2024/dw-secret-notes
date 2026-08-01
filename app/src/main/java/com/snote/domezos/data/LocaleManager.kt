package com.snote.domezos.data

import android.content.Context
import android.os.LocaleList
import java.util.Locale

object LocaleManager {

    fun applyLocale(base: Context): Context {
        val tag = Prefs.getLanguage(base) ?: return base
        val locale = Locale.forLanguageTag(tag)
        Locale.setDefault(locale)
        val config = base.resources.configuration.also {
            it.setLocales(LocaleList(locale))
        }
        return base.createConfigurationContext(config)
    }
}
