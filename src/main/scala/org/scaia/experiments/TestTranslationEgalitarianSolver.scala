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
object TestTranslationUtilitarianSolver{
  val debug= true
  val file = "experiments/data/translationEgalitarian.csv"

  val system = ActorSystem("TestTranslationEgalitarianSolver")//The Actor system
  def main(args: Array[String]): Unit = {
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("n,m,inclusiveE,hedoE,miqpE,inclusiveTime,preHedonicTime,postHedonic,hedonicTime,preMiqpTime,postMiqpTime,miqpTime\n")
    var n = 0
    for (n <- 2 to 2) {
      var m = 0
      for (m <- 2 to 30 ) {
        val nbPb = 100
        val nbMatchings = 100
        var selectiveE=0.0
        var hedonicE = 0.0
        var miqpE = 0.0

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

          val inclusiveSolver = new InclusiveSolver(pb,Egalitarian)
          var startingTime=System.nanoTime
          val inclusiveResult = inclusiveSolver.solve()
          selectiveTime+=System.nanoTime - startingTime
          selectiveE += inclusiveResult.egalitarianWelfare()

          val hedonicSolver = new ASIA2HedonicSolver(pb)
          startingTime=System.nanoTime()
          val hedonicResult = hedonicSolver.solve()
          hedonicTime+=System.nanoTime - startingTime
          preHedonicTime+=hedonicSolver.preSolvingTime
          postHedonicTime+=hedonicSolver.postSolvingTime
          hedonicE += hedonicResult.egalitarianWelfare()

          val miqpSolver = new MIQPSolver(pb,Utilitarian)
          startingTime=System.nanoTime()
          val miqpResult = miqpSolver.solve()
          miqpTime+=System.nanoTime - startingTime
          preMiqpTime+=miqpSolver.preSolvingTime
          postMiqpTime+=miqpSolver.postSolvingTime
          miqpE += miqpResult.egalitarianWelfare()


        }
        bw.write(n + "," + m + "," + selectiveE/nbPb + "," +  hedonicE/nbPb + "," + miqpE/nbPb + "," +
          selectiveTime/nbPb + "," +preHedonicTime/nbPb +"," +postHedonicTime/nbPb +"," +hedonicTime/nbPb + "," +
          preMiqpTime/nbPb +"," + postMiqpTime/nbPb + "," + miqpTime/nbPb + "\n"
        )
        bw.flush()
      }
    }
    bw.close()
  }
}