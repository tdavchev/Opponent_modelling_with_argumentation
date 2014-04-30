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

/**
 * The class defines an abstract argument.
 * @author Todor Davchev
 * @version 2.3 rtm
 * @see java.util.ArrayList
 * @see M. Dung - On the acceptability of argumentation and its fundamental role in nonmonotonicreasoning, logic programming and n-person games.
 *
 */
public class Argument {
	public String name;
	public int utility=1;
	public int attackersOut;
	public ArrayList<Argument> argumentsIamAttacking = new ArrayList<Argument>();
 public Argument(String name){
	 this.name=name;
 }
 
 public String getName(){
	 return name;
 }
 
 /**
  * Keep track of attackers labelled as OUT
  * as suggested by M. Caminada and S. Modgil
  * @see On the evaluation of argumentation formalisms.
  */
 public void incrAttackersOut(){
	 attackersOut++;
 }

 /**
  * Keep track of arguments this argument is attacking
  * @param plusOne - the newly added argument attacked
  */
 public void attacks(Argument plusOne){
	 argumentsIamAttacking.add(plusOne);
 }
 
 /**
  * Returns a list of all arguments being attacked by this argument
  * @return argumentsIamAttacking
  */
 public ArrayList<Argument> argumentsIamAttacking(){
	 return argumentsIamAttacking;
 } 
}
