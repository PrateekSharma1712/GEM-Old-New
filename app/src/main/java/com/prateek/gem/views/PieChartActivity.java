package com.prateek.gem.views;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.animation.AnimationEasing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.DefaultValueFormatter;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.prateek.gem.App;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Member;
import com.prateek.gem.persistence.DBImpl;

import java.util.ArrayList;

/**
 * Created by prateek on 28/4/15.
 */
public class PieChartActivity extends MainActivity {

    private PieChart pieChart = null;
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    private ArrayList<Float> contributions = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_pie_chart;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(false);
        pieChart.setDescription(App.getInstance().getCurr_group().getGroupName());
        pieChart.setHoleRadius(50f);
        pieChart.setCenterTextSize(14f);
        pieChart.setDrawCenterText(true);
        float groupExpenseTotal = DBImpl.getExpenseTotal(App.getInstance().getCurr_group().getGroupIdServer());
        pieChart.setCenterText("Contributions\n"+ groupExpenseTotal);


        members = DBImpl.getMembers(App.getInstance().getCurr_group().getGroupIdServer());
        for(int i = 0;i< members.size();i++) {
            contributions.add(DBImpl.getExpenseTotalByMember(App.getInstance().getCurr_group().getGroupIdServer(), members.get(i).getMemberName()));
        }

        DebugLogger.message(contributions);

        setData(members.size(), groupExpenseTotal);

        pieChart.animateY(1500, AnimationEasing.EasingOption.EaseInOutQuad);

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
    }

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < count; i++) {
            xVals.add(members.get(i).getMemberName());
            yVals1.add(new Entry(contributions.get(i), i));
        }


        PieDataSet dataSet = new PieDataSet(yVals1, "Members");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        /*for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);*/

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);



        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return getString(R.string.inr)+" " + value;
            }
        });
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(tf);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }
}
