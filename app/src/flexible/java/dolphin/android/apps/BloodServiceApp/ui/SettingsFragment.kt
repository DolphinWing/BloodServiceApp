@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.GeneralPreferenceFragment
import dolphin.android.util.PackageUtils

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)
        addPreferencesFromResource(R.xml.pref_open_source)

        findPreference("app_version")?.summary =
                PackageUtils.getPackageInfo(context, this::class.java)?.versionName ?: "x.x.x"
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "app_version" -> {
                showAssetContent(R.string.app_change_log, "version.txt")
                return true
            }
            "privacy_policy" -> {
                showAssetContent(R.string.app_privacy_policy, "privacy_policy.txt")
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun showAssetContent(titleResId: Int, name: String, encoding: String = "UTF-8") {
        activity?.runOnUiThread {
            AlertDialog.Builder(activity!!)
                    .setTitle(titleResId)
                    .setMessage(GeneralPreferenceFragment.read_asset_text(activity!!, name, encoding))
                    .setPositiveButton(android.R.string.ok, null)
                    .show().apply {
                        findViewById<TextView>(android.R.id.message)?.textSize = 12f
                    }
        }

    }
}