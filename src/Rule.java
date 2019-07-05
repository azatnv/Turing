
class Rule {
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

    int getCurCond() { return curCond; }

    int getNextCond() { return nextCond; }

    char getCurSym() { return curSym; }

    char getNextSym() { return nextSym; }

    Command getCmd() { return cmd; }

    @Override
    public int hashCode() {
        int result = 83 * curCond + 7;
        result = 37 * result + curSym;
        return result;
    }
}
