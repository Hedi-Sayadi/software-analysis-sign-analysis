package de.uni_passau.fim.se2.sa.sign.interpretation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignTransferRelationTest {
    private SignTransferRelation transferRelation;

    @BeforeEach
    public void setUp() {
        transferRelation = new SignTransferRelation();
    }

    @Test
    public void testEvaluateInt() {
        assertEquals(SignValue.MINUS, transferRelation.evaluate(-5));
        assertEquals(SignValue.ZERO, transferRelation.evaluate(0));
        assertEquals(SignValue.PLUS, transferRelation.evaluate(7));
    }

    @Test
    public void testEvaluateNegOperation() {
        for (SignValue val : SignValue.values()) {
            if (val == SignValue.BOTTOM) {
                assertEquals(SignValue.BOTTOM, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            } else if (val == SignValue.PLUS) {
                assertEquals(SignValue.MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            } else if (val == SignValue.MINUS) {
                assertEquals(SignValue.PLUS, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            } else if (val == SignValue.ZERO) {
                assertEquals(SignValue.ZERO, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            } else if (val == SignValue.PLUS_MINUS) {
                assertEquals(SignValue.PLUS_MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            } else if (val == SignValue.ZERO_PLUS) {
                assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            } else if (val == SignValue.ZERO_MINUS) {
                assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            } else if (val == SignValue.UNINITIALIZED_VALUE) {
                assertEquals(SignValue.UNINITIALIZED_VALUE, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            } else {
                assertEquals(SignValue.TOP, transferRelation.evaluate(TransferRelation.Operation.NEG, val));
            }
        }
    }

    @Test
    public void testEvaluateAdd() {
        assertEquals(SignValue.PLUS, transferRelation.evaluateADD(SignValue.PLUS, SignValue.PLUS));
        assertEquals(SignValue.MINUS, transferRelation.evaluateADD(SignValue.MINUS, SignValue.MINUS));
        assertEquals(SignValue.TOP, transferRelation.evaluateADD(SignValue.PLUS, SignValue.MINUS));
        assertEquals(SignValue.BOTTOM, transferRelation.evaluateADD(SignValue.BOTTOM, SignValue.PLUS));
        assertEquals(SignValue.TOP, transferRelation.evaluateADD(SignValue.UNINITIALIZED_VALUE, SignValue.ZERO));
        assertEquals(SignValue.PLUS, transferRelation.evaluateADD(SignValue.PLUS, SignValue.ZERO_PLUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateADD(SignValue.ZERO_MINUS, SignValue.ZERO_MINUS));
        assertEquals(SignValue.BOTTOM, transferRelation.evaluateADD(SignValue.PLUS, SignValue.BOTTOM));
        assertEquals(SignValue.TOP, transferRelation.evaluateADD(SignValue.ZERO, SignValue.UNINITIALIZED_VALUE));
        assertEquals(SignValue.TOP, transferRelation.evaluateADD(SignValue.TOP, SignValue.PLUS));
        assertEquals(SignValue.TOP, transferRelation.evaluateADD(SignValue.MINUS, SignValue.TOP));
        assertEquals(SignValue.PLUS, transferRelation.evaluateADD(SignValue.ZERO, SignValue.PLUS));
        assertEquals(SignValue.MINUS, transferRelation.evaluateADD(SignValue.ZERO, SignValue.MINUS));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluateADD(SignValue.ZERO, SignValue.ZERO_PLUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateADD(SignValue.ZERO, SignValue.ZERO_MINUS));
        assertEquals(SignValue.PLUS_MINUS, transferRelation.evaluateADD(SignValue.ZERO, SignValue.PLUS_MINUS));
        assertEquals(SignValue.PLUS, transferRelation.evaluateADD(SignValue.PLUS, SignValue.ZERO));
        assertEquals(SignValue.MINUS, transferRelation.evaluateADD(SignValue.MINUS, SignValue.ZERO));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluateADD(SignValue.ZERO_PLUS, SignValue.ZERO));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateADD(SignValue.ZERO_MINUS, SignValue.ZERO));
        assertEquals(SignValue.PLUS_MINUS, transferRelation.evaluateADD(SignValue.PLUS_MINUS, SignValue.ZERO));



    }

    @Test
    public void testEvaluateSub() {
        assertEquals(transferRelation.evaluateADD(SignValue.PLUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.PLUS)),
                transferRelation.evaluateSUB(SignValue.PLUS, SignValue.PLUS));
    }

    @Test
    public void testEvaluateMul() {
        assertEquals(SignValue.PLUS, transferRelation.evaluateMUL(SignValue.PLUS, SignValue.PLUS));
        assertEquals(SignValue.MINUS, transferRelation.evaluateMUL(SignValue.PLUS, SignValue.MINUS));
        assertEquals(SignValue.ZERO, transferRelation.evaluateMUL(SignValue.ZERO, SignValue.PLUS));
        assertEquals(SignValue.BOTTOM, transferRelation.evaluateMUL(SignValue.BOTTOM, SignValue.PLUS));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluateMUL(SignValue.PLUS, SignValue.ZERO_PLUS));
        assertEquals(SignValue.TOP, transferRelation.evaluateMUL(SignValue.PLUS_MINUS, SignValue.ZERO_PLUS));
        assertEquals(SignValue.MINUS, transferRelation.evaluateMUL(SignValue.MINUS, SignValue.PLUS));
        assertEquals(SignValue.PLUS, transferRelation.evaluateMUL(SignValue.MINUS, SignValue.MINUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateMUL(SignValue.ZERO_MINUS, SignValue.PLUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateMUL(SignValue.MINUS, SignValue.ZERO_PLUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateMUL(SignValue.ZERO_PLUS, SignValue.MINUS));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluateMUL(SignValue.MINUS, SignValue.ZERO_MINUS));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluateMUL(SignValue.ZERO_MINUS, SignValue.MINUS));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluateMUL(SignValue.ZERO_PLUS, SignValue.ZERO_PLUS));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluateMUL(SignValue.ZERO_MINUS, SignValue.ZERO_MINUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateMUL(SignValue.ZERO_PLUS, SignValue.ZERO_MINUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateMUL(SignValue.ZERO_MINUS, SignValue.ZERO_PLUS));
        assertEquals(SignValue.PLUS_MINUS, transferRelation.evaluateMUL(SignValue.PLUS_MINUS, SignValue.PLUS));
        assertEquals(SignValue.PLUS_MINUS, transferRelation.evaluateMUL(SignValue.MINUS, SignValue.PLUS_MINUS));
        assertEquals(SignValue.PLUS_MINUS, transferRelation.evaluateMUL(SignValue.PLUS_MINUS, SignValue.MINUS));
    }

    @Test
    public void testEvaluateDiv() {
        assertEquals(SignValue.BOTTOM, transferRelation.evaluateDIV(SignValue.PLUS, SignValue.ZERO));
        assertEquals(SignValue.ZERO, transferRelation.evaluateDIV(SignValue.ZERO, SignValue.PLUS));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluateDIV(SignValue.PLUS, SignValue.PLUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluateDIV(SignValue.MINUS, SignValue.PLUS));
        assertEquals(SignValue.TOP, transferRelation.evaluateDIV(SignValue.PLUS_MINUS, SignValue.ZERO_PLUS));
    }

    //I added this to enhance branch coverage :
    @Test
    public void testEvaluateBinaryOperations() {
        assertEquals(
                transferRelation.evaluateADD(SignValue.PLUS, SignValue.MINUS),
                transferRelation.evaluate(TransferRelation.Operation.ADD, SignValue.PLUS, SignValue.MINUS));
        assertEquals(
                transferRelation.evaluateSUB(SignValue.PLUS, SignValue.MINUS),
                transferRelation.evaluate(TransferRelation.Operation.SUB, SignValue.PLUS, SignValue.MINUS));
        assertEquals(
                transferRelation.evaluateMUL(SignValue.PLUS, SignValue.MINUS),
                transferRelation.evaluate(TransferRelation.Operation.MUL, SignValue.PLUS, SignValue.MINUS));
        assertEquals(
                transferRelation.evaluateDIV(SignValue.PLUS, SignValue.MINUS),
                transferRelation.evaluate(TransferRelation.Operation.DIV, SignValue.PLUS, SignValue.MINUS));
    }

    @Test
    public void testEvaluateBinaryNullOperands() {
        assertThrows(NullPointerException.class, () -> {
            transferRelation.evaluate(TransferRelation.Operation.ADD, null, SignValue.PLUS);
        });

        assertThrows(NullPointerException.class, () -> {
            transferRelation.evaluate(TransferRelation.Operation.ADD, SignValue.PLUS, null);
        });
    }

    @Test
    public void testEvaluateBinaryInvalidOperation() {
        assertThrows(IllegalStateException.class, () -> {
            transferRelation.evaluate(null, SignValue.PLUS, SignValue.MINUS);
        });
}

    @Test
    void testEvaluateMULReturnsTopDefault() {
        SignValue signValue = transferRelation.evaluateMUL(SignValue.UNINITIALIZED_VALUE, SignValue.PLUS);
        assertEquals(SignValue.TOP, signValue);
    }

}