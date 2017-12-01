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
            if (debug) println(s"Since ${a.name} is empty ${i.name} is assigned to ${a.name}")
            result.a += (i -> a)
            result.g += (i -> new Group(i))
            free -= i
          } else {
            if (debug) println(s"${a.name} considers ${i.name} with ${result.p(a)}")
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
              subgroups -= ng //we consider also the increasing of
            }
            subgroups.foreach { g2 =>
              val u= rule match {
                case Utilitarian => {
                  val u=g2.u(a.name)
                  if (debug) println(f"U(${a.name}%s,$g2%s)=$u%2.3f")
                  u
                }
                case Egalitarian => {
                  val u=g2.umin(a.name)
                  if (debug) println(f"E(${a.name}%s,$g2%s)=$u%2.3f")
                  u
                }
              }
              if (debug) println("The utility of the subgroup " + g2 + " is " + u)
              if (umax < u || (umax == u && (g2.size> bG.size)) ) {
                umax = u
                bG = g2
              }
            }
            if (debug) println(s"Since $bG is the best subgroup of $ng")
            bG.foreach(j => result.g += (j -> bG))
            (g diff bG).foreach { j =>
              if (debug) println(s"${j.name} is disassigned from ${a.name}")
              result.a += (j -> Activity.VOID)
              result.g += (j -> new Group(j))
              free += j // i is busy
              concessions += (j -> concessions(j).tail)
            }
            if (bG.contains(i)) {
              if (debug) println(s"${i.name} is assigned to ${a.name}")
              result.a += (i -> a)
              free -= i
            }else{
              if (debug) println(s"${i.name} is rejected so concedes")
              concessions += (i -> concessions(i).tail)
            }
          }
        }
      }
    }
    result
  }
}
