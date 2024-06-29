{ include("$jacamoJar/templates/common-cartago.asl") }

/*
Cars are simple circles, they don't have orientation.
Directions are absolute: up, down, right, left.
Cars cannot go on top of each other, nor can they kill pedestrians.
They can only drive on streets block (whether it is a zebra-crossing or not).

Before moving, the car retrieves the direction of the block it currently occupies.
2 options for street blocks:
1) unidirectional: the car follows this direction if the block targeted is free. If it is not free (another agent occupies it, 
or it is a building), then the car randomly chooses another direction so as to turn (but it cannot go back).
2) bidirectional: if a precedence has been asserted to one of the direction, the car follows it in priority
and the other cars following blocks with no precedence must let it pass first
If there has not been any precedence asserted to this block, the car chooses randomly between both directions.

If the car arrives at a zebra-crossing and there is a pedestrian crossing in front of the vehicle, it waits for the pedestrian
to free the way instead of trying to change of direction directly.
*/

/* Initial beliefs and rules */
busy(0). //not turning or in the process of driving

/* Initial goals */
!drive_random.

// ------------------------------------------------------------- //

/* Plans */
+!drive_random : pos(X,Y) & cellC(X,Y,street_up,P) & busy(0) <- 
    -+busy(1);
    .wait(200);
    //.print("Attempting to go up...");
    !up(X,Y).

+!drive_random : pos(X,Y) & cellC(X,Y,street_down,P) & busy(0) <-
    -+busy(1);
    .wait(200);
    //.print("Attempting to go down...");
    !down(X,Y).

+!drive_random : pos(X,Y) & cellC(X,Y,street_right,P) & busy(0) <- 
    -+busy(1);
    .wait(200);
    //.print("Attempting to go right...");
    !right(X,Y).

+!drive_random : pos(X,Y) & cellC(X,Y,street_left,P) & busy(0) <- 
    -+busy(1);
    .wait(200);
    //.print("Attempting to go left...");
    !left(X,Y).


+!drive_random : pos(X,Y) & cellC(X,Y,street_up_right,P) & busy(0) <- 
    -+busy(1);
    .wait(200);
    !draw_random_direction(street_up, street_right, P).

+!drive_random : pos(X,Y) & cellC(X,Y,street_up_left,P) & busy(0) <- 
    -+busy(1);
    .wait(200);
    !draw_random_direction(street_up, street_left,P).

+!drive_random : pos(X,Y) & cellC(X,Y,street_down_right,P) & busy(0) <- 
    -+busy(1);
    .wait(200);
    !draw_random_direction(street_down, street_right,P).

+!drive_random : pos(X,Y) & cellC(X,Y,street_down_left,P) & busy(0) <- 
    -+busy(1);
    .wait(200);
    !draw_random_direction(street_down, street_left,P).

-!drive_random <-
    .wait(100);
    !drive_random.

+!draw_random_direction(D1, D2, P) <- 
    ?pos(X,Y);
    jia.random_direction(X, Y, D);
    if (not(D == D1) & not(D == D2)) {
        !draw_random_direction(D1, D2, P);
    } else {
        if (D==street_up & P==street_up) {
            .print("I have the priority to go on ", X, ",", Y-1, "!");
            .broadcast(tell, priority(X,Y-1));
            up;
            .broadcast(untell, priority(X,Y-1));
            //.print("I don't have the priority to go on ", X, ",", Y-1, " anymore!");
        }
        if (D==street_up & not(P==street_up)) {
            !up(X,Y);
        }

        if (D==street_down & P==street_down) {
            .print("I have the priority to go on ", X, ",", Y+1, "!");
            .broadcast(tell, priority(X,Y+1));
            down;
            .broadcast(untell, priority(X,Y+1));
            //.print("I don't have the priority to go on ", X, ",", Y+1, " anymore!");
        }
        if (D==street_down & not(P==street_down)) {
            !down(X,Y);
        }

        if (D==street_right & P==street_right) {
            .print("I have the priority to go on ", X+1, ",", Y, "!");
            .broadcast(tell, priority(X+1,Y));
            right;
            .broadcast(untell, priority(X+1,Y));
            //.print("I don't have the priority to go on ", X+1, ",", Y, " anymore!");
        }
        if (D==street_right & not(P==street_right)) {
            !right(X,Y);
        }

        if (D==street_left & P==street_left) {
            .print("I have the priority to go on ", X-1, ",", Y, "!");
            .broadcast(tell, priority(X-1,Y));
            left;
            .broadcast(untell, priority(X-1,Y));
            //.print("I don't have the priority to go on ", X-1, ",", Y, " anymore!");
        }
        if (D==street_left & not(P==street_left)) {
            !left(X,Y);
        }
    }.

//Checks of the priority before actually moving!
+!up(X,Y) : priority(X,Y-1)[source(Sender)] <-
    .print("I let ", Sender, " pass first!");
    .wait({-priority(X,Y-1)[source(Sender)]});
    //.print("All clear!");
    up.
+!up(X,Y) : not(priority(X,Y-1)[source(Sender)]) <-
    up.

+!down(X,Y) : priority(X,Y+1)[source(Sender)] <-
    .print("I let ", Sender, " pass first!");
    .wait({-priority(X,Y+1)[source(Sender)]});
    //.print("All clear!");
    down.
+!down(X,Y) : not(priority(X,Y+1)[source(Sender)]) <-
    down.

+!right(X,Y) : priority(X+1,Y)[source(Sender)] <-
    .print("I let ", Sender, " pass first!");
    .wait({-priority(X+1,Y)[source(Sender)]});
    //.print("All clear!");
    right.
