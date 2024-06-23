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

+!drive_random : pos(X,Y) & cellC(X,Y,street_up_right) & busy(0) <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(200);
    !draw_random_direction(street_up, street_right).

+!drive_random : pos(X,Y) & cellC(X,Y,street_up_left) & busy(0) <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(200);
    !draw_random_direction(street_up, street_left).

+!drive_random : pos(X,Y) & cellC(X,Y,street_down_right) & busy(0) <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(200);
    !draw_random_direction(street_down, street_right).

+!drive_random : pos(X,Y) & cellC(X,Y,street_down_left) & busy(0) <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(200);
    !draw_random_direction(street_down, street_left).

-!drive_random <-
    .wait(100);
    !drive_random.


+!draw_random_direction(D1, D2) <- 
    ?pos(X,Y);
    jia.random_direction(X, Y, D);
    //.print("New Direction Drawn: ", D);
    if (not(D == D1) & not(D == D2)) {
        !draw_random_direction(D1, D2);
    } else {
        if (D==street_up) {
        .print("Attempting to go up...");
        up;
        }
        if (D==street_down) {
        .print("Attempting to go down...");
        down;
        }
        if (D==street_right) {
        .print("Attempting to go right...");
        right;
        }
        if (D==street_left) {
        .print("Attempting to go left...");
        left;
        }
    }.


+success("up") <-
    .print("Went up!");
    -+busy(0);
    ?busy(B);
    !drive_random.
    
+fail("up") <-
    .print("Cannot go up");
    !change_direction.

+success("down") <-
    .print("Went down!");
    -+busy(0);
    ?busy(B);
    !drive_random.
    
+fail("down") <-
    .print("Cannot go down");
    !change_direction.

+success("right") <-
    .print("Went right!");
    -+busy(0);
    ?busy(B);
    !drive_random.
    
+fail("right") <-
    .print("Cannot go right");
    !change_direction.

+success("left") <-
    .print("Went left!"); 
    -+busy(0);
    ?busy(B);
    !drive_random.
    
+fail("left") <-
    .print("Cannot go left");
    !change_direction.


+!change_direction : success("up") <-
    //.print("Trying to change of direction");
    ?pos(X,Y);
    ?success(D); //last successful move
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    //.print("New Direction Drawn: ", NewD);
    !no_going_back(NewD,street_down).

+!change_direction : success("down") <-
    //.print("Trying to change of direction");
    ?pos(X,Y);
    ?success(D); //last successful move
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    //.print("New Direction Drawn: ", NewD);
    !no_going_back(NewD,street_up).

+!change_direction : success("right") <-
    //.print("Trying to change of direction");
    ?pos(X,Y);
    ?success(D); //last successful move
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    //.print("New Direction Drawn: ", NewD);    
    !no_going_back(NewD,street_left).

+!change_direction : success("left") <-
    //.print("Trying to change of direction");
    ?pos(X,Y);
    ?success(D); //last successful move
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    //.print("New Direction Drawn: ", NewD);  
    !no_going_back(NewD,street_right).

-!change_direction <- //car is blocked right from the start, so no last successful move to rely on yet
    .print("Please don't start a car in a blocking position from the start, it is not fair!!").


+!no_going_back(NewD,D) <-
    if (not(NewD==D)) {
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
        if (NewD==street_left) {
        left;
        }
    } else {
        !change_direction; //need to draw another direction then
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
    .print("Down cell: x=", X, " & y=", Y, " ; ", D).*/

+whoL(X,Y,W) : (W==car) | (W==pedestrian) <-
    .print("Agent on left cell?: x=", X, " & y=", Y, " ; ", W).

+whoR(X,Y,W) : (W==car) | (W==pedestrian) <-
    .print("Agent on right cell?: x=", X, " & y=", Y, " ; ", W).

+whoU(X,Y,W) : (W==car) | (W==pedestrian) <-
    .print("Agent on up cell?: x=", X, " & y=", Y, " ; ", W).

+whoD(X,Y,W) : (W==car) | (W==pedestrian) <-
    .print("Agent on down cell?: x=", X, " & y=", Y, " ; ", W).