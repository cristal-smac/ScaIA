// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver._
import org.scaia.solver.asia._

/**
  * Main app to test MSsolver vs DisMNSolver
  * sbt "run-main org.scaia.test.LargeTestSolverWithRandomMatchings Utilitarian"
  * */
object LargeTestInclusiveSolverWithRandomMatchings{
  val debug= true
  val system = ActorSystem("ScaIA")//The Actor system
  def main(args: Array[String]): Unit = {
    val criterion=args(0)
    val rule : SocialRule= criterion match {
      case "Utilitarian" => Utilitarian
      case "Egalitarian" => Egalitarian
      case _ => {
        throw new RuntimeException("My argument ("+criterion+") is not suppported")
      }
    }
    println("#n,m,mnU,dismnU,mnTime,dismnTime")
    var n = 0
    for (n <- 2 to 10) {//2 to 10
      var m = 0
      for (m <- 2 * n to 40 * n) {//2 * n to 40 * n
        val nbPb = 100
        var mnU=0.0
        var dismnU =0.0
        var mnTime = 0.0
        var dismnTime = 0.0
        var o=0
        for (o <- 1 to nbPb) {
          val c= (Math.ceil(m.toDouble/n.toDouble)).toInt
          //println("n: "+n+" m: "+m+" c: "+c)
          val pb = IAProblem.generatePositiveRandom(n, m, c)
          val solverR = new InclusiveSolver(pb,rule)
          var startingTime=System.currentTimeMillis()
          var result = solverR.solve()
          mnTime+=System.currentTimeMillis - startingTime
          mnU += (rule match {
            case Utilitarian => result.utilitarianWelfare()
            case Egalitarian => result.egalitarianWelfare()
          })

          val disSolverR = new DistributedInclusiveSolver(pb, system, true, rule)
          startingTime=System.currentTimeMillis()
          result = disSolverR.solve()
          dismnTime+=System.currentTimeMillis - startingTime
          dismnU += (rule match {
            case Utilitarian => result.utilitarianWelfare()
            case Egalitarian => result.egalitarianWelfare()
          })

        }
        println(n+","+m+","+mnU/nbPb+","+dismnU/nbPb+","+mnTime/nbPb+","+dismnTime/nbPb)
      }
    }
  }
}