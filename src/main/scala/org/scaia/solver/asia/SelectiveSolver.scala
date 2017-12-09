// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia._

/**
  *  Heuristics which returns a "good" matching
  *  @param pb to solved
  *  @param approximation true if only subgroups of size -1 are investigated
  *  @param rule to apply (i.e. maximize the utilitarian/egalitarian welfare)
  */
class SelectiveSolver(pb : IAProblem, approximation: Boolean, rule: SocialRule) extends ASIASolver(pb){
  override def solve() = {
    val result = new Matching(pb)
    var free = pb.individuals// Initially all the individuals are free
    var concessions=  Map[Individual,List[Activity]]()
    //Build the concession lists of attractive activities for each individual
    pb.individuals.foreach{ i =>
      concessions+= (i-> i.concessions(pb.activities))
    }
    while (! free.isEmpty){ //While the individuals are not all assigned
      free.foreach { i: Individual => // For each free individual
        if (debug) println("\n" + result)
        if (concessions(i).isEmpty){// Either he is deseperated
          if (debug) println(i + " can no more concede and so definitively inactive")
          result.a += (i -> Activity.VOID)
          result.g += (i -> new Group(i))
          free -= i
        } else { // Otherwise he proposes himself to the preferred activity
          val a = concessions(i).head
          val g = result.p(a)
          val ng = g + i
          if (g.isEmpty){ // Either the coalition is empty
            if (debug) println(s"Since ${a.name} is empty, ${i.name} is assigned to ${a.name}")
            result.a += (i -> a)
            result.g += (i -> new Group(i))
            free -= i
          } else { // Or the coalition is non-empty
            if (debug) println(s"${i.name} considers ${a.name} with ${result.p(a)}")
            var umax = Double.MinValue
            var bG = new Group()
            var subgroups = Set[Group]()
            if (approximation) { // Either at most a single individual can be excluded
              ng.foreach { j =>
                val s = ng.filterNot(_.equals(j))
                subgroups += s
              }
              subgroups+=ng
            } else { // Or all the potential groups are considered
              subgroups = ng.subgroups().filterNot(o => o.equals(Group()))
            }
            if (a.c <= g.size) { // If the capacity is reached the coalition cannot increase
              subgroups -= ng
            }
            subgroups.foreach { sg =>
              val u= rule match { // Social rule for the coalition
                case Utilitarian => {
                  val u=sg.usum(a.name)
                  if (debug) println(f"$sg%s.usum(${a.name}%s)=$u%2.3f")
                  u
                }
                case Egalitarian => {
                  val u=sg.umin(a.name)
                  if (debug) println(f"$sg%s.umin(${a.name}%s)=$u%2.3f")
                  u
                }
              }
              if (umax < u || (umax == u && (sg.size>bG.size))){// The best and largest group is selected
                umax = u
                bG = sg
              }
            }
            if (debug) println(s"Since <$a,$bG> is the best coalition, ")
            bG.foreach(j => result.g += (j -> bG))
            (g diff bG).foreach { j => // Some individuals can be excluded
              if (debug) println(s"${j.name} is disassigned from ${a.name}")
              result.a += (j -> Activity.VOID)
              result.g += (j -> new Group(j))
              free += j // i is busy
              concessions += (j -> concessions(j).tail)// and concede
            }
            if (bG.contains(i)) { // The individual i can be integrated
              if (debug) println(s"${i.name} is assigned to ${a.name}")
              result.a += (i -> a)
              free -= i
            }else{ // or rejected
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
