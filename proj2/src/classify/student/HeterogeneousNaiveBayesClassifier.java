/* PROJECT 2, TASK 1 (required)
 * ----------------------------
 * Implement the train and getLabel methods.
 * Use the UnigramFeatureExtractor initially, but
 * later use a feature extractor of your choice.
 */

package classify.student;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;

import classify.general.Classifier;
import classify.general.Example;
import util.Counter;
import util.CounterMap;

public class HeterogeneousNaiveBayesClassifier implements Classifier {

  private List<CounterMap<Object, Object>> labelAndFeatureCountsList;
  private List<Set<Object>> featureDomainList;
  private Counter<Object> labelCounts;
  
  public Object getLabel(Example example) {
    Counter<Object> scores = getLabelScores(example);
    return scores.argmax();
  }

  public Counter<Object> getLabelScores(Example example) {
    Counter<Object> labelScores = new Counter<Object>();
    for (Object label : labelCounts.keySet()) {
      double labelScore = getLogProbabilityOfLabel(example, label);
      labelScores.setCount(label, labelScore);
    }
    return labelScores;
  }
  
  public double getLogProbabilityOfLabel(Example example, Object label) {
    System.err.println("CS 121 STUDENT: implement me for extra credit!");
    HeterogeneousExample heterogeneousExample = (HeterogeneousExample)example;
    return 0.0;
  }

  public void train(List<Example> examples) {
    System.err.println("CS 121 STUDENT: implement me for extra credit!");
    labelAndFeatureCountsList = new ArrayList<CounterMap<Object, Object>>();
    labelCounts = new Counter<Object>();
    for(Example example : examples) {
      HeterogeneousExample heterogeneousExample = (HeterogeneousExample)example;
    }
  }

  public void testDistributionsValid() {
    System.err.println("CS 121 STUDENT: optional but highly recommended!");
  }
}