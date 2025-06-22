package de.uni_passau.fim.se2.sa.sign.lattice;

import de.uni_passau.fim.se2.sa.sign.interpretation.SignValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignLatticeTest {
    private SignLattice lattice;

    @BeforeEach
    public void setUp() {
        lattice = new SignLattice();
    }

    @Test
    public void testTop() {
        assertEquals(SignValue.TOP, lattice.top());
    }

    @Test
    public void testBottom() {
        assertEquals(SignValue.BOTTOM, lattice.bottom());
    }

    @Test
    public void testJoin() {
        SignValue plus = SignValue.PLUS;
        SignValue minus = SignValue.MINUS;
        SignValue zero = SignValue.ZERO;

        SignValue joinResult = lattice.join(plus, minus);
        assertTrue(joinResult.isLessOrEqual(SignValue.TOP));
        assertEquals(zero, lattice.join(zero, zero));
    }

    @Test
    public void testIsLessOrEqual() {
        SignValue top = SignValue.TOP;
        SignValue bottom = SignValue.BOTTOM;
        SignValue zero = SignValue.ZERO;
        SignValue plus = SignValue.PLUS;

        assertTrue(lattice.isLessOrEqual(bottom, zero));
        assertTrue(lattice.isLessOrEqual(bottom, top));
        assertTrue(lattice.isLessOrEqual(plus, top));
        assertTrue(lattice.isLessOrEqual(zero, top));
        assertTrue(lattice.isLessOrEqual(zero, zero));
        assertFalse(lattice.isLessOrEqual(top, bottom));
    }

}