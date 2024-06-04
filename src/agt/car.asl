{ include("$jacamoJar/templates/common-cartago.asl") }

/*
Things to consider for the movement-very simple one:
before moving, the car has to: retrieve the direction of the block on which it is, check if the next block is
free of obstacles (in our case, building or other cars), and retrieve its own position, then move forward of one block
Let's assume the car will be represented as circles, so no need to define an orientation. 

Then-more complex:
according to the perceipts of the directions surrounding the agent, choose which street to take (in that case,
turn towards the direction of the guiding block if the car turns, not the block on which it is currently)
If the next block is a zebra-crossing and there is no other streets to take instead, stop and wait until
there are no pedestrians
*/


/* Initial beliefs and rules */
//gsize(_,W,H); Not so sure we need to use this here!

/* Initial goals */
!start.

/* Plans */
+!start <-
    ?pos(X,Y);
    ?cell(X,Y,D);
    !drive_random.

-?pos(X,Y) <-
    .wait(200);
    !start.

-?cell(X,Y,D) <-
    .wait(200);
    !start.

+!drive_random : pos(X,Y) & cell(X,Y,street_up) <- 
    .print("Attempting to go up...");
    up;
    .wait(1000);
    !drive_random.

+!drive_random : pos(X,Y) & cell(X,Y,street_down) <-
    .print("Attempting to go down...");
    down;
    .wait(1000);
    !drive_random.

+!drive_random : pos(X,Y) & cell(X,Y,street_left) <- 
    .print("Attempting to go left...");
    left;
    .wait(1000);
    !drive_random.

+!drive_random : pos(X,Y) & cell(X,Y,street_right) <- 
    .print("Attempting to go right...");
    right;
    .wait(1000);
    !drive_random.

-!drive_random <-
    .wait(200);
    !drive_random.


+up_successful <-
    .print("Going up!").
    
+up_failed <-
    .print("Cannot go up");
    !change_direction.

+down_successful <-
    .print("Going down!").
    
+down_failed <-
    .print("Cannot go down");
    !change_direction.

+right_successful <-
    .print("Going right!").
    
+right_failed <-
    .print("Cannot go right");
    !change_direction.

+left_successful <-
    .print("Going left!").
    
+left_failed <-
    .print("Cannot go left");
    !change_direction.

+!change_direction <-
    .print("Trying to change of direction");
    ?pos(X,Y);
    ?cell(X,Y,D);
    .print("Position: ", X, "/", Y, ";Street Direction: ", D);
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    .print("New Direction Drawn: ", NewD);
    if (not(NewD == D)) {
        -+cell(X,Y,D);
        !drive_random;
    } else {

        !change_direction; //if the new direction drawn is similar to the old one, another one is drawn
    }.


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

+cell(X,Y,agent) <-
    .print("There is an agent at x=", X, " & y=", Y).

