/**
 * 
 */
package simulator.old;


/**
 * @author s0909773
 * 
 */
public class Controller {

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#backward(int)
     */
	
    public void backward(int speed) {
        System.out.println("Backward " + speed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#forward(int)
     */

    public void forward(int speed) {
        System.out.println("Forward " + speed);

    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#floatWheels()
     */

    public void floatWheels() {
        System.out.println("FloatWheels");
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#stop()
     */

    public void stop() {
        System.out.println("Stop");

    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#rotate(int, int)
     */

    public void rotate(int deg, int speed) {
        System.out.println("Rotate (deg: " + deg + ", speed: " + speed + ")");
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#setWheelSpeeds(int, int)
     */

    public void setWheelSpeeds(int leftWheelSpeed, int rightWheelSpeed) {
        System.out.println("Set Wheel Speeds: " + leftWheelSpeed + ", "
                + rightWheelSpeed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#getMaximumWheelSpeed()
     */

    public int getMaximumWheelSpeed() {
        return 720;
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#kick()
     */

    public void kick() {
        System.out.println("Kick");
    }


	public void penaltyKick() {
		System.out.println("Penalty Kick!");
		
	}


    public boolean isReady() {
        return true;
    }


	public void addListener(ControllerListener cl) {
		System.out.println("Adding listener.");
	}

/*	@Override
	public void gentleKick(int speed, int angle) {
		// TODO Auto-generated method stub
		
	}*/

}
