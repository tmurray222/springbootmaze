package net.elkman.springbootmaze;

import java.util.*;

/**
 * This class takes a maze representation (from MazeBuilder) and incrementally visits the next paths
 * through the maze, based on a breadth-first search.  This is done in a single-step fashion so
 * the user can graphically see how the solution is built, if he/she chooses.
 * @author Todd
 *
 */
public class PathSolver {
	private MazeBuilder theBuilder;
	
	public enum Direction {START, LEFT, RIGHT, UP, DOWN, FINISH};
	
	public class PathEntry {
		public Direction direction;
		public int row;
		public int col;
		
		public PathEntry(Direction newDirection, int newRow, int newCol) {
			direction = newDirection;
			row = newRow;
			col = newCol;
		}
		
		@Override
		public int hashCode()
		{
			// Probably not the smartest hash if indexes go over 256 but I'll use it for now
			return direction.hashCode() << 16 + row << 8 + col;
		}
	}
	
	public class Path extends ArrayList<PathEntry>
	{
		private static final long serialVersionUID = 8846215693397724421L;

		public void addEntry(PathEntry newOne) {
			this.add(newOne);
		}
	}
	
	public Set<Path> pathsConsidered = new HashSet<>();
	public Path winningPath;
	public int numberOfSteps = 0;
	
	public int getNumberOfSteps() {
		return numberOfSteps;
	}
	public PathSolver(MazeBuilder theBuilder)
	{
		this.theBuilder = theBuilder;
		
	}
	
	/**
	 * Get ready to solve the maze by seeding it with the first path entry, consisting of the start node.
	 */
	public void startPathEnumeration() {
		// Create our starting entry.
		PathEntry start = new PathEntry(Direction.START, theBuilder.getStartRow(), theBuilder.getStartCol());
		Path startPath = new Path();
		startPath.add(start);
		pathsConsidered.add(startPath);
	}
	
	public boolean hasWinningPath() {
		return (this.winningPath != null);
	}
	
	public boolean hasMorePathsToConsider() {
		return (this.pathsConsidered.size() > 0);
	}
	
	/**
	 * From the paths that are currently being considered, see which direction(s) that each of these paths
	 * can go to.  Each extension of those paths will be added to the list.  This results in a breadth-first
	 * search that will terminate when the first winning path has been solved, so it should always return
	 * the shortest path.
	 * @return true if a complete solution ("winner") has been found.
	 */
	public boolean stepGetAllNextPaths() {
		boolean foundWinner = false;
		
		if (pathsConsidered == null || pathsConsidered.size() == 0) {
			//System.out.println("No paths considered yet - seeding the list");
			startPathEnumeration();
		}
		// Need to copy the existing path list to avoid a ConcurrentModificationException
		List<Path> pathsToLookAtInThisRound = new ArrayList<Path>();
		pathsToLookAtInThisRound.addAll(pathsConsidered);
		for (Path thisPath: pathsToLookAtInThisRound) {
			foundWinner |= stepGetNextPaths(thisPath);
		}
		pathsConsidered.removeAll(pathsToLookAtInThisRound);
		//System.out.println("After all paths, there are now " + pathsConsidered.size() + " paths to be considered in the next round.");
		if (pathsConsidered.size() == 0) {
			System.out.println("No more paths are left and we never found the end of the maze");
		}
		if (winningPath != null) {
			this.markWinningPath();
		}
		numberOfSteps++;
		return foundWinner;
	}
	
	private boolean stepGetNextPaths(Path existingPath)
	{
		boolean foundWinner = false;
		PathEntry last = existingPath.get(existingPath.size() - 1);
		
		List<PathEntry> nextPaths = getNextPaths(existingPath, last.row, last.col);
		
		for (PathEntry result: nextPaths) {
			Path newPath = (Path) existingPath.clone();
			newPath.add(result);
			pathsConsidered.add(newPath);
			if (result.direction.equals(Direction.FINISH)) {
				//System.out.println("We found the winning path!");
				winningPath = newPath;
			}
		}
		pathsConsidered.remove(existingPath);
		
		// Indicate which cells have been visited by all four directions.  This provides a display to the user
		// when single-stepping, and also prevents other path tests from visiting the same nodes twice.
		if (!foundWinner) {
			for (PathEntry nexts: nextPaths) {
				theBuilder.setCell(nexts.row, nexts.col, '*');
			}
		}
		return foundWinner;
	}
	
	/**
	 * From the current cell, look up, down, left, and right to see which cells are open.
	 * (But not up, up, down, down, left, right, left, right, B, and A.)
	 * Returns the cell(s) that are valid so they can be added to the existing path.
	 * @param existingPath - the path that got us here in the first place, so we don't try
	 * to return to existing nodes
	 * @param workRow - current test location
	 * @param workCol - current test location
	 * @return valid entries that can be the next cells in the path
	 */
	private List<PathEntry> getNextPaths(Path existingPath, int workRow, int workCol) {
		List<PathEntry> retList = new ArrayList<>();
		
		PathEntry up = testCell(existingPath, Direction.UP, workRow - 1, workCol);
		if (up != null) {
			retList.add(up);
		}
		
		PathEntry down = testCell(existingPath, Direction.DOWN, workRow + 1, workCol);
		if (down != null) {
			retList.add(down);
		}
		
		PathEntry left = testCell(existingPath, Direction.LEFT, workRow, workCol - 1);
		if (left != null) {
			retList.add(left);
		}
		
		PathEntry right = testCell(existingPath, Direction.RIGHT, workRow, workCol + 1);
		if (right != null) {
			retList.add(right);
		}
		
		return retList;
	}
	
	// PathEntry returned will contain the CURRENT row and column, plus the direction from the PREVIOUS entry.
	/**
	 * Test an individual cell to see if it can be the next cell in the path.
	 * @param existingPath - how we got here, and checked to make sure that we aren't doubling back
	 * @param direction - direction being tested
	 * @param testRow - location being checked
	 * @param testCol - location being checked
	 * @return a PathEntry to be added to the current path if it's valid, or null if we can't go there
	 */
	private PathEntry testCell(Path existingPath, Direction direction, int testRow, int testCol) {
		// Make sure we haven't already visited this entry.
		for (PathEntry entry: existingPath) {
			if (entry.row == testRow && entry.col == testCol) {
				//System.out.println(debug + " TO " + testRow + ", " + testCol + " has already been visited");
				return null;
			}
		}
		char testChar = theBuilder.getCell(testRow, testCol);
		PathEntry next = null;
		// If we found an open cell, add it to the path, and remember which direction got us here.
		if (testChar == '.') {
			next = new PathEntry(direction, testRow, testCol);
		} else if (testChar == 'B') {
			System.out.println(testRow + ", " + testCol + " is the finish of the maze!");
			next = new PathEntry(Direction.FINISH, testRow, testCol);
		}
		return next;
	}
	
	/**
	 * Overlay the solution on the maze so it can be presented to the user.
	 */
	private void markWinningPath() {
		for (PathEntry entry: winningPath) {
			char current = theBuilder.getCell(entry.row, entry.col);
			if (current != 'A' && current != 'B') {
				theBuilder.setCell(entry.row, entry.col, '!');
			}
		}
	}
}
