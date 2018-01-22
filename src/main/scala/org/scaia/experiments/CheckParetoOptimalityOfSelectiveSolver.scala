// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver._
import org.scaia.solver.asia.{SelectiveSolver, Utilitarian}

/**
  * Main app to check the Pareto-optimality of the outcome of the selective solver
  * */
object CheckParetoOptimalityOfSelectiveSolver{
  val debug= true
  val system = ActorSystem("CheckParetoOptimalityOfSelectiveSolver")//The Actor system
  def main(args: Array[String]): Unit = {
    var n = 0
    for (n <- 1 to 10) {
      var m = 0
      for (m <- 2 * n to 10 * n) {
        val nbPb = 100
        var paretoRate = 0.0
        var o=0
        for (o <- 1 to nbPb) {
          val pb = IAProblem.randomProblem(n, m, true, false)

          val solver = new SelectiveSolver(pb, false, Utilitarian)
          val result = solver.solve()
          if  (!result.isParetoOptimal()) {
            println(pb)
            print(result)
            System.exit(-1)

          } else {
            paretoRate+=1
          }
        }
        println(n + "," + m + "," + paretoRate/nbPb )
      }
    }
  }
}