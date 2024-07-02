{ include("$jacamoJar/templates/common-cartago.asl") }


/* Initial beliefs and rules */
target(_ ,_ , _). //creating the template
waiting(0).       // if 1 it means it's waiting for a response

/* Initial goals */
!live.

// ---------------------------------------------------------------------- //
+!live <- 
    .wait(3000);
    ?school(X,Y);
    .print("I'm going to school at ", school(X,Y), " now.");
    -+target("school",X, Y);
    !goToPos(X,Y).

-!live <- 
    .print("Failed to live a day");
    .wait(1000);
    !live.

// ---------------------------------------------------------------------- //
// I'm not in the target position yet
+!goToPos(X,Y) : pos(AgX, AgY) & (not(AgX == X) | not(AgY == Y)) & waiting(0) 
    <- !next_step(X,Y).

// I'm in the school position
+!goToPos(X,Y) : pos(X, Y) & school(X, Y) & waiting(0)
    <- .print("I'm at school!");
    .wait(4000);
    ?park(PX, PY);
    -+target("park", PX, PY);
    .print("I'm going to park at ", park(PX,PY), " now.");
    !goToPos(PX, PY).

// I'm in the park position
+!goToPos(X,Y) : pos(X, Y) & park(X, Y) & waiting(0) <-
    .print("I'm at park!");
    .wait(4000);
    -+target("stop", -1, -1);
    .print("I FINISH MY DAAAAY!");
    .wait(1000000). // MAYBE NOT NEEDED IT HAS JUST FINIHES THE GOALS

// Failure handling for pos goal
-!goToPos(X,Y) : waiting(0)
    <- .print("Failed to move to position: ", X, ", ", Y);
        .wait(1000);
        !goToPos(X,Y).

// Failure handling for pos goal
-!goToPos(X,Y) : waiting(1)
    <- .print("...I'm waiting..");
        .wait(1000);
        !goToPos(X,Y).


// ---------------------------------------------------------------------- //

/* These are the plans to have the pedestrian walk in the direction of X,Y.
 It uses the internal action jia.get_direction which encodes a search algorithm.  */
+!next_step(X,Y) : pos(AgX, AgY) <-
   jia.get_dir(AgX, AgY, X, Y, D);
    .wait(500);
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

-!next_step(X,Y) <-
    .print("Failed the next step");
    .wait(1000);
    !next_step(X,Y).

// ---------------------------------------------------------------------- //

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
+whoL(X,Y, W, P) : W == adultPedestrian <- 
    !neighbours(P, "left").

+whoR(X,Y, W, P) : W == adultPedestrian <-
    !neighbours(P, "right").

+whoU(X,Y, W, P) : W == adultPedestrian <-  
    !neighbours(P, "upper").

+whoD(X,Y, W, P) : W == adultPedestrian <-  
    !neighbours(P, "down").

+!neighbours(P, Position) <-  
    -+waiting(1);
    .print("I'm waiting for an hi from ",P," ...");
    .wait(3000).

-!neighbours(P, Position)
    <-  -+waiting(0);
    .print("I already greeted ", P, "! I'll continue my day now.").
*/

+greetings[source(Sender)] <-
    !handle_greeting(Sender).

+!handle_greeting(Sender) <-
    -+waiting(1);
    .print(Sender, " just greeted me!");
    .wait(3000);
    .send(Sender, tell, greetings_back);
    .print("Nice to meet you ", Sender, "! I'll continue my day..");
    -+waiting(0).
