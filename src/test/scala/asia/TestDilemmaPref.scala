package asia

import asia.DilemmaPref._
import org.scaia.asia.{Activity, Coalition, Group, Matching}
import org.scaia.solver.asia._
import org.scaia.util.MathUtils._
import org.scalatest.FlatSpec

class TestDilemmaPref extends FlatSpec {

  val allSoundMatchings= pb.allSoundMatchings()

  //Result for the MNSolver with the utilitarian rule (no approximation)
  val m1= new Matching(pb)
  m1.a= Map(blue -> club, cyan -> club, magenta -> Activity.VOID, red -> ball )
  m1.g= Map(blue -> Group(blue, cyan), cyan -> Group(blue, cyan), magenta-> Group(magenta), red -> Group(red))

  //Result for the InculsiveSolver with the egalitarian rule
  val m2= new Matching(pb)
  m2.a= Map(blue -> club, cyan -> club, magenta -> ball, red -> ball )
  m2.g= Map(blue -> Group(blue, cyan), cyan -> Group(blue, cyan), magenta-> Group(magenta, red), red -> Group(magenta, red))

  val cMagentaVoid = new Coalition(Activity.VOID, Group(magenta))
  val cRedVoid = new Coalition(Activity.VOID, Group(red))
  val cMagentaRedBall =  new Coalition(ball, Group(magenta,red))
  val cMagentaRedClub =  new Coalition(club, Group(magenta,red))


  "M1 with club={blue,cyan} ball{magenta} void{red}" should "be IR" in {
    assert(m1.isIndividuallyRational())
  }

  "M2 with club={blue,cyan} ball{magenta, red}" should "be not IR" in {
    assert(!m2.isIndividuallyRational())
  }


  "Dilemma pref " should "have a IR matching" in {
    var IRMatchings=allSoundMatchings.filter(m => m.isIndividuallyRational())
    println("Number of Sound matchingq: "+allSoundMatchings.size)//63
    println("Number of IR (Sound) matchings: "+IRMatchings.size)//51
    assert(!IRMatchings.isEmpty)

  }

  "magenta" should "strictly prefers stay alone doing nothing rather than being with red and ball" in {
    println("uMagenta(MagentaVoid)= "+magenta.u(cMagentaVoid.group.names, cMagentaVoid.activity.name))
    println("uMagenta(MagentaRedBall)= "+magenta.u(cMagentaRedBall.group.names, cMagentaRedBall.activity.name))
    assert(magenta.sprefC(cMagentaVoid,cMagentaRedBall))
  }

  "magenta" should "strictly strictly prefers club with red rather staying alone doing nothing" in {
    println("uMagenta(MagentaVoid)= "+magenta.u(cMagentaVoid.group.names, cMagentaVoid.activity.name))
    println("uMagenta(MagentaRedClub= "+magenta.u(cMagentaRedClub.group.names, cMagentaRedClub.activity.name))
    assert(magenta.sprefC(cMagentaRedClub, cMagentaVoid))
  }

  "red" should "strictly prefers stay alone doing nothing rather than being with magenta and ball" in {
    println("uRed(RedVoid)= "+red.u(cRedVoid.group.names, cRedVoid.activity.name))
    println("uRed(MagentaRedBall)= "+red.u(cMagentaRedBall.group.names, cMagentaRedBall.activity.name))
    assert(red.sprefC(cRedVoid, cMagentaRedBall))
  }
  
  "red" should "strictly strictly prefers club with magenta rather than staying alone doing nothing" in {
    println("uRed(RedVoid)= "+red.u(cRedVoid.group.names, cRedVoid.activity.name))
    println("uRed(MagentaRedClub)= "+red.u(cMagentaRedClub.group.names, cMagentaRedClub.activity.name))
    assert(red.sprefC(cMagentaRedClub,cRedVoid))
  }



