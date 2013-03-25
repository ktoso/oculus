// Until https://github.com/siasia/xsbt-proguard-plugin/issues/15 is resolved, using our patched version.
// GitHub fork with the changes: https://github.com/adamw/xsbt-proguard-plugin
libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-proguard-plugin" % (v+"-0.1.3-sml"))

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.0")
