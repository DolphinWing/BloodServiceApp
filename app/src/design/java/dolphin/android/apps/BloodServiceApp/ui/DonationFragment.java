package dolphin.android.apps.BloodServiceApp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity;
import dolphin.android.apps.BloodServiceApp.provider.DonateDay;

/**
 * Created by dolphin on 2015/03/15.
 * implements with RecyclerView
 */
public class DonationFragment extends BaseListFragment
        implements DonationListAdapter.OnItemClickListener {
    private final static String TAG = "DonationFragment";

    private RecyclerView mRecyclerView;
    private DonationListAdapter mAdapter;
    private View mProgressView;
    private View mEmptyView;

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
        View rootView = inflater.inflate(R.layout.fragment_donation_sticky_grid, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LayoutManager(getActivity()));
        mProgressView = rootView.findViewById(android.R.id.progress);//[35]
        mEmptyView = rootView.findViewById(android.R.id.empty);//[35]
        return rootView;
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

        mAdapter = new DonationListAdapter(getActivity(), days, this);

        long cost = System.currentTimeMillis() - start;
        if (getActivity() != null) {
            sendDownloadCost(getString(R.string.title_section2), cost);
        }

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.setAdapter(mAdapter);
                    if (mEmptyView != null) {//[35]
                        mEmptyView.setVisibility(mAdapter == null || mAdapter.getItemCount() <= 0
                                ? View.VISIBLE : View.GONE);
                    }
                    //setEmptyText(getText(R.string.title_data_not_available));
                    setFragmentBusy(false);
                }
            });
        }
    }

    @Override
    public void setFragmentBusy(boolean busy) {
        super.setFragmentBusy(busy);

        if (mProgressView != null) {//[35]
            mProgressView.setVisibility(busy ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onItemClicked(View view, Object data) {
        Log.d(TAG, "onItemClicked: " + data.toString());

        DonateActivity donation = (DonateActivity) data;
        final ArrayList<String> list = new ArrayList<>();
        list.add(donation.getName());
        list.add(donation.getLocation());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("You want to find")
                .setAdapter(new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_single_choice, list),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        Log.d(TAG, "pos = " + position);
                    }
                });
        builder.show();
    }
}
