import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class Agent {
	public Map<Argument, ArrayList<Argument>> attackRelation = new HashMap<Argument, ArrayList<Argument>>(); // key= argument; content= attackers
	double goals; // to be discussed
	KnowledgeBase kb;
	String name;
	Map<Agent, Double> opp = new HashMap<Agent, Double>();
	public Agent(KnowledgeBase kb, String name){
		this.kb = kb;
		this.name = name;
		attackRelation = Host.populateAttacks(kb.arguments);
//		collectUnattacked();
//		label();
	}

	protected void update(Argument arg){
		kb.includeArgument(arg);
		for(Agent model: opp.keySet()){
			model.kb.includeArgument(arg);
			model.attackRelation.clear();
			model.attackRelation = Host.populateAttacks(model.kb.arguments);
//			collectUnattacked();
//			label();
		}
		attackRelation.clear();
		attackRelation = Host.populateAttacks(kb.arguments);
//		collectUnattacked();
//		label();
	}
	
	public void opponentModel(double probability, Agent oppModel){
		opp.put(oppModel, probability);
	}
}
