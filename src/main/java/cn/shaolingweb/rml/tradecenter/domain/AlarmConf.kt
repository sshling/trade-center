package cn.shaolingweb.rml.tradecenter.domain

class AlarmConf {
    var code: Int? = null
    var name: String? = null
    var up: Double = 0.toDouble()
    //需要自定义JSON转换这个字段
    var upKeepTimeStr: String? = null
    var upKeepTime: Int = 0
    var down: Double = 0.toDouble()
    var downKeepTime: Int = 0
    var downKeepTimeStr: String? = null
    var b1Vol: Long = 0
    var s1Vol: Long = 0
}
