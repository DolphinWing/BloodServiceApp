@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.DonateDay
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.*
import eu.davidea.viewholders.FlexibleViewHolder

class DonationListFragment : Fragment() {
    companion object {
        private const val TAG = "DonationListFragment"
    }

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var viewModel: DataViewModel? = null
    private var siteId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DataViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        swipeRefreshLayout = contentView.findViewById(android.R.id.progress)
        swipeRefreshLayout?.isRefreshing = true
        recyclerView = contentView.findViewById(android.R.id.list)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = SmoothScrollLinearLayoutManager(activity!!)
        }
        queryData()
        return contentView
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args?.containsKey("site_id") == true) {
            siteId = args.getInt("site_id", -1)
            queryData()
        }
    }

    private fun queryData() {
        viewModel?.getDonationData(siteId)?.observe(this, Observer {
            Log.d(TAG, "donation list: ${it?.size}")
            val list = ArrayList<AbstractFlexibleItem<*>>()
            it?.forEach {
                //Log.d(TAG, "${it.dateString} has ${it.activityCount}")
                val dateItem = DateItem(it)
                //list.add(dateItem)
                it.activities.forEach {
                    //Log.d(TAG, "  ${it.name} @ ${it.location}")
                    list.add(ActivityItem(dateItem, it))
                }
            }
            recyclerView?.adapter = FlexibleAdapter(list).apply {
                setStickyHeaders(true)
                setDisplayHeadersAtStartUp(true)
            }
            swipeRefreshLayout?.isRefreshing = false
            swipeRefreshLayout?.isEnabled = false
        })
    }

    internal class DateItem(private val day: DonateDay)
        : AbstractHeaderItem<FlexibleViewHolder>(), IHeader<FlexibleViewHolder> {

        override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                    holder: FlexibleViewHolder?, position: Int, list: MutableList<Any>?) {
            (holder as? DateHolder)?.apply {
                title?.text = day.dateString
                count?.text = day.activityCount.toString()
            }
        }

        override fun equals(other: Any?) = (other as? DateItem)?.day?.timeInMillis == day.timeInMillis

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

    internal class ActivityItem(header: DateItem?, private val activity: DonateActivity)
        : AbstractSectionableItem<FlexibleViewHolder, DateItem>(header) {
        init {
            this.header = header
        }

        override fun createViewHolder(view: View?,
                                      adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?)
                : FlexibleViewHolder = ActivityHolder(view, adapter)

        override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                    holder: FlexibleViewHolder?, position: Int, list: MutableList<Any>?) {
            (holder as? ActivityHolder)?.apply {
                title?.text = activity.name
                location?.text = activity.location
                duration?.text = activity.duration
            }
        }

        override fun equals(other: Any?) = (other as? ActivityItem)?.activity == activity

        override fun hashCode(): Int = activity.hashCode()

        override fun getLayoutRes(): Int = R.layout.listview_donation_activity

        internal class ActivityHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?)
            : FlexibleViewHolder(view, adapter) {
            val title: TextView? = view?.findViewById(android.R.id.title)
            val location: TextView? = view?.findViewById(android.R.id.text1)
            val duration: TextView? = view?.findViewById(android.R.id.text2)
        }
    }
}