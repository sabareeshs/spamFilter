package classify.general;

import java.util.List;

import util.Counter;

public class AlwaysPositiveClassifier extends BinaryClassifier {

  public Object getLabel(Example example) {
		return positiveLabel;
	}
    
  public Counter<Object> getLabelScores(Example example) {
    Counter<Object> result = new Counter<Object>();
    result.setCount(positiveLabel, 1.0);
    return result;
  }

	public void train(List<Example> examples) {
		System.err.println(getClass().getName() + " does not train...");
	}

}
