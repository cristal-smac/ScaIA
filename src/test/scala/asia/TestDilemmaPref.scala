package asia

import asia.DilemmaPref._
import org.scaia.asia.{Activity, Coalition, Group, Matching}
import org.scaia.solver.asia._
import org.scaia.util.MathUtils._
import org.scalatest.FlatSpec

class TestDilemmaPref extends FlatSpec {

  val allMatchings= pb.allSoundMatchings()

  //Result for the MNSolver with the utilitarian rule (no approximation)
  val m1= new Matching(pb)
  m1.a= Map(blue -> club, cyan -> club, magenta -> Activity.VOID, red -> ball )
  m1.g= Map(blue -> Group(blue, cyan), cyan -> Group(blue, cyan), magenta-> Group(magenta), red -> Group(red))

  //Result for the InculsiveSolver with the egalitarian rule
  val m2= new Matching(pb)
  m2.a= Map(blue -> club, cyan -> club, magenta -> ball, red -> ball )
  m2.g= Map(blue -> Group(blue, cyan), cyan -> Group(blue, cyan), magenta-> Group(magenta, red), red -> Group(magenta, red))

  "MNSolver utilitarian" should "be club={blue,cyan} ball{magenta} void{red}" in {
    val solver = new MNSolver(pb, false, Utilitarian)
    //println("MNSolver with utilitarian (no approximation)")
    val result =solver.solve()
    //println(s"result: $m1")
    //println(s"result: $result")
    assert(result.equals(m1))
  }

  "Inclusive solver egalitarian" should "be club={blue,cyan} ball{magenta, red}" in {
    //println("InculsiveSolver with egalitarian")
    val solver = new InclusiveSolver(pb, Egalitarian)
    val result =solver.solve()
    //println(s"result: $result")
    assert(result.equals(m2))//result.equals(m2)
  }

  "M1" should "be Pareto-optimal" in {
    //println("Number of Pareto-optimal: "+pb.allSoundMatchings().size)
    assert(m1.isParetoOptimal())
  }

  "M1" should "be MaxUtilitarian" in {
    val maxUtilitarianMatchings=pb.allMaxUtilitarian()
    //println(s"Utilitarian matchings: $maxUtilitarianMatchings ")
    assert(maxUtilitarianMatchings.contains(m1))
  }

  "M2" should "be not MaxEgaliarian" in {
    val maxEgalitarianMatchings=pb.allMaxEgalitarian()
    //println(s"Egalitarian matchings: $maxEgalitarianMatchings")
    assert(! maxEgalitarianMatchings.contains(m2))
  }
}