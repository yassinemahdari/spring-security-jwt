package ma.hps.powercard.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class StringUtil {

	
	public static String NVL(String str1,String str2)
	{
		if(str1 != null)
			return str1;
		return str2;
	}
	
	public static BigDecimal stringToDecimal(String str) throws Exception
	{
		if(str == null)
			return null;
		
		if(str.equals("") || str.equals("null"))
			return null;
		
		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
		String strTmp = str.replace(".", ",");
		Number number = format.parse(strTmp);
        
		return BigDecimal.valueOf(number.doubleValue());
	}
	
	public static BigDecimal stringToLong(String str) throws Exception
	{
		if(str == null)
			return null;
		
		if(str.equals("") || str.equals("null"))
			return null;
        
		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
		String strTmp = str.replace(".", ",");
		Number number = format.parse(strTmp);
		
		return BigDecimal.valueOf(number.longValue());
	}
	
	public static String formatNumber(String localeStr, BigDecimal number, int exp )
	{
		if(number == null)
			return null;
		
		NumberFormat currencyFormatter;
		if (localeStr.equals("fr_FR") || localeStr.equals("es_ES"))
			currencyFormatter = NumberFormat.getInstance(Locale.FRENCH);
		else
			currencyFormatter = NumberFormat.getInstance(Locale.ENGLISH);
		
		currencyFormatter.setMinimumFractionDigits(exp);
		currencyFormatter.setMaximumFractionDigits(exp);
			
		
		return currencyFormatter.format(number);
	}
	
	public static String formatAmount(String localeStr, BigDecimal number, int exp,String currencyAlpha )
	{
		if(number == null)
			return null;
		return formatNumber(localeStr,number,exp) + " " + currencyAlpha;
	}
}
