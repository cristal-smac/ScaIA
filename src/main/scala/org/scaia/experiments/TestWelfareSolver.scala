// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import java.io.{BufferedWriter, FileWriter}

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.experiments.TestTranslationUtilitarianSolver.file
import org.scaia.solver._
import org.scaia.solver.asia._

/**
  * Main app to test InclusiveSolver vs HillClimbing
  * sbt "run org.scaia.test.TestWelfareSolver Utilitarian/Egalitarian"
  * */
object TestWelfareSolver{
  val debug= false

  def main(args: Array[String]): Unit = {
    val criterion=args(0)
    val rule : SocialRule= criterion match {
      case "Utilitarian" => Utilitarian
      case "Egalitarian" => Egalitarian
      case _ => throw new RuntimeException("The argument is not suppported")
    }
    val file = s"experiments/data/welfare$rule.csv"
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("n,m,behaviourU,otherU,behaviourTime,otherTime\n")
    var n = 0
    for (n <- 2 to 10){
      var m = 0
      for (m <- 2 * n to 10 * n) {
        val nbPb = 100
        var behaviourU=0.0
        var otherU=0.0
        var behaviourTime = 0.0
        var otherTime= 0.0
        var o=0
        for (o <- 1 to nbPb) {
          if (debug) println(s"Pb $o")
          val pb = IAProblem.randomProblem(n, m)
          val solverR : ASIASolver  = rule match {
            case Utilitarian => new SelectiveSolver(pb, true, rule)
            case Egalitarian => new InclusiveSolver(pb, rule)
          }

          if (debug) println(s"Behaviour")
          var startingTime=System.currentTimeMillis()
          var result = solverR.solve()
          behaviourTime+=System.currentTimeMillis - startingTime
          behaviourU += (rule match {
            case Utilitarian => result.utilitarianWelfare()
            case Egalitarian => result.egalitarianWelfare()
          })

          if (debug) println(s"Local search")
          val otherSolver = new HillClimbingInclusiveSolver(pb, rule)
          startingTime=System.currentTimeMillis()
          result = otherSolver.solve()
          otherTime+=System.currentTimeMillis - startingTime
          otherU += (rule match {
            case Utilitarian => result.utilitarianWelfare()
            case Egalitarian => result.egalitarianWelfare()
          })

        }
        bw.write(s"$n,$m,${behaviourU/nbPb},${otherU/nbPb},${behaviourTime/nbPb},${otherTime/nbPb}\n")
        bw.flush()
      }
    }
  }
}