// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import java.io.{BufferedWriter, FileWriter}

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver._
import org.scaia.solver.asia._

/**
  * Main app to check the Pareto-optimality of the outcome of the selective solver
  * */
object EvaluateSelectiveSolver{
  val debug= true
  def main(args: Array[String]): Unit = {
    val file = s"experiments/data/selective.csv"
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("n,m,U(M),MaxU,ParetoRate,IRRate\n")
    var n = 0
    for (n <- 2 to 2) {
      var m = 0
      for (m <- 2 * n to 20) {
        var paretoRate = 0.0
        var irRate = 0.0
        var u = 0.0
        var maxU = 0.0

        val nbPb = 100
        var o=0
        for (o <- 1 to nbPb) {
          val pb = IAProblem.randomProblem(n, m)
          var solver : ASIASolver = new SelectiveSolver(pb, false, Utilitarian)
          var result = solver.solve()
          u+=result.utilitarianWelfare()
          if (result.isParetoOptimal()) paretoRate+=1
          if (result.isIndividuallyRational()) irRate+=1

          solver = new MIQPSolver(pb,Utilitarian)
          result = solver.solve()
          maxU+=result.utilitarianWelfare()
        }
        bw.write(s"$n,$m,${u/nbPb},${maxU/nbPb},${paretoRate/nbPb},${irRate/nbPb}\n")
        bw.flush()
      }
    }
    bw.close()
    System.exit(0)
  }
}