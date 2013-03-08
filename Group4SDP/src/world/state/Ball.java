package world.state;

public class Ball extends Entity {
	
	public double x;
	public double y;
	public double speedX;
	public double speedY;
	
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
