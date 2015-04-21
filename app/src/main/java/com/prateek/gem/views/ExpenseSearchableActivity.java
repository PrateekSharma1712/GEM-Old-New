package com.prateek.gem.views;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.R;
import com.prateek.gem.AppConstants.JSONConstants;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Item;
import com.prateek.gem.model.SectionHeaderObject;
import com.prateek.gem.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ExpenseSearchableActivity extends ActionBarActivity {

	private ListView expenses_searchview;
	SearchExpensesAdapter searchExpenseAdapter;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expense_searchable);
		context = this;
		// get the action bar
        ActionBar actionBar = getSupportActionBar();
 
        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
		expenses_searchview = (ListView) findViewById(R.id.expenses_search);
		
		handleIntent(getIntent());
	}
	
	/**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
 
            /**
             * Use this query to display search results like 
             * 1. Getting the data from SQLite and showing in listview 
             * 2. Making webrequest and displaying the data 
             * For now we just display the query only
             */
            //txtQuery.setText("Search Query: " + query);
            List<ExpenseOject> searchedExpenses = new ArrayList<ExpenseOject>();
            System.out.println("List "+query);
            for(ExpenseOject eo: App.getInstance().getExpensesList()){
            	if(eo.toString().toLowerCase().contains(query.toLowerCase())){
            		searchedExpenses.add(eo);
            	}
            }
            
            System.out.println("Searched Expenses"+searchedExpenses);
            List<Item> searchedItems = Utils.gatherExpenses(AppConstants.DATE_WISE,searchedExpenses);
            searchExpenseAdapter = new SearchExpensesAdapter(searchedItems);
            expenses_searchview.setAdapter(searchExpenseAdapter);
        }
 
    }
    
    public class SearchExpensesAdapter extends BaseAdapter{

		private List<Item> items;
		LayoutInflater li;
		
		public SearchExpensesAdapter(List<Item> items) {
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
					textView.setTextAppearance(context,android.R.attr.textAppearanceMedium);
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
							//deleteExpense(expense);
							System.out.println("Delete Expense ID"+expense.getExpenseId());
						}
					});
					
					editExpense.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {		
							//editExpense(expense);
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
}
