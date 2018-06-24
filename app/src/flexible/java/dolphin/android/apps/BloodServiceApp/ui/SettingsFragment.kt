@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import dolphin.android.apps.BloodServiceApp.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)
        addPreferencesFromResource(R.xml.pref_open_source)
    }

}