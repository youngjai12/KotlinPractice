import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue


fun main1(args: Array<String>) {
    val ints = arrayListOf(123L, 234L, 343L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
    val d = mapOf<Int,MutableList<Long>>(0 to mutableListOf<Long>(), 1 to mutableListOf<Long>(), 2 to mutableListOf<Long>())
    val partition = 3
    for ((i, e) in ints.withIndex()) {
        val idx = i%partition
        d[idx]!!.add(e)
    }
    for(ele in d){
        println((0 until 3).random())
        println(ele.value.size)
    }
}

fun main(){
    val target = "나스닥"
    val tt: String? = null
    println(tt.equals(""))
    println(ForeignExchange.getExchangeByAlias(target))
    println(ForeignExchange.getExchangeByAlias("뉴욕"))
    println(ForeignExchange.getExchangeByAlias("nasdadfq"))

}

