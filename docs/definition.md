# Project Definition

## Algorithms

The idea is to implement the Thompson's construction algorithm to transform a regular expression (regex) into an equivalent nondeterministic finite automaton (NFA). This NFA will be used to match strings againts the regular expression. The program will also preprocess the string to allow for richer regex syntax (`[1-9], \d` etc). Finally the NFA is used to match the string.

If there is time I will implement a NFA to deterministic finite automaton (DFA) transformation with [powerset construction](https://en.wikipedia.org/wiki/Powerset_construction) and compare it's performance to the implementation that uses only NFA. The DFA will have `2^n` states where n is the number of states in NFA. This seems to imply that there could be a threshold m where if n <= m then it is faster to match input strings with a DFA but when n > m then it is faster to use the NFA because of the time it takes to construct a DFA and the time it takes to match strings with NFA vs DFA.   

#### Thompson's contruction

The algorithm works recursively by splitting an expression into its constituent subexpressions, from which the NFA will be constructed using [a set of rules](https://perl.plover.com/Regex/article.html) (*How to Turn a Regex into a Penny Machine*).

The algorithm has following properties: 

1. Has a single initial state which can't be reached from any other state.
2. Has a single final state which is connected to all accepting states with a empty transition.
3. The number of states leaving any state is at most two.
4. Since an NFA of m states and at most e transitions from each state can match a string of length n in time O(emn), a Thompson NFA can do pattern matching in linear time, assuming a fixed-size alphabet.

#### Regex preprocessing

Regex preprocessing removes all the syntactical sugar from the regex. For example `[1-5]` is tranformed into `(1|2|3|4|5)`. This allows for richer regexes making the program more powerful for the user while also making the internal workings of the program simpler. The preprocesser can also add useful metacharacters in the input string to make the work of the NFA constructor easier.

Other important thing done by preprocessing is transforming the expressions with `+` regex symbol into expressions with `*` regex symbol. This makes building the NFA's simpler because for example expressions `(ab)+` and `ab(ab)*` are equivalent.

#### Matching input

NFA is given the input string for matching. It handles the input by using these rules:

1. To start, enqueue the first state to active states queue Q.
2. Whenever the machine reads an input character c, dequeue all the states from Q one by one. If a dequeued state X has an outgoing transition(s) labeled c add the state(s) at the end of the transition(s) to Q. If there is a blank (ε) transition leading from state X to Y, then whenever X is enqueued to Q, also enqueue Y to Q. The machined continues enqueuing all subsequent states with blank transition recursively until it can't.
3. If there are still more characters in the input string return to 2 and read the next char c. 
4. When the input string is read. Dequeu all the states from Q if any one of them was the final state then the input string is matched. Otherwise the string is not matched.

## Data structures

The states created during construction of a NFA are their own data structure. They contain all the needed information to simulate a NFA state: what states are connection to it and what kind of a state it is (start, finish, other). 

A queue is used to store the active states during the runtime of the program.

Other data structures might be added as needs arise.

## The problems and solutions

Contructing NFA's is useful for programs that try to match the same regex multiple times because it is faster than simulating the regex when applied on multiple different strings. UNIX grep is a good example of this. It would be really slow if you had to simulate the regex on input big sizes without a NFA.

I chose Thompson's construction as the algorithm to perform the transformation because it is the original and most well known one. You can also find lots of material on it online.  

The data structures used here seemed to make the most sense. A state as a structure seems like a good idea and using a queue to help with handling the active states during matching the input is logical.

## Using the program

The program takes a regex and a string as an input. The regexes are used to contruct NFA's. The input string is matched against a regex. The program outputs a boolean value telling whether the string was matched or not.

## Target time and space complexity

* The target time complexity for preprocessing the regex is `O(n)` with space complexity of `O(n)`.
* The target time complexity for constructing a NFA is `O(n)` with space complexity of `O(n)`.
* The target time complexity for matching the string is `O(n)` with space complexity of `O(n)`. 

## Sources

[Thompson's contruction](https://en.wikipedia.org/wiki/Thompson%27s_construction)

[Powerset construction](https://en.wikipedia.org/wiki/Powerset_construction)

[A Regular Expression Matcher](http://www.cs.princeton.edu/courses/archive/spr09/cos333/beautiful.html)

[Regex in Perl](https://perl.plover.com/Regex/article.html)

