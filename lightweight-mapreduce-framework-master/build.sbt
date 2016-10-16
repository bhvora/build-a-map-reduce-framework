//Author: Harshali Singh

name              := "S3Utils"
version           := "0.1.0-SNAPSHOT"
organization      := "com.github.harshali"
mainClass in (Compile) := Some("S3Utils")
publishMavenStyle := true
crossPaths        := false
autoScalaLibrary  := false

// library dependencies. (orginization name) % (project name) % (version)
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.10.65"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => {
 case entry => {
   val strategy = mergeStrategy(entry)
   if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
   else strategy
 }
}}

