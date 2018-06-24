package dolphin.android.apps.BloodServiceApp.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import dolphin.android.apps.BloodServiceApp.MyApplication;
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
public class DonationFragment extends BaseListFragment/*
        implements DonationListAdapter.OnItemClickListener, View.OnClickListener*/ {
    private final static String TAG = "DonationFragment";

    private RecyclerView mRecyclerView;
    private DonationListAdapter mAdapter;
    private View mProgressView;
    private View mEmptyView;
    //    private View mBottomSheetBackground;
//
//    private BottomSheetBehavior mBottomSheetBehavior;
    private FirebaseAnalytics mFirebaseAnalytics;

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

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSiteId() > 0) {
            updateFragment(-1, -1);//refresh ui
        }

        if (getActivity() != null) {
            MyApplication application = (MyApplication) getActivity().getApplication();
            if (application.isGooglePlayServiceSupported()) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            } else {
                mFirebaseAnalytics = null;
            }
        } else {
            mFirebaseAnalytics = null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_donation_sticky_grid, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LayoutManager(getActivity()));
//            mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                        return true;
//                    }
//                    return false;
//                }
//            });
        }
        mProgressView = rootView.findViewById(android.R.id.progress);//[35]
        mEmptyView = rootView.findViewById(android.R.id.empty);//[35]

