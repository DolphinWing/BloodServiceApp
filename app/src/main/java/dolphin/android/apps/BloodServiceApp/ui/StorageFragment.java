package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dolphin.android.apps.BloodServiceApp.MyApplication;
import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;

/**
 * blood storage list fragment
 */
public class StorageFragment extends BaseListFragment {
    private final static String TAG = "StorageFragment";
    private final static boolean DEBUG_LOG = false;

    private AdView mAdView;
    private View mProgressView;

    // TODO: Rename and change types of parameters
    public static StorageFragment newInstance(int siteId, long timeInMillis) {
        StorageFragment fragment = new StorageFragment();
        fragment.setArguments(getArgBundle(siteId, timeInMillis));
        fragment.setFragmentId("storage");
        return fragment;
    }

    private String[] mBloodType;
    private String[] mBloodTypeName;
    private String[] mBloodStorage;
    private SparseArray<ArrayList<Integer>> mBloodStorageCache = new SparseArray<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StorageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG_LOG) {
            Log.d(TAG, "onCreate");
        }
        mBloodType = getResources().getStringArray(R.array.blood_type_value);
        mBloodTypeName = getResources().getStringArray(R.array.blood_type);
        mBloodStorage = getResources().getStringArray(R.array.blood_storage_status);

        if (getSiteId() > 0) {
            updateFragment(-1, -1);//refresh ui
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG_LOG) {
            Log.d(TAG, "onCreateView");
        }
        View view = inflater.inflate(R.layout.fragment_storage, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
        }
        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));

        mProgressView = view.findViewById(android.R.id.progress);//[35]

        mAdView = (AdView) view.findViewById(R.id.adView);
        //hide ADs if user choose not to show it
        if (!PrefsUtil.isEnableAdView(getActivity())) {
            Log.w(TAG, "no ads...");
            mAdView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (DEBUG_LOG) {
            Log.d(TAG, String.format("onActivityCreated %d", mAdView.getVisibility()));
        }
        if (mAdView == null || mAdView.getVisibility() != View.VISIBLE) {
            return;//to avoid possible NullPointerException
        }

        loadAds();
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (DEBUG_LOG) {
            Log.d(TAG, "onPause");
        }
        if (mAdView != null && mAdView.getVisibility() == View.VISIBLE) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG_LOG) {
            Log.d(TAG, String.format("onResume %d %s", mAdView.getVisibility(),
                    PrefsUtil.isEnableAdView(getActivity())));
        }
        if (mAdView != null) {
            if (mAdView.getVisibility() == View.VISIBLE) {//previous yes
                mAdView.resume();
                if (!PrefsUtil.isEnableAdView(getActivity())) {//yes -> no
                    Log.v(TAG, "yes to no... that's fine");
                    mAdView.setVisibility(View.GONE);
                    try {
                        mAdView.destroy();
                    } catch (Exception e) {
                        Log.e(TAG, "destroy ads exception: " + e.getMessage());
                    }
                }
            } else if (PrefsUtil.isEnableAdView(getActivity())) {//no -> yes
                Log.v(TAG, "no to yes... THANK YOU!!!");
                //send a request to server
                loadAds();
            }
        }

        //disable loading animation
        List<Integer> list = mBloodStorageCache.get(getSiteId());
        if (list != null) {//already has data, so update GUI directly
            if (DEBUG_LOG) {
                Log.d(TAG, "[onResume] already get from server, don't update from server");
            }
            setListAdapter(new MyAdapter(getActivity(), list));
            setFragmentBusy(false);
        } else if (!isFragmentBusy() && getSiteId() > 0) {
            //no data, and not loading, get again from server
            if (DEBUG_LOG) {
                Log.d(TAG, "[onResume] no loaded, read from server site id = " + getSiteId());
            }
            updateFragment(-1, -1);//refresh ui
        } else {//already updating... so we don't do anything
            if (DEBUG_LOG) {
                Log.d(TAG, "[onResume] do nothing, site id = " + getSiteId());
            }
        }
    }

    private void loadAds() {
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mAdView.loadAd(adRequest);
            }
        });
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void updateFragment(int siteID, long timeInMillis) {
        super.updateFragment(siteID, timeInMillis);
        if (DEBUG_LOG) {
            Log.d(TAG, "updateFragment " + siteID);
        }
        if (this.isRemoving() || this.isDetached() || getActivity() == null) {
            setFragmentBusy(false);
            Log.w(TAG, "not attached to Activity");
            return;
        } else if (isFragmentBusy()) {
            return;//don't update when fragment is busy
        }
        setFragmentBusy(true);
        setEmptyText(getText(R.string.title_downloading_data));
        setListAdapter(new MyAdapter(getActivity(), new ArrayList<Integer>()));
        //setListAdapter(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadBloodStorage();
            }
        }).start();
    }

    private void downloadBloodStorage() {
        long start = System.currentTimeMillis();

        MyApplication application = (MyApplication) getActivity().getApplication();
        //BloodDataHelper helper = new BloodDataHelper(getActivity());
        //SparseArray<HashMap<String, Integer>> array = helper.getBloodStorage(false);
        SparseArray<HashMap<String, Integer>> array = application.getCacheBloodStorage();
        if (array == null) {//check cache
            BloodDataHelper helper = new BloodDataHelper(getActivity());
            array = helper.getBloodStorage();
            application.setCacheBloodStorage(array);//cache it
        }
        HashMap<String, Integer> map = array.get(getSiteId());

        final ArrayList<Integer> list = new ArrayList<>();
        if (map == null) {
            sendDownloadException("storage array empty", false);
        } else {//check blood storage
            for (String bloodType : mBloodType) {
                list.add(map.get(bloodType));
            }
            mBloodStorageCache.put(getSiteId(), list);
            long cost = System.currentTimeMillis() - start;
            if (getActivity() != null) {
                sendDownloadCost(getString(R.string.title_section1), cost);
            }
        }

        //[48]++ java.lang.IllegalStateException: Fragment not attached to Activity
        if (this.isRemoving() || this.isDetached() || getActivity() == null) {
            Log.w(TAG, "Fragment not attached to Activity");
            setFragmentBusy(false);
            return;//no need to update
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    setListAdapter(new MyAdapter(getActivity(), list));
                    setEmptyText(getString(R.string.title_data_not_available));
                }
                setFragmentBusy(false);
            }
        });
    }

    private final static int[] Icons = {
            //Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN
            android.R.color.black,
            R.drawable.ic_storage_stock1,
            R.drawable.ic_storage_stock2,
            R.drawable.ic_storage_stock3
    };

    private class MyAdapter extends ArrayAdapter<Integer> {

        public MyAdapter(Context context, List<Integer> objects) {
            super(context, R.layout.listview_storage, android.R.id.title, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View layout = super.getView(position, convertView, parent);
            //noinspection ConstantConditions
            int index = getItem(position);
            ImageView icon = (ImageView) layout.findViewById(android.R.id.icon);
            icon.setImageResource(Icons[index]);
            //icon.setBackgroundResource(Icons[index]);
            TextView title = (TextView) layout.findViewById(android.R.id.title);
            title.setText(String.format("%s%s", mBloodTypeName[position],
                    mBloodStorage[index]));
            return layout;
        }
    }

    private boolean mIsBusy = false;

    @Override
    public void setFragmentBusy(boolean busy) {
        super.setFragmentBusy(busy);
        if (DEBUG_LOG) {
            Log.d(TAG, "setFragmentBusy " + busy);
        }
        mIsBusy = busy;
        if (getActivity() != null && mProgressView != null) {//[35]
            mProgressView.setVisibility(busy ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isFragmentBusy() {
        return mIsBusy;
    }
}
