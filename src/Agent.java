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
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for defining an agent,
 * also known as player for the game
 * @author Todor Davchev
 * @version 2.3 rtm
 * @see java.util.ArrayList
 * @see java.util.Map
 * @see N. Oren, M. Thimm and T. Rienstra - Uncertainty for strategic argumentation
 *
 */

class Agent {
	public Map<Argument, ArrayList<Argument>> attackRelation =
			new HashMap<Argument, ArrayList<Argument>>(); // where key= argument; content= attackers
	KnowledgeBase kb; // Agent's knowledge
	String name; // Agent's name
	Map<Agent, Double> opp = new HashMap<Agent, Double>(); // collection of opponents
	public Agent(KnowledgeBase kb, String name){
		this.kb = kb;
		this.name = name;
		if(kb != null)
			attackRelation = Host.populateAttacks(kb.arguments);
	}
	
	/**
	 * Updates the state of this agent
	 * @param arg - the newly added argument
	 */
	protected void update(Argument arg){
		kb.includeArgument(arg);
		for(Agent model: opp.keySet()){
			model.kb.includeArgument(arg);
			model.attackRelation.clear();
			model.attackRelation = Host.populateAttacks(model.kb.arguments);
		}
		attackRelation.clear();
		attackRelation = Host.populateAttacks(kb.arguments);
	}
	
	/**
	 * Includes opponent model for managing uncertainty
	 * @param probability - the likelihood of the model
	 * @param oppModel - the model itself
	 */
	public void opponentModel(double probability, Agent oppModel){
		opp.put(oppModel, probability);
	}
}
