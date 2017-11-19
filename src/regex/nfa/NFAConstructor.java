package regex.nfa;

import regex.input.RegexSubstring;
import regex.util.CharacterClassifier;
import regex.util.StateType;

public class NFAConstructor {

    private static RegexSubstring regex;

    /**
     * @param input regex
     * @return first state of the NFA
     */
    public NFAState constructNFA(String input) {
        System.out.println("input: " + input);
        NFAState start = new NFAState(StateType.START, 'ε');
        NFAState epsilon = new NFAState(StateType.NORMAL, 'ε');
        start.setNext(epsilon);
        NFAState prev = recursiveBuild(epsilon, epsilon, new RegexSubstring(input));
        prev.setNext(new NFAState(StateType.END, 'ε'));
        return start;
    }

    /**
     * @param componentStart first state of the part currently under construction
     * @param prev the previous NFAState
     * @param regex the input regex
     * @return last state of the constructed NFA part
     */
    private NFAState recursiveBuild(NFAState componentStart, NFAState prev, RegexSubstring regex) {
        while (regex.hasNextChar()) {
            char c = regex.getNextChar();
            System.out.println("current char " + c);
            if (CharacterClassifier.isRegexCharacter(c)) {
                prev = handleRegexSymbol(componentStart, prev, regex, c);
            } else {
                switch (c) {
                    /*
                    case '(':
                        current = recursiveBuild(new NFAState(regex.getNextChar()), prev, regex.getExpression());
                    case ')':
                        if (CharacterClassifier.isRegexCharacter(regex.peekNextChar())) {
                            return handleRegexSymbol(start, prev, regex, regex.getNextChar());
                        }
                        return prev;
                    */
                    default:
                        System.out.println("default");
                        NFAState current = new NFAState(c);
                        prev.setNext(current);
                        prev = current;
                        break;
                }
            }
        }
        return prev;
    }
    
    /**
     * @param componentStart first state of the part currently under construction
     * @param prev the previous NFAState
     * @param regex the input regex
     * @param c current character from the input string
     * @return last state of the constructed NFA part
     */
    private NFAState handleRegexSymbol(NFAState componentStart, NFAState prev, RegexSubstring regex, char c) {
        switch (c) {
            case '|':
                prev = union(componentStart, prev, regex);
                break;
            case '*':
                prev = kleeneStar(componentStart, prev, regex);
                break;
        }
        return prev;
    }

    //doesn't work
    /**
     * @param componentStart first state of the part currently under construction
     * @param prev the previous NFAState
     * @param regex the input regex
     * @return last state of the constructed NFA kleene start part
     */
    private NFAState kleeneStar(NFAState componentStart, NFAState prev, RegexSubstring regex) {
        NFAState starFirst = new NFAState('ε');
        prev.setNext(starFirst);
        recursiveBuild(componentStart, starFirst, regex);
        NFAState starLast = new NFAState('ε');
        prev.setNext(starLast);
        return starLast;
    }

    /**
     * @param componentStart first state of the part currently under construction
     * @param prev the previous NFAState
     * @param regex the input regex
     * @return last state of the constructed NFA union part
     */
    private NFAState union(NFAState componentStart, NFAState prev, RegexSubstring regex) {
        //Union first split
        NFAState startA = componentStart.arrowA; //arrowA component is already built
        componentStart.arrowA = null;
        NFAState unionFirst = new NFAState('ε'); //new epsilon transition to union split
        componentStart.setNext(unionFirst);
        unionFirst.setNext(startA);
        //Union second split
        NFAState unionSecond = new NFAState('ε'); //new epsilon transition to union split
        componentStart.setNext(unionSecond);
        NFAState unionSecondEnd = recursiveBuild(unionSecond, unionSecond, regex);
        //Link both union sides to a union end state
        NFAState unionEnd = new NFAState('ε');
        prev.setNext(unionEnd); //link first union part to union end state
        unionSecondEnd.setNext(unionEnd); //link second union part to union end state
        return unionEnd;
    }

}
