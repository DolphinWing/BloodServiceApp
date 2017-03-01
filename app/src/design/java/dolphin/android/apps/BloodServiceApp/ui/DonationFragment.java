package dolphin.android.apps.BloodServiceApp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity;
import dolphin.android.apps.BloodServiceApp.provider.DonateDay;
import dolphin.android.util.PackageUtils;

/**
 * Created by dolphin on 2015/03/15.
 * implements with RecyclerView
 */
public class DonationFragment extends BaseListFragment
        implements DonationListAdapter.OnItemClickListener, View.OnClickListener {
    private final static String TAG = "DonationFragment";

    private RecyclerView mRecyclerView;
    private DonationListAdapter mAdapter;
    private View mProgressView;
    private View mEmptyView;

    private BottomSheetBehavior mBottomSheetBehavior;

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
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    return true;
                }
                return false;
            }
        });
        mProgressView = rootView.findViewById(android.R.id.progress);//[35]
        mEmptyView = rootView.findViewById(android.R.id.empty);//[35]

        View bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        if (bottomSheet != null) {
            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                private final static boolean DEBUG_LOG = false;

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    //Log.d(TAG, "onStateChanged: " + newState);
                    if (DEBUG_LOG) {
                        switch (newState) {
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                Log.d(TAG, "onStateChanged STATE_COLLAPSED");
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:
                                Log.d(TAG, "onStateChanged STATE_EXPANDED");
                                break;
                            case BottomSheetBehavior.STATE_HIDDEN:
                                Log.d(TAG, "onStateChanged STATE_HIDDEN");
                                break;
                            case BottomSheetBehavior.STATE_DRAGGING:
                                Log.d(TAG, "onStateChanged STATE_DRAGGING");
                                break;
                            case BottomSheetBehavior.STATE_SETTLING:
                                Log.d(TAG, "onStateChanged STATE_SETTLING");
                                break;
                            default:
                                Log.d(TAG, "onStateChanged: " + newState);
                                break;
                        }
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    //Log.d(TAG, "onSlide: " + slideOffset);
                }
            });
            //mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mBottomSheetBehavior.setPeekHeight(0);
            mBottomSheetBehavior.setHideable(true);

            View button1 = bottomSheet.findViewById(android.R.id.button1);
            if (button1 != null) {
                button1.setOnClickListener(this);
            }
            View button2 = bottomSheet.findViewById(android.R.id.button2);
            if (button2 != null) {
                button2.setOnClickListener(this);
            }
        } else {//try alternative one
            mBottomSheetBehavior = BottomSheetBehavior.from(rootView);
        }
        return rootView;
    }

    @Override
    public void updateFragment(int siteID, long timeInMillis) {
        super.updateFragment(siteID, timeInMillis);

        if (this.isRemoving() || this.isDetached() || getActivity() == null) {
            setFragmentBusy(false);
            Log.w(TAG, "not attached to Activity");
        } else if (!isFragmentBusy()) {//only start update when it is idle
            setFragmentBusy(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadDonateActivities();
                }
            }).start();
        } else {
            Log.w(TAG, "already updating " + siteID);
        }
    }

    private void downloadDonateActivities() {
        long start = System.currentTimeMillis();

        BloodDataHelper helper = new BloodDataHelper(getActivity());
        ArrayList<DonateDay> days = helper.getLatestWeekCalendar(getSiteId());
        if (days == null) {
            sendDownloadException("donation array empty", false);
            setFragmentBusy(false);
            return;
        }

        //[48]++ java.lang.IllegalStateException: Fragment not attached to Activity
        if (this.isRemoving() || this.isDetached()) {
            setFragmentBusy(false);
            return;//no need to update
        }

        mAdapter = new DonationListAdapter(getActivity(), days, this);

        long cost = System.currentTimeMillis() - start;
        if (getActivity() != null) {
            sendDownloadCost(getString(R.string.title_section2), cost);
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

    private boolean mIsBusy = false;

    @Override
    public void setFragmentBusy(boolean busy) {
        super.setFragmentBusy(busy);
        mIsBusy = busy;
        if (mProgressView != null) {//[35]
            mProgressView.setVisibility(busy ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isFragmentBusy() {
        return mIsBusy;
    }

    private DonateActivity mDonateActivity;

    @Override
    public void onItemClicked(View view, Object data) {
        //Log.d(TAG, "onItemClicked: " + data.toString());
        if (!PrefsUtil.isEnableSearchOnMap(getActivity())) {
            return;
        }

        if (data != null && data instanceof DonateActivity) {//store the data
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mDonateActivity = (DonateActivity) data;
        } else {//bottom list item callback or no data to handle, so hide bottom sheet
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case android.R.id.button1:
                showSearchMapDialog(mDonateActivity);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case android.R.id.button2:
                addToCalendar(mDonateActivity);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    private void showSearchMapDialog(DonateActivity donation) {
        final ArrayList<String> list = prepareList(donation);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_search_on_maps)
                .setAdapter(new ArrayAdapter<>(getActivity(),
                                //android.R.layout.simple_selectable_list_item, list),
                                R.layout.spinner_dropdown_textview, list),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                //Log.d(TAG, "pos = " + position);
                                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + list.get(position));
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        });
        builder.show();
    }

    private ArrayList<String> prepareList(DonateActivity donation) {
        ArrayList<String> list = new ArrayList<>();

        list.add(donation.getName());
        if (donation.getName().contains("(")) {
            String[] name = donation.getName().split("\\(");
            list.add(name[0]);
            if (name[1].contains(")")) {
                list.add(name[1].split("\\)")[0]);
            } else {
                list.add(name[1]);
            }
        }
        if (donation.getName().contains("（")) {
            String[] name = donation.getName().split("（");
            list.add(name[0]);
            if (name[1].contains("）")) {
                list.add(name[1].split("）")[0]);
            } else {
                list.add(name[1]);
            }
        }

        String location = donation.getLocation();

        if (location.contains("-")) {
            String[] loc = location.split("-");
            for (String l : loc) {
                if (l.isEmpty()) {
                    continue;
                }
                String[] loc1 = splitByParentheses(l);
                for (String l1 : loc1) {
                    list.add(removeNumberTrailing(l1));
                }
            }
        } else if (location.contains("(")) {
            String[] loc = splitByParentheses(location);
            for (String l : loc) {
                list.add(removeNumberTrailing(l));
            }
        } else {
            String[] loc = splitByParentheses2(location);
            for (String l : loc) {
                list.add(removeNumberTrailing(l));
            }
        }

        //http://stackoverflow.com/a/203992
        Set<String> s = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(s);
        return list;
    }

    private String[] splitByParentheses(String location) {
        if (location.contains("(")) {
            String[] loc = location.split("\\(");
            for (int i = 0; i < loc.length; i++) {
                loc[i] = loc[i].contains(")") ? loc[i].substring(0, loc[i].indexOf(")")) : loc[i];
            }
            return loc;
        }
        return new String[]{location};
    }

    private String[] splitByParentheses2(String location) {
        String lparen = getString(R.string.search_on_map_split_lparen);
        String rparen = getString(R.string.search_on_map_split_rparen);
        if (location.contains(lparen)) {
            String[] loc = location.split(lparen);
            for (int i = 0; i < loc.length; i++) {
                loc[i] = loc[i].contains(rparen)
                        ? loc[i].substring(0, loc[i].indexOf(rparen))
                        : loc[i].contains(")") ? loc[i].substring(0, loc[i].indexOf(")")) : loc[i];
            }
            return loc;
        }
        return new String[]{location};
    }

    private String removeNumberTrailing(String location) {
        String num = getString(R.string.search_on_map_split_number);
        if (location.contains(num)) {
            return location.substring(0, location.indexOf(num) + 1);
        } else {
            //maybe some other patterns?
        }
        return location;
    }

    //Android Essentials: Adding Events to the User’s Calendar
    //http://goo.gl/jyT75l
    private void addToCalendar(DonateActivity donation) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, donation.getName());
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, donation.getLocation());
        //calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "description");
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                donation.getStartTime().getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                donation.getEndTime().getTimeInMillis());
        if (PackageUtils.isCallable(getActivity(), calIntent)) {
            startActivity(calIntent);
        }
    }
}
