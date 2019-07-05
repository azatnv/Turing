import java.io.*;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) throws FileNotFoundException {
        args[0] = ".\\input\\initial_tape";
        args[1] = ".\\input\\description";
        args[2] = ".\\output\\final_tape";

        Scanner scTape = new Scanner(new File(args[0]));
        StringBuilder firstTape = new StringBuilder(scTape.nextLine().trim());
        char initSym = firstTape.charAt(0);

        Scanner scDesc = new Scanner(new File(args[1]));
        int initCond = Integer.parseInt(scDesc.nextLine().trim().replace("First Condition: ", ""));

        State firstState = new State(initCond, initSym);
        Machine machine = new Machine(firstState, firstTape);

        int count = 1;
        boolean hasStop = false;
        while (scDesc.hasNextLine()) {
            count++;
            String lineDesc = scDesc.nextLine().trim();
            if (!lineDesc.matches("Q([0-9]{1,2})\\s[a-pr-z0-9_]\\s->\\sQ([0-9]{1,2})\\s[a-pr-z0-9_]\\s([RHL])")) {
                System.out.println("Ошибка: неверный формат файла с правилами\nСтрока: " + count + ")");
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
            if (!machine.addRule(newRule)) {
                System.out.println("Ошибка : правило не было добавлено из-за превышения допустимого числа состояний 99.\nСтрока: " + lineDesc);
                return;
            }
            if (nextCond == 0)
                hasStop = true;
        }
        if (!hasStop) {
            System.out.println("Ошибка: в машине отсутствует останов (сотояние Q0)!)");
            return;
        }
        scTape.close();
        scDesc.close();

        machine.fullProcess();
        if (machine.isOver()) {
            System.out.println("Машина завершила работу. Совершено шагов " + machine.getAmountSteps());
        }

        machine.refresh();
    }
}
