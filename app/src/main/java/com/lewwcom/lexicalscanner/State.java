package com.lewwcom.lexicalscanner;

import java.util.ArrayList;
import java.util.List;

/**
 * State of Transition Graph.
 */
public class State {

    private boolean isEnd; // set this state to be end state
    private boolean haveNextSate; // set this state to have next state
    private List<Transition> transitions = new ArrayList<>(); // transitions list

    /**
     * State Constructor.
     * @param isEnd
     */
    public State(boolean isEnd, boolean haveNextSate) {
        this.isEnd = isEnd;
        this.haveNextSate = haveNextSate;

    }

    /**
     * Get next state with input character.
     * @param c input character.
     * @return next state in transition graph or null if next state is unavailable.
     */
    public State nextState(char c) {
        Transition transition =
                transitions.stream().filter(t -> t.match(c)).findFirst().orElse(null);
        return transition != null ? transition.getNextState() : null;
    }

    /**
     * Check end state.
     * @return whether state is end state or not.
     */
    public boolean isEnd() {
        return isEnd;
    }

    public boolean haveNextSate() {
        return haveNextSate;
    }

    /**
     * Add transition input into transitions list.
     * @param transition transition input.
     */
    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

}
