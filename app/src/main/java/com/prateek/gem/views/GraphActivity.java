package com.prateek.gem.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.prateek.gem.R;

public class GraphActivity extends ActionBarActivity {

	LinearLayout layoutParentView;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		initUI();
		
		for(int i = 0;i<13;i++){
			View barLayout = View.inflate(this, R.layout.bar, null);
			/*barLayout.setLayoutParams(new LayoutParams(10, 200));
			barLayout.setBackgroundColor(getResources().getColor(R.color.android_basic_darkred));*/
			layoutParentView.addView(barLayout);
		}
		/*barLayout.setLayoutParams(new LayoutParams(10, 250));
		barLayout.setBackgroundColor(getResources().getColor(R.color.android_basic_darkgreen));
		layoutParentView.addView(barLayout);*/
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initUI() {
		context = this;
		layoutParentView = (LinearLayout) findViewById(R.id.graphParentLayout);
	}
	
	/*=============
	 * Code for graph library
	 * /*
	 *
	 *
	GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();
	seriesStyle.setValueDependentColor(new ValueDependentColor() {
	  @Override
	  public int get(GraphViewDataInterface data) {
	    // the higher the more red
	    return Color.rgb((int)(150+((data.getY()/3)*100)), (int)(150-((data.getY()/3)*150)), (int)(150-((data.getY()/3)*150)));
	  }
	});
	
	List<SectionHeaderObject> expenseDateHeaders = Utils.getExpenseDateHeaders(GEMApp.getInstance().getExpensesList());
	System.out.println("Expense Date Headers"+expenseDateHeaders);
	GraphViewData[] expenseDataArray = new GraphViewData[expenseDateHeaders.size()];
	String[] staticHLabels = new String[expenseDateHeaders.size()];
	for (int i = 0; i < expenseDateHeaders.size(); i++) {
		expenseDataArray[i] = new GraphViewData(Double.parseDouble(Utils.formatShortDate1(String.valueOf(expenseDateHeaders.get(i).getHeaderTitle()))), expenseDateHeaders.get(i).getAmount());
		staticHLabels[i] = Utils.formatShortDate1(String.valueOf(expenseDateHeaders.get(i).getHeaderTitle()));
	}
	
	GraphViewSeries expenseDataArraySeries = new GraphViewSeries(expenseDataArray);
	// init example series data
	GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
	    new GraphViewData(1, 2.0d)
	    , new GraphViewData(2, 1.5d)
	    , new GraphViewData(3, 2.5d)
	    , new GraphViewData(4, 1.0d)
	});
	 
	GraphView graphView = new BarGraphView(
	    this // context
	    , "GraphViewDemo" // heading
	);
	
	
	
	
	graphView.setHorizontalLabels(staticHLabels);
	graphView.addSeries(expenseDataArraySeries); // data
	graphView.getGraphViewStyle().setGridColor(Color.GREEN);
	graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
	graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
	graphView.getGraphViewStyle().setVerticalLabelsWidth(20);
	graphView.getGraphViewStyle().setNumHorizontalLabels(5);
	graphView.getGraphViewStyle().setNumVerticalLabels(10);
	graphView.setViewPort(2, 40);
	graphView.setScrollable(true);
	layoutParentView = (LinearLayout) findViewById(R.id.graphParentLayout);
	layoutParentView.addView(graphView);*/
	
	
}
