package de.uni_passau.fim.se2.sa.sign.interpretation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignValueTest {

    @Test
    void testJoinSimpleCases() {
        assertEquals(SignValue.MINUS, SignValue.MINUS.join(SignValue.BOTTOM));
        assertEquals(SignValue.ZERO_MINUS, SignValue.ZERO.join(SignValue.MINUS));
        assertEquals(SignValue.TOP, SignValue.PLUS.join(SignValue.ZERO_MINUS));
        assertEquals(SignValue.PLUS_MINUS, SignValue.PLUS.join(SignValue.MINUS));
        assertEquals(SignValue.ZERO_PLUS, SignValue.PLUS.join(SignValue.ZERO));
        assertEquals(SignValue.TOP, SignValue.ZERO_PLUS.join(SignValue.ZERO_MINUS));
        assertEquals(SignValue.TOP, SignValue.TOP.join(SignValue.MINUS));
        assertEquals(SignValue.TOP, SignValue.TOP.join(SignValue.TOP));
    }

    @Test
    void testIsLessOrEqual() {
        assertTrue(SignValue.BOTTOM.isLessOrEqual(SignValue.ZERO));
        assertTrue(SignValue.MINUS.isLessOrEqual(SignValue.ZERO_MINUS));
        assertTrue(SignValue.ZERO.isLessOrEqual(SignValue.ZERO_PLUS));
        assertTrue(SignValue.ZERO.isLessOrEqual(SignValue.TOP));
        assertFalse(SignValue.PLUS.isLessOrEqual(SignValue.ZERO));
        assertTrue(SignValue.ZERO.isLessOrEqual(SignValue.ZERO));
        assertFalse(SignValue.TOP.isLessOrEqual(SignValue.PLUS_MINUS));
    }

    @Test
    void testSimpleCases() {
        assertTrue(SignValue.isZero(SignValue.ZERO));
        assertFalse(SignValue.isZero(SignValue.MINUS));

        assertTrue(SignValue.isMaybeZero(SignValue.ZERO_MINUS));
        assertTrue(SignValue.isMaybeZero(SignValue.ZERO_PLUS));
        assertTrue(SignValue.isMaybeZero(SignValue.TOP));
        assertFalse(SignValue.isMaybeZero(SignValue.MINUS));

        assertTrue(SignValue.isNegative(SignValue.MINUS));
        assertFalse(SignValue.isNegative(SignValue.ZERO));

        assertTrue(SignValue.isMaybeNegative(SignValue.ZERO_MINUS));
        assertTrue(SignValue.isMaybeNegative(SignValue.PLUS_MINUS));
        assertFalse(SignValue.isMaybeNegative(SignValue.ZERO_PLUS));
    }

    @Test
    void testGetSize() {
        for (SignValue v : SignValue.values()) {
            assertEquals(1, v.getSize());
        }
    }

    @Test
    void testThrowsExceptionWhenUNINITIALIZEDVALUE() {
        for (SignValue signValue : SignValue.values()) {
            if (signValue != SignValue.UNINITIALIZED_VALUE) {
                assertThrows(IllegalStateException.class, () -> SignValue.UNINITIALIZED_VALUE.isLessOrEqual(signValue));
                assertThrows(IllegalStateException.class, () -> signValue.isLessOrEqual(SignValue.UNINITIALIZED_VALUE));
            }
        }
        assertThrows(IllegalStateException.class, () ->
                SignValue.UNINITIALIZED_VALUE.isLessOrEqual(SignValue.UNINITIALIZED_VALUE));
    }

    @Test
    void testJoin() {
        for (SignValue a : SignValue.values()) {
            for (SignValue b : SignValue.values()) {
                if (a != SignValue.UNINITIALIZED_VALUE && b != SignValue.UNINITIALIZED_VALUE) {
                    assertEquals(a.join(b), b.join(a));
                }
            }
        }
    }


}