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
public class DonationViewHolder extends RecyclerView.ViewHolder {
    private Context mContext;
    private boolean mIsHeader;
    private TextView title;
    private TextView tv1;
    private TextView tv2;

    public DonationViewHolder(Context context, View itemView) {
        super(itemView);

        mContext = context;

        title = (TextView) itemView.findViewById(android.R.id.title);
        mIsHeader = (itemView.findViewById(android.R.id.text1) == null);
        if (!mIsHeader) {//only for activity
            tv1 = (TextView) itemView.findViewById(android.R.id.text1);
            tv2 = (TextView) itemView.findViewById(android.R.id.text2);
        }
    }

    public boolean isHeader() {
        return mIsHeader;
    }

    public void bindItem(DonateDay day, DonateActivity activity) {
        if (isHeader() && day != null) {
            title.setText(day.getDateString());
        }
        if (!isHeader() && activity != null) {
            title.setText(activity.getName());
            tv1.setText(activity.getDuration(mContext));
            tv2.setText(activity.getLocation());
        }
    }
}
