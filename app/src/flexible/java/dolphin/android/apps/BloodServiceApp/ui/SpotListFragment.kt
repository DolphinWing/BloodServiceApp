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
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import eu.davidea.viewholders.ExpandableViewHolder
import eu.davidea.viewholders.FlexibleViewHolder

class SpotListFragment : Fragment(), FlexibleAdapter.OnItemClickListener {
    companion object {
        private const val TAG = "SpotListFragment"
    }

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var viewModel: DataViewModel? = null
    private var siteId = -1
    private var prefs: PrefsUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(DataViewModel::class.java)
        prefs = PrefsUtil(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        swipeRefreshLayout = contentView.findViewById(android.R.id.progress)
        swipeRefreshLayout?.apply {
            //isEnabled = false
            isRefreshing = true
        }
        recyclerView = contentView.findViewById(android.R.id.list)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = SmoothScrollLinearLayoutManager(requireActivity())
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
        activity?.runOnUiThread {
            swipeRefreshLayout?.isEnabled = true
            swipeRefreshLayout?.isRefreshing = true
            recyclerView?.contentDescription = getString(R.string.title_downloading_data)
        }
        Log.d(TAG, "query $siteId")
        viewModel?.getSpotData(siteId)?.observe(viewLifecycleOwner, Observer { spots ->
            Log.d(TAG, "spot list: ${spots!!.size}")
            val list = ArrayList<IFlexible<*>>()
            spots.forEach { city ->
                //Log.d(TAG, "city: $cityId ${spots?.get(cityId.toInt())}")
                val cityItem = CityItem(city.cityName ?: city.cityId.toString())
                list.add(cityItem)
                city.locations.forEach { spot ->
                    SpotItem(cityItem, spot)
                    //list.add(SpotItem(cityItem, it))
                }
            }
            val adapter = FlexibleAdapter(list, this).apply {
                //setStickyHeaders(prefs?.isHeaderSticky ?: true)
                setDisplayHeadersAtStartUp(true)
                expandItemsAtStartUp()
            }
            recyclerView?.adapter = adapter
//            try {
//                //fix Enable sticky headers after setting Adapter to RecyclerView
//                adapter.setStickyHeaders(prefs?.isHeaderSticky ?: false)
//            } catch (e: IllegalStateException) {
//                //try to catch the exception
//            }
            recyclerView?.contentDescription = null
            swipeRefreshLayout?.apply {
                isRefreshing = false
                isEnabled = false
            }
        })
    }

    internal class CityItem(private val city: String)
        : AbstractExpandableHeaderItem<ExpandableViewHolder, SpotItem>(),
            IHeader<ExpandableViewHolder> {
        override fun createViewHolder(view: View?,
                                      adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?)
                : ExpandableViewHolder = CityHolder(view, adapter)

        override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                    holder: ExpandableViewHolder?, position: Int,
                                    list: MutableList<Any>?) {
            (holder as? CityHolder)?.apply {
                title?.text = city
                title?.isActivated = isExpanded
            }
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
                                    holder: FlexibleViewHolder?, position: Int,
                                    list: MutableList<Any>?) {
            (holder as? SpotHolder)?.apply {
                location?.text = spot.spotName
                itemView.tag = spot
            }
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
        //Log.d(TAG, "click $position")
        (view?.tag as? SpotInfo)?.let { spot ->
            //Log.d(TAG, "spot: ${spot.spotId} ${spot.spotName}")
            BloodDataHelper.getOpenSpotLocationMapIntent(activity, spot)?.let { intent ->
                activity?.startActivity(intent) //show in browser, don't parse it
            }
        }
        return true
    }
}