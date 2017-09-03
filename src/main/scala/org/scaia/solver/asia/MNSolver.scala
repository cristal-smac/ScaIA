// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia._

/**
  *  Heuristics which returns a "good" matching
  *  @param pb to solved
  *  @param restricted true if only subgroups of size -1 are investigated
  *  @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  */
class MNSolver(pb : IAProblem, restricted: Boolean, rule: SocialRule) extends ASIASolver(pb){
  override def solve() = {
    val result = new Matching(pb)
    var free = pb.individuals// Initially all the individuals are free
    var concessions=  Map[Individual,List[Activity]]()
    //Build the concession list of attractive activities for each individual
    pb.individuals.foreach{ i =>
      concessions+= (i-> i.concessions(pb.activities))
    }
    while (! free.isEmpty){
      free.foreach { i: Individual =>
        if (debug) println("Current matching: " + result)
        if (concessions(i).isEmpty) {
          if (debug) println(i + " can no more concede and so inactive")
          result.a += (i -> Activity.VOID)
          result.g += (i -> new Group(i))
          free -= i
        } else {
          val a = concessions(i).head
          val g = result.p(a)
          val ng = g + i
          if (debug) println(i.name + " considers " + a.name + " with group " + g)
          if (g.isEmpty) {
            if (debug) println("Since the current group of " + a.name + " is empty the individual is assigned to the activity " + a.name)
            result.a += (i -> a)
            result.g += (i -> new Group(i))
            free -= i
          } else {
            if (debug) println(i.name + " is considered by the current group of " + a.name + " :" + result.p(a))
            var umax = Double.MinValue
            var bG = new Group()
            var subgroups = Set[Group]()
            if (restricted) {
              //We only consider subgroup of size |g|
              ng.foreach { j =>
                val s = ng.filterNot(_.equals(j))
                subgroups += s
              }
              subgroups+=ng
            } else subgroups = ng.subgroups().filterNot(o => o.equals(Group()))
            if (a.c > g.size) {
              if (debug) println("The quota of " + a.name + " is not reached")
            }
            else {
              if (debug) println("The quota of " + a.name + " is reached")
              subgroups -= ng //we consider also the increasing of
            }
            subgroups.foreach { g2 =>
              val u= rule match {
                case Utilitarian => g2.u(a.name)
                case Egalitarian => g2.umin(a.name)

              }
              if (debug) println("The utility of the subgroup " + g2 + " is " + u)
              if (umax < u || (umax == u && (g2.size> bG.size)) ) {
                umax = u
                bG = g2
              }
            }
            if (debug) println(bG + " is the best subgroup of " + ng)
            bG.foreach(j => result.g += (j -> bG))
            (g diff bG).foreach { j =>
              if (debug) println(j + " is disassigned from " + a.name)
              result.a += (j -> Activity.VOID)
              result.g += (j -> new Group(j))
              free += j // i is busy
              concessions += (j -> concessions(j).tail)
            }
            if (bG.contains(i)) {
              if (debug) println(i + " is assigned to " + a)
              result.a += (i -> a)
              free -= i
            }else{
              if (debug) println(i + " is reject so concedes")
              concessions += (i -> concessions(i).tail)
            }
          }
        }
      }
    }
    result
  }
}
