package com.prateek.gem.groups;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.R;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Item;
import com.prateek.gem.model.SectionHeaderObject;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.services.MyDBService;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.AddExpenseActivity;
import com.prateek.gem.views.ExpenseDetailActivity;
import com.prateek.gem.views.MyProgressDialog;
import com.prateek.gem.widgets.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by prateek on 4/5/15.
 */
public class ExpensesFragment extends Fragment {

    ListView expenses;
    ExpensesAdapter expenseAdapter;
    RelativeLayout noExpensesView;
    ScrollView instructionsView;
    private Context context;
    Intent dbServiceIntent,addExpenseIntent;
    MyResultReceiver resultReceiver;
    IntentFilter dbIntentFilter;
    int currentGroupId;
    MyProgressDialog pd;
    IntentFilter deleteExpenseIntentFilter;
    DeleteExpenseReceiver deleteExpenseReceiver;
    private boolean isExpenseLoading;
    private AsyncTask loadExpenseTask;

    ExpenseOject deletingExpense;
    private FloatingActionButton vAddExpenseButton = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expenses,
                container, false);

        initUI(rootView);
        currentGroupId = App.getInstance().getCurr_group().getGroupIdServer();
        resultReceiver = new MyResultReceiver();
        dbIntentFilter = new IntentFilter(AppConstants.DB_RECEIVER2);
        dbIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        dbServiceIntent = new Intent(getActivity(), MyDBService.class);
        dbServiceIntent.putExtra(AppConstants.SERVICE_ID, AppConstants.ServiceIDs.GET_EXPENSES);
        dbServiceIntent.putExtra(DB.TGroups.GROUPID_SERVER, currentGroupId);

        context.startService(dbServiceIntent);

        return rootView;
    }

    private class LoadExpenseTask extends AsyncTask<Void, Void, Boolean> {

        private List<Item> items;

        @Override
        protected Boolean doInBackground(Void... params) {
            if(App.getInstance().getExpensesList() != null && App.getInstance().getExpensesList().size() != 0) {
                items = Utils.gatherExpenses(AppConstants.DATE_WISE, App.getInstance().getExpensesList());
                if(expenseAdapter == null) {
                    expenseAdapter = new ExpensesAdapter(items);
                }

                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                expenses.setVisibility(View.VISIBLE);
                noExpensesView.setVisibility(View.GONE);
                instructionsView.setVisibility(View.GONE);
                expenses.setAdapter(expenseAdapter);
                expenseAdapter.setExpenses(items);
            }
            else{
                instructionsView.setVisibility(View.VISIBLE);
                noExpensesView.setVisibility(View.VISIBLE);
                expenses.setVisibility(View.GONE);
            }

            isExpenseLoading = false;
        }
    }

    private void initUI(View view) {
        context = getActivity();
        expenses = (ListView) view.findViewById(R.id.expenses);
        noExpensesView = (RelativeLayout) view.findViewById(R.id.noExpensesView);
        instructionsView = (ScrollView) view.findViewById(R.id.instructionsView);
        vAddExpenseButton = (FloatingActionButton) view.findViewById(R.id.vAddExpenseButton);
        addExpenseIntent = new Intent(getActivity(), AddExpenseActivity.class);

        deleteExpenseReceiver = new DeleteExpenseReceiver();
        deleteExpenseIntentFilter = new IntentFilter(DeleteExpenseReceiver.DELETEEXPENSESUCCESSRECEIVER);
        deleteExpenseIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        vAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!App.getInstance().getItems().isEmpty()) {
                    startActivity(addExpenseIntent);
                } else {
                    Utils.showToast(context, getString(R.string.addItemsMessage));
                }
            }
        });
    }

    public class ExpensesAdapter extends BaseAdapter{

        private List<Item> items;
        LayoutInflater li;
        private Calendar currentCalendar = Calendar.getInstance();

        public ExpensesAdapter(List<Item> items) {
            super();
            this.items = items;
            li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            System.out.println("items");
            System.out.println(items);
        }

        public void setExpenses(List<Item> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class HeaderHolder {

            private TextView headerField, totalAmountField;
            private View emptyViewTop;
            private View view = null;

            HeaderHolder(View view) {
                this.view = view;
                headerField = (TextView) this.view.findViewById(R.id.groupByField);
                totalAmountField = (TextView) this.view.findViewById(R.id.amount);
                emptyViewTop = this.view.findViewById(R.id.emptyViewTop);
            }
        }

        class SectionHolder {

            private View view = null;
            private TextView expenseBy, expenseAmount, expenseItem, expenseDetails;
            private LinearLayout expenseParticipants, expenseDetailsLayout;

            SectionHolder(View view) {
                this.view = view;
                expenseBy = (TextView) this.view.findViewById(R.id.expenseBy);
                expenseAmount = (TextView) this.view.findViewById(R.id.expenseAmount);
                expenseItem = (TextView) this.view.findViewById(R.id.expenseItem);
                expenseDetails = (TextView) this.view.findViewById(R.id.expenseDetails);
                expenseParticipants = (LinearLayout) this.view.findViewById(R.id.expenseParticipants);
                expenseDetailsLayout = (LinearLayout) this.view.findViewById(R.id.expenseDetailsLayout);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            HeaderHolder headerHolder = null;
            SectionHolder sectionHolder = null;
            final Item item = items.get(position);
            if(v == null) { // if view is null
                if(item.isSection()) {
                    v = li.inflate(R.layout.list_element_expense_view, null);
                    sectionHolder = new SectionHolder(v);
                    v.setTag(sectionHolder);
                } else {
                    v = li.inflate(R.layout.list_element_header_view, null);
                    headerHolder = new HeaderHolder(v);
                    v.setTag(headerHolder);
                }
            } else {
                if (v.getTag() instanceof SectionHolder) {
                    sectionHolder = (SectionHolder) v.getTag();
                } else {
                    headerHolder = (HeaderHolder) v.getTag();
                }
            }

            // will run all time
            if(item.isSection()) {

                final ExpenseOject expense = (ExpenseOject) item;

                if(sectionHolder == null) {
                    v = li.inflate(R.layout.list_element_expense_view, null);
                    sectionHolder = new SectionHolder(v);
                    v.setTag(sectionHolder);
                }

                sectionHolder.expenseBy.setText(expense.getExpenseBy());

                sectionHolder.expenseAmount.setText(getResources().getString(R.string.inr) + " " + expense.getAmount());

                sectionHolder.expenseItem.setText(expense.getItem());

                /*final ImageView expanderImage = (ImageView) v.findViewById(R.id.expanderImage);
                final Button deleteExpense = (Button) v.findViewById(R.id.deleteExpense);
                final Button editExpense = (Button) v.findViewById(R.id.editExpense);*/

                final JSONArray array;
                String participantsString = "";
                try {
                    array = new JSONArray(expense.getParticipants());
                    for (int i = 0; i < array.length(); i++) {
                        participantsString += array.getJSONObject(i).getString(AppConstants.JSONConstants.MEMBERNAME);
                        participantsString += ", ";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TextView textView = new TextView(context);
                textView.setText(participantsString.subSequence(0, participantsString.length() - 2));
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setSingleLine(true);
                textView.setTextSize(18);
                sectionHolder.expenseParticipants.addView(textView);

                /*expanderImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (expenseDetailsLayout.getVisibility() == View.GONE) {
                            expenseDetailsLayout.setVisibility(View.VISIBLE);
                            expanderImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_collapse));
                        } else {
                            expenseDetailsLayout.setVisibility(View.GONE);
                            expanderImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_expand));
                        }
                    }
                });

                deleteExpense.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        deleteExpense(expense);
                        System.out.println("Delete Expense ID" + expense.getExpenseId());
                    }
                });

                editExpense.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        editExpense(expense);
                        System.out.println("Edit Expense ID" + expense.getExpenseId());
                    }
                });*/

                sectionHolder.expenseDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("expense", expense);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } else {
                SectionHeaderObject sectionHeader = (SectionHeaderObject) item;

                if(headerHolder == null) {
                    v = li.inflate(R.layout.list_element_header_view, null);
                    headerHolder = new HeaderHolder(v);
                    v.setTag(headerHolder);
                }

                if (position != 0) {
                    headerHolder.emptyViewTop.setVisibility(View.VISIBLE);
                } else {
                    headerHolder.emptyViewTop.setVisibility(View.GONE);
                }

                String dateString = "";
                if (currentCalendar.get(Calendar.YEAR) == sectionHeader.getHeaderTitleCalendar().get(Calendar.YEAR)) {
                    if (currentCalendar.get(Calendar.DAY_OF_YEAR) == sectionHeader.getHeaderTitleCalendar().get(Calendar.DAY_OF_YEAR)) {
                        dateString = "Today";
                    } else if (currentCalendar.get(Calendar.DAY_OF_YEAR) - 1 == sectionHeader.getHeaderTitleCalendar().get(Calendar.DAY_OF_YEAR)) {
                        dateString = "Yesterday";
                    } else {
                        dateString = Utils.formatDateWithoutYear("" + sectionHeader.getHeaderTitle());
                    }
                } else {
                    dateString = Utils.formatDate("" + sectionHeader.getHeaderTitle());
                }

                headerHolder.headerField.setText(dateString);
                headerHolder.totalAmountField.setText(getResources().getString(R.string.inr) + " " + Utils.round(sectionHeader.getAmount(), 2));

            }
            return v;
        }
    }

    public void deleteExpense(final ExpenseOject expense){
        deletingExpense = expense;
        String message = "Are you sure to delete following expense:\n\nExpense by: " +
                Html.fromHtml("<b>" + deletingExpense.getExpenseBy() + "</b>") +
                " for\n"+Html.fromHtml("<b>"+deletingExpense.getItem()+"</b>") +" worth \n"+
                Html.fromHtml("<b>"+getString(R.string.inr)+deletingExpense.getAmount()+"</b>");
        StringBuilder title = new StringBuilder(getString(R.string.deleteExpense));
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog ad = builder.create();
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_action_content_discard));
        builder.setPositiveButton(getString(R.string.button_delete), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.getInstance().getExpensesList().remove(deletingExpense);
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair(DB.TExpenses.EXPENSE_ID, ""+deletingExpense.getExpenseIdServer()));
                list.add(new BasicNameValuePair("realexpenseId", ""+deletingExpense.getExpenseId()));
                list.add(new BasicNameValuePair(DB.TExpenses.GROUP_ID_FK, ""+deletingExpense.getGroupId()));
                list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.DELETE_EXPENSE));

                pd = new MyProgressDialog(context,true, "Deleting Expense");
                pd.show();
                System.out.println("Expense server id "+deletingExpense.getExpenseIdServer());
                FullFlowService.ServiceDeleteExpense(context, AppConstants.NOT_DELETEEXPENSE, list);
                System.out.println("list deleting" +list);
                ad.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });



        builder.show();

		/*ConfirmationDialog mcd = new ConfirmationDialog();
		Bundle bundle = new Bundle();
		bundle.putString(ConfirmationDialog.TITLE, getString(R.string.deleteExpense));
		bundle.putInt(ConfirmConstants.CONFIRM_KEY, ConfirmConstants.EXPENSE_DELETE);
		bundle.putInt(TExpenses.EXPENSE_ID, expense.getExpenseId());
		bundle.putString(ConfirmationDialog.BUTTON1, getResources().getString(R.string.button_delete));
		bundle.putString(ConfirmationDialog.BUTTON2, getResources().getString(R.string.button_cancel));

		String message = "Are you sure to delete following expense:\n\nExpense by: " +
				Html.fromHtml("<b>"+expense.getExpenseBy()+"</b>") +
				" for\n"+Html.fromHtml("<b>"+expense.getItem()+"</b>") +" worth \n"+
				Html.fromHtml("<b>"+getString(R.string.inr)+expense.getAmount()+"</b>");

		bundle.putString(ConfirmationDialog.MESSAGE, message);
		mcd.setArguments(bundle);
		mcd.show(getSupportFragmentManager(), "ComfirmationDialog");*/
    }

    public void editExpense(ExpenseOject expense){

    }

    public void populateExpenses() {
        isExpenseLoading = true;
        loadExpenseTask = new LoadExpenseTask().execute();
    }



    public class MyResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("in receiver");

            switch (intent.getIntExtra(AppConstants.SERVICE_ID, 0)) {
                case AppConstants.ServiceIDs.GET_EXPENSES:
                    System.out.println("to populate expenses");
                    populateExpenses();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("STOP");
        context.unregisterReceiver(resultReceiver);
        context.unregisterReceiver(deleteExpenseReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("START");
        context.registerReceiver(resultReceiver, dbIntentFilter);
        context.registerReceiver(deleteExpenseReceiver,deleteExpenseIntentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUME");
        populateExpenses();
    }

    public class DeleteExpenseReceiver extends BroadcastReceiver{

        public static final String DELETEEXPENSESUCCESSRECEIVER = "com.prateek.gem.views.AddExpenseActivity.DELETEEXPENSESUCCESSRECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
            pd.dismiss();
            int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
            boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);
            switch (notId) {
                case AppConstants.NOT_DELETEEXPENSE:
                    if(result){
                        App.getInstance().getExpensesList().remove(deletingExpense);
                        //App.getInstance().setAllGroups(Group.updateTotalExpense(deletingExpense.getGroupId(), App.getInstance().getAllGroups(), App.getInstance().getCurr_group().getTotalOfExpense(), deletingExpense.getAmount(), 0));
                        populateExpenses();
                    }else{
                        Utils.showToast(context, "Cannot Delete, Please try after some time");
                    }
                    break;

                default:
                    break;
            }
        }
    }



}
