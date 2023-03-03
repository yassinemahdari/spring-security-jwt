package ma.hps.powercard.utils;

public class LangUtil {

	
	public static String getIsoLangCode(String localeStr)
	{
		if(localeStr == null)
			return "ENG";
		else if (localeStr.equals("fr_FR"))
			return "FRE";
		else if (localeStr.equals("en_US"))
			return "ENG";
		else if (localeStr.equals("es_ES"))
			return "SPA";
		else if (localeStr.equals("ar_AR"))
			return "ARA";
		else if (localeStr.equals("it_IT"))
			return "ITA";
		
		return "ENG";
	}
	

}
