import java.util.function.Supplier;

public class RDQueriedTopic extends RDTopic {
	
	private String key;
	private Supplier<Double> query;
	RDTopicInfo info;
	
	public RDQueriedTopic(String key, Supplier<Double> query, RDTopicInfo info) {
		this.key = key;
		this.query = query;
		this.info = info;
	}

	@Override
	public double getValue() {
		return query.get();
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public RDTopicInfo getTopicInfo() {
		return info;
	}

}
