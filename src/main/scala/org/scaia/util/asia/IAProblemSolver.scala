// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

/**
  * Solve a particular IAProblem instance
  * TODO read command line arguments
  * sbt "run org.scaia.util.asia.IAProblemSolver examples/asia/circularPreferencepb.txt examples/asia/circularPreferenceMatching.txt"
  */
object IAProblemSolver extends App{
  //Run main
  println("Let us solve this problem")
  //Parse commande line
  if (args.length == 1) {
    println("no parameter")
  }else{
    args.foreach(println(_))
  }
  println("That's all folk !")
}
