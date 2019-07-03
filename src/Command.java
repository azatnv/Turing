
public enum Command {
    R (1),
    L (-1),
    H (0);

    private int move;

    Command(int a) {
        this.move = a;
    }

    public int getMove() {
        return move;
    }
}
