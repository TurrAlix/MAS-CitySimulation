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
+!drive_random : pos(X,Y) & cell(X,Y,street_up) & busy(0)  <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(2000);
    .print("Attempting to go up...");
    up;
    !drive_random.

+!drive_random : pos(X,Y) & cell(X,Y,street_down) & busy(0) <-
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(2000);
    .print("Attempting to go down...");
    down;
    !drive_random.

+!drive_random : pos(X,Y) & cell(X,Y,street_left) & busy(0) <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(2000);
    .print("Attempting to go left...");
    left;
    !drive_random.

+!drive_random : pos(X,Y) & cell(X,Y,street_right) & busy(0) <- 
    -+busy(1);
    ?busy(B);
    .print("Busy?", B);
    .wait(2000);
    .print("Attempting to go right...");
    right;
    !drive_random.

-!drive_random <-
    .wait(500);
    !drive_random.


+up_successful <-
    .print("Went up!");
    -up_successful;
    -+busy(0);
    ?busy(B);
    .print("Busy?", B).
    
+up_failed <-
    .print("Cannot go up");
    -up_failed;
    !change_direction.

+down_successful <-
    .print("Went down!");
    -down_successful;
    -+busy(0);
    ?busy(B);
    .print("Busy?", B).
    
+down_failed <-
    .print("Cannot go down");
    -down_failed;
    !change_direction.

+right_successful <-
    .print("Went right!");
    -right_successful;
    -+busy(0);
    ?busy(B);
    .print("Busy?", B).
    
+right_failed <-
    .print("Cannot go right");
    -right_failed;
    !change_direction.

+left_successful <-
    .print("Went left!");
    -left_successful;
    -+busy(0);
    ?busy(B);
    .print("Busy?", B).
    
+left_failed <-
    .print("Cannot go left");
    -left_failed;
    !change_direction.

/*TODO
For now, change_direction is actually occuring at the same time as drive_random as this latter is
triggered as soon as there is a pos and a cell percepts without no constraint of previous success or whatever (so
basically it's triggered almost constantly, even when there is a change of direction occuring...)
> this leads to repetitions in the car logs and probably explains why it goes crazy at some point (with car 2
going crazy from the very start as it calls change_direction right away); to fix this, we probably need to be
more restrictive in our calls of the drive_random functions so that they cannot occur in parallel of change_direction
(creation of additional control beliefs maybe? > to be investigated)
-------------UPDATEEE-----------------------
Now thanks to the belief busy() it works fine to prevent change_direction from occuring at the same time as drive_random,
but since the cell beliefs are all stored in the beliefs' base when the car is going on a block on which it has already gone,
it calls several times drive_random simultaneously (interferences between the percept coming from the environment and
the one already stored in the belief's base >> so we should find a way to remove the cell percepts along the way 
while not preventing the change_direction function from working as it needs the D value from the cell percept...)
*/
+!change_direction <-
    .wait(2000);
    .print("Trying to change of direction");
    ?pos(X,Y);
    ?cell(X,Y,D);
    .print("Position: ", X, "/", Y, "; Street Direction: ", D);
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    .print("New Direction Drawn: ", NewD);
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


/*TODO: We should try to make car and pedestrian as separated percepts compared to
cells (so also some changes needed in City.java) so as to not compare D="car" with
NewD="street_left" (for example) in change_direction function*/
/*+cell(X,Y,car) <-
    .print("There is a car at x=", X, " & y=", Y).

+cell(X,Y,pedestrian) <-
    .print("There is a pedestrian at x=", X, " & y=", Y).*/