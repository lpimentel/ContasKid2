package com.padm.ipg.contaskid;

import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;

public class GameLogicTest {
    @Test
    public void questionRulesHoldForAllLevels() {
        Random random = new Random(2026L);
        for (int level = 1; level <= 6; level++) {
            for (int i = 0; i < 1000; i++) {
                GameLogic.Question q = GameLogic.newQuestion(level, random);
                assertTrue(q.first >= 0 && q.first <= 9);
                assertTrue(q.second >= 0 && q.second <= 9);
                if (level == 1) assertTrue(q.first + q.second <= 10);
                if (level == 1 || level == 2) assertEquals('+', q.operator);
                if (level == 3) assertEquals('-', q.operator);
                if (level == 5) assertEquals('*', q.operator);
                if (q.operator == '-') assertTrue(q.first >= q.second);
                switch (q.operator) {
                    case '+': assertEquals(q.first + q.second, q.answer); break;
                    case '-': assertEquals(q.first - q.second, q.answer); break;
                    case '*': assertEquals(q.first * q.second, q.answer); break;
                    default: fail("Unexpected operator");
                }
            }
            assertEquals(level * 10, GameLogic.targetScore(level));
        }
    }

    @Test
    public void questionAlwaysContainsValidArithmetic() {
        assertEquals(9, new GameLogic.Question(4, 5, '+').answer);
        assertEquals(3, new GameLogic.Question(7, 4, '-').answer);
        assertEquals(28, new GameLogic.Question(7, 4, '*').answer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnknownLevel() {
        GameLogic.newQuestion(7, new Random(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnknownOperator() {
        new GameLogic.Question(1, 2, '/');
    }
}
