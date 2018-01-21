// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

/**
  * Main app to test MSsolver vs DisMNSolver
  * sbt "run Welfare/Time Utilitarian/Egalitarian"
  * */
object Test{
  val debug= true
  def main(args: Array[String]): Unit = {

    args(0)  match {
      case "Welfare" => {
        val test = TestWelfareSolver
        test.main(Array[String](args(1)))
      }
      case "Time" => {
        val test = TestTimeSolver
        test.main(Array[String](args(1)))
      }
      case _ => {
        throw new RuntimeException("My argument ("+args(0)+") is not suppported")
      }
    }
  }
}