import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class Playground {
	private ArrayList<Argument> pi = new ArrayList<Argument>();
	Agent pro, opp, prosModelA, prosModelB, oppsModelA, myself, undec;
	int oMoves = 0;
	private Map<String, ArrayList<Argument>> personalDecisions = new HashMap<String, ArrayList<Argument>>();
	ArgumentationSystem as = new ArgumentationSystem();
	int counter = 0; //checks to see if its planner's or opponent's turn
	
	public Playground(){
		ArrayList<Argument> argumentList = new ArrayList<Argument>();
		String winner;
		argumentList.add(as.argumentList.get(0));
		for(int i =0; i<5; ++i){
			argumentList.add(as.argumentList.get(i));
		}
		for(int i =5; i<as.argumentList.size(); ++i){
			argumentList.add(as.argumentList.get(i));
		}
		KnowledgeBase kb = new KnowledgeBase(argumentList);
		pro = new Agent(kb, "proponent");
		undec = new Agent(kb, "undecided");
		ArrayList<Argument> argumentList2 = new ArrayList<Argument>();
		for(int i =0; i<20; ++i){
			argumentList2.add(as.argumentList.get(i));
		}
		KnowledgeBase kbOpp1 = new KnowledgeBase(argumentList2);
		prosModelA = new Agent(kbOpp1, "modelA");
		ArrayList<Argument> argumentList3 = new ArrayList<Argument>();
		for(int i =3; i<as.argumentList.size(); ++i){
			argumentList3.add(as.argumentList.get(i));
		}
		argumentList3.add(as.argumentList.get(0));
		KnowledgeBase kbOpp2 = new KnowledgeBase(argumentList3);
		prosModelB = new Agent(kbOpp2, "modelB");
		pro.opponentModel(0.5, prosModelA); pro.opponentModel(0.5, prosModelB);
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
					moveWithUtil = minimax(pi, 35, opp, null, legalmoves, -99999, 99999);
//					moveWithUtil = uMStar(pi, 35, opp, null, legalmoves);
//					moveWithUtil = mStar(pi, 35, opp, null, legalmoves);
					if(!moveWithUtil.isEmpty()){
						for(Argument arg:moveWithUtil.keySet()){
							move = arg;
						}
						if((move != pi.get(pi.size()-1))
								&& move!=null 
								&& pi.size()>1){
							loser = winner;
							winner = opp;
						}
					}
					else if(moveWithUtil.isEmpty() 
							&& move == null){
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
					moveWithUtil = minimax(pi, 35, pro, null, legalmoves, -99999, 99999);
//					moveWithUtil = uMStar(pi, 35, pro, null, legalmoves);
//					moveWithUtil = mStar(pi, 35, pro, null, legalmoves);
					for(Argument arg:moveWithUtil.keySet()){
						move = arg; // if more than one appropriate argument pick any
						break;
					}
					if((move != pi.get(pi.size()-1))
							&&move!=null){
						loser = winner;
						winner = pro;
					}
				}
			}
			if(move != null){
				loser.update(move);
				System.out.println(winner.name + " says -> " + move.name);
				Host.sleep(100);
			}
			if(pi.contains(move))
				winner = undec;
		}while(loser.attackRelation.get(move)!=null  
				&& (!pi.contains(move)));
		return winner.name;
	}
	
	/**
	 * All arguments attacking the previous move are legal
	 * apart from the ones already played.
	 * This function supports indecisiveness.
	 * 
	 * @param agent
	 * @param move
	 * @return
	 */
	private ArrayList<Argument> findLegalmoves(Agent agent, Argument move, ArrayList<Argument> listOfAlreadySaidArguments){
		ArrayList<Argument> legalmoves = new ArrayList<>();
		if(agent.attackRelation.get(move) != null){
			for(Argument arg: agent.attackRelation.get(move)){
				if(!listOfAlreadySaidArguments.contains(arg) 
						&& !legalmoves.contains(arg)){
					legalmoves.add(arg);
				}
				else if(listOfAlreadySaidArguments.size()>1){
					if(listOfAlreadySaidArguments.get(listOfAlreadySaidArguments.size()-2).equals(arg)){ // support the option for indecisiveness
						legalmoves.add(arg);
					}
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
//		System.err.println("Last played argument is: " + dialogue.get(dialogue.size()-1).name);
		ArrayList<Argument> legalmoves = new ArrayList<>();
		ArrayList<Argument> tempDialogue = new ArrayList<Argument>();
		if(legalMoves.isEmpty() || depth == 0){
			score = evaluationFunction(dialogue, player, opponent);
			maxMoveMaxUtil.put(null, score);
		}
		else{
			System.out.print("");
//			for(int i=0; i < legalMoves.size();++i){System.out.println("legalmoves upon calling function is " + legalMoves.get(i).name); Host.sleep(100);}
			Host.populateArrayList(tempDialogue, dialogue);
			if(player==myself){
				for(Agent opponentModel: player.opp.keySet()){
					legalMoves = findLegalmoves(opponentModel, tempDialogue.get(tempDialogue.size()-1), tempDialogue);
					for(Argument move: legalMoves){
//						System.out.println("------> "+ move.name);
						tempDialogue.add(move);
						legalmoves = findLegalmoves(player, move, tempDialogue);
//						for(int i=0; i < legalmoves.size();++i){System.out.println("legalmove for " + move.name + " is " + legalmoves.get(i).name); Host.sleep(100);}
						maxMoveMaxUtil = minimax(tempDialogue, depth-1, opponentModel, myself, legalmoves, alpha, beta);
						for(Argument key: maxMoveMaxUtil.keySet()){
							score = maxMoveMaxUtil.get(key);
						}
						if(score > alpha) {
							alpha = score;
							bestMove = tempDialogue.get(tempDialogue.size()-1);
//							System.err.println(bestMove.name);
						}
						tempDialogue.remove(move);
						if(alpha>=beta) break;
					}
				}
			}
			else{
				legalMoves = findLegalmoves(player, tempDialogue.get(tempDialogue.size()-1), tempDialogue); //see legal moves for current model only
				for(Argument move: legalMoves){
					tempDialogue.add(move);
					legalmoves = findLegalmoves(player, move, tempDialogue);
					maxMoveMaxUtil = minimax(tempDialogue, depth-1, myself, player, legalmoves, alpha, beta);
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
	
	
	private Map<Argument, Double> uMStar(ArrayList<Argument> dialogue, int depth, Agent agent, Agent opponent, ArrayList<Argument> legalMoves){
		double playUtil = 0; // player on turn's utility
		double maxUtil = -9999;
		Map<Argument, Double> maxMoveMaxUtil = new HashMap<Argument, Double>(); 
		Map<Argument, Double> proMoveProUtil = new HashMap<Argument, Double>();
//		for(int i =0; i< legalMoves.size();++i){System.out.println("on entering um* -> legalmove: " + legalMoves.get(i).name);}Host.sleep(100);
		Argument maxMove = null; // move with maximised utility
		ArrayList<Argument> tempDialogue = new ArrayList<Argument>(); //secures the dialogue + M and dialogue +M +M' from paper
		if(legalMoves.isEmpty() ||depth == 0){
			playUtil = evaluationFunction(dialogue, agent, opponent);
			maxMoveMaxUtil.put(null, playUtil);
		}
		else{
			Host.populateArrayList(tempDialogue, dialogue);
			for(Argument move: legalMoves){
				playUtil=0;
				tempDialogue.add(move);
				if(depth==1){
					playUtil = evaluationFunction(tempDialogue, agent, opponent);
				}
				else{
					for(Agent agentOpp: agent.opp.keySet()){
						ArrayList<Argument> legalmoves = findLegalmoves(agentOpp, move, tempDialogue);
						for(int i =0; i< legalmoves.size();++i){if(legalmoves.get(i).name.equals("ab"))System.out.println("Success");}
						Map<Argument, Double> oppMoveOppUtil = new HashMap<Argument, Double>();
						Argument oppMove = null;
						oppMoveOppUtil = uMStar(tempDialogue, depth-1, agentOpp, agent, legalmoves);
						for(Argument key: oppMoveOppUtil.keySet()){
							oMoves++;
							oppMove = key;
							tempDialogue.add(oppMove);
							legalmoves=findLegalmoves(agent, oppMove, tempDialogue);
							proMoveProUtil = uMStar(tempDialogue, depth-2, agent, agentOpp, legalmoves);
							for(Argument keyTwo: proMoveProUtil.keySet()){
							playUtil = proMoveProUtil.get(keyTwo);
							if(playUtil>maxUtil){ //might return more than one answer
								maxMoveMaxUtil.clear();
								maxUtil = playUtil;
								maxMove = move;
								maxMoveMaxUtil.put(maxMove, maxUtil);
							}
						}
						}
					}					
				}
				if(playUtil > maxUtil){
					maxMoveMaxUtil = clearList(maxMoveMaxUtil);
				}
				if(playUtil >= maxUtil){
					maxUtil = playUtil;
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
	
	/**
	 * no legalmoves update...
	 * @param dialogue
	 * @param depth
	 * @param agent
	 * @param legalMoves
	 * @return
	 */
	private Map<Argument, Double> mStar(ArrayList<Argument> dialogue, int depth, Agent agent, Agent opponent, ArrayList<Argument> legalMoves){
		double playUtil = 0; // player on turn's utility
		Map<Argument, Double> maxMoveMaxUtil = new HashMap<Argument, Double>();
		Map<Argument, Double> oppMoveOppUtil = new HashMap<Argument, Double>();
		Map<Argument, Double> proMoveProUtil = new HashMap<Argument, Double>();
		Argument maxMove = null; // move with maximised utility
		ArrayList<Argument> tempDialogue = new ArrayList<Argument>(); //secures the dialogue + M and dialogue +M +M' from paper
		if(legalMoves.isEmpty() ||depth == 0){
			playUtil = evaluationFunction(dialogue, agent, opponent);
			maxMoveMaxUtil.put(null, playUtil);
		}
		else{
			double maxUtil = -999999;
			Host.populateArrayList(tempDialogue, dialogue);
			for(Argument move: legalMoves){
				legalMoves = findLegalmoves(agent, move, tempDialogue);
				if(depth==1){
					tempDialogue.add(move);
					playUtil = evaluationFunction(tempDialogue, agent, opponent);
				}
				else{
					tempDialogue.add(move);
					for(Agent agentOpp: agent.opp.keySet()){
						Argument oppMove;
						oppMoveOppUtil = mStar(tempDialogue, depth-1, agentOpp, agent, legalMoves);
						for(Argument key: oppMoveOppUtil.keySet()){
							oppMove = key;
							tempDialogue.add(oppMove);
							proMoveProUtil = mStar(tempDialogue, depth-2, agent, agentOpp, legalMoves);
							for(Argument keyTwo: proMoveProUtil.keySet()){
								playUtil = proMoveProUtil.get(keyTwo);
								if(playUtil>maxUtil){ //might return more than one answer
									maxMoveMaxUtil.clear();
									maxUtil = playUtil;
									maxMove = move;
									maxMoveMaxUtil.put(maxMove, maxUtil);
								}
							}
						}
					}					
				}
				if(playUtil>maxUtil){
					maxMoveMaxUtil.clear();
					maxUtil = playUtil;
					maxMove = move;
					maxMoveMaxUtil.put(maxMove, maxUtil);
				}
			}
		}
		return maxMoveMaxUtil;
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
		double bla;
		int counter=1;
		double opponentMoves=0.0; 
		if((pi.size()&1)!=0)
			v = -1.0;
		else
			v = 1.0;
		for(Argument argument: dialogue){
			if((counter&1) == 0){
				opponentMoves = opponentMoves + 1.0;
			}
			if(dialogue.size()>1 
					&& counter > 1){
				if(dialogue.get(counter-2).equals(argument)){
					score+=v/2.0;
				}
			}
			else{
				if(as.labelling.get("IN").contains(argument))
					score += v;
				else if(as.labelling.get("OUT").contains(argument))
					score -= -v;
				else
					score += 0;
			}
			counter++;
		}
		if(agent != myself){
			score += score*opponent.opp.get(agent)*(1.0/opponentMoves);
		}
		else if(opponent != null){
			score += score*agent.opp.get(opponent)*(1.0/opponentMoves);
		}
		return score;
	}
	
	/**
	 * Collects utility see Oren+Thimm ask if their is correct
	 * 
	 * @param dialogue
	 * @return utility
	 */
	private double collectUtility(ArrayList<Argument> dialogue, Agent agent, Agent opponent){
		int v = 0;
		double util = 0;
		int opponentMoves=0, counter=1;
		if((pi.size()&1)!=0)
			v = -1;
		else
			v = 1;
		for(Argument argument: dialogue){
			if((counter&1) == 0){
				opponentMoves++;
			}
			if(as.labelling.get("IN").contains(argument))
				util += v;
			else if(as.labelling.get("OUT").contains(argument))
				util -= -v;
			else
				util += 0;
			counter++;
		}
		if(agent == myself 
				&& opponent != null){
			util += util*agent.opp.get(opponent)*(1.0/opponentMoves);
		}
		return util;
	}
	

}
