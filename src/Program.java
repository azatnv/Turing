import java.io.*;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) throws FileNotFoundException {
        File initialTape = new File(args[0]);
        File description = new File(args[1]);
        File finalTape = new File(args[2]);
        if (!initialTape.exists()) {
            System.out.println("Файл не найден " + initialTape.getName());
            return;
        } else if (!description.exists()) {
            System.out.println("Файл не найден " + description.getName());
            return;
        } else if (!finalTape.exists()) {
            System.out.println("Файл не найден " + finalTape.getName());
            return;
        }

        Scanner scTape = new Scanner(initialTape);
        StringBuilder firstTape = new StringBuilder(scTape.nextLine().trim().replace(' ', '_'));
        if (firstTape.length() == 0) {
            System.out.println("Ошибка: неверный формат файла " + initialTape.getName());
            return;
        }
        char initSym = firstTape.charAt(0);

        Scanner scDesc = new Scanner(description);
        String strInitCond = scDesc.nextLine();
        if (!strInitCond.matches("^First\\sCondition:\\s\\d+$")) {
            System.out.println("Ошибка: неверный формат файла " + description.getName());
            return;
        }
        int initCond = Integer.parseInt(strInitCond.trim().replace("First Condition: ", ""));

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
            String[] cur = parts[0].split("\\s");
            String[] next = parts[1].split("\\s");
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
                System.out.println("Ошибка: правило не было добавлено из-за превышения допустимого числа состояний 99.\nСтрока: " + lineDesc);
                return;
            }
            if (nextCond == 0)
                hasStop = true;
        }
        if (!hasStop) {
            System.out.println("Ошибка: в машине отсутствует останов (сотояние Q0)!");
            return;
        }
        scTape.close();
        scDesc.close();

        try {
            String result = machine.fullProcess();
            if (!machine.hasProblem()) {
                System.out.println("Машина готова к отладке (выходной файл записан). Всего шагов " + machine.getAmountSteps() +
                        "\nКонечная лента: \"" + result + "\"");
                FileWriter out = new FileWriter(finalTape);
                out.write("Начальная лента: \"" + firstTape + "\"\n");
                out.write("Конечная лента:  \"" + result + "\"\n");
                out.close();
            } else {
                System.out.println("Машина не завершает своё выполнение (выходной файл не записан). Шаг прерывания " + machine.getAmountSteps() +
                        "\nЛента: \"" + result + "\"");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String str;
            System.out.println("\nПроизвести отладку? (yes/no)");
            while (true) {
                str = in.readLine();
                if (str.equals("no")) return;
                else if (str.equals("yes")) break;
                else System.out.println("Введите ваш ответ \"yes\" или \"no\"");
            }
            machine.refresh();
            System.out.println("\nДоступные команды: \n  1) breakpoint [число] [число] ..." +
                                                  "\n     (устанавливает точки останова на нескольких шагах)" +
                                                  "\n  2) c" +
                                                  "\n     (машина продолжает работу до следующей точки останова)" +
                                                  "\n  3) s" +
                                                  "\n     (выполняет одно правило - один или два шага (либо машина стоит на месте))" +
                                                  "\n  4) stop " +
                                                  "\n     (завершает работу машины)\n\n");
            System.out.println("шаг 0: \"" + firstTape.insert(1, '|').insert(0, '|') + "\", " +
                    "Q" + firstState.getCondition() + " - начальные параметры");
            System.out.println("Начните вводить команды");
            int allSteps = machine.getAmountSteps();
            int i = 0;
            Queue<Integer> breakpoints = new PriorityQueue<>();
            breakpoints.add(allSteps);
            str = in.readLine();
            while (!str.equals("stop") && i < allSteps) {
                if (str.matches("breakpoint(\\s\\d+)+")) {
                    str = str.replace("breakpoint ", "");
                    String[] points = str.split("\\s");
                    for (String el: points) {
                        Integer num = Integer.valueOf(el);
                        if (num < allSteps && num >= 1)
                            breakpoints.add(num);
                    }
                } else if (str.equals("c")) {
                    Integer point = breakpoints.poll();
                    while (true) {
                        if (point != null && point <= i) {
                            point = breakpoints.poll();
                        } else break;
                    }
                    if (point != null) {
                        while (i < point - 1) {
                            i = printStep(i, machine, true);
                        }
                        int old = i;
                        while (old == i) {
                            i = printStep(i, machine, false);
                        }
                        if (i == allSteps) {
                            System.out.println("Машина закончила работу");
                            return;
                        }
                    }
                } else if (str.equals("s")) {
                    i = printStep(i, machine, false);
                    if (i == allSteps) {
                        System.out.println("Машина закончила работу");
                        return;
                    }
                } else {
                    System.out.println("введите одну из команд");
                    str = in.readLine();
                    continue;
                }
                str = in.readLine();
            }
            System.out.println("Машина закончила работу");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int printStep(int i, Machine machine, boolean isContinue) {
        machine.performStep();
        if (machine.getBetweenTwoSteps()[0] != null) {
            i++;
            if (!isContinue)
                System.out.println("шаг " + i + ": " + machine.getBetweenTwoSteps()[0]);
        }
        if (machine.getBetweenTwoSteps()[1] != null) {
            i++;
            if (!isContinue)
                System.out.println("шаг " + i + ": " + machine.getBetweenTwoSteps()[1]);
        }
        return i;
    }
}