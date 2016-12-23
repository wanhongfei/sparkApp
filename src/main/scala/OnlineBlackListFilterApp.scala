import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 背景描述：在广告点击计费系统中，我们在线过滤掉黑名单的点击，进而保护广告商的利益，
  * 只进行有效的广告点击计费。或者在防刷评分（或者流量）系统,过滤掉无效的投票或者评分或者流量。
  * 实现技术：使用transform API直接基于RDD编程，进行join操作
  *
  * Created by Administrator on 2016/4/30.
  */
object OnlineBlackListFilterApp {

  def main(args: Array[String]) {
    //    val conf = new SparkConf() //创建SparkConf对象
    //    conf.setAppName("OnlineBlackListFilter") //设置Spark应用程序的名称，在程序运行的监控界面可以看到名称
    //    conf.setMaster("local") //此时，程序在本地运行，不需要安装Spark集群
    //    val ssc = new StreamingContext(conf, Seconds(10))
    //    val topicMap = "mytopic".split(":").map((_, 1)).toMap
    //    val lines = KafkaUtils.createStream(ssc, "localhost:2181", "80037", topicMap).map(_._2)
    //    lines.print()
    //    ssc.start()
    //    ssc.awaitTermination()
    val conf = new SparkConf() //创建SparkConf对象
    conf.setAppName("OnlineBlackListFilter") //设置Spark应用程序的名称，在程序运行的监控界面可以看到名称
    conf.setMaster("local") //此时，程序在本地运行，不需要安装Spark集群
    //    conf.setMaster("spark://master:7077") //此时，程序在本地运行，不需要安装Spark集群
    val ssc = new StreamingContext(conf, Seconds(100))
    val blackList = Array(("hadoop", true), ("mahout", true))
    val blackListRDD = ssc.sparkContext.parallelize(blackList, 8)
    val adsClickStream = ssc.socketTextStream("127.0.0.1", 9999)
    val adsClickStreamFormatted = adsClickStream.map(ads => (ads.split(" ")(1), ads))
    adsClickStreamFormatted.transform(userClickRDD => {
      if(!userClickRDD.partitions.isEmpty){}
      val joinedBlackListRDD = userClickRDD.leftOuterJoin(blackListRDD)
      val validClicked = joinedBlackListRDD.filter(joinedItem => {
        if (joinedItem._2._2.getOrElse(false)) {
          false
        } else {
          true
        }
      })
      validClicked.map(validClicked => {
        validClicked._2._1
      })
    }).print()
    ssc.start()
    ssc.awaitTermination()
  }

}
