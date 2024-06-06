{ include("$jacamoJar/templates/common-cartago.asl") }

/* Initial goals */
!walk_random.
//!start
//+!start : true <- .wait(500); skip; !start.

/* Plans */
+!walk_random <- 
    jia.random_direction(X,Y,D); //draw a different direction that is free
    .print("Direction Drawn: ", D);
    if (D==street_up) {
        .print("Attempting to go up.");
        up;
    }
    if (D==street_down) {
        .print("Attempting to go down.");
        down;
    }
    if (D==street_right) {
        .print("Attempting to go right.");
        right;
    }
    if (D==street_left) {
        .print("Attempting to go left.");
        left;
    }
    .wait(4000);
    !walk_random.


-!walk_random <-
    .wait(200);
    !drive_random.


+up_successful <-
    .print("Went up!").
    
+up_failed <-
    .print("Cannot go up");
    !walk_random.

+down_successful <-
    .print("Went down!").
    
+down_failed <-
    .print("Cannot go down");
    !walk_random.

+right_successful <-
    .print("Went right!").
    
+right_failed <-
    .print("Cannot go right");
    !walk_random.

+left_successful <-
    .print("Went left!").
    
+left_failed <-
    .print("Cannot go left");
    !walk_random.




//Logs for percepts
+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").

+cell(X,Y,street_right) <-
    .print("There is a street right at x=", X, " & y=", Y).

+cell(X,Y,building) <-
    .print("There is a building at x=", X, " & y=", Y).

+cell(X,Y,street_up) <-
    .print("There is a street up at x=", X, " & y=", Y).

+cell(X,Y,street_down) <-
    .print("There is a street down at x=", X, " & y=", Y).

+cell(X,Y,street_left) <-
    .print("There is a street left at x=", X, " & y=", Y).

+cell(X,Y,car) <-
    .print("There is a car at x=", X, " & y=", Y).

+cell(X,Y,pedestrian) <-
    .print("There is a pedestrian at x=", X, " & y=", Y).