import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class KnowledgeBase {
	Map<String, Map<Argument, ArrayList<Argument>>> attackRelation = new HashMap<String, Map<Argument, ArrayList<Argument>>>();
	ArrayList<Argument> arguments = new ArrayList<Argument>();
	String name;
	protected KnowledgeBase(ArrayList<Argument> arguments){
		this.arguments = arguments;
	}
	
	protected void includeArgument(Argument arg){
		if(!arguments.contains(arg))
		arguments.add(arg);
	}
}
