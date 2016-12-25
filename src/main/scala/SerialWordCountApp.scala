import org.apache.spark.SparkContext._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
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
    val ssc = new StreamingContext(sc, Seconds(10));
    // 设置checkpoint
    ssc.checkpoint(".")
    // 开启端口接收数据
    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)
    // 将字符串按照空格划分
    val words = lines.flatMap(_.split(" "))
    // 将words变成(word,1)
    val wordsMap = words.map(word => (word, 1))

    // 更新状态
    wordsMap.updateStateByKey(
      // groupByKey=>(string,[int1,int2])
      (currValues: Seq[Int], preValue: Option[Int]) => {
        val currValue = currValues.sum //将目前值相加
        Some(currValue + preValue.getOrElse(0)) //目前值的和加上历史值
      }
    ).print()

    // 开始监听端口
    ssc.start()
    ssc.awaitTermination()
  }
}