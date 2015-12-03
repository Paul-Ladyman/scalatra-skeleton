resolvers += "Templemore Repository" at "http://templemore.co.uk/repo"

resolvers += "Local Ivy repository" at "file:///" + Path.userHome + "/.ivy2/local"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

addSbtPlugin("org.scalatra.sbt" % "scalatra-sbt" % "0.3.5")

addSbtPlugin("templemore" % "sbt-cucumber-plugin" % "0.8.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.9")