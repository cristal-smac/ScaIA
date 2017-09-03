// Copyright (C) Maxime MORGE 2017
package org.scaia.solver

import org.scaia.asia._
import org.scaia.hedonic
import org.scaia.hedonic.Game
import org.scaia.solver.asia.ASIASolver
import org.scaia.solver.hedonic.CISSolver



/**
  *  Translate the ASIA problem into an hedonic one , use CISSolver and retranslate
  *  @param pb to solved
  */
class ASIA2HedonicSolver(pb : IAProblem) extends ASIASolver(pb){

  override def solve() : Matching= {
    var hedonic=Game(pb)//call factory
    if (debug) println("Hedonic game: "+hedonic)
    val solver= new CISSolver(hedonic)
    val matching= solver.solve()
    if (debug) println("Hedonic mathing: "+matching)
    val result= Matching(matching,pb)
    return result
  }
}
