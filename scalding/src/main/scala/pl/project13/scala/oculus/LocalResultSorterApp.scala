package pl.project13.scala.oculus

import java.io.{BufferedReader, FileReader, FileOutputStream, File}
import com.google.common.io.{LineProcessor, Files, Resources}
import pl.project13.scala.oculus.phash.PHash
import pl.project13.scala.oculus.distance.Distance
import com.google.common.base.Charsets
import scala.collection.mutable
import org.apache.commons.collections.MultiMap
import com.google.common.collect.{ArrayListMultimap, Multimaps, Maps}

object LocalResultSorterApp extends App {

  val f = new File("/Users/ktoso/code/oculus/compare-YE7VzlLtp-4_AND_e98uKex3hSw.csv")

  val data = Files.readLines(f, Charsets.UTF_8, new LineProcessor[Map[Long, Data]] {

    var data = mutable.Map[Long, Data]()

    var first = true

    override def processLine(line: String): Boolean = {
      first match {
        case true =>
          first = false
          true

        case continue =>
          val arr = line.split(",")
          val d = Data(arr(0).toLong, arr(1).toLong, arr(2).toLong, arr(3), arr(4), arr(5).toLong, arr(6).toLong)
          import d._

          data.get(frameRef) match {
            case None => data(frameRef) = d
            case Some(old) =>
              if (histDist <= old.histDist && mhDist <= old.mhDist) {
                data(frameAtt) = d
              }
          }

          true
      }
    }

    override def getResult = data.toMap
  })

//  for {
//    frame <- data.keySet.toList.map(_.toLong).sorted
//    d = data(frame.toString)
//  } {
//    println(s"frame: $frame:    " +
//                "d.dctDist: " +
//                d.dctDist + ", ",
//            "d.histDist: " +
//                d.histDist + ", ",
//            "d.mhDist: " +
//                d.mhDist + ", ",
//            "d.idRef: " +
//                d.idRef + ", ",
//            "d.idAtt: " +
//                d.idAtt + ", ",
//            "d.frameRef: " +
//                d.frameRef + ", ",
//            "d.frameAtt: " +
//                d.frameAtt
//           )
//  }

  println("================ EXACT MATCHES ==================")
  val exactMatches = for {
    (frame, d) <- data
    if frame == d.frameRef
  } yield {
    println(s"$frame -> ${d.frameRef} :: $d")
    d
  }

  println("exactMatches.size = " + exactMatches.size)
  println("data.keySet.size = " + data.keySet.size)
  println("perfectMatchRatio = " + (exactMatches.size.toDouble / data.keySet.size))
  println("================ END OF EXACT MATCHES ==================")


  val multimap = Files.readLines(f, Charsets.UTF_8, new LineProcessor[ArrayListMultimap[Long, Data]] {

    var data: ArrayListMultimap[Long, Data] = ArrayListMultimap.create()

    var first = true

    override def processLine(line: String): Boolean = {
      first match {
        case true =>
          first = false
          true

        case continue =>
          val arr = line.split(",")
          val d = Data(arr(0).toLong, arr(1).toLong, arr(2).toLong, arr(3), arr(4), arr(5).toLong, arr(6).toLong)

          data.put(d.frameAtt, d)

          true
      }
    }

    override def getResult = data
  })


  import collection.JavaConverters._

  println("================ ORDERED BY DCT MATCHES ==================")
  val totalDctOff = for {
    frameAtt <- data.keySet
    results = data.get(frameAtt)

    ordered = results.toList.sortBy(_.dctDist)
    printMe = ordered.head
    fDiff = math.abs(frameAtt - printMe.frameRef)
  } yield {
    println(s"frame: $frameAtt, best (dct): " + printMe + s" ($fDiff off)")
    fDiff
  }

  println("================ ORDERED BY MH MATCHES ==================")
  val totalMhOff = for {
    frameAtt <- data.keySet
    results = data.get(frameAtt)

    ordered = results.toList.sortBy(_.mhDist)
    printMe = ordered.head
    fDiff = math.abs(frameAtt - printMe.frameRef)
  } yield {
    println(s"frame: $frameAtt, best (mh):" + printMe + s" ($fDiff off)")
    fDiff
  }

  println("================ ORDERED BY HIST MATCHES ==================")
  val totalHistOff = for {
    frameAtt <- data.keySet
    results = data.get(frameAtt)

    ordered = results.toList.sortBy(_.histDist)
    printMe = ordered.head
    fDiff = math.abs(frameAtt - printMe.frameRef)
  } yield {
    println(s"frame: $frameAtt, best (hist):" + printMe + s" ($fDiff off)")
    fDiff
  }

  println()
  println("totalDctOff = " + totalDctOff.sum)
  println("totalMhOff = " + totalMhOff.sum)
  println("totalHistOff = " + totalHistOff.sum)


  case class Data(
                     dctDist: Long,
                     histDist: Long,
                     mhDist: Long,
                     idRef: String,
                     idAtt: String,
                     frameRef: Long,
                     frameAtt: Long) {
    override def toString = s"""dctD: $dctDist, histD: $histDist, mhD: $mhDist, refId: $idRef, refFrame: $frameRef"""
  }

}

