// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia._

/**
  *  Heuristics which returns a "good" matching
  *  @param pb to solve
  *  @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  */
class HillClimbingInclusiveSolver(pb : IAProblem, rule: SocialRule) extends ASIASolver(pb){


  override def solve() : Matching =  {
    var current = pb.generateRandomPositiveInclusiveMatching()
    //Returns a random matching with as much as possible active individual over a positive problem not overconstrainted
    while(true){
      val neighbor= highValueSuccessor(current)
      val nu= rule match {
        case Utilitarian => neighbor.utilitarianWelfare()
        case Egalitarian => neighbor.egalitarianWelfare()
      }
      val cu= rule match {
        case Utilitarian => current.utilitarianWelfare()
        case Egalitarian => current.egalitarianWelfare()
      }
      if (nu <= cu) return current
      current= neighbor
    }
    return current//Only required for compilation
  }


  //Return the successor matching with highest utilitarian welfare
  def highValueSuccessor(current: Matching) = {
    val inds : Array[Individual] = pb.individuals.toArray
    var maxU = -1.0
    var bestMatching = new Matching(pb)
    pb.individuals.foreach{ i => //for each individual
      val ai= current.a(i)
      pb.activities.filterNot(_.equals(ai)).foreach{ a => //for each other activity
        if (!current.isFull(a)){//If the other activity is not full and attractive
        val neighbor= current
          //Move i to a
          neighbor.a+=(i -> a)
          //Build the groups
          neighbor.g+=(i-> (current.p(a) + i))
          current.p(a).foreach( j => neighbor.g+=(j -> (current.p(a) + i)))
          current.p(ai).foreach( j => neighbor.g+=(j -> (current.p(ai) - i)))
          val u= rule match {
            case Utilitarian => neighbor.utilitarianWelfare()
            case Egalitarian => neighbor.egalitarianWelfare()
          }
          if (u> maxU){
            maxU=u
            bestMatching=neighbor
          }
        }else{//If the other activity is full
          //Switch candidate
          current.p(a).foreach { j =>
            val neighbor = current.swap(i, j)
            val u = rule match {
              case Utilitarian => neighbor.utilitarianWelfare()
              case Egalitarian => neighbor.egalitarianWelfare()
            }
            if (u > maxU) {
              maxU = u
              bestMatching = neighbor
            }
          }
        }
      }
    }
    bestMatching
  }
  /*
  def highValueSuccessor(current: Matching) = {
    val inds : Array[Individual] = pb.individuals.toArray
    var maxU = -1.0
    var bestMatching = new Matching(pb)
    pb.individuals.foreach{ i => //for each individual
      val ai= current.a(i)
      pb.activities.filterNot(_.equals(ai)).foreach{ a => //for each other activity
        if (!current.full(a) && i.u(a.name)>0){//If the other activity is not full and attractive
        val neighbor= current
          //Move i to a
          neighbor.a+=(i -> a)
          //Build the groups
          neighbor.g+=(i-> (current.p(a) + i))
          current.p(a).foreach( j => neighbor.g+=(j -> (current.p(a) + i)))
          current.p(ai).foreach( j => neighbor.g+=(j -> (current.p(ai) - i)))
          val u= rule match {
            case Utilitarian => neighbor.utilitarianWelfare()
            case Egalitarian => neighbor.egalitarianWelfare()
            case NashProduct => neighbor.nashWelfare()
          }
          if (u> maxU){
            maxU=u
            bestMatching=neighbor
          }
        }else{//If the other activity is full
          //Switch candidate
          current.p(a).foreach { j =>
            if (i.u(a.name) > 0 && j.u(ai.name) >0 ) {// If the other activities are muttually attractive
              val neighbor = current.swap(i, j)
              val u = rule match {
                case Utilitarian => neighbor.utilitarianWelfare()
                case Egalitarian => neighbor.egalitarianWelfare()
                case NashProduct => neighbor.nashWelfare()
              }
              if (u > maxU) {
                maxU = u
                bestMatching = neighbor
              }
            }
          }
        }
      }
    }
    bestMatching
  }
  */
}

