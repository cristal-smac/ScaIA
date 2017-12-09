// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver._
import org.scaia.solver.asia.{ExhaustiveSolver, SelectiveSolver, Utilitarian}

/**
  * Main app to test multiples random examples
  * */
object TestHedonicSolverWithRandomMatchings{
  val debug= true
  val system = ActorSystem("ScaIA")//The Actor system
  def main(args: Array[String]): Unit = {
    println("n,m,utilitarianR,hedoUtilitarian,eUtilitarian,meanCR,meanHEDO,meanE")
    var n = 0
    for (n <- 2 to 2) {
      var m = 0
      for (m <- 2 to 20 ) {//(m <- 2 * n to 10 * n)
        val nbPb = 100
        val nbMatchings = 100
        var utilitarianR=0.0
        var hedoUtilitarian = 0.0
        var eUtilitarian = 0.0

        var meanCR = 0.0
        var meanHEDO = 0.0
        var meanE = 0.0
        var o=0
        for (o <- 1 to nbPb) {
          val pb = IAProblem.generateRandom(n, m)


          val solverR = new SelectiveSolver(pb,true,Utilitarian)
          var startingTime=System.currentTimeMillis()
          val resultR = solverR.solve()
          meanCR+=System.currentTimeMillis - startingTime
          utilitarianR += resultR.utilitarianWelfare()


          val hedosolver = new ASIA2HedonicSolver(pb)
          startingTime=System.currentTimeMillis()
          val hedoresult = hedosolver.solve()
          meanHEDO+=System.currentTimeMillis - startingTime
          hedoUtilitarian += hedoresult.utilitarianWelfare()

          val exhaustifsolver = new ExhaustiveSolver(pb,Utilitarian)
          startingTime=System.currentTimeMillis()
          val exhaustifresult = exhaustifsolver.solve()
          meanE+=System.currentTimeMillis - startingTime
          eUtilitarian += exhaustifresult.utilitarianWelfare()



        }
        println(n + "," + m + "," + utilitarianR/nbPb + "," +  hedoUtilitarian/nbPb + "," + eUtilitarian/nbPb + "," +
          meanCR/nbPb + "," +meanHEDO/nbPb + "," +meanE/nbPb
        )
      }
    }
  }
}