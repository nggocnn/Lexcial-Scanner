package com.lewwcom.lexicalscanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LexicalScanner {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
    private static final Pattern VERTICAL_WHITESPACE_PATTERN = Pattern.compile("\\v");

    private final State initialState;

    /**
     * Constructor of Lexical Scanner.
     *
     * @param input input stream of automaton file.
     */
    public LexicalScanner(InputStream input) {
        Scanner scanner = new Scanner(input);

        int totalStates = scanner.nextInt();
        int initialStateIndex = scanner.nextInt();
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

        // Init state list
        State[] states = new State[totalStates];
        Arrays.setAll(states, i -> {
            int endStateIndex = Arrays.binarySearch(endStates, i);
            boolean isEnd = endStateIndex >= 0;
            boolean haveNextState = Arrays.binarySearch(haveNextStates, i) >= 0;
            String stateName = isEnd ? endStatesNames[endStateIndex] : "Invalid";
            return new State(isEnd, haveNextState, stateName);
        });
        initialState = states[initialStateIndex];

        // Parse input for state transition
        String[] regexs = scanner.nextLine().trim().split("\\s+");
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
        scanner.close();
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

        State state = this.initialState;
        StringBuilder token = new StringBuilder();
        Set<String> tokens = new LinkedHashSet<>();
        List<String> errors = new ArrayList<>();
        int line = 1;

        for (int nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            char c = (char) nextChar;
            State nextState = state.nextState(c);
            if (nextState != null) {
                token.append(c);
                state = nextState;
            } else {
                handleNoNextState(state, token.toString(), c, line, tokens, errors);
                token.setLength(0);
                if (state != this.initialState) {
                    reader.unread(c);
                    state = this.initialState;
                    continue;
                }
            }
            if (isLineTerminator(c, reader)) {
                ++line;
            }
        }
        if (token.length() > 0) {
            handleNoNextState(state, token.toString(), Character.MIN_VALUE, line, tokens, errors);
        }

        toOutput(System.out, tokens, errors);
        toOutput(output, tokens, errors);
        reader.close();
    }

    /**
     * When transition is not exist for pair of <code>state</code> and <code>nextChar</code>,
     * determine whether this <code>state</code> is ending state or not and act accordingly.
     */
    private void handleNoNextState(State state, String token, char nextChar, int line,
            Set<String> tokens, List<String> errors) {
        if (state.isEnd()) {
            String beautifiedToken = token.replace("\n", "\\n");
            tokens.add(String.format("%s (%s)", beautifiedToken, state.getStateName()));

        } else if (!(match(nextChar, WHITESPACE_PATTERN) && state == initialState)) {
            String c = match(nextChar, VERTICAL_WHITESPACE_PATTERN) ? "newline"
                    : Character.toString(nextChar);
            errors.add(String.format("Error[Ln %d]: current string is: '%s', but next char is: %s",
                    line, token, c));
        }
    }

    /**
     * Recognized line terminators:
     * <ul>
     * <li>A newline (line feed) character ('\n'),
     * <li>A carriage-return character followed immediately by a newline character ("\r\n"),
     * <li>A standalone carriage-return character ('\r'),
     * <li>A next-line character ('\u0085'),
     * <li>A line-separator character ('\u2028'), or
     * <li>A paragraph-separator character ('\u2029').
     * </ul>
     */
    private boolean isLineTerminator(char currentChar, PushbackReader reader) throws IOException {
        if (currentChar == '\r') {
            int c = reader.read();
            if (c == -1 || (char) c == '\n') {
                return true;
            }
            reader.unread(c);
            return true;
        }
        return match(currentChar, VERTICAL_WHITESPACE_PATTERN);
    }

    /** Match <code>c</code> against pattern <code>pattern</code>. */
    private boolean match(char c, Pattern pattern) {
        return pattern.matcher(Character.toString(c)).matches();
    }

    private void toOutput(OutputStream output, Set<String> tokens, List<String> errors) {
        PrintStream printStream = new PrintStream(output);

        printStream.println("Tokens:");
        tokens.forEach(printStream::println);

        printStream.println("\nErrors:");
        errors.forEach(printStream::println);

        printStream.close();
    }

}
