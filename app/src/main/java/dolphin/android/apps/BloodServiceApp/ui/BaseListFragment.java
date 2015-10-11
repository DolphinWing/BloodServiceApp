package dolphin.android.apps.BloodServiceApp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;

import dolphin.android.apps.BloodServiceApp.MyApplication;
import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.DonateDay;
import dolphin.android.apps.BloodServiceApp.ui.dummy.DummyContent;

/**
 * Created by dolphin on 2014/10/10.
 */
public class BaseListFragment extends Fragment
        implements AbsListView.OnItemClickListener, MainActivity.OnBloodCenterChanged {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "site_id";
    public static final String ARG_PARAM2 = "time_in_millis";

    private boolean mAutoGridView = false;

    public void setAutoGridView(boolean auto) {
        mAutoGridView = auto;
    }

    // TODO: Rename and change types of parameters
    private int mSiteId;
    private Calendar mTime = Calendar.getInstance();

    protected OnFragmentInteractionListener mListener;
    private MainActivity mActivity;

    /**
     * The fragment's ListView/GridView.
     */
    protected AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    protected ListAdapter mAdapter;

    public static Bundle getArgBundle(int siteId, long timeInMillis) {
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, siteId);
        args.putLong(ARG_PARAM2, timeInMillis);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSiteId = getArguments().getInt(ARG_PARAM1, 0);
            mTime.setTimeInMillis(getArguments().getLong(ARG_PARAM2,
                    System.currentTimeMillis()));
        }

        // TODO: Change Adapter to display your content
        if (!getResources().getBoolean(R.bool.eng_mode)) {
            mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1, DummyContent.ITEMS);
        } else {
            mAdapter = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(mAutoGridView ? R.layout.fragment_donation
                : R.layout.fragment_donation_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        if (activity instanceof MainActivity) {
            mActivity = ((MainActivity) activity);
            mActivity.registerOnBloodCenterChanged(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mActivity != null) {
            mActivity.unregisterOnBloodCenterChanged(this);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        if (mListView != null) {
            View emptyView = mListView.getEmptyView();

            if (emptyView != null && emptyText instanceof TextView) {
                ((TextView) emptyView).setText(emptyText);
            }
        }
    }

    public int getSiteId() {
        return mSiteId;
    }

    public Calendar getTime() {
        return mTime;
    }

    public String getSimpeTimeString() {
        return DonateDay.getSimpleDateTimeString(getTime());
    }

    public void setListAdapter(ListAdapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null && mListView != null) {
            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        }
    }

    public void updateFragment(int siteId, long timeInMillis) {
        if (siteId >= 0) {
            mSiteId = siteId;
        }
        if (timeInMillis > 0) {
            mTime.setTimeInMillis(timeInMillis);
        }
        //TODO: override me to udpate the fragment
        setEmptyText(getString(R.string.title_data_not_available));
    }

    @Override
    public void notifyChanged(int siteId, long timeInMillis) {
        updateFragment(siteId, timeInMillis);
    }

    private String mFragmentId;

    public void setFragmentId(String id) {
        mFragmentId = id;
    }

    public String getFragmentId() {
        return mFragmentId;
    }

    public void setFragmentBusy(boolean busy) {
        if (mListener != null) {
            if (busy) {
                mListener.onUpdateStart(getFragmentId());
            } else {
                mListener.onUpdateComplete(getFragmentId());
            }
        }
    }

    public void sendDownloadCost(String action, long timeInMillis) {
        String label = "";
        if (timeInMillis < 1000) {
            label = "<1";
        } else if (timeInMillis < 3000) {
            label = "1~3";
        } else if (timeInMillis < 5000) {
            label = "3~5";
        } else if (timeInMillis < 7000) {
            label = "5~7";
        } else {
            label = ">7";
        }

        // Get tracker.
        Tracker t = null;
        if (getActivity() != null) {
            t = ((MyApplication) getActivity().getApplication())
                    .getTracker(MyApplication.TrackerName.APP_TRACKER);
        }
        if (t != null) {
            // Set screen name.
            // Where path is a String representing the screen name.
            t.setScreenName("dolphin.android.apps.BloodServiceApp.MainActivity");
            // This event will also be sent with &cd=Home%20Screen.
            // Build and send an Event.
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Network")
                    .setAction(action)
                    .setLabel(label)
                    //.setValue(timeInMillis) //[33]dolphin++
                    .build());
            t.send(new HitBuilders.TimingBuilder()
                    .setCategory("Network")
                    .setValue(timeInMillis)
                    .setVariable(action).build());
            // Clear the screen name field when we're done.
            t.setScreenName(null);
        }
    }

    public void sendDownloadException(String desc, boolean fatal) {
        // Get tracker.
        Tracker t = null;
        if (getActivity() != null) {
            t = ((MyApplication) getActivity().getApplication())
                    .getTracker(MyApplication.TrackerName.APP_TRACKER);
        }
        if (t != null) {
            // Set screen name.
            // Where path is a String representing the screen name.
            t.setScreenName("dolphin.android.apps.BloodServiceApp.MainActivity");
            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(desc)
                    .setFatal(fatal)
                    .build());
            // Clear the screen name field when we're done.
            t.setScreenName(null);
        }
    }
}
