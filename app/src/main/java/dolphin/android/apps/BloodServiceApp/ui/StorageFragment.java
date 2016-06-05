package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class StorageFragment extends BaseListFragment {
    private final static String TAG = "StorageFragment";
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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StorageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
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
        View view = inflater.inflate(R.layout.fragment_storage_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        if (mAdapter != null) {
            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        }
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
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

        Log.d(TAG, String.format("onActivityCreated %d", mAdView.getVisibility()));
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
        Log.d(TAG, "onPause");
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

        Log.d(TAG, String.format("onResume %d %s", mAdView.getVisibility(),
                PrefsUtil.isEnableAdView(getActivity())));
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

        BloodDataHelper helper = new BloodDataHelper(getActivity());
        SparseArray<HashMap<String, Integer>> array = helper.getBloodStorage(false);
        HashMap<String, Integer> map = array.get(getSiteId());

        //[48]++ java.lang.IllegalStateException: Fragment not attached to Activity
        if (this.isRemoving() || this.isDetached()) {
            return;//no need to update
        }

        final ArrayList<Integer> list = new ArrayList<Integer>();
        if (map == null) {
            sendDownloadException("storage array empty", false);
        } else {//check blood storage
            for (String bloodType : mBloodType) {
                list.add(map.get(bloodType));
            }
            long cost = System.currentTimeMillis() - start;
            if (getActivity() != null) {
                sendDownloadCost(getString(R.string.title_section1), cost);
            }
        }

        if (getActivity() != null) {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View layout = super.getView(position, convertView, parent);
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

    @Override
    public void setFragmentBusy(boolean busy) {
        super.setFragmentBusy(busy);

        if (mProgressView != null) {//[35]
            mProgressView.setVisibility(busy ? View.VISIBLE : View.GONE);
        }
    }
}
