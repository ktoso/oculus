package pl.project13.scala.oculus

import org.powermock.api.mockito.PowerMockito._
import cascading.property.AppProps
import org.mockito.Matchers._

object FixCascading {

  type AnyMap = java.util.Map[AnyRef, AnyRef]

  def getApplicationJarClass(clazz: Class[_]) {
    mockStatic(classOf[AppProps])

    doReturn(clazz).when(AppProps.getApplicationJarClass(anyObject[AnyMap]))
  }
}
