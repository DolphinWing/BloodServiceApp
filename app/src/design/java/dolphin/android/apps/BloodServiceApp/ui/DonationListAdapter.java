package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity;
import dolphin.android.apps.BloodServiceApp.provider.DonateDay;

/**
 * Created by dolphin on 2015/03/15.
 */
public class DonationListAdapter extends RecyclerView.Adapter<DonationViewHolder> {
    private final static String TAG = "ProgramListAdapter";

    private final Context mContext;
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

    public DonationListAdapter(Context context, ArrayList<DonateDay> items) {
        this(context, items, null);
    }

    DonationListAdapter(Context context, ArrayList<DonateDay> items, OnItemClickListener l) {
        mContext = context;
        mItems = new ArrayList<>();
        mItemClickListener = l;

        //int sectionManager = -1;
        int headerCount = 0, itemCount = 0;
        int sectionFirstPosition = 0;
        for (DonateDay day : items) {
            //sectionManager = (sectionManager + 1) % 2;
            sectionFirstPosition = headerCount + itemCount;
            headerCount++;
            mItems.add(new MyItem(true, sectionFirstPosition, day, null,
                    day.getActivities().size() > 0));
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
        return new DonationViewHolder(mContext, view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(DonationViewHolder holder, int position) {
        final MyItem item = mItems.get(position);
        final View itemView = holder.itemView;
        holder.bindItem(item.day, item.activity);

        final GridSLM.LayoutParams lp = new GridSLM.LayoutParams(itemView.getLayoutParams());
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
}
