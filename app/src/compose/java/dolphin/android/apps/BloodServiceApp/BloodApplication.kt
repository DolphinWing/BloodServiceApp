package dolphin.android.apps.BloodServiceApp

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil

/**
 * Created by dolphin on 2014/10/21.
 * http://wangshifuola.blogspot.tw/2011/12/androidapplicationglobal-variable.html
 */
class BloodApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        // Firebase.remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        // https://github.com/material-components/material-components-android/blob/master/docs/theming/Color.md#apply-dynamic-colors-to-all-activities-in-the-app
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
