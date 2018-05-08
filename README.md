# springbootmaze
Coding test for PeopleNet

# Overview
I wrote the maze solver as a Spring Boot app. The API requests are handled in the MazeController. The front end page, index.html, 
is served as a static page, but most of the dynamic work is done through JavaScript. I used jQuery mainly for simplicity 
since I haven't learned enough about AngularJS to figure out how to write a good front end.

## API handler
MazeController handles the following API requests:
* GET / Redirects to index.html, a static page.
* POST /inputMaze Takes the input from the textarea and sends it to MazeBuilder, which will validate it.  If the maze is not valid,
it will return a 400 Bad Request and send the error message.
* GET /singleStep Runs one iteration of the solution.
* GET /fiveSteps Runs five steps.  I implemented these two APIs so I could trace how the solution was progressing.
* GET /doCompleteSolution Finishes solving the maze.

All but / return a MazeResponseObject, which contains the following:
* The string representation of the maze solved thus far
* Any messages, such as the number of steps taken (either individually clicking the step button or running to completion)
* A flag saying that the "winning" path has been found
* A flag saying that there are no more valid paths and there is no solution

## MazeBuilder
This parses the input string via a POST request, validates it, and stores the maze as an array of strings.

## PathSolver
This does a breadth-first search by starting with the initial, trivial path (just the "A" cell).  For each step of the solution,
PathSolver will go through all of the paths found thus far and then test which cells can be reached from the end of the current path.
The valid paths are then added to the list of possible paths, and the previously checked paths are discarded.  The first path
that finds its way to the "B" cell is deemed the winning path.

## index.html
This page has several divs that are hidden or shown depending on what's currently in progress:
* mazeinput: Textarea for maze input.  I tested this with maze1.txt, maze2.txt, and maze3.txt.
* correctmaze: Display of the maze as solved thus far.  Within this, the solverbuttons div is hidden as soon as the maze is solved
or is found non-solvable.
* incorrectmaze: Any error messages found within MazeBuilder.  Try mangling one of the input files by removing or adding characters.
* sadhorn: A little audio feedback (via SoundCloud) if the maze is valid but cannot be solved.  Try walling in the "B" cell so it can't be reached.
* fireworks: A little celebration once the maze is properly solved.

# Packaging
This is a Spring Boot application. Assuming I'm using GitHub properly, you should be able to clone the repository and build the
project through Maven. pom.xml is there so I believe it will load all the dependencies.

If that doesn't work, then this can be set up by creating a new Spring Boot application and installing the three source files 
(MazeBuilder, MazeController, and PathSolver) into the net.elkman.springbootmaze source file, and then copying the static files 
(index.html, nrhp.css, and the JPEGs and GIF) into the src/main/resources/static directory.

nrhp.css is so named because I took it from another project.

