package pl.project13.scala.oculus.distance

object Distance {

  // Calculate a sum of set bits of XOR'ed bytes
  def hammingDistance(b1: Array[Byte], b2: Array[Byte]): Int =
    (b1.zip(b2).map((x: (Byte, Byte)) => numberOfBitsSet((x._1 ^ x._2).toByte))).sum

  // 1 iteration for each bit, 8 total. Shift right and AND 1 to get i-th bit
  @inline private def numberOfBitsSet(b: Byte): Int = (0 to 7).map((i: Int) => (b >>> i) & 1).sum

}
