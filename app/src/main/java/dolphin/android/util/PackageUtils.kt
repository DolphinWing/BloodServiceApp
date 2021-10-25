package dolphin.android.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

class PackageUtils {
    companion object {
        /**
         * get package info
         *
         * @param context
         * @param cls
         * @return
         */
        @JvmStatic
        fun getPackageInfo(context: Context?, cls: Class<*>): PackageInfo? {
            return try {
                context?.packageManager?.getPackageInfo(ComponentName(context, cls).packageName, 0)
                //return pinfo;
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }

        /**
         * check if any activity can handle this intent
         *
         * @param context
         * @param intent
         * @return
         */
        @JvmStatic
        fun isCallable(context: Context?, intent: Intent?): Boolean {
            if (intent == null)
                return false
            val list = context?.packageManager?.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY) ?: ArrayList<ResolveInfo>()
            return list.size > 0
        }
    }

    //http://goo.gl/xtf7I
    fun enablePackage(context: Context?, cls: Class<*>) {
        context?.packageManager?.setComponentEnabledSetting(ComponentName(context, cls),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }

    fun disablePackage(context: Context?, cls: Class<*>) {
        context?.packageManager?.setComponentEnabledSetting(ComponentName(context, cls),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }
}
