package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity;
import dolphin.android.apps.BloodServiceApp.provider.DonateDay;

/**
 * Created by dolphin on 2015/03/15.
 * Donation adapter
 */
public class DonationListAdapter extends RecyclerView.Adapter<DonationListAdapter.DonationViewHolder> {
    //private final static String TAG = "DonationListAdapter";

    private final DonationFragment mFragment;
    private final ArrayList<MyItem> mItems;

    private class MyItem {
        boolean isHeader;
        int sectionFirstPosition;
        DonateDay day;
        public DonateActivity activity;
        boolean hasChildren;

        MyItem(boolean header, int pos, DonateDay d, DonateActivity act, boolean child) {
            isHeader = header;
            sectionFirstPosition = pos;
            day = d;
            activity = act;
            hasChildren = isHeader && child;
        }
    }

    private static final int VIEW_TYPE_HEADER_1 = 0;

    private static final int VIEW_TYPE_HEADER_0 = 1;

    private static final int VIEW_TYPE_CONTENT = 2;

    private OnItemClickListener mItemClickListener;

    @SuppressWarnings("unused")
    public DonationListAdapter(DonationFragment fragment, ArrayList<DonateDay> items) {
        this(fragment, items, null);
    }

    DonationListAdapter(DonationFragment fragment, ArrayList<DonateDay> items, OnItemClickListener l) {
        mFragment = fragment;
        mItems = new ArrayList<>();
        mItemClickListener = l;

        //int sectionManager = -1;
        int headerCount = 0, itemCount = 0;
        int sectionFirstPosition;
        for (DonateDay day : items) {
            //sectionManager = (sectionManager + 1) % 2;
            sectionFirstPosition = headerCount + itemCount;
            headerCount++;
            mItems.add(new MyItem(true, sectionFirstPosition, day, null,
                    day.getActivityCount() > 0));
            for (DonateActivity a : day.getActivities()) {
                itemCount++;
                mItems.add(new MyItem(false, sectionFirstPosition, day, a, false));
            }
        }
    }

    @Override
    public DonationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_HEADER_1) {
            view = inflater.inflate(R.layout.listview_donation_date, parent, false);
        } else if (viewType == VIEW_TYPE_HEADER_0) {
            view = inflater.inflate(R.layout.listview_donation_date_no_children, parent, false);
        } else {
            view = inflater.inflate(R.layout.listview_donation_activity, parent, false);
        }
        return new DonationViewHolder(mFragment, view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(DonationViewHolder holder, int position) {
        final MyItem item = mItems.get(position);
        final View itemView = holder.itemView;
        holder.bindItem(mFragment.getContext(), item.day, item.activity);

        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader ?
                mItems.get(position).hasChildren ? VIEW_TYPE_HEADER_1 : VIEW_TYPE_HEADER_0
                : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, Object data);
    }


    static class DonationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private DonationFragment mFragment;
        private boolean mIsHeader;
        private View container;
        private TextView title;
        private TextView tv1;
        private TextView tv2;
        //private View background;
        private DonationListAdapter.OnItemClickListener mListener;
        private Object data;

        @SuppressWarnings("unused")
        public DonationViewHolder(DonationFragment fragment, View itemView) {
            this(fragment, itemView, null);
        }

        DonationViewHolder(DonationFragment fragment, View itemView,
                           DonationListAdapter.OnItemClickListener listener) {
            super(itemView);

            mFragment = fragment;
            mListener = listener;
            //set data to item
            container = itemView;
            container.setTag(this);//store myself

            //background = itemView.findViewById(android.R.id.background);
            title = itemView.findViewById(android.R.id.title);
            tv1 = itemView.findViewById(android.R.id.text1);
            mIsHeader = (tv1 == null);
            if (!isHeader()) {//only for activity
                mFragment.registerForContextMenu(container);//only activity can have menu
                tv2 = itemView.findViewById(android.R.id.text2);
//                //itemView.setOnClickListener(this);
//                View more = itemView.findViewById(android.R.id.button1);
//                if (more != null) {
//                    more.setOnClickListener(this);
//                    itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            //do nothing, just to have click animation on list item
//                            if (mListener != null) {
//                                mListener.onItemClicked(view, null);
//                            }
//                        }
//                    });
//                } else {//use full item
//                    itemView.setOnClickListener(this);
//                }
            }
        }

        boolean isHeader() {
            return mIsHeader;
        }

        void bindItem(Context context, DonateDay day, DonateActivity activity) {
            if (isHeader() && day != null) {
                title.setText(day.getDateString());
                data = day;
            }
            if (!isHeader() && activity != null) {
                data = activity;
                title.setText(activity.getName());
                tv1.setText(activity.getDuration(context));
                tv2.setText(activity.getLocation());
            }
            title.setTag(data);
        }

        Object getData() {
            return data;
        }

        @Override
        public void onClick(View view) {
//        Log.d("ViewHolder", title.getTag().toString());
            if (mListener != null) {
                mListener.onItemClicked(view, title.getTag());
            }
        }
    }
}
