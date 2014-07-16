package util;

import java.util.HashMap;
import java.util.Map;

/** 
 * A class capable of "interning" or canonicalizing Java objects, to save memory.
 * @author grenager
 *
 * @param <T>
 */
public class Interner<T> {
  
  private Map<T,T> map = new HashMap<T,T>();
  
  public T intern(T object) {
    T result = map.get(object);
    if (result==null) {
      map.put(object, object);
      return object;
    }
    return result;
  }

}
