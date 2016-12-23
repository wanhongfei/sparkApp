import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
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
    /**
      * 第一步：创建Spark的配置对象，设置Spark程序的运行时的配置信息
      * 例如说通过setMaster来设置程序要连接的spark集群的master的url，如果设置为
      * local， 则代表Spark程序在本地运行，特别适合于机器配置条件非常差
      * （例如只有1g的内存）的初学者
      */
    val conf = new SparkConf() //创建SparkConf对象
    conf.setAppName("OnlineBlackListFilter") //设置Spark应用程序的名称，在程序运行的监控界面可以看到名称
    conf.setMaster("local") //此时，程序在本地运行，不需要安装Spark集群
//    conf.setMaster("spark://localhost:7077") //此时，程序在本地运行，不需要安装Spark集群

    val ssc = new StreamingContext(conf, Seconds(1))

    val wordStream = ssc.socketTextStream("localhost", 9999)
    val wordList = wordStream.map(word => (word,1)).reduceByKey((a,b) => (a+b)).print()


    val topicMap = "mytopic".split(":").map((_, 1)).toMap
    //zookeeper quorums server list
    val zkQuorum = "localhost:2181";
    //consumer group
    val group = "console-consumer-80037,console-consumer-73072"
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    lines.print()
    //
//
//    /**
//      * 黑名单数据准备，实际上黑名单一般都是动态的，例如在Redis中或者数据库中，黑名单的生成往往有复杂的业务逻辑，
//      * 具体情况算法不同，但是在SparkStreaming进行处理的时候每次都能够访问完整的信息
//      *
//      */
//    val blackList = Array(("hadoop", true), ("mahout", true))
//    val blackListRDD = ssc.sparkContext.parallelize(blackList, 8)
//
//    val adsClickStream = ssc.socketTextStream("localhost", 9999)
//
//    /**
//      * 此处模拟的广告点击的每条数据的格式为：time、name
//      * 此处map操作的结果是name, (time, name)的格式
//      */
//    val adsClickStreamFormatted = adsClickStream.map(ads => (ads.split(" ")(1), ads))
//    adsClickStreamFormatted.transform(userClickRDD => {
//      //通过leftOuterJoin操作既保留了左侧用户广告点击内容的RDD的所有内容，又获得了相应点击内容是否在黑名单中
//      val joinedBlackListRDD = userClickRDD.leftOuterJoin(blackListRDD)
//      println("=============================")
//      println(joinedBlackListRDD.collect())
//      println("=============================")
//      val validClicked = joinedBlackListRDD.filter(joinedItem => {
//
//
//        /**
//          * 进行filter过滤的时候，其输入元素是一个Tuple：（name,((time, name), boolean)）
//          * 其中第一个元素是黑名单的名称，第二个元素的第二个元素是进行leftOuterJoin的时候是否存在该值
//          * 如果存在的话，表明当前广告点击是黑名单，需要过滤掉，否则的话则是有效点击内容；
//          */
//        if (joinedItem._2._2.getOrElse(false)) {
//          false
//        } else {
//          true
//        }
//      })
//      validClicked.map(validClicked => {
//        validClicked._2._1
//      })
//    }).print()

    /**
      * 计算后的有效数据一般都会写入Kafka中，下游的计费系统会从Kafka中pull到有效数据进行计费
      */
    ssc.start()
    ssc.awaitTermination()
  }

}
