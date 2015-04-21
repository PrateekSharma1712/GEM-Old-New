package com.prateek.gem.personal.expense;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.prateek.gem.R;
import com.prateek.gem.personal.expense.ViewExpenses.PlaceholderFragment;

public class FilterAdapter extends BaseAdapter {

	private String[] list = null;
	private PlaceholderFragment screen = null;
	private SparseBooleanArray checkedArray = null;
	private boolean[] checked;

	public FilterAdapter(String[] list, PlaceholderFragment screen) {
		super();
		this.list = list;
		this.screen = screen;
		checkedArray = new SparseBooleanArray();
		checked = new boolean[list.length];
	}

	@Override
	public int getCount() {
		
		return list.length;
	}

	@Override
	public Object getItem(int position) {
		
		return list[position];
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null) {
			LayoutInflater li = (LayoutInflater) screen.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = li.inflate(android.R.layout.simple_list_item_multiple_choice, null);
		}
		
		/*TextView text = (TextView) view.findViewById(R.id.checkText);
		final CheckBox check = (CheckBox) view.findViewById(R.id.checkView);
		text.setText(list[position]);
		System.out.println(checked[position]);
		check.setChecked(checked[position]);
		
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				checkedArray.put(position, isChecked);
				
				if(check.isChecked()) {
					checked[position] = true;
				} else {
					checked[position] = false;
				}
				System.out.println("checked[position]" +"position "+position +isChecked);
				
			}
		});*/
		
		return view;
	}
	
	public SparseBooleanArray getCheckedArray() {
		return checkedArray;
	}

}
