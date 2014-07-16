package classify.spam;

import util.Counter;
import util.Interner;
import util.Pair;

public class WordShapeFeatureExtractor implements EmailMessageFeatureExtractor {

  private static Interner<String> interner = new Interner<String>();

  public Counter<Object> extractFeatures(EmailMessage message) {
    Counter<Object> featureCounts = new Counter<Object>();
    for (Pair<String, Integer> pair : message.getBody()) {
      String s = getWordShape(pair.first());
      s = interner.intern(s);
      featureCounts.incrementCount(s, 1.0);
    }
    return featureCounts;
  }
  
  private String getWordShape(String s) {
    StringBuilder b = new StringBuilder();
    char preprev = (char) -1;
    char prev = (char) -1;
    for (int i=0; i<s.length(); i++) {
      char c = s.charAt(i);
      char next = '?';
      if (Character.isDigit(c)) next = '0';
      else if (Character.isLowerCase(c)) next = 'c';
      else if (Character.isUpperCase(c)) next = 'C';
      
      // cases:
      //   1. prev, preprev
      //   2. i'm diff from prev guy
      //   3. prev guy diff from his prev guy
      if (prev<0 || prev!=next || preprev<0 || preprev!=prev) {
        b.append(next);
      }
      preprev = prev;
      prev = next;
    }
    String shape = b.toString();
//    System.err.println(s + " mapped to " + shape);
    return shape;
  }
}