// Copyright (C) Maxime MORGE 2017
package org.scaia.experiments

/*
import optimus.optimization._

/*
*  "com.github.vagmcs" %% "optimus" % "2.0.0",
*  "oscar" % "oscar-cp_2.11" % "3.1.0-SNAPSHOT"
 */
object TestQuadraticProg {

  def main(args: Array[String]): Unit = {
    implicit val problem = LQProblem(SolverLib.gurobi)

    val x1d= MPIntVar("x1s", 0 to 1)
    val x2d= MPIntVar("x2d", 0 to 1)
    val x3d = MPIntVar("x3d", 0 to 1)
    val x4d = MPIntVar("x4d", 0 to 1)
    val x1s= MPIntVar("x1s", 0 to 1)
    val x2s= MPIntVar("x2s", 0 to 1)
    val x3s = MPIntVar("x3s", 0 to 1)
    val x4s = MPIntVar("x4s", 0 to 1)

    maximize(
      x1s - .33 * x1s * x2s + .33 * x1s * x3s + .33 * x1s * x4s
    + .5 * x2s  + .33 * .5 * x2s * x1s  + .33 * .5 * x2s * x3s  + .33 * .5 * x2s * x4s
    + x3s + .33 * x3s * x1s + .33 * x3s * x2s
    + .5 * x4s + .33 * x4s * x1s - .33 * x4s * x2s - .33 * x4s *x3s
    + x1d - .33 * x1d * x2d + .33 * x1d * x3d + .33 * x1d * x4d
    + .5 * x2d + .33 * .5 * x2d * x1d + 0.33 * .5 * x2d * x3d  + .33 * .5 * x2d * x4d
    + x3d + .33 * x3d * x1d + .33 * x3d * x2d
    + .5 * x4d +  .33 * x4d * x1d  - .33 * x4d * x2d - .33 * x4d * x3d
    )
    subjectTo(
      x1d + x1s <:=1,
      x2d + x2s <:=1,
      x3d + x3s <:=1,
      x4d + x4s <:=1,
      x1d + x2d + x3d + x4d <:= 2,
      x1s + x2s + x3s + x4s <:= 2
    )

   start()
    println("x1s= " + x1s.value + " x1d= " + x1d.value)
    println("x2s= " + x2s.value + " x2d= " + x2d.value)
    println("x3s= " + x3s.value + " x3d= " + x3d.value)
    println("x4s= " + x4s.value + " x4d= " + x4d.value)

    release()
  }
}

*/