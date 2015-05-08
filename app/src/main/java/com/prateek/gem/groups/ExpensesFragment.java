package com.prateek.gem.groups;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.R;
import com.prateek.gem.SyncService;
import com.prateek.gem.items.ItemsActivity;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Item;
import com.prateek.gem.model.SectionHeaderObject;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.services.MyDBService;
import com.prateek.gem.utils.CreateExcel;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.AddExpenseActivity;
import com.prateek.gem.views.GraphActivity;
import com.prateek.gem.views.HisabActivity;
import com.prateek.gem.views.MainActivity;
import com.prateek.gem.views.MembersActivity;
import com.prateek.gem.views.MyProgressDialog;
import com.prateek.gem.views.MyStatsActivity;
import com.prateek.gem.views.PieChartActivity;
import com.prateek.gem.widgets.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jxl.write.WriteException;

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
    DeleteExpenseRecevier deleteExpenseReceiver;

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
        
        return rootView;
    }

    private void initUI(View view) {
        context = getActivity();
        expenses = (ListView) view.findViewById(R.id.expenses);
        noExpensesView = (RelativeLayout) view.findViewById(R.id.noExpensesView);
        instructionsView = (ScrollView) view.findViewById(R.id.instructionsView);
        vAddExpenseButton = (FloatingActionButton) view.findViewById(R.id.vAddExpenseButton);
        addExpenseIntent = new Intent(getActivity(), AddExpenseActivity.class);


        deleteExpenseReceiver = new DeleteExpenseRecevier();
        deleteExpenseIntentFilter = new IntentFilter(DeleteExpenseRecevier.DELETEEXPENSESUCCESSRECEIVER);
        deleteExpenseIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        vAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!App.getInstance().getItems().isEmpty()){
                    startActivity(addExpenseIntent);
                }else{
                    Utils.showToast(context, getString(R.string.addItemsMessage));
                }
            }
        });


    }

    public class ExpensesAdapter extends BaseAdapter {

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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final Item item = items.get(position);
            if(item != null){
                if(item.isSection()){
                    final ExpenseOject expense = (ExpenseOject) item;
                    v = li.inflate(R.layout.list_element_expense_view, null);
                    final TextView expenseBy = (TextView) v.findViewById(R.id.expenseBy);
                    expenseBy.setText(expense.getExpenseBy());
                    final TextView expenseAmount = (TextView) v.findViewById(R.id.expenseAmount);
                    expenseAmount.setText(getResources().getString(R.string.inr) + " " +expense.getAmount());
                    final TextView expenseItem = (TextView) v.findViewById(R.id.expenseItem);
                    expenseItem.setText(expense.getItem());
                    final LinearLayout expenseParticipants = (LinearLayout) v.findViewById(R.id.expenseParticipants);
                    final LinearLayout expenseDetailsLayout = (LinearLayout) v.findViewById(R.id.expenseDetailsLayout);
                    final ImageView expanderImage = (ImageView) v.findViewById(R.id.expanderImage);
                    final Button deleteExpense = (Button) v.findViewById(R.id.deleteExpense);
                    final Button editExpense = (Button) v.findViewById(R.id.editExpense);
                    final JSONArray array;
                    String participantsString = "";
                    try{
                        array = new JSONArray(expense.getParticipants());
                        for(int i = 0;i<array.length();i++){
                            participantsString += array.getJSONObject(i).getString(AppConstants.JSONConstants.MEMBERNAME);
                            participantsString += ", ";
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    TextView textView = new TextView(context);
                    textView.setText(participantsString.subSequence(0, participantsString.length()-2));
                    textView.setTextColor(getResources().getColor(android.R.color.black));
                    textView.setSingleLine(true);
                    textView.setTextSize(18);
                    expenseParticipants.addView(textView);

                    expanderImage.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            if(expenseDetailsLayout.getVisibility() == View.GONE){
                                expenseDetailsLayout.setVisibility(View.VISIBLE);
                                expanderImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_collapse));
                            }else{
                                expenseDetailsLayout.setVisibility(View.GONE);
                                expanderImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_expand));
                            }
                        }
                    });

                    deleteExpense.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            deleteExpense(expense);
                            System.out.println("Delete Expense ID"+expense.getExpenseId());
                        }
                    });

                    editExpense.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            editExpense(expense);
                            System.out.println("Edit Expense ID"+expense.getExpenseId());
                        }
                    });
                }
                else{
                    SectionHeaderObject sectionHeader = (SectionHeaderObject) item;
                    v = li.inflate(R.layout.list_element_header_view, null);
                    final TextView headerField = (TextView) v.findViewById(R.id.groupByField);
                    final TextView totalAmountField = (TextView) v.findViewById(R.id.amount);
                    final View emptyViewTop = v.findViewById(R.id.emptyViewTop);
                    if(position != 0) {
                        emptyViewTop.setVisibility(View.VISIBLE);
                    } else {
                        emptyViewTop.setVisibility(View.GONE);
                    }

                    String dateString = "";
                    if(currentCalendar.get(Calendar.YEAR) == sectionHeader.getHeaderTitleCalendar().get(Calendar.YEAR)) {
                        if(currentCalendar.get(Calendar.DAY_OF_YEAR) == sectionHeader.getHeaderTitleCalendar().get(Calendar.DAY_OF_YEAR)) {
                            dateString = "Today";
                        } else if(currentCalendar.get(Calendar.DAY_OF_YEAR)-1 == sectionHeader.getHeaderTitleCalendar().get(Calendar.DAY_OF_YEAR)) {
                            dateString = "Yesterday";
                        } else {
                            dateString = Utils.formatDateWithoutYear("" + sectionHeader.getHeaderTitle());
                        }
                    } else {
                        dateString = Utils.formatDate(""+sectionHeader.getHeaderTitle());
                    }

                    headerField.setText(dateString);
                    totalAmountField.setText(getResources().getString(R.string.inr) + " "+Utils.round(sectionHeader.getAmount(), 2));
                }
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
        if(App.getInstance().getExpensesList() != null && App.getInstance().getExpensesList().size() != 0){
            System.out.println("Expenses"+ App.getInstance().getExpensesList());
            expenses.setVisibility(View.VISIBLE);
            noExpensesView.setVisibility(View.GONE);
            instructionsView.setVisibility(View.GONE);

            expenseAdapter = new ExpensesAdapter(Utils.gatherExpenses(AppConstants.DATE_WISE, App.getInstance().getExpensesList()));
            expenses.setAdapter(expenseAdapter);
        }
        else{
            instructionsView.setVisibility(View.VISIBLE);
            noExpensesView.setVisibility(View.VISIBLE);
            expenses.setVisibility(View.GONE);
        }
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
        context.startService(dbServiceIntent);
        //((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*if(App.getInstance().getExpensesList() != null){
            System.out.println("inside");
            System.out.println("starting service"+currentGroupId);
            context.startService(dbServiceIntent);
            //populateExpenses();
        }*/

    }

    public class DeleteExpenseRecevier extends BroadcastReceiver{

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
