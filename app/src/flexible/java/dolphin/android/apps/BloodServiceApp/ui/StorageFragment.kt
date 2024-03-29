@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import dolphin.android.apps.BloodServiceApp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class StorageFragment : Fragment() {
    companion object {
        private const val TAG = "StorageFragment"
    }

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var adView: AdView
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.siteId.observe(
            this,
            Observer { id ->
                queryData(id)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.fragment_recycler_view_with_ads, container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(android.R.id.progress)
        swipeRefreshLayout?.isRefreshing = true
        recyclerView = view.findViewById(android.R.id.list)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = SmoothScrollLinearLayoutManager(requireActivity())
        }
        adView = view.findViewById(android.R.id.custom)
//        Handler().postDelayed({
//            loadAds()
//        }, 500) //delay load make ui show first
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adView.destroy()
    }

    private fun loadAds() {
        adView.loadAd(
            AdRequest.Builder()
                // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        )
    }

    private fun queryData(siteId: Int) {
        activity?.runOnUiThread {
            swipeRefreshLayout?.isEnabled = true
            swipeRefreshLayout?.isRefreshing = true
            recyclerView?.contentDescription = getString(R.string.title_downloading_data)
        }
        viewModel.getStorageData()?.observe(
            viewLifecycleOwner,
            Observer {
                val list = ArrayList<ItemView>()
                it?.get(siteId)?.let { map ->
                    Log.v(TAG, "site id = $siteId")
                    Log.v(TAG, "  A = ${map["A"]}")
                    list.add(ItemView(requireActivity(), "A", map["A"]!!))
                    Log.v(TAG, "  B = ${map["B"]}")
                    list.add(ItemView(requireActivity(), "B", map["B"]!!))
                    Log.v(TAG, "  O = ${map["O"]}")
                    list.add(ItemView(requireActivity(), "O", map["O"]!!))
                    Log.v(TAG, "  AB = ${map["AB"]}")
                    list.add(ItemView(requireActivity(), "AB", map["AB"]!!))
                }
                recyclerView?.adapter = FlexibleAdapter(list)
                recyclerView?.contentDescription = null
                swipeRefreshLayout?.isRefreshing = false
                swipeRefreshLayout?.isEnabled = false

                loadAds()
            }
        )
    }

    internal class ItemView(context: Context, private val type: String, private val status: Int) :
        AbstractFlexibleItem<ItemView.ItemHolder>() {
        private val bloodTypeText = context.resources.getStringArray(R.array.blood_type)
        private val bloodStorage = context.resources.getStringArray(R.array.blood_storage_status)
        private val bloodStatusIcon = intArrayOf(
            android.R.color.black,
            R.drawable.ic_storage_stock1,
            R.drawable.ic_storage_stock2,
            R.drawable.ic_storage_stock3
        )

        @SuppressLint("SetTextI18n")
        override fun bindViewHolder(
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
            holder: ItemHolder?,
            position: Int,
            list: MutableList<Any>?
        ) {
            holder?.apply {
                text?.text = "${bloodTypeText[position]}${bloodStorage[status]}"
                icon?.setImageResource(bloodStatusIcon[status])
            }
        }

        override fun equals(other: Any?): Boolean = (other as? ItemView)?.type == type

        override fun hashCode(): Int = type.hashCode()

        override fun createViewHolder(
            view: View?,
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?
        ): ItemHolder =
            ItemHolder(view, adapter)

        override fun getLayoutRes(): Int = R.layout.listview_storage

        internal class ItemHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?) :
            FlexibleViewHolder(view, adapter) {
            var icon: ImageView? = view?.findViewById(android.R.id.icon)
            var text: TextView? = view?.findViewById(android.R.id.title)
        }
    }
}
