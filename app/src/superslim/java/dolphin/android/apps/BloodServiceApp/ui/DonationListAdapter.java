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
        public boolean isHeader;
        public int sectionFirstPosition;
        public DonateDay day;
        public DonateActivity activity;

        public MyItem(boolean header, int pos, DonateDay d, DonateActivity act) {
            isHeader = header;
            sectionFirstPosition = pos;
            day = d;
            activity = act;
        }
    }

    private static final int VIEW_TYPE_HEADER = 0;

    private static final int VIEW_TYPE_CONTENT = 1;

    public DonationListAdapter(Context context, ArrayList<DonateDay> items) {
        mContext = context;
        mItems = new ArrayList<>();

        //int sectionManager = -1;
        int headerCount = 0, itemCount = 0;
        int sectionFirstPosition = 0;
        for (DonateDay day : items) {
            //sectionManager = (sectionManager + 1) % 2;
            sectionFirstPosition = headerCount + itemCount;
            headerCount++;
            mItems.add(new MyItem(true, sectionFirstPosition, day, null));
            for (DonateActivity a : day.getActivities()) {
                itemCount++;
                mItems.add(new MyItem(false, sectionFirstPosition, day, a));
            }
        }
    }

    @Override
    public DonationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = inflater.inflate(R.layout.listview_donation_date, parent, false);
        } else {
            view = inflater.inflate(R.layout.listview_donation_activity, parent, false);
        }
        return new DonationViewHolder(mContext, view);
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
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
