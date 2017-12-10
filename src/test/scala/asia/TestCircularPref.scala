package asia

import CircularPref._
import org.scaia.asia.{Activity, Coalition, Group, Matching}
import org.scalatest.FlatSpec

class TestCircularPref extends FlatSpec {

  val allSoundMatchings= pb.allSoundMatchings()

  "The example with circular social preference" should "have no CS matching" in {
    assert(! allSoundMatchings.exists(m => m.isCoreStable))
  }

  "The example with circular social preference" should "have no SCS matching" in {
    assert(! allSoundMatchings.exists(m => m.isStrictCoreStable))
  }

  "The example with circular social preference" should "have no NS matching" in {
    val matchings = allSoundMatchings.filter(m => m.isNashStable)
   // println(s"Number of NS sound matchings: ${matchings.size} ")
    assert(! allSoundMatchings.exists(m => m.isNashStable))
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

}