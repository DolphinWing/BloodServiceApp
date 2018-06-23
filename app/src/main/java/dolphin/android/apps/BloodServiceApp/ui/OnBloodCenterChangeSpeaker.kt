package dolphin.android.apps.BloodServiceApp.ui

interface OnBloodCenterChangeSpeaker {
    fun registerOnBloodCenterChanged(listener: OnBloodCenterChangeListener?)
    fun unregisterOnBloodCenterChanged(listener: OnBloodCenterChangeListener?)
}