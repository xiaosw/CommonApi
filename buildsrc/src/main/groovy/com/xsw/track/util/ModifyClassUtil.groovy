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

    private static byte[] internalModifyClass(byte[] srcClassByteCode) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor cv = new ClassVisitorWrapper(classWriter)
        ClassReader cr = new ClassReader(srcClassByteCode)
        cr.accept(cv, ClassReader.SKIP_DEBUG)
        return classWriter.toByteArray()
    }

    private static void onlyVisitClassMethod(byte[] srcClassCode) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitorWrapper cvw = new ClassVisitorWrapper(
                classWriter)
        cvw.onlyVisit = true
        ClassReader cr = new ClassReader(srcClassCode)
        cr.accept(cvw, ClassReader.SKIP_DEBUG)
    }

}