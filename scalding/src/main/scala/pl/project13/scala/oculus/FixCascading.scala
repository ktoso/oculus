//package pl.project13.scala.oculus
//
//import org.powermock.api.mockito.PowerMockito
//import org.powermock.api.mockito.PowerMockito._
//import cascading.property.AppProps
//import org.mockito.Matchers._
//import java.util
//import org.mockito.stubbing.Answer
//import org.mockito.invocation.InvocationOnMock
//import org.mockito.Matchers
//
//object FixCascading {
//
//  type AnyMap = java.util.Map[AnyRef, AnyRef]
//
//  def getApplicationJarClass(clazz: Class[_]) {
//    val mockClazz: Class[AppProps] = classOf[AppProps]
//
//    mockStatic(mockClazz)
//
//    val map = new util.HashMap[AnyRef,AnyRef]()
//    map.put("cascading.app.appjar.class", clazz)
////    PowerMockito.when(AppProps.getApplicationJarClass(map))
////      .thenReturn(clazz)
//
//    type AnyMap = java.util.Map[AnyRef, AnyRef]
////    when(AppProps.getApplicationJarClass(any(classOf[AnyMap])))
////      .thenThrow(new RuntimeException("a"))
//
//    doThrow(new RuntimeException("a"))
//      .when(classOf[AppProps], "getApplicationJarClass", anyMap)
//
//    println("clazz = " + clazz)
//    println("AppProps.getApplicationJarClass(null) = " + AppProps.getApplicationJarClass(map))
//  }
//}
