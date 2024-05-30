{ include("$jacamoJar/templates/common-cartago.asl") }

/*
Things to consider for the movement-very simple one:
before moving, the car has to: retrieve the direction of the block on which it is, check if the next block is
free of obstacles (in our case, building or other cars), and retrieve its own position, then move forward of one block
Let's assume the car will be represented as circles, so no need to define an orientation. 

Then-more complex:
according to the perceipts of the directions surrounding the agent, choose which street to take (in that case,
turn towards the direction of the guiding block if the car turns, not the block on which it is currently)
If the next block is a zebra-crossing and there is no other streets to take instead, stop and wait until
there are no pedestrians
*/


/* Initial beliefs and rules */
//gsize(_,W,H); Not so sure we need to use this here!
current_block(X,Y,D); //current position + direction > see how to manage this percept in the Java code


/* Initial goals */
!drive_random.

/* Plans */
+!drive_random : current_block(X,Y,"UP") <- 
    !go_on(X,Y-1,"UP");
    !drive_random.

+!drive_random : current_block(X,Y,"DOWN") <-
    !go_on(X,Y+1,"DOWN");
    !drive_random.

+!drive_random : current_block(X,Y,"LEFT") <- 
    !go_on(X-1,Y,"LEFT");
    !drive_random.

+!drive_random : current_block(X,Y,"RIGHT") <- 
    !go_on(X+1,Y,"RIGHT");
    !drive_random.


//Actual movement manager with the perception of obstacles (buildings and other agents)
+!go_on(X,Y,D) : free(X,Y) <-
    .print("Going ", D);
    move(D). //to be matched with the definition of movement here

+!go_on(X,Y) : obstacle(X,Y) <- //means that there is another car
    .print("Oh, another car! Let's wait!");
    .wait(100).

+!go_on(X,Y) : building(X,Y) <-
    .print("Oh, a building! Looks like I have no choice but to turn.");
    !change_direction.


+!change_direction <-
    ?current_block(X,Y,D);
    jia.random_direction(X,Y,NewD); //draw a different direction that is free
    (NewD != D ->
        -+current_block(X,Y,NewD);
    !change_direction).


//Logs for percepts
+current_block(X, Y, D) <- .print("I'm in (", X, ", ", Y, "and the street's direction is ", D, ")").

