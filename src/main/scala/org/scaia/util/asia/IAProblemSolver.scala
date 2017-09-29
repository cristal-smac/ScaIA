// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

/**
  * Solve a particular IAProblem instance
  * sbt "run org.scaia.util.asia.IAProblemSolver -h -i -d foo bar"
  * java -jar target/scala-2.11/ScaIA-assembly-0.3.jar -h -i -d foo bar
  */
object IAProblemSolver extends App{
  val usage = """
    Usage: java -jar ScaIA-assembly-X.Y.jar [-hide] inputFilename outputFilename
    The following options are available:
    -h: hillclimbing (false by default)
    -i: inclusive (false by default)
    -d: distributed (false by defaut)
    -e: egalitarian (utilitarian by defaut)")
  """

  // Defalut values
  var hillclimbing= false
  var inclusive=false
  var distributed=false
  var egalitarian=false

  //Run main
  println("Let us solve this problem")
  //Parse commande line
  if (args.length <= 2) {
    println(usage)
    System.exit(1)
  }
  args.foreach(println(_))
  val arglist = args.toList
  //TODO https://stackoverflow.com/questions/2315912/best-way-to-parse-command-line-parameters


  println("That's all folk !")
}
