@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper

class MainActivity : AppCompatActivity(), NavigationDrawerFragment.NavigationDrawerCallbacks {
    companion object {
        private const val TAG = "MainActivity"
        const val PREF_PRIVATE_POLICY = "private_policy"
    }

    private lateinit var bottomNavigationView: BottomNavigationView
    private var navigationFragment: NavigationDrawerFragment? = null
    private var contentFragment: Fragment? = null

    private lateinit var helper: BloodDataHelper
    //private var siteId: Int = -1
    private val model: DataViewModel by viewModels()
    private val siteId: Int
        get() = model.siteId.value ?: -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        helper = BloodDataHelper(this)
        model.siteId.observe(this, Observer { id ->
            supportActionBar?.title = helper.getBloodCenterName(id)
        })

        setContentView(R.layout.activity_main_drawer)
        findViewById<Toolbar>(R.id.toolbar)?.apply { setSupportActionBar(this) }
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_action_notes)
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            switchToSection(menuItem.itemId) //switch when click bottom navigation
            true
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navigationFragment = NavigationDrawerFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.navigation_drawer, navigationFragment!!)
                .commitNow()
        navigationFragment?.setUp(R.id.navigation_drawer, drawerLayout)
        //Log.d(TAG, "selected center: ${navigationFragment?.selectedCenter}")
        if (navigationFragment?.selectedCenter == Int.MIN_VALUE) {
            //switchToSection(R.id.action_settings)
            navigationFragment?.lockDrawer()
        } else {//load data
            model.siteId.postValue(navigationFragment?.selectedCenter ?: -1)
            //supportActionBar?.title = helper.getBloodCenterName(siteId)
            //switchToSection(R.id.action_section2)
            navigationFragment?.unlockDrawer()
        }
        //Log.d(TAG, "site id = $siteId")
        switchToSection(R.id.action_section2) //auto load first section

        checkPrivatePolicyReview()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val settings = contentFragment is SettingsFragment
        menu?.findItem(R.id.action_personal)?.isVisible = false
        menu?.findItem(R.id.action_settings)?.isVisible = false
        menu?.findItem(R.id.action_facebook)?.isVisible =
                BloodDataHelper.getOpenFacebookIntent(this, siteId) != null && !settings
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                if (contentFragment is SettingsFragment) {
                    onBackPressed()
                } else {
                    navigationFragment?.openDrawer()
                }
            R.id.action_facebook -> {
                BloodDataHelper.getOpenFacebookIntent(this, siteId)?.let {
                    startActivity(it)
                }
                return true
            }
            R.id.action_go_to_website -> {
                BloodDataHelper.getOpenBloodCalendarSourceIntent(this, siteId)?.let {
                    startActivity(it)
                }
                return true
            }
            //R.id.action_private_policy -> {
            //    showPrivacyPolicyReview()
            //    return true
            //}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (contentFragment is SettingsFragment) {
            //switchToSection(R.id.action_section2)
            bottomNavigationView.selectedItemId = R.id.action_section2
            return
        }
        if (navigationFragment?.isDrawerOpen == true) {
            navigationFragment?.closeDrawer()
            return
        }

        super.onBackPressed()
    }

    override fun onNavigationDrawerItemSelected(position: Int) {
        //Log.d(TAG, "onNavigationDrawerItemSelected: $position")
        if (position == NavigationDrawerFragment.ITEM_SETTINGS) {
            switchToSection(R.id.action_settings)
            //bottomNavigationView.selectedItemId = R.id.action_settings
            return
        }
        if (position == NavigationDrawerFragment.ITEM_PRIVACY_POLICY) {
            showPrivacyPolicyReview()
            return
        }

        model.siteId.postValue(navigationFragment?.selectedCenter ?: siteId)
        //Log.d(TAG, ">>> site id = $siteId")
//        supportActionBar?.title = helper.getBloodCenterName(siteId)
//        //refresh each fragment if exists
//        intArrayOf(R.id.action_section1, R.id.action_section2, R.id.action_section3).forEach { id ->
//            sectionCache[id.and(0xFFFF)]?.arguments = Bundle().apply {
//                putInt("site_id", siteId)
//            }
//        }
    }

    private val sectionCache = SparseArray<Fragment>()

    private fun switchToSection(id: Int) {
        val key: Int = id.and(0xFFFF)
        contentFragment = if (sectionCache[key] == null) {
            when (id) {
                R.id.action_section2 -> {
                    DonationListFragment()
                }
                R.id.action_section1 -> {
                    StorageFragment()
                }
                R.id.action_section3 -> {
                    SpotListFragment()
                }
                R.id.action_settings -> {
                    SettingsFragment()
                    //bottomNavigationView.selectedItemId = R.id.action_settings
                }
                else -> DonationListFragment()
            }
        } else {
            sectionCache[key]
        }
        contentFragment?.let { fragment ->
            fragment.arguments = Bundle().apply { putInt("site_id", siteId) }
            supportActionBar?.title = if (id == R.id.action_settings)
                getString(R.string.action_settings) else helper.getBloodCenterName(siteId)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commitNowAllowingStateLoss()
            sectionCache.put(key, fragment)
        }
        //Log.d(TAG, "switch to section $id $contentFragment")
        val settings = contentFragment is SettingsFragment
        bottomNavigationView.visibility = if (settings) View.GONE else View.VISIBLE
        supportActionBar?.setHomeAsUpIndicator(
                if (settings) R.drawable.ic_action_arrow_back else R.drawable.ic_action_notes)
        invalidateOptionsMenu()
    }

    private fun showPrivacyPolicyReview() {
        SettingsFragment.showPrivacyPolicyReview(this)
    }

    private fun checkPrivatePolicyReview() {
        val c = navigationFragment?.selectedCenter ?: siteId
        if (c < 0) {
            Log.w(TAG, "not yet ready ($c)... don't show privacy warning")
            return
        }
        val prefs = PrefsUtil.getDefaultPreference(this)
        val updateCode = FirebaseRemoteConfig.getInstance().getLong("privacy_policy_update_code")
        //if private policy has been updated
        if (prefs.getLong(PREF_PRIVATE_POLICY, 0) < updateCode) {
            Handler().postDelayed({
                Snackbar.make(findViewById(R.id.main_container),
                        R.string.snackbar_privacy_policy_updated, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_privacy_policy_review) {
                            showPrivacyPolicyReview()
                            prefs.edit().putLong(PREF_PRIVATE_POLICY, updateCode).apply()
                        }
                        //.setAction(R.string.snackbar_privacy_policy_ignore) {
                        //    prefs.edit().putLong(PREF_PRIVATE_POLICY, updateCode).apply()
                        //}
                        .show()
            }, 10000)
        }
    }
}