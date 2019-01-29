/*
 * Copyright (c) 2019 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.txt
 */
package it.diab.core

import android.app.Application
import android.preference.PreferenceManager
import it.diab.core.util.Activities.Settings.PREF_UI_STYLE
import it.diab.core.util.UIUtils
import it.diab.core.util.extensions.get

@Suppress("unused")
class DiabApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setStyle()
    }

    private fun setStyle() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        UIUtils.setStyleMode(prefs[PREF_UI_STYLE, "1"])
    }
}