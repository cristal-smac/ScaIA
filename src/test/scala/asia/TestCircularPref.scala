package asia

import CircularPref._
import org.scaia.asia.{Activity, Coalition, Group, Matching}
import org.scalatest.FlatSpec

class TestCircularPref extends FlatSpec {

  val allMatchings= pb.allSoundMatchings()

  "The example with circular social preference" should "have no CS matching" in {
    assert(! allMatchings.exists(m => m.isCoreStable))
  }
  "The example with circular social preference" should "have no SCS matching" in {
    assert(! allMatchings.exists(m => m.isStrictCoreStable))
  }

  "The example with circular social preference" should "have no NS matching" in {
    assert(! allMatchings.exists(m => m.isNashStable))
  }

  val m1= new Matching(pb)
  m1.a= Map(i1 -> a, i2 -> a, i3 -> Activity.VOID)
  m1.g= Map(i1 -> Group(i1, i2), i2 -> Group(i1, i2), i3 -> Group(i3))

  "M1 with pM1(a)={i1,i2}" should "be weakly/strongly blocked by (a,i3)" in {
    assert( new Coalition(a,Group(i3)).weaklyBlock(m1))
    assert( new Coalition(a,Group(i3)).stronglyBlock(m1))

  }

  val m2= new Matching(pb)
  m2.a= Map(i1 -> Activity.VOID, i2 -> a, i3 -> a)
  m2.g= Map(i1 -> Group(i1), i2 -> Group(i2, i3), i3 -> Group(i2, i3))

  "M2 with pM2(a)={i2,i3}" should "be weakly/strongly blocked by (a,i1)" in {
    assert( new Coalition(a,Group(i1)).weaklyBlock(m2))
    assert( new Coalition(a,Group(i1)).stronglyBlock(m2))
  }

  val m3= new Matching(pb)
  m3.a= Map(i1 -> a, i2 -> Activity.VOID, i3 -> a)
  m3.g= Map(i1 -> Group(i1, i3), i2 -> Group(i2), i3 -> Group(i1, i3))

  "M3 with pM2(a)={i1,i3}" should "be weakly/strongly blocked by (a,i2)" in {
    assert( new Coalition(a,Group(i2)).weaklyBlock(m3))
    assert( new Coalition(a,Group(i2)).stronglyBlock(m3))
  }

  "M1/M2/M2" should "not be individual rational" in {
    assert(! m1.isIndividuallyRational())
    assert(! m2.isIndividuallyRational())
    assert(! m3.isIndividuallyRational())

  }

  /*
    println("Matching #1\n"+m1)
    println("M1 is strict core stable (false) ? "+m1.isStrictCoreStable())
    println("M1 is core stable (false) ? "+m1.isCoreStable())
    println("M1 is individually stable (false) ? "+m1.isIndividuallyStable())
    println("M1 is contractually individually stable (true) ? "+m1.isContractuallyIndividuallyStable())
    println("M1 is Nash stable (false) ? "+m1.isNashStable())
    println("M1 is Pareto-optimal (true) ? "+m1.isParetoOptimum())
    println("Utilitarian welfare of M1 "+m1.utilitarianWelfare())
    println("Egalitarian welfare of M1 "+m1.egalitarianWelfare())
    val c2= new Coalition(Activity.VOID,new Group(p2))
    println("(theta,p2) weakly blocks M1 (true) ? "+c2.weaklyBlock(m1))

    println("Matching #2\n"+m2)
    println("M2 is is strict core stable (false) ? "+m2.isStrictCoreStable())
    println("M2 is is core stable (false) ? "+m2.isCoreStable())
    println("M2 is individually stable (true) ? "+m2.isIndividuallyStable())
    println("M2 is contractually individually stable (true) ? "+m2.isContractuallyIndividuallyStable())
    println("M2 is Nash stable (false) ? "+m2.isNashStable())
    println("M2 is Pareto-optimal (true)? "+m2.isParetoOptimum())
    println("Utilitarian welfare of M2 "+m2.utilitarianWelfare())
    println("Egalitarian welfare of M2 "+m2.egalitarianWelfare())
    val c3= new Coalition(Activity.VOID,new Group(p3))
    println("(theta,p3) weakly blocks M2 (true) ? "+c3.weaklyBlock(m2))



    println("Matching #3\n"+m3)
    println("M3 is strict core stable (false) ? "+m3.isStrictCoreStable())
    println("M3 is core stable (false) ? "+m3.isCoreStable())
    println("M3 is individually stable (true) ? "+m3.isIndividuallyStable())
    println("M3 is contractually individually stable (true) ? "+m3.isContractuallyIndividuallyStable())
    println("M3 is Nash stable (false) ? "+m3.isNashStable())
    println("M3 is Pareto-optimal (true) ? "+m3.isParetoOptimum())
    println("Utilitarian welfare of M3 "+m3.utilitarianWelfare())
    println("Egalitarian welfare of M3 "+m3.egalitarianWelfare())

    val c4= new Coalition(Activity.VOID,new Group(i1))
    println("(theta,p4) weakly blocks M3 (true) ? "+c4.weaklyBlock(m3))
  */

}