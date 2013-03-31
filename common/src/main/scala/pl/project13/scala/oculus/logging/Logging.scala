package pl.project13.scala.oculus.logging

import grizzled.slf4j.Logger

trait Logging {
  val logger = Logger(getClass)
}
