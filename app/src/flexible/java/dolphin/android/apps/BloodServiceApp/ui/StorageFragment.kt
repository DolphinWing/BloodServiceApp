@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import dolphin.android.apps.BloodServiceApp.R
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager

class StorageFragment : Fragment() {
    companion object {
        private const val TAG = "StorageFragment"
    }

    private var siteId: Int = -1

    private var recyclerView: RecyclerView? = null
    private lateinit var adView: AdView
    private var viewModel: DataViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.d(TAG, "onCreate")
        viewModel = ViewModelProviders.of(activity!!).get(DataViewModel::class.java)
        viewModel?.getStorageData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_recycler_view_with_ads, container, false)
        //Log.d(TAG, "onCreateView")
        recyclerView = contentView.findViewById(android.R.id.list)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = SmoothScrollLinearLayoutManager(activity!!)
        }
        adView = contentView.findViewById(android.R.id.custom)
        Handler().post {
            adView.loadAd(AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build())
        }
        viewModel?.getStorageData()?.observe(this, Observer {
            it?.get(siteId)?.let {
                Log.d(TAG, "site id = $siteId")
                Log.d(TAG, "  A = ${it["A"]}")
                Log.d(TAG, "  B = ${it["B"]}")
                Log.d(TAG, "  O = ${it["O"]}")
                Log.d(TAG, "  AB = ${it["AB"]}")

            }
        })
        return contentView
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args?.containsKey("site_id") == true) {
            siteId = args.getInt("site_id", -1)
            Log.d(TAG, "site id = $siteId")
            viewModel?.getStorageData()?.observe(this, Observer {
                it?.get(siteId)?.let {
                    Log.d(TAG, "site id = $siteId")
                    Log.d(TAG, "  A = ${it["A"]}")
                    Log.d(TAG, "  B = ${it["B"]}")
                    Log.d(TAG, "  O = ${it["O"]}")
                    Log.d(TAG, "  AB = ${it["AB"]}")

                }
            })
        }
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
}