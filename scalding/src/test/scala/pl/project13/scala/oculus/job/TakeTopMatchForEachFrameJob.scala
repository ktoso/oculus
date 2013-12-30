package pl.project13.scala.oculus.job

import com.google.common.annotations.VisibleForTesting
import com.twitter.scalding.{Tsv, Job, Args, IterableSource}

@VisibleForTesting
class TakeTopMatchForEachFrameJob(source: IterableSource[TakeTopMatchData])(args: Args) extends Job(args) {

  val inputFile = args("input")
  val outputFile = args("output")

  source
    .read
    .mapTo(0 -> ('idRef, 'dist, 'frameFrame, 'frameRef)) { in: TakeTopMatchData =>
      (in.idRef, in.dist, in.frameFrame, in.frameRef)
    }
    .groupBy('frameFrame) {
      _.sortWithTake(('dist, 'frameRef, 'idRef) -> 'topMatchL, 1) { (d1: (Int, String, String), d2: (Int, String, String)) =>
        d1._1 < d2._1
      }
    }
    .map('topMatchL -> 'topMatchL) { t: List[_] => t.head }
    .write(Tsv(outputFile))

}

case class TakeTopMatchData(idRef: String, dist: Long, frameFrame: String, frameRef: String)