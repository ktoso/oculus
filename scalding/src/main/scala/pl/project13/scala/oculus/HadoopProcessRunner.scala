package pl.project13.scala.oculus

import java.io.File
import com.twitter.scalding
import scala.sys.process._
import pl.project13.hadoop.NoJarTool
import org.apache.hadoop.util.ToolRunner
import org.apache.hadoop.conf.Configuration
import scala.concurrent.{ExecutionContext, Future}

case class HadoopProcessRunner(args: List[String]) {

  import ExecutionContext.Implicits.global

  def runAndWait(conf: Configuration) = {
    val tool = new NoJarTool(new scalding.Tool, conf)
    ToolRunner.run(conf, tool, args.toArray)
  }

  def runAsync(conf: Configuration): Future[Int] = {
    val tool = new NoJarTool(new scalding.Tool, conf)

    Future {
      ToolRunner.run(conf, tool, args.toArray)
    }
  }

}
