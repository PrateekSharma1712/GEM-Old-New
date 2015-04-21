package com.prateek.gem.personal.expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFilters {

	private static List<String> categoryFilter = new ArrayList<String>();

	private static List<String> monthFilter = new ArrayList<String>();

	public static List<String> getCategoryFilter() {
		return categoryFilter;
	}

	public static List<String> getMonthFilter() {
		return monthFilter;
	}

	public static void addCategory(String category) {
		categoryFilter.add(category);
	}

	public static void addMonths(String month) {
		monthFilter.add(month);
	}

	public static void clearFilters() {
		categoryFilter.clear();
		monthFilter.clear();
	}
}
