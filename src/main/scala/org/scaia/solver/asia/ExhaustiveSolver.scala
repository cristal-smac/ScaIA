// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia._

/**
  *  Exhaustive solver which returns the matching with the best utilitarian welfare
  *  @param pb to solved
  *  @param rule to apply (maximize the utilitarian/egalitarian welfare
  */
class ExhaustiveSolver(pb : IAProblem, rule: SocialRule) extends ASIASolver(pb){

  /**
    *
    * @return
    */
  override def solve() = {
    var result = new Matching(pb)
    var maxSW = Double.MinValue
    pb.allSoundMatchings().foreach{ m=>
      val u= rule match {
        case Utilitarian => m.utilitarianWelfare()
        case Egalitarian => m.egalitarianWelfare()
      }
      if (u>maxSW){
        maxSW=u
        result=m
      }
    }
    result
  }
}
