/* PROJECT 2, TASK 1 (required)
 * ----------------------------
 * Implement the train and getLabel methods.
 * Use the UnigramFeatureExtractor initially, but
 * later use a feature extractor of your choice.
 */

package classify.student;

import java.util.List;
import util.Counter;
import util.CounterMap;

import classify.general.Classifier;
import classify.general.Example;

public class NaiveBayesClassifier implements Classifier {

  private CounterMap<Object, Object> labelAndFeatureCounts;
  private Counter<Object> labelCounts;
  
  private Counter<Object> labelProbs; // log prob of all labels
  private Counter<Object> vocabulary;
  
  // parameters for smoothing
  // P(knownword/label) = (c(knownword,label) + delta) / (c(label) + V * delta)
  // P(unknownword/label) = (eta * delta) / (c(label) + V * delta)
  // V = W + eta 
  // W is number of distinct words/features
  private double delta = 1.0;
  private double eta = 1.0;
  private double V;
  
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
	
	Counter<Object> features = example.getFeatures();
	Counter<Object> wordCounts = labelAndFeatureCounts.getCounter(label);
	
	double logP = labelProbs.getCount(label);

	for(Object f : features.keySet()) {
		double p = 1.0;
		if(wordCounts.containsKey(f)) {
			p = (wordCounts.getCount(f) + this.delta)
				/ (labelCounts.getCount(label) + this.V * this.delta);
		} else {
			p = (eta * this.delta)
					/ (labelCounts.getCount(label) + this.V * this.delta);
		}
		logP += Math.log(p);
	}
	 
 //   System.err.println("CS 121 STUDENT: implement me as part of TASK 1!");
    return logP;
  }

  public void train(List<Example> examples) {
    labelAndFeatureCounts = new CounterMap<Object, Object>();
    labelCounts = new Counter<Object>();
    vocabulary = new Counter<Object>();
    for(Example e : examples) {
    	labelCounts.incrementCount(e.getLabel(),1.0);
    	Counter<Object> features = e.getFeatures();
    	for(Object f : features.keySet()) {
    		vocabulary.incrementCount(f, 1);
    		labelAndFeatureCounts.incrementCount(e.getLabel(), f, features.getCount(f));
    	}
    }
    labelProbs = new Counter<Object>();
    // lets compute log probabilities for all labels
 	double totalExamples = examples.size();
 	for(Object key : labelCounts.keySet()){
    	//System.out.println(key + " " + labelCounts.getCount(key)) ;
 		labelProbs.setCount(key, Math.log(labelCounts.getCount(key)/totalExamples));
 	}

 	this.V = this.eta + this.vocabulary.size();
  //  System.err.println("CS 121 STUDENT: implement me as part of TASK 1!");
  }

  public void testDistributionsValid() {
    System.err.println("CS 121 STUDENT: optional but highly recommended!");
  }
}