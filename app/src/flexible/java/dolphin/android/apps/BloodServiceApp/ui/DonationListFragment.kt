@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.DonateDay
import dolphin.android.util.PackageUtils
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.*
import eu.davidea.viewholders.FlexibleViewHolder


class DonationListFragment : Fragment(), FlexibleAdapter.OnItemClickListener,
        FlexibleAdapter.OnItemLongClickListener {
    companion object {
        private const val TAG = "DonationListFragment"
    }

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private val viewModel: DataViewModel by activityViewModels()
    private var prefs: PrefsUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PrefsUtil(requireActivity())
        viewModel.siteId.observe(this, Observer { id ->
            queryData(id)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(
            R.layout.fragment_recycler_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(android.R.id.progress)
        swipeRefreshLayout?.isRefreshing = true
        recyclerView = view.findViewById(android.R.id.list)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = FixedLayoutManager(requireActivity())
        }
    }

    private val adapterList = ArrayList<AbstractFlexibleItem<*>>()

    private fun queryData(siteId: Int) {
        //Log.d(TAG, "start query data")
        activity?.runOnUiThread {
            swipeRefreshLayout?.isEnabled = true
            swipeRefreshLayout?.isRefreshing = true
            recyclerView?.contentDescription = getString(R.string.title_downloading_data)
        }
        viewModel.getDonationData(siteId).observe(viewLifecycleOwner, Observer { dayList ->
            //Log.d(TAG, "donation list: ${dayList?.size}")
            adapterList.clear()
            val list = ArrayList<AbstractFlexibleItem<*>>()
            dayList?.forEach { day ->
                //Log.d(TAG, "${it.dateString} has ${it.activityCount}")
                if (day.activities.isNotEmpty()) {//make sure it is not empty
                    val dateItem = DateItem(day)
                    adapterList.add(dateItem)
                    day.activities.forEach { act ->
                        //Log.d(TAG, "  ${it.name} @ ${it.location}")
                        adapterList.add(ActivityItem(dateItem, act))
                        list.add(adapterList.last())
                    }
                } else {
                    Log.w(TAG, "no activities in ${day.dateString}")
                }
            }
            //Log.d(TAG, "adapter list size: ${adapterList.size}")
            val adapter = FlexibleAdapter(list, this@DonationListFragment).apply {
                setStickyHeaders(prefs?.isHeaderSticky ?: true)
                setDisplayHeadersAtStartUp(true)
            }
            recyclerView?.adapter = adapter
//            try {
//                //fix Enable sticky headers after setting Adapter to RecyclerView
//                adapter.setStickyHeaders(prefs?.isHeaderSticky ?: false)
//            } catch (e: IllegalStateException) {
//                //try to catch the exception
//            }
            recyclerView?.contentDescription = null
            swipeRefreshLayout?.isRefreshing = false
            swipeRefreshLayout?.isEnabled = false
        })
    }

    /**
     * https://github.com/davideas/FlexibleAdapter/wiki/5.x-%7C-Headers-and-Sections#sticky-headers
     */
    internal class DateItem(private val day: DonateDay)
        : AbstractHeaderItem<FlexibleViewHolder>(), IHeader<FlexibleViewHolder> {

        override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                    holder: FlexibleViewHolder?, position: Int,
                                    list: MutableList<Any>?) {
            (holder as? DateHolder)?.apply {
                title?.text = day.dateString
                count?.text = day.activityCount.toString()
            }
        }

        override fun equals(
                other: Any?) = (other as? DateItem)?.day?.timeInMillis == day.timeInMillis

        override fun hashCode(): Int = day.timeInMillis.hashCode()

        override fun createViewHolder(view: View?,
                                      adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?)
                : FlexibleViewHolder = DateHolder(view, adapter)

        override fun getLayoutRes() = if (day.activityCount > 0) {
            R.layout.listview_donation_date
        } else {
            R.layout.listview_donation_date_no_children
        }

        internal class DateHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?)
            : FlexibleViewHolder(view, adapter, true) {
            var title: TextView? = view?.findViewById(android.R.id.text1)
            var count: TextView? = view?.findViewById(android.R.id.text2)
        }
    }

    internal class ActivityItem(header: DateItem?, val activity: DonateActivity)
        : AbstractSectionableItem<FlexibleViewHolder, DateItem>(header) {
        init {
            this.header = header
        }

        override fun createViewHolder(view: View?,
                                      adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?)
                : FlexibleViewHolder = ActivityHolder(view, adapter)

        override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                    holder: FlexibleViewHolder?, position: Int,
                                    list: MutableList<Any>?) {
            (holder as? ActivityHolder)?.apply {
                title?.text = activity.name
                location?.text = activity.location
                startTime?.text = activity.startTimeString
                endTime?.text = activity.endTimeString
                this.itemView.contentDescription = activity.accessibilityString
            }
            //fragment.registerForContextMenu(holder?.itemView)
        }

        override fun equals(other: Any?) = (other as? ActivityItem)?.activity == activity

        override fun hashCode(): Int = activity.hashCode()

        override fun getLayoutRes(): Int = R.layout.listview_donation_activity

        internal class ActivityHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?)
            : FlexibleViewHolder(view, adapter) {
            val title: TextView? = view?.findViewById(android.R.id.title)
            val startTime: TextView? = view?.findViewById(android.R.id.text1)
            val endTime: TextView? = view?.findViewById(android.R.id.text2)
            val location: TextView? = view?.findViewById(android.R.id.message)
        }
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        //Log.d(TAG, "onItemClick $position")
        return false
    }

    override fun onItemLongClick(position: Int) {
        //Log.d(TAG, "onItemLongClick $position")
        (adapterList[position] as? ActivityItem)?.let { item ->
            Log.d(TAG, "  ${item.activity.name}")

            AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.action_more)
                    .setItems(arrayOf(getString(R.string.action_add_to_calendar),
                            getString(R.string.action_search_location))) { _, index ->
                        //Log.d(TAG, "select $index")
                        when (index) {
                            0 -> addActivityToCalendar(item.activity)
                            1 -> showSearchMapDialog(item.activity)
                        }
                    }
                    .show()
        }
    }

    //Android Essentials: Adding Events to the User’s Calendar
    //http://goo.gl/jyT75l
    private fun addActivityToCalendar(donation: DonateActivity) {
        val calIntent = Intent(Intent.ACTION_INSERT).apply {
            setDataAndType(CalendarContract.Events.CONTENT_URI, "vnd.android.cursor.item/event")
            putExtra(CalendarContract.Events.TITLE, donation.name)
            putExtra(CalendarContract.Events.EVENT_LOCATION, donation.location)
            putExtra(CalendarContract.Events.DESCRIPTION,
                    getString(R.string.action_add_to_calendar_description))
            putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, donation.startTime.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, donation.endTime.timeInMillis)
        }
        if (PackageUtils.isCallable(activity, calIntent)) {
            startActivity(calIntent)
        }
    }

    private fun showSearchMapDialog(donation: DonateActivity) {
        val list = donation.prepareLocationList(requireActivity()).toTypedArray()
        AlertDialog.Builder(requireActivity())
                .setTitle(R.string.action_search_on_maps)
                .setItems(list) { _, index ->
                    //Log.d(TAG, "select $index ${list[index]}")
                    if (PrefsUtil.isGoogleMapsInstalled(activity)) {
                        openActivityOnGoogleMaps(list[index])
                    } else {
                        openActivityOnGoogleMapsInBrowser(list[index])
                    }
                }
                .show()
    }

    private fun openActivityOnGoogleMaps(location: String) {
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$location"))
        mapIntent.setPackage("com.google.android.apps.maps")
        if (PackageUtils.isCallable(activity, mapIntent)) {
            startActivity(mapIntent)
        }
    }

    private fun openActivityOnGoogleMapsInBrowser(location: String) {
        //https://www.google.com/maps/search/?api=1&query=centurylink+field
        val mapIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$location"))
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (PackageUtils.isCallable(activity, mapIntent)) {
            startActivity(mapIntent)
        }
    }
}
