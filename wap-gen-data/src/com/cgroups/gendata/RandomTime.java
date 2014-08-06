package com.cgroups.gendata;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class RandomTime {
	Random random = null;
	Date date_start = null;
	Date date_end = null;
	public RandomTime(){
		random = new Random();
		Calendar cal = Calendar.getInstance();
		cal.set(2014, 1, 1);
		date_start = cal.getTime();
		cal = Calendar.getInstance();
		date_end = cal.getTime();
	}
	public Date getNextDate(){
		double value = random.nextDouble();
		value *= (date_end.getTime()-date_start.getTime());
		long offset = (long) value;
		Date date = new Date(date_start.getTime()+offset);
		return date;
	}
	public String getNextDay(){
		DateFormat date_format = new SimpleDateFormat("yyyyMMdd");
		Date date = this.getNextDate();
		return date_format.format(date);
	}
}
