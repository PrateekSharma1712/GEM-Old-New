package com.prateek.gem.model;

import java.util.Comparator;

public class DateComparator implements Comparator<ExpenseOject>{

	@Override
	public int compare(ExpenseOject one, ExpenseOject two) {
		System.out.println("one :: "+one.getDate());
		System.out.println("two :: "+two.getDate());
		return one.getDate().compareTo(two.getDate());
	}
	
}
