{ include("$jacamoJar/templates/common-cartago.asl") }

/* Initial goals */
!walk_random.

/* Plans */
+!walk_random <-
    ?pos(X,Y);
    jia.random_walk(X,Y,D); //draw a different direction that is free
    .print("Direction Drawn: ", D);
    .wait(1000);
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
    }.


-!walk_random <-
    .wait(500);
    !walk_random.


+success(1,"up") <-
    .print("Went up!");
    !walk_random.
    
+success(0,"up") <-
    .print("Cannot go up");
    !walk_random.

+success(1,"down") <-
    .print("Went down!");
    !walk_random.
    
+success(0,"down") <-
    .print("Cannot go down");
    !walk_random.

+success(1,"right") <-
    .print("Went right!");
    !walk_random.
    
+success(0,"right") <-
    .print("Cannot go right");
    !walk_random.

+success(1,"left") <-
    .print("Went left!"); 
    !walk_random.
    
+success(0,"left") <-
    .print("Cannot go left");
    !walk_random.



//Logs for percepts
+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").

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