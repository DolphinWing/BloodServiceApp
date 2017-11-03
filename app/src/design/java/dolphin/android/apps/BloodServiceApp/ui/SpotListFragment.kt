package dolphin.android.apps.BloodServiceApp.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tonicartos.superslim.GridSLM
import com.tonicartos.superslim.LayoutManager
import com.tonicartos.superslim.LinearSLM
import dolphin.android.apps.BloodServiceApp.MyApplication
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import dolphin.android.apps.BloodServiceApp.provider.SpotList
import kotlin.concurrent.thread

/**
 * Created by jimmyhu on 7/6/17.
 *
 * A replacement to use RecyclerView and sticky grid like in DonationFragment
 */
class SpotListFragment : BaseListFragment() {
    private val TAG = "SpotListFragment"
    private val DEBUG_UI = false

    private var mRecyclerView: RecyclerView? = null
    private var mProgressView: View? = null
    private var mMyAdapter: MyAdapter? = null
    private var mEmptyView: View? = null

    companion object Factory {
        fun create(siteId: Int, timeInMillis: Long): SpotListFragment {
            val fragment = SpotListFragment()
            fragment.arguments = BaseListFragment.getArgBundle(siteId, timeInMillis)
            fragment.fragmentId = "spot"
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (siteId > 0) {
            updateFragment(-1, -1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_spot_recycler_list, container, false)
        mRecyclerView = rootView.findViewById<RecyclerView?>(R.id.recycler_view)
        mRecyclerView!!.layoutManager = LayoutManager(activity)
        mEmptyView = rootView.findViewById(android.R.id.empty)
        mProgressView = rootView.findViewById(android.R.id.progress)
        return rootView//super.onCreateView(inflater, container, savedInstanceState)
    }

    private var mIsBusy = false

    override fun setFragmentBusy(busy: Boolean) {
        super.setFragmentBusy(busy)
        mIsBusy = busy
        if (activity != null && mProgressView != null) {//[35]
            mProgressView!!.visibility = if (mIsBusy) View.VISIBLE else View.GONE
        }
    }

    private fun isFragmentBusy(): Boolean {
        return mIsBusy
    }

    override fun updateFragment(siteId: Int, timeInMillis: Long) {
        super.updateFragment(siteId, timeInMillis)

        if (activity == null || this.isRemoving || this.isDetached) {
            setFragmentBusy(false)
            Log.w(TAG, "not attached to Activity")
            return
        } else if (isFragmentBusy()) {//don't update when busy
            Log.w(TAG, "still working...")
            return
        }

        setFragmentBusy(true)
        thread {
            if (DEBUG_UI) {
                debugUiGenerator()
            } else {
                downloadDonationSpotLocationMap()
            }
        }
    }

    private fun debugUiGenerator() {
        val list = SparseArray<SpotList>()
        for (k in 0..40) {
            list.put(k, SpotList(k))
            for (i in 1..6) {
                list.get(k).addStaticLocation(SpotInfo(i, k, String.format("city $k location $i")))
            }
            for (i in 6..10) {
                list.get(k).addDynamicLocation(SpotInfo(i, k, String.format("city $k location $i")))
            }
        }
        mMyAdapter = MyAdapter(activity, null, 5, list)
        activity.runOnUiThread {
            mRecyclerView!!.adapter = mMyAdapter
            mEmptyView!!.visibility = if (mMyAdapter == null) View.VISIBLE else View.GONE
            //Log.d(TAG, "debug generate complete ${mMyAdapter?.itemCount}")
            setFragmentBusy(false)
        }
    }

    private fun downloadDonationSpotLocationMap() {
        //Log.d(TAG, "download start $siteId")
        val helper = BloodDataHelper(activity)
        val app: MyApplication = activity.application as MyApplication
        var list: SparseArray<SpotList>? = app.getCacheSpotList(siteId)
        if (list == null) {
            //Log.d(TAG, "try to download from server")
            list = helper.getDonationSpotLocationMap(siteId)
            app.setCacheSpotList(siteId, list, helper.cityList)
        } else {
            //Log.d(TAG, "use cache data")
            helper.cityList = app.getCacheCityList(siteId)
        }
        //val spots = list
        mMyAdapter = if (list == null || activity == null || activity.isFinishing) {
            null
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) {
            null
        } else {
            //Log.d(TAG, "list ${list.size()}")
            MyAdapter(activity, helper, siteId, list)
        }
        if (activity != null) {
            activity.runOnUiThread {
                //Log.d(TAG, "download complete ${mMyAdapter?.itemCount}")
                mRecyclerView?.adapter = mMyAdapter
                mEmptyView?.visibility = if (mMyAdapter == null) View.VISIBLE else View.GONE
                setFragmentBusy(false)
            }
        }
    }

    private class MyAdapter(activity: Activity, helper: BloodDataHelper?, siteId: Int,
                            spotList: SparseArray<SpotList>?) : RecyclerView.Adapter<MyViewHolder>() {
        //private val TAG = "SpotListFragment"
        private val items = ArrayList<MyItem>()

        data class MyItem(val sectionFirstPosition: Int, var isHeader: Boolean, var name: String,
                          var data: SpotInfo?)

        override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
            if (items[position].isHeader) {
                holder!!.bindItem(items[position].name, null, null)
            } else {
                holder!!.bindItem(items[position].data?.spotName, items[position].data, onItemClick)
            }
            //Log.d(TAG, "$position)  name = $name")

            val lp = GridSLM.LayoutParams.from(holder.itemView!!.layoutParams)
            lp.setSlm(LinearSLM.ID)
            lp.firstPosition = items[position].sectionFirstPosition
            holder.itemView.layoutParams = lp
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
            val inflater = LayoutInflater.from(parent!!.context)
            val itemView: View
            when (viewType) {
                1 -> {
                    //Log.d(TAG, "spot city")
                    itemView = inflater.inflate(R.layout.listview_spot_city, parent, false)
                }
                0 -> {
                    //Log.d(TAG, "spot location")
                    itemView = inflater.inflate(R.layout.listview_spot_location, parent, false)
                }
                else -> itemView = inflater.inflate(R.layout.listview_spot_city, parent, false)
            }

            return MyViewHolder(itemView)
        }

        override fun getItemViewType(position: Int): Int = if (items[position].isHeader) 1 else 0

        override fun getItemCount(): Int = items.size

        init {
            val ids = activity.resources.getIntArray(R.array.blood_center_id)
            val centers = activity.resources.getStringArray(R.array.blood_center_donate_station_city_id)
            val cities = centers[ids.indexOf(siteId)].split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
            //Log.d(TAG, "${ids.indexOf(siteId)} cities: $cities")
            var itemCount = 0
            var sectionFirstPosition: Int
            for ((headerCount, cityId) in cities.withIndex()) {
                sectionFirstPosition = headerCount + itemCount
                //Log.d(TAG, "$headerCount $cityId sectionFirstPosition = $sectionFirstPosition")
                val name = if (helper == null) cityId else helper.getCityName(Integer.parseInt(cityId))
                items.add(MyItem(sectionFirstPosition, true, name, null))
                if (spotList!!.get(Integer.parseInt(cityId)) == null) continue
                spotList.get(Integer.parseInt(cityId))!!.locations.forEach { spot ->
                    itemCount++
                    items.add(MyItem(sectionFirstPosition, false, spot.spotName, spot))
                }
            }
            //Log.d(TAG, "total locations: $itemCount")
            //notifyDataSetChanged()
        }

        private val onItemClick = View.OnClickListener {
            //Log.d(TAG, "data: ${it.tag}")
            val spot = it.tag as SpotInfo
            //Log.d(TAG, "spot: ${spot.spotId} ${spot.spotName}")
            val intent = BloodDataHelper.getOpenSpotLocationMapIntent(activity, spot)
            if (intent != null) {//show in browser, don't parse it
                activity.startActivity(intent)
            }
        }
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView? = null
        var container: View = view

        fun bindItem(name: String?, data: Any?, listener: View.OnClickListener?) {
            title!!.text = name
            if (listener != null) {
                container.tag = data
                container.setOnClickListener(listener)
            }
        }

        init {
            title = view.findViewById<TextView?>(android.R.id.title)
        }
    }
}