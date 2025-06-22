package de.uni_passau.fim.se2.sa.sign;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import de.uni_passau.fim.se2.sa.sign.interpretation.SignInterpreter;
import de.uni_passau.fim.se2.sa.sign.interpretation.SignValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public class SignAnalysisImpl implements SignAnalysis {

  @Override
  public SortedSetMultimap<Integer, AnalysisResult> analyse(
      final String pClassName, final String pMethodName) throws AnalyzerException, IOException {
      // help of llm
    ClassReader classReader = new ClassReader(pClassName);
    ClassNode classNode = new ClassNode();
    classReader.accept(classNode, 0);

    int colonIndex = pMethodName.indexOf(':');
    String methodName = pMethodName.substring(0, colonIndex);
    String methodDesc = pMethodName.substring(colonIndex + 1);

    MethodNode methodToAnalyze = null;
    for (MethodNode m : classNode.methods) {
      if (m.name.equals(methodName) && m.desc.equals(methodDesc)) {
        methodToAnalyze = m;
        break;
      }
    }

    Map<String, MethodNode> methodMap = new HashMap<>();
    for (MethodNode m : classNode.methods) {
      methodMap.put(m.name + m.desc, m);
    }

    SignInterpreter interpreter = new SignInterpreter(pClassName, methodMap);

    Analyzer<SignValue> analyzer = new Analyzer<>(interpreter);
    Frame<SignValue>[] frames = analyzer.analyze(pClassName, methodToAnalyze);

    List<Pair<AbstractInsnNode, Frame<SignValue>>> instructionPairs = new ArrayList<>();
    for (int i = 0; i < methodToAnalyze.instructions.size(); i++) {
      AbstractInsnNode insn = methodToAnalyze.instructions.get(i);
      Frame<SignValue> frame = frames[i];

      if (frame != null) {
        instructionPairs.add(new Pair<>(insn, frame));
      }
    }

    return extractAnalysisResults(instructionPairs);
  }




  /**
   * Extracts the analysis results from the given pairs of instructions and frames.
   *
   * <p>The result is a {@link SortedSetMultimap} that maps line numbers to the analysis results.
   * For each line number, there can be multiple analysis results.  The method expects a list of
   * pairs of instructions and frames.  The instructions are expected to be in the same order as
   * they are in the method.  The frames are expected to be the frames that are computed for the
   * instructions.  The method will extract the analysis results from the frames and map them to
   * the line numbers of the instructions.
   *
   * @param pPairs The pairs of instructions and frames.
   * @return The analysis results.
   */
  private SortedSetMultimap<Integer, AnalysisResult> extractAnalysisResults(
      final List<Pair<AbstractInsnNode, Frame<SignValue>>> pPairs) {
    final SortedSetMultimap<Integer, AnalysisResult> result = TreeMultimap.create();
    int lineNumber = -1;

    for (final Pair<AbstractInsnNode, Frame<SignValue>> pair : pPairs) {
      final AbstractInsnNode instruction = pair.key();
      final Frame<SignValue> frame = pair.value();
      if (instruction instanceof LineNumberNode lineNumberNode) {
        lineNumber = lineNumberNode.line;
      }

      if (isDivByZero(instruction, frame)) {
        result.put(lineNumber, AnalysisResult.DIVISION_BY_ZERO);
      } else if (isMaybeDivByZero(instruction, frame)) {
        result.put(lineNumber, AnalysisResult.MAYBE_DIVISION_BY_ZERO);
      }

      if (isNegativeArrayIndex(instruction, frame)) {
        result.put(lineNumber, AnalysisResult.NEGATIVE_ARRAY_INDEX);
      } else if (isMaybeNegativeArrayIndex(instruction, frame)) {
        result.put(lineNumber, AnalysisResult.MAYBE_NEGATIVE_ARRAY_INDEX);
      }
    }

    return result;
  }

  private boolean isDivByZero(final AbstractInsnNode pInstruction, final Frame<SignValue> pFrame) {
    if (pInstruction.getOpcode() == Opcodes.IDIV) {
      SignValue divisor = pFrame.getStack(pFrame.getStackSize() - 1);
      return SignValue.isZero(divisor);
    }
    return false;
  }

  private boolean isMaybeDivByZero(
      final AbstractInsnNode pInstruction, final Frame<SignValue> pFrame) {
    if (pInstruction.getOpcode() == Opcodes.IDIV) {
      SignValue divisor = pFrame.getStack(pFrame.getStackSize() - 1);
      return SignValue.isMaybeZero(divisor);
    }
    return false;
  }

  private boolean isNegativeArrayIndex(
      final AbstractInsnNode pInstruction, final Frame<SignValue> pFrame) {
    if (pInstruction.getOpcode() == Opcodes.IALOAD || pInstruction.getOpcode() == Opcodes.IASTORE) {
      SignValue index = pFrame.getStack(pFrame.getStackSize() - 1);
      return SignValue.isNegative(index);
    }
    return false;
  }

  private boolean isMaybeNegativeArrayIndex(
      final AbstractInsnNode pInstruction, final Frame<SignValue> pFrame) {
    if (pInstruction.getOpcode() == Opcodes.IALOAD || pInstruction.getOpcode() == Opcodes.IASTORE) {
      SignValue index = pFrame.getStack(pFrame.getStackSize() - 1);
      return SignValue.isMaybeNegative(index);
    }
    return false;
  }

  private record Pair<K, V>(K key, V value) {

    @Override
    public String toString() {
      return "Pair{key=" + key + ", value=" + value + '}';
    }
  }
}
