package lindenmayer;
import java.awt.geom.Point2D;
import java.util.Stack;

public class TurtleBidon implements Turtle {

	private Stack<State> stack = new Stack<State>();
	private double step;
	private double delta;
	private State state;
	
	/*
	*Ce boolean indique si le pinceau est activé ou non, le booléan n'a cependant aucune effet puisque cette 
	* classe est bidon
	*/
	//private boolean brushActivated = false;
	
	private class State {
		
		private double positionX;
		private double positionY;
		private double angle;
		
		public State(double positionX, double positionY, double angle) {
			this.positionX = positionX;
			this.positionY = positionY;
			this.angle = angle;
		}
		
		public double getPositionX() {
			return positionX;
		}
		public double getPositionY() {
			return positionY;
		}
		public double getAngle() {
			return Math.toDegrees(angle);
		}

	}
	@Override
	public void draw() {
		//brushActivated = true;
		double positionX = state.getPositionX() + step * Math.cos(Math.toRadians(state.getAngle()));
		double positionY = state.getPositionY() + step * Math.sin(Math.toRadians(state.getAngle()));
		state = new State(positionX, positionY, state.getAngle());
		push();
		System.out.println("draw : ("+ state.getPositionX() + ", " + state.getPositionY() + ", " + state.getAngle() + ")");
	}

	@Override
	public void move() {
		//brushActivated = false;
		double positionX = state.getPositionX() + step * Math.cos(Math.toRadians(state.getAngle()));
		double positionY = state.getPositionY() + step * Math.sin(Math.toRadians(state.getAngle()));
		state = new State(positionX, positionY, state.getAngle());
		push();
		System.out.println("move : ("+ state.getPositionX() + ", " + state.getPositionY() + ", " + state.getAngle() + ")");
	}

	@Override
	public void turnR() {
		state = new State(state.getPositionX(), state.getPositionY(), state.getAngle()-delta);
		push();
		System.out.println("turnR : ("+ state.getPositionX() + ", " + state.getPositionY() + ", " + state.getAngle() + ")");
	}

	@Override
	public void turnL() {
		state = new State(state.getPositionX(), state.getPositionY(), state.getAngle()+delta);
		push();
		System.out.println("turnL : ("+ state.getPositionX() + ", " + state.getPositionY() + ", " + state.getAngle() + ")");
	}

	@Override
	public void push() {
		stack.push(state);
	}

	@Override
	public void pop() {
		stack.pop();
	}

	@Override
	public void stay() {
	}

	@Override
	public void init(Point2D position, double angle_deg) {
		state = new State(position.getX(), position.getY(), angle_deg);
		while(!stack.empty()) stack.pop();
		push();
		System.out.println("init : ("+ state.getPositionX() + ", " + state.getPositionY() + ", " + state.getAngle() + ")");
	}

	@Override
	public Point2D getPosition() {
		return new Point2D.Double(state.positionX, state.positionY);
	}

	@Override
	public double getAngle() {
		return state.angle;
	}

	@Override
	public void setUnits(double step, double delta) {
		this.step = step;
		this.delta = delta;
	}

}
