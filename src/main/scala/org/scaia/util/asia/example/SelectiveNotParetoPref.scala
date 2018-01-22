// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia.example

import org.scaia.asia._
import org.scaia.solver.asia.{SelectiveSolver, Utilitarian}

/**
  * Example where the SelectiveSolver reach a non-Paretian result
  * reach different solution
  * m: 2
  * n: 1
  * activities: a(2)
  * individuals: 1 2
  * 1: a 0.6830438763184743
  * 1: 2 0.43062362702240264
  * 2: a -0.316059653554158
  * 2: 1 0.8798770134310938
  * b(2): [1]
  * void: [2]
  * */
object SelectiveNotParetoPref {

  val a = new Activity("a", 2)

  val one: Individual = new Individual("1", 2) {
    vMap += ("a" -> 0.6830438763184743)
    wMap += ("2" -> 0.43062362702240264)
  }

  val two: Individual = new Individual("2", 2) {
    vMap += ("a" ->  -0.316059653554158)
    wMap += ("1" -> 0.8798770134310938)
  }

  val pb = new IAProblem(Group(one, two), Set(a))
  def main(args: Array[String]): Unit = {
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