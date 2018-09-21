@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import java.util.*

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the [
 * design guidelines](https://developer.android.com/design/patterns/navigation-drawer.html#Interaction) for a complete explanation of the behaviors implemented here.
 */
class NavigationDrawerFragment : Fragment() {

    companion object {
        /**
         * Remember the position of the selected item.
         */
        private const val STATE_SELECTED_POSITION = "selected_navigation_drawer_position"

        /**
         * Per the design guidelines, you should show the drawer on launch until the user manually
         * expands it. This shared preference tracks this.
         */
        const val PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned"
        const val PREF_USER_NEAR_BY_CENTER = "near_by_center"

        const val ITEM_SETTINGS = -1
        const val ITEM_PRIVACY_POLICY = -2
    }

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private var mCallbacks: NavigationDrawerCallbacks? = null

//    /**
//     * Helper component that ties the action bar to the navigation drawer.
//     */
//    private val mDrawerToggle: ActionBarDrawerToggle? = null

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerListView: ListView? = null
    private var mFragmentContainerView: View? = null

    private var mCurrentSelectedPosition = Int.MIN_VALUE
    private var mFromSavedInstanceState: Boolean = false
    private var mUserLearnedDrawer: Boolean = false
//    private var mActionView: ActionView? = null

    val isDrawerOpen: Boolean
        get() = mDrawerLayout?.isDrawerOpen(mFragmentContainerView!!) ?: false

    private val actionBar: ActionBar?
        get() = (activity as AppCompatActivity).supportActionBar

    val selectedCenter: Int
        get() = when {
            mCurrentSelectedPosition == Int.MIN_VALUE -> Int.MIN_VALUE
            activity != null ->
                activity!!.resources!!.getIntArray(R.array.blood_center_id)[mCurrentSelectedPosition + 1]
            else -> -1
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false)

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION)
            mFromSavedInstanceState = true
        } else {
            mCurrentSelectedPosition = sp.getInt(PREF_USER_NEAR_BY_CENTER, Integer.MIN_VALUE)
        }

        if (mCurrentSelectedPosition == 5) {//Hualien center has been merged to Taipei center
            mCurrentSelectedPosition = 0
        }
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false)

        val centre = resources.getStringArray(R.array.blood_center)
        val list = ArrayList(Arrays.asList(*centre))
        list.removeAt(0)

        mDrawerListView = layout.findViewById(android.R.id.list)
        mDrawerListView?.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                selectItem(position)
            }
            adapter = MyAdapter(activity!!, list)
            itemsCanFocus = false
            setItemChecked(mCurrentSelectedPosition, true)
        }

        layout.findViewById<View>(android.R.id.edit)?.setOnClickListener {
            selectItem(ITEM_SETTINGS)
        }
        layout.findViewById<View>(R.id.action_private_policy)?.setOnClickListener {
            selectItem(ITEM_PRIVACY_POLICY)
        }
        layout.findViewById<View>(R.id.action_go_personal)?.setOnClickListener {
            startPersonalData()
            closeDrawer()
        }
        layout.findViewById<View>(R.id.dummy)?.setOnTouchListener { _, _ -> true }
        return layout
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    fun setUp(fragmentId: Int, drawerLayout: DrawerLayout) {
        mFragmentContainerView = activity!!.findViewById(fragmentId)
        mDrawerLayout = drawerLayout
//        mActionView = actionView

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout?.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
        // set up the drawer's list view with items and click listener

        actionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            //mDrawerLayout.openDrawer(mFragmentContainerView);
            openDrawer()
        }

//        if (mActionView != null) {
//            mDrawerLayout?.setDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
//                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//                    //super.onDrawerSlide(drawerView, slideOffset)
//                    if (slideOffset < 0.5 && mActionView!!.action is BackAction) {
//                        mActionView!!.setAction(DrawerAction(), ActionView.ROTATE_CLOCKWISE)
//                    } else if (slideOffset > 0.3 && mActionView!!.action is DrawerAction) {
//                        mActionView!!.setAction(BackAction(),
//                                ActionView.ROTATE_COUNTER_CLOCKWISE)
//                    }
//                }
//            })
//        }
    }

    private fun selectItem(position: Int) {
        if (position == Integer.MIN_VALUE) {
            openDrawer()
            return
        } else if (position == ITEM_SETTINGS || position == ITEM_PRIVACY_POLICY) {//settings
            closeDrawer()
            mCallbacks?.onNavigationDrawerItemSelected(position)
            return
        }
        mCurrentSelectedPosition = position

        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sp.edit()
        editor.putInt(PREF_USER_NEAR_BY_CENTER, position)
        editor.putBoolean(PREF_USER_LEARNED_DRAWER, true)
        editor.apply()
        unlockDrawer()

        //actionBar?.title = mDrawerListView.getItemAtPosition(position).toString()
        mDrawerListView?.setItemChecked(position, true)
        closeDrawer()
        mCallbacks?.onNavigationDrawerItemSelected(position)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        try {
            mCallbacks = activity as NavigationDrawerCallbacks
        } catch (e: ClassCastException) {
            throw ClassCastException("Activity must implement NavigationDrawerCallbacks.")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition)
    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        // Forward the new configuration the drawer toggle component.
//        mDrawerToggle?.onConfigurationChanged(newConfig)
//    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        fun onNavigationDrawerItemSelected(position: Int)
    }

    fun openDrawer() {
        mDrawerLayout?.openDrawer(mFragmentContainerView!!)
    }

    fun closeDrawer() {
        mDrawerLayout?.closeDrawer(mFragmentContainerView!!)
    }

    fun lockDrawer() {
        mDrawerLayout?.setDrawerLockMode(if (isDrawerOpen)
            DrawerLayout.LOCK_MODE_LOCKED_OPEN
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun unlockDrawer() {
        mDrawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private inner class MyAdapter(context: Context, objects: List<String>)
    //android.R.layout.simple_list_item_activated_1
        : ArrayAdapter<String>(context, R.layout.listview_blood_center, android.R.id.title, objects)

    private fun startPersonalData() {
        PrefsUtil.startBrowserActivity(activity,
                FirebaseRemoteConfig.getInstance().getString("url_blood_donor_info"))
    }
}
