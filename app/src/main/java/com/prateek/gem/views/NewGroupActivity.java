package com.prateek.gem.views;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.AppConstants.ConfirmConstants;
import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.OnModeConfirmListener;
import com.prateek.gem.R;
import com.prateek.gem.model.Group;
import com.prateek.gem.persistence.DBAdapter;
import com.prateek.gem.persistence.DB.TGroups;
import com.prateek.gem.persistence.DB.TMembers;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.services.ServiceHandler;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.widgets.ConfirmationDialog;

public class NewGroupActivity extends MainActivity implements OnClickListener,OnModeConfirmListener{

	Context context;
	Button addGroupButton;
	ImageView groupIcon;
	private EditText newGroupName;
	Uri groupIconUri;
	private long recordedTime;
	public MyProgressDialog pd,pd1,pd2;
	ServiceHandler handler;
	Group recentlyAddedGroup;
	int addedMemberIntoGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_group;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar("Add Group", R.drawable.ic_group);
    }

    @Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.addGroupButton:
			System.out.println("pressed");
			if(!Utils.stringify(newGroupName.getText()).isEmpty()){
				if(!Group.contains(App.getInstance().getAllGroups(),Utils.stringify(newGroupName.getText()))){
					ConfirmationDialog mcd = new ConfirmationDialog();
					Bundle bundle = new Bundle();
					bundle.putString(ConfirmationDialog.TITLE, getString(R.string.addGroup));
					bundle.putInt(ConfirmConstants.CONFIRM_KEY, ConfirmConstants.FROM_ADD_GROUP);
					bundle.putString(ConfirmationDialog.BUTTON1, getString(R.string.button_add));
					bundle.putString(ConfirmationDialog.BUTTON2, getString(R.string.button_cancel));
					bundle.putString(ConfirmationDialog.MESSAGE, "Are you sure to add "+Utils.stringify(newGroupName.getText())+"?");
					mcd.setArguments(bundle);
					mcd.show(getSupportFragmentManager(), "ComfirmationDialog");
				}else{
					Utils.showError(newGroupName,(FragmentActivity) context,getString(R.string.existing_group));
				}
			}
			else{
				Utils.showError(newGroupName,(FragmentActivity) context);						
			}
			break;
		case R.id.groupIcon:
			groupIconUri = Utils.getFolderPath("Groups", true);			
			System.out.println("SCHEME"+groupIconUri.getScheme());
			System.out.println("PATH"+groupIconUri.getPath());
			System.out.println("TIME"+Utils.getCurrentTimeInMilliSecs());
			recordedTime = Utils.getCurrentTimeInMilliSecs();
			String imgFilepath = groupIconUri.getScheme()+"://"+groupIconUri.getPath()+"/"+recordedTime+".jpg";
			System.out.println("ImG FILE PATh"+imgFilepath);
			groupIconUri = Uri.parse(imgFilepath);
			GridDialog mcd = new GridDialog();
			Bundle bundle = new Bundle();
			bundle.putString("Message", "");
			bundle.putString("Title", "Select");
			mcd.setArguments(bundle);
			mcd.show(getSupportFragmentManager(), "GridDialog");
			
			break;
		default:
			if(Utils.deleteFile(groupIconUri.getPath())){
				System.out.println("File deleted successfully");
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if(resultCode == Activity.RESULT_OK )//&& resultCode == RESULT_OK)
		{			
			switch(requestCode){
			case AppConstants.REQUEST_CODE_FROM_GALLERY:				
				new ImageCompressionAsyncTask(true).execute(data.getDataString());
				
				break;
			case AppConstants.REQUEST_CODE_CLICK_IMAGE:
				new ImageCompressionAsyncTask(false).execute(data.getDataString());
				break;
			}
		}
	}
	
	public void openNewActivity(int requestCodeClickImage) {
		switch(requestCodeClickImage){
		case AppConstants.REQUEST_CODE_CLICK_IMAGE:
			//Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
			startActivityForResult(cameraIntent, AppConstants.REQUEST_CODE_CLICK_IMAGE);
			break;
		case AppConstants.REQUEST_CODE_FROM_GALLERY:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, AppConstants.REQUEST_CODE_FROM_GALLERY);
			break;
		}
	}	
	
	private void initUI() {
		context = this;
		addGroupButton = (Button) findViewById(R.id.addGroupButton);
		groupIcon = (ImageView) findViewById(R.id.groupIcon);	
		groupIcon.setOnClickListener(this);		
		addGroupButton.setOnClickListener(this);		
		newGroupName = (EditText) findViewById(R.id.newGroupName);
	}
	
	class ImageCompressionAsyncTask extends AsyncTask<String, Void, String>{
		private boolean fromGallery;
		
		public ImageCompressionAsyncTask(boolean fromGallery){
			this.fromGallery = fromGallery;
		}

		@Override
		protected String doInBackground(String... params) {
			System.out.println("PARAMS"+params[0]);
			String filePath = compressImage(params[0]);
			return filePath;
		}
		
		public String compressImage(String imageUri) {
			System.out.println("Image uri"+imageUri);
			String filePath = getRealPathFromURI(imageUri);
			System.out.println("Real file path"+filePath);
			Bitmap scaledBitmap = null;
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;						
			Bitmap bmp = BitmapFactory.decodeFile(filePath,options);
			
			int actualHeight = options.outHeight;
			int actualWidth = options.outWidth;
			float maxHeight = 816.0f;
			float maxWidth = 612.0f;
			float imgRatio = actualWidth / actualHeight;
			float maxRatio = maxWidth / maxHeight;

			if (actualHeight > maxHeight || actualWidth > maxWidth) {
				if (imgRatio < maxRatio) {
					imgRatio = maxHeight / actualHeight;
					actualWidth = (int) (imgRatio * actualWidth);
					actualHeight = (int) maxHeight;
				} else if (imgRatio > maxRatio) {
					imgRatio = maxWidth / actualWidth;
					actualHeight = (int) (imgRatio * actualHeight);
					actualWidth = (int) maxWidth;
				} else {
					actualHeight = (int) maxHeight;
					actualWidth = (int) maxWidth;     
					
				}
			}
					
			options.inSampleSize = Utils.calculateInSampleSize(options, actualWidth, actualHeight);
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inTempStorage = new byte[16*1024];
				
			try{	
				bmp = BitmapFactory.decodeFile(filePath,options);
			}
			catch(OutOfMemoryError exception){
				exception.printStackTrace();
				
			}
			try{
				scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
			}
			catch(OutOfMemoryError exception){
				exception.printStackTrace();
			}
							
			float ratioX = actualWidth / (float) options.outWidth;
			float ratioY = actualHeight / (float)options.outHeight;
			float middleX = actualWidth / 2.0f;
			float middleY = actualHeight / 2.0f;
				
			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

			Canvas canvas = new Canvas(scaledBitmap);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

							
			ExifInterface exif;
			try {
				exif = new ExifInterface(filePath);
			
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
				Log.d("EXIF", "Exif: " + orientation);
				Matrix matrix = new Matrix();
				if (orientation == 6) {
					matrix.postRotate(90);
					Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 3) {
					matrix.postRotate(180);
					Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 8) {
					matrix.postRotate(270);
					Log.d("EXIF", "Exif: " + orientation);
				}
				scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileOutputStream out = null;
			
			try {
				out = new FileOutputStream(groupIconUri.getPath());
				scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			return groupIconUri.getPath();

		}
		
		private String getRealPathFromURI(String contentURI) {
			System.out.println("contentURI"+contentURI);
			Uri contentUri = Uri.parse(contentURI);
			Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
			System.out.println("Cursor"+cursor.getCount());
			if (cursor == null) {
				return contentUri.getPath();
			} else {
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				return cursor.getString(idx);
			}
		}

		@Override
		protected void onPostExecute(String result) {			 
			super.onPostExecute(result);
				System.out.println("Result"+result);
				if(groupIconUri != null){
					BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromPath(groupIconUri.getPath());
					Bitmap icon = bitmapDrawable.getBitmap();					
					groupIcon.setImageBitmap(icon);				
					groupIcon.setScaleType(ScaleType.FIT_XY);
				}
		}
		
	}

	@Override
	public void modeConfirmed() {
		new AddGroupTask().execute((Void)null);
	}
	
	public class AddGroupTask extends AsyncTask<Void, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd1 = new MyProgressDialog(context,true,"Adding Group "+Utils.stringify(newGroupName.getText()));
			pd1.show();
		}
		
		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Long d = new Date().getTime();		
			if(groupIconUri == null){
				String defaultPath = "0";
				groupIconUri = Uri.parse(defaultPath);
			}
			System.out.println("asdasda"+groupIconUri.getPath());			
			int i = Utils.uploadFile(groupIconUri.getPath());
			System.out.println(i);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair(TGroups.GROUPNAME,Utils.stringify(newGroupName.getText())));
			list.add(new BasicNameValuePair(TGroups.DATEOFCREATION,d.toString()));
			if(recordedTime == 0){
				list.add(new BasicNameValuePair(TGroups.GROUPICON,recordedTime+""));
			}else{
				list.add(new BasicNameValuePair(TGroups.GROUPICON,recordedTime+".jpg"));
			}		
			list.add(new BasicNameValuePair("ADMIN",""+ App.getInstance().getAdmin().getPhoneNumber()));
			list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.ADD_GROUP));
			handler = new ServiceHandler();
			String json = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
			JSONObject object = null;
			try{
				object = new JSONObject(json);
				if(!object.getBoolean("error")){
					recentlyAddedGroup = new Group();
					recentlyAddedGroup.setGroupIdServer(object.getInt(TGroups.GROUPID));
					recentlyAddedGroup.setGroupName(object.getString(TGroups.GROUPNAME));
					recentlyAddedGroup.setGroupIcon(object.getString(TGroups.GROUPICON));
					recentlyAddedGroup.setDate(object.getString(TGroups.DATEOFCREATION));
					recentlyAddedGroup.setMembersCount(object.getInt(TGroups.TOTALMEMBERS));
					recentlyAddedGroup.setTotalOfExpense((float) object.getDouble(TGroups.TOTALOFEXPENSE));
					recentlyAddedGroup.setAdmin(object.getString("admin"));
					return recentlyAddedGroup.getGroupIdServer();
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd1.dismiss();
			if(result > 0){
				new AddMemberTask().execute(new Integer[]{result});				
			}
		}
	}
	
	public class AddMemberTask extends AsyncTask<Integer, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd2 = new MyProgressDialog(context,true,"Adding Admin");
			pd2.show();
		}
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair(TMembers.GROUP_ID_FK,""+params[0]));
			list.add(new BasicNameValuePair(TMembers.PHONE_NUMBER, App.getInstance().getAdmin().getPhoneNumber()));
			list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.ADD_MEMBER));
			String json = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
			JSONObject object = null;
			try{
				object = new JSONObject(json);
				if(!object.getBoolean("error")){
					addedMemberIntoGroup = object.getInt("id");
					return true;
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd2.dismiss();
			if(result){
				recentlyAddedGroup.setMembersCount(1);				

                ContentValues cv = new ContentValues();
				cv.put(TGroups.GROUPID_SERVER, recentlyAddedGroup.getGroupIdServer());				
				cv.put(TGroups.GROUPNAME, recentlyAddedGroup.getGroupName());
				cv.put(TGroups.GROUPICON, recentlyAddedGroup.getGroupIcon().toString());
				cv.put(TGroups.DATEOFCREATION, recentlyAddedGroup.getDate());
				cv.put(TGroups.TOTALMEMBERS, recentlyAddedGroup.getMembersCount());
				cv.put(TGroups.TOTALOFEXPENSE, recentlyAddedGroup.getTotalOfExpense());
				cv.put(TGroups.ADMIN, recentlyAddedGroup.getAdmin());
				long rowId = DBImpl.insert(TGroups.TGROUPS, cv);
				recentlyAddedGroup.setGroupId((int) rowId);

				cv = new ContentValues();
				cv.put(TMembers.MEMBER_ID_SERVER, addedMemberIntoGroup);
				cv.put(TMembers.NAME, App.getInstance().getAdmin().getUserName());
				cv.put(TMembers.PHONE_NUMBER, App.getInstance().getAdmin().getPhoneNumber());
				cv.put(TMembers.GROUP_ID_FK, recentlyAddedGroup.getGroupIdServer());
				//System.out.println("Content Values"+cv.toString());
				DBImpl.insert(TMembers.TMEMBERS, cv);

				App.getInstance().getAllGroups().add(recentlyAddedGroup);
				finish();
			}
		}
	}

}
