package dolphin.android.apps.BloodServiceApp.provider

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import dolphin.android.util.NoCoverageRequired
import java.util.Locale

/**
 * http://gunhansancar.com/change-language-programmatically-in-android/
 */
@NoCoverageRequired class LocaleUtil {
    @NoCoverageRequired
    companion object Helper {
        fun onAttach(context: Context): Context {
            val locale = Locale("zh", "TW")
            Locale.setDefault(locale)

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                updateResources(context, locale)
            else
                updateResourcesLegacy(context, locale)
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun updateResources(context: Context, locale: Locale): Context {
            val configuration = context.resources.configuration
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            return context.createConfigurationContext(configuration)
        }

        @SuppressLint("ObsoleteSdkInt")
        @Suppress("DEPRECATION")
        private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
            val resources = context.resources
            val configuration = resources.configuration
            configuration.locale = locale
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLayoutDirection(locale)
            }
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return context
        }
    }
}
