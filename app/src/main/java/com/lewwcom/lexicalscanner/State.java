package com.lewwcom.lexicalscanner;

import java.util.ArrayList;
import java.util.List;

/**
 * State of Transition Graph.
 */
public class State {

    private boolean isEnd; // set this state to be end state
    private boolean haveNextState; // set this state to have next state
    private String stateName;
    private List<Transition> transitions = new ArrayList<>(); // transitions list

    /**
     * State Constructor.
     *
     * @param isEnd If the current state is an end state
     * @param haveNextState If the current state have edges going out
     * @param stateName The textual name of the state
     */
    public State(boolean isEnd, boolean haveNextState, String stateName) {
        this.isEnd = isEnd;
        this.haveNextState = haveNextState;
        this.stateName = stateName;
    }

    /**
     * Get next state with input character.
     *
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
     *
     * @return whether state is end state or not.
     */
    public boolean isEnd() {
        return isEnd;
    }

    public boolean haveNextState() {
        return haveNextState;
    }

    public String stateName() { return stateName; }

    /**
     * Add transition input into transitions list.
     *
     * @param transition transition input.
     */
    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

}
