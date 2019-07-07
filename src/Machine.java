import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Machine {
    private static final int MAX_CONDITIONS = 99;
    private static final int INITIAL_CURSOR_POSITION = 0;
    private static final int MAX_TAPE_LENGTH = 100000;
    private static final int MAX_STEPS = 1000000;

    private State startState;
    private StringBuilder startTape;

    private Set<Rule> rules;
    private ArrayList<String> steps;
    private String[] betweenTwoSteps = new String[2];

    private State state;
    private StringBuilder finishTape;
    private int tapeLength;
    private int cursor;

    private boolean isOver = false;
    private int amountSteps = 0;


    Machine(State firstState, StringBuilder firstTape) {
        this.state = new State(firstState.getCondition(), firstState.getSymbol());
        this.startState = new State(firstState.getCondition(), firstState.getSymbol());
        this.startTape = new StringBuilder(firstTape);
        this.finishTape = new StringBuilder(firstTape);
        this.tapeLength = firstTape.length();
        this.rules = new HashSet<>();
        this.steps = new ArrayList<>();
        this.cursor = INITIAL_CURSOR_POSITION;
    }

    boolean addRule(Rule rule) {
        if (rule.getNextCond() > MAX_CONDITIONS || rule.getCurCond() > MAX_CONDITIONS)
            return false;
        rules.add(rule);
        return true;
    }

    String fullProcess() {
        int countSteps = 0;
        while (state.getCondition() != 0) {
            int exitCode = performStep();
            if (exitCode == -1) {
                amountSteps = countSteps;
                return String.valueOf(finishTape);
            } else countSteps += exitCode;
            if (countSteps > MAX_STEPS) {
                amountSteps = countSteps;
                System.out.println("Превышено число шагов 1000000");
                return String.valueOf(finishTape);
            }
            if (tapeLength > MAX_TAPE_LENGTH) {
                amountSteps = countSteps;
                System.out.println("Длина ленты слишком большая (шаг " + countSteps + ")");
                return String.valueOf(finishTape);
            }
        }
        amountSteps = countSteps;
        isOver = true;
        return String.valueOf(finishTape);
    }

    private void addStep(int index) {
        StringBuilder temp = new StringBuilder(finishTape);
        temp.insert(cursor + 1, '|');
        temp.insert(cursor, '|');
        steps.add(String.valueOf(temp));
        betweenTwoSteps[index] = "\""+ temp + "\" Q" + state.getCondition();
    }

    int performStep() {
        betweenTwoSteps[0] = null;
        betweenTwoSteps[1] = null;
        int result = 0;
        int currentCond = state.getCondition();
        char currentSym = state.getSymbol();
        int nextCond = -1;
        char nextSym = ' ';
        Command cmd = Command.H;
        for (Rule rule: rules) {
            if (currentCond == rule.getCurCond() && currentSym == rule.getCurSym()) {
                nextCond = rule.getNextCond();
                nextSym = rule.getNextSym();
                cmd = rule.getCmd();
                break;
            }
        }
        if (nextSym == ' ' || nextCond == -1) {
            System.out.println("Ошибка: неполное описание состояния Q" + currentCond + "!");
            return -1;
        } else if (nextSym != currentSym) {
            if (nextSym == '_') {
                finishTape.deleteCharAt(cursor);
                finishTape.insert(cursor, '_');
                state.setSymbol('_');
            } else {
                finishTape.deleteCharAt(cursor);
                finishTape.insert(cursor, nextSym);
                state.setSymbol(nextSym);
            }
            addStep(0);
            result += 1;
        }

        state.setCondition(nextCond);

        if (cmd == Command.R) {
            cursor++;
            if (cursor == tapeLength) {
                finishTape.append(new StringBuilder(" "));
                tapeLength++;
            }
        } else if (cmd == Command.L)  {
            cursor--;
            if (cursor == -1) {
                finishTape = new StringBuilder(" ").append(finishTape);
                cursor = 0;
            }
        }
        if (cmd != Command.H) {
            addStep(1);
            result += 1;
        }

        state.setSymbol(finishTape.charAt(cursor));
        return result;
    }

    List<String> getSteps(int to) {
        return steps.subList(0, to);
    }

    void refresh() {
        tapeLength = startTape.length();
        finishTape = new StringBuilder(startTape);
        state = new State(startState.getCondition(), startState.getSymbol());
        cursor = INITIAL_CURSOR_POSITION;
        steps.clear();
    }

    String[] getBetweenTwoSteps() { return betweenTwoSteps; }

    boolean isOver() { return isOver; }

    int getAmountSteps() { return amountSteps; }
}
