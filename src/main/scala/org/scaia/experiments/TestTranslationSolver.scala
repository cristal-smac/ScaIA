// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import java.io.{BufferedWriter, FileWriter}

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver.ASIA2HedonicSolver
import org.scaia.solver.asia._

/**
  * Main app to test multiples random examples
  * TODO
  * */
object TestTranslationSolver{
  val debug= true
  val file = "experiments/data/translation.csv"

  val system = ActorSystem("TestTranslationSolver")//The Actor system
  def main(args: Array[String]): Unit = {
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("n,m,selectiveU,hedoU,miqpU,selectiveTime,preHedonicTime,postHedonic,hedonicTime,preMiqpTime,postMiqpTime,miqpTime\n")
    var n = 0
    for (n <- 2 to 2) {
      var m = 0
      for (m <- 2 to 30 ) {
        val nbPb = 100
        val nbMatchings = 100
        var selectiveU=0.0
        var hedonicU = 0.0
        var miqpU = 0.0

        var selectiveTime = 0.0
        var hedonicTime = 0.0
        var preHedonicTime = 0.0
        var postHedonicTime = 0.0

        var miqpTime = 0.0
        var preMiqpTime = 0.0
        var postMiqpTime = 0.0

        var o=0
        for (o <- 1 to nbPb) {
          val pb = IAProblem.randomProblem(n, m)

          val selectiveSolver = new SelectiveSolver(pb,true,Utilitarian)
          var startingTime=System.nanoTime
          val selectiveResult = selectiveSolver.solve()
          selectiveTime+=System.nanoTime - startingTime
          selectiveU += selectiveResult.utilitarianWelfare()

          val hedonicSolver = new ASIA2HedonicSolver(pb)
          startingTime=System.nanoTime()
          val hedonicResult = hedonicSolver.solve()
          hedonicTime+=System.nanoTime - startingTime
          preHedonicTime+=hedonicSolver.preSolvingTime
          postHedonicTime+=hedonicSolver.postSolvingTime
          hedonicU += hedonicResult.utilitarianWelfare()

          val miqpSolver = new MIQPSolver(pb,Utilitarian)
          startingTime=System.nanoTime()
          val miqpResult = miqpSolver.solve()
          miqpTime+=System.nanoTime - startingTime
          preMiqpTime+=miqpSolver.preSolvingTime
          postMiqpTime+=miqpSolver.postSolvingTime
          miqpU += miqpResult.utilitarianWelfare()


        }
        bw.write(n + "," + m + "," + selectiveU/nbPb + "," +  hedonicU/nbPb + "," + miqpU/nbPb + "," +
          selectiveTime/nbPb + "," +preHedonicTime/nbPb +"," +postHedonicTime/nbPb +"," +hedonicTime/nbPb + "," +
          preMiqpTime/nbPb +"," + postMiqpTime/nbPb + "," + miqpTime/nbPb + "\n"
        )
        bw.flush()
      }
    }
    bw.close()
  }
}