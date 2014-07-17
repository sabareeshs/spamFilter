/* PROJECT 2, TASK 3 (optional)
 * ----------------------------
 * Implement the train and getClass methods.
 * 
 * NOTE: Students must implement either the
 * PerceptronClassifier or the LogisticRegressionClassifier.
 */

package classify.student;

import java.util.List;

import classify.general.BinaryClassifier;
import classify.general.Example;
import classify.spam.EmailMessage;
import util.Counter;

public class PerceptronClassifier extends BinaryClassifier {

    private Counter<Object> weightVector;
    private double bias = 0;

    @Override
    public Object getLabel(Example example) {
        Counter<Object> scores = getLabelScores(example);
        return scores.argmax();
    }

    /**
     * Just creates a Counter of scores using w*x as the positive class score and -w*x as the
     * negative class score.
     */
    @Override
    public Counter<Object> getLabelScores(Example example) {
        Counter<Object> scores = new Counter<Object>();
        double score = getScore(example);
        scores.setCount(positiveLabel, score);
        scores.setCount(negativeLabel, -score);
        return scores;
    }

    public double getScore(Example example) {
        double score = this.bias;
        Counter<Object> features = example.getFeatures();
        for (Object f : features.keySet()) {
            score += weightVector.getCount(f) * features.getCount(f);
        }
        //System.err.println("CS 121 STUDENT: implement me as part of TASK 3!");
        return score;
    }

    private void updateWeights(Example example, double error, double alpha) {
        Counter<Object> features = example.getFeatures();
        for (Object f : features.keySet()) {
            weightVector.incrementCount(f, alpha * error * features.getCount(f));
        }
        this.bias += alpha * error;
    }

    @Override
    public void train(List<Example> examples) {
        //System.err.println("CS 121 STUDENT: implement me as part of TASK 3!");
        weightVector = new Counter<Object>();
        int numEpochs = 100;
        double alpha = 10;
        while (numEpochs-- > 0) {
            for (Example e : examples) {
                double score = (getScore(e) >= 0 ? 1.0 : 0.0);
                double y = (e.getLabel() == EmailMessage.labelSpam ? 0.0 : 1.0);
                updateWeights(e,y-score, alpha);
            }
            alpha -= alpha/10;
        }
    }
}
