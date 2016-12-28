import org.apache.spark.SparkContext._
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

/**
  * Created by wanhongfei on 2016/12/24.
  * Object下的都是静态，Class下都是成员
  */
object PartitionByWordCountApp {

  /**
    * 函数入口
    *
    * @param args
    */
  def main(args: Array[String]) {
    // 初始化常量
    val conf = new SparkConf() /*.setMaster("local")*/ .setAppName("PartitionByWordCountApp")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("file:///E:/bigdata.txt").collect()
    // 默认个数为运行的cpu个数
    val words = sc.parallelize(lines).flatMap(line => line.split(" ")).map(word => (word, 1));
    // 分26个区
    words.groupByKey(new AsciiPartitioner(27)).saveAsTextFile("file:///E:/temp/bigdata_output");
  }
}

//自定义分区类，需继承Partitioner类
class AsciiPartitioner(numParts: Int) extends Partitioner {

  //覆盖分区数
  override def numPartitions: Int = numParts

  //覆盖分区号获取函数
  override def getPartition(key: Any): Int = {
    val skey = key.toString.toLowerCase();
    if (skey.length == 0 || !(skey.charAt(0) >= 'a' && skey.charAt(0) <= 'z')) 26
    else
      skey.charAt(0).toInt % numParts
  }

}
