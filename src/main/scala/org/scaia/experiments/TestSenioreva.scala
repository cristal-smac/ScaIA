// Copyright (C) Maxime MORGE 2018
package org.scaia.experiments

import akka.actor.ActorSystem
import org.scaia.solver.asia.SelectiveSolver
import org.scaia.util.asia.IAProblemParser
import org.scaia.solver.asia._

/**
  * Main app to test the solver over the data collected during senioreva
  * */
object TestSenioreva{
  val debug= true
  val system = ActorSystem("TestSenioreva")

  def main(args: Array[String]): Unit = {
    val parser =new IAProblemParser("examples/asia/senioreva.txt")// eventually "notBestUtilPb.txt"
    val pb = parser.parse()

    val selectiveSolver = new SelectiveSolver(pb, true, Utilitarian)
    val inclusiveSolver = new InclusiveSolver(pb, Egalitarian)
    val disSelectiveSolver = new DistributedSelectiveSolver(pb, system,true, Utilitarian)
    val disInclusiveSolver = new DistributedInclusiveSolver(pb, system, Egalitarian)
    val hillSolverUtil = new HillClimbingInclusiveSolver(pb, Utilitarian)
    val hillSolverEgal = new HillClimbingInclusiveSolver(pb, Egalitarian)
    val MIQPSolverUtil = new MIQPSolver(pb, Utilitarian)
    val MIQPSolverEgal = new MIQPSolver(pb, Egalitarian)

    var startingTime = System.currentTimeMillis()
    var outcome = selectiveSolver.solve()
    var time = System.currentTimeMillis - startingTime
    println(s"Selective: U(M)=${outcome.utilitarianWelfare()} T(ms)=$time")

    startingTime = System.currentTimeMillis()
    outcome = disSelectiveSolver.solve()
    time = System.currentTimeMillis - startingTime
    println(s"DisSelective U(M)=${outcome.utilitarianWelfare()} T(ms)=$time")

    startingTime = System.currentTimeMillis()
    outcome = hillSolverUtil.solve()
    time = System.currentTimeMillis - startingTime
    println(s"HillSolverUtil U(M)=${outcome.utilitarianWelfare()} T(ms)=$time")

    startingTime = System.currentTimeMillis()
    outcome = MIQPSolverUtil.solve()
    time = System.currentTimeMillis - startingTime
    println(s"MIQPSolverUtil U(M)=${outcome.utilitarianWelfare()} T(ms)=$time")

    startingTime = System.currentTimeMillis()
    outcome = inclusiveSolver.solve()
    time = System.currentTimeMillis - startingTime
    println(s"Inclusive: E(M)=${outcome.egalitarianWelfare()} T(ms)=$time")

    startingTime = System.currentTimeMillis()
    outcome = hillSolverEgal.solve()
    time = System.currentTimeMillis - startingTime
    println(s"HillSolverEgal E(M)=${outcome.egalitarianWelfare()} T(ms)=$time")

    startingTime = System.currentTimeMillis()
    outcome = MIQPSolverEgal.solve()
    time = System.currentTimeMillis - startingTime
    println(s"MIQPSolverUtil E(M)=${outcome.egalitarianWelfare()} T(ms)=$time")


    System.exit(0)
  }
}