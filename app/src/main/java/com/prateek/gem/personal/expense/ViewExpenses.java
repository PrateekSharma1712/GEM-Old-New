package com.prateek.gem.personal.expense;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Switch;
import android.widget.TextView;

import com.prateek.gem.App;
import com.prateek.gem.R;
import com.prateek.gem.persistence.DBAdapter;

public class ViewExpenses extends ActionBarActivity implements
		ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static DBAdapter db = null;
	private static float totalAmount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_expenses);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] groupsArray = new String[App.getInstance().getAllGroups().size() + 1];
		groupsArray[0] = "Personal";
		for(int i = 0;i< App.getInstance().getAllGroups().size();i++) {
			groupsArray[i+1] = App.getInstance().getAllGroups().get(i).getGroupName();
		}
		
		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, groupsArray), this);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_expenses, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position)).commit();
		return true;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		private ListView expensesView = null;
		private PlaceholderFragment context = null;
		private ExpensesAdapter adapter = null;
		private SlidingDrawer slider = null;
		private TextView handle = null;
		private ListView categoryList = null;
		private Switch categoryToggle = null;
		private FilterAdapter categoryAdapter = null;
		private String[] categoryArray = null;
		private Button done = null;

		private ArrayList<MyExpenses> expenses = null;
		private ArrayList<MyItems> items = null;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			context = this;
			categoryArray = getResources().getStringArray(R.array.categoryarray);
			db = new DBAdapter(context.getActivity());
			View rootView = inflater.inflate(R.layout.personal_view_expenses,
					container, false);
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
			case 0:
				createPersonalUI(rootView);
				break;

			default:
				System.out.println("Groups");
				break;
			}
			return rootView;
		}

		@SuppressWarnings("deprecation")
		private void createPersonalUI(View rootView) {
			db.open();
			expenses  = (ArrayList<MyExpenses>) db.getExpenses();
			items = (ArrayList<MyItems>) db.getItems(null);
			expensesView = (ListView) rootView.findViewById(R.id.expensesList);
			categoryList = (ListView) rootView.findViewById(R.id.categoryList);
			slider = (SlidingDrawer) rootView.findViewById(R.id.drawer);
			categoryToggle = (Switch) rootView.findViewById(R.id.categoryToggle);
			done = (Button) rootView.findViewById(R.id.done);
			adapter = new ExpensesAdapter(context, expenses);
			expensesView.setAdapter(adapter);
			db.close();
			
			for(MyExpenses e : expenses) {
				System.out.println(e.getExpenseId() + ".."+e.getAmount());
				totalAmount += e.getAmount();
			}
			
			adapter.setTotalAmount(totalAmount);
			
			categoryToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton button, boolean checked) {
					if(checked) {
						System.out.println("visible");
						categoryList.setVisibility(View.VISIBLE);
						
						if(categoryAdapter == null) {
							categoryAdapter = new  FilterAdapter(categoryArray, context);
							categoryList.setAdapter(new ArrayAdapter<String>(context.getActivity(), android.R.layout.simple_list_item_multiple_choice, categoryArray));
						} else{
							categoryAdapter.notifyDataSetChanged();
						}
					} else{
						System.out.println("gone");
						categoryList.setVisibility(View.GONE);
					}
				}
			});
			
			done.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					/*SparseBooleanArray checkedArray = categoryAdapter.getCheckedArray();
					ExpenseFilters.clearFilters();
					for(int i = 0;i<checkedArray.size();i++) {
						if(checkedArray.get(checkedArray.keyAt(i))) {
							ExpenseFilters.addCategory(categoryArray[checkedArray.keyAt(i)]);
						}
					}*/
					
					SparseBooleanArray checkedItemPositions = categoryList.getCheckedItemPositions();
					ExpenseFilters.clearFilters();
					for(int i = 0;i<checkedItemPositions.size();i++){
						int position = checkedItemPositions.keyAt(i);
						if(checkedItemPositions.valueAt(i)){
							ExpenseFilters.addCategory(categoryArray[position]);
						}
					}
					System.out.println(ExpenseFilters.getCategoryFilter());
					slider.close();
					refreshExpenses();
				}
			});
		}

		protected void refreshExpenses() {
			List<MyExpenses> temp = new ArrayList<MyExpenses>();
			for(MyExpenses e : expenses) {
				if(ExpenseFilters.getCategoryFilter().contains(MyItems.getItem(e.getItemId(), items).getCategory())) {
					temp.add(e);
				}
			}
			
			if(ExpenseFilters.getCategoryFilter().size() != 0) {
				expenses.clear();
				expenses.addAll(temp);
				adapter.notifyDataSetChanged();
			}
		}
		
		
	}

}
