// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia._

/**
  *  Inclusive heuristic which returns a "good" matching
  *  @param pb to solved
  *  @param rule to apply (maximize the utilitarian/egalitarian/nash welfare
  *  TODO improve trace of the other solvers
  */
class InclusiveSolver(pb : IAProblem, rule: SocialRule) extends ASIASolver(pb){
  override def solve() : Matching = {
    val result = new Matching(pb)
    var free = pb.individuals// Initially all the individuals are free
    var concessions=  Map[Individual,List[Activity]]()
    //Build the concession list of attractive activities for each individual
    pb.individuals.foreach{ i =>
      concessions+= (i-> i.concessions(pb.activities))
    }
    while (! free.isEmpty){
      free.foreach { i: Individual =>
        if (debug) println("\n"+result)//Current matching
        if (concessions(i).isEmpty) {
          if (debug) println(i + " can no more concede and so inactive")
          result.a += (i -> Activity.VOID)
          result.g += (i -> new Group(i))
          free -= i
        } else {
          val a = concessions(i).head
          val g = result.p(a)
          val ng = g + i
          if (debug) println(i.name + " considers " + a.name + " with " + g)
          if (!result.isFull(a)) {
            if (debug) println(s"Since ${a.name} is not full ${i.name} is assigned to ${a.name}")
            result.a += (i -> a)
            result.g += (i -> new Group(i))
            free -= i
          } else {
            if (debug) println(s"${a.name} considers ${i.name} with ${result.p(a)}")
            var umax = Double.MinValue
            var bG = new Group()
            var subgroups = Set[Group]()
            ng.foreach { j =>
              val s = ng.filterNot(_.equals(j))
              subgroups += s
            }
            subgroups.foreach { sg =>
              val u= rule match {
                case Utilitarian => {
                  val u=sg.usum(a.name)
                  if (debug) println(f"$sg%s.uusum(${a.name}%s)=$u%2.3f")
                  u
                }
                case Egalitarian => {
                  val u=sg.umin(a.name)
                  if (debug) println(f"$sg%s.umin(${a.name}%s)=$u%2.3f")
                  u
                }
              }
              if (umax < u) {
                umax = u
                bG = sg
              }
            }
            if (debug) println(s"Since <$a,$bG> is the best coalition")
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
    if (debug) println(result)
    result
  }
}