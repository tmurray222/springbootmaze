# springbootmaze
Coding test for Trimble

# Overview
I wrote the maze solver as a Spring Boot app. The API requests are handled in the MazeController. The front end page, index.html, 
is served as a static page, but most of the dynamic work is done through JavaScript. I used jQuery mainly for simplicity 
since I haven't learned enough about AngularJS to figure out how to write a good front end.

# Packaging
This is a Spring Boot application. Assuming I'm using GitHub properly, you should be able to clone the repository and build the
project through Maven. pom.xml is there so I believe it will load all the dependencies.

If that doesn't work, then this can be set up by creating a new Spring Boot application and installing the three source files 
(MazeBuilder, MazeController, and PathSolver) into the net.elkman.springbootmaze source file, and then copying the static files 
(index.html, nrhp.css, and the JPEGs and GIF) into the src/main/resources/static directory.

nrhp.css is so named because I took it from another project