//        View bottomSheet = rootView.findViewById(R.id.bottom_sheet);
//        if (bottomSheet != null) {
//            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                private final static boolean DEBUG_LOG = false;
//
//                @Override
//                public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                    //Log.d(TAG, "onStateChanged: " + newState);
//                    if (DEBUG_LOG) {
//                        switch (newState) {
//                            case BottomSheetBehavior.STATE_COLLAPSED:
//                                Log.d(TAG, "onStateChanged STATE_COLLAPSED");
//                                break;
//                            case BottomSheetBehavior.STATE_EXPANDED:
//                                Log.d(TAG, "onStateChanged STATE_EXPANDED");
//                                break;
//                            case BottomSheetBehavior.STATE_HIDDEN:
//                                Log.d(TAG, "onStateChanged STATE_HIDDEN");
//                                break;
//                            case BottomSheetBehavior.STATE_DRAGGING:
//                                Log.d(TAG, "onStateChanged STATE_DRAGGING");
//                                break;
//                            case BottomSheetBehavior.STATE_SETTLING:
//                                Log.d(TAG, "onStateChanged STATE_SETTLING");
//                                break;
//                            default:
//                                Log.d(TAG, "onStateChanged: " + newState);
//                                break;
//                        }
//                    }
//                }
//
//                @Override
//                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                    //Log.d(TAG, "onSlide: " + slideOffset);
//                }
//            });
//            //mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//            mBottomSheetBehavior.setPeekHeight(0);
//            mBottomSheetBehavior.setHideable(true);
//
//            View button1 = bottomSheet.findViewById(android.R.id.button1);
//            if (button1 != null) {
//                button1.setOnClickListener(this);
//            }
//            View button2 = bottomSheet.findViewById(android.R.id.button2);
//            if (button2 != null) {
//                button2.setOnClickListener(this);
//            }
//        } else {//try alternative one
//            mBottomSheetBehavior = BottomSheetBehavior.from(rootView);
//        }
//        mBottomSheetBackground = rootView.findViewById(R.id.bottom_sheet_background);
//        if (mBottomSheetBackground != null) {
//            mBottomSheetBackground.setOnTouchListener(new View.OnTouchListener() {
//                @SuppressLint("ClickableViewAccessibility")
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    //only hide when touch up, otherwise you will see duplicated animations
//                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                        showBottomSheet(false);
//                    }
//                    return true;
//                }
//            });
//        }
        return rootView;
    }

    private View contextMenuTarget = null;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        contextMenuTarget = v;//store it for animation
        if (contextMenuTarget != null) {
            contextMenuTarget.setBackgroundResource(R.drawable.bloody_list_activated_holo);
        }
        if (getActivity() != null) {//inflate the context menu
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_menu_donation, menu);
            if (v.getTag() != null && v.getTag() instanceof DonationListAdapter.DonationViewHolder) {
                mDonateActivity = (DonateActivity) ((DonationListAdapter.DonationViewHolder) v.getTag()).getData();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item != null) {
            String action = null;
            switch (item.getItemId()) {
                case R.id.action_search_location:
                    showSearchMapDialog(mDonateActivity);
                    action = getString(R.string.action_search_location);
                    break;
                case R.id.action_add_to_calendar:
                    addToCalendar(mDonateActivity);
                    action = getString(R.string.action_add_to_calendar);
                    break;
            }
            if (action != null) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,
                        BloodDataHelper.getBloodCenterName(getActivity(), getSiteId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, action);
                if (mFirebaseAnalytics != null) {
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        if (contextMenuTarget != null) {
            contextMenuTarget.setBackgroundResource(R.drawable.bloody_list_selector_holo_light);
            contextMenuTarget = null;//clear it
        }
    }

    @Override
    public void updateFragment(int siteID, long timeInMillis) {
        super.updateFragment(siteID, timeInMillis);

        if (this.isRemoving() || this.isDetached() || getActivity() == null) {
            setFragmentBusy(false);
            Log.w(TAG, "not attached to Activity");
        } else if (!isFragmentBusy()) {//only start update when it is idle
//            showBottomSheet(false);//no more bottom sheet
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
        if (getActivity() == null) {
            return;//don't do anything
        }
        MyApplication application = (MyApplication) getActivity().getApplication();
        ArrayList<DonateDay> days = application.getCacheDonationList(getSiteId());
        if (days == null) {//check cache
            long start = System.currentTimeMillis();
            BloodDataHelper helper = new BloodDataHelper(getActivity());
            days = helper.getLatestWeekCalendar(getSiteId());
            application.setCacheDonationList(getSiteId(), days);
            long cost = System.currentTimeMillis() - start;
            if (getActivity() != null) {//make sure we can send data
                sendDownloadCost(getString(R.string.title_section2), cost);
            }
        }
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

        mAdapter = new DonationListAdapter(this, days, null);

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mRecyclerView != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
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

//    @Override
//    public void onItemClicked(View view, Object data) {
//        //Log.d(TAG, "onItemClicked: " + data.toString());
//        if (!PrefsUtil.isEnableSearchOnMap(getActivity())) {
//            return;
//        }
//
//        if (data != null && data instanceof DonateActivity) {//store the data
////            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
////                //to have a animation that we select another one
////                showBottomSheet(false);
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        showBottomSheet(true);
////                    }
////                }, 100);
////            } else {
//            showBottomSheet(true);
////            }
//            mDonateActivity = (DonateActivity) data;
//
//            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,
//                    BloodDataHelper.getBloodCenterName(getActivity(), getSiteId()));
//            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.action_more));
//            if (mFirebaseAnalytics != null) {
//                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//            }
//        } else {//bottom list item callback or no data to handle, so hide bottom sheet
//            showBottomSheet(false);
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        String action = null;
//        switch (view.getId()) {
//            case android.R.id.button1:
//                showSearchMapDialog(mDonateActivity);
//                showBottomSheet(false);
//                action = getString(R.string.action_search_location);
//                break;
//            case android.R.id.button2:
//                addToCalendar(mDonateActivity);
//                showBottomSheet(false);
//                action = getString(R.string.action_add_to_calendar);
//                break;
//        }
//
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,
//                BloodDataHelper.getBloodCenterName(getActivity(), getSiteId()));
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, action);
//        if (mFirebaseAnalytics != null && action != null) {
//            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//        }
//    }

    private void showSearchMapDialog(DonateActivity donation) {
        if (getActivity() == null) {
            return;//don't do anything
        }
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
                                if (PrefsUtil.isGoogleMapsInstalled(getActivity())) {
                                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + list.get(position));
                                    openGoogleMaps(gmmIntentUri);
                                } else {
                                    openMapInBrowser(list.get(position));
                                }
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

        if (location.contains("　")) {
            String[] loc = location.split("　");
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

    @SuppressWarnings("StatementWithEmptyBody")
    private String removeNumberTrailing(String location) {
        String num = getString(R.string.search_on_map_split_number);
        if (location.contains(num)) {
            return location.substring(0, location.indexOf(num) + 1);
        } else {
            //FIXME: maybe some other patterns?
        }
        return location;
    }

    //Android Essentials: Adding Events to the User’s Calendar
    //http://goo.gl/jyT75l
    private void addToCalendar(DonateActivity donation) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setDataAndType(CalendarContract.Events.CONTENT_URI, "vnd.android.cursor.item/event");
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

//    //How to implement onBackPressed() in Fragments?
//    //http://stackoverflow.com/a/29166971
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (getView() == null) {
//            return;
//        }
//
//        //Fragment pressing back button
//        //http://stackoverflow.com/a/27145007
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK
//                        && BottomSheetBehavior.STATE_EXPANDED == mBottomSheetBehavior.getState()) {
//                    // handle back button's click listener
//                    showBottomSheet(false);
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
//
//    private void showBottomSheet(final boolean visible) {
//        if (mBottomSheetBackground != null) {
//            //mBottomSheetBackground.setVisibility(visible ? View.VISIBLE : View.GONE);
//            if ((mBottomSheetBackground.getVisibility() == View.VISIBLE && visible)
//                    || (mBottomSheetBackground.getVisibility() == View.GONE && !visible)) {
//                return;//already the same state
//            }
//
//            float from = visible ? 0.0f : 1.0f;
//            float to = visible ? 1.0f : 0.0f;
//            //http://stackoverflow.com/a/20629036
//            //https://developer.android.com/reference/android/view/animation/AlphaAnimation.html
//            AlphaAnimation animation1 = new AlphaAnimation(from, to);
//            animation1.setDuration(200);
//            //animation1.setStartOffset(5000);
//            animation1.setFillAfter(true);
//            animation1.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                    //Log.d(TAG, "onAnimationStart");
//                    if (visible) {
//                        mBottomSheetBackground.setVisibility(View.VISIBLE);
//                    }
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    //Log.d(TAG, "onAnimationEnd");
//                    if (!visible) {
//                        mBottomSheetBackground.setVisibility(View.GONE);
//                        //Log.d(TAG, "hide background");
//                    }
//                    //http://tomkuo139.blogspot.tw/2009/11/android-alphaanimation.html
//                    mBottomSheetBackground.setAnimation(null);//clear animation
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//            mBottomSheetBackground.startAnimation(animation1);
//        }
//        if (mBottomSheetBehavior != null) {
//            mBottomSheetBehavior.setState(visible ? BottomSheetBehavior.STATE_EXPANDED
//                    : BottomSheetBehavior.STATE_COLLAPSED);
//        }
//    }

    private void openGoogleMaps(Uri dataUri) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, dataUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (PackageUtils.isCallable(getActivity(), mapIntent)) {
            startActivity(mapIntent);
        }
    }

    private void openMapInBrowser(String location) {
        //https://www.google.com/maps/search/?api=1&query=centurylink+field
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=".concat(location)));
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (PackageUtils.isCallable(getActivity(), mapIntent)) {
            startActivity(mapIntent);
        }
    }
}
