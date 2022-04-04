package com.lewwcom.lexicalscanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Lexical Scanner.
 */
public class LexicalScanner {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    private State initialState;

    /**
     * Constructor of Lexical Scanner.
     * @param input input stream of automaton file.
     */
    public LexicalScanner(InputStream input) {

        Scanner scanner = new Scanner(input); // Automaton file scanner

        // State list
        int totalStates = scanner.nextInt(); // Get total number of states in transition table
        int initialStateIndex = scanner.nextInt(); // Set the initial state's index

        scanner.nextLine();

        // Get all end states
        String endStatesLine = scanner.nextLine();
        int[] endStates = Stream.of(endStatesLine.split("\\s")).mapToInt(Integer::parseInt).sorted()
                .toArray();

        String haveNextStateLine = scanner.nextLine();
        int[] haveNextStates = Stream.of(haveNextStateLine.split("\\s")).mapToInt(Integer::parseInt).sorted()
                .toArray();

        // Search and set which state is an end state
        State[] states = new State[totalStates];
        Arrays.setAll(states, i -> {
            boolean isEnd = Arrays.binarySearch(endStates, i) >= 0;
            boolean haveNextState = Arrays.binarySearch(haveNextStates, i) >= 0;
            return new State(isEnd, haveNextState);
        });

        // Set initial state
        initialState = states[initialStateIndex];

        // Parse input for state transition
        String[] regexs = scanner.nextLine().split("\\s\\s");

        // Parse state transition index
        for (State state : states) {
            if (state.haveNextSate()) {
                for (String regex : regexs) {
                    int stateIndex = scanner.nextInt();
                    // Add transition if next state is available
                    if (stateIndex >= 0) {
                        state.addTransition(new Transition(regex, states[stateIndex]));
                    }
                }
            }
        }
        scanner.close(); // close automaton file scanner
    }

    /**
     * Scan, tokenize input string and print out tokens.
     * @param input input stream.
     * @throws IOException
     */
    public void scan(InputStream input) throws IOException {
        PushbackReader reader = new PushbackReader(new InputStreamReader(input));
        State state = this.initialState;
        StringBuilder token = new StringBuilder();


        for (int nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            char c = (char) nextChar;
            if (isWhitespace(c)) {
                continue;
            }

            State nextState = state.nextState(c);
            if (nextState != null) {
                token.append(c);
                state = nextState;
            } else {
                handleNoNextState(state, token.toString(), c);
                if (state != this.initialState) {
                    reader.unread(c);
                    state = this.initialState;
                }
                token.setLength(0);
            }
        }

        if (token.length() > 0) {
            handleNoNextState(state, token.toString(), '0');
        }
    }

    /**
     * Check next input character is a white space or not.
     * @param c an input character.
     * @return whether c is a white space or not.
     */
    private boolean isWhitespace(char c) {
        return WHITESPACE_PATTERN.matcher(Character.toString(c)).matches();
    }

    /**
     * Handle whether next state is available or not.
     * @param currentState current state.
     * @param currentToken current token.
     * @param nextChar next character.
     */
    private void handleNoNextState(State currentState, String currentToken, char nextChar) {
        if (currentState.isEnd()) {
            System.out.println(currentToken);
        } else {
            System.err.println("Error: current string is '" + currentToken + "', but next char is " + nextChar);
        }
    }

}
