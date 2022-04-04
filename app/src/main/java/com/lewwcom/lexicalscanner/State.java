package com.lewwcom.lexicalscanner;

import java.util.ArrayList;
import java.util.List;

public class State {

    private boolean isEnd = false;
    private List<Transition> transitions = new ArrayList<>();

    public State(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public State nextState(char c) {
        Transition transition =
                transitions.stream().filter(t -> t.matches(c)).findFirst().orElse(null);
        return transition != null ? transition.getNextState() : null;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

}
