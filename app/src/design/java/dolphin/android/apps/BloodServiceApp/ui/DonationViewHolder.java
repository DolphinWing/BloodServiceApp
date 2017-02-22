package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import dolphin.android.apps.BloodServiceApp.provider.DonateActivity;
import dolphin.android.apps.BloodServiceApp.provider.DonateDay;

/**
 * Created by dolphin on 2015/03/15.
 */
class DonationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context mContext;
    private boolean mIsHeader;
    private TextView title;
    private TextView tv1;
    private TextView tv2;
    //private View background;
    private DonationListAdapter.OnItemClickListener mListener;

    public DonationViewHolder(Context context, View itemView) {
        this(context, itemView, null);
    }

    DonationViewHolder(Context context, View itemView,
                       DonationListAdapter.OnItemClickListener listener) {
        super(itemView);

        mContext = context;
        mListener = listener;

        //background = itemView.findViewById(android.R.id.background);
        title = (TextView) itemView.findViewById(android.R.id.title);
        mIsHeader = (itemView.findViewById(android.R.id.text1) == null);
        if (!mIsHeader) {//only for activity
            tv1 = (TextView) itemView.findViewById(android.R.id.text1);
            tv2 = (TextView) itemView.findViewById(android.R.id.text2);
            //itemView.setOnClickListener(this);
            View more = itemView.findViewById(android.R.id.button1);
            if (more != null) {
                more.setOnClickListener(this);
            } else {//use full item
                itemView.setOnClickListener(this);
            }
        }
    }

    public boolean isHeader() {
        return mIsHeader;
    }

    void bindItem(DonateDay day, DonateActivity activity) {
        if (isHeader() && day != null) {
            title.setText(day.getDateString());
            title.setTag(day);
        }
        if (!isHeader() && activity != null) {
            title.setText(activity.getName());
            title.setTag(activity);
            tv1.setText(activity.getDuration(mContext));
            tv2.setText(activity.getLocation());
        }
    }

    @Override
    public void onClick(View view) {
//        Log.d("ViewHolder", title.getTag().toString());
        if (mListener != null) {
            mListener.onItemClicked(view, title.getTag());
        }
    }
}
