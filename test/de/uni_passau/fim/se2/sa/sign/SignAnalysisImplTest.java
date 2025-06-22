package de.uni_passau.fim.se2.sa.sign;

import com.google.common.collect.SortedSetMultimap;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SignAnalysisImplTest {

    @Test
    public void testAllCases() throws AnalyzerException, IOException {
        SignAnalysisImpl signAnalysis = new SignAnalysisImpl();

        String className = "de.uni_passau.fim.se2.sa.examples.PublicFunctional";
        String methodName = "allCases:()I";

        SortedSetMultimap<Integer, AnalysisResult> results = signAnalysis.analyse(className, methodName);

        assertTrue(results.containsEntry(24, AnalysisResult.DIVISION_BY_ZERO));
        assertTrue(results.containsEntry(25, AnalysisResult.MAYBE_DIVISION_BY_ZERO));
        assertTrue(results.containsEntry(26, AnalysisResult.MAYBE_DIVISION_BY_ZERO));
        assertTrue(results.containsEntry(27, AnalysisResult.MAYBE_DIVISION_BY_ZERO));
        assertTrue(results.containsEntry(30, AnalysisResult.NEGATIVE_ARRAY_INDEX));
        assertTrue(results.containsEntry(31, AnalysisResult.MAYBE_NEGATIVE_ARRAY_INDEX));
        assertTrue(results.containsEntry(34, AnalysisResult.MAYBE_DIVISION_BY_ZERO));
    }

}