package com.prateek.gem.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.R;
import com.prateek.gem.AppConstants.JSONConstants;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.GiverTakerObject;
import com.prateek.gem.model.SettlementObject;
import com.prateek.gem.persistence.DBAdapter.TExpenses;
import com.prateek.gem.persistence.DBAdapter.TSettlement;
import com.prateek.gem.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HisabActivity extends ActionBarActivity {

	private ListView hisabList;	
	private ImageView saveSettlement,cancelSettlement;
	private RelativeLayout settlementSection;
	private TextView querySentence;
	private String leftName,rightName;
	private EditText amountEntryField;
	private String queryString;
	Animation animation;
	private Context context;
	private float amount;
	private float enteredAmount;	
	private List<SettlementObject> settlements;
	Intent setHistory;
	MyProgressDialog pd;
	IntentFilter addSettlementIntentFilter;
	AddSettlementRecevier addSettlementReceiver;
	SettlementObject addingSettlement;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hisab);
		initUI();
		CalculateHisabAsycTask calculateHisabAsycTask = new CalculateHisabAsycTask();
		calculateHisabAsycTask.execute(new String[]{""});
		
	}
	
	public void initUI(){
		context = this;
		
		hisabList = (ListView) findViewById(R.id.hisabs);
		saveSettlement = (ImageView) findViewById(R.id.saveSettlement);
		cancelSettlement = (ImageView) findViewById(R.id.cancelSettlement);		
		settlementSection = (RelativeLayout) findViewById(R.id.settlementSection);	
		querySentence = (TextView) findViewById(R.id.querySentence);
		amountEntryField = (EditText) findViewById(R.id.amountField);
		
		saveSettlement.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!amountEntryField.getText().toString().equals("")){
					enteredAmount = Utils.round(Float.parseFloat(Utils.stringify(amountEntryField.getText())),2);
					if(enteredAmount == 0){
						Utils.showError(amountEntryField, (HisabActivity)context, "Cannot be 0");					
					}/*else if(enteredAmount > amount){
						Utils.showError(amountEntryField, (HisabActivity)context, "Cannot give more than required");
					}*/
					else{						
						settlementSection.setVisibility(View.GONE);
						String date = String.valueOf(new Date().getTime());
						List<NameValuePair> list = new ArrayList<NameValuePair>();
						list.add(new BasicNameValuePair(TExpenses.GROUP_ID_FK, String.valueOf(App.getInstance().getCurr_group().getGroupIdServer())));
						list.add(new BasicNameValuePair("spentby", (String) leftName));
						list.add(new BasicNameValuePair("takenby", (String) rightName));
						list.add(new BasicNameValuePair(TExpenses.AMOUNT, String.valueOf(enteredAmount)));
						list.add(new BasicNameValuePair("date", date));
						list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.ADD_SETTLEMENT));
					
						addingSettlement = new SettlementObject();
						addingSettlement.setGivenBy(leftName);
						addingSettlement.setTakenBy(rightName);
						addingSettlement.setAmount(enteredAmount);
						addingSettlement.setDate(date); 
						addingSettlement.setGroupId(App.getInstance().getCurr_group().getGroupIdServer());
						
						ContentValues cv = new ContentValues();						
						cv.put(TSettlement.GROUP_ID_FK, App.getInstance().getCurr_group().getGroupIdServer());
						cv.put(TSettlement.GIVEN_BY, leftName);
						cv.put(TSettlement.TAKEN_BY, rightName);
						cv.put(TSettlement.AMOUNT, enteredAmount);
						cv.put(TSettlement.DATE, date);
						
						pd = new MyProgressDialog(context, true, "Adding Settlement");
						pd.show();
						
						FullFlowService.ServiceAddSettlement(context,AppConstants.NOT_ADDSETTLE, list,cv);
						//new AddSettlementTask().execute(new Object[]{leftName,rightName,enteredAmount});
					}
				}else{
					Utils.showError(amountEntryField, (HisabActivity)context, "Cannot be empty");
				}
			}
		});
		
		cancelSettlement.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				settlementSection.setVisibility(View.GONE);
			}
		});
		
		addSettlementReceiver = new AddSettlementRecevier();
        addSettlementIntentFilter = new IntentFilter(AddSettlementRecevier.ADDSETTLEMENTSUCCESSRECEIVER);
        addSettlementIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(addSettlementReceiver, addSettlementIntentFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hisab, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_set_history) {
			if(!App.getInstance().getSettlements().isEmpty()){
				setHistory = new Intent(HisabActivity.this, SettlementHistoryActivity.class);
				startActivity(setHistory);
			}else{
				Utils.showToast(context, getString(R.string.nosettlements));
			}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	private class CalculateHisabAsycTask extends AsyncTask<String, Void, Map<GiverTakerObject, Float>>{

		@Override
		protected Map<GiverTakerObject, Float> doInBackground(String... params) {

			Map<GiverTakerObject, Float> hisabMap = new HashMap<GiverTakerObject, Float>();
			JSONArray jsonArray;		
			try{
				for(ExpenseOject expenseObject: App.getInstance().getExpensesList()){
					jsonArray = new JSONArray(expenseObject.getParticipants());
					for(int i = 0;i<jsonArray.length();i++){
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if(!expenseObject.getExpenseBy().equals(jsonObject.getString(JSONConstants.MEMBERNAME))){ // If expense by and participant are not same
							// for checking the to and fro kharcha(Choti-Ankur, Ankur-Choti)
							GiverTakerObject giverTakerObject = new GiverTakerObject(expenseObject.getExpenseBy(), jsonObject.getString(JSONConstants.MEMBERNAME));
							GiverTakerObject reverseObject = new GiverTakerObject(jsonObject.getString(JSONConstants.MEMBERNAME), expenseObject.getExpenseBy());							
							
							if(hisabMap.containsKey(reverseObject)){
								float newshare = hisabMap.get(reverseObject) - expenseObject.getShare();										
								hisabMap.put(reverseObject, Utils.round(newshare, 2));								
							}
							else if(hisabMap.containsKey(giverTakerObject)){
								float newshare = hisabMap.get(giverTakerObject) + expenseObject.getShare();
								hisabMap.put(giverTakerObject, Utils.round(newshare, 2));
							}
							else{
								hisabMap.put(giverTakerObject, expenseObject.getShare());
							}						
						}					
					}				
				}
				
				System.out.println("hisab +++++"+ App.getInstance().getSettlements());
				//Handling Settlements if any
				if(App.getInstance().getSettlements().size() > 0){
					for(SettlementObject object: App.getInstance().getSettlements()){
						GiverTakerObject giverTakerObject = new GiverTakerObject(object.getGivenBy(), object.getTakenBy());
						GiverTakerObject reverseObject = new GiverTakerObject(object.getTakenBy(), object.getGivenBy());
						if(hisabMap.containsKey(reverseObject)){
							float newshare = hisabMap.get(reverseObject) - object.getAmount();										
							hisabMap.put(reverseObject, Utils.round(newshare, 2));								
						}
						else if(hisabMap.containsKey(giverTakerObject)){
							float newshare = hisabMap.get(giverTakerObject) + object.getAmount();
							hisabMap.put(giverTakerObject, Utils.round(newshare, 2));
						}
						else{
							hisabMap.put(giverTakerObject, object.getAmount());
						}		
					}
				}
				
				//Refining the Final Map... Removing entries having amount as 0 since it is not needed
				Set<Entry<GiverTakerObject, Float>> entrySet = hisabMap.entrySet();
				App.getInstance().setGiverTakermap(new HashMap<GiverTakerObject, Float>());
				for(Entry<GiverTakerObject, Float> entry: entrySet){
					if(entry.getValue() != 0){
						App.getInstance().getGiverTakermap().put(entry.getKey(), entry.getValue());
					}
				}
			}catch(JSONException e){
				e.printStackTrace();
			}		
			return hisabMap;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new MyProgressDialog(context);			
			pd.show();
		}

		@Override
		protected void onPostExecute(Map<GiverTakerObject, Float> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result.size() > 0){
				System.out.println("map givertaker"+ App.getInstance().getGiverTakermap());
				hisabList.setAdapter(new HisabAdapter(App.getInstance().getGiverTakermap()));
			}else{
				Utils.showToast(context, "No calculations, as per now");
				finish();
			}
			pd.dismiss();
		}
	}
	
	public class HisabAdapter extends BaseAdapter{
		
		private List<String> hisabArray;

		public HisabAdapter(Map<GiverTakerObject, Float> map) {
			super();			
			hisabArray = new ArrayList<String>();
			Set<Entry<GiverTakerObject, Float>> entries = map.entrySet();
			for(Entry<GiverTakerObject, Float> entry:entries){
				if(entry.getValue() >= 0){
					hisabArray.add(entry.getKey().getWhoUsed()+":"+entry.getKey().getWhoSpent()+":"+entry.getValue());
				}
				else{
					float value = Math.abs(entry.getValue());
					hisabArray.add(entry.getKey().getWhoSpent()+":"+entry.getKey().getWhoUsed()+":"+value);
				}
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hisabArray.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return hisabArray.get(position);
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
			
			String hisabItem = hisabArray.get(position);		
			giverField.setText(hisabItem.split(":")[0]);
			takerField.setText(hisabItem.split(":")[1]);
			amountTextView.setText(hisabItem.split(":")[2]);
			v.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					settlementSection.setVisibility(View.VISIBLE);
					leftName = Utils.stringify(giverField.getText());
					rightName = Utils.stringify(takerField.getText());
					amount = Utils.round(Float.parseFloat(amountTextView.getText().toString()),2);
					queryString = "How much amount <b>"+leftName+"</b> give to <b>"+rightName+"</b>";
					querySentence.setText(Html.fromHtml(queryString));
					amountEntryField.setText(String.valueOf(amount));
				}
			});
			
			
			return v;
		}
		
	}
	
	public class AddSettlementRecevier extends BroadcastReceiver{

		public static final String ADDSETTLEMENTSUCCESSRECEIVER = "com.prateek.gem.views.HisabActivity.ADDSETTLEMENTSUCCESSRECEIVER";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			pd.dismiss();
			int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
			boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);
			switch (notId) {
			case AppConstants.NOT_ADDSETTLE:
				if(result){					
					Utils.showToast(context, "Added Succesfully");
					App.getInstance().getSettlements().add(addingSettlement);
					CalculateHisabAsycTask calculateHisabAsycTask = new CalculateHisabAsycTask();
					calculateHisabAsycTask.execute(new String[]{""});
					hisabList.setAdapter(new HisabAdapter(App.getInstance().getGiverTakermap()));
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
		unregisterReceiver(addSettlementReceiver);
	}

}
