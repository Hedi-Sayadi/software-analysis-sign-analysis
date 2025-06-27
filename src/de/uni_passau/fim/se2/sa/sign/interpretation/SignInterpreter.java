package de.uni_passau.fim.se2.sa.sign.interpretation;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

public class SignInterpreter extends Interpreter<SignValue> implements Opcodes {

  private final String pClassName;
  private final Map<String, MethodNode> methods;

  public SignInterpreter(final String pClassName, final Map<String, MethodNode> methods) {
    this(ASM9, pClassName, methods);
  }

  /**
   * Constructs a new {@link Interpreter}.
   *
   * @param pAPI The ASM API version supported by this interpreter. Must be one of {@link #ASM4},
   *     {@link #ASM5}, {@link #ASM6}, {@link #ASM7}, {@link #ASM8}, or {@link #ASM9}
   * @param pClassName The name of the class that contains the method to be analyzed.
   * @param methods All methods of the class that contains the method to be analyzed.
   */
  protected SignInterpreter(final int pAPI, final String pClassName, final Map<String, MethodNode> methods) {
    super(pAPI);
    if (getClass() != SignInterpreter.class) {
      throw new IllegalStateException();
    }

    this.pClassName = pClassName;
    this.methods = methods;
  }

  /** {@inheritDoc} */
  @Override
  public SignValue newValue(final Type pType) {
    if (pType == null) return null;
    if (pType.getSort() == Type.INT) {
      return SignValue.UNINITIALIZED_VALUE;
    }
    return null; // we are noot interested in non integer values
  }

  /** {@inheritDoc} */
  @Override
  public SignValue newOperation(final AbstractInsnNode pInstruction) throws AnalyzerException {
    switch (pInstruction.getOpcode()) {

      case ICONST_M1: return SignValue.MINUS;
      case ICONST_0: return SignValue.ZERO;
      case ICONST_1: return SignValue.PLUS;
      case ICONST_2: return SignValue.PLUS;
      case ICONST_3: return SignValue.PLUS;
      case ICONST_4: return SignValue.PLUS;
      case ICONST_5: return SignValue.PLUS;
      // help of llm
      case BIPUSH:
      case SIPUSH:
        SignTransferRelation signTransferRelation = new SignTransferRelation();
        IntInsnNode intInsnNode = (IntInsnNode) pInstruction;
        return signTransferRelation.evaluate(intInsnNode.operand);
    }
    return  SignValue.UNINITIALIZED_VALUE;
  }

  /** {@inheritDoc} */
  @Override
  public SignValue copyOperation(final AbstractInsnNode pInstruction, final SignValue pValue) {
    return pValue;
  }

  /** {@inheritDoc} */
  @Override
  public SignValue unaryOperation(final AbstractInsnNode pInstruction, final SignValue pValue)
      throws AnalyzerException {
    SignTransferRelation signTransferRelation = new SignTransferRelation();
    if (pInstruction.getOpcode() == INEG) {
        return signTransferRelation.evaluate(TransferRelation.Operation.NEG,pValue);
    }
    else if (pInstruction.getOpcode() == IINC) {
      IincInsnNode iincNode = (IincInsnNode) pInstruction;
      int increment = iincNode.incr;

      if (increment > 0) {
        return signTransferRelation.evaluateADD(pValue, SignValue.PLUS);
      } else if (increment < 0) {
        return signTransferRelation.evaluateADD(pValue, SignValue.MINUS);
      } else {
        return pValue;
      }
    }
    return pValue;
  }

  /** {@inheritDoc} */
  @Override
  public SignValue binaryOperation(
      final AbstractInsnNode pInstruction, final SignValue pValue1, final SignValue pValue2) {
    SignTransferRelation signTransferRelation = new SignTransferRelation();
    switch (pInstruction.getOpcode()) {
      case IADD: return signTransferRelation.evaluateADD(pValue1, pValue2);
      case ISUB: return signTransferRelation.evaluateSUB(pValue1, pValue2);
      case IMUL: return signTransferRelation.evaluateMUL(pValue1, pValue2);
      case IDIV: return signTransferRelation.evaluateDIV(pValue1, pValue2);
      default: return SignValue.TOP;
    }
  }

  /** {@inheritDoc} */
  @Override
  public SignValue ternaryOperation(
      final AbstractInsnNode pInstruction,
      final SignValue pValue1,
      final SignValue pValue2,
      final SignValue pValue3) {
    return null; // Nothing to do.
  }

  /** {@inheritDoc} */
  @Override
  public SignValue naryOperation(
      final AbstractInsnNode pInstruction, final List<? extends SignValue> pValues) {
    // help of llm to know how to detect if a bytecode instruction is a method call
    if (pInstruction.getOpcode() == INVOKEVIRTUAL ||
            pInstruction.getOpcode() == INVOKESPECIAL ||
            pInstruction.getOpcode() == INVOKESTATIC) {

      MethodInsnNode methodNode = (MethodInsnNode) pInstruction;

      // I put this because without it, when debugging i notice that the condition methodNode.owner.equals(internalClassName) will be always null. //help of llm
      String internalClassName = this.pClassName.replace('.', '/');

      if (methodNode.owner.equals(internalClassName) &&
              methodNode.desc.endsWith(")I")) {

        String methodKey = methodNode.name + methodNode.desc;
        MethodNode calledMethod = this.methods.get(methodKey);

        if (calledMethod != null) {
          try {
            SignInterpreter interpreter = new SignInterpreter(this.pClassName, this.methods);
            Analyzer<SignValue> analyzer = new Analyzer<>(interpreter);
            Frame<SignValue>[] frames = analyzer.analyze(this.pClassName, calledMethod);

            SignValue returnValue = SignValue.BOTTOM;

            AbstractInsnNode[] instructions = calledMethod.instructions.toArray();
            for (int i = 0; i < instructions.length; i++) {
              AbstractInsnNode insn = instructions[i];
              if (insn.getOpcode() == IRETURN && frames[i] != null && frames[i].getStackSize() > 0) {
                SignValue currentReturn = frames[i].getStack(frames[i].getStackSize() - 1);
                returnValue = returnValue.join(currentReturn);
              }
            }
            return returnValue;

          } catch (Exception e) {
            return SignValue.TOP;
          }
        }
      }
    }
    return SignValue.TOP;
  }

  /** {@inheritDoc} */
  @Override
  public void returnOperation(
      final AbstractInsnNode pInstruction, final SignValue pValue, final SignValue pExpected) {
    // Nothing to do.
  }

  /** {@inheritDoc} */
  @Override
  public SignValue merge(final SignValue pValue1, final SignValue pValue2) {
    if(pValue1!=null) {
      return pValue1.join(pValue2);
    }
    return SignValue.BOTTOM;
    }
  }

