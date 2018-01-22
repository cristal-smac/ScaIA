// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia.example

import org.scaia.asia._
import org.scaia.solver.asia.{SelectiveSolver, Utilitarian}
import org.scaia.util.asia.IAProblemParser

/**
  * Example where the SelectiveSolver reach a non-Paretian result
  * */
object SelectiveNotParetoPref {

  def main(args: Array[String]): Unit = {
    val parser =new IAProblemParser("examples/asia/selectiveNotParetoOptimal.txt")
    val pb = parser.parse()
    println(pb)
    val solver = new SelectiveSolver(pb, false, Utilitarian)
    solver.debug = true
    val result = solver.solve()
    println(result)
    if (!result.isParetoOptimal()) {
      println("Is not Pareto-optimal contrary to")
      pb.allSoundMatchings().filter(m => m.isParetoOptimal()).foreach{ m =>
        println(m)
      }
    } else {
      println("Is Pareto-optimal")
    }

  }
}