import java.util.ArrayList;


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
 
 public void incrAttackersOut(){
	 attackersOut++;
 }

 public void attacks(Argument plusOne){
	 argumentsIamAttacking.add(plusOne);
 }
 
 public ArrayList<Argument> argumentsIamAttacking(){
	 return argumentsIamAttacking;
 }
 
 public void setUtility(int utility){
	 this.utility=utility;
 }
 
 public int getUtility(){
	 return utility;
 }
 
}
