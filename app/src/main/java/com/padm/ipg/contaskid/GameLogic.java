package com.padm.ipg.contaskid;

import java.util.Random;

/** Pure Java rules for the six arithmetic levels. */
public final class GameLogic {
    public static final int QUESTIONS_PER_LEVEL = 10;

    private GameLogic() { }

    public static final class Question {
        public final int first;
        public final int second;
        public final char operator;
        public final int answer;

        public Question(int first, int second, char operator) {
            this.first = first;
            this.second = second;
            this.operator = operator;
            switch (operator) {
                case '+': answer = first + second; break;
                case '-': answer = first - second; break;
                case '*': answer = first * second; break;
                default: throw new IllegalArgumentException("Unknown operation");
            }
        }
    }

    public static Question newQuestion(int level, Random random) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("Level must be 1..6");
        }
        int first;
        int second;
        do {
            first = random.nextInt(10);
            second = random.nextInt(10);
        } while (level == 1 && first + second > 10);

        char operator;
        switch (level) {
            case 1:
            case 2:
                operator = '+';
                break;
            case 3:
                operator = '-';
                break;
            case 4:
                operator = random.nextBoolean() ? '+' : '-';
                break;
            case 5:
                operator = '*';
                break;
            default:
                operator = new char[] {'+', '-', '*'}[random.nextInt(3)];
                break;
        }
        if (operator == '-' && second > first) {
            int temp = first;
            first = second;
            second = temp;
        }
        return new Question(first, second, operator);
    }

    public static int targetScore(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("Level must be 1..6");
        }
        return level * QUESTIONS_PER_LEVEL;
    }
}
