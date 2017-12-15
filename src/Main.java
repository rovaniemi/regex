
import regex.benchmark.RegexBenchmark;
import regex.nfa.*;
import regex.input.RegexStringPreprocessor;

public class Main {

    /**
     * @author Joonas Sarapalo
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            warnAndExit("No arguments received!");
        }
        if (args[0].equals("--help")) {
            helpAndExit();
        }
        if (args.length < 2) {
            warnAndExit("Regex and input string required!");
        }

        String regex = args[0];
        String input = args[1];

        //UNNECESSARY?
        /*if (input.length() != 0) {
            if ((input.charAt(0) == '\'' && input.charAt(input.length() - 1) != '\'') ||
                    (input.charAt(0) != '\'' && input.charAt(input.length() - 1) == '\'')) {
                warnAndExit("Input string required!");
            }
        }*/
        char type;
        switch (args.length) {
            case 2: //no test flag set
                runProgram(regex, input);
                break;
            //REFAKTOROI => ei type check toistoa, testaa java regexillä onko valid (ei tarte palauttaa testeiltä)
            case 3: //no specific run times argument set
                type = args[2].charAt(0);
                if (type == 'r' || type == 'v') { //my regex versus java regex
                    if (!RegexBenchmark.getBenchmark(type, 1, regex, input)) {
                        warnAndExit("Input string didn't match the regex!");
                    }
                    break;
                }
                int mul = 1;
                for (int i = 0; i < 3; i++) {
                    long timeStart = System.currentTimeMillis();
                    int n = 10 * mul;
                    if (!RegexBenchmark.getBenchmark(type, n, regex, input)) {
                        warnAndExit("Input string didn't match the regex!");
                    }
                    mul *= 10;
                    long timeEnd = System.currentTimeMillis();
                    System.out.println(n + " runs of the program with parameters: [ " + regex + ", " + input + " ]\n"
                            + "Total time: " + (timeEnd - timeStart) + "ms. \nAverage time: " + ((double) (timeEnd - timeStart) / n) + "ms.");
                }
                break;
            case 4:
                try {
                    type = args[2].charAt(0);
                    if (type == 'r' || type == 'v') { //my regex versus java regex
                        if (!RegexBenchmark.getBenchmark(type, 1, regex, input)) {
                            warnAndExit("Input string didn't match the regex!");
                        }
                        break;
                    }
                    long timeStart = System.currentTimeMillis();
                    if (!RegexBenchmark.getBenchmark(type, Long.parseLong(args[3]), regex, input)) {
                        warnAndExit("Input string didn't match the regex!");
                    }
                    long timeEnd = System.currentTimeMillis();
                    System.out.println("1 run of the program with parameters: [ " + regex + ", " + input + " ]\n"
                            + "Total time: " + (timeEnd - timeStart) + "ms.");
                } catch (Exception e) {
                    System.out.println("Fourth argument must be an integer!");
                    System.exit(1);
                }
                break;
            default:
                warnAndExit("Too many arguments provided!");
        }
    }

    /**
     * @param regex
     * @param input
     */
    private static void runProgram(String regex, String input) {
        String preprocessed = RegexStringPreprocessor.parseInput(regex);
        NFAState start = new NFAConstructor().constructNFA(preprocessed);
        boolean result = new NFAMatcher(start).match(input);
        System.out.println("Matching result with\n"
                + "  regex: '" + regex + "'\n"
                + "  input: '" + input + "'\n"
                + "  result: " + result);
    }

    /**
     * @param message warning message
     */
    private static void warnAndExit(String message) {
        System.out.println(message + "\n"
                + "Give arguments in form: \" regex input \" or \" regex 'input' \" without the \" characters.\n"
                + "Type --help to see info.");
        System.exit(1);
    }

    private static void helpAndExit() {
        System.out.println("Give arguments in form: \" regex input \" or \" regex 'input' \" without the \" characters.\n"
                + "(Empty string can be input like this '')\n\n"
                + "Regex argument supports:\n"
                + "  Symbols:\n"
                + "    parentheses: (), for example (ab)\n"
                + "    kleenestar: *, for example a*\n"
                + "    plus: +, for example a+\n"
                + "    union: |, for example (a|b) (IMPORTANT! Expects parentheses.)\n"
                + "  Character classes (match certain type of character):\n"
                + "    any character - .\n"
                + "    digit - \\\\d\n"
                + "    alphabet - \\\\a\n"
                + "    lowercase character - \\\\l\n"
                + "    uppercase character - \\\\u\n"
                + "    alphabet, digit and _ - \\\\w\n\n"
                + "Optional 3rd argument: test type can be provided after regex and input arguments\n"
                + "Optional 4th argument: run times can be provided to run the tests a specific number of times\n"
                + "Without 4th parameter the default mode with multiple runs is done\n"
                + "  Test types:\n"
                + "    a: benchmark whole process with all prints\n"
                + "    w: benchmark whole process\n"
                + "    p: benchmark regex preprocessing\n"
                + "    c: benchmark nfa construction\n"
                + "    m: benchmark input string matching\n"
                + "    f: benchmark matching words from a file (provide file location as the input string)\n"
                + "    v: benchmark comparisons against Java Matcher.find() from a file (provide file location as the input string)\n"
                + "    r: benchmark comparisons against Java Patter.match()\n\n"
                + "Examples:"
                + "  \"java -jar regex.jar a+ aa\" - Normal run, regex a+ with input aa. \n"
                + "  \"java -jar regex.jar a* ''\" - Normal run, regex a* with input  .\n"
                + "  \"java -jar regex.jar (a|b)* aaaaabba w\" - Benchmark the whole program, regex (a|b)* with input aaaaabba.\n"
                + "  \"java -jar regex.jar \\\\a*. abcd_ m\" - Benchmark matching, regex \\\\a*. with input abcd_.");
        System.exit(1);
    }

    /*TODO LIST:
    0. BUGIT: 
    true: 'abab', 'abcd', 'cdabcdab'
    false: '', 'ab', 'cd'
    //String regex = "(ab|cd)+"; //b*a* toimii
    //String input = "cdabcdabcdab";
    * sulkeet aiheuttaa bugin -> input: "", regex: b+ => false, regex: (b)+ => true 
    1. file reading mittaukset (vs java regex) 
    2. testimittaukset ja niiden esittäminen (pandas?)
    3. dokumentaatio
    4. loppu hionta kaikkeen => katso se lista
    ----------------------------------------------
    x. preprosessointi (lisä syntaksia, kuten [0-9], [a-zA-Z], \*)
    x.1 muuta regex symbolit vastaamaan lukuja => nyt kaikki \char voidaan esiprosessoida => NFAkonstruktorin ei tarvitse välittää \ merkistä
    y. paremmat kommentit nfa konstruktoriin + siistiminen
     */
}
