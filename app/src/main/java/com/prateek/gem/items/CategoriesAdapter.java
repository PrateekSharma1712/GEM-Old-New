package com.prateek.gem.items;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateek.gem.R;
import com.prateek.gem.groups.ItemsFragment;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Items;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.views.MainActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by prateek on 9/2/15.
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private String[] cats = null;
    private Object currentScreen = null;
    private LayoutInflater mInflater = null;
    private Map<String, Boolean> categories = null;
    private int lastSelected = 0;

    public CategoriesAdapter(Object screen) {
        currentScreen = screen;
        categories = new LinkedHashMap<String, Boolean>();
        if(screen instanceof ItemsFragment) {
            mInflater = LayoutInflater.from(((ItemsFragment) screen).getActivity());
            cats = ((ItemsFragment) currentScreen).getResources().getStringArray(R.array.categoryarray);
        } else {
            mInflater = LayoutInflater.from(((SelectingItemsActivity) currentScreen));
            cats = ((SelectingItemsActivity) currentScreen).getResources().getStringArray(R.array.categoryarray);
        }

        for(int i = 0;i<cats.length;i++) {
            categories.put(cats[i], false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.category_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ArrayList<Items> items = DBImpl.getItems(AppDataManager.getCurrentGroup().getGroupIdServer(), cats[position]);
        holder.vCategoryName.setText(cats[position]);
        holder.vNoOfItems.setText("("+String.valueOf(items.size())+")");
        DebugLogger.message("categories.get(cats[position])" + position + ".." + categories.get(cats[position]));

        if(categories.get(cats[position])) {
            holder.vCategoryName.setTextColor(AppDataManager.getThemePrimaryColor());
            holder.itemView.setActivated(true);
        } else {
            holder.vCategoryName.setTextColor(AppDataManager.getThemePrimaryTextColor());
            holder.itemView.setActivated(false);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentScreen instanceof ItemsFragment) {
                    toggleSelection(position);
                    ((ItemsFragment)currentScreen).loadItems(cats[position]);
                }
            }
        });
    }

    public void toggleSelection(int position) {
        DebugLogger.message("lastSelected" + lastSelected);
        if(lastSelected != -1) {
            categories.put(cats[lastSelected], false);
        }
        categories.put(cats[position], true);
        notifyItemChanged(position);
        notifyItemChanged(lastSelected);
        lastSelected = position;
        if(currentScreen instanceof ItemsFragment) {
            ((ItemsFragment) currentScreen).setSelectedCategoryIndex(lastSelected);
        }
    }


    @Override
    public int getItemCount() {
        return cats.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView vCategoryName = null;
        private TextView vNoOfItems = null;
        private View view = null;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            vCategoryName = (TextView) view.findViewById(android.R.id.text1);
            vNoOfItems = (TextView) view.findViewById(android.R.id.text2);
        }
    }

    public String getSelectedCategory() {
        return cats[lastSelected];
    }
}
