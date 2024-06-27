{ include("$jacamoJar/templates/common-cartago.asl") }

target(_ ,_ , _). //creating the template

/* Initial goals */
!live.

// ---------------------------------------------------------------------- //

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

// I'm not in the target position yet
+!goToPos(X,Y) : pos(AgX, AgY) & (not(AgX == X) | not(AgY == Y)) <-
    !next_step(X,Y).

// I'm in the office position
+!goToPos(X,Y) : pos(X, Y) & office(X, Y) <-
    .print("I'm at the office!");
    .wait(4000);
    ?supermarket(PX, PY);
    -+target("supermarket", PX, PY);
    .print("I'm going to the supermarket at ", supermarket(PX,PY), " now.");
    !goToPos(PX, PY).

// I'm in the supermarket position
+!goToPos(X,Y) : pos(X, Y) & supermarket(X, Y) <-
    .print("I'm at the supermarket!");
    .wait(4000);
    -+target("stop", -1, -1);
    .print("I FINISH MY DAAAAY!");
    .wait(1000000). // MAYBE NOT NEEDED IT HAS JUST FINIHED THE GOALS

// Failure handling for pos goal
-!goToPos(X,Y)
    <- .print("Failed to move to position: ", X, ", ", Y);
        .wait(1000);
        !goToPos(X,Y).

// ---------------------------------------------------------------------- //

/* These are the plans to have the pedestrian walk in the direction of X,Y.
 It uses the internal action jia.get_direction which encodes a search algorithm.  */
+!next_step(X,Y) : pos(AgX, AgY) <-
    jia.get_dir(AgX, AgY, X, Y, D);
    .wait(400);
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

+success("up") <-
    .print("Went up!");
    //-+busy(0);
    //?busy(B);
    ?target(_, X, Y);
    !goToPos(X, Y).
+fail("up",P) <-
    .print("Cannot go up");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("down") <-
    .print("Went down!");
    //-+busy(0);
    //?busy(B);
    ?target(_, X, Y);
    !goToPos(X, Y).
+fail("down",P) <-
    .print("Cannot go down");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("right") <-
    .print("Went right!");
    //-+busy(0);
    //?busy(B);
    ?target(_, X, Y);
    !goToPos(X, Y). 
+fail("right",P) <-
    .print("Cannot go right");
    ?target(_, X, Y);
    !next_step(X, Y).

+success("left") <-
    .print("Went left!"); 
    //-+busy(0);
    //?busy(B);
    ?target(_, X, Y);
    !goToPos(X, Y).   
+fail("left",P) <-
    .print("Cannot go left");
    ?target(_, X, Y);
    !next_step(X, Y).


+pos(X, Y) <- .print("I'm in (", X, ", ", Y, ")").

/*
//obs1
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
*/

//obs2
+whoL(X,Y, W, Id) : W == pedestrian
    <-  
    //?agent_name(Id, Name);
    //.print("I'm near: " , Name);
    //!sayHello(Name);
    .print("i say hello to the child!");
    .send(pedestrian_child, tell, "hello");
    .wait(1000).

+whoR(X,Y, W, Id) : W == pedestrian 
    <-  
    //?agent_name(Id, Name);
    //.print("I'm near: " , Name);
    //!sayHello(Name);
    .print("i say hello to the child!");
    .send(pedestrian_child, tell, "hello");
    .wait(1000).

+whoU(X,Y, W, Id) : W == pedestrian
    <-  
    //?agent_name(Id, Name);
    //.print("I'm near: " , Name);
    //!sayHello(Name);
    .print("i say hello to the child!");
    .send(pedestrian_child, tell, "hello");
    .wait(1000).

+whoD(X,Y, W, Id) : W == pedestrian
    <-  
    //?agent_name(Id, Name);
    //.print("I'm near: " , Name);
    //!sayHello(Name);
    .print("i say hello to the child!");
    .send(pedestrian_child, tell, "hello");
    .wait(1000).


//+!kqml_received(Sender, Performative, Content, MsgId)
+!kqml_received(pedestrian_child, tell, "nice_to_meet_you", MsgId) <-
    .print("Received 'nice_to_meet_you' from ", pedestrian_child, "!");
    .send(pedestrian_child, tell, "nice_to_meet_you_too");
    .print("Sent 'Nice to meet you' to ", pedestrian_adult, "!").



// ---------------------------------------------------------------------- //
//NOT WORKING

/*
+!sayHello <-
    .print("Sending 'hello!' to ", Name);
    .send(Name, tell, "hello").
-!sayHello(Id) <-
    .print("Failed to say hello to ", Id);
    .wait(1000).

+hello[source(Sender)] <-
    .print("Received 'Hello' from ", Sender, "!");
    .send(Sender, tell, nice_to_meet_you);
    .print("Sent 'Nice to meet you' to ", Sender, "!").
-!received_hello<-
    .print("Failed to received hello");
    .wait(1000).

+!received_nice_to_meet_you[content(nice_to_meet_you), sender(Sender)] <- 
    .print("Received 'Nice to meet you' from ", Sender, "!").
-!received_nice_to_meet_you<-
    .print("Failed to received nice to meet you");
    .wait(1000).
*/
// ---------------------------------------------------------------------- //
