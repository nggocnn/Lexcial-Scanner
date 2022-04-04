package com.lewwcom.lexicalscanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LexicalScanner {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    private State initialState;

    public LexicalScanner(InputStream input) {
        Scanner scanner = new Scanner(input);

        // State list
        int totalStates = scanner.nextInt();
        int initialStateIndex = scanner.nextInt();
        scanner.nextLine();

        String endStatesLine = scanner.nextLine();
        int[] endStates = Stream.of(endStatesLine.split("\\s")).mapToInt(Integer::parseInt).sorted()
                .toArray();

        State[] states = new State[totalStates];
        Arrays.setAll(states, i -> {
            boolean isEnd = Arrays.binarySearch(endStates, i) >= 0;
            return new State(isEnd);
        });
        initialState = states[initialStateIndex];

        // Transitions
        String[] regexs = scanner.nextLine().split("\\s");
        for (State state : states) {
            for (String regex : regexs) {
                int stateIndex = scanner.nextInt();
                if (stateIndex >= 0) {
                    state.addTransition(new Transition(regex, states[stateIndex]));
                }
            }
        }

        scanner.close();
    }

    public void scan(InputStream input) throws IOException {
        PushbackReader reader = new PushbackReader(new InputStreamReader(input));
        State state = initialState;
        StringBuilder token = new StringBuilder();

        for (int nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            char c = (char) nextChar;
            if (isWhitespace(c)) {
                continue;
            }
            State nextState = state.nextState(c);
            if (nextState != null) {
                token.append(Character.toString(c));
                state = nextState;
            } else {
                handleNoNextState(state, token.toString(), c);
                if (state != initialState) {
                    reader.unread(c);
                    state = initialState;
                }
                token.setLength(0);
            }
        }
        if (token.length() > 0) {
            handleNoNextState(state, token.toString(), '0');
        }
    }

    private boolean isWhitespace(char c) {
        return WHITESPACE_PATTERN.matcher(Character.toString(c)).matches();
    }

    private void handleNoNextState(State state, String token, char nextChar) {
        if (state.isEnd()) {
            System.out.println(token);
        } else {
            System.err.println("Error: current string: " + token + "; next char: " + nextChar);
        }
    }

}
