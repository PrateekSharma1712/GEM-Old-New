package com.prateek.gem.personal.expense;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prateek.gem.R;
import com.prateek.gem.persistence.DBAdapter;
import com.prateek.gem.personal.expense.ViewExpenses.PlaceholderFragment;
import com.prateek.gem.utils.Utils;

public class ExpensesAdapter extends BaseAdapter {

	private PlaceholderFragment screen = null;
	private ArrayList<MyExpenses> expenses = null;
	private DBAdapter db = null;
	private float totalAmount = 0f;

	public ExpensesAdapter(PlaceholderFragment screen, ArrayList<MyExpenses> expenses) {
		super();
		this.screen = screen;
		this.expenses = expenses;
		db = new DBAdapter(screen.getActivity());
	}
	
	public void setTotalAmount(float amount) {
		totalAmount = amount; 
	}

	@Override
	public int getCount() {
		if(expenses.size() > 0)
			return expenses.size()+1;
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return expenses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return expenses.get(position).getExpenseId();
	}
	
	static class ViewHolder {
		private TextView dateView, itemView, amountView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		db.open();
		if(view == null) {
			LayoutInflater inflator = (LayoutInflater) screen.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.row_item_expenses, null);
			
			holder = new ViewHolder();
			holder.dateView = (TextView) view.findViewById(R.id.dateView);
			holder.amountView = (TextView) view.findViewById(R.id.amountView);
			holder.itemView = (TextView) view.findViewById(R.id.itemView);
			view.setTag(holder);
		} 
		
		holder = (ViewHolder) view.getTag();
		if(position == 0) {
			holder.dateView.setTypeface(holder.dateView.getTypeface(), Typeface.BOLD);
			holder.amountView.setTypeface(holder.amountView.getTypeface(), Typeface.BOLD);
			holder.itemView.setTypeface(holder.itemView.getTypeface(), Typeface.BOLD);
			
			holder.dateView.setText("Date");
			holder.itemView.setText("Item");
			holder.amountView.setText("Amount\n("+totalAmount+")");
		} else {
			MyExpenses expense = expenses.get(position - 1);
			System.out.println(expense);
			holder.dateView.setText(Utils.formatShortDate(expense.getExpDate()));
			MyItems item = MyItems.getItem(expense.getItemId(), (ArrayList<MyItems>) db.getItems(null));
			if(item != null) {
				holder.itemView.setText(item.getItemName());
			}
			holder.amountView.setText(String.valueOf(expense.getAmount()));
		}
		db.close();
		return view;
	}

}
