{ include("$jacamoJar/templates/common-cartago.asl") }


/* Initial beliefs and rules */


/* Initial goals */
!start.


/* Plans */
+!start : true <- .wait(500); skip; !start.
