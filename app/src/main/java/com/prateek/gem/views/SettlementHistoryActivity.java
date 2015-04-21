package com.prateek.gem.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.prateek.gem.App;
import com.prateek.gem.R;
import com.prateek.gem.model.SettlementObject;

public class SettlementHistoryActivity extends ActionBarActivity {

	private ListView settlementHistoryListView;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settlement_history);
		
		initUI();
		
	}

	private void initUI() {
		context = this;
		settlementHistoryListView = (ListView) findViewById(R.id.settlementHistory);
		settlementHistoryListView.setAdapter(new HisabAdapter());
	}
	
	public class HisabAdapter extends BaseAdapter{

		public HisabAdapter() {
			super();			
			System.out.println("Settlements"+ App.getInstance().getSettlements());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return App.getInstance().getSettlements().size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return App.getInstance().getSettlements().get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null){
				LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.list_element_hisab,null);
			}
			
			final TextView giverField,takerField,amountTextView;
			
			
			giverField = (TextView) v.findViewById(R.id.giver);
			takerField = (TextView) v.findViewById(R.id.taker);
			amountTextView = (TextView) v.findViewById(R.id.amount);
			
			SettlementObject settlementItem = App.getInstance().getSettlements().get(position);
			
			giverField.setText(settlementItem.getGivenBy());
			takerField.setText(settlementItem.getTakenBy());
			amountTextView.setText(String.valueOf(settlementItem.getAmount()));
			
			return v;
		}
		
	}
}
