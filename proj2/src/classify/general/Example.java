package classify.general;

import util.Counter;

/**
 * A class representing a data example, for machine learning training and testing.
 * @author grenager
 *
 */
public class Example {
  
  private Object label;
  private Counter<Object> features;
  
  public Object getLabel() {
    return label;
  }
  
  public Counter<Object> getFeatures() {
    return features;
  }
  
  public Example(Object label, Counter<Object> features) {
    this.label = label;
    this.features = features;
  }
  
}
