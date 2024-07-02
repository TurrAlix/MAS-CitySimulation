{ include("$jacamoJar/templates/common-cartago.asl") }

// ---------------------------------------------------------------------- //

/* Initial beliefs and rules */
target(_ ,_ , _). //creating the template
waiting(0).       // if it's 1 it means it's waiting for a response

/* Initial goals */
!live.

// ---------------------------------------------------------------------- //

//Starting of the Adults life is to go to the office
+!live <- 
    ?office(X,Y);
    .print("I'm going to the office at ", office(X,Y), " now.");
    -+target("office",X, Y);
    !goToPos(X,Y).

-!live <- 
    .print("Failed to live a day");
    .wait(1000);
    !live.

// ---------------------------------------------------------------------- //

// Movements toward the target position, it's going but not yet arrived
+!goToPos(X,Y) : pos(AgX, AgY) & (not(AgX == X) | not(AgY == Y)) & waiting(0)
    <- !next_step(X,Y).

// Arrived in the office position, after that Adults go to the supermarket!
+!goToPos(X,Y) : pos(X, Y) & office(X, Y) & waiting(0)
    <- .print("I'm at the office!");
    .wait(4000);
    ?supermarket(PX, PY);
    -+target("supermarket", PX, PY);
    .print("I'm going to the supermarket at ", supermarket(PX,PY), " now.");
    !goToPos(PX, PY).

// It's in the supermarket position
+!goToPos(X,Y) : pos(X, Y) & supermarket(X, Y) & waiting(0)
    <- .print("I'm at the supermarket!");
    .wait(4000);
    -+target("stop", -1, -1);
    .print("I FINISH MY DAAAAY!").

-!goToPos(X,Y) : waiting(0)
    <- .print("Failed to move to position: ", X, ", ", Y);
        .wait(1000);
        !goToPos(X,Y).

-!goToPos(X,Y) : waiting(1)
    <- .print("...I'm waiting..");
        .wait(1000);
        !goToPos(X,Y).

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

-!next_step(X,Y)  
    <-  .print("Failed the next step");
        .wait(1000);
        !next_step(X,Y).

// ---------------------------------------------------------------------- //
/*  PERCEPTS */

+success("up") <-
    .print("Went up!");
    ?target(_, X, Y);
    !goToPos(X, Y).
+fail("up",P) <-
    .print("Cannot go up");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("down") <-
    .print("Went down!");
    ?target(_, X, Y);
    !goToPos(X, Y).
+fail("down",P) <-
    .print("Cannot go down");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("right") <-
    .print("Went right!");
    ?target(_, X, Y);
    !goToPos(X, Y). 
+fail("right",P) <-
    .print("Cannot go right");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("left") <-
    .print("Went left!"); 
    ?target(_, X, Y);
    !goToPos(X, Y).   
+fail("left",P) <-
    .print("Cannot go left");
    ?target(_, X, Y);
    !next_step(X, Y).

+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").

/*
//obs1
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

/*
//obs2
+whoL(X,Y, W, P) : (W == adultPedestrian) | (W == childPedestrian) <-  
    .print("Oh, ", P, " is on the left cell! Hello ", P, "!");
    .send(P, tell, greetings);
    .wait({+greetings_back[source(Sender)]});
    .wait(100).

+whoR(X,Y, W, P) : (W == adultPedestrian) | (W == childPedestrian) <-  
    .print("Oh, ", P, " is on the right cell! Hello ", P, "!");
    .send(P, tell, greetings);
    .wait({+greetings_back[source(Sender)]});
    .wait(100).

+whoU(X,Y, W, P) : (W == adultPedestrian) | (W == childPedestrian) <-  
    .print("Oh, ", P, " is on the upper cell! Hello ", P, "!");
    .send(P, tell, greetings);
    .wait({+greetings_back[source(Sender)]});
    .wait(100).

+whoD(X,Y, W, P) : (W == adultPedestrian) | (W == childPedestrian) <-  
    .print("Oh, ", P, " is on the down cell! Hello ", P, "!");
    .send(P, tell, greetings);
    .wait({+greetings_back[source(Sender)]});
    .wait(100).

+greetings[source(Sender)] <-
    .print(Sender, " just greeted me!");
    .send(Sender, tell, greetings_back);
    .print("Nice to meet you ", Sender, "!");
    .wait ({+greetings_back[source(Sender)]});
    .wait(200).

+greetings_back[source(Sender)] <-
    .print(Sender, " greeted me back! I'll continue my day now!");
    .wait(100).
*/


+!greet(P, Position) : not(last_greeted(P)) 
    <- -+waiting(1);
    -+last_greeted(P);
    .print("Oh, ", P, " is on the ", Position, " cell! Hello ", P, "!");
    .send(P, tell, greetings);
    .print("I'll wait for greetings back....").

-!greet(P, Position) // Last person greeted was P, so no need to greet again now
    <-  -+waiting(0);
    .print("I already greeted ", P, "! I'll continue my day now.").

+whoL(X, Y, W, P) : (W == adultPedestrian) | (W == childPedestrian) <- 
    !greet(P, "left").

+whoR(X, Y, W, P) : (W == adultPedestrian) | (W == childPedestrian) <-  
    !greet(P, "right").

+whoU(X, Y, W, P) : (W == adultPedestrian) | (W == childPedestrian) <-  
    !greet(P, "upper").

+whoD(X, Y, W, P) : (W == adultPedestrian) | (W == childPedestrian) <-  
    !greet(P, "down").


// Other Adults could greet other Adults
+greetings[source(Sender)] <-
    !handle_initial_greeting(Sender).

+!handle_initial_greeting(Sender) <-
    .print(Sender, " just greeted me!");
    .send(Sender, tell, greetings_back);
    .print("Nice to meet you ", Sender, "! I'll continue my day..");
    .wait(3000);
    -+waiting(0).

+greetings_back[source(Sender1)] <-
    !handle_greeting_back(Sender1).

+!handle_greeting_back(Sender1) <-
    .print(Sender1, " greeted me back! I'll continue my day now!");
    .wait(3000);
    -+waiting(0).