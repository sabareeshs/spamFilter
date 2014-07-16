package util;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Collection;

/**
 * Maintains counts of (key, value) pairs.  The map is structured so
 * that for every key, one can get a counter over values.  Example
 * usage: keys might be words with values being POS tags, and the
 * count being the number of occurences of that word/tag pair.  The
 * sub-counters returned by getCounter(word) would be count
 * distributions over tags for that word.
 *
 * @author Dan Klein
 */
public class CounterMap<K, V> {

  private Map<K, Counter<V>> counterMap;
  private double totalAcrossAllCounters = 0;
  private int sizeAcrossAllCounters = 0;

  // -----------------------------------------------------------------------

  public CounterMap() {
  		counterMap = new LinkedHashMap<K, Counter<V>>();
  }

  // -----------------------------------------------------------------------

  protected Counter<V> ensureCounter(K key) {
    Counter<V> valueCounter = counterMap.get(key);
    if (valueCounter == null) {
//    		System.out.println("Adding counter for " + key);
      valueCounter = new Counter<V>();
      counterMap.put(key, valueCounter);
    }
    return valueCounter;
  }
  
  /**
   * Returns whether the countermap contains the given key.  Note that this is the
   * way to distinguish keys which are in the countermap with count zero, and those
   * which are not in the counter (and will therefore return count zero from
   * getCount().
   *
   * @param key
   * @return whether the counter contains the key
   */
  public boolean containsKey(K key) {
    return counterMap.containsKey(key);
  }

  /**
   * Returns the keys that have been inserted into this CounterMap.
   */
  public Set<K> keySet() {
    return counterMap.keySet();
  }
  
  public Collection<Counter<V>> values() {
	  return counterMap.values();
  }
  
  public void removeValueFromCounter(K key, V value) {
	  if(counterMap.containsKey(key)) {
		  Counter<V> valueCounter = counterMap.get(key);
		  if(valueCounter.containsKey(value)) {
			  totalAcrossAllCounters -= valueCounter.getTotalCount();
			  sizeAcrossAllCounters -= valueCounter.size();
			  valueCounter.remove(value);
			  if(valueCounter.isEmpty())
				  counterMap.remove(key);
			  else {
				  totalAcrossAllCounters += valueCounter.getTotalCount();
				  sizeAcrossAllCounters += valueCounter.size();
			  }
		  }
	  }
  }

  /**
   * Sets the count for a particular (key, value) pair.
   */
  public void setCount(K key, V value, double count) {
    Counter<V> valueCounter = ensureCounter(key);
    totalAcrossAllCounters -= valueCounter.getTotalCount();
    sizeAcrossAllCounters -= valueCounter.size();
    valueCounter.setCount(value, count);
    totalAcrossAllCounters += valueCounter.getTotalCount();
    sizeAcrossAllCounters += valueCounter.size();
  }

  /**
   * Increments the count for a particular (key, value) pair.
   */
  public void incrementCount(K key, V value, double count) {
    Counter<V> valueCounter = ensureCounter(key);
    totalAcrossAllCounters -= valueCounter.getTotalCount();
    sizeAcrossAllCounters -= valueCounter.size();
    valueCounter.incrementCount(value, count);
    totalAcrossAllCounters += valueCounter.getTotalCount();
    sizeAcrossAllCounters += valueCounter.size();
  }

  /**
   * Gets the count of the given (key, value) entry, or zero if that
   * entry is not present.  Does not create any objects.
   */
  public double getCount(K key, V value) {
    Counter<V> valueCounter = counterMap.get(key);
    if (valueCounter == null)
      return 0.0;
    return valueCounter.getCount(value);
  }

  /**
   * Gets the sub-counter for the given key.  If there is none, a
   * counter is created for that key, and installed in the CounterMap.
   * You can, for example, add to the returned empty counter directly
   * (though you shouldn't).  This is so whether the key is present or
   * not, modifying the returned counter has the same effect (but
   * don't do it).
   */
  public Counter<V> getCounter(K key) {
	  return ensureCounter(key);
  }
  
  public double getCounterTotalCount(K key) {
	  double counterTotalCount = 0;
	  if(containsKey(key)) {
		  counterTotalCount += getCounter(key).getTotalCount();
	  }
	  return counterTotalCount;
  }
  
  public int getCounterSize(K key) {
	  int counterSize = 0;
	  if(containsKey(key)) {
		  counterSize += getCounter(key).size();
	  }
	  return counterSize;
  }
  
  public Set<V> getCounterKeySet(K key) {
	  Set<V> keySet = null;
	  if(containsKey(key)) {
		  keySet = getCounter(key).keySet();
	  }
	  return keySet;
  }

