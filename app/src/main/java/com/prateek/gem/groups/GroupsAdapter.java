package com.prateek.gem.groups;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateek.gem.App;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Group;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.views.MainActivity;

import java.util.ArrayList;

/**
 * Created by prateek on 23/11/14.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private ArrayList<Group> mGroups = null;
    private MainActivity mScreen = null;

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    public GroupsAdapter(MainActivity screen) {
        mScreen = screen;
    }

    public void setGroups(ArrayList<Group> groups) {
        mGroups = groups;
        Group personalGroup = Group.getPersonalGroup();
        mGroups.add(0, personalGroup);
        DebugLogger.message("mGroups" + mGroups);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView vGroupName;
        public TextView vGroupDescription;
        public View vView;

        public ViewHolder(View v) {
            super(v);
            vView = v;
            vGroupName = (TextView) v.findViewById(R.id.vGroupName);
            vGroupDescription = (TextView) v.findViewById(R.id.vGroupDescription);
        }

        public ViewHolder(View v, int type) {
            super(v);
            vView = v;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if(!isPositionHeader(position)) {
            final Group group = mGroups.get(position-1);
            DebugLogger.message("Group :: " + group + ", position :: " + position);
            float expenseTotal = DBImpl.getExpenseTotal(group.getGroupIdServer());
            if (expenseTotal == 0.0f) {
                viewHolder.vGroupDescription.setText("Tap to make expenses");
            } else {
                viewHolder.vGroupDescription.setText("Expenses : " + mScreen.getString(R.string.inr) + " " + expenseTotal);
            }
            viewHolder.vGroupName.setText(group.getGroupName());

            viewHolder.vView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.getInstance().setCurr_group(mGroups.get(position));
                    ((MainLandingScreen) mScreen).onItemSelected(group, position, viewHolder.vGroupName);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return (mGroups.size() > 0) ? mGroups.size() + 1 : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
        if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_group, parent, false);
            vh = new ViewHolder(v);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_group, parent, false);
            v.setMinimumHeight(180);
            vh = new ViewHolder(v, TYPE_HEADER);
        }
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return (position == 0);
    }
}
