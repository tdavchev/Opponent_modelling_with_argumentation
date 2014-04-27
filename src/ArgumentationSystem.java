import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class ArgumentationSystem {
	public Map<Argument, ArrayList<Argument>> attackRelation = new HashMap<Argument, ArrayList<Argument>>(); // key= argument; content= attackers
	protected Map<String, ArrayList<Argument>> labelling = new HashMap<String, ArrayList<Argument>>();
	public ArrayList<Argument> nonAttacked = new ArrayList<Argument>();
	ArrayList<Argument> argumentList= new ArrayList<Argument>();
	KnowledgeBase kb = new KnowledgeBase(argumentList);
	Scanner scannera = new Scanner(System.in);
	private String letter;
	public ArgumentationSystem(){
		defineArguments(kb);
		attackRelation = Host.populateAttacks(kb.arguments);
		collectUnattacked();
		label();
		printLabelling();
	}
	
	public void defineArguments(KnowledgeBase kb){
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		Argument g = new Argument("g");
		Argument h = new Argument("h");
		Argument i = new Argument("i");
		Argument j = new Argument("j");
		Argument k = new Argument("k");
		Argument l = new Argument("l");
		Argument m = new Argument("m");
		Argument n = new Argument("n");
		Argument o = new Argument("o");
		Argument p = new Argument("p");
		Argument q = new Argument("q");
		Argument r = new Argument("r");
		Argument s = new Argument("s");
		Argument t = new Argument("t");
		Argument u = new Argument("u");
		Argument v = new Argument("v");
		Argument w = new Argument("w");
		Argument x = new Argument("x");
		Argument y = new Argument("y");
		Argument z = new Argument("z");
		Argument ab = new Argument("ab");
		argumentList.add(a); argumentList.add(b);
		argumentList.add(c); argumentList.add(d);
		argumentList.add(e); argumentList.add(f);
		argumentList.add(g); argumentList.add(h);
		argumentList.add(i); argumentList.add(j);
		argumentList.add(k); argumentList.add(l);
		argumentList.add(m); argumentList.add(n);
		argumentList.add(o); argumentList.add(p);
		argumentList.add(q); argumentList.add(r);
		argumentList.add(s); argumentList.add(t);
		argumentList.add(u); argumentList.add(v);
		argumentList.add(w); argumentList.add(x);
		argumentList.add(y); argumentList.add(z);
		argumentList.add(ab);
		// tree 1
//		b.attacks(a); 
//		c.attacks(b);
//		d.attacks(b); e.attacks(d);
//		c.attacks(d); d.attacks(c);
//		f.attacks(b); g.attacks(f); h.attacks(g);
		// end of tree 1
		//tree 2
		b.attacks(a); c.attacks(b); d.attacks(c); e.attacks(c);
		f.attacks(d); g.attacks(d); h.attacks(d);
		i.attacks(f);
		j.attacks(a); k.attacks(j); l.attacks(j);
		m.attacks(a); n.attacks(m); o.attacks(m); p.attacks(o); q.attacks(o);
//		//tree 3 cont'd 2
		r.attacks(q); s.attacks(q); t.attacks(r); u.attacks(r); v.attacks(r); w.attacks(t); x.attacks(t); y.attacks(x);
		z.attacks(u); ab.attacks(u);
		System.out.println("Argumentation System size: " + argumentList.size());
	}
	
	private void collectUnattacked(){
		for(Argument member:kb.arguments){
			if(!attackRelation.containsKey(member)){
				nonAttacked.add(member);
			}
		}
	}
	
	/**
	 * This method is built as advised by S. Modgil and M. Caminada
	 * The idea is to label all arguments involved in a game and devise a grounded extension
	 * for the game.
	 */
	public void label(){
		Map<String, ArrayList<Argument>> temp = new HashMap<String, ArrayList<Argument>>();
		Host.initiateCollection(labelling, "IN", "OUT", "UNDEC");			
		temp = Host.deepCopy(labelling);
		Host.sleep(100);
		// Assign IN to all arguments that are not attacked
		if(nonAttacked.size()>0){
			temp = processNonAttacked(temp);
			Host.sleep(100);
		}
		// assign labels to the rest of the arguments
		processTheOthers(temp);
//		printLabelling();
	}
	
	private Map<String, ArrayList<Argument>> processNonAttacked(Map<String, ArrayList<Argument>> collection){
		for(Argument member: nonAttacked){
			Host.includeElement(collection, "IN", member);
		}
		return collection;
	}
	
	private void processTheOthers(Map<String, ArrayList<Argument>> collection){
		do{
			labelling = Host.deepCopy(collection);
			// get all arguments which are being attacked
			for(Argument arg:attackRelation.keySet()){
				// if argument is not member of labelling
				if((!labelling.get("IN").contains(arg)) 
						&& (!labelling.get("OUT").contains(arg)) 
								&& (!labelling.get("UNDEC").contains(arg))){
					// and all its attackers are labelled as OUT
					boolean allAreOut = true;
					for(Argument attacker: attackRelation.get(arg)){
						if(!collection.get("OUT").contains(attacker))
							allAreOut = false;
					}
					if(allAreOut)
						Host.includeElement(collection, "IN", arg);
					//any argument attacked by one or more from IN is labelled as OUT
					for(Argument attacker:collection.get("IN")){
						if(attackRelation.get(arg).contains(attacker) 
								&& (!collection.get("OUT").contains(arg))){
							Host.includeElement(collection, "OUT", arg);
						}
					}
				}
			}
		}while(!labelling.equals(collection));
		// Process what is left as undecided
		processUNDEC();
	}
	
	private void processUNDEC(){
		for(Argument rest: kb.arguments){
			if(!labelling.get("IN").contains(rest) 
					&& !labelling.get("OUT").contains(rest)){
				Host.includeElement(labelling, "UNDEC", rest);
			}
		}
	}
	
	private void printLabelling(){
		for(String label:labelling.keySet()){
			System.err.print(label + ": "); Host.sleep(100);
			ArrayList<Argument> members = new ArrayList<Argument>();
			members = labelling.get(label);
			for(int i =0; i<members.size(); ++i){
				System.out.print(members.get(i).name+", ");
			}
			System.out.println();
			Host.sleep(100);
		}
	}
}
