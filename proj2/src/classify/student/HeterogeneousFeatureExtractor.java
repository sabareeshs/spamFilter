/* PROJECT 2, TASK 2 (required)
 * ----------------------------
 * Implement the extract features method.
 */

package classify.student;

import util.Counter;
import classify.spam.EmailMessage;
import classify.spam.EmailMessageFeatureExtractor;
import java.util.List;
import java.util.ArrayList;

public class HeterogeneousFeatureExtractor {

  // Put whatever feature extractors you want to use in this array
  // using the same syntax as the examples
  private EmailMessageFeatureExtractor[] featureExtractorList = 
  { 
      new classify.spam.UnigramFeatureExtractor(),
      new classify.spam.WordShapeFeatureExtractor()
    };
  
  public List<Counter<Object>> extractFeatures(EmailMessage message) {
    List<Counter<Object>> features = new ArrayList<Counter<Object>>();
    // puts each feature extractors feature counts in a separate counter
    for(EmailMessageFeatureExtractor fextractor : featureExtractorList) {
      features.add(fextractor.extractFeatures(message));
    }
    return features;
  }

}
