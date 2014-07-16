package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import util.PriorityQueue;

/**
 * A class maintaining a Map from typed Object keys to double values.
 * 
 * @author grenager
 * 
 * @param <T>
 */
public class Counter<T> implements Comparator {

  private Map<T, Double> map;

  private double totalCount = 0;

  public double getTotalCount() {
    return totalCount;
  }

  public void setCount(T key, double count) {
    totalCount -= getCount(key);
    map.put(key, count);
    totalCount += count;
  }
  
  public void incrementCounts(Counter<T> counts) {
    for (T key : counts.keySet()) {
      incrementCount(key, counts.getCount(key));
    }
  }

  public void improveCount(T key, double count) {
    if (!map.containsKey(key)) {
      setCount(key, count);
    } else {
      // otherwise, we have some other count already
      double oldCount = getCount(key);
      if (count > oldCount) {
        setCount(key, count); // only set it if it's better!
      }
    }
  }

  public void incrementCount(T key, double count) {
    double d = getCount(key);
    setCount(key, d + count);
  }

  public double remove(T key) {
    Double d = map.get(key);
    if (d == null)
      return 0.0;
    totalCount -= d;
    map.remove(key);
    return d;
  }

  public double getCount(T key) {
    Double d = map.get(key);
    if (d == null)
      return 0.0;
    return d;
  }

  public boolean containsKey(T key) {
    return map.containsKey(key);
  }

  public Set<T> keySet() {
    return map.keySet();
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.size() == 0;
  }

  public String toString() {
    return map.toString();
  }
  
  public String toVerticalString() {
    StringBuilder b = new StringBuilder();
    for (T key : keySet()) {
      double count = getCount(key);
      b.append(key + "\t" + count + "\n");
    }
    return b.toString();
  }
  
  public T argmax() {
    T best = null;
    double max = Double.NEGATIVE_INFINITY;
    for (T key : keySet()) {
      double count = getCount(key);
      if (count > max) {
        max = count;
        best = key;
      }
    }
    return best;
  }

  public Set<T> keysAbove(double d) {
    Set<T> result = new LinkedHashSet<T>();
    for (T key : keySet()) {
      double count = getCount(key);
      if (count > d)
        result.add(key);
    }
    return result;
  }
  
  public Set<T> keysAboveIncluding(double d) {
    Set<T> result = new LinkedHashSet<T>();
    for (T key : keySet()) {
      double count = getCount(key);
      if (count >= d)
        result.add(key);
    }
    return result;
  }
  
  public Set<T> keysBelow(double d) {
    Set<T> result = new LinkedHashSet<T>();
    for (T key : keySet()) {
      double count = getCount(key);
      if (count < d)
        result.add(key);
    }
    return result;
  }
  
  public Set<T> keysBelowIncluding(double d) {
    Set<T> result = new LinkedHashSet<T>();
    for (T key : keySet()) {
      double count = getCount(key);
      if (count < d)
        result.add(key);
    }
    return result;
  }
  
  public Set<T> keysBetween(double lower, double upper) {
    Set<T> result = new LinkedHashSet<T>();
    for (T key : keySet()) {
      double count = getCount(key);
      if (count < upper && count > lower)
        result.add(key);
    }
    return result;
  }
  
  public Set<T> keysBetweenIncluding(double lower, double upper) {
    Set<T> result = new LinkedHashSet<T>();
    for (T key : keySet()) {
      double count = getCount(key);
      if (count <= upper && count >= lower)
        result.add(key);
    }
    return result;
  }

  public double max() {
    double max = Double.NEGATIVE_INFINITY;
    for (T key : keySet()) {
      double count = getCount(key);
      if (count > max) {
        max = count;
      }
    }
    return max;
  }

  public Counter<T> exp() {
    Counter<T> result = new Counter<T>();
    for (T key : keySet()) {
      result.setCount(key, Math.exp(getCount(key)));
    }
    return result;
  }

  public Counter<T> log() {
    Counter<T> result = new Counter<T>();
    for (T key : keySet()) {
      result.setCount(key, Math.log(getCount(key)));
    }
    return result;
  }

  public Counter<T> normalize() {
    Counter<T> result = new Counter<T>();
    double totalCount = getTotalCount();
    for (T key : keySet()) {
      result.setCount(key, getCount(key) / totalCount);
    }
    return result;
  }

  public Counter<T> logNormalize() {
    double max = max();
    Counter<T> shifted = shift(-max);
    Counter<T> exp = shifted.exp();
    double total = exp.getTotalCount();
    double logTotal = Math.log(total);
    return shifted.shift(-logTotal);
  }

  public T sampleFromDistribution(Random random) {
    double total = getTotalCount();
    double d = random.nextDouble(); // between 0 and 1 inclusive
    double accumulator = 0;
    for (T key : keySet()) {
      accumulator += getCount(key);
      if (d <= accumulator / total)
        return key;
    }
    return keySet().iterator().next(); // this is very rare occurrence, a
    // result of double math
  }

  public Counter() {
    map = new LinkedHashMap<T, Double>();
  }
  
  public Counter(Counter<T> other) {
    this();
    incrementCounts(other);
  }

  public Counter<T> scale(double d) {
    Counter<T> result = new Counter<T>();
    for (T key : keySet()) {
      result.setCount(key, getCount(key) * d);
    }
    return result;
  }

  public Counter<T> shift(double d) {
    Counter<T> result = new Counter<T>();
    for (T key : keySet()) {
      result.setCount(key, getCount(key) + d);
    }
    return result;
  }

  /**
   * Builds a priority queue whose elements are the counter's elements, and
   * whose priorities are those elements' counts in the counter.
   */
  public PriorityQueue<T> asPriorityQueue() {
    PriorityQueue<T> pq = new PriorityQueue<T>();
    for (Map.Entry<T, Double> entry : map.entrySet()) {
      pq.add(entry.getKey(), entry.getValue());
    }
    return pq;
  }

  public PriorityQueue<T> asPriorityQueueReverseOrder() {
    PriorityQueue<T> pq = new PriorityQueue<T>();
    for (Map.Entry<T, Double> entry : map.entrySet()) {
      pq.add(entry.getKey(), -entry.getValue());
    }
    return pq;
  }

  public int compare(Object o1, Object o2) {
    Double c1 = getCount((T)o1);
    Double c2 = getCount((T)o2);
    return c1.compareTo(c2);
  }
  
  public List<T> toSortedList() {
    List<T> result = new ArrayList<T>(keySet());
    Collections.sort(result, this);
    Collections.reverse(result); // sort from highest to lowest
    return result;
  }
  
  public static void main(String[] args) {
    Counter<String> c = new Counter<String>();
    c.setCount("a", 212);
    c.setCount("b", 1234);
    c.setCount("c", .007);
    c = c.log();
    c = c.logNormalize();
    c = c.exp();
    System.err.println("total: " + c.getTotalCount());
  }

}