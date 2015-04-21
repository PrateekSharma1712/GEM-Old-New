package com.prateek.gem.personal.expense;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.prateek.gem.R;
import com.prateek.gem.persistence.DBAdapter;
import com.prateek.gem.utils.Utils;

public class AddExpense extends ActionBarActivity implements OnClickListener, OnDateSetListener, OnItemSelectedListener {

	Context context = null;
	DBAdapter db = null;
	private EditText dateField = null;
	private Spinner categories = null;
	private Spinner modeOfPayment = null;
	private AutoCompleteTextView expenseFor = null;
	private EditText amountView = null;
	long dateSelectedInMillis;
	private String[] categoryArray = null;
	private String[] itemsArray = null;
	private String[] modesArray = null;
	private ArrayList<MyItems> items = null;
	private ArrayAdapter<String> itemsAdapter = null;
	private View errorView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_add_expense);
		initUI();
	}
	
	private void initUI() {
		context = this;
		db = new DBAdapter(context);
		db.open();
		db.cretateTables();
		System.out.println(db.getExpenses());
		db.close();
		
		dateField = (EditText) findViewById(R.id.dateField);
		dateField.setOnClickListener(this);
		
		categories = (Spinner) findViewById(R.id.categories);
		categoryArray = getResources().getStringArray(R.array.categoryarray);
    	ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(context, R.layout.my_simple_spinner_item, categoryArray);
    	categoriesAdapter.setDropDownViewResource(R.layout.listitem_dropdown);
    	categories.setAdapter(categoriesAdapter);
    	categories.setOnItemSelectedListener(this);
    	
    	modeOfPayment = (Spinner) findViewById(R.id.modeOfPayment);
		modesArray = getResources().getStringArray(R.array.modesarray);
    	ArrayAdapter<String> modesAdapter = new ArrayAdapter<String>(context, R.layout.my_simple_spinner_item, modesArray);
    	modesAdapter.setDropDownViewResource(R.layout.listitem_dropdown);
    	modeOfPayment.setAdapter(modesAdapter);
    	
    	expenseFor = (AutoCompleteTextView) findViewById(R.id.expenseFor);
    	
    	amountView = (EditText) findViewById(R.id.expenseAmount);
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
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_expense, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
        if (id == R.id.action_saveExpense) {
        	saveExpense();
            return true;
        } else if(id == R.id.viewexpense) {
        	openExpenses();
        	return true;
        }
        return super.onOptionsItemSelected(item);
	}

	private void saveExpense() {
		String selectedItem = expenseFor.getText().toString();
		String selectedCategory = categories.getSelectedItem().toString();
		String selectedMode = modeOfPayment.getSelectedItem().toString();
		float amount = 0f;
		if(!TextUtils.isEmpty(amountView.getText().toString()))
			amount = Float.parseFloat(amountView.getText().toString());
		
		if(errorView != null) {
			errorView.clearFocus();
		}
		if(validateExpenseData(selectedItem, amount)) {
			MyExpenses expense = new MyExpenses();
			expense.setExpDate(String.valueOf(dateSelectedInMillis));
			expense.setAmount(amount);
			expense.setMode(selectedMode);
			
			MyItems item = MyItems.contains(selectedItem, items);
			System.out.println("item "+item);
			if(item == null) {
				ContentValues itemValues = new ContentValues();
				itemValues.put(DBAdapter.ITEMS_NAME, selectedItem);
				itemValues.put(DBAdapter.ITEMS_CATEGORY, selectedCategory);
				db.open();
				long rowId = db.insert(DBAdapter.TABLE_ITEMS, itemValues);
				db.close();
				expense.setItemId((int) rowId);
			} else {
				expense.setItemId(item.getItemId());
				items.add(item);
			}
			
			ContentValues expenseValues = new ContentValues();
			expenseValues.put(DBAdapter.EXP_DATE, expense.getExpDate());
			expenseValues.put(DBAdapter.EXP_AMOUNT, expense.getAmount());
			expenseValues.put(DBAdapter.EXP_ITEM, expense.getItemId());
			expenseValues.put(DBAdapter.EXP_MODE, expense.getMode());
			db.open();
			long rowId = db.insert(DBAdapter.TABLE_EXPENSES, expenseValues);
			System.out.println("Expense Added :" +rowId);
			db.close();
			System.out.println("expense"+expense);
			if(rowId > 0) {
				Utils.showToast(context, "Added Expense");
				amountView.setText("");
				dateField.setText("");
			}
		}
	}
	
	private void openExpenses() {
		Intent intent = new Intent(AddExpense.this,ViewExpenses.class);
		startActivity(intent);
	}

	private boolean validateExpenseData(String selectedItem, float amount) {
		boolean result = false;
		
		if(dateSelectedInMillis == 0){
			errorView = dateField;
			Utils.showError(dateField, (AddExpense)context, getString(R.string.psdate));			
		}else if(TextUtils.isEmpty(selectedItem)){
			errorView = expenseFor;
			Utils.showError(expenseFor, (AddExpense)context, getString(R.string.psdate));
		}else if(TextUtils.isEmpty(String.valueOf(amount))){
			errorView = amountView;
			Utils.showError(amountView, (AddExpense)context, getString(R.string.psamountempty));					
		}else if(amount == 0f){
			errorView = amountView;
			Utils.showError(amountView, (AddExpense)context, getString(R.string.psamountvalid));				
		}else{
			result = true;
		}
		
		return result;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long id) {
		db.open();
    	items = (ArrayList<MyItems>) db.getItems(categoryArray[position]);
    	db.close();
    	itemsArray = getItemNameArray(items);
    	itemsAdapter = new ArrayAdapter<String>(context,R.layout.my_simple_spinner_item,itemsArray);
    	
    	expenseFor.setAdapter(itemsAdapter);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private String[] getItemNameArray(ArrayList<MyItems> items) { 
		String[] array = new String[items.size()];
    	for(int i = 0;i<items.size();i++) {
    		array[i] = items.get(i).getItemName();
    	}
    	
    	return array;
	}

}
