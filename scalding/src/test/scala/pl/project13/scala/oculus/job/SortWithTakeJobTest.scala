//package pl.project13.scala.oculus.job
//
//import org.scalatest.FlatSpec
//import org.scalatest.matchers.ShouldMatchers
//import com.twitter.scalding._
//
//class SortWithTakeJobTest extends FlatSpec with ShouldMatchers with TupleConversions {
//
//  "SortWithTake" should "take 10" in {
//    val input = List(
//      "0" -> "kapi kapi pi pu po klop kap pop pup hop mop",
//      "0" -> "kapi kapi pi pu po klop kap pop pup hop mop",
//      "0" -> "kapi kapi pi pu po klop kap pop pup hop mop",
//      "0" -> "kapi kapi pi pu po klop kap pop pup hop mop",
//      "0" -> "kapi kapi pi pu po klop kap pop pup hop mop",
//      "0" -> "kapi kapi pi pu po klop kap pop pup hop mop",
//      "0" -> "kapi kapi pi pu po klop kap pop pup hop mop",
//      "0" -> "kapi kapi pi pu po klop kap pop pup hop mop"
//    )
//
//    JobTest(new SortWithTakeJob(_))
//      .arg("input", "inFile")
//      .arg("output", "outFile")
//      .source(TextLine("inFile"), input)
//      .sink[(String, List[String])](Tsv("outFile")) { out =>
//        val list = out.toList
//        list foreach println
//        list should contain ("hop" -> List("hop", "hop"))
//      }
//      .run
//      .finish
//  }
//
//
//}
