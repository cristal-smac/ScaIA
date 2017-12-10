package asia

import akka.actor.ActorSystem
import asia.DilemmaPref._
import org.scaia.asia.{Activity, Coalition, Group, Matching}
import org.scaia.solver.asia._
import org.scaia.util.MathUtils._
import org.scalatest.FlatSpec

class TestDilemmaPref extends FlatSpec {

  val debug=false
  val system = ActorSystem("IAProblemSolver") //The Actor system

  val allSoundMatchings= pb.allSoundMatchings()

  //Result for the SelectiveSolver with the utilitarian rule (no approximation)
  val m1= new Matching(pb)
  m1.a= Map(blue -> club, cyan -> club, magenta -> Activity.VOID, red -> ball )
  m1.g= Map(blue -> Group(blue, cyan), cyan -> Group(blue, cyan), magenta-> Group(magenta), red -> Group(red))

  val m1bis= new Matching(pb)
  m1bis.a= Map(blue -> club, cyan -> club, red -> Activity.VOID, magenta -> ball )
  m1bis.g= Map(blue -> Group(blue, cyan), cyan -> Group(blue, cyan), magenta-> Group(magenta), red -> Group(red))


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

  "M1 with club={blue,cyan} ball{magenta} void{red}" should "be not SC" in {
    assert(!m1.isCohesive())
  }

  "M2 with club={blue,cyan} ball{magenta, red}" should "be SC" in {
    assert(m2.isCohesive())
  }

  "magenta" should "strictly prefers stay alone doing nothing rather than being with red and ball" in {
    if (debug) println("uMagenta(MagentaVoid)= "+magenta.u(cMagentaVoid.group.names, cMagentaVoid.activity.name))
    if (debug) println("uMagenta(MagentaRedBall)= "+magenta.u(cMagentaRedBall.group.names, cMagentaRedBall.activity.name))
    assert(magenta.sprefC(cMagentaVoid,cMagentaRedBall))
  }

  "magenta" should "strictly strictly prefers club with red rather staying alone doing nothing" in {
    if (debug) println("uMagenta(MagentaVoid)= "+magenta.u(cMagentaVoid.group.names, cMagentaVoid.activity.name))
    if (debug) println("uMagenta(MagentaRedClub= "+magenta.u(cMagentaRedClub.group.names, cMagentaRedClub.activity.name))
    assert(magenta.sprefC(cMagentaRedClub, cMagentaVoid))
  }

  "red" should "strictly prefers stay alone doing nothing rather than being with magenta and ball" in {
    if (debug) println("uRed(RedVoid)= "+red.u(cRedVoid.group.names, cRedVoid.activity.name))
    if (debug) println("uRed(MagentaRedBall)= "+red.u(cMagentaRedBall.group.names, cMagentaRedBall.activity.name))
    assert(red.sprefC(cRedVoid, cMagentaRedBall))
  }
  
  "red" should "strictly strictly prefers club with magenta rather than staying alone doing nothing" in {
    if (debug) println("uRed(RedVoid)= "+red.u(cRedVoid.group.names, cRedVoid.activity.name))
    if (debug) println("uRed(MagentaRedClub)= "+red.u(cMagentaRedClub.group.names, cMagentaRedClub.activity.name))
    assert(red.sprefC(cMagentaRedClub,cRedVoid))
  }

  "SelectiveSolver utilitarian" should "be club={blue,cyan} ball={magenta} void={red}" in {
    val solver = new SelectiveSolver(pb, false, Utilitarian)
    //solver.debug=true
    val result =solver.solve()
    assert(result.equals(m1))
  }

  "DistributedSelectiveSolver utilitarian" should "be club={blue,cyan} ball={magenta} void={red}" in {
    val solver = new DistributedSelectiveSolver(pb, system, false, Utilitarian)
    //solver.debug=true
    val result =solver.solve()
    assert(result.equals(m1))
  }


  "SelectiveSolver egalitarian" should "be club={blue,cyan} ball={magenta} void={red}" in {
    val solver = new SelectiveSolver(pb, false, Egalitarian)
    //solver.debug=true
    val result =solver.solve()
    assert(result.equals(m1))
  }

  "Inclusive solver egalitarian" should "be club={blue,cyan} ball={magenta, red}" in {
    val solver = new InclusiveSolver(pb, Egalitarian)
    //solver.debug=true
    val result =solver.solve()
    assert(result.equals(m2))
  }

