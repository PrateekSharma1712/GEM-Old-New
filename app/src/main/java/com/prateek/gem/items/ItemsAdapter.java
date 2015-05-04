package com.prateek.gem.items;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prateek.gem.R;
import com.prateek.gem.groups.ItemsFragment;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Items;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.views.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 12/2/15.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private Object currentScreen = null;
    private LayoutInflater mInflater = null;
    private ArrayList<Items> mItems = null;
    private SparseBooleanArray selectedPositions = new SparseBooleanArray();

    public ItemsAdapter(Object screen) {
        currentScreen = screen;
        if(screen instanceof ItemsFragment)
            mInflater = LayoutInflater.from(((ItemsFragment) screen).getActivity());
        else
            mInflater = LayoutInflater.from((SelectingItemsActivity) screen);
    }

    public SparseBooleanArray getSelectedPositions() {
        return selectedPositions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(currentScreen instanceof ItemsActivity) {
            View view = mInflater.inflate(R.layout.item_delete_text, parent, false);
            return new ViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.item_selection_text, parent, false);
            return new ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Items item = mItems.get(position);
        holder.vItemName.setText(item.getItemName());

        if(selectedPositions.indexOfKey(item.getIdServer()) < 0) {
            holder.vItemName.setTextColor(AppDataManager.getThemePrimaryTextColor());
            holder.vActionImage.setColorFilter(AppDataManager.getThemePrimaryTextColor());
        } else {
            if (selectedPositions.valueAt(selectedPositions.indexOfKey(item.getIdServer()))) {
                holder.vItemName.setTextColor(AppDataManager.getThemeSecondaryTextColor());
                holder.vActionImage.setColorFilter(AppDataManager.getThemeSecondaryTextColor());
            } else {
                holder.vItemName.setTextColor(AppDataManager.getThemePrimaryTextColor());
                holder.vActionImage.setColorFilter(AppDataManager.getThemePrimaryTextColor());
            }
        }

        holder.vActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPositions.indexOfKey(item.getIdServer()) < 0) {
                    selectedPositions.put(item.getIdServer(), true);
                } else {
                    selectedPositions.put(item.getIdServer(), !selectedPositions.valueAt(selectedPositions.indexOfKey(item.getIdServer())));
                }

                if (currentScreen instanceof ItemsActivity) {
                    ((ItemsActivity) currentScreen).runActionMode();
                    notifyDataSetChanged();
                }

                if (currentScreen instanceof SelectingItemsActivity) {
                    ((SelectingItemsActivity) currentScreen).runActionMode();
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setDataset(ArrayList<Items> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void resetSelectedPositions() {
        selectedPositions.clear();
        DebugLogger.message("selectedPositions" + selectedPositions);
    }

    public void setSelectedPositions(String selectedItemsString) {
        DebugLogger.message("selectedItemsString.isEmpty()||" + selectedItemsString.isEmpty());
        if(!selectedItemsString.isEmpty()) {
            String[] items = selectedItemsString.split(", ");
            for (int i = 0; i < items.length; i++) {
                DebugLogger.message("items.." + items[i]);
                int itemIdServer = DBImpl.getItemId(items[i], AppDataManager.getCurrentGroup().getGroupIdServer());
                if(itemIdServer != 0) {
                    selectedPositions.put(itemIdServer, true);
                }
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView vItemName = null;
        private ImageView vActionImage = null;

        public ViewHolder(View view) {
            super(view);
            vItemName = (TextView) view.findViewById(android.R.id.text1);
            vActionImage = (ImageView) view.findViewById(R.id.vDeleteItem);
            vActionImage.setColorFilter(Color.RED);

        }
    }

    public int getSelectedCount() {
        int count = 0;
        for(int i = 0;i<selectedPositions.size();i++) {
            if(selectedPositions.get(selectedPositions.keyAt(i)))
                count++;
        }

        return count;
    }

    public List<Integer> getItemIds() {
        List<Integer> ids = new ArrayList<>();
        for(int i = 0;i<selectedPositions.size();i++) {
            if(selectedPositions.get(selectedPositions.keyAt(i)))
                ids.add(selectedPositions.keyAt(i));
        }
        return ids;
    }


}
