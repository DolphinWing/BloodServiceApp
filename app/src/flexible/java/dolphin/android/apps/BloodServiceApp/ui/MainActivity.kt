@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper

class MainActivity : AppCompatActivity(), NavigationDrawerFragment.NavigationDrawerCallbacks {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navigationFragment: NavigationDrawerFragment
    private lateinit var contentFragment: Fragment

    private var siteId: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_drawer)
        findViewById<Toolbar>(R.id.toolbar)?.apply { setSupportActionBar(this) }
//        supportActionBar?.apply {
//            //setHomeButtonEnabled(true)
//            setDisplayShowHomeEnabled(true)
//            setDisplayHomeAsUpEnabled(true)
//        }
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            switchToSection(it.itemId)
            true
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //navigationFragment = fragmentManager.findFragmentById(R.id.navigation_drawer) as NavigationDrawerFragment
        navigationFragment = NavigationDrawerFragment()
        supportFragmentManager?.beginTransaction()
                ?.replace(R.id.navigation_drawer, navigationFragment)
                ?.commitNow()
        navigationFragment.setUp(R.id.navigation_drawer, drawerLayout)
        if (navigationFragment.selectedCenter == Int.MIN_VALUE) {
            //switchToSection(R.id.action_settings)
            navigationFragment.lockDrawer()
        } else {//load data
            switchToSection(R.id.action_section1)
            navigationFragment.unlockDrawer()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_personal)?.isVisible = false
        menu?.findItem(R.id.action_settings)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> navigationFragment.openDrawer()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (navigationFragment.isDrawerOpen) {
            navigationFragment.closeDrawer()
            return
        }

        super.onBackPressed()
    }

    override fun onNavigationDrawerItemSelected(position: Int) {
        if (position == -1) {
            //switchToSection(R.id.action_settings)
            bottomNavigationView.selectedItemId = R.id.action_settings
            return
        }

        siteId = resources.getIntArray(R.array.blood_center_id)[position + 1]
        supportActionBar?.title = BloodDataHelper(this).getBloodCenterName(siteId)
    }

    private fun switchToSection(id: Int) {
        when (id) {
            R.id.action_section1,
            R.id.action_section2,
            R.id.action_section3,
            R.id.action_settings -> {
                contentFragment = SettingsFragment()
                supportActionBar?.title = getString(R.string.action_settings)
                //bottomNavigationView.selectedItemId = R.id.action_settings
            }
        }
        supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container, contentFragment)
                ?.commitNowAllowingStateLoss()
    }
}