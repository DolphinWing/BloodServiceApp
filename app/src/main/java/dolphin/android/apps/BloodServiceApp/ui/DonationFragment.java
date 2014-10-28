package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity;
import dolphin.android.apps.BloodServiceApp.provider.DonateDay;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class DonationFragment extends BaseListFragment {

    // TODO: Rename and change types of parameters
    public static DonationFragment newInstance(int siteId, long timeInMillis) {
        DonationFragment fragment = new DonationFragment();
        Bundle args = BaseListFragment.getArgBundle(siteId, timeInMillis);
        fragment.setArguments(args);
        fragment.setFragmentId("donation");
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DonationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSiteId() > 0) {
            updateFragment(-1, -1);//refresh ui
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation_sticky_grid,
                container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        if (mAdapter != null) {
            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        }
        if (mListView instanceof StickyGridHeadersGridView) {
            ((StickyGridHeadersGridView) mListView).setAreHeadersSticky(false);
        }

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void updateFragment(int siteID, long timeInMillis) {
        super.updateFragment(siteID, timeInMillis);
        setFragmentBusy(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadDonateActivities();
            }
        }).start();
    }

    private void downloadDonateActivities() {
        long start = System.currentTimeMillis();

        BloodDataHelper helper = new BloodDataHelper(getActivity());
        ArrayList<DonateDay> days = helper.getLatestWeekCalendar(getSiteId());
        if (days == null) {
            sendDownloadException("donation array empty", false);
            return;
        }

        final ArrayList<MyItem> list = new ArrayList<MyItem>();
        for (int i = 0; i < days.size(); i++) {
            DonateDay day = days.get(i);
            if (day.getActivityCount() > 0) {
                //list.add(new MyItem(day.getDateString()));
                List<DonateActivity> acts = day.getActivities();
                for (int j = 0; j < day.getActivityCount(); j++) {
                    //list.add(String.format("%s", day.getDateString()));
                    //list.add(day.toString());
                    list.add(new MyItem(acts.get(j)));
                }
            }
        }

        long cost = System.currentTimeMillis() - start;
        sendDownloadCost(getString(R.string.title_section2), cost);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setListAdapter(new MyStickyAdapter(getActivity(), list));
                setFragmentBusy(false);
            }
        });
    }

    private class MyItem {
        public final static int TYPE_DATE = 1;
        public final static int TYPE_LOCATION = 2;
        public String Title;
        public int Type;
        public DonateActivity Donation;

//        public MyItem(String title) {
//            Type = TYPE_DATE;
//            Title = title;
//        }

        public MyItem(DonateActivity activity) {
            Type = TYPE_LOCATION;
            Donation = activity;
        }
    }

    private class MyStickyAdapter extends StickyGridHeadersSimpleArrayAdapter<MyItem> {

        public MyStickyAdapter(Context context, List<MyItem> items) {
            super(context, items, R.layout.listview_donation_date,
                    R.layout.listview_donation_activity);
        }

        @Override
        public long getHeaderId(int position) {
            MyItem item = getItem(position);
            Calendar day = DonateDay.resetToMidNight(item.Donation.getStartTime());
            return day.getTimeInMillis();//super.getHeaderId(position);
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            View view = super.getHeaderView(position, convertView, parent);
            HeaderViewHolder holder = (HeaderViewHolder) view.getTag();
            MyItem item = getItem(position);
            holder.textView.setText(
                    DonateDay.getSimpleDateString(item.Donation.getStartTime()));
            return view;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View layout = super.getView(position, convertView, parent);
            MyItem item = getItem(position);
            //ImageView icon = (ImageView) layout.findViewById(android.R.id.icon);
            TextView title = (TextView) layout.findViewById(android.R.id.title);
            TextView tv1 = (TextView) layout.findViewById(android.R.id.text1);
            TextView tv2 = (TextView) layout.findViewById(android.R.id.text2);
            title.setText(item.Donation.getName());
            tv1.setText(item.Donation.getDuration());
            tv2.setText(item.Donation.getLocation());
            return layout;//super.getView(position, convertView, parent);
        }
    }
}
