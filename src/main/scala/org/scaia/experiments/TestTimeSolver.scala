// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver.asia._
import scala.util.Random

/**
  * Main app to test MSsolver vs DisMNSolver
  * sbt "run-main org.scaia.test.TestTimeSolver Utilitarian/Egalitarian"
  * */
object TestTimeSolver{
  val debug= true
  def main(args: Array[String]): Unit = {
    val criterion=args(0)
    val r = scala.util.Random
    val system = ActorSystem("TestTimeSolver"+criterion+r.nextString(5))//The Actor system
    val rule : SocialRule= criterion match {
      case "Utilitarian" => Utilitarian
      case "Egalitarian" => Egalitarian
      case _ => {
        throw new RuntimeException("My argument ("+criterion+") is not suppported")
      }
    }
    println("n,m,cU,dU,cTime,dTime")
    var n = 0
    for (n <- 2 to 10) {
      var m = 0
      for (m <- 2 * n to 40 * n) {
        val nbPb = 100
        var centraliedU=0.0
        var distributedU =0.0
        var centralizeTime = 0.0
        var distributedTime = 0.0
        var o=0
        for (o <- 1 to nbPb) {
          val pb = IAProblem.randomProblem(n, m)

          val centralizedSolver : ASIASolver  = rule match {
            case Utilitarian => new SelectiveSolver(pb, true, rule)
            case Egalitarian => new InclusiveSolver(pb, rule)
          }
          var startingTime=System.currentTimeMillis()
          var result = centralizedSolver.solve()
          centralizeTime+=System.currentTimeMillis - startingTime
          centraliedU += (rule match {
            case Utilitarian => result.utilitarianWelfare()
            case Egalitarian => result.egalitarianWelfare()
          })
          val distributedSolver : ASIASolver  = rule match {
            case Utilitarian => new DistributedSelectiveSolver(pb, system, true, rule)
            case Egalitarian => new DistributedInclusiveSolver(pb, system, true, rule)
          }

          startingTime=System.currentTimeMillis()
          result = distributedSolver.solve()
          distributedTime+=System.currentTimeMillis - startingTime
          distributedU += (rule match {
            case Utilitarian => result.utilitarianWelfare()
            case Egalitarian => result.egalitarianWelfare()
          })

        }
        println(n+","+m+","+centraliedU/nbPb+","+distributedU/nbPb+","+centralizeTime/nbPb+","+distributedTime/nbPb)
      }
    }
  }
}