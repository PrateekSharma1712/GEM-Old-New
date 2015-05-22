package com.prateek.gem.groups;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.R;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Item;
import com.prateek.gem.model.Member;
import com.prateek.gem.model.SectionHeaderObject;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.services.MyDBService;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.AddExpenseActivity;
import com.prateek.gem.views.AddMembersActivity;
import com.prateek.gem.views.ExpenseDetailActivity;
import com.prateek.gem.views.MembersActivity;
import com.prateek.gem.views.MyProgressDialog;
import com.prateek.gem.widgets.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by prateek on 4/5/15.
 */
public class MembersFragment extends Fragment {

    private Context context = null;
    ListView membersView;
    RelativeLayout noMembersView;
    MembersAdapter memberAdapter;
    int currentGroupId;
    int selectedMemberPosition;
    Intent addMembersActivityIntent;
    AsyncTask<Void, Void, Void> deleteTask; //created because concurrent modification was happening in quick deletion of member
    MyProgressDialog pd;
    IntentFilter deleteMemberIntentFilter;
    DeleteMemberRecevier deleteMemberReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_members,
                container, false);

        initUI(rootView);


        return rootView;
    }

    private void initUI(View view) {
        context = getActivity();
        membersView = (ListView) view.findViewById(R.id.members);
        membersView.setCacheColorHint(0);
        noMembersView = (RelativeLayout) view.findViewById(R.id.noMembersView);
        memberAdapter = new MembersAdapter(context, App.getInstance().getCurr_Members());
        if(memberAdapter != null && memberAdapter.getCount() != 0){
            membersView.setAdapter(memberAdapter);
            membersView.setVisibility(View.VISIBLE);
            noMembersView.setVisibility(View.GONE);
        }
        else{
            membersView.setVisibility(View.GONE);
            noMembersView.setVisibility(View.VISIBLE);
        }
        addMembersActivityIntent = new Intent(getActivity(),AddMembersActivity.class);

        deleteMemberReceiver = new DeleteMemberRecevier();
        deleteMemberIntentFilter = new IntentFilter(DeleteMemberRecevier.DELETEMEMBERSUCCESSRECEIVER);
        deleteMemberIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(deleteMemberReceiver, deleteMemberIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(deleteMemberReceiver);
    }

    public class MembersAdapter extends BaseAdapter {

        private Context _c;
        private List<Member> members;

        public MembersAdapter(Context _c, List<Member> members) {
            super();
            this._c = _c;
            this.members = members;

        }

        public class ViewHolder{
            public TextView memberNameView;
            public TextView photoNumberView;
            public ImageView editMemberButton;
            public ImageView removeMemberButton;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return members.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return members.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return members.get(position).getMemberId();
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            // TODO Auto-generated method stub
            View v = view;
            ViewHolder holder;
            if(v == null){
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.list_element_member, null);
                holder = new ViewHolder();
                holder.memberNameView = (TextView) v.findViewById(R.id.memberName);
                holder.photoNumberView = (TextView) v.findViewById(R.id.memberPhoneNumber);
                holder.editMemberButton = (ImageView) v.findViewById(R.id.button_edit_member);
                holder.removeMemberButton = (ImageView) v.findViewById(R.id.button_remove_member);
                v.setTag(holder);


            }else{
                holder=(ViewHolder)v.getTag();
            }

            Member member = members.get(position);

            holder.memberNameView.setText(member.getMemberName());
            holder.photoNumberView.setText(member.getPhoneNumber());
            holder.removeMemberButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    selectedMemberPosition = position;
                    System.out.println("Position"+selectedMemberPosition);

                    if(members.get(selectedMemberPosition).getPhoneNumber().equals(App.getInstance().getCurr_group().getAdmin())){
                        Utils.showToast(context, getString(R.string.cannotdeleteadmin));
                    }else{
                        System.out.println("MEMBER ID"+members.get(selectedMemberPosition).getMemberId());
                        System.out.println("MEMBER ID"+members.get(selectedMemberPosition).getMemberIdServer());
                        deleteMember(members.get(selectedMemberPosition).getMemberId(),members.get(selectedMemberPosition).getMemberIdServer(),selectedMemberPosition);
                    }

                }

                private void deleteMember(final int memberId, final int memberIdServer, final int selectedMemberPosition) {

                    String message = "Are you sure to delete "+members.get(selectedMemberPosition).getMemberName() + " from "+ App.getInstance().getCurr_group().getGroupName();
                    StringBuilder title = new StringBuilder(getString(R.string.deleteExpense));
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    final AlertDialog ad = builder.create();
                    builder.setMessage(message);
                    builder.setTitle(title);
                    builder.setIcon(getResources().getDrawable(R.drawable.ic_action_content_discard));
                    builder.setPositiveButton(getString(R.string.button_delete), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Member server id "+memberIdServer);
                            final List<NameValuePair> list = new ArrayList<NameValuePair>();
                            list.add(new BasicNameValuePair(DB.TMembers.MEMBER_ID, ""+memberIdServer));
                            list.add(new BasicNameValuePair("realmemberid", ""+memberId));
                            list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.DELETE_MEMBER));

                            pd = new MyProgressDialog(context,true,"Deleting member");
                            pd.show();
                            FullFlowService.ServiceDeleteMember(context,AppConstants.NOT_DELETEMEMBER, list);
                            ad.dismiss();
                        }
                    });

                    builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad.dismiss();
                        }
                    });

                    builder.show();

					/*ConfirmationDialog mcd = new ConfirmationDialog();
					Bundle bundle = new Bundle();
					bundle.putInt(ConfirmConstants.CONFIRM_KEY, ConfirmConstants.MEMBER_DELETE);
					bundle.putInt(TMembers.MEMBER_ID, members.get(selectedMemberPosition).getMemberId());
					bundle.putString(ConfirmationDialog.TITLE, getString(R.string.deleteMember));
					bundle.putString(ConfirmationDialog.BUTTON1, getString(R.string.button_delete));
					bundle.putString(ConfirmationDialog.BUTTON2, getString(R.string.button_cancel));
					bundle.putString(ConfirmationDialog.MESSAGE, "Are you sure to delete "+members.get(selectedMemberPosition).getMemberName() + " from "+
					GEMApp.getInstance().getCurr_group().getGroupName());
					mcd.setArguments(bundle);
					mcd.show(getFragmentManager(), "ComfirmationDialog");*/

                }
            });

            holder.editMemberButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }
            });
            return v;
        }

    }

    public class DeleteMemberRecevier extends BroadcastReceiver{

        public static final String DELETEMEMBERSUCCESSRECEIVER = "com.prateek.gem.views.AddExpenseActivity.DELETEMEMBERSUCCESSRECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
            pd.dismiss();
            int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
            boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);
            switch (notId) {
                case AppConstants.NOT_DELETEMEMBER:
                    if(result){
                        App.getInstance().getCurr_Members().remove(selectedMemberPosition);
                        memberAdapter.notifyDataSetChanged();
                    }else{
                        Utils.showToast(context, "Cannot Delete, Please try after some time");
                    }
                    break;

                default:
                    break;
            }
        }
    }

}