  "DistributedInclusive solver egalitarian" should "be club={blue,cyan} ball={magenta, red}" in {
    val solver = new DistributedInclusiveSolver(pb, system, true, Egalitarian)
    //solver.debug=true
    val result =solver.solve()
    assert(result.equals(m2))
  }


  "Inclusive solver utilitarian" should "be club={blue,cyan} ball{magenta, red}" in {
    val solver = new InclusiveSolver(pb, Utilitarian)
    //solver.debug=true
    val result =solver.solve()
    assert(result.equals(m2))
  }



  "M1" should "not be Perfect" in {
    assert(!m1.isPerfect())
  }


  "M1" should "be MaxUtilitarian" in {
    val maxUtilitarianMatchings=pb.allMaxUtilitarian()
    //if (debug) println(s"Utilitarian matchings: $maxUtilitarianMatchings ")
    assert(maxUtilitarianMatchings.contains(m1))
  }

  "M2" should "be not MaxEgaliarian" in {
    val maxEgalitarianMatchings=pb.allMaxEgalitarian()
    //if (debug) println(s"Egalitarian matchings: $maxEgalitarianMatchings")
    assert(! maxEgalitarianMatchings.contains(m2))
  }

  "M1" should "be not core stable" in {
    val matchings= allSoundMatchings.filter(m => m.isCoreStable())
    if (debug) println(s"Number of core stable matchings: ${matchings.size} ")
    assert(! m1.isCoreStable())// since club(2): magenta strongly blocks this matching
  }

  "The dilemma problem" should "have no Perfect sound matching" in {
    val matchings= allSoundMatchings.filter(m => m.isPerfect())
    if (debug) println(s"Number of Perfect sound matchings: ${matchings.size} ")
    assert(matchings.isEmpty)
  }

  "The dilemma problem" should "have no sound CS matching" in {
    val matchings= allSoundMatchings.filter(m => m.isCoreStable())
    if (debug) println(s"Number of CS sound matchings: ${matchings.size} ")
    assert(matchings.isEmpty)
  }

  "The dilemma problem" should "have no sound SCS matching" in {
    val matchings= allSoundMatchings.filter(m => m.isStrictCoreStable())
    if (debug) println(s"Number of SCS sound matchings: ${matchings.size} ")
    assert(matchings.isEmpty)
  }

  "The dilemma problem" should "have (7) NS sound matchings" in {
    val matchings= allSoundMatchings.filter(m => m.isNashStable())
    if (debug) println(s"Number of NS sound matchings: ${matchings.size} ")
    assert(matchings.size==7)
  }

  "The dilemma problem" should "have (9) IS sound matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isIndividuallyStable())
    if (debug) println(s"Number of IS sound matchings: ${matchings.size} ")
    assert(matchings.size==9)
  }
  
  "The dilemma problem" should "have (16) CIS sound matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isContractuallyIndividuallyStable())
    if (debug) println(s"Number of CIS sound matchings: ${matchings.size} ")
    assert(matchings.size==16)
  }

  "The dilemma problem" should "have (15) PO sound matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isParetoOptimal())
    if (debug) println(s"Number of PO sound matchings: ${matchings.size} ")
    assert(matchings.size==15)
  }

  "The dilemma problem" should "have (51) IR sound matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isIndividuallyRational())
    if (debug) println(s"Number of IR sound matchings: ${matchings.size} ")
    assert(matchings.size==51)
  }

  "The dilemma problem" should "have (63) sound matchings" in {
    val matchings=allSoundMatchings
    if (debug) println(s"Number of sound matchings: ${matchings.size} ")
    assert(matchings.size==63)
  }

  "The dilemma problem" should "have (2) MaxUtil sound matchings" in {
    val matchings=pb.allMaxUtilitarian()
    if (debug) println(s"Number of MaxUtil sound matchings: ${matchings.size} ")
    assert(matchings.size==2)
  }

  "The dilemma problem" should "have (2) MaxEgal sound matchings" in {
    val matchings=pb.allMaxEgalitarian()
    //if (debug) println(s"Egalitarian sound matchings: $matchings ")
    if (debug) println(s"Number of MaxEgal sound matchings: ${matchings.size} ")
    assert(matchings.size==2)
  }

  "The dilemma problem" should "have (6) social cohesive matchings" in {
    val matchings=allSoundMatchings.filter(m => m.isCohesive())
    if (debug) println(s"Number of SC sound matchings: ${matchings.size} ")
    assert(matchings.size==6)
  }

}