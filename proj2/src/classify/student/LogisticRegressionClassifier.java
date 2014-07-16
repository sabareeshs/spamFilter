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
import classify.general.BinaryClassifier;
import classify.general.Example;

public class LogisticRegressionClassifier extends BinaryClassifier {

  private Counter<Object> weightVector;
  
  public Object getLabel(Example example) {
    Counter scores = getLabelScores(example);
    return scores.argmax();
  }

  /**
   * Just creates a Counter of scores using sigmoid(w*x) as the
   * positive class score and 1-sigmoid(w*x) as the negative class
   * score.
   */
  public Counter<Object> getLabelScores(Example example) {
    Counter<Object> scores = new Counter<Object>();
    double score = getScore(example);
    scores.setCount(positiveLabel, score);
    scores.setCount(negativeLabel, 1-score);
    return scores;
  }
  
  public double getScore(Example example) {
    System.err.println("CS 121 STUDENT: implement me for EXTRA CREDIT!");
    double score = 0.0;
    return sigmoidFunction(score);
  }

  public void train(List<Example> examples) {
    System.err.println("CS 121 STUDENT: implement me for EXTRA CREDIT!");
    weightVector = new Counter<Object>();
  }
  
  static public double sigmoidFunction(double z) {
    double score = 1.0 / (1.0 + Math.exp(-z));
    return score;
  }
}