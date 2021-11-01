package kamilalisp.test;

import kamilalisp.api.Evaluation;
import kamilalisp.data.Atom;
import kamilalisp.data.StringConstant;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListBasic {
    @Test
    void testCons() {
        assertTrue(Evaluation.evalString("(cons 2 (cons 3 'nil))").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(3))
        ))));

        assertTrue(Evaluation.evalString("(size (~ '(1 2 3) 3))").get(0).equals(new Atom(BigDecimal.valueOf(2))));
        assertTrue(Evaluation.evalString("(= 2 (size (~ '(1 2 3) 3)))").get(0).equals(new Atom(BigDecimal.valueOf(1))));
        assertTrue(Evaluation.evalString("(= 2 (size (~ '(1 2 3) 3)))").get(0).coerceBool());
    }

    @Test
    void testCdr() {
        assertTrue(Evaluation.evalString("(cdr (cons 2 (cons 3 'nil)))").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(3))
        ))));

        assertTrue(Evaluation.evalString("(cdr 'nil)").get(0).equals(Atom.NULL));
    }

    @Test
    void testUnion() {
        assertTrue(Evaluation.evalString("(| '(1 2 3) '(2 3 4))").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(1)),
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(3)),
                new Atom(BigDecimal.valueOf(4))
        ))));

        assertTrue(Evaluation.evalString("(| \"hi\" \"hello\")").get(0).getStringConstant().get().get().equals("hielo"));
        assertTrue(Evaluation.evalString("(| 1 0)").get(0).getNumber().get().equals(BigDecimal.valueOf(1)));
        assertTrue(Evaluation.evalString("(| 0 1)").get(0).getNumber().get().equals(BigDecimal.valueOf(1)));
        assertTrue(Evaluation.evalString("(| 0 0)").get(0).getNumber().get().equals(BigDecimal.valueOf(0)));
        assertTrue(Evaluation.evalString("(| 1 1)").get(0).getNumber().get().equals(BigDecimal.valueOf(1)));
        assertTrue(Evaluation.evalString("(| 5 20)").get(0).getNumber().get().equals(BigDecimal.valueOf(21)));
    }

    @Test
    void testIntersection() {
        assertTrue(Evaluation.evalString("(& '(1 2 3) '(2 3 4))").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(3))
        ))));

        assertTrue(Evaluation.evalString("(& \"hi\" \"hello\")").get(0).getStringConstant().get().get().equals("h"));
        assertTrue(Evaluation.evalString("(& 1 0)").get(0).getNumber().get().equals(BigDecimal.valueOf(0)));
        assertTrue(Evaluation.evalString("(& 0 1)").get(0).getNumber().get().equals(BigDecimal.valueOf(0)));
        assertTrue(Evaluation.evalString("(& 0 0)").get(0).getNumber().get().equals(BigDecimal.valueOf(0)));
        assertTrue(Evaluation.evalString("(& 1 1)").get(0).getNumber().get().equals(BigDecimal.valueOf(1)));
        assertTrue(Evaluation.evalString("(& 5 20)").get(0).getNumber().get().equals(BigDecimal.valueOf(4)));
    }

    @Test
    void testIotaDepth1() {
        assertTrue(Evaluation.evalString("(iota 5)").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(0)),
                new Atom(BigDecimal.valueOf(1)),
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(3)),
                new Atom(BigDecimal.valueOf(4))
        ))));

        assertTrue(Evaluation.evalString(
                "(= (iota '(3 4 5)) '(" +
                        "(0 0 0) (0 0 1) (0 0 2) (0 0 3) (0 0 4) (0 1 0) (0 1 1) (0 1 2) (0 1 3) (0 1 4) " +
                        "(0 2 0) (0 2 1) (0 2 2) (0 2 3) (0 2 4) (0 3 0) (0 3 1) (0 3 2) (0 3 3) (0 3 4) " +
                        "(1 0 0) (1 0 1) (1 0 2) (1 0 3) (1 0 4) (1 1 0) (1 1 1) (1 1 2) (1 1 3) (1 1 4) " +
                        "(1 2 0) (1 2 1) (1 2 2) (1 2 3) (1 2 4) (1 3 0) (1 3 1) (1 3 2) (1 3 3) (1 3 4) " +
                        "(2 0 0) (2 0 1) (2 0 2) (2 0 3) (2 0 4) (2 1 0) (2 1 1) (2 1 2) (2 1 3) (2 1 4) " +
                        "(2 2 0) (2 2 1) (2 2 2) (2 2 3) (2 2 4) (2 3 0) (2 3 1) (2 3 2) (2 3 3) (2 3 4)))").get(0).coerceBool());
    }

    @Test
    void testFold() {
        assertTrue(Evaluation.evalString("(foldl + 0 '(1 2 3))").get(0).equals(new Atom(BigDecimal.valueOf(6))));
        assertTrue(Evaluation.evalString("(foldl + 0 '(1 2 3 4 5))").get(0).equals(new Atom(BigDecimal.valueOf(15))));
        assertTrue(Evaluation.evalString("(foldr + 0 '(1 2 3 4 5))").get(0).equals(new Atom(BigDecimal.valueOf(15))));
        assertTrue(Evaluation.evalString("(foldr - 0 '(1 2 3 4 5))").get(0).equals(new Atom(BigDecimal.valueOf(3))));
    }

    @Test
    void testAny() {
        assertTrue(Evaluation.evalString("(any (lambda (x) (= x 1)) '(1 2 3))").get(0).coerceBool());
        assertTrue(Evaluation.evalString("(any (lambda (x) (= x 2)) '(1 2 3))").get(0).coerceBool());
        assertTrue(Evaluation.evalString("(any (lambda (x) (= x 3)) '(1 2 3))").get(0).coerceBool());
        assertFalse(Evaluation.evalString("(any (lambda (x) (= x 4)) '(1 2 3))").get(0).coerceBool());
    }

    @Test
    void testZip() {
        assertTrue(Evaluation.evalString("(zip '(1 2 3) '(4 5 6))").get(0).equals(new Atom(List.of(
                new Atom(List.of(new Atom(BigDecimal.valueOf(1)), new Atom(BigDecimal.valueOf(4)))),
                new Atom(List.of(new Atom(BigDecimal.valueOf(2)), new Atom(BigDecimal.valueOf(5)))),
                new Atom(List.of(new Atom(BigDecimal.valueOf(3)), new Atom(BigDecimal.valueOf(6))))
        ))));
    }

    @Test
    void testZipWith() {
        assertTrue(Evaluation.evalString("(zip-with + '(1 2 3) '(4 5 6))").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(5)),
                new Atom(BigDecimal.valueOf(7)),
                new Atom(BigDecimal.valueOf(9))
        ))));
    }

    @Test
    void testReverse() {
        assertTrue(Evaluation.evalString("(reverse '(1 2 3))").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(3)),
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(1))
        ))));

        assertTrue(Evaluation.evalString("(reverse '(1 2 3 4 5))").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(5)),
                new Atom(BigDecimal.valueOf(4)),
                new Atom(BigDecimal.valueOf(3)),
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(1))
        ))));

        assertTrue(Evaluation.evalString("(reverse \"hello\")").get(0).getStringConstant().get().get().equals("olleh"));
    }

    @Test
    void testRotate() {
        assertTrue(Evaluation.evalString("(rotate '(1 2 3 4 5) 1)").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(3)),
                new Atom(BigDecimal.valueOf(4)),
                new Atom(BigDecimal.valueOf(5)),
                new Atom(BigDecimal.valueOf(1))
        ))));

        assertTrue(Evaluation.evalString("(rotate '(1 2 3 4 5) 3)").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(4)),
                new Atom(BigDecimal.valueOf(5)),
                new Atom(BigDecimal.valueOf(1)),
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(3))
        ))));

        assertTrue(Evaluation.evalString("(rotate '(1 2 3 4 5) 5)").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(1)),
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(3)),
                new Atom(BigDecimal.valueOf(4)),
                new Atom(BigDecimal.valueOf(5))
        ))));

        assertTrue(Evaluation.evalString("(rotate '(1 2 3 4 5) -1)").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(5)),
                new Atom(BigDecimal.valueOf(1)),
                new Atom(BigDecimal.valueOf(2)),
                new Atom(BigDecimal.valueOf(3)),
                new Atom(BigDecimal.valueOf(4))
        ))));

        assertTrue(Evaluation.evalString("(rotate '(1 2 3 4 5) -3)").get(0).equals(new Atom(List.of(
                new Atom(BigDecimal.valueOf(3)),
                new Atom(BigDecimal.valueOf(4)),
                new Atom(BigDecimal.valueOf(5)),
                new Atom(BigDecimal.valueOf(1)),
                new Atom(BigDecimal.valueOf(2))
        ))));

        assertTrue(Evaluation.evalString("(rotate \"hello, world\" 3)").get(0).equals(new Atom(new StringConstant("lo, worldhel"))));
        assertTrue(Evaluation.evalString("(rotate \"hello, world\" -3)").get(0).equals(new Atom(new StringConstant("rldhello, wo"))));
        assertTrue(Evaluation.evalString("(rotate \"hello, world\" 5)").get(0).equals(new Atom(new StringConstant(", worldhello"))));
        assertTrue(Evaluation.evalString("(rotate \"hello, world\" 10)").get(0).equals(new Atom(new StringConstant("ldhello, wor"))));
    }
}
