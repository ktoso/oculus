package pl.project13.scala.oculus.conversions

import com.twitter.scalding.{Mode, Job, FilterFunction, TupleConverter}
import cascading.tuple.Fields
import cascading.pipe.{Each, Pipe}

trait WriteDOT {
  this: Job =>

  override def buildFlow(implicit mode : Mode) = {
    validateSources(mode)
    // Sources are good, now connect the flow:
    val flow = mode.newFlowConnector(config).connect(flowDef)
    flow.writeDOT(getClass.getSimpleName + ".dot")
    flow.writeStepsDOT(getClass.getSimpleName + "-steps.dot")
    flow
  }

}