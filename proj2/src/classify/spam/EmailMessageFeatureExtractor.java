package classify.spam;

import util.Counter;

public interface EmailMessageFeatureExtractor {

	public Counter<Object> extractFeatures(EmailMessage message);
	
}