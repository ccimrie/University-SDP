package comms.control;

public interface ServerInterface {
	
	public boolean quit();
	public boolean stop();
	public void setDefaultTravelSpeed(Double speed);
	public boolean travel(Double distance);
	public boolean travel(Double distance, Double speed);
	public boolean kick();
	public void setDefaultRotateSpeed(Double speed);
	public boolean rotate(Double angle);
	public boolean rotate(Double angle, Double speed);
	public boolean forward();
	public boolean forward(Double speed);
	public boolean arcForward(Double radius);
	public boolean arcForward(Double radius, Double speed);
	public boolean isMoving();
	public boolean isStalled();
	public boolean isTouchPressed();
}
