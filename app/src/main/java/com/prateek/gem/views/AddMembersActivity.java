package com.prateek.gem.views;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.R;
import com.prateek.gem.model.Member;
import com.prateek.gem.services.ServiceHandler;
import com.prateek.gem.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddMembersActivity extends ActionBarActivity {

	List<Member> phoneContacts;
	List<Member> selectedMembers;
	List<Member> existingMembers;
	List<String> displayNames;
	ListView phoneConListView;
	int position;	
	int currentGroupId;	
	MyProgressDialog pd;
	private ServiceHandler handler;
	private Context context;
	IntentFilter addMemberIntentFilter;
	AddMemberRecevier addMemberReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_members);		
		initUI();
		PopulatePhoneContacts populatePhoneContacts = new PopulatePhoneContacts();
		populatePhoneContacts.execute(new String[]{""});
		
		addMemberReceiver = new AddMemberRecevier();
        addMemberIntentFilter = new IntentFilter(AddMemberRecevier.ADDMEMBERSUCCESSRECEIVER);
        addMemberIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(addMemberReceiver, addMemberIntentFilter);
	}

	private void initUI() {
		context = this;
		phoneConListView = (ListView) findViewById(R.id.phoneContacts);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(addMemberReceiver);
	}



	public class PopulatePhoneContacts extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			getPhoneContacts();
			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new MyProgressDialog(context);
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			fillContacts();
			pd.dismiss();
		}

		private void fillContacts() {			
			phoneConListView.setCacheColorHint(0);
			phoneConListView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, displayNames));
		}
	}
	
	public void getPhoneContacts(){
    	ContentResolver contResv = getContentResolver();
        Cursor cursor = contResv.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst())
        {
            phoneContacts = new ArrayList<Member>();
            displayNames = new ArrayList<String>();
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = contResv.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext()) 
                    {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        Member member = new Member();
                        member.setMemberName(contactName);
                        member.setPhoneNumber(Utils.correctNumber(contactNumber));
                        displayNames.add(contactName);
                        phoneContacts.add(member);
                        Collections.sort(phoneContacts, Member.memberComparator);
                        Collections.sort(displayNames);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext()) ;
        }        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_members, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_saveMember) {
			getCheckedContacts();
			existingMembers = App.getInstance().getCurr_Members();
			List<Member> temp = selectedMembers;
			System.out.println("dfdsfsd"+temp);
			for(int i = 0;i<existingMembers.size();i++){
				for(int j = 0;j<selectedMembers.size();j++){
					if(selectedMembers.get(j).getMemberName().equals(existingMembers.get(i).getMemberName())){					
						temp.remove(selectedMembers.get(j));
						break;
					}					
				}
			}
			selectedMembers = temp;			
			
			if(selectedMembers.size()>0){
				pd = new MyProgressDialog(context, true, "Adding Members");
				pd.show();
				FullFlowService.ServiceAddMembers(context,AppConstants.NOT_ADDMEMBERS,selectedMembers, App.getInstance().getCurr_group().getGroupIdServer(), App.getInstance().getCurr_group().getGroupId());
			}else{
				Utils.showToast(context, "Member(s) already present");
			}	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void getCheckedContacts() {
		SparseBooleanArray checkedItemPositions = phoneConListView.getCheckedItemPositions();
		selectedMembers = new ArrayList<Member>();
		for(int i = 0;i<checkedItemPositions.size();i++){
			int position = checkedItemPositions.keyAt(i);
			if(checkedItemPositions.valueAt(i)){
				putMembers(position);
			}
		}
	}
	
	public void putMembers(int position) {
		selectedMembers.add(phoneContacts.get(position));
	}
	
	public class AddMemberRecevier extends BroadcastReceiver{

		public static final String ADDMEMBERSUCCESSRECEIVER = "com.prateek.gem.views.AddExpenseActivity.ADDMEMBERSUCCESSRECEIVER";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			pd.dismiss();
			int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
			boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);
			switch (notId) {
			case AppConstants.NOT_ADDMEMBERS:
				if(result){					
					Utils.showToast(context, "Added Succesfully");
					finish();
				}else{
					Utils.showToast(context, "Cannot add members, Please try after some time");	
				}
				break;
			
			default:
				break;
			}
		}		
	}

}
