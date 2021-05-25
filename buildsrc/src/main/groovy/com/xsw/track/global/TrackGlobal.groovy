package com.xsw.track.global

import com.xsw.track.bean.MethodDesc
import org.objectweb.asm.Opcodes

class TrackGlobal {

    private static final def sGlobal = [:]
    static final def sAccCodeMap = new HashMap<String, Integer>()
    static final def sOpCodeMap = new HashMap<String, Integer>()
    private static final Map<String, MethodDesc> sVisitMethod = new HashMap<>()
    static final def KEY_ON_CLICK
    static {
        def onClickMethodDesc = new MethodDesc('onClick',
                '(Landroid/view/View;)V',
                'android/view/View$OnClickListener',
                'onClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD],
                "(Landroid/view/View;)Z")
        KEY_ON_CLICK = onClickMethodDesc.key
        setMethodDesc(onClickMethodDesc)

        sAccCodeMap.put(1, "ACC_PUBLIC")
        sAccCodeMap.put(2, "ACC_PRIVATE")
        sAccCodeMap.put(4, "ACC_PROTECTED")
        sAccCodeMap.put(8, "ACC_STATIC")
        sAccCodeMap.put(16, "ACC_FINAL")
        sAccCodeMap.put(32, "ACC_SUPER")
        sAccCodeMap.put(32, "ACC_SYNCHRONIZED")
        sAccCodeMap.put(64, "ACC_VOLATILE")
        sAccCodeMap.put(64, "ACC_BRIDGE")
        sAccCodeMap.put(128, "ACC_VARARGS")
        sAccCodeMap.put(128, "ACC_TRANSIENT")
        sAccCodeMap.put(256, "ACC_NATIVE")
        sAccCodeMap.put(512, "ACC_INTERFACE")
        sAccCodeMap.put(1024, "ACC_ABSTRACT")
        sAccCodeMap.put(2048, "ACC_STRICT")
        sAccCodeMap.put(4096, "ACC_SYNTHETIC")
        sAccCodeMap.put(8192, "ACC_ANNOTATION")
        sAccCodeMap.put(16384, "ACC_ENUM")
        sAccCodeMap.put(131072, "ACC_DEPRECATED")

        HashMap<String, Integer> map = new HashMap<>();
        map.put("NOP", 0);
        map.put("ACONST_NULL", 1);
        map.put("ICONST_M1", 2);
        map.put("ICONST_0", 3);
        map.put("ICONST_1", 4);
        map.put("ICONST_2", 5);
        map.put("ICONST_3", 6);
        map.put("ICONST_4", 7);
        map.put("ICONST_5", 8);
        map.put("LCONST_0", 9);
        map.put("LCONST_1", 10);
        map.put("FCONST_0", 11);
        map.put("FCONST_1", 12);
        map.put("FCONST_2", 13);
        map.put("DCONST_0", 14);
        map.put("DCONST_1", 15);
        map.put("BIPUSH", 16);
        map.put("SIPUSH", 17);
        map.put("LDC", 18);
        map.put("ILOAD", 21);
        map.put("LLOAD", 22);
        map.put("FLOAD", 23);
        map.put("DLOAD", 24);
        map.put("ALOAD", 25);
        map.put("IALOAD", 46);
        map.put("LALOAD", 47);
        map.put("FALOAD", 48);
        map.put("DALOAD", 49);
        map.put("AALOAD", 50);
        map.put("BALOAD", 51);
        map.put("CALOAD", 52);
        map.put("SALOAD", 53);
        map.put("ISTORE", 54);
        map.put("LSTORE", 55);
        map.put("FSTORE", 56);
        map.put("DSTORE", 57);
        map.put("ASTORE", 58);
        map.put("IASTORE", 79);
        map.put("LASTORE", 80);
        map.put("FASTORE", 81);
        map.put("DASTORE", 82);
        map.put("AASTORE", 83);
        map.put("BASTORE", 84);
        map.put("CASTORE", 85);
        map.put("SASTORE", 86);
        map.put("POP", 87);
        map.put("POP2", 88);
        map.put("DUP", 89);
        map.put("DUP_X1", 90);
        map.put("DUP_X2", 91);
        map.put("DUP2", 92);
        map.put("DUP2_X1", 93);
        map.put("DUP2_X2", 94);
        map.put("SWAP", 95);
        map.put("IADD", 96);
        map.put("LADD", 97);
        map.put("FADD", 98);
        map.put("DADD", 99);
        map.put("ISUB", 100);
        map.put("LSUB", 101);
        map.put("FSUB", 102);
        map.put("DSUB", 103);
        map.put("IMUL", 104);
        map.put("LMUL", 105);
        map.put("FMUL", 106);
        map.put("DMUL", 107);
        map.put("IDIV", 108);
        map.put("LDIV", 109);
        map.put("FDIV", 110);
        map.put("DDIV", 111);
        map.put("IREM", 112);
        map.put("LREM", 113);
        map.put("FREM", 114);
        map.put("DREM", 115);
        map.put("INEG", 116);
        map.put("LNEG", 117);
        map.put("FNEG", 118);
        map.put("DNEG", 119);
        map.put("ISHL", 120);
        map.put("LSHL", 121);
        map.put("ISHR", 122);
        map.put("LSHR", 123);
        map.put("IUSHR", 124);
        map.put("LUSHR", 125);
        map.put("IAND", 126);
        map.put("LAND", 127);
        map.put("IOR", 128);
        map.put("LOR", 129);
        map.put("IXOR", 130);
        map.put("LXOR", 131);
        map.put("IINC", 132);
        map.put("I2L", 133);
        map.put("I2F", 134);
        map.put("I2D", 135);
        map.put("L2I", 136);
        map.put("L2F", 137);
        map.put("L2D", 138);
        map.put("F2I", 139);
        map.put("F2L", 140);
        map.put("F2D", 141);
        map.put("D2I", 142);
        map.put("D2L", 143);
        map.put("D2F", 144);
        map.put("I2B", 145);
        map.put("I2C", 146);
        map.put("I2S", 147);
        map.put("LCMP", 148);
        map.put("FCMPL", 149);
        map.put("FCMPG", 150);
        map.put("DCMPL", 151);
        map.put("DCMPG", 152);
        map.put("IFEQ", 153);
        map.put("IFNE", 154);
        map.put("IFLT", 155);
        map.put("IFGE", 156);
        map.put("IFGT", 157);
        map.put("IFLE", 158);
        map.put("IF_ICMPEQ", 159);
        map.put("IF_ICMPNE", 160);
        map.put("IF_ICMPLT", 161);
        map.put("IF_ICMPGE", 162);
        map.put("IF_ICMPGT", 163);
        map.put("IF_ICMPLE", 164);
        map.put("IF_ACMPEQ", 165);
        map.put("IF_ACMPNE", 166);
        map.put("GOTO", 167);
        map.put("JSR", 168);
        map.put("RET", 169);
        map.put("TABLESWITCH", 170);
        map.put("LOOKUPSWITCH", 171);
        map.put("IRETURN", 172);
        map.put("LRETURN", 173);
        map.put("FRETURN", 174);
        map.put("DRETURN", 175);
        map.put("ARETURN", 176);
        map.put("RETURN", 177);
        map.put("GETSTATIC", 178);
        map.put("PUTSTATIC", 179);
        map.put("GETFIELD", 180);
        map.put("PUTFIELD", 181);
        map.put("INVOKEVIRTUAL", 182);
        map.put("INVOKESPECIAL", 183);
        map.put("INVOKESTATIC", 184);
        map.put("INVOKEINTERFACE", 185);
        map.put("INVOKEDYNAMIC", 186);
        map.put("NEW", 187);
        map.put("NEWARRAY", 188);
        map.put("ANEWARRAY", 189);
        map.put("ARRAYLENGTH", 190);
        map.put("ATHROW", 191);
        map.put("CHECKCAST", 192);
        map.put("INSTANCEOF", 193);
        map.put("MONITORENTER", 194);
        map.put("MONITOREXIT", 195);
        map.put("MULTIANEWARRAY", 197);
        map.put("IFNULL", 198);
        map.put("IFNONNULL", 199);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            sOpCodeMap.put(entry.getValue(), entry.getKey());
        }
    }

    def static getGlobal() {
        return sGlobal
    }

    static void setMethodDesc(MethodDesc methodDesc) {
        if (null == methodDesc) {
            return
        }
        sVisitMethod.put("${methodDesc.name}${methodDesc.desc}", methodDesc)
    }

    static MethodDesc getMethodDesc(String key) {
        return sVisitMethod.get(key)
    }

    static MethodDesc getMethodDesc(String name, String desc) {
        return sVisitMethod.get("$name$desc")
    }

    static def getVisitMethods() {
        return sVisitMethod
    }
}