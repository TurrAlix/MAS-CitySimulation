{ include("$jacamoJar/templates/common-cartago.asl") }

//school(0,11).
target(_ ,_ , _). //creating the template

/* Initial goals */
!live.


// ---------------------------------------------------------------------- //
+!live <- 
    ?school(X,Y);
    .print("I'm going to school at ", school(X,Y), " now.");
    -+target("school",X, Y);
    //.print("I'm going to school at ", school(X,Y), " now.");
    .wait(100);
    !goToPos(X,Y).

-!live <- 
    .print("Failed to live a day");
    .wait(1000);
    !live.

// ---------------------------------------------------------------------- //
/* These plans encode how an agent should go to an exact position X,Y.
 * This one assumes that the position is reachable. If the position is not reachable, it will loop forever.  */
// i'm not in the target position yet
+!goToPos(X,Y) : pos(AgX, AgY) & (not(AgX == X) | not(AgY == Y))    
   <- //.print("GOTOPOS: not yet arrived");
      .wait(100);
      !next_step(X,Y).

// i'm in the school position
+!goToPos(X,Y) : pos(X, Y) & school(X, Y)
    <- .print("GOTOPOS: I'm at school!");
        .wait(5000);
        ?park(PX, PY);
        -+target("park", PX, PY);
        .print("I'm going to park at ", park(PX,PY), " now.");
        !goToPos(PX, PY).

// i'm in the park position
+!goToPos(X,Y) : pos(X, Y) & park(X, Y)
    <- .print("GOTOPOS: I'm at park!");
        .wait(5000);
        -+target("stop", -1, -1);
        .print("I FINISH MY DAAAAY!");
        .wait(1000000). // MAYBE NOT NEEDED IT HAS JUST FINIHES THE GOALS

// Failure handling for pos goal
-!goToPos(X,Y)
    <- .print("Failed to move to position: ", X, ", ", Y);
        .wait(1000);
        !goToPos(X,Y).


// ---------------------------------------------------------------------- //
/* These are the plans to have the pedestrian walk in the direction of X,Y.
 * They are used by the plans go_near and pos. It uses the internal action jia.get_direction which encodes a search algorithm.  */
+!next_step(X,Y) : pos(AgX, AgY) 
   <- .print("NEXT STEP");
   jia.get_dir(AgX, AgY, X, Y, D);
    .wait(1000);
    if (D==up) {
        .print("Attempting to go up.");
        up;
    }
    if (D==down) {
        .print("Attempting to go down.");
        down;
    }
    if (D==right) {
        .print("Attempting to go right.");
        right;
    }
    if (D==left) {
        .print("Attempting to go left.");
        left;
    }.

-!next_step(X,Y)  
    <-  .print("Failed the next step");
        .wait(1000);
        !next_step(X,Y).

// ---------------------------------------------------------------------- //
/*
+!walk_random <-
    .println("..Walk randomly.");
    ?pos(X,Y);
    jia.random_walk(X,Y,D); //draw a different direction that is free
    .wait(1000);
    if (D==street_up) {
        up;
    }
    if (D==street_down) {
        down;
    }
    if (D==street_right) {
        right;
    }
    if (D==street_left) {
        left;
    };
    !live.
-!walk_random <-
    .wait(500);
    !walk_random.
*/

// ---------------------------------------------------------------------- //
+success("up") <-
    .print("Went up!");
    //-+busy(0);
    //?busy(B);
    ?target(_, X, Y);
    !goToPos(X, Y).
+fail("up") <-
    .print("Cannot go up");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("down") <-
    .print("Went down!");
    //-+busy(0);
    //?busy(B);
    ?target(_, X, Y);
    !goToPos(X, Y).
+fail("down") <-
    .print("Cannot go down");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("right") <-
    .print("Went right!");
    //-+busy(0);
    //?busy(B);
    ?target(_, X, Y);
    !goToPos(X, Y). 
+fail("right") <-
    .print("Cannot go right");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("left") <-
    .print("Went left!"); 
    //-+busy(0);
    //?busy(B);
    ?target(_, X, Y);
    !goToPos(X, Y).   
+fail("left") <-
    .print("Cannot go left");
    ?target(_, X, Y);
    !next_step(X, Y).

+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").

/*
+cellL(X,Y,D) <-
    .print("Left cell: x=", X, " & y=", Y, " ; ", D).

+cellR(X,Y,D) <-
    .print("Right cell: x=", X, " & y=", Y, " ; ", D).

+cellC(X,Y,D) <-
    .print("Current cell: x=", X, " & y=", Y, " ; ", D).

+cellU(X,Y,D) <-
    .print("Up cell: x=", X, " & y=", Y, " ; ", D).

+cellD(X,Y,D) <-
    .print("Down cell: x=", X, " & y=", Y, " ; ", D).


+whoL(X,Y,W) <-
    .print("Agent on left cell?: x=", X, " & y=", Y, " ; ", W).

+whoR(X,Y,W) <-
    .print("Agent on right cell?: x=", X, " & y=", Y, " ; ", W).

+whoU(X,Y,W) <-
    .print("Agent on up cell?: x=", X, " & y=", Y, " ; ", W).

+whoD(X,Y,W) <-
    .print("Agent on down cell?: x=", X, " & y=", Y, " ; ", W).
    */