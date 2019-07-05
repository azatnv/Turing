class State {
    private int condition;
    private char symbol;

    State(int condition, char symbol) {
        this.condition = condition;
        this.symbol = symbol;
    }

    int getCondition() { return condition; }

    void setCondition(int condition) {
        this.condition = condition;
    }

    char getSymbol() { return symbol; }

    void setSymbol(char symbol) {
        this.symbol = symbol;
    }

}
