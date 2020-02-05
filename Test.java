package lindenmayer;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Test {
	public static void main(String[] args) {
		LSystem ls = new LSystem();
		TurtleBidon turtle = new TurtleBidon();
		ls.addSymbol("d".charAt(0));
		System.out.println("symbole ajouté à l'alphabet : "+ls.alphabet.get("d".charAt(0)).getSym());
		ls.addRule(new Symbol("d".charAt(0)), "dd");
		System.out.println("règle ajouté à rules : "+ls.rules.get(new Symbol("d".charAt(0))));
		for (Symbol name: ls.rules.keySet()){
            char key = name.getSym();
            String value = ls.rules.get(name).toString();  
            System.out.println(key + " : " + value);  
} 
		
		
		
	}
}
