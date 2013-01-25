package world.state;

public class Ball extends Entity {
	
	public double x;
	public double y;
	
	public Ball() {
		super();
		// It's just a ball man
	}
	
	public String name() {
		return "Ball";
	}
	
	public String position() {
		return String.format("Ball: %s", this.getPosition());
	}
	
}
