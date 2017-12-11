public class RDSubscribedTopic extends RDTopic {

	private String key;
	RDTopicInfo info;
	
	protected double value;
	
	private InferMethod inferMethod;
	
	public RDSubscribedTopic(String key, RDTopicInfo info, InferMethod inferMethod) {
		this.key = key;
		this.info = info;
		this.value = -1d;
		this.inferMethod = inferMethod;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public RDTopicInfo getTopicInfo() {
		return info;
	}

	public InferMethod getInferMethod() {
		return inferMethod;
	}

}
