package com.xsw.track.visitor

import com.xsw.track.config.TrackConfig
import com.xsw.track.global.TrackGlobal
import com.xsw.track.util.Log
import com.xsw.track.util.Utils
import org.objectweb.asm.*

class ClassVisitorWrapper extends ClassVisitor {
    private static final TAG_PREFIX = "************"
    private static final TAG_SUFFIX = "************\n"
    private static final TAG = ClassVisitorWrapper.class.simpleName

    private boolean onlyVisit = false
    private String superName
    private String[] interfaces

    ClassVisitorWrapper(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor)
    }

    @Override
    void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute)
//            Log.i("$TAG_PREFIX $TAG#visitAttribute $TAG_SUFFIX")
//            Log.i("$attribute")
//            Log.i("t = ${attribute.type}")
//            Log.i("mc = ${attribute.metaClass}")
//            Log.i("mpv = ${attribute.metaPropertyValues}")
//            Log.i("p = ${attribute.properties}")
//            Log.i("")
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
//            Log.i("$TAG_PREFIX $TAG#visitAnnotation: $desc, $visible $TAG_SUFFIX")
        return super.visitAnnotation(desc, visible)
    }

    @Override
    void visitInnerClass(String name, String outerName,
                         String innerName, int access) {
//            Log.i("$TAG_PREFIX $TAG#visitInnerClass: n = $name, on = $outerName, in = $innerName $TAG_SUFFIX")
        super.visitInnerClass(name, outerName, innerName, access)
    }

    @Override
    void visitOuterClass(String owner, String name, String desc) {
//            Log.i("$TAG_PREFIX visitOuterClass: $owner, $name $TAG_SUFFIX")
        super.visitOuterClass(owner, name, desc)
    }

    @Override
    void visitSource(String source, String debug) {
//            Log.i("$TAG_PREFIX $TAG#visitSource: $TAG_SUFFIX")
        super.visitSource(source, debug)
    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
//            Log.i("$TAG_PREFIX $TAG#visitField:$name, $signature $TAG_SUFFIX")
        return super.visitField(access, name, desc, signature, value)
    }

    @Override
    void visit(int version, int access, String name,
               String signature, String superName, String[] interfaces) {
//            Log.i("$TAG_PREFIX $TAG#visit:n = $name, sn = $superName $TAG_SUFFIX")
        this.superName = superName
        this.interfaces = interfaces
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     *
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     * {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param start 方法参数起始索引（ 0：this，1+：普通参数 ）
     *
     * @param count 方法参数个数
     *
     * @param paramOpcodes 参数类型对应的ASM指令
     *
     */
    private static void visitMethodWithLoadedParams(MethodVisitor methodVisitor,
                                            int opcode, String owner,
                                            String methodName,
                                            String methodDesc,
                                            int start,
                                            int count,
                                            List<Integer> paramOpcodes) {
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes[i - start], i)
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false)
    }

    @Override
    MethodVisitor visitMethod(int access, String name,
                              String desc, String signature, String[] exceptions) {
        // Log.i("$TAG_PREFIX $TAG#visitMethod: a = $access, n = $name, s = $signature, d = $desc, e = $exceptions $TAG_SUFFIX")
        MethodVisitor methodVisitor = null
        if (null != interfaces && interfaces.length > 0) {
            final def methodDesc = TrackGlobal.getMethodDesc(name, desc)
            if (!Utils.isNull(methodDesc) && !onlyVisit) {
                final def mv = cv.visitMethod(access, name, desc, signature, exceptions)
                if (null != mv) {
                    if (interfaces.contains(methodDesc.parent)) {
                        methodVisitor = new MethodVisitorWrapper(mv) {
                            @Override
                            void visitCode() {
                                visitMethodWithLoadedParams(mv,
                                        Opcodes.INVOKESTATIC,
                                        TrackConfig.TRACK_MANAGER_NAME,
                                        methodDesc.agentName,
                                        methodDesc.trackDesc,
                                        methodDesc.paramsStart,
                                        methodDesc.paramsCount,
                                        methodDesc.opcodes)
                                if (methodDesc.key == TrackGlobal.KEY_ON_CLICK) {
                                    Label l1 = new Label()
                                    mv.visitJumpInsn(Opcodes.IFEQ, l1)
                                    mv.visitInsn(Opcodes.RETURN)
                                    mv.visitLabel(l1)
                                }
                                super.visitCode()
                            }
                        }
                    }
                }
            }
        }
        if (null != methodVisitor) {
            return methodVisitor
        }
        return super.visitMethod(access, name, desc, signature, exceptions)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
//            Log.i("$TAG_PREFIX $TAG#visitEnd $TAG_SUFFIX")
    }

}