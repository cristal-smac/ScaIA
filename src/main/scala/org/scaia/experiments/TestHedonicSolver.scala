// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver.ASIA2HedonicSolver
import org.scaia.solver.asia._
import org.scaia.solver.hedonic._
/**
  * Main app to test multiples random examples
  * */
object TestHedonicSolver{
  val debug= true
  val system = ActorSystem("TestHedonicSolver")//The Actor system
  def main(args: Array[String]): Unit = {
    println("n,m,selectiveU,hedoU,miqpU,selectiveTime,hedonicTime,miqpTime")
    var n = 0
    for (n <- 2 to 2) {
      var m = 0
      for (m <- 2 to 20 ) {//(m <- 2 * n to 10 * n)
        val nbPb = 100
        val nbMatchings = 100
        var selectiveU=0.0
        var hedonicU = 0.0
        var miqpU = 0.0

        var selectiveTime = 0.0
        var hedonicTime = 0.0
        var timeMIQP = 0.0
        var o=0
        for (o <- 1 to nbPb) {
          val pb = IAProblem.generateRandom(n, m)

          val selectiveSolver = new SelectiveSolver(pb,true,Utilitarian)
          var startingTime=System.currentTimeMillis()
          val selectiveResult = selectiveSolver.solve()
          selectiveTime+=System.currentTimeMillis - startingTime
          selectiveU += selectiveResult.utilitarianWelfare()

          val hedonicSolver = new ASIA2HedonicSolver(pb)
          startingTime=System.currentTimeMillis()
          val hedonicResult = hedonicSolver.solve()
          hedonicTime+=System.currentTimeMillis - startingTime
          hedonicU += hedonicResult.utilitarianWelfare()

          val miqpSolver = new MIQPSolver(pb,Utilitarian)
          startingTime=System.currentTimeMillis()
          val miqpResult = miqpSolver.solve()
          timeMIQP+=System.currentTimeMillis - startingTime
          miqpU += miqpResult.utilitarianWelfare()

        }
        println(n + "," + m + "," + selectiveU/nbPb + "," +  hedonicU/nbPb + "," + miqpU/nbPb + "," +
          selectiveTime/nbPb + "," +hedonicTime/nbPb + "," +timeMIQP/nbPb
        )
      }
    }
  }
}