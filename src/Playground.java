import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class Playground {
	private ArrayList<Argument> pi = new ArrayList<Argument>();
	Agent pro, opp, prosModelA, prosModelB, oppsModelA, myself;
	int oMoves = 0;
	private Map<String, ArrayList<Argument>> personalDecisions = new HashMap<String, ArrayList<Argument>>();
	ArgumentationSystem as = new ArgumentationSystem();
	int counter = 0; //checks to see if its planner's or opponent's turn
	
	public Playground(){
		ArrayList<Argument> argumentList = new ArrayList<Argument>();
		String winner;
		for(int i =0; i<5; ++i){
			argumentList.add(as.argumentList.get(i));
		}
		for(int i =5; i<as.argumentList.size(); ++i){
			argumentList.add(as.argumentList.get(i));
//			System.err.println(as.argumentList.get(i).name);
		}
		KnowledgeBase kb = new KnowledgeBase(argumentList);
		pro = new Agent(kb, "proponent");
		ArrayList<Argument> argumentList2 = new ArrayList<Argument>();
//		argumentList.clear();
		for(int i =0; i<5; ++i){
			argumentList2.add(as.argumentList.get(i));
		}
		KnowledgeBase kbOpp1 = new KnowledgeBase(argumentList2);
		prosModelA = new Agent(kb, "modelA");
//		argumentList.clear();
		ArrayList<Argument> argumentList3 = new ArrayList<Argument>();
		for(int i =4; i<as.argumentList.size(); ++i){
			argumentList3.add(as.argumentList.get(i));
		}
		argumentList3.add(as.argumentList.get(0)); argumentList3.add(as.argumentList.get(1));
		KnowledgeBase kbOpp2 = new KnowledgeBase(argumentList3);
		prosModelB = new Agent(kbOpp2, "modelB");
		pro.opponentModel(1.0, prosModelA);// pro.opponentModel(0.0, prosModelB);
		ArrayList<Argument> argumentList4 = new ArrayList<Argument>();
		for(int i =0; i<as.argumentList.size(); ++i){
			argumentList4.add(as.argumentList.get(i));
		}
		Host.sleep(100);
		argumentList4.add(as.argumentList.get(0)); argumentList4.add(as.argumentList.get(1));
		KnowledgeBase kbOpp = new KnowledgeBase(argumentList4);
		opp = new Agent(kbOpp, "opponent");
		oppsModelA = new Agent(kbOpp, "oppModelA");
		opp.opponentModel(1.0, oppsModelA);Host.sleep(100);
		System.out.println(pro.name + " says -> " + as.argumentList.get(0).name);
		winner = play(as.argumentList.get(0));
		System.out.println("Winner is: " + winner);
	}
	
	private String play(Argument move){
		Agent winner = opp;
		Agent loser = pro;
		Host.initiateCollection(personalDecisions, pro.name, opp.name);
		ArrayList<Argument> legalmoves = new ArrayList<>();
		do{
			pi.add(move);
			if((pi.size()&1)!=0){// if odd opponent's turn
				myself = opp;
				legalmoves = findLegalmoves(opp, move, pi);
				if(legalmoves != null){
					Map<Argument, Double> moveWithUtil = new HashMap<Argument, Double>();
					moveWithUtil = minimax(pi, 9, opp, null, legalmoves, -99999, 99999);
//					moveWithUtil = uMStar(pi, 9, opp, null, legalmoves);
//					move = mStar(pi, 9, opp, null, legalmoves);
					if(!moveWithUtil.isEmpty()){
						for(Argument arg:moveWithUtil.keySet()){
							move = arg;
						}
						if((move != pi.get(pi.size()-1))&&move!=null){
							loser = winner;
							winner = opp;
						}
					}
					else if(moveWithUtil.isEmpty() && move == null){
						winner = pro;
						loser = opp;
					}
				}
			}
			else{// proponent's turn
				myself = pro;
				legalmoves = findLegalmoves(pro, move, pi);
				Map<Argument, Double> moveWithUtil = new HashMap<Argument, Double>();
				if(legalmoves != null){
					moveWithUtil = minimax(pi, 9, pro, null, legalmoves, -99999, 99999);
//					moveWithUtil = uMStar(pi, 9, pro, null, legalmoves);
//					move = mStar(pi, 9, pro, null, legalmoves);
					for(Argument arg:moveWithUtil.keySet()){
						move = arg;
						break;
					}
					if((move != pi.get(pi.size()-1))&&move!=null){
						loser = winner;
						winner = pro;
					}
				}
			}
			if(move != null){
			loser.update(move);
			System.out.println(winner.name + " says -> " + move.name);Host.sleep(100);}
		}while(loser.attackRelation.get(move)!=null  
				&& (!pi.contains(move)));
		return winner.name;
	}
	
	/**
	 * All arguments attacking the previous move are legal
	 * apart from the ones already played.
	 * It is currently not tracing back should there be no legalmoves
	 * to advance given the current one.
	 * 
	 * @param agent
	 * @param move
	 * @return
	 */
	private ArrayList<Argument> findLegalmoves(Agent agent, Argument move, ArrayList<Argument> listOfAlreadySaidArguments){
		// drives off the model with insufficient arguments to win
		// if I want to stick to the higher chance of invalidness of a model and 
		// thus not to drive the model with insufficientness take the listOfAlreadySaidArguments and swap with pi
		ArrayList<Argument> legalmoves = new ArrayList<>();
		if(agent.attackRelation.get(move) != null){
		for(Argument arg: agent.attackRelation.get(move)){
			if(!listOfAlreadySaidArguments.contains(arg) && !legalmoves.contains(arg)){
				legalmoves.add(arg);
			}
		}
		}
		return legalmoves;
	}
	
	private Map<Argument, Double> minimax(ArrayList<Argument> dialogue, int depth, Agent player, 
			Agent opponent, ArrayList<Argument> legalMoves, double alpha, double beta){
		double score = -9999;
		Argument bestMove = null;
		Map<Argument, Double> maxMoveMaxUtil = new HashMap<Argument, Double>();
		ArrayList<Argument> tempDialogue = new ArrayList<Argument>();
		if(legalMoves.isEmpty() || depth == 0){
			score = evaluationFunction(dialogue, player, opponent);
			maxMoveMaxUtil.put(null, score);
		}
		else{
			Host.populateArrayList(tempDialogue, dialogue);
			if(player==myself){
				for(Agent opponentModel: player.opp.keySet()){
					legalMoves = findLegalmoves(opponentModel, tempDialogue.get(tempDialogue.size()-1), tempDialogue);
					for(Argument move: legalMoves){
						tempDialogue.add(move);
						legalMoves = findLegalmoves(player, move, tempDialogue);
						maxMoveMaxUtil = minimax(tempDialogue, depth-1, opponentModel, myself, legalMoves, alpha, beta);
						for(Argument key: maxMoveMaxUtil.keySet()){
							score = maxMoveMaxUtil.get(key);
						}
						if(score > alpha) {
							alpha = score;
							bestMove = tempDialogue.get(tempDialogue.size()-1);
						}
						tempDialogue.remove(move);
						if(alpha>=beta) break;
					}
				}
			}
			else{
				legalMoves = findLegalmoves(player, tempDialogue.get(tempDialogue.size()-1), tempDialogue);
				for(Argument move: legalMoves){
					tempDialogue.add(move);
					legalMoves = findLegalmoves(player, move, tempDialogue);
					maxMoveMaxUtil = minimax(tempDialogue, depth-1, myself, player, legalMoves, alpha, beta);
					for(Argument key: maxMoveMaxUtil.keySet()){
						score = maxMoveMaxUtil.get(key);
					}
					if(score < beta) {
						beta = score;
						bestMove = tempDialogue.get(tempDialogue.size()-1);
					}
					tempDialogue.remove(move);
					if(alpha >= beta) break;
				}
			}	
			maxMoveMaxUtil.clear();
			maxMoveMaxUtil.put(bestMove, (player==myself)?alpha:beta);
		}
		return maxMoveMaxUtil;
	}
	
	
	private Map<Argument, Double> uMStar(ArrayList<Argument> dialogue, int depth, Agent agent, ArrayList<Argument> legalMoves){
		double playUtil = 0; // player on turn's utility
		double maxUtil = -9999;
		Map<Argument, Double> maxMoveMaxUtil = new HashMap<Argument, Double>(); 
		Argument maxMove = null; // move with maximised utility
		ArrayList<Argument> tempDialogue = new ArrayList<Argument>(); //secures the dialogue + M and dialogue +M +M' from paper
		if(depth == 0){
			playUtil = collectUtility(dialogue, agent);
		}
		else{
			Host.populateArrayList(tempDialogue, dialogue);
			for(Argument move: legalMoves){
				playUtil=0;
				tempDialogue.add(move);
				if(depth==1){
					playUtil = collectUtility(tempDialogue, agent);
				}
				else{
					for(Agent agentOpp: agent.opp.keySet()){
						ArrayList<Argument> legalmoves = findLegalmoves(agentOpp, move, tempDialogue);
						Map<Argument, Double> oppMoveOppUtil = new HashMap<Argument, Double>();
						Argument oppMove = null;
						oppMoveOppUtil = uMStar(tempDialogue, depth-1, agentOpp, legalmoves);
						int oMoves = 0;
						for(Argument key: oppMoveOppUtil.keySet()){
							oMoves++;
							oppMove = key;
						}
						tempDialogue.add(oppMove);
						legalmoves=findLegalmoves(agent, oppMove, tempDialogue);
						Map<Argument, Double> proMoveProUtil = new HashMap<Argument, Double>();
						proMoveProUtil = uMStar(tempDialogue, depth-2, agent, legalmoves);
						for(Argument key: proMoveProUtil.keySet()){
							playUtil += proMoveProUtil.get(key)*agent.opp.get(agentOpp)*(1/oMoves);
						}
					}					
				}
				if(playUtil > maxUtil){
					maxMoveMaxUtil = clearList(maxMoveMaxUtil);
				}
				if(playUtil >= maxUtil){
					maxUtil = playUtil;
					maxMove = move;
					maxMoveMaxUtil.put(maxMove, maxUtil);
				}
			}
		}
		return maxMoveMaxUtil;
	}
	
	private Map<Argument, Double> clearList(Map<Argument, Double> tuple){
		 for(Iterator<Entry<Argument, Double>> it = tuple.entrySet().iterator(); it.hasNext(); ) {
		      Entry<Argument, Double> entry = it.next();
		        it.remove();
		 }
		 return tuple;
	}
	
	private Argument mStar(ArrayList<Argument> dialogue, int depth, Agent agent, ArrayList<Argument> legalMoves){
		int playUtil = 0; // player on turn's utility
		int maxUtil = -999999;
		Argument maxMove = null; // move with maximised utility
		ArrayList<Argument> tempDialogue = new ArrayList<Argument>(); //secures the dialogue + M and dialogue +M +M' from paper
		if(depth == 0){
			playUtil = collectUtility(dialogue, agent);
		}
		else{
			Host.populateArrayList(tempDialogue, dialogue);
			for(Argument move: legalMoves){
				if(depth==1){
					tempDialogue.add(move);
					playUtil = collectUtility(tempDialogue, agent);
				}
				else{
					tempDialogue.add(move);
					for(Agent agentOpp: agent.opp.keySet()){
						Argument oppMove = mStar(tempDialogue, depth-1, agentOpp, legalMoves);
						tempDialogue.add(oppMove);
						mStar(tempDialogue, depth-2, agent, legalMoves);
					}					
				}
				if(playUtil>maxUtil){
					maxUtil = playUtil;
					maxMove = move;
				}
			}
		}
		return maxMove;
	}
	
	/**
	 * Collects utility see Oren+Thimm
	 * 
	 * @param dialogue
	 * @return utility
	 */
	private double evaluationFunction(ArrayList<Argument> dialogue, Agent agent, Agent opponent){
		double v = 0;
		double score = 0;
		if((pi.size()&1)!=0)
			v = -1;
		else
			v = 1;
		for(Argument argument: dialogue){
			if(as.labelling.get("IN").contains(argument))
				score += v;
			else if(as.labelling.get("OUT").contains(argument))
				score -= -v;
			else
				score += 0;
		}
		if(agent != myself) 
			score = score*opponent.opp.get(agent);
		else if(opponent != null){
			score = score*agent.opp.get(opponent);
		}
		return score;
	}
	
	/**
	 * Collects utility see Oren+Thimm
	 * 
	 * @param dialogue
	 * @return utility
	 */
	private int collectUtility(ArrayList<Argument> dialogue, Agent agent){
		int v = 0;
		int util = 0;
		if((pi.size()&1)!=0)
			v = -1;
		else
			v = 1;
		for(Argument argument: dialogue){
			if(as.labelling.get("IN").contains(argument))
				util += v;
			else if(as.labelling.get("OUT").contains(argument))
				util -= -v;
			else
				util += 0;
		}
		return util;
	}
	

}
