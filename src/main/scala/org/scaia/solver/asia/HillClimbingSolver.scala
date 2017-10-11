// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia._

/**
  *  Local search algorithm which returns a "good" matching
  *  @param pb to solve
  *  @param rule to apply (maximize the utilitarian/egalitarian welfare
  */
class HillClimbingSolver(pb : IAProblem, rule: SocialRule) extends ASIASolver(pb){

  var step= 1
  /**
    * Returns a matching which maximizes the social rule
    * @return
    */
  override def solve() : Matching =  {
    var current = pb.generateRandomMatching()
    while(true){
      val neighbor= highValueSuccessor(current)
      val nw= rule match {
        case Utilitarian => neighbor.utilitarianWelfare()
        case Egalitarian => neighbor.egalitarianWelfare()
      }
      val cw= rule match {
        case Utilitarian => current.utilitarianWelfare()
        case Egalitarian => current.egalitarianWelfare()
      }
      if (nw <= cw) return current
      if (debug) println(s"HillClimbingSolver: step $step")
      step+=1
      current= neighbor
    }
    return current
  }

  /**
    *  Return the successor matching with the highest utilitarian welfare
    */
  def highValueSuccessor(current: Matching) = {
    val inds : Array[Individual] = pb.individuals.toArray
    var maxW = -1.0
    var bestMatching = new Matching(pb)
    pb.individuals.foreach{ i => //For each individual i
      val ai= current.a(i)
      pb.activities.filterNot(_.equals(ai)).foreach{ a => //For each other activity a
        if (!current.isFull(a)){//If a is not full
        val neighbor= current
          neighbor.a+=(i -> a)// i moves to a
          //Build the groups
          neighbor.g+=(i-> (current.p(a) + i))
          current.p(a).foreach( j => neighbor.g+=(j -> (current.p(a) + i)))
          current.p(ai).foreach( j => neighbor.g+=(j -> (current.p(ai) - i)))
          val w= rule match {
            case Utilitarian => neighbor.utilitarianWelfare()
            case Egalitarian => neighbor.egalitarianWelfare()
          }
          if (w> maxW){
            maxW=w
            bestMatching=neighbor
          }
        }else{//If a is full
          //Switch candidate
          current.p(a).foreach{ j=> // For each individual j assigned to a
            val neighbor= current.swap(i,j)// swap i and j
            val w= rule match {
              case Utilitarian => neighbor.utilitarianWelfare()
              case Egalitarian => neighbor.egalitarianWelfare()
            }
            if (w> maxW){
              maxW=w
              bestMatching=neighbor
            }
          }
        }
      }
      if (!ai.equals(Activity.VOID)){//if i is active
      val neighbor= current
        //Move i to inactive
        neighbor.a+=(i -> Activity.VOID)
        //Build the groupe
        neighbor.g+=(i -> Group(i))
        current.p(ai).filterNot(_.equals(i)).foreach( j => neighbor.g+=(j -> (current.p(ai) - i)))
        val w= rule match {
          case Utilitarian => neighbor.utilitarianWelfare()
          case Egalitarian => neighbor.egalitarianWelfare()
        }
        if (w> maxW){
          maxW=w
          bestMatching=neighbor
        }
      }
    }
    bestMatching
  }
}

