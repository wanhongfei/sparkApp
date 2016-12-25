import org.apache.spark.SparkContext._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wanhongfei on 2016/12/24.
  * Object下的都是静态，Class下都是成员
  */
object SerialWordCountApp {

  /**
    * 函数入口
    *
    * @param args
    */
  def main(args: Array[String]) {
    // 初始化常量
    val conf = new SparkConf() /*.setMaster("local")*/ .setAppName("SerialWordCountApp")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(10))
    // 设置checkpoint
    ssc.checkpoint(".")
    // 开启端口接收数据
    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)
    // 将字符串按照空格划分
    val words = lines.flatMap(_.split(" "))
    // 将words变成(word,1)
    val wordsMap = words.map(word => (word, 1))

    // 更新状态
    // 使用了cogroup函数，会导致全部数据的扫描，性能低，大量复杂的计算不适合使用
//    wordsMap.updateStateByKey(
//      // groupByKey=>(string,[int1,int2])
//      (currValues: Seq[Int], preValue: Option[Int]) => {
//        val currValue = currValues.sum //将目前值相加
//        Some(currValue + preValue.getOrElse(0)) //返回目前值的和加上历史值
//      }
//    ).print()

    // 更新状态，针对某key的当前状态和前状态
    val mappingFunc = (word: String, one: Option[Int], state: State[Int]) => {
      val sum = one.getOrElse(0) + state.getOption.getOrElse(0)
      val output = (word, sum) // 最新的状态
      state.update(sum) // 更新状态
      output // 放回最新的状态
    }
    // 最新状态
    val state = wordsMap.mapWithState(StateSpec.function(mappingFunc))
    // 打印结果
    state.print()

    // 开始监听端口
    ssc.start()
    ssc.awaitTermination()
  }
}