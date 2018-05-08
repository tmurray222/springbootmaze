package net.elkman.springbootmaze;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MazeController {
	private MazeBuilder theBuilder;
	private PathSolver solver;

	private class MazeResponseObject {
		@SuppressWarnings("unused")
		public String currentMaze;
		@SuppressWarnings("unused")
		public String message;
		@SuppressWarnings("unused")
		public boolean foundWinner;
		@SuppressWarnings("unused")
		public boolean hasNoSolution;
		
		public MazeResponseObject(String currentMaze, String message, boolean foundWinner, boolean hasNoSolution) {
			this.currentMaze = currentMaze;
			this.message = message;
			this.foundWinner = foundWinner;
			this.hasNoSolution = hasNoSolution;
		}
	}
	// Attempt to redirect to index.html, which is a static page.
	@RequestMapping("/")
	public void homePage(HttpServletResponse httpServletResponse) {
		try {
			httpServletResponse.sendRedirect("index.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="inputMaze", method = RequestMethod.POST)
	public MazeResponseObject submitMaze(@RequestParam("inputMaze") String inputMaze) throws IOException {
		
		theBuilder = new MazeBuilder();
		solver = new PathSolver(theBuilder);
		theBuilder.buildAMaze(inputMaze);
		solver.startPathEnumeration();
		
		return new MazeResponseObject(inputMaze, "Number of steps: 0", false, false);
	}
	
	@RequestMapping("/singleStep")
	public MazeResponseObject singleStep() {
		if (solver == null) {
			System.err.println("Solver seems to have disappeared!");
		}
		solver.stepGetAllNextPaths();
		
		String[] maze = theBuilder.getInputMaze();
		String tweakedMaze = String.join("\n", maze);
		
		String message;
		if (solver.hasWinningPath()) {
			message = "A maze solution has been found after " + solver.getNumberOfSteps() + " steps!";
		} else if (!solver.hasMorePathsToConsider()) {
			message = "Maze cannot be solved.  There is no path from A to B.";
		} else {
			message = "Number of steps so far: " + solver.getNumberOfSteps();
		}
		
		return new MazeResponseObject(tweakedMaze, message, solver.hasWinningPath(), !solver.hasMorePathsToConsider());
	}
	
	@RequestMapping("/fiveSteps")
	public MazeResponseObject fiveSteps() {
		if (solver == null) {
			System.err.println("Solver seems to have disappeared!");
		}
		for (int i = 0; i < 5; i++) {
			solver.stepGetAllNextPaths();
			if (solver.hasWinningPath() || !solver.hasMorePathsToConsider()) {
				break;
			}
		}
		
		String[] maze = theBuilder.getInputMaze();
		String tweakedMaze = String.join("\n", maze);
		
		String message;
		if (solver.hasWinningPath()) {
			message = "A maze solution has been found after " + solver.getNumberOfSteps() + " steps!";
		} else if (!solver.hasMorePathsToConsider()) {
			message = "Maze cannot be solved.  There is no path from A to B.";
		} else {
			message = "Number of steps so far: " + solver.getNumberOfSteps();
		}
		
		return new MazeResponseObject(tweakedMaze, message, solver.hasWinningPath(), !solver.hasMorePathsToConsider());
	}
	
	@RequestMapping("/doCompleteSolution")
	public MazeResponseObject doCompleteSolution() {
		if (solver == null) {
			System.err.println("Solver seems to have disappeared!");
		}
			
		long startTime = System.currentTimeMillis();
		
		while (!solver.hasWinningPath() && solver.hasMorePathsToConsider()) {
			solver.stepGetAllNextPaths();
		}
	
		String[] maze = theBuilder.getInputMaze();
		String tweakedMaze = String.join("\n", maze);
		
		long endTime = System.currentTimeMillis();
		double elapsedTime = (endTime - startTime) / 1000.0;
		
		String message;
		if (solver.hasWinningPath()) {
			message = "A maze solution has been found after " + solver.getNumberOfSteps() + " steps and " + elapsedTime + " seconds!";
		} else if (!solver.hasMorePathsToConsider()) {
			message = "Maze cannot be solved.  There is no path from A to B.";
		} else {
			message = "Why am I returning out of this function?";
		}
		System.out.println(message);
		return new MazeResponseObject(tweakedMaze, message, solver.hasWinningPath(), !solver.hasMorePathsToConsider());
	}
	@ExceptionHandler(IOException.class)
	void handleIOException(IOException e, HttpServletResponse response) throws IOException {
		System.out.println("Going through the exception handler");
	    response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}
	
	public static void main(String[] args) {
		SpringApplication.run(MazeController.class, args);
	}
}
