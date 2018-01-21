// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver._
import org.scaia.solver.asia.{SelectiveSolver, Utilitarian}

/**
  * Main app to test multiples random examples
  * */
object CheckSolverWithAllMatchings{
  val debug= true
  val system = ActorSystem("ScaIA")//The Actor system
  def main(args: Array[String]): Unit = {
    println("#n,m,bestUtilitarian,meansUtilitarian," +
      "utilitarian")
    var n = 0
    for (n <- 2 to 10) {
      var m = 0
      for (m <- 2 * n to 10 * n) {
        val nbPb = 100
        val nbMatchings = 10000
        var utilitarian=0.0
        var disUtilitarian =0.0
        var meansUtilitarian = 0.0
        var bestUtilitarian = 0.0
        var o=0
        for (o <- 1 to nbPb) {
          val pb = IAProblem.generateRandom(n, m)

          val solver = new SelectiveSolver(pb, false, Utilitarian)
          val result = solver.solve()
          utilitarian += result.utilitarianWelfare()

          var bestTmpUtilitarian = 0.0
          val matchings = pb.allSoundMatchings()
          matchings.foreach{ m =>
            val u = m.utilitarianWelfare()
            if (u > bestTmpUtilitarian) bestTmpUtilitarian = u
            meansUtilitarian += u
          }
          bestUtilitarian+=bestTmpUtilitarian
        }
        println(n + "," + m + "," + bestUtilitarian/nbPb + "," + meansUtilitarian/(nbMatchings*nbPb) + "," + utilitarian/nbPb )
      }
    }
  }
}