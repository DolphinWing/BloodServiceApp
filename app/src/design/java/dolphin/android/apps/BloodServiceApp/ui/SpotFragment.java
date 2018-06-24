package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.idunnololz.widgets.AnimatedExpandableListView;

import java.util.ArrayList;

import dolphin.android.apps.BloodServiceApp.MyApplication;
import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo;
import dolphin.android.apps.BloodServiceApp.provider.SpotList;

/**
 * Donation Spot list.
 * Created by jimmyhu on 2017/2/21.
 */

public class SpotFragment extends BaseListFragment {
    private final static String TAG = "SpotFragment";
    private final static boolean DEBUG_LOG = false;
    private static boolean AUTO_COLLAPSE = true;

    private View mProgressView;

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

        //AUTO_COLLAPSE = getResources().getBoolean(R.bool.feature_enable_auto_collapse_expand_list_view);
        AUTO_COLLAPSE = FirebaseRemoteConfig.getInstance().getBoolean("enable_auto_collapse");

        if (getSiteId() > 0) {
            updateFragment(-1, -1);//refresh ui
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot_list, container, false);
        mListView = (ExpandableListView) view.findViewById(android.R.id.list);
        if (mListView != null) {
            mListView.setEmptyView(view.findViewById(android.R.id.empty));
            // In order to show animations, we need to use a custom click handler
            // for our ExpandableListView.
            if (mListView instanceof AnimatedExpandableListView) {
                final AnimatedExpandableListView listView = (AnimatedExpandableListView) mListView;
                listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    //https://github.com/idunnololz/AnimatedExpandableListView/
                    //http://stackoverflow.com/a/24181292
                    int previousGroup = 0;//auto expand 0

                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                                long id) {
                        // We call collapseGroupWithAnimation(int) and
                        // expandGroupWithAnimation(int) to animate group
                        // expansion/collapse.
                        if (listView.isGroupExpanded(groupPosition)) {
                            listView.collapseGroupWithAnimation(groupPosition);
                            previousGroup = -1;
                            //we must have data to get, so it should not have null pointer
                        } else if (AUTO_COLLAPSE) {
//                            if (previousGroup != -1 && listView.isGroupExpanded(previousGroup)) {
//                                listView.collapseGroupWithAnimation(previousGroup);
//                            }
//                            listView.expandGroupWithAnimation(groupPosition);
//                            previousGroup = groupPosition;

                            //http://pastebin.com/0jJ5Lbz0
                            if (previousGroup != -1 && mAdapter.getGroupCount() > previousGroup) {
                                //If we will now collapse group above us, we need to restore scroll
                                // position to keep user looking at the same position
                                boolean needsToRestore = previousGroup < listView.getFirstVisiblePosition();
                                //Log.d(TAG, "needsToRestore = " + needsToRestore);
                                View firstChild = listView.getChildAt(0);
                                int restoreYOffset = (firstChild == null)
                                        ? 0 : (firstChild.getTop() - listView.getPaddingTop());
                                //Log.d(TAG, "restoreYOffset = " + restoreYOffset);
                                if (listView.isGroupExpanded(previousGroup)) {
                                    listView.collapseGroupWithAnimation(previousGroup);
                                }
                                listView.expandGroupWithAnimation(groupPosition);
                                if (needsToRestore && mAdapter.getGroup(previousGroup) != null) {
                                    SpotList cityOld = (SpotList) mAdapter.getGroup(previousGroup);
                                    int restorePosition = listView.getFirstVisiblePosition() -
                                            cityOld.getLocations().size();
                                    //Log.d(TAG, "restorePosition = " + restorePosition);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        listView.setSelectionFromTop(restorePosition, restoreYOffset);
                                    } else {
                                        listView.setSelection(restorePosition);
                                    }
                                }
                                previousGroup = groupPosition;
                            } else {
                                //No group to collapse, so expand immediately
                                listView.expandGroupWithAnimation(groupPosition);
                                previousGroup = groupPosition;
                            }
                        } else {
                            listView.expandGroupWithAnimation(groupPosition);
                            previousGroup = groupPosition;
                        }
                        return true;
                    }

                });
            }
            if (mListView instanceof ExpandableListView) {
                final ExpandableListView listView = (ExpandableListView) mListView;
                listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View view,
                                                int groupPosition, int childPosition, long id) {
                        if (view.getTag() instanceof SpotInfo) {
                            Intent intent = BloodDataHelper.getOpenSpotLocationMapIntent(getActivity(),
                                    (SpotInfo) view.getTag());
                            if (intent != null) {//show in browser, don't parse it
                                startActivity(intent);
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        }

        mProgressView = view.findViewById(android.R.id.progress);
        return view;
    }

    private ExpandableListView getListView() {
        return (ExpandableListView) mListView;
    }

    @Override
    public void updateFragment(int siteId, long timeInMillis) {
        super.updateFragment(siteId, timeInMillis);
        if (DEBUG_LOG) {
            Log.d(TAG, "updateFragment " + siteId);
        }
        if (this.isRemoving() || this.isDetached() || getActivity() == null) {
            setFragmentBusy(false);
            Log.w(TAG, "not attached to Activity");
            return;
        } else if (isFragmentBusy()) {//don't update when busy
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

    private MyAdapter mAdapter;

    private void downloadDonationSpotLocationMap() {
        if (getActivity() == null) return;
        final BloodDataHelper helper = new BloodDataHelper(getActivity());
        MyApplication application = (MyApplication) getActivity().getApplication();
        SparseArray<SpotList> list = application.getCacheSpotList(getSiteId());
        if (list == null) {//check cache
            list = helper.getDonationSpotLocationMap(getSiteId());
            application.setCacheSpotList(getSiteId(), list, helper.getCityList());
        } else {//need to restore city names
            helper.setCityList(application.getCacheCityList(getSiteId()));
        }

        if (this.isRemoving() || this.isDetached() || getActivity() == null) {
            setFragmentBusy(false);
            return;//no need to update
        }

        final SparseArray<SpotList> spots = list;
        if (spots == null) {
            mAdapter = null;
        } else if (getActivity() != null) {
            //Log.d(TAG, String.format("cities: %d", spots.size()));
            mAdapter = new MyAdapter(getActivity(), helper, getSiteId(), spots);
        }
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && getListView() != null) {
                        //if (spots != null) {
                        //    Log.d(TAG, String.format("cities: %d", spots.size()));
                        getListView().setAdapter(mAdapter);
                        if (spots != null && spots.size() > 0) {
                            getListView().expandGroup(0);
                        }
                        //}
                        setEmptyText(getString(R.string.title_data_not_available));
                    }
                    setFragmentBusy(false);
                }
            });
    }

    private boolean mIsBusy = false;

    @Override
    public void setFragmentBusy(boolean busy) {
        super.setFragmentBusy(busy);
        mIsBusy = busy;
        if (getActivity() != null && mProgressView != null) {//[35]
            mProgressView.setVisibility(mIsBusy ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isFragmentBusy() {
        return mIsBusy;
    }

    /**
     * http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
     */
    private static class MyAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private final SparseArray<SpotList> mList;
        private final Context mContext;
        private final int mSiteId;
        private final BloodDataHelper mHelper;
        private final LayoutInflater mInflater;
        private final int[] mGroupId;

        MyAdapter(Context context, BloodDataHelper helper, int siteId, SparseArray<SpotList> list) {
            mContext = context;
            mHelper = helper;
            mSiteId = siteId;
            mList = list;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int[] ids = mContext.getResources().getIntArray(R.array.blood_center_id);
            String[] centers = mContext.getResources().getStringArray(R.array.blood_center_donate_station_city_id);
            int i;
            for (i = ids.length - 1; i > 0; i--) {
                if (ids[i] == mSiteId) {
                    break;
                }
            }
            String[] cities = centers[i].split(",");
            mGroupId = new int[cities.length];
            for (i = 0; i < cities.length; i++) {
                mGroupId[i] = Integer.parseInt(cities[i]);
            }
        }

//        static class MyList {
//            SparseArray<SpotList> spots;
//            int[] groups;
//        }
//
//        MyList getList() {
//            MyList list = new MyList();
//            list.spots = mList;
//            list.groups = mGroupId;
//            return list;
//        }

        @Override
        public int getGroupCount() {
            return mList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            //Log.d(TAG, String.format("g: %d, children: %d", groupPosition,
            //        mList.get(mGroupId[groupPosition]).getLocations().size()));
            return mList.get(mGroupId[groupPosition]);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            //Log.d(TAG, String.format("g: %d, c:%d", groupPosition, childPosition));
            if (mList.get(mGroupId[groupPosition]) == null) {
                return null;
            }
            ArrayList<SpotInfo> infos = mList.get(mGroupId[groupPosition]).getLocations();
            if (childPosition < infos.size()) {
                return infos.get(childPosition);
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition * 1000;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * 1000 + childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview_spot_city, parent, false);
            }

            TextView title = convertView.findViewById(android.R.id.title);
            if (title != null) {
                SpotList list = (SpotList) getGroup(groupPosition);
                if (list != null) {
                    title.setText(mHelper.getCityName(list.getCityId()));
                }
            }
            return convertView;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild,
                                     View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview_spot_location, parent, false);
            }

            TextView title = convertView.findViewById(android.R.id.title);
            if (title != null) {
                SpotInfo info = (SpotInfo) getChild(groupPosition, childPosition);
                if (info != null) {
                    title.setText(info.getSpotName());
                }
                convertView.setTag(info);
            }
            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            if (mList.get(mGroupId[groupPosition]) == null) {
                return 0;
            }
            return mList.get(mGroupId[groupPosition]).getLocations().size();
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
