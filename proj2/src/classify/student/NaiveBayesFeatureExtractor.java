/* PROJECT 2, TASK 2 (required)
 * ----------------------------
 * Implement the extract features method.
 */

package classify.student;

import util.Counter;
import classify.spam.EmailMessage;
import classify.spam.EmailMessageFeatureExtractor;

public class NaiveBayesFeatureExtractor implements EmailMessageFeatureExtractor {

  public Counter<Object> extractFeatures(EmailMessage message) {
    System.err.println("CS 121 STUDENT: implement me as part of TASK 2!");
    Counter<Object> features = new Counter<Object>();
    return features;
  }

}
