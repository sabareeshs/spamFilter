package classify.general;

import java.util.List;

import util.Counter;

public abstract class BinaryClassifier implements Classifier {

  protected Object positiveLabel = new String("+");
  protected Object negativeLabel = new String("-");

  public Object getPositiveLabel() {
    return positiveLabel;
  }
  public void setPositiveLabel(Object positiveLabel) {
    this.positiveLabel = positiveLabel;
  }
  
  public Object getNegativeLabel() {
    return negativeLabel;
  }
  public void setNegativeLabel(Object negativeLabel) {
    this.negativeLabel = negativeLabel;
  }
  
  public abstract Object getLabel(Example example);
  public abstract Counter<Object> getLabelScores(Example example);
  public abstract void train(List<Example> examples);

}
