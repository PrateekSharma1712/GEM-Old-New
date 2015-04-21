package com.prateek.gem;

import com.prateek.gem.persistence.DBAdapter;
import com.prateek.gem.services.MyDBService;
import com.prateek.gem.views.ExpensesActivity.SyncSuccessReceiver;

import android.app.IntentService;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SyncService extends IntentService {

	Intent broadcastIntent;
	String adminPhoneNumber;
	DBAdapter db;
	
	public SyncService() {
		super("SyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		broadcastIntent = new Intent();
		broadcastIntent.setAction(SyncSuccessReceiver.SUCCESS_RECEIVER);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra("done", true);
		
		db = new DBAdapter(getApplicationContext());
		db.open();
		
		FirstTimeLoadService loadingService = new FirstTimeLoadService();
		loadingService.insertMembers(App.getInstance().getCurr_group().getGroupIdServer(), db);
		loadingService.insertExpenses(App.getInstance().getCurr_group().getGroupIdServer(), db);
		loadingService.insertItems(App.getInstance().getCurr_group().getGroupIdServer(), db);
		loadingService.insertSettlements(App.getInstance().getCurr_group().getGroupIdServer(), db);
		
		db.close();
		
		db.open();
		MyDBService dbService = new MyDBService();
		dbService.getMembers(this, App.getInstance().getCurr_group().getGroupIdServer());
		dbService.getExpenses(this, App.getInstance().getCurr_group().getGroupIdServer());
		dbService.getItems(this, App.getInstance().getCurr_group().getGroupIdServer());
		dbService.getSettlements(this, App.getInstance().getCurr_group().getGroupIdServer());
		db.close();
		
		sendBroadcast(broadcastIntent);
		
	}
}
