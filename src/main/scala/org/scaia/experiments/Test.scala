// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.asia._
import org.scaia.solver._

/**
  * Main app to test MSsolver vs DisMNSolver
  * sbt "run org.scaia.experiment.Test Utilitarian"
  * */
object Test{
  val debug= true
  def main(args: Array[String]): Unit = {
    args(0)  match {
      case "Welfare" => {
        val test = TestInclusiveSolverWithRandomMatchings
        test.main(Array[String](args(1)))
      }
      case "Time" => {
        val test = LargeTestInclusiveSolverWithRandomMatchings
        test.main(Array[String](args(1)))
      }
      case _ => {
        throw new RuntimeException("My argument ("+args(0)+") is not suppported")
      }
    }
  }
}