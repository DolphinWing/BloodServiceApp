package dolphin.android.apps.BloodServiceApp

import android.app.Application
import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil

/**
 * Created by dolphin on 2014/10/21.
 * http://wangshifuola.blogspot.tw/2011/12/androidapplicationglobal-variable.html
 */
class MyApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }
}