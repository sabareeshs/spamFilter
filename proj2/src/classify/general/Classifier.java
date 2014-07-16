package classify.general;

import java.util.List;


import util.Counter;

/**
 * An interface for classes which can learn a mapping from data examples to labels.
 * @author grenager
 *
 */
public interface Classifier {

  /**
   * Get the label assigned to the example by this Classifier.
   * @param example
   * @return
   */
  public Object getLabel(Example example);

  /**
   * Get the unnormalized log scores for each label.
   * 
   * @param example
   * @return
   */
  public Counter<Object> getLabelScores(Example example);

  /**
   * Train this Classifier from a List of data examples.
   * @param examples
   */
  public void train(List<Example> examples);

}