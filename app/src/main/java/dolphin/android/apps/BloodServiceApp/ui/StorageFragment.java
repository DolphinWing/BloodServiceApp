package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Context;
import android.os.Bundle;
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
    private AdView mAdView;

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

        mAdView = (AdView) view.findViewById(R.id.adView);
        //hide ADs if user choose not to show it
        if (!PrefsUtil.isEnableAdView(getActivity())) {
            mAdView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        if (mAdView == null/* || mAdView.getVisibility() != View.VISIBLE*/) {
            return;//to avoid possible NullPointerException
        }

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
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
        if (mAdView != null) {
            mAdView.resume();
            if (PrefsUtil.isEnableAdView(getActivity())) {//no -> yes
                mAdView.setVisibility(View.VISIBLE);
            } else {//yes -> no
                mAdView.setVisibility(View.GONE);
                //mAdView.destroy();
                //mAdView = null;
            }
        }
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
        SparseArray<HashMap<String, Integer>> array = helper.getBloodStorage();
        HashMap<String, Integer> map = array.get(getSiteId());
        if (map == null) {
            sendDownloadException("storage array empty", false);
            return;
        }
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < mBloodType.length; i++) {
//            list.add(String.format("%s %s", mBloodTypeName[i],
//                    mBloodStorage[map.get(mBloodType[i])]));
            list.add(map.get(mBloodType[i]));
        }

        long cost = System.currentTimeMillis() - start;
        sendDownloadCost(getString(R.string.title_section1), cost);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setListAdapter(new MyAdapter(getActivity(), list));
                setFragmentBusy(false);
            }
        });
    }

    private final static int[] Icons = {
            //Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN
            android.R.color.black,
            android.R.color.holo_red_dark,//R.drawable.ic_storage_1,
            android.R.color.holo_orange_dark,//R.drawable.ic_storage_2,
            android.R.color.holo_green_dark,//R.drawable.ic_storage_3
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
            TextView title = (TextView) layout.findViewById(android.R.id.title);
            title.setText(String.format("%s %s", mBloodTypeName[position],
                    mBloodStorage[index]));
            return layout;
        }
    }
}
