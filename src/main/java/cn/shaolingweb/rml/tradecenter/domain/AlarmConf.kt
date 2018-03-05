package cn.shaolingweb.rml.tradecenter.domain

class AlarmConf {
    var code: String? = null
    var name: String? = null
    var up: Double = 0.toDouble()
    var down: Double = 0.toDouble()
    var upKeepTime: Int = 0
    var downKeepTime: Int = 0
    var b1Vol: Long = 0
    var s1Vol: Long = 0
}
