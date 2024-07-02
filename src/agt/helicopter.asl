{ include("$jacamoJar/templates/common-cartago.asl") }

// ----------------------------------------------------------------------------------- //


/* Initial beliefs and rules */
busy(0). //not fixing a car


// ----------------------------------------------------------------------------------- //
/* PLANS */

/* It receives a call from a car, it's not busy so it goes to fix */
+!fix_car(X,Y)[source(Sender)] : busy(0) <-
    -+busy(1);
    .print("I'm on my way to fix the ", Sender, " at (", X, ";", Y, ")!");
    !go_to_carX(X,Y);
    !go_to_carY(X,Y);
    .print(Sender, " is fully repaired!");
    .send(Sender, tell, fixed); //to trigger the car's movement
    .send(Sender, untell, fixed); //to not make the belief's base of the car too crowded
    -+busy(0);
    ?helicopterParkingPos(A,B);  
    !return_to_the_parking(A,B).

/* It fails if another car breaks down while the helicopter is busy taking care of a first car*/
-!fix_car(X,Y)[source(Sender)] : busy(1) <-
    .print(Sender, " also seems to need my help, but I'm busy for now. Call again later!");
    .wait(1000);
    !fix_car(X,Y)[source(Sender)].

// ----------------------------------------------------------------------------------- //
/* Movements of the agents */

+!go_to_carX(X,Y) : pos(W,Z) & W<=X <-
    if (not(W==X)) {
        !step_right;
        !go_to_carX(X,Y)
    }.

+!go_to_carX(X,Y) : pos(W,Z) & W>X <-
    !step_left;
    !go_to_carX(X,Y).

+!go_to_carY(X,Y) : pos(W,Z) & Z<Y <-
    !step_down;
    !go_to_carY(X,Y).

+!go_to_carY(X,Y) : pos(W,Z) & Z>Y <-
    !step_up;
    !go_to_carY(X,Y).

+!go_to_carY(X,Y) : pos(W,Z) & Z==Y <-
    .print("Just reached the car to repair in (", X, ";", Y, ")! Car repair in progress...");
    .wait(1000).

+!step_right <-
    .wait(200);
    right.

+!step_left <-
    .wait(200);
    left.

+!step_up <-
    .wait(200);
    up.

+!step_down <-
    .wait(200);
    down.
    
// ----------------------------------------------------------------------------------- //

/* It finish to fix and it's not again busy, so it goes back to the parking slot */
+!return_to_the_parking(X,Y) : busy(0) <-
    if(not(X==-1) & not(Y==-1)) {
        .print("Going back to my parking in (", X, ";", Y, ")");
        !go_to_parkingX(X,Y);
        !go_to_parkingY(X,Y);
    } else {
        .print("I can't find my parking...");
    }.  

//another car called for help, so the plan to return to the parking failed
+!return_to_the_parking(X,Y) : busy(1) <-
    .wait(100).
 
//another car called for help, so the plan to return to the parking failed
-!return_to_the_parking(X,Y) : busy(1) <-
    .wait(100). 

//in case of system bug
-!return_to_the_parking(X,Y) : busy(0) <- 
    .wait(100);
    !return_to_the_parking(X,Y).


//  ----------------------------------------------------------------------------------- //

// Movements to go back
+!go_to_parkingX(X,Y) : pos(W,Z) & W<=X & busy(0) <-
    if (not(W==X)) {
        !step_right;
        !go_to_parkingX(X,Y)
    }.

+!go_to_parkingX(X,Y) : pos(W,Z) & W>X & busy(0) <-
    !step_left;
    !go_to_parkingX(X,Y).

+!go_to_parkingY(X,Y) : pos(W,Z) & Z<Y & busy(0) <-
    !step_down;
    !go_to_parkingY(X,Y).

+!go_to_parkingY(X,Y) : pos(W,Z) & Z>Y & busy(0) <-
    !step_up;
    !go_to_parkingY(X,Y).

+!go_to_parkingY(X,Y) : pos(W,Z) & Z==Y & busy(0) <-
    .print("I'm parked!").

//  ----------------------------------------------------------------------------------- //
/* PERCEPTS */

+success("up") <-
    .print("Went up!").
    
+fail("up",P) <-
    .print("Cannot go up");
    .wait(100);
    !step_up.

+success("down") <-
    .print("Went down!").
    
+fail("down",P) <-
    .print("Cannot go down");
    .wait(100);
    !step_down.

+success("right") <-
    .print("Went right!").
    
+fail("right",P) <-
    .print("Cannot go right");
    .wait(100);
    !step_right.

+success("left") <-
    .print("Went left!").
    
+fail("left",P) <-
    .print("Cannot go left");
    .wait(100);
    !step_left.
    
//Logs for percepts
+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").