package jia;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;
import SearchAlgorithm.AStarSearch;

import city.WorldModel;

public class get_dir extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
        try {
            String sAction = "skip";

            WorldModel model = WorldModel.get();
            int iagx = (int) ((NumberTerm) terms[0]).solve();
            int iagy = (int) ((NumberTerm) terms[1]).solve();
            int itox = (int) ((NumberTerm) terms[2]).solve();
            int itoy = (int) ((NumberTerm) terms[3]).solve();
            System.out.println("GETDIR: itoy: " + itoy + " itox: " + itox);

            if (model.inGrid(itox, itoy)) {
                sAction = AStarSearch.getDirection(iagx, iagy, itox, itoy);
                System.out.println("GET DIR: action from " + iagx + "x" + iagy + " to " + itox + "x" + itoy + ": \t" + sAction);
                if (sAction.equals("skip")) {
                    System.out.println("No route from " + iagx + "x" + iagy + " to " + itox + "x" + itoy + "!");
                }
            }
            return un.unifies(terms[4], new Atom(sAction));
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
