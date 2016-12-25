import org.apache.spark.SparkContext._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wanhongfei on 2016/12/24.
  * Object下的都是静态，Class下都是成员
  */
object RecoverableWordCountApp {

  /**
    * 函数入口
    *
    * @param args
    */
  def main(args: Array[String]) {
    // checkpoint目录
    val checkpointdir = "."
    val ssc = StreamingContext.getOrCreate(checkpointdir, () => {
      //getOrCreate方法，从checkpoint中重新创建StreamingContext对象或新创建一个StreamingContext对象
      println("====================== create new StreamingContext ============================")
      val conf = new SparkConf() /*.setMaster("local")*/ .setAppName("RecoverableWordCountApp")
      val sc = new SparkContext(conf)
      val ssc = new StreamingContext(sc, Seconds(10))
      ssc.checkpoint(checkpointdir)
      println("===============================================================================")
      // 返回ssc
      ssc
    })
    // 开启端口接收数据
    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)
    // 将字符串按照空格划分
    val words = lines.flatMap(_.split(" "))
    // 将words变成(word,1)
    val wordsMap = words.map(word => (word, 1))
    // 将(word,1)按照word，累加
    val wordsCntMap = wordsMap.reduceByKey(_ + _)
    // 打印输出
    wordsCntMap.print()
    // 开始监听端口
    ssc.start()
    ssc.awaitTermination()
  }
}