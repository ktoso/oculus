package pl.project13.scala.oculus.distance

// todo implement me!!!!
/**
 * Based on paper: http://www.cs.huji.ac.il/~ofirpele/publications/ECCV2010.pdf
 *
 * As well as Fast Earth Mover's Distance (EMD) Code - http://www.cs.huji.ac.il/~ofirpele/FastEMD/code/
 * Which implements this paper: http://www.cs.huji.ac.il/~ofirpele/pics/pdf.gif
 */
object HistogramDistance {

  // Calculate a sum of set bits of XOR'ed bytes
  def hammingDistance(b1: Array[Byte], b2: Array[Byte]): Int =
    (b1.zip(b2).map((x: (Byte, Byte)) => numberOfBitsSet((x._1 ^ x._2).toByte))).sum

  // 1 iteration for each bit, 8 total. Shift right and AND 1 to get i-th bit
  @inline private def numberOfBitsSet(b: Byte): Int = (0 to 7).map((i: Int) => (b >>> i) & 1).sum

}
