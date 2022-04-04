package com.lewwcom.lexicalscanner;

import java.util.regex.Pattern;

public class Transition {

    private Pattern pattern;
    private State nextState;

    public Transition(String regex, State nextState) {
        pattern = Pattern.compile(regex);
        this.nextState = nextState;
    }

    public boolean matches(char c) {
        return pattern.matcher(Character.toString(c)).matches();
    }

    public State getNextState() {
        return nextState;
    }

}
