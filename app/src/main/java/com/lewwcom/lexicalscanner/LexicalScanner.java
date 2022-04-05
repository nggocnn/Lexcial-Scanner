package com.lewwcom.lexicalscanner;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LexicalScanner {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    private final State initialState;

    /**
     * Constructor of Lexical Scanner.
     *
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

        // Get all end states' name
        String endStatesNamesLine = scanner.nextLine();
        String[] endStatesNames = endStatesNamesLine.split("\\s");

        // Get all states that have next states
        String haveNextStatesLine = scanner.nextLine();
        int[] haveNextStates = Stream.of(haveNextStatesLine.split("\\s"))
                .mapToInt(Integer::parseInt).sorted().toArray();

        // Search and set which state is an end state
        State[] states = new State[totalStates];
        Arrays.setAll(states, i -> {
            int endStateIndex = Arrays.binarySearch(endStates, i);
            boolean isEnd = endStateIndex >= 0;
            boolean haveNextState = Arrays.binarySearch(haveNextStates, i) >= 0;
            String stateName = isEnd ? endStatesNames[endStateIndex] : "invalid";
            return new State(isEnd, haveNextState, stateName);
        });

        // Set initial state
        initialState = states[initialStateIndex];

        // Parse input for state transition
        String[] regexs = scanner.nextLine().trim().split("\\s+");

        // Parse state transition index
        for (State state : states) {
            if (state.haveNextState()) {
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
     *
     * @param input input stream.
     * @param output output stream.
     * @throws IOException scan error.
     */
    public void scan(InputStream input, OutputStream output) throws IOException {
        PushbackReader reader = new PushbackReader(new InputStreamReader(input));
        PrintStream printStream = new PrintStream(output);

        State state = this.initialState;
        StringBuilder token = new StringBuilder();

        for (int nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            char c = (char) nextChar;

            State nextState = state.nextState(c);
            if (nextState != null) {
                token.append(c);
                state = nextState;
            } else {
                handleNoNextState(state, token.toString(), c, printStream);
                if (state != this.initialState) {
                    reader.unread(c);
                    state = this.initialState;
                }
                token.setLength(0);
            }
        }

        if (token.length() > 0) {
            handleNoNextState(state, token.toString(), '0', printStream);
        }

        reader.close();
        printStream.close();
    }

    /**
     * Handle whether next state is available or not.
     *
     * @param currentState current state.
     * @param currentToken current token.
     * @param nextChar next character.
     */
    private void handleNoNextState(State currentState, String currentToken, char nextChar, PrintStream printStream) {
        if (currentState.isEnd()) {
            String beautifiedToken = currentToken.replace("\n", "\\n");
            System.out.printf("- %s (%s)%n", beautifiedToken, currentState.stateName());
            printStream.printf("%s (%s)%n", beautifiedToken, currentState.stateName());
        } else if (!isWhitespace(nextChar)) {
            System.err.println("Error: current string is '" + currentToken + "', but next char is "
                    + nextChar);
        }
    }

    /**
     * Check next input character is a white space or not.
     *
     * @param c an input character.
     * @return whether c is a white space or not.
     */
    private boolean isWhitespace(char c) {
        return WHITESPACE_PATTERN.matcher(Character.toString(c)).matches();
    }

}
