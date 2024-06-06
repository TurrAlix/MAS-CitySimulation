package jia;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

import java.util.Random;

import city.WorldModel;


public class random_walk extends DefaultInternalAction {
    Random rnd = new Random();

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
        try {
            String sAction = null;

            WorldModel model = WorldModel.get();

            NumberTerm agx = (NumberTerm) terms[0];
            NumberTerm agy = (NumberTerm) terms[1];

            int iagx = (int) agx.solve();
            int iagy = (int) agy.solve();
            int itox = -1;
            int itoy = -1;
            while (!model.inGrid(itox, itoy)) {
                switch (rnd.nextInt(4)) {
                case 0:
                    itox = iagx - 1;
                    sAction = "street_left";
                    break;
                case 1:
                    itox = iagx + 1;
                    sAction = "street_right";
                    break;
                case 2:
                    itoy = iagy - 1;
                    sAction = "street_up";
                    break;
                case 3:
                    itoy = iagy + 1;
                    sAction = "street_down";
                    break;
                }
            }
            return un.unifies(terms[2], new Atom(sAction));
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
