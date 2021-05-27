package com.xsw.track.util

import com.xsw.track.visitor.ClassVisitorWrapper
import org.objectweb.asm.*

class ModifyClassUtil {

    private static final TAG_PREFIX = "************"
    private static final TAG_SUFFIX = "************\n"

    static byte[] modifyClass(String className, byte[] srcByteCode) {
        try {
            Log.i("start modify: $className")
            byte[] newClassByteCode = internalModifyClass(srcByteCode, className)
            Log.i("revisit method...")
            onlyVisitClassMethod(newClassByteCode, className)
            Log.i("modify complete: $className")
            return newClassByteCode
        } catch(Exception e) {
            Log.e(e)
        }
        return srcByteCode
    }

    private static byte[] internalModifyClass(byte[] srcClassByteCode,  String className) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor cv = new ClassVisitorWrapper(classWriter, className)
        ClassReader cr = new ClassReader(srcClassByteCode)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    private static void onlyVisitClassMethod(byte[] srcClassCode, String className) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitorWrapper cvw = new ClassVisitorWrapper(
                classWriter, className)
        cvw.onlyVisit = true
        ClassReader cr = new ClassReader(srcClassCode)
        cr.accept(cvw, ClassReader.EXPAND_FRAMES)
    }

}