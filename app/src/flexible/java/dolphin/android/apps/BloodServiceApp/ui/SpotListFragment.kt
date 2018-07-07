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
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import eu.davidea.viewholders.ExpandableViewHolder
import eu.davidea.viewholders.FlexibleViewHolder

class SpotListFragment : Fragment(),FlexibleAdapter.OnItemClickListener {
    companion object {
        private const val TAG = "SpotListFragment"
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
        swipeRefreshLayout?.apply {
            //isEnabled = false
            isRefreshing = true
        }
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
        viewModel?.getSpotData(siteId)?.observe(this, Observer { spots ->
            Log.d(TAG, "spot list: ${spots?.size()}")
            val list = ArrayList<IFlexible<*>>()
            viewModel?.getCityKey()?.forEach { cityId ->
                //Log.d(TAG, "city: $cityId ${spots?.get(cityId.toInt())}")
                val cityItem = CityItem(viewModel?.getCityName(cityId.toInt()) ?: cityId.toString())
                list.add(cityItem)
                spots?.get(cityId.toInt())?.locations?.forEach {
                    SpotItem(cityItem, it)
                    //list.add(SpotItem(cityItem, it))
                }
            }
            recyclerView?.adapter = FlexibleAdapter(list, this).apply {
                setStickyHeaders(true)
                setDisplayHeadersAtStartUp(true)
                expandItemsAtStartUp()
            }
            swipeRefreshLayout?.apply {
                isRefreshing = false
                isEnabled = false
            }
        })
    }

    internal class CityItem(private val city: String)
        : AbstractExpandableHeaderItem<ExpandableViewHolder, SpotItem>(), IHeader<ExpandableViewHolder> {
        override fun createViewHolder(view: View?,
                                      adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?)
                : ExpandableViewHolder = CityHolder(view, adapter)

        override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                    holder: ExpandableViewHolder?, position: Int, list: MutableList<Any>?) {
            (holder as? CityHolder)?.title?.text = city
        }

        override fun equals(other: Any?): Boolean = (other as? CityItem)?.city == city

        override fun hashCode(): Int = city.hashCode()

        override fun getLayoutRes(): Int = R.layout.listview_spot_city

        internal class CityHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?)
            : ExpandableViewHolder(view, adapter, true) {
            val title: TextView? = view?.findViewById(android.R.id.title)
            override fun shouldNotifyParentOnClick(): Boolean = true
        }
    }

    internal class SpotItem(header: CityItem, private val spot: SpotInfo)
        : AbstractSectionableItem<FlexibleViewHolder, CityItem>(header) {
        init {
            this.header = header
            header.addSubItem(this)
        }

        override fun createViewHolder(view: View?,
                                      adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?)
                : FlexibleViewHolder = SpotHolder(view, adapter)

        override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                    holder: FlexibleViewHolder?, position: Int, list: MutableList<Any>?) {
            (holder as? SpotHolder)?.apply { location?.text = spot.spotName }
        }

        override fun equals(other: Any?): Boolean = (other as? SpotItem)?.spot == spot

        override fun hashCode(): Int = spot.hashCode()

        override fun getLayoutRes(): Int = R.layout.listview_spot_location

        internal class SpotHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?)
            : FlexibleViewHolder(view, adapter) {
            val location: TextView? = view?.findViewById(android.R.id.title)
            //override fun shouldNotifyParentOnClick(): Boolean = true
        }
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        Log.d(TAG, "click $position")
        return true
    }
}