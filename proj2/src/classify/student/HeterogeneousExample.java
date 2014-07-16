package classify.student;

import util.Counter;
import classify.general.Example;
import java.util.List;

/**
 * A class representing a data example, for machine learning training and testing.
 * @author grenager
 *
 */
public class HeterogeneousExample extends Example {
  
  private Object label;
  private List<Counter<Object>> features;
  
  public Object getLabel() {
    return label;
  }
  
  public List<Counter<Object>> getFeaturesList() {
    return features;
  }
  
  public HeterogeneousExample(Object label, List<Counter<Object>> features) {
    super(label, null);
    this.label = label;
    this.features = features;
  }
  
}
