import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Host {
	protected static int wins = 0;
	public static void main(String[] Args){
//		ArgumentationSystem as = new ArgumentationSystem();
		for(int i = 0; i<1; i++){
			Playground playground = new Playground();
		}
	}
	
	/*
	 * For every argument registered, checks what this argument attacks
	 * if the attacked hasn't been attacked so far creates an entry in the map
	 * and adds the argument in the list
	 * else includes it to the other attackers
	 */
	public static Map<Argument, ArrayList<Argument>> populateAttacks(ArrayList<Argument> argumentList){	
			Map<Argument, ArrayList<Argument>> attackRelation = new HashMap<Argument, ArrayList<Argument>>();
			for(Argument arg: argumentList){
				for(Argument attackedArg: arg.argumentsIamAttacking){
					if(argumentList.contains(attackedArg)){
							if(attackRelation.containsKey(attackedArg)){
								Host.includeElement(attackRelation, attackedArg, arg);
							}
							else{
								Host.includeFirstElement(attackRelation, attackedArg, arg);
							}
					}
				}
			}
//			for(Argument arg: attackRelation.keySet()){
//				ArrayList<Argument> array = new ArrayList<Argument>();
//				array = attackRelation.get(arg);
//				System.out.print(arg.name + "-> ");
//				for(Argument argument:array){
//					System.out.print(argument.name + ", ");
//				}
//				Host.sleep(100);
//				System.out.println();
//			}
//		
//		System.out.println("--------------");
		return attackRelation;
	}
	
	protected static <K,V> void includeFirstElement(Map<K, ArrayList<V>> collection, K key, V element){
		ArrayList<V> turns = new ArrayList<V>();
		collection.put(key, turns);
		turns.add(element);
	}
	
	protected static <K, V> void includeElement(Map<K, ArrayList<V>> collection, K key, V element){
		ArrayList<V> turns = new ArrayList<V>();
		turns = collection.get(key);
		turns.add(element);
	}
	
	protected static <K, V> void initiateCollection(Map<K, ArrayList<V>> collection,K... values){
		ArrayList<V> initial = new ArrayList<V>();
		for(K val: values){
			collection.put(val, initial);
		}
	}
	
	protected static void populateArrayList(ArrayList<Argument> toPopulate, ArrayList<Argument> with){
		for(int i=0; i< with.size(); i++){
			toPopulate.add(with.get(i));
		}
	}
	
	/**
	 * Copy hash map to another hash map
	 * 
	 * @param original
	 * @return copy
	 */
	public static <K1, V> Map<K1, ArrayList<V>> deepCopy(Map<K1, ArrayList<V>> original){
		Map<K1, ArrayList<V>> copy = new HashMap<K1, ArrayList<V>>();
		for(Entry<K1, ArrayList<V>> entry: original.entrySet()){
			copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		return copy;
	}
	
	/**
	 * Thread sleep
	 * 
	 * @param time - how long in ms
	 */
	 protected static void sleep(long time){
	    try {
	           Thread.sleep(time);
	       } catch (InterruptedException e) {
	           e.printStackTrace();
	       }
	  }
}
