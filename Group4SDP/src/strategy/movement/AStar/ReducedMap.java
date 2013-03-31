package strategy.movement.AStar;

import world.state.WorldState;

/**
 * @author Jakov Smelkin
 */
public class ReducedMap implements TileBasedMap {
	/** The map width in tiles */
	public final int WIDTH;
	/** The map height in tiles */
	public final int HEIGHT;

	public double REDUCTION = 20.0;

	/** Indicate pitch at a given location */
	public static final int PITCH = 0;
	/** Indicate wall at a given location */
	public static final int BLOCKED = 1;
	public static final int BALL = 3;
	/** Indicate our robot at a given location */
	public static final int US = 2;

	/** The terrain settings for each tile in the map */
	public int[][] terrain;
	/** The unit in each tile of the map */
	private int[][] units;
	/** Indicator if a given tile has been visited during the search */
	private boolean[][] visited;

	public boolean avoidball = false;
	public boolean avoidenemy = true;

	/**
	 * Create a new test map with some default configuration
	 */
	public ReducedMap(WorldState world, boolean avoidball, boolean avoidenemy) {
		synchronized (world) {
			HEIGHT = reduceRound(480);
			WIDTH = reduceRound(640);
			terrain = new int[WIDTH][HEIGHT];
			units = new int[WIDTH][HEIGHT];
			visited = new boolean[WIDTH][HEIGHT];
			this.avoidball = avoidball;
			this.avoidenemy = avoidball;
			// Enemy robot

			int themx = reduceRound(world.theirRobot.x);
			int themy = reduceRound(world.theirRobot.y);
			System.out.println(world.theirRobot.x);
			System.out.println(world.theirRobot.y);
			System.out.println(themx);
			System.out.println(themy);
			if (themx < 1)
				themx += 2;
			if (themx == 1)
				themx += 1;
			if (themy < 1)
				themy += 2;
			if (themy == 1)
				themy += 1;
			if (themx == WIDTH - 1)
				themx -= 2;
			if (themy == HEIGHT - 1)
				themy -= 2;
			if (themx >= WIDTH)
				themx -= 3;
			if (themy >= HEIGHT)
				themy -= 3;
			if (avoidenemy) {
				fillArea(themx - 2, themy - 2, 5, 5, BLOCKED);
			}
			// Fill ball just for display
			int ballx = reduceRound(world.ball.x);
			int bally = reduceRound(world.ball.y);
			if (ballx <= 1)
				ballx += 2;
			if (bally <= 1)
				bally += 2;
			if (ballx >= WIDTH - 1)
				ballx -= 2;
			if (bally >= HEIGHT - 1)
				bally -= 2;
			if (avoidball) {
				fillArea(ballx - 2, bally - 2, 4, 4, BLOCKED);
			} else {
				fillArea(ballx - 2, bally - 2, 4, 4, BALL);
			}
			// Walls
			int temp = reduceRound(world.goalInfo.pitchConst.getLeftBuffer());
			fillArea(0, 0, temp, HEIGHT, BLOCKED);
			temp = reduceRound(world.goalInfo.pitchConst.getTopBuffer());
			fillArea(0, 0, WIDTH, temp, BLOCKED);
			temp = reduceRound(world.goalInfo.pitchConst.getRightBuffer());
			fillArea(WIDTH - temp, 0, temp, HEIGHT, BLOCKED);
			temp = reduceRound(world.goalInfo.pitchConst.getBottomBuffer());
			fillArea(0, HEIGHT - temp, WIDTH, temp, BLOCKED);
			units[reduceRound(world.ourRobot.x)][reduceRound(world.ourRobot.y)] = US;
		}
	}

	public int reduceRound(double n) {
		return (int) (Math.round(n / REDUCTION));
	}

	public int reduceFloor(double n) {
		return (int) (Math.floor(n / REDUCTION));
	}

	/**
	 * Fill an area with a given terrain type
	 * 
	 * @param x
	 *            The x coordinate to start filling at
	 * @param y
	 *            The y coordinate to start filling at
	 * @param width
	 *            The width of the area to fill
	 * @param height
	 *            The height of the area to fill
	 * @param type
	 *            The terrain type to fill with
	 */
	private void fillArea(int x, int y, int width, int height, int type) {
		int xMin = Math.max(x, 0);
		int yMin = Math.max(y, 0);
		int xMax = Math.min(x + width, WIDTH);
		int yMax = Math.min(y + height, HEIGHT);
		for (int xp = xMin; xp < xMax; ++xp) {
			for (int yp = yMin; yp < yMax; ++yp) {
				terrain[xp][yp] = type;
			}
		}
	}

	/**
	 * Clear the array marking which tiles have been visted by the path finder.
	 */
	public void clearVisited() {
		for (int x = 0; x < getWidthInTiles(); x++) {
			for (int y = 0; y < getHeightInTiles(); y++) {
				visited[x][y] = false;
			}
		}
	}

	/**
	 * @see TileBasedMap#visited(int, int)
	 */
	public boolean visited(int x, int y) {
		return visited[x][y];
	}

	/**
	 * Get the terrain at a given location
	 * 
	 * @param x
	 *            The x coordinate of the terrain tile to retrieve
	 * @param y
	 *            The y coordinate of the terrain tile to retrieve
	 * @return The terrain tile at the given location
	 */
	public int getTerrain(int x, int y) {
		return terrain[x][y];
	}

	/**
	 * Get the unit at a given location
	 * 
	 * @param x
	 *            The x coordinate of the tile to check for a unit
	 * @param y
	 *            The y coordinate of the tile to check for a unit
	 * @return The ID of the unit at the given location or 0 if there is no unit
	 */
	public int getUnit(int x, int y) {
		try {
			return units[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}

	/**
	 * Set the unit at the given location
	 * 
	 * @param x
	 *            The x coordinate of the location where the unit should be set
	 * @param y
	 *            The y coordinate of the location where the unit should be set
	 * @param unit
	 *            The ID of the unit to be placed on the map, or 0 to clear the
	 *            unit at the given location
	 */
	public void setUnit(int x, int y, int unit) {
		units[x][y] = unit;
	}

	/**
	 * @see TileBasedMap#blocked(Mover, int, int)
	 */
	public boolean blocked(int x, int y) {
		// if theres a unit at the location, then it's blocked
		if (getUnit(x, y) != 0) {
			return true;
		}
		try {
			return terrain[x][y] == BLOCKED;
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}

	/**
	 * @see TileBasedMap#getCost(Mover, int, int, int, int)
	 */
	public float getCost(int sx, int sy, int tx, int ty) {
		int coef;
		if (blocked(sx - 1, sy) || blocked(sx - 1, sy - 1)
				|| blocked(sx, sy - 1) || blocked(sx + 1, sy - 1)
				|| blocked(sx + 1, sy) || blocked(sx + 1, sy + 1)
				|| blocked(sx, sy + 1) || blocked(sx - 1, sy + 1)) {
			coef = 3;
		} else {
			coef = 1;
		}
		if (sx == tx || sy == ty)
			return 10 * coef;
		else
			return 14 * coef;
	}

	/**
	 * @see TileBasedMap#getHeightInTiles()
	 */
	public int getHeightInTiles() {
		return HEIGHT;
	}

	/**
	 * @see TileBasedMap#getWidthInTiles()
	 */
	public int getWidthInTiles() {
		return WIDTH;
	}

	/**
	 * @see TileBasedMap#pathFinderVisited(int, int)
	 */
	public void pathFinderVisited(int x, int y) {
		visited[x][y] = true;
	}

}
