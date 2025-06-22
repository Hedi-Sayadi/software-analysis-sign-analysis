package de.uni_passau.fim.se2.sa.sign.interpretation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignInterpreterTest {
    private SignInterpreter interpreter;

    @BeforeEach
    void setup() {
        interpreter = new SignInterpreter("TestClass", new HashMap<>());
    }

    @Test
    void testNewValueReturnsNull() {
        assertNull(interpreter.newValue(null));
    }

    @Test
    void testNewValueReturnsUninitializedValue() {
        Type intType = Type.INT_TYPE;
        assertEquals(SignValue.UNINITIALIZED_VALUE, interpreter.newValue(intType));
    }

    @Test
    void testNewOperationICONSTReturnsZero() throws Exception {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.ICONST_0);
        assertEquals(SignValue.ZERO, interpreter.newOperation(abstractInsnNode));
    }

    @Test
    void testNewOperationICONSTRPlus() throws Exception {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.ICONST_1);
        assertEquals(SignValue.PLUS, interpreter.newOperation(abstractInsnNode));
    }

    @Test
    void testNewOperationBIPUSHReturnsMinus() throws Exception {
        IntInsnNode abstractInsnNode = new IntInsnNode(Opcodes.BIPUSH, -9);
        assertEquals(SignValue.MINUS, interpreter.newOperation(abstractInsnNode));
    }

    @Test
    void testNewOperationReturnsUninitialized() throws Exception {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.IRETURN);
        assertEquals(SignValue.UNINITIALIZED_VALUE, interpreter.newOperation(abstractInsnNode));
    }

    @Test
    void testCopyOperation() {
        SignValue value = SignValue.PLUS;
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.NOP);
        assertEquals(value, interpreter.copyOperation(abstractInsnNode, value));
    }

    @Test
    void testUnaryOperationINEG() throws Exception {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.INEG);
        assertEquals(SignValue.MINUS, interpreter.unaryOperation(abstractInsnNode, SignValue.PLUS));
    }

    @Test
    void testUnaryOperationNOPReturnsSameValue() throws Exception {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.NOP);
        assertEquals(SignValue.PLUS, interpreter.unaryOperation(abstractInsnNode, SignValue.PLUS));
    }

    @Test
    void testBinaryOperationIADD() {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.IADD);
        SignValue signValue = interpreter.binaryOperation(abstractInsnNode, SignValue.ZERO, SignValue.PLUS);
        assertEquals(SignValue.PLUS, signValue);
    }

    @Test
    void testBinaryOperationReturnsTop() {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.IREM);
        assertEquals(SignValue.TOP, interpreter.binaryOperation(abstractInsnNode, SignValue.ZERO, SignValue.PLUS));
    }

    @Test
    void testTernaryOperationReturnsNull() {
        assertNull(interpreter.ternaryOperation(new InsnNode(Opcodes.NOP), SignValue.ZERO, SignValue.PLUS, SignValue.MINUS));
    }

    @Test
    void testNaryOperationReturnsTop() {
        assertEquals(SignValue.TOP, interpreter.naryOperation(new InsnNode(Opcodes.NOP), List.of(SignValue.PLUS, SignValue.ZERO)));
    }

    @Test
    void testMergeReturnsJoin() {
        SignValue signValue = interpreter.merge(SignValue.ZERO, SignValue.PLUS);
        assertEquals(SignValue.ZERO_PLUS, signValue);
    }

    @Test
    void testMergeReturnsBottom() {
        SignValue signValue = interpreter.merge(null, SignValue.PLUS);
        assertEquals(SignValue.BOTTOM, signValue);
    }

    @Test
    void testNewOperationICONSTM1ReturnsMinus() throws Exception {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.ICONST_M1);
        assertEquals(SignValue.MINUS, interpreter.newOperation(abstractInsnNode));
    }

    @Test
    void testNewOperationICONST2To5ReturnPlus() throws Exception {
        int[] opcodes = {Opcodes.ICONST_2, Opcodes.ICONST_3, Opcodes.ICONST_4, Opcodes.ICONST_5};
        for (int opcode : opcodes) {
            AbstractInsnNode abstractInsnNode = new InsnNode(opcode);
            assertEquals(SignValue.PLUS, interpreter.newOperation(abstractInsnNode));
        }
    }

    @Test
    void testBinaryOperationISUB() {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.ISUB);
        SignValue signValue = interpreter.binaryOperation(abstractInsnNode, SignValue.PLUS, SignValue.MINUS);
        assertNotNull(signValue);
    }

    @Test
    void testBinaryOperationIMUL() {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.IMUL);
        SignValue signValue = interpreter.binaryOperation(abstractInsnNode, SignValue.PLUS, SignValue.MINUS);
        assertNotNull(signValue);
    }

    @Test
    void testBinaryOperationIDIV() {
        AbstractInsnNode abstractInsnNode = new InsnNode(Opcodes.IDIV);
        SignValue signValue = interpreter.binaryOperation(abstractInsnNode, SignValue.PLUS, SignValue.MINUS);
        assertNotNull(signValue);
    }

    static class SignInterpreterSubclass extends SignInterpreter {
        public SignInterpreterSubclass() {
            super("Dummy", new HashMap<>());
        }
    }
    @Test
    void testConstructor_throwsIfSubclass() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> new SignInterpreterSubclass());
        assertEquals("java.lang.IllegalStateException", exception.getClass().getName());
    }
}