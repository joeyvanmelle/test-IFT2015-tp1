package lindenmayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.lang.reflect.InvocationTargetException;
import org.json.*;
public class LSystem {
	 Symbol symbol;
	 Map<Character, Symbol> alphabet;
	 Map<Symbol, List<Iterator>> rules;
	 Map<Symbol, String> actions;
	 Iterator axiom;
	private boolean mustInit = false;
	private Point2D initPosition;
	private double initAngle;
    /**
     * constructeur vide monte un système avec alphabet vide et sans règles
     */
    public LSystem(){
    	symbol = null;
    	alphabet = (Map<Character, Symbol>) new HashMap<Character, Symbol>();
    	rules = (Map<Symbol, List<Iterator>>) new HashMap<Symbol, List<Iterator>>();
    	actions = (Map<Symbol, String>) new HashMap<Symbol, String>();
    	axiom = new ArrayList<Symbol>().iterator();
    }
    
    /* méthodes d'initialisation de système */
    public Symbol addSymbol(char sym) {
    	symbol = new Symbol(sym);
    	alphabet.put(sym, symbol);
    	return symbol;
    }
    
    public void addRule(Symbol sym, String expansion) {
    	char[] chars = expansion.toCharArray();
    	ArrayList<Symbol> list = new ArrayList<Symbol>();
    	for (char c : chars) {
    		list.add(alphabet.get(c));
    	}
    	List<Iterator> rule;
    	if (rules.get(sym) == null) {
    		 rule = (List<Iterator>) new ArrayList<Iterator>();
    	} else {
    		rule = rules.get(sym);
    	}
    	System.out.println("iter: "+list.iterator());
    	rule.add(list.iterator());
    	rules.put(sym, rule);
    }
    
    public void setAction(Symbol sym, String action) {
    	actions.put(sym, action);
    }
    
    public void setAxiom(String str){
    	char[] chars = str.toCharArray();
    	ArrayList<Symbol> list = new ArrayList<Symbol>();
    	for (char c : chars) {
    		list.add(alphabet.get(c));
    	}
    	axiom = list.iterator();
    }
 
    /* initialisation par fichier */
    public static void readJSONFile(String filename, LSystem system, Turtle turtle) throws java.io.IOException {
    	JSONObject input = new JSONObject(new JSONTokener(new java.io.FileReader(filename))); // lecture de fichier JSON avec JSONTokener
        JSONArray alphabet = input.getJSONArray("alphabet");
        JSONObject rules = input.getJSONObject("rules");
        JSONObject actions = input.getJSONObject("actions");
        String axiom = input.getString("axiom");
        JSONObject parameters = input.getJSONObject("parameters");
        JSONArray start = parameters.getJSONArray("start");
        system.setAxiom(axiom);
        for (int i=0; i<alphabet.length(); i++){
            String letter = alphabet.getString(i);
            Symbol sym = system.addSymbol(letter.charAt(0)); // un caractère
            JSONArray ruleTab = rules.getJSONArray(letter);
            for (int j=0; j<ruleTab.length(); j++) {
            	system.addRule(sym, ruleTab.getString(j));
            }
            system.setAction(sym, actions.getString(letter));
        }
        turtle.setUnits(parameters.getDouble("step"), parameters.getDouble("angle"));
        Point2D point = new Point2D.Double(start.getDouble(0), start.getDouble(1));
        turtle.init(point, start.getDouble(2));
        
    }
 
    /* accès aux règles et exécution */
    public Iterator getAxiom(){
    	return axiom;
    }
    public Iterator rewrite(Symbol sym) {
    	List<Iterator> rule = rules.get(sym);
    	return rule.get((int) Math.random() * rule.size());
    }
    
    public void tell(Turtle turtle, Symbol sym) {
    	try {
			turtle.getClass().getDeclaredMethod(actions.get(sym), null).invoke(turtle, null);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
    }
 
    /* opérations avancées */
    public Iterator applyRules(Iterator seq, int n) {
    	ArrayList<Symbol> list = new ArrayList<Symbol>();
    	while(seq.hasNext()) {
    		list.add((Symbol) seq.next());
    	}
    	for (int i=0; i<n; i++) {
    		for(int j=0; j<list.size(); j++) {
    			Iterator iter = rewrite(list.get(j));
    			ArrayList<Symbol> subList = new ArrayList<Symbol>();
    			while(iter.hasNext()) {
    				subList.add((Symbol) iter.next());
    			}
    			list.remove(j);
    			list.addAll(j, subList);
    		}
    	}
    	return list.iterator();
    }
    
    public void tell(Turtle turtle, Symbol sym, int rounds){
    	if (rounds == 0) {
    		tell(turtle, sym);
    	} else {
    		ArrayList<Symbol> list = new ArrayList<Symbol>();
    		list.add(sym);
    		Iterator iter = list.iterator();
    		iter = applyRules(iter, 1);
    		while (iter.hasNext()) {
    			tell(turtle, (Symbol) iter.next(), rounds-1);
    		}
    	}
    }
    
    public Rectangle2D getBoundingBox(Turtle turtle, Iterator seq, int n) {
    	Rectangle2D rect = new Rectangle2D.Double();
    	boolean firstIteration = false;
    	if (!mustInit) {
    		mustInit = true;
    		firstIteration = true;
    		initPosition = turtle.getPosition();
    		initAngle = turtle.getAngle();
    	}
    	if (n == 0) {
        	double height = 0;
        	double width = 0;
    		while (seq.hasNext()) {
    			Symbol sym = (Symbol) seq.next();
    			if (actions.get(sym).equals("draw")) {
    					try {
							turtle.getClass().getDeclaredMethod("move", null).invoke(turtle, null);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						}
    			} else {
    				tell(turtle, (Symbol) sym);
    			}
    			height = turtle.getPosition().getX();
    			width = turtle.getPosition().getY();
    			if (height < 0 && width < 0) {
    				height = Math.abs(height);
    				width = Math.abs(width);
    				rect = rect.createUnion(new Rectangle2D.Double(-width, 0, width, height));
    			} else if (height < 0 && width > 0) {
    				height = Math.abs(height);
    				width = Math.abs(width);
    				rect = rect.createUnion(new Rectangle2D.Double(0, 0, width, height));
    			} else if (height > 0 && width < 0) {
    				height = Math.abs(height);
    				width = Math.abs(width);
    				rect = rect.createUnion(new Rectangle2D.Double(-width,height, width, height));
    			} else {
    				height = Math.abs(height);
    				width = Math.abs(width);
    				rect = rect.createUnion(new Rectangle2D.Double(0,height, width, height));
    			}
    		}
    	} else {
    		seq = applyRules(seq, 1);
    		while(seq.hasNext()) {
    			ArrayList<Symbol> list = new ArrayList<Symbol>();
    			list.add((Symbol) seq.next());
    			rect = rect.createUnion(getBoundingBox(turtle, list.iterator(), n-1));
    		}
    	}
    	if (firstIteration) {
    		turtle.init(initPosition, initAngle);
    		mustInit = false;
    	}
    	return rect;
    }
}