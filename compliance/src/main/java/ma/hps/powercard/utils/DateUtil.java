package ma.hps.powercard.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	
	
	public static Date addDay(Date dateIn, int nbDay)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateIn);
		cal.add(Calendar.DATE, nbDay);
		return cal.getTime();
		
	}
	
	public static Date addMonth(Date dateIn, int nbMonth)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateIn);
		cal.add(Calendar.DATE, nbMonth);
		return cal.getTime();
	}
	
	public static Date truncDate(Date dateIn)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateIn);
		cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date getSysDate()
	{
		return Calendar.getInstance().getTime();
	}

	public static Date getTruncSysDate()
	{
		return DateUtil.truncDate(DateUtil.getSysDate());
	}
	
	public static boolean isDateBetween(Date date, Date dateStart, Date dateEnd)
	{
		if(date == null)
			return false;
		if(dateStart == null && dateEnd == null)
			return true;
		if(dateStart != null)
			if (date.before(dateStart))
				return false;
		
		if(dateEnd != null)
			if (date.after(dateEnd))
				return false;
		
		return true;
		
	}
}
