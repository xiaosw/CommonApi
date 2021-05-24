package com.xsw.track.util

import com.xsw.track.visitor.ClassVisitorWrapper
import org.objectweb.asm.*

class ModifyClassUtil {

    private static final TAG_PREFIX = "************"
    private static final TAG_SUFFIX = "************\n"

    static byte[] modifyClass(String className, byte[] srcByteCode) {
        try {
            Log.i("start modify: $className")
            byte[] newClassByteCode = internalModifyClass(srcByteCode)
            Log.i("revisit method...")
            onlyVisitClassMethod(newClassByteCode)
            Log.i("modify complete: $className")
            return newClassByteCode
        } catch(Exception e) {
            Log.e(e)
        }
        return srcByteCode
    }

    private static byte[] internalModifyClass(byte[] srcClass) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor methodFilterCV = new ClassVisitorWrapper(Opcodes.ASM5, classWriter)
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG);
        return classWriter.toByteArray();
    }

    private static void onlyVisitClassMethod(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitorWrapper methodFilterCV = new ClassVisitorWrapper(Opcodes.ASM5,
                classWriter)
        methodFilterCV.onlyVisit = true
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG);
    }

}