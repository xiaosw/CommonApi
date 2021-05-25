package com.xsw.track.visitor

import com.xsw.track.util.Log
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes;

class MethodVisitorWrapper extends MethodVisitor {
    private static final TAG_PREFIX = ""
    private static final TAG_SUFFIX = ":"
    private static final TAG = MethodVisitorWrapper.class.getSimpleName()

    MethodVisitorWrapper(MethodVisitor methodVisitor) {
        super(Opcodes.ASM9, methodVisitor)
    }

    @Override
    void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        Log.i("$TAG_PREFIX ${TAG}#visitMethodInsn $TAG_SUFFIX",
                Log.getOpName(opcode), owner, name, desc, itf)
        super.visitMethodInsn(opcode, owner, name, desc,itf)
    }

    @Override
    void visitAttribute(Attribute attribute) {
        Log.i("$TAG_PREFIX ${TAG}#visitAttribute$TAG_SUFFIX", attribute)
        super.visitAttribute(attribute)
    }

    @Override
    void visitEnd() {
        Log.i("$TAG_PREFIX ${TAG}#visitEnd$TAG_SUFFIX")
        super.visitEnd()
    }

    @Override
    void visitFieldInsn(int opcode, String owner, String name, String desc) {
        Log.i("$TAG_PREFIX ${TAG}#visitFieldInsn$TAG_SUFFIX",
                Log.getOpName(opcode), owner, name, desc)
        super.visitFieldInsn(opcode, owner, name, desc)
    }

    @Override
    void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        Log.i("$TAG_PREFIX ${TAG}#visitFrame$TAG_SUFFIX", type, local, nLocal, nStack, stack)
        super.visitFrame(type, nLocal, local, nStack, stack)
    }

    @Override
    void visitLabel(Label label) {
        Log.i("$TAG_PREFIX ${TAG}#visitLabel$TAG_SUFFIX", label)
        super.visitLabel(label)
    }

    @Override
    void visitLineNumber(int line, Label label) {
        Log.i("$TAG_PREFIX ${TAG}#visitLineNumber$TAG_SUFFIX", line, label)
        super.visitLineNumber(line, label)
    }

    @Override
    void visitIincInsn(int var, int increment) {
        Log.i("$TAG_PREFIX ${TAG}#visitIincInsn$TAG_SUFFIX", var, increment)
        super.visitIincInsn(var, increment)
    }

    @Override
    void visitIntInsn(int i, int i1) {
        Log.i("$TAG_PREFIX ${TAG}#visitIntInsn$TAG_SUFFIX", i, i1)
        super.visitIntInsn(i, i1)
    }

    @Override
    void visitMaxs(int maxStack, int maxLocals) {
        Log.i("$TAG_PREFIX ${TAG}#visitMaxs$TAG_SUFFIX", maxStack, maxLocals)
        super.visitMaxs(maxStack, maxLocals)
    }

    @Override
    void visitVarInsn(int opcode, int var) {
        Log.i("$TAG_PREFIX ${TAG}#visitVarInsn$TAG_SUFFIX", Log.getOpName(opcode), var)
        super.visitVarInsn(opcode, var)
    }

    @Override
    void visitJumpInsn(int opcode, Label label) {
        Log.i("$TAG_PREFIX ${TAG}#visitJumpInsn$TAG_SUFFIX", Log.getOpName(opcode), label)
        super.visitJumpInsn(opcode, label)
    }

    @Override
    void visitLdcInsn(Object o) {
        Log.i("$TAG_PREFIX ${TAG}#visitLdcInsn$TAG_SUFFIX", o);
        super.visitLdcInsn(o);
    }

    @Override
    void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
        Log.i("$TAG_PREFIX ${TAG}#visitLookupSwitchInsn$TAG_SUFFIX", label, ints, labels)
        super.visitLookupSwitchInsn(label, ints, labels)
    }

    @Override
    void visitMultiANewArrayInsn(String s, int i) {
        Log.i("$TAG_PREFIX ${TAG}#visitMultiANewArrayInsn$TAG_SUFFIX", s, i)
        super.visitMultiANewArrayInsn(s, i)
    }

    @Override
    void visitTableSwitchInsn(int i, int i1, Label label, Label[] labels) {
        Log.i("$TAG_PREFIX ${TAG}#visitTableSwitchInsn$TAG_SUFFIX", i, i1,
                label, labels)
        super.visitTableSwitchInsn(i, i1, label, labels)
    }

    @Override
    void visitTryCatchBlock(Label label, Label label1, Label label2, String s) {
        Log.i("$TAG_PREFIX ${TAG}#visitTryCatchBlock$TAG_SUFFIX", label,
                label1, label2, s)
        super.visitTryCatchBlock(label, label1, label2, s)
    }

    @Override
    void visitTypeInsn(int opcode, String s) {
        Log.i("$TAG_PREFIX ${TAG}#visitTypeInsn$TAG_SUFFIX", Log.getOpName(opcode), s)
        super.visitTypeInsn(opcode, s)
    }

    @Override
    void visitCode() {
        Log.i("$TAG_PREFIX ${TAG}#visitCode$TAG_SUFFIX")
        super.visitCode()
    }

    @Override
    void visitLocalVariable(String s, String s1, String s2, Label label,
                            Label label1, int i) {
        Log.i("$TAG_PREFIX ${TAG}#visitLocalVariable$TAG_SUFFIX", s, s1,
                s2, label, label1, i)
        super.visitLocalVariable(s, s1, s2, label, label1, i)
    }

    @Override
    void visitInsn(int opcode) {
        Log.i("$TAG_PREFIX ${TAG}#visitInsn$TAG_SUFFIX", Log.getOpName(opcode))
        super.visitInsn(opcode)
    }

}