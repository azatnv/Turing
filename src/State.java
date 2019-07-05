
public class State {
    private int condition;
    private char symbol;

    State(int condition, char symbol) {
        this.condition = condition;
        this.symbol = symbol;
    }

    public int getCondition() { return condition; }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public char getSymbol() { return symbol; }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

}
