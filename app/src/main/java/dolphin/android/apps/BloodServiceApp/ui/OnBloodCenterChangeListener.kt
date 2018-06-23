package dolphin.android.apps.BloodServiceApp.ui

interface OnBloodCenterChangeListener {
    fun notifyChanged(siteId: Int, timeInMillis: Long)
}