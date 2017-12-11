
public class RDValue extends RDObject {
	
	private String key;
	private String value;
	
	public RDValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
}
