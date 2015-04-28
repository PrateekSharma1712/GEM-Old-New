package com.prateek.gem.groups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prateek.gem.AppConstants;
import com.prateek.gem.R;

import com.prateek.gem.items.ItemsActivity;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Member;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.services.ServiceHandler;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.utils.AppSharedPreference;
import com.prateek.gem.utils.LoadingScreen;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.ExpensesActivity;
import com.prateek.gem.views.MainActivity;
import com.prateek.gem.widgets.AppDialog;
import com.prateek.gem.widgets.ConfirmationDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AddGroupScreen extends MainActivity implements AppDialog.DialogClickListener {


    private ImageView vGroupImage = null;
    Uri originalUri = null;
    private EditText vGroupName = null;
    private String[] imageCaptureOptions = new String[]{"Camera", "Gallery"};
    private String filepathFromCamera = null;
    private boolean isNew = false;
    private Intent selfIntent = null;
    private CardView vExpenseCardView = null;
    private CardView vMemberCardView = null;
    private CardView vItemCardView = null;
    private Intent mExenseScreenIntent = null, mAddExpenseScreenIntent = null;
    private Intent mMemberScreenIntent = null, mAddMemberScreenIntent = null;
    private Intent mItemScreenIntent = null;
    private MenuItem vEditGroup = null;
    private MenuItem vDeleteGroup = null;
    private boolean isEditMode = false;
    private long recordedTime;
    Group recentlyAddedGroup = null;
    ServiceHandler handler;
    int addedMemberIntoGroup;

    public enum CardViewType {
        EXPENSE, MEMBER, ITEM
    }

    private static final String GROUP_NAME = "group_name";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_group_screen;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        base = this;

        vGroupName = (EditText) findViewById(R.id.vGroupName);
        ViewCompat.setTransitionName(vGroupName, GROUP_NAME);
        vExpenseCardView = (CardView) findViewById(R.id.vExpenseBrief);
        vMemberCardView = (CardView) findViewById(R.id.vMembersBrief);
        vItemCardView = (CardView) findViewById(R.id.vItemsBrief);
        vExpenseCardView.startAnimation(Utils.inFromBottomAnimation(600));
        vMemberCardView.startAnimation(Utils.inFromBottomAnimation(600));
        vItemCardView.startAnimation(Utils.inFromBottomAnimation(600));

        selfIntent = getIntent();
        if(selfIntent != null) {
            isNew = selfIntent.getBooleanExtra(AppConstants.GROUP_SOURCE, Boolean.FALSE);
            DebugLogger.message("isNew" + isNew);
            if (!isNew) {
                recentlyAddedGroup = selfIntent.getParcelableExtra(AppConstants.GROUP);
                if(recentlyAddedGroup == null) {
                    recentlyAddedGroup = AppDataManager.getCurrentGroup();
                }
                vGroupName.setText(recentlyAddedGroup.getGroupName());
                vGroupName.setHint(R.string.groups_edit_groupname);
            }
        }

        if(isNew) {
            isEditMode = true;
            vExpenseCardView.setVisibility(View.GONE);
            vMemberCardView.setVisibility(View.GONE);
            vItemCardView.setVisibility(View.GONE);
        } else {
            /*updateExpenseView();
            updateMemberView();
            updateItemView();*/
        }


        vGroupImage = (ImageView) findViewById(R.id.vGroupIcon);
        vGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordedTime = Utils.getCurrentTimeInMilliSecs();
                //AppDialog.init(AppDataManager.currentScreen, getString(R.string.takephoto), null, imageCaptureOptions);
                ConfirmationDialog mcd = new ConfirmationDialog();
                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.takephoto));
                bundle.putInt(AppConstants.ConfirmConstants.CONFIRM_KEY, AppConstants.ConfirmConstants.IMAGE_CAPTURE);
                bundle.putStringArrayList(ConfirmationDialog.LIST, new ArrayList<String>(Arrays.asList(imageCaptureOptions)));
                mcd.setArguments(bundle);
                mcd.show(getSupportFragmentManager(), "ComfirmationDialog");
            }
        });

        vExpenseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExenseScreenIntent = new Intent(AddGroupScreen.this, ExpensesActivity.class);
                startActivity(mExenseScreenIntent);
            }
        });

        toggleEditMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugLogger.message("toolbar" + mToolBar);
        if(isNew) {
            setToolbar("Add Group", R.drawable.ic_group);
        } else {
            if(AppDataManager.getCurrentGroup() != null) {
                setToolbar(AppDataManager.getCurrentGroup().getGroupName(), R.drawable.ic_group);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_group_screen, menu);
        vEditGroup = menu.findItem(R.id.editGroup);
        vDeleteGroup = menu.findItem(R.id.deleteGroup);

        if(isNew) {
            isEditMode = true;
            vEditGroup.setIcon(R.drawable.ic_action_navigation_accept_white);
            vDeleteGroup.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.editGroup: {
                if (isEditMode) {
                    Utils.hideKeyboard(vGroupName);

                    //save/update group
                    saveGroup();
                } else {
                    isEditMode = true;
                    vEditGroup.setIcon(R.drawable.ic_action_navigation_accept_white);
                    //make image, text field enabled
                }
                toggleEditMode();
            }
            case R.id.deleteGroup:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGroup() {
        if(!Utils.stringify(vGroupName.getText()).isEmpty()){
            if(!Group.contains(AppDataManager.getGroups(), Utils.stringify(vGroupName.getText()))){
                ConfirmationDialog mcd = new ConfirmationDialog();
                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.addGroup));
                bundle.putInt(AppConstants.ConfirmConstants.CONFIRM_KEY, AppConstants.ConfirmConstants.FROM_ADD_GROUP);
                bundle.putString(ConfirmationDialog.BUTTON1, getString(R.string.button_cancel));
                bundle.putString(ConfirmationDialog.BUTTON2, getString(R.string.button_add));
                if(isNew)
                    bundle.putString(ConfirmationDialog.MESSAGE, "Are you sure to add "+Utils.stringify(vGroupName.getText())+"?");
                else
                    bundle.putString(ConfirmationDialog.MESSAGE, "Are you sure to edit "+Utils.stringify(vGroupName.getText())+"?");
                mcd.setArguments(bundle);
                mcd.show(getSupportFragmentManager(), "ComfirmationDialog");
            }else{
                Utils.showError(vGroupName,this,getString(R.string.existing_group));
            }
        }
        else{
            Utils.showError(vGroupName, this);
        }
    }

    /**
     *
     * method to toggle visibility in case of edit mode and read mode
     */
    private void toggleEditMode() {
        if(isEditMode) {
            vGroupName.setEnabled(true);
        } else {
            vGroupName.setEnabled(false);
        }
    }

    @Override
    public void onItemClick(int position) {
        DebugLogger.message(imageCaptureOptions[position] + " used for taking photo");
        if(isEditMode) {
            switch (position) {
                case 0: //Camera
                    Intent getCameraImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String appName = AppDataManager.currentScreen.getResources().getString(R.string.app_name);

                    File cameraFolder;
                    if (Utils.isSDPresent())
                        cameraFolder = new File(Environment.getExternalStorageDirectory(), appName + "/");
                    else
                        cameraFolder = AppDataManager.currentScreen.getCacheDir();

                    if (!cameraFolder.exists())
                        cameraFolder.mkdirs();

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        DebugLogger.message("error creating file" + ex.getMessage());
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        getCameraImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                        if (getCameraImageIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(getCameraImageIntent, AppConstants.REQUEST_CODE_CLICK_IMAGE);
                        }
                    }
                    break;
                case 1: //Gallery
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_FROM_GALLERY);
                    break;
            }
        } else {
            Utils.showToast(this, "Click on edit button to edit");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        filepathFromCamera = "file:" + image.getAbsolutePath();
        return image;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLogger.message("AddGroupScreen :: onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == AppConstants.REQUEST_CODE_FROM_GALLERY) {
            if(data != null) {
                originalUri = data.getData();
                String path = Utils.getPath(AppDataManager.currentScreen, originalUri);
                Drawable d = Drawable.createFromPath(path);
                vGroupImage.setImageDrawable(d);
            }
        } else if(requestCode == AppConstants.REQUEST_CODE_CLICK_IMAGE) {
            DebugLogger.message("filepathFromCamera" + filepathFromCamera);
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            vGroupImage.setImageBitmap(imageBitmap);*/
            //setPic();
            Drawable d = Drawable.createFromPath(filepathFromCamera);
            DebugLogger.message("drawable" + d);
            vGroupImage.setImageDrawable(d);
        }

    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = vGroupImage.getWidth();
        int targetH = vGroupImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepathFromCamera, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(filepathFromCamera, bmOptions);
        vGroupImage.setImageBitmap(bitmap);

    }

    @Override
    public void modeConfirmed() {
        DebugLogger.message("Confirmed");
        new AddGroupTask().execute((Void)null);
        isEditMode = false;
        vEditGroup.setIcon(R.drawable.ic_action_content_edit);
    }

    public class AddGroupTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadingScreen.showLoading(base, "Adding Group " + Utils.stringify(vGroupName.getText()));
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Long d = new Date().getTime();
            if(originalUri == null){
                String defaultPath = "0";
                originalUri = Uri.parse(defaultPath);
            }
            System.out.println("asdasda"+originalUri.getPath());
            int i = Utils.uploadFile(originalUri.getPath());
            System.out.println(i);
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair(DB.TGroups.GROUPNAME,Utils.stringify(vGroupName.getText())));
            list.add(new BasicNameValuePair(DB.TGroups.DATEOFCREATION,d.toString()));
            if(recordedTime == 0){
                list.add(new BasicNameValuePair(DB.TGroups.GROUPICON,recordedTime+""));
            }else{
                list.add(new BasicNameValuePair(DB.TGroups.GROUPICON,recordedTime+".jpg"));
            }
            list.add(new BasicNameValuePair("ADMIN",""+ AppSharedPreference.getPreferenceString(AppConstants.ADMIN_PHONE)));
            list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.ADD_GROUP));
            handler = new ServiceHandler();
            String json = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
            JSONObject object = null;
            try{
                object = new JSONObject(json);
                if(!object.getBoolean("error")){
                    recentlyAddedGroup = new Group();
                    recentlyAddedGroup.setGroupIdServer(object.getInt(DB.TGroups.GROUPID));
                    recentlyAddedGroup.setGroupName(object.getString(DB.TGroups.GROUPNAME));
                    recentlyAddedGroup.setGroupIcon(object.getString(DB.TGroups.GROUPICON));
                    recentlyAddedGroup.setDate(object.getString(DB.TGroups.DATEOFCREATION));
                    recentlyAddedGroup.setMembersCount(object.getInt(DB.TGroups.TOTALMEMBERS));
                    recentlyAddedGroup.setTotalOfExpense((float) object.getDouble(DB.TGroups.TOTALOFEXPENSE));
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
            super.onPostExecute(result);
            LoadingScreen.updateIndicatorMessage("Adding Group Owner");
            if(result > 0){
                if(isNew) {
                    new AddMemberTask().execute(new Integer[]{result});
                } else {
                    Utils.showToast(base, "Updated Successfully");
                    LoadingScreen.dismissProgressDialog();
                    finish();
                }
            }
        }
    }

    public class AddMemberTask extends AsyncTask<Integer, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... params) {
            DebugLogger.message("AddMemberTask::doInBackground");
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair(DB.TMembers.GROUP_ID_FK,""+params[0]));
            list.add(new BasicNameValuePair(DB.TMembers.NAME,AppSharedPreference.getPreferenceString(AppConstants.ADMIN_NAME)));
            list.add(new BasicNameValuePair(DB.TMembers.PHONE_NUMBER,AppSharedPreference.getPreferenceString(AppConstants.ADMIN_PHONE)));
            list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.ADD_MEMBER));
            String json = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
            JSONObject object = null;
            try{
                DebugLogger.message("in try");

                object = new JSONObject(json);
                if(!object.getBoolean("error")){
                    DebugLogger.message("no error");
                    addedMemberIntoGroup = object.getInt("id");
                    return true;
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            DebugLogger.message("after try");
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DebugLogger.message("result" + result);
            if(result){
                DebugLogger.message("result is true");
                recentlyAddedGroup.setMembersCount(1);

                long rowId = DBImpl.addGroup(recentlyAddedGroup);
                recentlyAddedGroup.setGroupId((int) rowId);

                DBImpl.addAdminToGroup(addedMemberIntoGroup, recentlyAddedGroup.getGroupIdServer());
                DebugLogger.message("dismissProgressDialog");
                LoadingScreen.dismissProgressDialog();
                finish();

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isNew && recentlyAddedGroup != null)
            DBImpl.updateLastUpdated(recentlyAddedGroup.getGroupIdServer(), Utils.getCurrentTimeInMilliSecs());
    }

    /*private void updateMemberView() {
        ArrayList<Member> members = DBImpl.getMembers(recentlyAddedGroup.getGroupIdServer());
        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutMembersView);
        View view = LayoutInflater.from(this).inflate(R.layout.member_mini_row, null, false);
        TextView memberName = (TextView) view.findViewById(R.id.memberName);
        TextView memberPhoneNumber = (TextView) view.findViewById(R.id.memberPhoneNumber);

        memberName.setText(members.get(0).getMemberName());
        memberPhoneNumber.setText(members.get(0).getPhoneNumber());
        layout.addView(view);

        if(members.size() > 0) {
            addMoreView(getString(R.string.viewall), getString(R.string.more), CardViewType.MEMBER, layout);
        } else {
            addSingleMoreView("No Members, " + getString(R.string.taptoadd), CardViewType.MEMBER, layout);
        }
    }

    private void updateExpenseView() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutExpenseView);
        addSingleMoreView("No Expenses, " + getString(R.string.taptoadd), CardViewType.EXPENSE, layout);
    }

    private void addSingleMoreView(String text, final CardViewType cardType, LinearLayout layout) {
        View view = LayoutInflater.from(this).inflate(R.layout.singlemore, null, false);
        TextView viewLabel = (TextView) view.findViewById(R.id.viewLabel);
        viewLabel.setText(text);

        viewLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardType == CardViewType.MEMBER) {
                    mMemberScreenIntent = new Intent(AddGroupScreen.this, MembersActivity.class);
                    startActivity(mMemberScreenIntent);
                } else if(cardType == CardViewType.EXPENSE) {
                    mExenseScreenIntent = new Intent(AddGroupScreen.this, AddExpenseActivity.class);
                    startActivity(mExenseScreenIntent);
                } else if(cardType == CardViewType.ITEM) {
                    mItemScreenIntent = new Intent(AddGroupScreen.this, ItemsActivity.class);
                    startActivity(mItemScreenIntent);
                }
            }
        });
        layout.addView(view);
    }

    private void addMoreView(String textLeft, String textRight, final CardViewType cardType, View layout) {
        View view = LayoutInflater.from(this).inflate(R.layout.more, null, false);
        LinearLayout.LayoutParams singleViewParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        TextView viewLabel = (TextView) view.findViewById(R.id.viewLabel);
        TextView moreLabel = (TextView) view.findViewById(R.id.moreLabel);

        viewLabel.setText(textLeft);
        moreLabel.setText(textRight);

        viewLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardType == CardViewType.MEMBER) {
                    mMemberScreenIntent = new Intent(AddGroupScreen.this, MembersActivity.class);
                    startActivity(mMemberScreenIntent);
                } else if(cardType == CardViewType.EXPENSE) {
                    mExenseScreenIntent = new Intent(AddGroupScreen.this, ExpensesActivity.class);
                    startActivity(mExenseScreenIntent);
                }
            }
        });

        moreLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardType == CardViewType.MEMBER) {
                    mAddMemberScreenIntent = new Intent(AddGroupScreen.this, AddMembersActivity.class);
                    startActivity(mAddMemberScreenIntent);
                } else if(cardType == CardViewType.EXPENSE) {
                    mAddExpenseScreenIntent = new Intent(AddGroupScreen.this, AddExpenseActivity.class);
                    startActivity(mAddExpenseScreenIntent);
                }
            }
        });


        ((LinearLayout) layout).addView(view);

    }*/

    /*private void updateItemView() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutItemsView);
        addSingleMoreView("No Items, " + getString(R.string.taptoadd), CardViewType.ITEM, layout);
    }*/

    public static void launch(MainActivity activity, View transitionView, Bundle bundle) {
        AppDataManager.setCurrentGroup((Group) bundle.getParcelable(AppConstants.GROUP));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, GROUP_NAME);
        Intent intent = new Intent(activity, AddGroupScreen.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(activity, intent, options.toBundle());

    }
}
