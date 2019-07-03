import java.io.*;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) throws FileNotFoundException {
        args[0] = "input\\initial_tape.txt";
        args[1] = "input\\description.txt";
        args[2] = "output\\final_tape.txt";
        
        Scanner scTape = new Scanner(new File(args[0]));
        StringBuilder firstTape = new StringBuilder(scTape.nextLine().trim());
        char initSym = firstTape.charAt(0);

        Scanner scDesc = new Scanner(new File(args[1]));
        int initCond = Integer.parseInt(scDesc.nextLine().trim().replace("First Condition: ", ""));
        
        State firstState = new State(initCond, initSym);
        Machine machine = new Machine(firstState, firstTape);

        int count = 1;
        while (scDesc.hasNextLine()) {
            count++;
            String lineDesc = scDesc.nextLine().trim();
            if (!lineDesc.matches("Q([0-9]{1,2})\\s[a-z0-9]\\s->\\sQ([0-9]{1,2})\\s[a-z0-9_]\\s([RHL])")) {
                System.out.println("Неверный формат файла с правилами (ошибка в строке " + count + ")");
                return;
            }
            String[] parts = lineDesc.split(" -> ");
            String[] cur = parts[0].split(" ");
            String[] next = parts[1].split(" ");
            cur[0] = cur[0].replace("Q", "");
            next[0] = next[0].replace("Q", "");
            int curCond = Integer.parseInt(cur[0]);
            int nextCond = Integer.parseInt(next[0]);
            char curSym = cur[1].charAt(0);
            char nextSym = next[1].charAt(0);
            Command cmd;
            if (next[2].equals("R")) {
                cmd = Command.R;
            } else if (next[2].equals("L")) {
                cmd = Command.L;
            } else cmd = Command.H;
            Rule newRule = new Rule(curCond, curSym, nextCond, nextSym, cmd);
            machine.addRule(newRule);
        }
    }

}
