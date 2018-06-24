@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dolphin.android.apps.BloodServiceApp.R
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager

class DonationListFragment : Fragment() {
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        swipeRefreshLayout = contentView.findViewById(android.R.id.progress)
        recyclerView = contentView.findViewById(android.R.id.list)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = SmoothScrollLinearLayoutManager(activity!!)
        }
        return contentView
    }
}