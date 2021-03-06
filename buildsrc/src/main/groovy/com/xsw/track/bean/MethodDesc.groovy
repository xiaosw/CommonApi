package com.xsw.track.bean

class MethodDesc {
    // 原方法名
    String name
    // 原方法描述
    String desc
    // 方法所在的接口或类
    String parent
    // 采集数据的方法名
    String agentName
    // 采集数据的方法描述
    String agentDesc
    // 采集数据的方法参数起始索引（ 0：this，1+：普通参数 ）
    int paramsStart
    // 采集数据的方法参数个数
    int paramsCount
    // 参数类型对应的ASM指令，加载不同类型的参数需要不同的指令
    List<Integer> opcodes

    private String mTrackDesc
    private String mKey = ""

    MethodDesc(String name, String desc, String parent, String agentName, String agentDesc,
               int paramsStart, int paramsCount, List<Integer> opcodes, String trackDesc) {
        this.name = name
        this.desc = desc
        this.parent = parent
        this.agentName = agentName
        this.agentDesc = agentDesc
        this.paramsStart = paramsStart
        this.paramsCount = paramsCount
        this.opcodes = opcodes
        this.mTrackDesc = trackDesc
        this.mKey = name + desc
    }

    String getKey() {
        return mKey
    }

    String getTrackDesc() {
        return mTrackDesc
    }

    @Override
    public String toString() {
        return "MethodDesc{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", parent='" + parent + '\'' +
                ", agentName='" + agentName + '\'' +
                ", agentDesc='" + agentDesc + '\'' +
                ", paramsStart=" + paramsStart +
                ", paramsCount=" + paramsCount +
                ", opcodes=" + opcodes +
                ", trackDesc=" + mTrackDesc +
                ", key=" + mKey +
                '}';
    }
}