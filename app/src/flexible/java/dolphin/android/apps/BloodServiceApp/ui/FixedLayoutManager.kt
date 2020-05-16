@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager

/**
 * https://www.jianshu.com/p/2eca433869e9
 */
class FixedLayoutManager(context: Context) : SmoothScrollLinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}