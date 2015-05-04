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
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Group group = mGroups.get(position);
        DebugLogger.message("Group :: " + group + ", position :: " + position);
        float expenseTotal = DBImpl.getExpenseTotal(group.getGroupIdServer());
        if(expenseTotal == 0.0f) {
            viewHolder.vGroupDescription.setText("Tap to make expenses");
        } else {
            viewHolder.vGroupDescription.setText("Expenses : " + mScreen.getString(R.string.inr) +" " + expenseTotal);
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

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
}