+!right(X,Y) : not(priority(X+1,Y)[source(Sender)]) <-
    right.

+!left(X,Y) : priority(X-1,Y)[source(Sender)] <-
    .print("I let ", Sender, " pass first!");
    .wait({-priority(X-1,Y)[source(Sender)]});
    //.print("All clear!");
    left.
+!left(X,Y) : not(priority(X-1,Y)[source(Sender)]) <-
    left.

// ------------------------------------------------------------- //

+success("up") <-
    .print("Went up!");
    -+busy(0);
    !drive_random.
+fail("up",P) : state(works) <-
    if (P==true){ //wait at the zebra-crossing
        .print("Letting the pedestrian(s) cross before going up.");
        .wait(200);
        up;
    } else {
        .print("Cannot go up.");
        !change_direction;
    }.
+fail("up",P) : state(broken_down) <-
    ?pos(X,Y);
    .print("I just broke down in ", pos(X,Y), ". Waiting for the helicopter to fix me!");
    .send(helicopter, achieve, fix_car(X,Y)).

+success("down") <-
    .print("Went down!");
    -+busy(0);
    !drive_random.
+fail("down",P) : state(works) <-
    if (P==true){ //wait at the zebra-crossing
        .print("Letting the pedestrian(s) cross before going down.");
        .wait(200);
        down;
    } else {
        .print("Cannot go down.");
        !change_direction;
    }.
+fail("down",P) : state(broken_down) <-
    ?pos(X,Y);
    .print("I just broke down in ", pos(X,Y), ". Waiting for the helicopter to fix me!");
    .send(helicopter, achieve, fix_car(X,Y)).

+success("right") <-
    .print("Went right!");
    -+busy(0);
    !drive_random.
+fail("right",P) : state(works) <-
    if (P==true){ //wait at the zebra-crossing
        .print("Letting the pedestrian(s) cross before going right.");
        .wait(200);
        right;
    } else {
        .print("Cannot go right.");
        !change_direction;
    }.
+fail("right",P) : state(broken_down) <-
    ?pos(X,Y);
    .print("I just broke down in ", pos(X,Y), ". Waiting for the helicopter to fix me!");
    .send(helicopter, achieve, fix_car(X,Y)).

+success("left") <-
    .print("Went left!"); 
    -+busy(0);
    !drive_random.
+fail("left",P) : state(works) <-
    if (P==true){ //wait at the zebra-crossing
        .print("Letting the pedestrian(s) cross before going left.");
        .wait(200);
        left;
    } else {
        .print("Cannot go left.");
        !change_direction;
    }.
+fail("left",P) : state(broken_down) <-
    ?pos(X,Y);
    .print("I just broke down in ", pos(X,Y), ". Waiting for the helicopter to fix me!");
    .send(helicopter, achieve, fix_car(X,Y)).

// ------------------------------------------------------------- //

+!change_direction : success("up") <-
    ?pos(X,Y);
    ?success(D); //last successful move
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    !no_going_back(NewD,street_down).

+!change_direction : success("down") <-
    ?pos(X,Y);
    ?success(D); //last successful move
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    !no_going_back(NewD,street_up).

+!change_direction : success("right") <-
    ?pos(X,Y);
    ?success(D); //last successful move
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    !no_going_back(NewD,street_left).

+!change_direction : success("left") <-
    ?pos(X,Y);
    ?success(D); //last successful move
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    !no_going_back(NewD,street_right).

-!change_direction <- //car is blocked right from the start, so no last successful move to rely on yet
    .print("Please don't start a car in a blocking position from the start, it is not fair!!").

// ------------------------------------------------------------- //

+!no_going_back(NewD,D) <-
    ?pos(X,Y);
    if (not(NewD==D)) {
        .print("Attempting to turn...");
        if (NewD==street_up) {
            !up(X,Y);
        }
        if (NewD==street_down) {
            !down(X,Y);
        }
        if (NewD==street_right) {
            !right(X,Y);
        }
        if (NewD==street_left) {
            !left(X,Y);
        }
    } else {
        !change_direction; //need to draw another direction then
    }.

// ------------------------------------------------------------- //

//Logs for percepts
+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").

+fixed[source(helicopter)] <- //message sent from the helicopter once it is done fixing the car
    .print("I'm fixed! Thanks, helicopter!");
    .wait(1000);
    -+busy(0);
    change_state;
    !drive_random.

+priority(X,Y)[source(Sender)] <-
    -+priority(X,Y)[source(Sender)].

-priority(X,Y)[source(Sender)] <-
    -priority(X,Y)[source(Sender)].

/*+cellL(X,Y,D,P) <-
    .print("Left cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").

+cellR(X,Y,D,P) <-
    .print("Right cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").

+cellC(X,Y,D,P) <-
    .print("Current cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").

+cellU(X,Y,D,P) <-
    .print("Up cell: x=", X, " & y=", Y, " ; infrastructure=", D, " ; precedence=", P").

+cellD(X,Y,D,P) <-
    .print("Down cell: x=", X, " & y=", Y, " ; ", D).*/

+whoL(X,Y,W,P) : (W==agCar) | (W==agPedestrian) <-
    .print("Agent ",P, " on left cell: x=", X, " & y=", Y).

+whoR(X,Y,W,P) : (W==agCar) | (W==agPedestrian) <-
    .print("Agent ",P, " on right cell: x=", X, " & y=", Y).

+whoU(X,Y,W,P) : (W==agCar) | (W==agPedestrian) <-
    .print("Agent ",P, " on up cell: x=", X, " & y=", Y).

+whoD(X,Y,W,P) : (W==agCar) | (W==agPedestrian) <-
    .print("Agent ",P, " on down cell: x=", X, " & y=", Y).