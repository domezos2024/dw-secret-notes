package com.snote.domezos

import android.app.Application
import android.content.Context
import com.snote.domezos.data.LocaleManager

class DwApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(base))
    }
}
