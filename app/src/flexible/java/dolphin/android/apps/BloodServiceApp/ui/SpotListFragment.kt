@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dolphin.android.apps.BloodServiceApp.R
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager

class SpotListFragment : Fragment() {
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        swipeRefreshLayout = contentView.findViewById(android.R.id.progress)
        swipeRefreshLayout?.apply {
            isEnabled = false
        }
        recyclerView = contentView.findViewById(android.R.id.list)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = SmoothScrollLinearLayoutManager(activity!!)
        }
        return contentView
    }
}