  /**
   * Returns the total of all counts in sub-counters.  This
   * implementation is linear; it recalculates the total each time.
   */
  public double getTotalCount() {
//    double total = 0.0;
//    for (Map.Entry<K, Counter<V>> entry : counterMap.entrySet()) {
//      Counter<V> counter = entry.getValue();
//      total += counter.getTotalCount();
//    }
//    return total;
	  return totalAcrossAllCounters;
  }

  /**
   * Returns the total number of (key, value) entries in the
   * CounterMap (not their total counts).
   */
  public int totalSize() {
//    int total = 0;
//    for (Map.Entry<K, Counter<V>> entry : counterMap.entrySet()) {
//      Counter<V> counter = entry.getValue();
//      total += counter.size();
//    }
//    return total;
	  return sizeAcrossAllCounters;
  }

  /**
   * The number of keys in this CounterMap (not the number of
   * key-value entries -- use totalSize() for that)
   */
  public int size() {
    return counterMap.size();
  }

  /**
   * True if there are no entries in the CounterMap (false does not
   * mean totalCount > 0)
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("[\n");
    for (Map.Entry<K, Counter<V>> entry : counterMap.entrySet()) {
      sb.append("  ");
      sb.append(entry.getKey());
      sb.append(" -> ");
      sb.append(entry.getValue());
      sb.append("\n");
    }
    sb.append("]");
    return sb.toString();
  }
  
  public void test() {
		System.out.print("\tTesting CounterMap...");
		int totalSize1 = 0, totalSize2 = 0;
		double totalCount1 = 0, totalCount2 = 0;
		boolean hasBug = false;
		String errors = "";
		for(K key : keySet()) {
			totalCount1 += getCounterTotalCount(key);
			totalSize1 += getCounterSize(key);
			int numCKeys = 0;
			double counterTotal = 0;
			for(V cKey : getCounterKeySet(key)) {
				counterTotal += getCount(key, cKey);
				totalCount2 += getCount(key, cKey);
				numCKeys++;
			}
			totalSize2 += numCKeys;
			System.out.print("");
			if(numCKeys != getCounterSize(key)) {
				errors +=("NOT EQUAL: size()=" + getCounterSize(key) + "\tnumKeys=" + numCKeys + "\t");
				hasBug = true;
			}
	    if(counterTotal != getCounterTotalCount(key)) {
	    		errors +=("NOT EQUAL: totalCount()=" + getCounterTotalCount(key) + "\ttc=" + counterTotal + "\t");
	    		hasBug = true;
	    }
		}
	if(totalSize1 != totalSize()) {
		errors +=("NOT EQUAL: totalSize()=" + totalSize() + "\tts1=" + totalSize1 + "\t");
		hasBug = true;
	}
	if(totalCount1 != getTotalCount()) {
		errors +=("NOT EQUAL: getTotalCount()=" + getTotalCount() + "\ttc1=" + totalCount1 + "\t");
		hasBug = true;
	}
	if(totalSize2 != totalSize()) {
	    errors +=("NOT EQUAL: totalSize()=" + totalSize() + "\tts2=" + totalSize2 + "\t");
		hasBug = true;
	}
	if(totalCount2 != getTotalCount()) {
		errors +=("NOT EQUAL: getTotalCount()=" + getTotalCount() + "\ttc2=" + totalCount2 + "\t");
		hasBug = true;
	}
	if(hasBug) {
		System.out.println("FAILED");
		System.out.println("\tERRORS:\n");
		System.out.println(errors + "\n");
	} else
		System.out.println("PASSED");
  }

  // -----------------------------------------------------------------------

  public static void main(String[] args) {
    CounterMap<String, String> bigramCounterMap = new CounterMap<String, String>();
    bigramCounterMap.incrementCount("people", "run", 1);
    bigramCounterMap.incrementCount("cats", "growl", 2);
    bigramCounterMap.incrementCount("cats", "scamper", 3);
    bigramCounterMap.incrementCount("snakes", "slither", 1);
    System.out.println(bigramCounterMap);
    bigramCounterMap.removeValueFromCounter("snakes", "slither");
    System.out.println("Entries for cats: " + bigramCounterMap.getCounter("cats"));
    System.out.println("Entries for dogs: " + bigramCounterMap.getCounter("dogs"));
    System.out.println("Count of cats scamper: " + bigramCounterMap.getCount("cats", "scamper"));
    System.out.println("Count of snakes slither: " + bigramCounterMap.getCount("snakes", "slither"));
    System.out.println("Total size: " + bigramCounterMap.totalSize());
    System.out.println("Total count: " + bigramCounterMap.getTotalCount());
    System.out.println(bigramCounterMap);
  }
}