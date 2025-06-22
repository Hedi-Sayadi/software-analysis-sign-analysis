package de.uni_passau.fim.se2.sa.sign;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignAnalysisMainTest {

    @Test
    public void testCall() throws Exception {
        SignAnalysisMain main = new SignAnalysisMain();
        main.setClassName("de.uni_passau.fim.se2.sa.examples.PublicFunctional");
        main.setMethodName("allCases:()I");

        Integer exitCode = main.call();
        assertEquals(0, exitCode);
    }

}