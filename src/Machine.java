import java.util.HashSet;
import java.util.Set;

public class Machine {
    static final int MAX_CONDITIONS = 99;
    static final int INITIAL_CURSOR_POSITION = 0;

    private State state;
    private Set<Rule> rules;
    private StringBuilder tape;
    private int cursor;

    Machine(State firstState, StringBuilder firstTape) {
        this.state = firstState;
        this.tape = firstTape;
        this.rules = new HashSet<>();
        this.cursor = INITIAL_CURSOR_POSITION;
    }

    void addRule(Rule rule) {
        rules.add(rule);
    }

    void fullProcess() {

    }
}
