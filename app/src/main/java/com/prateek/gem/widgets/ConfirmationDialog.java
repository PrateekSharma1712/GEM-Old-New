package com.prateek.gem.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.prateek.gem.AppConstants.ConfirmConstants;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Items;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ConfirmationDialog extends DialogFragment {


    public interface OnModeConfirmListener {
        public void modeConfirmed();

        public void deleteMemberConfirmed(int memberId);

        public void deleteExpenseConfirmed(int expenseId);

        public void deleteItemConfirmed(boolean deleteItemConfirmed);

        public void openNewActivity(int requestCodeClickImage);

        public void onItemsAdded(String category, String item);
    }

    OnModeConfirmListener onModeConfirmListener;
    private int confirmId;
    GridAdapter gridAdapter;
    public static final String TITLE = "title";
    public static final String BUTTON1 = "button1";
    public static final String BUTTON2 = "button2";
    public static final String MESSAGE = "Message";

    public static final String SELECTED_CATEGORY = "category";
    public static final String LIST = "list";

    private Spinner vCategories = null;
    private FloatingHint vItemName = null;

    public ConfirmationDialog() {
        super();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("on attach");
        try {
            onModeConfirmListener = (OnModeConfirmListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onModeConfirmListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        confirmId = getArguments().getInt(ConfirmConstants.CONFIRM_KEY);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if(confirmId == ConfirmConstants.CONFIRM_SELECTED_ITEMS_LIST
                || confirmId == ConfirmConstants.IMAGE_CAPTURE) {
            DebugLogger.message("LIST");
            dialog.setContentView(R.layout.dialog_list);
            dialog.findViewById(android.R.id.closeButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            RecyclerView list = (RecyclerView) dialog.findViewById(R.id.vShowList);
            LinearLayoutManager mLM = new LinearLayoutManager(getActivity());
            list.setLayoutManager(mLM);
            ShowListAdapter adapter = new ShowListAdapter(getArguments().getStringArrayList(LIST));
            list.setAdapter(adapter);
            ViewGroup.LayoutParams params = list.getLayoutParams();
            params.height = generateListHeight(list);
            list.setLayoutParams(params);
            list.requestLayout();
        } else if(confirmId != ConfirmConstants.ITEM_ADD) {
            dialog.setContentView(R.layout.dialog_mode_detail);
            TextView modeDetailTextView = (TextView) dialog.findViewById(R.id.detail);
            modeDetailTextView.setText(getArguments().getString("Message"));
        } else {
            dialog.setContentView(R.layout.dialog_add_item);
            vCategories = (Spinner) dialog.findViewById(R.id.vCategories);
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.categoryarray));
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            vCategories.setAdapter(categoryAdapter);
            vCategories.setSelection(getArguments().getInt(SELECTED_CATEGORY));

            vItemName = (FloatingHint) dialog.findViewById(R.id.vItemName);
        }


        String button1 = getArguments().getString(BUTTON1);
        String button2 = getArguments().getString(BUTTON2);
        DebugLogger.message(button1);
        DebugLogger.message(button2);
        GridView buttonsGrid = (GridView) dialog.findViewById(R.id.buttonsGrid);
        if(button1 != null && button2 != null) {
            buttonsGrid.setVisibility(View.VISIBLE);
            List<String> buttonsArray = new ArrayList<String>();
            buttonsArray.add(button1);
            buttonsArray.add(button2);
            buttonsGrid.setAdapter(new GridAdapter(buttonsArray, getActivity()));
        } else {
            buttonsGrid.setVisibility(View.GONE);
        }

        TextView dialogHeading = (TextView) dialog.findViewById(R.id.dialogHeading);
        dialogHeading.setText(getArguments().getString(TITLE));
        return dialog;


    }

    private int generateListHeight(RecyclerView list) {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(list.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            view = new ListHolder(LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, list, false)).itemView;
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        DebugLogger.message("total height" + totalHeight);
        return totalHeight;
    }

    private class GridAdapter extends BaseAdapter {

        private Context _c;
        private List<String> buttonsArray;

        public GridAdapter(List<String> buttonsArray, FragmentActivity activity) {
            super();
            this.buttonsArray = buttonsArray;
            _c = activity;
        }

        @Override
        public int getCount() {
            return buttonsArray.size();
        }

        @Override
        public Object getItem(int position) {
            return buttonsArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(position == 0) {
                    v = li.inflate(R.layout.inactivebutton, null);
                } else {
                    v = li.inflate(R.layout.activebutton, null);
                }
            }

            final Button buttonText = (Button) v.findViewById(R.id.buttonText);
            buttonText.setText(buttonsArray.get(position));

            buttonText.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    System.out.println("in");
                    System.out.println(buttonText.getText());
                    if (position == 1) {
                        System.out.println(position);

                        switch (confirmId) {
                            case ConfirmConstants.FROM_ADD_GROUP:
                                System.out.println("group");
                                onModeConfirmListener.modeConfirmed();
                                break;
                            case ConfirmConstants.MEMBER_DELETE:
                            /*int memberId = getArguments().getInt(TMembers.MEMBER_ID);
							System.out.println("dlete"+memberId);
							onModeConfirmListener.deleteMemberConfirmed(memberId);*/
                                break;
                            case ConfirmConstants.EXPENSE_DELETE:
							/*int expenseId = getArguments().getInt(TExpenses.EXPENSE_ID);
							System.out.println("confirm dlete"+expenseId);
							onModeConfirmListener.deleteExpenseConfirmed(expenseId);*/
                                break;
                            case ConfirmConstants.ITEM_DELETE:
                                onModeConfirmListener.deleteItemConfirmed(true);
                                dismiss();
                                break;

                            case ConfirmConstants.ITEM_ADD:
                                if(validateItems(vCategories.getSelectedItem().toString(), vItemName.getText().toString())) {
                                    onModeConfirmListener.onItemsAdded(vCategories.getSelectedItem().toString(), vItemName.getText().toString());
                                    Utils.hideKeyboard(vItemName);
                                    dismiss();
                                }
                                break;

                            case ConfirmConstants.CONFIRM_SELECTED_ITEMS_LIST:
                                onModeConfirmListener.modeConfirmed();
                                dismiss();
                                break;
                        }

                    } else {
                        System.out.println(position);
                        dismiss();
                    }

                }
            });

            return v;
        }


    }

    private boolean validateItems(String category, String item) {
        boolean result = false;
        Items model = new Items();
        model.setItemName(item);
        if(item == null) {
            Utils.showToast(this.getActivity(), "Invalid item");
        } else if(item.length() == 0) {
            Utils.showToast(this.getActivity(), "Item is empty");
        } else if(DBImpl.getItems(String.valueOf(AppDataManager.getCurrentGroup().getGroupIdServer())).contains(model)) {
            Utils.showToast(this.getActivity(), item + " already exists");
        } else {
            result = true;
        }
        return result;
    }

    public class ShowListAdapter extends RecyclerView.Adapter<ListHolder> {

        private List<String> list;

        ShowListAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);
            ListHolder holder = new ListHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ListHolder viewHolder, int position) {
            viewHolder.vListItem.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


    }

    private class ListHolder extends RecyclerView.ViewHolder{

        private TextView vListItem;

        public ListHolder(View view) {
            super(view);
            vListItem = (TextView) view.findViewById(android.R.id.text1);
            vListItem.setTextColor(AppDataManager.getThemePrimaryTextColor());
        }
    }

}
