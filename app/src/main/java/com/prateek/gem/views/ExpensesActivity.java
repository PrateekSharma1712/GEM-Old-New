package com.prateek.gem.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.write.WriteException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.prateek.gem.AppConstants.JSONConstants;
import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.R;
import com.prateek.gem.SyncService;
import com.prateek.gem.items.ItemsActivity;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Item;
import com.prateek.gem.model.SectionHeaderObject;
import com.prateek.gem.persistence.DBAdapter;
import com.prateek.gem.persistence.DB.TExpenses;
import com.prateek.gem.persistence.DB.TGroups;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.services.MyDBService;
import com.prateek.gem.utils.CreateExcel;
import com.prateek.gem.utils.Utils;

public class ExpensesActivity extends MainActivity {

	ListView expenses;
	ExpensesAdapter expenseAdapter;
	RelativeLayout noExpensesView;
	ScrollView instructionsView;
	private Context context;
	Intent intent,dbServiceIntent,addExpenseIntent,membersIntent,itemsIntent,calculateIntent,graphIntent,mystatsIntent, pieChartIntent;
	MyResultReceiver resultReceiver;
	IntentFilter dbIntentFilter;
	int currentGroupId;
	MyProgressDialog pd;
	IntentFilter deleteExpenseIntentFilter,syncDataIntentFilter;
	DeleteExpenseRecevier deleteExpenseReceiver;
	SyncSuccessReceiver syncSuccessReceiver;
	ExpenseOject deletingExpense;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("CREATE");
		System.out.println(App.getInstance().getCurr_group());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setLogo(R.drawable.ic_launcher);
		initUI();
		intent = getIntent();
		currentGroupId = App.getInstance().getCurr_group().getGroupIdServer();
		resultReceiver = new MyResultReceiver();
		dbIntentFilter = new IntentFilter(AppConstants.DB_RECEIVER2);
		dbIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);		
		
		dbServiceIntent = new Intent(ExpensesActivity.this, MyDBService.class);
		dbServiceIntent.putExtra(AppConstants.SERVICE_ID, AppConstants.ServiceIDs.GET_EXPENSES);
		dbServiceIntent.putExtra(TGroups.GROUPID_SERVER, currentGroupId);
		System.out.println("starting service"+currentGroupId);
		startService(dbServiceIntent);
		
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_expenses;
    }

    private void initUI() {
		context = this;
		expenses = (ListView) findViewById(R.id.expenses);
		noExpensesView = (RelativeLayout) findViewById(R.id.noExpensesView);
		instructionsView = (ScrollView) findViewById(R.id.instructionsView);		
		addExpenseIntent = new Intent(ExpensesActivity.this, AddExpenseActivity.class);
		membersIntent = new Intent(ExpensesActivity.this, MembersActivity.class);
		itemsIntent = new Intent(ExpensesActivity.this, ItemsActivity.class);
		calculateIntent = new Intent(ExpensesActivity.this, HisabActivity.class);
		mystatsIntent = new Intent(ExpensesActivity.this, MyStatsActivity.class);
		graphIntent = new Intent(ExpensesActivity.this, GraphActivity.class);
        pieChartIntent = new Intent(ExpensesActivity.this, PieChartActivity.class);

		deleteExpenseReceiver = new DeleteExpenseRecevier();
        deleteExpenseIntentFilter = new IntentFilter(DeleteExpenseRecevier.DELETEEXPENSESUCCESSRECEIVER);
        deleteExpenseIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        
        syncSuccessReceiver = new SyncSuccessReceiver();
        syncDataIntentFilter = new IntentFilter(SyncSuccessReceiver.SUCCESS_RECEIVER);
        syncDataIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        
        registerReceiver(deleteExpenseReceiver,deleteExpenseIntentFilter);
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.expenses, menu);
	    
	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setSubmitButtonEnabled(true);
	    searchView.setQueryRefinementEnabled(true);
	    searchView.setQueryHint("item,amount,expense by");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    case R.id.add_expense:
	    	if(!App.getInstance().getItems().isEmpty()){
	    		startActivity(addExpenseIntent);
	    	}else{
	    		Utils.showToast(context, getString(R.string.addItemsMessage));
	    	}
	    	
	    	return true;
	    case R.id.members:
	    	
	    	startActivity(membersIntent);
	    	return true;
	    case R.id.items:
	    	
	    	startActivity(itemsIntent);
	    	return true;
	    case R.id.calculate:
	    	
	    	startActivity(calculateIntent);
	    	return true;
	    /*case R.id.graph:
	    	
	    	startActivity(graphIntent);
	    	return true;*/
	    
		
		case R.id.mystats:
			startActivity(mystatsIntent);
			System.out.println("in mystats click"+ App.getInstance().getCurr_group());
			
			return true;
			
		case R.id.action_sync:
			performSync();
			return true;
			
		case R.id.export:
				final AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Choose month");
                final String[] months = Utils.getMonths();
                builder.setItems(months, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int position) {
                            try {
                                handleExport(months[position]);
                            } catch (WriteException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.show();
            return true;
        case R.id.action_piechart:
            startActivity(pieChartIntent);
            return true;
        }




	    return super.onOptionsItemSelected(item);
	}	
	
	private void handleExport(String month) throws WriteException, IOException {
		CreateExcel test = new CreateExcel(this);
		test.setParam(month);
	    test.setOutputFile(App.getInstance().getCurr_group().getGroupName()+"_"+month+".xls");
	    test.write();
	}

	private void performSync() {
		System.out.println(App.getInstance().getCurr_group().getGroupIdServer());
		DBImpl.deleteAllStuff(App.getInstance().getCurr_group().getGroupIdServer(), false);
		
		//load full data related to group
		
		pd = new MyProgressDialog(this, true, "Syncing group");
		pd.show();
		
		Intent syncServiceIntent = new Intent(ExpensesActivity.this,SyncService.class);
		startService(syncServiceIntent);
		
	}

	public class ExpensesAdapter extends BaseAdapter{

		private List<Item> items;
		LayoutInflater li;
		
		public ExpensesAdapter(List<Item> items) {
			super();
			this.items = items;
			li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
					expenseAmount.setText(Utils.addRupeeIcon(expense.getAmount()));
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
							participantsString += array.getJSONObject(i).getString(JSONConstants.MEMBERNAME);
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
					headerField.setText(Utils.formatDate(""+sectionHeader.getHeaderTitle()));
					totalAmountField.setText(Utils.addRupeeIcon(Utils.round(sectionHeader.getAmount(), 2)));
				}
			}
			return v;
		}
		
	}
	
	public void deleteExpense(final ExpenseOject expense){
		deletingExpense = expense;
		String message = "Are you sure to delete following expense:\n\nExpense by: " +
				Html.fromHtml("<b>"+deletingExpense.getExpenseBy()+"</b>") +
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
				list.add(new BasicNameValuePair(TExpenses.EXPENSE_ID, ""+deletingExpense.getExpenseIdServer()));
				list.add(new BasicNameValuePair("realexpenseId", ""+deletingExpense.getExpenseId()));
				list.add(new BasicNameValuePair(TExpenses.GROUP_ID_FK, ""+deletingExpense.getGroupId()));
				list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.DELETE_EXPENSE));
				
				pd = new MyProgressDialog(context,true, "Deleting Expense");
				pd.show();
				System.out.println("Expense server id "+deletingExpense.getExpenseIdServer());
				FullFlowService.ServiceDeleteExpense(context,AppConstants.NOT_DELETEEXPENSE, list);
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
	
	public class MyResultReceiver extends BroadcastReceiver{	
		
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
	protected void onDestroy() {
		super.onDestroy();	
		System.out.println("DESTROY");
		unregisterReceiver(deleteExpenseReceiver);
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("PAUSE");
		System.out.println("onpause.............");
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("STOP");
		unregisterReceiver(resultReceiver);
		unregisterReceiver(syncSuccessReceiver);
	}

	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("START");
		registerReceiver(resultReceiver,dbIntentFilter);
		registerReceiver(syncSuccessReceiver, syncDataIntentFilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("RESUME");
        setToolbar("Expenses", R.drawable.ic_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(App.getInstance().getExpensesList() != null){
			System.out.println("inside");
			populateExpenses();
		}
		
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
					App.getInstance().setAllGroups(Group.updateTotalExpense(deletingExpense.getGroupId(), App.getInstance().getAllGroups(), App.getInstance().getCurr_group().getTotalOfExpense(), deletingExpense.getAmount(), 0));
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
	
	public class SyncSuccessReceiver extends BroadcastReceiver{

		public static final String SUCCESS_RECEIVER = "com.prateek.gem.views.ExpenseActivity.SyncSuccessReceiver.SuccessReceiver";

		@Override
		public void onReceive(Context cxt, Intent receivingIntent) {

			populateExpenses();
			
			if(pd != null) {
				pd.dismiss();
			}
		}
		
	}
	
	
}
