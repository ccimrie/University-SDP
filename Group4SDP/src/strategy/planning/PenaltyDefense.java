package strategy.planning;

import movement.RobotMover;
import vision.Position;
import world.state.PossessionType;
import world.state.WorldState;
import strategy.calculations.AngleCalculator;
import strategy.calculations.DistanceCalculator;
import utility.SafeSleep;
import world.state.Ball;

public class PenaltyDefense extends StrategyInterface {
	double originalx;
	double originaly;
	Ball ball;
	Position topPole;
	Position botPole;
	Position penalty;
	double dToPenalty;
	RobotMover mover;

	public PenaltyDefense(WorldState world, RobotMover mover) {
		super(world, mover);
		// Original position of the ball. Use this to determine
		this.ball = world.ball;
		this.topPole = world.getOurGoalTop();
		this.botPole = world.getOurGoalBot();
		if (world.areWeOnLeft())
			this.penalty = world.goalInfo.getLeftPenalty();
		else
			this.penalty = world.goalInfo.getRightPenalty();
		this.mover = mover;

	}

	@Override
	public void run() {
		System.out.println("[PenaltyDefence] active");
		AngleCalculator ang = new AngleCalculator(world);
		while (!shouldidie && !Strategy.alldie) {
			double angleToUs = ang.AngleTurner(world.theirRobot,
					world.ourRobot.x, world.ourRobot.y);
			double angleToTop = ang.AngleTurner(world.theirRobot,
					topPole.getX(), topPole.getY());
			double angleToBot = ang.AngleTurner(world.theirRobot,
					botPole.getX(), botPole.getY());
			dToPenalty = DistanceCalculator.Distance(world.getOurRobot().x,
					world.getOurRobot().y, penalty.getX(), penalty.getY());
			double dToBall = world.distanceToBall();
			double y = world.projectedBallPos().getY();
			double yblock = (dToBall * Math.tan(angleToUs)) / 1.75;
			//
			// we defend right goal
			//
			if ( world.hasPossession == PossessionType.Us) return;
			if (!world.areWeOnLeft()) {
				yblock = world.getOurRobot().y - yblock;
				if (angleToUs > -5 && angleToUs < 5) {

				}
				// we defend right goal
				// enemy aims in top corner
				else if (angleToTop < 15) {
					// enemy make a shoot
					if (dToBall + 15 < dToPenalty) {
						// wait
						
						if (world.getOurRobot().y > 178) {
							if (world.getOurRobot().y > y) {
								mover.move(-100, 0);

							} else if (world.getOurRobot().y < y) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}

						}
						// we defend right goal
						// catch the ball

						else if (world.getOurRobot().y > 182) {
							if (world.getOurRobot().y > y) {
								mover.stopRobot();

							} else if (world.getOurRobot().y < y) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}

						}
						// we defend right goal
						// move back slightly
						else {
							if (world.getOurRobot().y > y) {
								mover.move(100, 0);

							} else if (world.getOurRobot().y < y) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}

						}

					}
					// we defend right goal
					// enemy still aiming
					else {

						// go to blocking position
						if (world.getOurRobot().y > 205) {
							if (world.getOurRobot().y > yblock) {
								mover.stopRobot();

							} else if (world.getOurRobot().y < yblock) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}
						}
						// we defend right goal
						// wait
						else if (world.getOurRobot().y > 210) {
							if (world.getOurRobot().y > yblock) {
								mover.move(-100, 0);

							} else if (world.getOurRobot().y < yblock) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}
						}
						// we defend right goal
						// move back slightly
						else {
							if (world.getOurRobot().y > yblock) {
								mover.move(100, 0);

							} else if (world.getOurRobot().y < yblock) {
								mover.move(100, 0);

							} else {
								mover.move(100, 0);
							}
						}

					}

				}
				// we defend right goal
				// enemy aims in bot corner
				else if (angleToBot > -15) {
					// enemy make a shoot
					if (dToBall + 15 < dToPenalty) {
						// catch the ball

						if (world.getOurRobot().y < 300) {
							if (world.getOurRobot().y < y) {
								mover.move(100, 0);

							} else if (world.getOurRobot().y > y) {
								mover.move(-100, 0);

							} else {
								mover.stopRobot();
							}

						}
						// we defend right goal
						// wait
						else if (world.getOurRobot().y < 305) {
							if (world.getOurRobot().y < y) {
								mover.stopRobot();

							} else if (world.getOurRobot().y > y) {
								mover.move(-100, 0);

							} else {
								mover.stopRobot();
							}

						}
						// we defend right goal
						// move back slightly
						else {

							mover.move(-100, 0);

						}

					}
					// we defend right goal
					// enemy still aiming
					else {

						// go to blocking position
						if (world.getOurRobot().y < 280) {
							if (world.getOurRobot().y > yblock) {
								mover.move(-100, 0);

							} else if (world.getOurRobot().y < yblock) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}
						}
						// we defend right goal
						// wait
						else if (world.getOurRobot().y < 285) {
							if (world.getOurRobot().y < yblock) {
								mover.stopRobot();

							} else if (world.getOurRobot().y > yblock) {
								mover.move(-100, 0);

							} else {
								mover.stopRobot();
							}
						}
						// we defend right goal
						// move back slightly
						else {
							mover.move(-100, 0);
						}

					}
				}
			}
			//
			// we defend left goal
			//
			else {
				yblock = world.getOurRobot().y + yblock;
				if (angleToUs > -5 && angleToUs < 5) {

				}

				// we defend left goal

				// enemy aims in top corner
				else if (angleToTop > -15) {
					// enemy make a shoot
					if (dToBall + 15 < dToPenalty) {
						// wait

						if (world.getOurRobot().y > 178) {
							if (world.getOurRobot().y > y) {
								mover.stopRobot();

							} else if (world.getOurRobot().y < y) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}

						}

						// we defend left goal

						// catch the ball

						else if (world.getOurRobot().y > 182) {
							if (world.getOurRobot().y > y) {
								mover.move(-100, 0);

							} else if (world.getOurRobot().y < y) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}

						}

						// we defend left goal

						// move back slightly
						else {
							if (world.getOurRobot().y > y) {
								mover.move(100, 0);

							} else if (world.getOurRobot().y < y) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}

						}

					}

					// we defend left goal

					// enemy still aiming
					else {

						// go to blocking position
						if (world.getOurRobot().y > 205) {
							if (world.getOurRobot().y > yblock) {
								mover.stopRobot();

							} else if (world.getOurRobot().y < yblock) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}
						}

						// we defend left goal

						// wait
						else if (world.getOurRobot().y > 210) {
							if (world.getOurRobot().y > yblock) {
								mover.move(-100, 0);

							} else if (world.getOurRobot().y < yblock) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}
						}

						// we defend left goal

						// move back slightly
						else {
							if (world.getOurRobot().y > yblock) {
								mover.move(100, 0);

							} else if (world.getOurRobot().y < yblock) {
								mover.move(100, 0);

							} else {
								mover.move(100, 0);
							}
						}

					}

				}

				// we defend left goal

				// enemy aims in bot corner
				else if (angleToBot < 15) {
					// enemy make a shoot
					if (dToBall + 15 < dToPenalty) {
						// catch the ball

						if (world.getOurRobot().y < 300) {
							if (world.getOurRobot().y < y) {
								mover.move(100, 0);

							} else if (world.getOurRobot().y > y) {
								mover.move(-100, 0);

							} else {
								mover.stopRobot();
							}

						}

						// we defend left goal

						// wait
						else if (world.getOurRobot().y < 305) {
							if (world.getOurRobot().y < y) {
								mover.stopRobot();

							} else if (world.getOurRobot().y > y) {
								mover.move(-100, 0);

							} else {
								mover.stopRobot();
							}

						}

						// we defend left goal

						// move back slightly
						else {

							mover.move(-100, 0);

						}

					}

					// we defend left goal

					// enemy still aiming
					else {

						// go to blocking position
						if (world.getOurRobot().y < 280) {
							if (world.getOurRobot().y > yblock) {
								mover.move(-100, 0);

							} else if (world.getOurRobot().y < yblock) {
								mover.move(100, 0);

							} else {
								mover.stopRobot();
							}
						}

						// we defend left goal

						// wait
						else if (world.getOurRobot().y < 285) {
							if (world.getOurRobot().y < yblock) {
								mover.stopRobot();

							} else if (world.getOurRobot().y > yblock) {
								mover.move(-100, 0);

							} else {
								mover.stopRobot();
							}
						}

						// we defend left goal

						// move back slightly
						else {
							mover.move(-100, 0);
						}

					}
				}
			}
			try {
				SafeSleep.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// double theirOriginal = Math.toDegrees(world.theirRobot.bearing);
		//
		// int counter = 0;
		// while (!shouldidie && !Strategy.alldie){
		// // System.out.println(Math.toDegrees(world.theirRobot.bearing) + " "
		// // + theirOriginal);
		//
		// if (Math.toDegrees(world.theirRobot.bearing) > theirOriginal + 5
		// && counter > -1) {
		// System.out.println("go backwards");
		// mover.move(0, -100);
		// try {
		// SafeSleep.sleep(300);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// mover.stopRobot();
		//
		// theirOriginal = Math.toDegrees(world.theirRobot.bearing);
		// counter--;
		// try {
		// SafeSleep.sleep(300);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } else if (Math.toDegrees(world.theirRobot.bearing) < theirOriginal -
		// 5
		// && counter < 1) {
		// System.out.println("going forwards");
		//
		// mover.move(0, 100);
		// try {
		// SafeSleep.sleep(300);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// mover.stopRobot();
		//
		// theirOriginal = Math.toDegrees(world.theirRobot.bearing);
		// counter++;
		// try {
		// SafeSleep.sleep(300);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } else {
		// try {
		// SafeSleep.sleep(50);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// // System.out.println(counter);
		// }
	}
}
