package net.elkman.springbootmaze;

import java.io.IOException;

/**
 * This class parses the string sent by the maze and builds an in-memory representation, really just containing
 * an array of strings that the user entered.  It also does error checking to make sure the maze contains legal
 * characters and is rectangular.
 * @author Todd
 *
 */
public class MazeBuilder {

	private String[] inputMaze;
	private int numColumns;
	private int startRow = -1;
	private int startCol = -1;
	private int endRow = -1;
	private int endCol = -1;
	
	public void buildAMaze(String theUserInput) throws IOException
	{
		inputMaze = theUserInput.split("\n");
		// Validate the maze.  Make sure that there's at least one line entered and that all rows have the same length.
		if (inputMaze.length < 2) {
			throw new IOException("Your maze must have at least one line!  Preferably more.");
		}
		numColumns = inputMaze[0].length();
		for (int i = 1; i < inputMaze.length; i++) {
			if (inputMaze[i].length() != numColumns) {
				throw new IOException("Your maze isn't rectangular.  Number of columns is different on row " + i);
			}
		}
		
		// Find a start and end point.
		for (int i = 0; i < getRowCount(); i++) {
			if (!inputMaze[i].matches("^[\\.#AB]*$")) {
				throw new IOException("Line " + inputMaze[i] + " should contain only . # A B");
			}
			if (inputMaze[i].indexOf('A') >= 0) {
				if (startRow == -1) {
					startRow = i;
					startCol = inputMaze[i].indexOf('A');
				} else {
					throw new IOException("More than one beginning point found in the maze!");
				}
			}
			if (inputMaze[i].indexOf('B') >= 0) {
				if (endRow == -1) {
					endRow = i;
					endCol = inputMaze[i].indexOf('B');
				} else {
					throw new IOException("More than one ending point found in the maze!");
				}
			}
		}
		if (startRow == -1) {
			throw new IOException("No starting point found in the maze!");
		}
		if (endRow == -1) {
			throw new IOException("No ending point found in the maze!");
		}
		
	}
	
	public String[] getInputMaze() {
		return inputMaze;
	}

	public int getStartRow() {
		return startRow;
	}

	public int getStartCol() {
		return startCol;
	}

	public int getEndRow() {
		return endRow;
	}

	public int getEndCol() {
		return endCol;
	}

	public int getColumnCount()
	{
		return numColumns;
	}
	
	public int getRowCount() {
		return inputMaze.length;
	}
	
	public char getCell(int row, int col) {
		if (row >= 0 && row < getRowCount() && col >= 0 && col < getColumnCount()) {
			return inputMaze[row].charAt(col);
		}
		// If the array index is out of bounds, return a different character,
		// and PathBuilder will know not to go there.  This could throw an IndexOutOfBoundsException,
		// but then PathBuilder.testCell would need to catch the exception.  The caller really just needs
		// to know if the next cell is open or if it's the end of the maze.
		return '!';
	}
	
	// Change the contents of a cell to indicate that it has already been visited or is part of the solution.
	public void setCell(int row, int col, char newChar) {
		if (row >= 0 && row < getRowCount() && col >= 0 && col < getColumnCount()) {
			if (inputMaze[row].charAt(col) != 'B') { // don't overwrite the end of the maze
				StringBuilder fixedString = new StringBuilder(inputMaze[row]);
				fixedString.setCharAt(col,  newChar);
				inputMaze[row] = fixedString.toString();
			}
		}
	}
}
