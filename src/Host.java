/**
 *  --------------------------------------------------
    --    Todor Davchev- University of Aberdeen     --
	--      email: t.davchev.10@aberdeen.ac.uk      --
	--      CS4527 Single Honours Computing	        --
	--    			 Project 2014  					--
	--         							            --
	--		   Optimising Decisions with            --
	--		    Opponent Modelling for              -- 
	--			Strategic Argumentation				--
	--												--
	--------------------------------------------------
	
	--------------------------------------------------
	--          Due Friday 16 May 17:00	            --
	--------------------------------------------------
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/**
 * The class is responsible for initialising the game
 * of argumentation. In addition, it provides methods
 * which are used system-wise by the other classes 
 * @author Todor Davchev
 * @version 2.3rtm
 * @see java.util.ArrayList
 * @see java.util.Date
 * @see java.util.Map
 *
 */

public class Host {
	protected static double wins = 0;
	protected static double undec = 0;
	private static double averageTime = 0;
	private final static double noGames = 1;
	protected static int argumentationSystemSize;
	public static void main(String[] Args){
		for(int i = 0; i<noGames; i++){ //control the number of games to be played
			long secs = (new Date().getTime());
			Playground playground = new Playground();
			long newsecs = (new Date().getTime());
//			System.out.println((newsecs-secs));
			averageTime += (newsecs-secs);
		}
		evaluate();
	}
	
	protected static void evaluate(){
		System.out.println("Argumentation system's size is: " + argumentationSystemSize);
		System.out.println("Total runs: " + noGames);
		System.out.println("Proponent won: " + (wins/noGames)*100 + "% of the games");
		System.out.println("Opponent won: " + ((noGames-(wins+undec))/noGames)*100 + "% of the games");
		System.out.println("and " + (undec/noGames)*100 +"% ended as undec.");
		System.out.println("Average time played is: " + averageTime/noGames);
	}
	
	/**
	 * A function which populates the attacks for a given knowledge base
	 * 
	 * @param argumentList - the list of arguments which need to have their attacks populated
	 * @return attackRelation - the attack relation
	 */
	public static Map<Argument, ArrayList<Argument>> populateAttacks(ArrayList<Argument> argumentList){	
			Map<Argument, ArrayList<Argument>> attackRelation = new HashMap<Argument, ArrayList<Argument>>();
			for(Argument arg: argumentList){
				for(Argument attackedArg: arg.argumentsIamAttacking){
					// a knowledge base does not have to know all attack relations
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
		return attackRelation;
	}
	
	/**
	 * Includes the first element to a collection on a given slot
	 * @param collection - specifies where the element is being included
	 * @param key - the slot
	 * @param element - the element itself
	 */
	protected static <K,V> void includeFirstElement(Map<K, ArrayList<V>> collection, K key, V element){
		ArrayList<V> turns = new ArrayList<V>();
		collection.put(key, turns);
		turns.add(element);
	}
	
	/**
	 * Manages the including to a collection which already has its first 
	 * element included on a given slot
	 * @param collection - specifies where the element is being included
	 * @param key - the slot
	 * @param element - the element itself
	 */
	protected static <K, V> void includeElement(Map<K, ArrayList<V>> collection, K key, V element){
		ArrayList<V> turns = new ArrayList<V>();
		turns = collection.get(key);
		turns.add(element);
	}
	
	/**
	 * Defines a new collection
	 * @param collection - the newly created collection
	 * @param values - the key slots to be included
	 */
	protected static <K, V> void initiateCollection(Map<K, ArrayList<V>> collection,K... values){
		ArrayList<V> initial = new ArrayList<V>();
		for(K val: values){
			collection.put(val, initial);
		}
	}
	
	/**
	 * Populates an ArrayList
	 * @param toPopulate - the list that needs to be populated
	 * @param with - the values with which the list is populated
	 */
	protected static void populateArrayList(ArrayList<Argument> toPopulate, ArrayList<Argument> with){
		for(int i=0; i< with.size(); i++){
			toPopulate.add(with.get(i));
		}
	}
	
	/**
	 * Copy hash map to another hash map
	 * 
	 * @param original - the map being copied
	 * @return copy - the new copy
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
	 * @param time - how long to sleep in ms
	 */
	 protected static void sleep(long time){
	    try {
	           Thread.sleep(time);
	       } catch (InterruptedException e) {
	           e.printStackTrace();
	       }
	  }
}
