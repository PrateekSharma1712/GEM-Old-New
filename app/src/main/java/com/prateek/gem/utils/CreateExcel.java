package com.prateek.gem.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Environment;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.AppConstants.JSONConstants;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Items;

public class CreateExcel {

	private String inputFile = null;
	private String fileName = null;
	private Context context = null;
	private int rowHeightForColumnHeaders = 22 * 20;
	private int columnWidthForColumnHeaders = 1 * 15;
	private int rowHeightForNonColumnHeaders = 18 * 20;
	
	private String mMonth = null;
	
	WritableCellFormat headerFormat, nonHeaderFormat, dateCellFormat = null;

	public CreateExcel(Context context) {
		this.context = context;
	}

	public void setOutputFile(String inputFile) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& Environment.getExternalStorageDirectory().canWrite()) {
			System.out.println("yessss");
			Utils.getFolderPath("Export", true);
			this.inputFile = Environment.getExternalStorageDirectory() + "/"
					+ AppConstants.APP_NAME + "/Export/" + inputFile;
		}
		this.fileName = inputFile;
	}

	public void write() throws IOException, WriteException {
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet("Exenses", 0);
		WritableSheet excelSheet = workbook.getSheet(0);

		createLabel(excelSheet);
		populateDate(excelSheet);

		workbook.write();
		workbook.close();
		Utils.showToast(context, "See excel file in GEM folder: \n"+fileName);
	}
	
	public class CellType {
		private static final int TYPE_DATE = 1; 
		private static final int TYPE_ITEM = 2;
		private static final int TYPE_CATEGORY = 3;
		private static final int TYPE_AMOUNT = 4;
		private static final int TYPE_SPENTBY = 5;
		private static final int TYPE_PARTICIPANTS = 6;
	}

	private void populateDate(WritableSheet sheet) throws WriteException {
		List<ExpenseOject> expenses = ExpenseOject.sortByDate(mMonth);
		for(int i=0;i<expenses.size();i++) {
			//addLabel(sheet, 0, i+1, Utils.formatShortDate(expenses.get(i).getDate().toString()), CellType.TYPE_DATE);
			addDate(sheet, 0, i+1, expenses.get(i).getDate(), CellType.TYPE_DATE);
			addLabel(sheet, 1, i+1, expenses.get(i).getItem(), CellType.TYPE_ITEM);
			addLabel(sheet, 2, i+1, Items.getCategoryOfItem(expenses.get(i).getItem()), CellType.TYPE_CATEGORY);
			addNumber(sheet, 3, i+1, (int)(expenses.get(i).getAmount()), CellType.TYPE_AMOUNT);
			addLabel(sheet, 4, i+1, expenses.get(i).getExpenseBy(), CellType.TYPE_SPENTBY);
			
			
			String participantsString = "";
			JSONArray array = null;
			try{
				array = new JSONArray(expenses.get(i).getParticipants());
				if(array.length() < App.getInstance().getCurr_Members().size()) {
					for(int j = 0;j<array.length();j++){
						participantsString += array.getJSONObject(j).getString(JSONConstants.MEMBERNAME);
						participantsString += ", ";
					}
				} 
				
			}catch(JSONException e){
				e.printStackTrace();
			}
			
			if(participantsString.length() != 0)
				addLabel(sheet, 5, i+1, participantsString.substring(0,participantsString.length()-2), CellType.TYPE_PARTICIPANTS);
			else
				addLabel(sheet, 5, i+1, participantsString, CellType.TYPE_PARTICIPANTS);
		}
		
	}

	private void createLabel(WritableSheet sheet) throws WriteException {
		// Write a few headers
		addCaption(sheet, 0, 0, "Date");
		addCaption(sheet, 1, 0, "Item");
		addCaption(sheet, 2, 0, "Category");
		addCaption(sheet, 3, 0, "Amount");
		addCaption(sheet, 4, 0, "Spent by");
		addCaption(sheet, 5, 0, "Participants");

	}

	/*private void createContent(WritableSheet sheet) throws WriteException,
			RowsExceededException {

		// Write a few number
		for (int i = 1; i < 10; i++) {
			// First column
			addNumber(sheet, 0, i, i + 10);
			// Second column
			addNumber(sheet, 1, i, i * i);
		}
		// Lets calculate the sum of it
		StringBuffer buf = new StringBuffer();
		buf.append("SUM(A2:A10)");
		Formula f = new Formula(0, 10, buf.toString());
		sheet.addCell(f);
		buf = new StringBuffer();
		buf.append("SUM(B2:B10)");
		f = new Formula(1, 10, buf.toString());
		sheet.addCell(f);

		//now a bit of text
		for (int i = 12; i < 20; i++) {
			//First column
			addLabel(sheet, 0, i, "Boring text " + i, 0);
			// Second column
			addLabel(sheet, 1, i, "Another text", 0);
		}
	}*/

	private void addCaption(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;

		WritableFont labelFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.BOLD);
		labelFont.setColour(Colour.WHITE);

		headerFormat = new WritableCellFormat();
		headerFormat.setAlignment(Alignment.CENTRE);
		headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		headerFormat.setBackground(Colour.AQUA);
		headerFormat.setWrap(true);
		headerFormat.setFont(labelFont);
		label = new Label(column, row, s.toUpperCase(Locale.ENGLISH), headerFormat);

		sheet.setRowView(row, rowHeightForColumnHeaders);
		sheet.setColumnView(column, columnWidthForColumnHeaders);
		sheet.addCell(label);
	}

	private void addNumber(WritableSheet sheet, int column, int row,
			Integer integer, int typeAmount) throws WriteException, RowsExceededException {
		jxl.write.Number number;
		setFormatForNonHeaderRows(sheet, typeAmount);
		number = new jxl.write.Number(column, row, integer, nonHeaderFormat);
		sheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s, int type)
			throws WriteException, RowsExceededException {

		Label label;

		setFormatForNonHeaderRows(sheet, type);

		label = new Label(column, row, s, nonHeaderFormat);
		sheet.setRowView(row, rowHeightForNonColumnHeaders);
		sheet.addCell(label);
	}
	
	private void addDate(WritableSheet sheet, int column, int row, Long s, int typeItem) throws WriteException {
		WritableFont labelFont = new WritableFont(WritableFont.ARIAL, 9,
				WritableFont.NO_BOLD);
		labelFont.setColour(Colour.BLACK);
		
		DateFormat format = new DateFormat("dd-MMM-yyyy");
		dateCellFormat = new WritableCellFormat(format);
		dateCellFormat.setAlignment(Alignment.CENTRE);
		dateCellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		dateCellFormat.setWrap(true);
		dateCellFormat.setFont(labelFont);
		
		WritableCell label = new DateTime(column, row, new Date(s), dateCellFormat);
		sheet.addCell(label);
		
	}

	private void setFormatForNonHeaderRows(WritableSheet sheet, int type)
			throws WriteException {
		WritableFont labelFont = new WritableFont(WritableFont.ARIAL, 9,
				WritableFont.NO_BOLD);
		labelFont.setColour(Colour.BLACK);
		
		nonHeaderFormat = new WritableCellFormat();
		nonHeaderFormat.setAlignment(Alignment.CENTRE);
		nonHeaderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		nonHeaderFormat.setWrap(true);
		nonHeaderFormat.setFont(labelFont);

	}

	public void setParam(String month) {
		mMonth = month;
		System.out.println("selected month"+mMonth);
	}
}
