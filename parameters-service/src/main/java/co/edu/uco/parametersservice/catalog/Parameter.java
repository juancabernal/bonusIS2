package co.edu.uco.parametersservice.catalog;

public class Parameter {
	
	private String key;
	private String value;

	// Constructor correcto
	public Parameter(String key, String value) {
		this.key = key;
		this.value = value;
	}

	// Getter y Setter de key
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	// Getter y Setter de value
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}