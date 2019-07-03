
public class Rule {
    private int curCond;
    private int nextCond;
    private char curSym;
    private char nextSym;
    private Command cmd;

    Rule(int curCond, char curSym, int nextCond, char nextSym, Command cmd) {
        this.curCond = curCond;
        this.nextCond = nextCond;
        this.curSym = curSym;
        this.nextSym = nextSym;
        this.cmd = cmd;
    }

    public int getCurCond() { return curCond; }

    public int getNextCond() { return nextCond; }

    public char getCurSym() { return curSym; }

    public char getNextSym() { return nextSym; }

    public Command getCmd() { return cmd; }

    @Override
    public int hashCode() {
        int result = 83 * curCond + 7;
        result = 37 * result + curSym;
        return result;
    }
}
