package com.prateek.gem.model;

import java.util.Calendar;

public class SectionHeaderObject implements Item,
		Comparable<SectionHeaderObject> {

	private Long headerTitle;
	private float amount;
	private Calendar calendar;

	public SectionHeaderObject(Long headerTitle) {
		super();
		this.headerTitle = headerTitle;
	}

	public SectionHeaderObject() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSection() {
		// TODO Auto-generated method stub
		return false;
	}

	public Long getHeaderTitle() {
		return headerTitle;
	}

	public void setHeaderTitle(Long headerTitle) {		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(headerTitle);
		// removing milliseconds for hour, minutes, seconds. Keeping only day, month and year
		Calendar new_c = Calendar.getInstance();
		new_c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
		new_c.set(Calendar.MONTH, c.get(Calendar.MONTH));
		new_c.set(Calendar.YEAR, c.get(Calendar.YEAR));		
		new_c.set(Calendar.HOUR_OF_DAY, 0);
		new_c.set(Calendar.MINUTE, 0);
		new_c.set(Calendar.SECOND, 0);
		new_c.set(Calendar.MILLISECOND, 0);
        this.calendar = new_c;
		this.headerTitle = new_c.getTimeInMillis();
	}

    public Calendar getHeaderTitleCalendar() {
        return calendar;
    }

	@Override
	public String toString() {
		return "SectionHeaderObject [headerTitle=" + headerTitle + ", amount="
				+ amount + "]";
	}

	@Override
	public int compareTo(SectionHeaderObject another) {
		// TODO Auto-generated method stub
		return another.headerTitle.compareTo(headerTitle);
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}
}
