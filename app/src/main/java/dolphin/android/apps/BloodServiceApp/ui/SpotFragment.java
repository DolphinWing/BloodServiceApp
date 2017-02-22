package dolphin.android.apps.BloodServiceApp.ui;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;
import dolphin.android.apps.BloodServiceApp.provider.SpotList;

/**
 * Donation Spot list.
 * Created by jimmyhu on 2017/2/21.
 */

public class SpotFragment extends BaseListFragment {
    private final static String TAG = "SpotFragment";
    private final static boolean DEBUG_LOG = false;

    public static SpotFragment newInstance(int siteId, long timeInMillis) {
        if (DEBUG_LOG) {
            Log.d(TAG, "newInstance " + siteId);
        }
        SpotFragment fragment = new SpotFragment();
        fragment.setArguments(getArgBundle(siteId, timeInMillis));
        fragment.setFragmentId("spot");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG_LOG) {
            Log.d(TAG, "onCreate");
        }
        super.onCreate(savedInstanceState);

        if (getSiteId() > 0) {
            updateFragment(-1, -1);//refresh ui
        }
    }

    @Override
    public void updateFragment(int siteId, long timeInMillis) {
        super.updateFragment(siteId, timeInMillis);
        if (DEBUG_LOG) {
            Log.d(TAG, "updateFragment " + siteId);
        }
        if (isFragmentBusy()) {//don't update when busy
            Log.w(TAG, "still working...");
            return;
        }
        setFragmentBusy(true);
        setEmptyText(getText(R.string.title_downloading_data));
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadDonationSpotLocationMap();
            }
        }).start();
    }

    private void downloadDonationSpotLocationMap() {
        BloodDataHelper helper = new BloodDataHelper(getActivity());
        SparseArray<SpotList> list = helper.getDonationSpotLocationMap(getSiteId());
        if (list != null) {
            Log.d(TAG, String.format("cities: %d", list.size()));
        }

        if (this.isRemoving() || this.isDetached() || getActivity() == null) {
            setFragmentBusy(false);
            return;//no need to update
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    //setListAdapter(new StorageFragment.MyAdapter(getActivity(), list));
                    setEmptyText(getString(R.string.title_data_not_available));
                }
                setFragmentBusy(false);
            }
        });
    }

    private boolean mIsBusy = false;

    @Override
    public void setFragmentBusy(boolean busy) {
        //super.setFragmentBusy(busy);
        mIsBusy = busy;
    }

    private boolean isFragmentBusy() {
        return mIsBusy;
    }
}
