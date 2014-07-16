/* PROJECT 2, TASK 3 (optional)
 * ----------------------------
 * Implement the train and getClass methods.
 * 
 * NOTE: Students must implement either the
 * PerceptronClassifier or the LogisticRegressionClassifier.
 */

package classify.student;

import java.util.List;

import util.Counter;
import classify.general.*;

public class PerceptronClassifier extends BinaryClassifier {

  private Counter<Object> weightVector;
  
  public Object getLabel(Example example) {
    Counter scores = getLabelScores(example);
    return scores.argmax();
  }

  /**
   * Just creates a Counter of scores using w*x as the positive class score
   * and -w*x as the negative class score.
   */
  public Counter<Object> getLabelScores(Example example) {
    Counter<Object> scores = new Counter<Object>();
    double score = getScore(example);
    scores.setCount(positiveLabel, score);
    scores.setCount(negativeLabel, -score);
    return scores;
  }
  
  public double getScore(Example example) {
    System.err.println("CS 121 STUDENT: implement me as part of TASK 3!");
    return 0.0;    
  }

  public void train(List<Example> examples) {
    System.err.println("CS 121 STUDENT: implement me as part of TASK 3!");
    weightVector = new Counter<Object>();
  }
}