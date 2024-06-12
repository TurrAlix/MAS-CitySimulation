{ include("$jacamoJar/templates/common-cartago.asl") }

/*
Things to consider for the movement-very simple one:
before moving, the car has to: retrieve the direction of the block on which it is, check if the next block is
free of obstacles (in our case, building or other cars), and retrieve its own position, then move forward of one block
Let's assume the car will be represented as circles, so no need to define an orientation. 

Then-more complex:
according to the percepts of the directions surrounding the agent, choose which street to take (in that case,
turn towards the direction of the guiding block if the car turns, not the block on which it is currently)
If the next block is a zebra-crossing and there is no other streets to take instead, stop and wait until
there are no pedestrians
*/


/* Initial beliefs and rules */
//gsize(_,W,H); Not so sure we need to use this here!
busy(0). //not turning or in the process of driving

/* Initial goals */
!drive_random.

/* Plans */
+!drive_random : pos(X,Y) & cellC(X,Y,street_up) & busy(0)  <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(200);
    .print("Attempting to go up...");
    up.

+!drive_random : pos(X,Y) & cellC(X,Y,street_down) & busy(0) <-
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(200);
    .print("Attempting to go down...");
    down.

+!drive_random : pos(X,Y) & cellC(X,Y,street_left) & busy(0) <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(200);
    .print("Attempting to go left...");
    left.

+!drive_random : pos(X,Y) & cellC(X,Y,street_right) & busy(0) <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(200);
    .print("Attempting to go right...");
    right.

-!drive_random <-
    .wait(100);
    !drive_random.


+success("up") <-
    .print("Went up!");
    -+busy(0);
    ?busy(B);
    .print("Busy?", B);
    !drive_random.
    
+fail("up") <-
    .print("Cannot go up");
    !change_direction.

+success("down") <-
    .print("Went down!");
    -+busy(0);
    ?busy(B);
    .print("Busy?", B);
    !drive_random.
    
+fail("down") <-
    .print("Cannot go down");
    !change_direction.

+success("right") <-
    .print("Went right!");
    -+busy(0);
    ?busy(B);
    .print("Busy?", B);
    !drive_random.
    
+fail("right") <-
    .print("Cannot go right");
    !change_direction.

+success("left") <-
    .print("Went left!"); 
    -+busy(0);
    ?busy(B);
    .print("Busy?", B);
    !drive_random.
    
+fail("left") <-
    .print("Cannot go left");
    !change_direction.

+!change_direction <-
    .wait(200);
    .print("Trying to change of direction");
    ?pos(X,Y);
    ?cellC(X,Y,D1);
    ?success(D2); //last successful move
    .print("Position: ", X, "/", Y, "; Street Direction: ", D1, "; Last successful move: ", D2);
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    .print("New Direction Drawn: ", NewD);
    if(D2=="up") {
        if (not(NewD==D1) & not(NewD==street_down)) {
            .print("Attempting to turn...");
            if (NewD==street_up) {
            up;
            }
            if (NewD==street_right) {
            right;
            }
            if (NewD==street_left) {
            left;
            }
        } else {
        !change_direction; //need to draw another direction then
        }
    }
    if(D2=="down") {
        if (not(NewD==D1) & not(NewD==street_up)) {
            .print("Attempting to turn...");
            if (NewD==street_down) {
            down;
            }
            if (NewD==street_right) {
            right;
            }
            if (NewD==street_left) {
            left;
            }
        } else {
        !change_direction;
        }
    }
    if(D2=="right") {
        if (not(NewD==D1) & not(NewD==street_left)) {
            .print("Attempting to turn...");
            if (NewD==street_up) {
            up;
            }
            if (NewD==street_down) {
            down;
            }
            if (NewD==street_right) {
            right;
            }
        } else {
        !change_direction;
        }
    }
    if(D2=="left") {
        if (not(NewD==D1) & not(NewD==street_right)) {
            .print("Attempting to turn...");
            if (NewD==street_up) {
            up;
            }
            if (NewD==street_down) {
            down;
            }
            if (NewD==street_left) {
            left;
            }
        } else {
        !change_direction;
        }
    } else { //car is blocked right from the start, so no last successful move to rely on yet
        .print("Please don't start a car in a blocking position from the start, it is not fair!!"); //we try again to change direction with the new values for success percept
    }. 


//Logs for percepts
+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").

/*+cellL(X,Y,D) <-
    .print("Left cell: x=", X, " & y=", Y, " ; ", D).

+cellR(X,Y,D) <-
    .print("Right cell: x=", X, " & y=", Y, " ; ", D).

+cellC(X,Y,D) <-
    .print("Current cell: x=", X, " & y=", Y, " ; ", D).

+cellU(X,Y,D) <-
    .print("Up cell: x=", X, " & y=", Y, " ; ", D).

+cellD(X,Y,D) <-
    .print("Down cell: x=", X, " & y=", Y, " ; ", D).


+whoL(X,Y,W) : (W==car) | (W==pedestrian) <-
    .print("Agent on left cell?: x=", X, " & y=", Y, " ; ", W).

+whoR(X,Y,W) : (W==car) | (W==pedestrian) <-
    .print("Agent on right cell?: x=", X, " & y=", Y, " ; ", W).

+whoU(X,Y,W) : (W==car) | (W==pedestrian) <-
    .print("Agent on up cell?: x=", X, " & y=", Y, " ; ", W).

+whoD(X,Y,W) : (W==car) | (W==pedestrian) <-
    .print("Agent on down cell?: x=", X, " & y=", Y, " ; ", W).*/