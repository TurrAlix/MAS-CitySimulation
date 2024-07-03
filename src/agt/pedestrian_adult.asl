{ include("$jacamoJar/templates/common-cartago.asl") }

// ---------------------------------------------------------------------- //

/* Initial beliefs and rules */
target(_ ,_ , _). //creating the template
waiting(0).       // if it's 1 it means it's waiting for a response
count(0).         // to make sure that an adult does not wait indefinitely for a greetings back

/* Initial goals */
!live.

// ---------------------------------------------------------------------- //

//Starting of the Adults life is to go to the office
+!live <- 
    ?office(X,Y);
    .print("I'm going to the office at ", office(X,Y), " now.");
    -+target("office",X, Y);
    !go_to_pos(X,Y).

-!live <- 
    .print("Failed to live a day");
    .wait(1000);
    !live.

// ---------------------------------------------------------------------- //

// Movements toward the target position, it's going but not yet arrived
+!go_to_pos(X,Y) : pos(AgX, AgY) & (not(AgX == X) | not(AgY == Y)) & waiting(0)
    <- !next_step(X,Y).

// Arrived in the office position, after that Adults go to the supermarket!
+!go_to_pos(X,Y) : pos(X, Y) & office(X, Y) & waiting(0)
    <- .print("I'm at the office!");
    .wait(4000);
    ?supermarket(PX, PY);
    -+target("supermarket", PX, PY);
    .print("I'm going to the supermarket at ", supermarket(PX,PY), " now.");
    !go_to_pos(PX, PY).

// It's in the supermarket position
+!go_to_pos(X,Y) : pos(X, Y) & supermarket(X, Y) & waiting(0)
    <- .print("I'm at the supermarket!");
    .wait(4000);
    -+target("stop", -1, -1);
    .print("I FINISH MY DAAAAY!").

-!go_to_pos(X,Y) : waiting(0)
    <- .print("Failed to move to position: ", X, ", ", Y);
        .wait(1000);
        !go_to_pos(X,Y).

-!go_to_pos(X,Y) : waiting(1)
    <- .print("...I'm waiting..");
        .wait(1000);
        !go_to_pos(X,Y).

// ---------------------------------------------------------------------- //

/* These are the plans to have the pedestrian walk in the direction of X,Y.
 It uses the internal action jia.get_dir which encodes an A star search algorithm.  */
+!next_step(X,Y) : pos(AgX, AgY) <-
    jia.get_dir(AgX, AgY, X, Y, D);
    .wait(250);
    if (D==up) {
        up;
    }
    if (D==down) {
        down;
    }
    if (D==right) {
        right;
    }
    if (D==left) {
        left;
    }.

-!next_step(X,Y)  <-
    .print("Failed the next step");
    .wait(1000);
    !next_step(X,Y).

// ---------------------------------------------------------------------- //
/*  PERCEPTS */

+success("up") <-
    .print("Went up!");
    ?target(_, X, Y);
    !go_to_pos(X, Y).
+fail("up",P) <-
    .print("Cannot go up");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("down") <-
    .print("Went down!");
    ?target(_, X, Y);
    !go_to_pos(X, Y).
+fail("down",P) <-
    .print("Cannot go down");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("right") <-
    .print("Went right!");
    ?target(_, X, Y);
    !go_to_pos(X, Y). 
+fail("right",P) <-
    .print("Cannot go right");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("left") <-
    .print("Went left!"); 
    ?target(_, X, Y);
    !go_to_pos(X, Y).   
+fail("left",P) <-
    .print("Cannot go left");
    ?target(_, X, Y);
    !next_step(X, Y).

+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").


/*
+cellL(X,Y,D,P) <-
    .print("Left cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").

+cellR(X,Y,D,P) <-
    .print("Right cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").

+cellC(X,Y,D,P) <-
    .print("Current cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").

+cellU(X,Y,D,P) <-
    .print("Up cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").

+cellD(X,Y,D,P) <-
    .print("Down cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").
*/


+whoL(X, Y, W, P) : W == childPedestrian <- 
    !greet(P, "left").

+whoR(X, Y, W, P) : W == childPedestrian <-  
    !greet(P, "right").

+whoU(X, Y, W, P) : W == childPedestrian <-  
    !greet(P, "upper").

+whoD(X, Y, W, P) : W == childPedestrian <-  
    !greet(P, "down").


// ---------------------------------------------------------------------- //
/*  COMMUNICATION */


+!greet(P, Position) : not(last_greeted(P)) <-
    -+waiting(1);
    -+last_greeted(P);
    .print("Oh, ", P, " is on the ", Position, " cell! Hello ", P, "!");
    .send(P, tell, greetings);
    .print("I'll wait for greetings back....");
    !wait_for_greetings_back.

-!greet(P, Position) <- // Last person greeted was P, so no need to greet again now
    -+waiting(0);
    .print("I already greeted ", P, "! I'll continue my day now.").


+!wait_for_greetings_back : waiting(1) & count(X) & X<3 <-
    -+count(X+1);
    .wait(800);
    !wait_for_greetings_back.

+!wait_for_greetings_back : waiting(1) & count(X) & X==3 <-
    -+count(0);
    .print("No greetings back, how rude! Whatever, I'll continue my day now!");
    -+waiting(0).

-!wait_for_greetings_back : waiting(0) <-
    -+count(0);
    .print("I'll continue my day now!").


+greetings_back[source(Sender1)] : waiting (1) <-
    !handle_greeting_back(Sender1).

+!handle_greeting_back(Sender1) <-
    .print(Sender1, " greeted me back!");
    .wait(2000);
    -+waiting(0).