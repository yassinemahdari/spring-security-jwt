package ma.hps.powercard.compliance.utils;


public class LangObject{
		private String columnValue;
		private String locale;
		private String columnName;
		

		public LangObject(String locale,String columnName) {
			this.columnName = columnName;
			this.locale = locale;
		}
		public LangObject(String locale) {
			this.locale = locale;
		}
		public LangObject() {
		}
		
		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public String getColumnName() {
			return columnName;
		}
		public String getColumnValue() {
			return columnValue;
		}
		
		public void setColumnValue(String columnValue) {
			this.columnValue = columnValue;
		}
		public String getLocale() {
			return locale;
		}
		public void setLocale(String locale) {
			this.locale = locale;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LangObject other = (LangObject) obj;
			if (columnName == null) {
				if (other.columnName != null)
					return false;
			} else if (!columnName.equals(other.columnName))
				return false;
			if (locale == null) {
				if (other.locale != null)
					return false;
			} else if (!locale.equals(other.locale))
				return false;
			return true;
		}
		
		

		
	}