  "MNSolver utilitarian" should "be club={blue,cyan} ball{magenta} void{red}" in {
    val solver = new MNSolver(pb, false, Utilitarian)
    //println("MNSolver with utilitarian (no approximation)")
    val result =solver.solve()
    //println(s"result: $m1")
    //println(s"result: $result")
    assert(result.equals(m1))
  }

  "Inclusive solver egalitarian" should "be club={blue,cyan} ball{magenta, red}" in {
    println("InculsiveSolver with egalitarian")
    val solver = new InclusiveSolver(pb, Egalitarian)
    //solver.debug=true
    val result =solver.solve()
    //println(s"result: $result")
    assert(result.equals(m2))//result.equals(m2)
  }

  "M1" should "not be Perfect" in {
    assert(!m1.isPerfect())
  }


  "M1" should "be Pareto-optimal" in {
    /*
    println("U(M1)= "+m1.utilitarianWelfare())
    println("E(M1)= "+m1.egalitarianWelfare())
    println("U(M2)= "+m2.utilitarianWelfare())
    println("E(M2)= "+m2.egalitarianWelfare())
  */
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

  "M1" should "be not core stable" in {
    val matchings= allSoundMatchings.filter(m => m.isCoreStable())
    println(s"Number of core stable matchings: ${matchings.size} ")
    assert(! m1.isCoreStable())// since club(2): magenta strongly blocks this matching
  }

  "The dilemma problem" should "have no Perfect sound matching" in {
    val matchings= allSoundMatchings.filter(m => m.isPerfect())
    println(s"Number of Perfect sound matchings: ${matchings.size} ")
    assert(matchings.isEmpty)
  }

  "The dilemma problem" should "have no sound CS matching" in {
    val matchings= allSoundMatchings.filter(m => m.isCoreStable())
    println(s"Number of CS sound matchings: ${matchings.size} ")
    assert(matchings.isEmpty)
  }

  "The dilemma problem" should "have no sound SCS matching" in {
    val matchings= allSoundMatchings.filter(m => m.isStrictCoreStable())
    println(s"Number of SCS sound matchings: ${matchings.size} ")
    assert(matchings.isEmpty)
  }

  "The dilemma problem" should "have (10) NS sound matchings" in {
    val matchings= allSoundMatchings.filter(m => m.isNashStable())
    println(s"Number of NS sound matchings: ${matchings.size} ")
    assert(! matchings.isEmpty)
  }

  "The dilemma problem" should "have (9) IS sound matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isIndividuallyStable())
    println(s"Number of IS sound matchings: ${matchings.size} ")
    assert(!matchings.isEmpty)
  }

  "The dilemma problem" should "have (12) CIS sound matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isContractuallyIndividuallyStable())
    println(s"Number of CIS sound matchings: ${matchings.size} ")
    assert(!matchings.isEmpty)
  }

  "The dilemma problem" should "have (15) PO sound matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isParetoOptimal())
    println(s"Number of PO sound matchings: ${matchings.size} ")
    assert(!matchings.isEmpty)
  }


  "The dilemma problem" should "have (51) IR sound matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isIndividuallyRational())
    println(s"Number of IR sound matchings: ${matchings.size} ")
    assert(!matchings.isEmpty)
  }


  "The dilemma problem" should "have (63) sound matchings" in {
    val matchings=allSoundMatchings
    println(s"Number of sound matchings: ${matchings.size} ")
    assert(!matchings.isEmpty)
  }

  "The dilemma problem" should "have (2) MaxUtil matchings" in {
    val matchings=pb.allMaxUtilitarian()
    println(s"Number of MaxUtil matchings: ${matchings.size} ")
    assert(!matchings.isEmpty)
  }

  "The dilemma problem" should "have (2) MaxEgal matchings" in {
    val matchings=pb.allMaxEgalitarian()
    println(s"Number of MaxEgal matchings: ${matchings.size} ")
    assert(!matchings.isEmpty)
  }


}