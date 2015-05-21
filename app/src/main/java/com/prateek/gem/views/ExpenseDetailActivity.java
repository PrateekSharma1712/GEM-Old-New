package com.prateek.gem.views;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.prateek.gem.AppConstants;
import com.prateek.gem.R;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.widgets.ScreenContainer;
import com.prateek.gem.widgets.TagLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by prateek on 12/5/15.
 */
public class ExpenseDetailActivity extends MainActivity implements ScreenContainer{

    private TagLayout spentFor = null, participants = null;
    private TextView date = null, spentBy = null, amount = null;
    private Context context = null;
    private ExpenseOject expense = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        expense = getIntent().getExtras().getParcelable("expense");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(expense != null) {
            date = (TextView) findViewById(R.id.date);
            spentBy = (TextView) findViewById(R.id.spentBy);
            amount = (TextView) findViewById(R.id.amount);
            spentFor = (TagLayout) findViewById(R.id.spentFor);
            participants = (TagLayout) findViewById(R.id.participants);

            date.setText(Utils.formatDate(String.valueOf(expense.getDate())));
            spentBy.setText(expense.getExpenseBy());
            amount.setText(getResources().getString(R.string.inr) + " " + String.valueOf(expense.getAmount()));

            ArrayList<String> tags = new ArrayList<>(Arrays.asList(expense.getItem().split(",")));
            spentFor.setList(tags);
            spentFor.show(8, this);

            final JSONArray array;
            ArrayList<String> participantsList = new ArrayList<>();
            try{
                array = new JSONArray(expense.getParticipants());
                for(int i = 0;i<array.length();i++){
                    participantsList.add(array.getJSONObject(i).getString(AppConstants.JSONConstants.MEMBERNAME));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

            participants.setList(participantsList);
            participants.show(8, this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar("Expense Detail", R.drawable.ic_group);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_expense_detail;
    }

    @Override
    public int getWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }
}
