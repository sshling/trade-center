package cn.shaolingweb.rml.tradecenter.domain

/**
 * 行情domain
 */
class Hq {
    var name: String? = null//gp名称
    var code: Int? = null
    var priceCurrent: Double? = null//当前价格
    var priceMax: Double? = null//今日最高价
    var priceMin: Double? = null//今日最低价
    var volumeTraded: Long? = null//成交的股数
    var b1Vol: Long? = null//买一数
    var s1Vol: Long? = null//卖一数
    override fun toString(): String {
        return String.format("%s[%s] cur:%s[%s,%s] volumeTraded[%s] ,b1Vol[%s] s1Vol[%s]",
                name,code,priceCurrent,priceMin,priceMax
                ,volumeTraded,b1Vol,s1Vol
        )
    }
}
