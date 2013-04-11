package pl.project13.scala.oculus.common.noun

object PercentageNoun {

  implicit def nums2Percentage(num: Long) = PercentageNoun(num)
}

case class PercentageNoun(part: Long) {
  def percent(max: Long): Long = if(max == 0) 0 else part * 100 / max
  def percentString(max: Long): String = percent(max) + "%"

  def outOf(max: Long) = "[%s] out of [%s], that's [%s]".format(part, max, percentString(max))
}
