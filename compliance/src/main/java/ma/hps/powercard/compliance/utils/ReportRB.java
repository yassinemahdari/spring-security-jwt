package ma.hps.powercard.compliance.utils;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportRB extends ResourceBundle {
	private Locale locale_chaine;
	private Dictionary<String, String> dictionary = null;

	public ReportRB(Locale locale_chaine, Dictionary<String, String> dictionary) {
		super();
		this.locale_chaine = locale_chaine;
		this.dictionary = dictionary;
	}

	@Override
	protected Object handleGetObject(String key) {
		Object translation = dictionary.get(key);
		if (translation == null || ((String) translation).trim().length() == 0) {
			return key;
		}
		return translation;
	}

	@Override
	public Enumeration<String> getKeys() {
		return dictionary.keys();
	}

	public Locale getlocale_chaine() {
		return locale_chaine;
	}

}
