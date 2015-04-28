package com.prateek.gem.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.AppConstants.JSONConstants;
import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.R;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Items;
import com.prateek.gem.model.Member;
import com.prateek.gem.persistence.DB.TExpenses;
import com.prateek.gem.utils.LoadingScreen;
import com.prateek.gem.utils.Utils;

public class AddExpenseActivity extends MainActivity implements OnClickListener,OnDateSetListener{

	private Spinner expenseBy,expenseFor;
	private EditText dateField,amountField;
	private Button participantsButton;
	private ArrayAdapter<String> itemsAdapter,membersAdapter;
	private boolean[] checkedMembers;
	List<Member> tempSelectedParticipantsList;	
	private List<Member> selectedPartcipants;
	long dateSelectedInMillis;
	List<String> items;	
	private Context context;
	private AlertDialog ad;
	IntentFilter addExpenseIntentFilter;
	AddExpenseRecevier addExpenseReceiver;
	ExpenseOject addingExpense;
	final List<Integer> checkedPositions = new ArrayList<Integer>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar("Add Expense", R.drawable.ic_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_expense;
    }

    public void initUI(){
		context = this;
		dateField = (EditText) findViewById(R.id.dateField);
		dateField.setOnClickListener(this);
		participantsButton = (Button) findViewById(R.id.participantsButton);
		participantsButton.setOnClickListener(this);
		amountField = (EditText) findViewById(R.id.expenseAmount);
		expenseBy = (Spinner) findViewById(R.id.expenseBy);
		expenseFor = (Spinner) findViewById(R.id.expenseFor);
		
		items = Items.getItemNameOfItems(App.getInstance().getItems(), App.getInstance().getCurr_group().getGroupIdServer());
		itemsAdapter = new ArrayAdapter<String>(context, R.layout.my_simple_spinner_item,items);
        itemsAdapter.setDropDownViewResource(R.layout.listitem_dropdown);
        expenseFor.setAdapter(itemsAdapter);
        
        List<String> membersNames = new ArrayList<String>();
        checkedMembers = new boolean[App.getInstance().getCurr_Members().size()];
        //checking members
        int i = 0;
        for(Member member: App.getInstance().getCurr_Members()){
        	membersNames.add(member.getMemberName());
        	checkedMembers[i] = true;
        	checkedPositions.add(i);
        	i++;
        }       
        membersAdapter = new ArrayAdapter<String>(context, R.layout.my_simple_spinner_item, membersNames);
        membersAdapter.setDropDownViewResource(R.layout.listitem_dropdown);
        expenseBy.setAdapter(membersAdapter);
        
        addExpenseReceiver = new AddExpenseRecevier();
        addExpenseIntentFilter = new IntentFilter(AddExpenseRecevier.ADDEXPENSESUCCESSRECEIVER);
        addExpenseIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(addExpenseReceiver, addExpenseIntentFilter);
        
        selectedPartcipants = new ArrayList<Member>();
        for(Member m : App.getInstance().getCurr_Members()){
        	selectedPartcipants.add(m);
        }
        participantsButton.setText(App.getInstance().getCurr_Members().size()+" out of "+ App.getInstance().getCurr_Members().size()+" selected");
		participantsButton.setBackgroundResource(R.drawable.edittext_style);
		participantsButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_navigation_accept), null);
		
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_expense, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_saveExpense) {
			saveExpense();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveExpense() {
		App.getInstance().setParticipantsList(selectedPartcipants);
		String amount = Utils.stringify(amountField.getText());
		List<Member> participantsList = App.getInstance().getParticipantsList();
		Items selectedItem = App.getInstance().getItems().get(expenseFor.getSelectedItemPosition());
		int selectedExpensee =  expenseBy.getSelectedItemPosition();
		System.out.println("selected expensee"+ App.getInstance().getCurr_Members().get(selectedExpensee).getMemberName());
		System.out.println("selected item"+selectedItem);
		
		if(checkEntryValidation(dateSelectedInMillis,amount,participantsList,selectedItem,selectedExpensee)){
			System.out.println("true");
			float share = Utils.round(Float.parseFloat(amount)/participantsList.size(), 2);				
			JSONArray array = null;
			JSONObject object = null;
			try {
				array = new JSONArray();					
				for(Member m : App.getInstance().getParticipantsList()){
						object = new JSONObject();						
						object.put(JSONConstants.MEMBERNAME,m.getMemberName());							
						array.put(object);
				} 					
			}catch (JSONException e) {					
				e.printStackTrace();
			}			
		
			addingExpense = new ExpenseOject(dateSelectedInMillis, 
					Utils.round(Float.parseFloat(amount), 3), 
					Utils.round(share, 3), 
					selectedItem.getItemName(), 
					App.getInstance().getCurr_Members().get(selectedExpensee).getMemberName(),
					array.toString(), 
					App.getInstance().getCurr_group().getGroupIdServer());
			
			System.out.println(addingExpense);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair(TExpenses.AMOUNT, ""+addingExpense.getAmount()));
			list.add(new BasicNameValuePair(TExpenses.DATE_OF_EXPENSE, ""+addingExpense.getDate()));
			list.add(new BasicNameValuePair(TExpenses.EXPENSE_BY, addingExpense.getExpenseBy()));
			list.add(new BasicNameValuePair(TExpenses.GROUP_ID_FK, ""+addingExpense.getGroupId()));
			list.add(new BasicNameValuePair(TExpenses.ITEM, addingExpense.getItem()));
			list.add(new BasicNameValuePair(TExpenses.PARTICIPANTS, addingExpense.getParticipants()));
			list.add(new BasicNameValuePair(TExpenses.SHARE, ""+addingExpense.getShare()));
			list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.ADD_EXPENSE));
			
			ContentValues cv = new ContentValues();
			cv.put(TExpenses.AMOUNT, addingExpense.getAmount());
			cv.put(TExpenses.DATE_OF_EXPENSE, addingExpense.getDate());
			cv.put(TExpenses.EXPENSE_BY, addingExpense.getExpenseBy());
			cv.put(TExpenses.GROUP_ID_FK, addingExpense.getGroupId());
			cv.put(TExpenses.ITEM, addingExpense.getItem());
			cv.put(TExpenses.PARTICIPANTS, addingExpense.getParticipants());
			cv.put(TExpenses.SHARE, ""+addingExpense.getShare());
			
			LoadingScreen.showLoading(context, "Adding Expense");
			FullFlowService.ServiceAddExpense(context,AppConstants.NOT_ADDEXPENSE,list,cv);
			
			//new AddExpenseTask(expense).execute();
		}
		
	}


	private boolean checkEntryValidation(long date, String amount, List<Member> participantsList, Items selectedItem, int selectedExpensee) {
		boolean retVal = false;
		if(date == 0){
			Utils.showError(dateField, (AddExpenseActivity)context, getString(R.string.psdate));			
		}else if(selectedExpensee == -1){
				
		}else if(selectedItem.equals(AppConstants.SPENT_FOR)){
			
		}else if(amount.equals("")){
			Utils.showError(amountField, (AddExpenseActivity)context, getString(R.string.psamountempty));					
		}else if(Float.parseFloat(amount) == 0f){
			Utils.showError(amountField, (AddExpenseActivity)context, getString(R.string.psamountvalid));				
		}else if(participantsList == null){
			Utils.showErrorOnButton(participantsButton, (AddExpenseActivity)context, getString(R.string.psparticipants));
		}else if(participantsList.size() == 0){
			Utils.showErrorOnButton(participantsButton, (AddExpenseActivity)context, getString(R.string.psparticipants));
		}else{
			retVal = true;
		}
		return retVal;
	}


	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, monthOfYear);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		dateSelectedInMillis = c.getTimeInMillis();
		dateField.setText(Utils.formatDate(""+c.getTimeInMillis()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dateField:
			Calendar c = Calendar.getInstance();			
			new DatePickerDialog(context, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.participantsButton:
			showParticipantsDialog(context);
			break;

		default:
			break;
		}
	}
	
	private void showParticipantsDialog(Context context) {
		
		final String[] members = new String[App.getInstance().getCurr_Members().size()];
		
		int i = 0;
		for(Member m: App.getInstance().getCurr_Members()){
			//System.out.println(m);
			members[i] = m.getMemberName();
			//checkedMembers[i] = true;
			//checkedPositions.add(i);
			i++;
		}
		selectedPartcipants = new ArrayList<Member>();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.title_fragment_participants);
		builder.setMultiChoiceItems(members, checkedMembers, new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int position,boolean checked) {
				System.out.println("position"+checked+".."+position);
				//System.out.println(checkedPositions);
				if(checked){
					checkedPositions.add(position);
				}else if(checkedPositions.contains(position)){
					checkedPositions.remove(Integer.valueOf(position));
				}
				//System.out.println(checkedPositions);
			}
		});
		
		builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//System.out.println(checkedPositions);
				for(Integer a: checkedPositions){
					selectedPartcipants.add(App.getInstance().getCurr_Members().get(a));
				}
				App.getInstance().setParticipantsList(selectedPartcipants);
				System.out.println(selectedPartcipants);
				participantsButton.setText(selectedPartcipants.size()+" out of "+members.length+" selected");
				participantsButton.setBackgroundResource(R.drawable.edittext_style);
				participantsButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_navigation_accept), null);				
				ad.dismiss();
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ad.dismiss();
			}			
		});
		ad = builder.create();
		builder.show();
	}
	


	public class AddExpenseRecevier extends BroadcastReceiver{

		public static final String ADDEXPENSESUCCESSRECEIVER = "com.prateek.gem.views.AddExpenseActivity.ADDEXPENSESUCCESSRECEIVER";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			LoadingScreen.dismissProgressDialog();
			int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
			boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);
			switch (notId) {
			case AppConstants.NOT_ADDEXPENSE:
				if(result){					
					Utils.showToast(context, "Added Succesfully");
					App.getInstance().getExpensesList().add(addingExpense);
					//App.getInstance().setAllGroups(Group.updateTotalExpense(addingExpense.getGroupId(), App.getInstance().getAllGroups(), App.getInstance().getCurr_group().getTotalOfExpense(), addingExpense.getAmount(), 1));
					finish();
				}else{
					Utils.showToast(context, "Cannot add, Please try after some time");	
				}
				break;
			
			default:
				break;
			}
		}		
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(addExpenseReceiver);
	}
}
