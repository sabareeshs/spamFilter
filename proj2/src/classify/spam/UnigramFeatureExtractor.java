package classify.spam;

import util.Counter;
import util.Interner;
import util.Pair;

public class UnigramFeatureExtractor implements EmailMessageFeatureExtractor {
  
  private static Interner<String> interner = new Interner<String>();

  public Counter<Object> extractFeatures(EmailMessage message) {
    Counter<Object> featureCounts = new Counter<Object>();
    for (Pair<String, Integer> pair : message.getBody()) {
      String s = pair.first();
      // s = s.toLowerCase();
      s = interner.intern(s);
      featureCounts.incrementCount(s, 1.0);
    }
    return featureCounts;
  }

}
