package asia

import UndesiredGuest._
import asia.UndesiredGuest.pb
import org.scaia.asia.{Activity, Coalition, Group, Matching}
import org.scaia.solver.asia.{Egalitarian, InclusiveSolver}
import org.scalatest.FlatSpec

class TestUndesiredGuest extends FlatSpec {

  val m1= new Matching(pb)
  m1.a= Map(i1 -> a, i2 -> a, i3 -> Activity.VOID)
  m1.g= Map(i1 -> Group(i1, i2), i2 -> Group(i1, i2), i3 -> Group(i3))

  "M1 with pM1(a)={i1,i2}" should "be SCS and so CS and IS" in {
    assert(m1.isStrictCoreStable())
    assert(m1.isCoreStable())
    assert(m1.isIndividuallyRational())
  }

  "M1 with pM1(a)={i1,i2}" should "be Pareto and so CSC and so CIS" in {
    assert(m1.isParetoOptimal())
    assert(m1.isContractuallyStrictCore())
    assert(m1.isContractuallyIndividuallyStable())
  }

  "M1 with pM1(a)={i1,i2}" should "be not NS" in {
    assert(!m1.isNashStable())
  }

  "The maximum egalitarian matching" should "be M1 with pM1(a)={i1,i2}" in {
    var u = -Double.MaxValue
    var maxEgalMatching = new Matching(pb)
    pb.allSoundMatchings().foreach { m =>
      if (m.egalitarianWelfare() >= u) {
        u = m.egalitarianWelfare()
        maxEgalMatching = m
      }
    }
    assert(Math.abs(m1.egalitarianWelfare() - maxEgalMatching.egalitarianWelfare())<= Math.ulp(1.0))
  }


  val m2= new Matching(pb)
  m2.a= Map(i1 -> Activity.VOID, i2 -> a, i3 -> a)

  "M2 with pM2(a)={i3}" should "be NS and so IS and so CIS" in {
    assert(m2.isNashStable())
    assert(m2.isIndividuallyStable())
    assert(m2.isContractuallyIndividuallyStable())
  }

  "M2 with pM2(a)={i3}" should "be not CS and so neither SCS" in {
    assert(! m2.isCoreStable())
    assert(! m2.isStrictCoreStable())
  }


  val m3= new Matching(pb)
  m3.a= Map(i1 -> a, i2 -> a, i3 -> a)
  m3.g= Map(i1 -> Group(i1, i2, i3), i2 -> Group(i1, i2, i3), i3 -> Group(i1, i2, i3))

  "M3 with pM3(a)={i1, i2, i3}" should "be Pareto-optimal and so CIS" in {
    assert(m3.isParetoOptimal())
    assert(m3.isContractuallyIndividuallyStable())
  }

  "M3 with pM3(a)={i1, i2, i3}" should "be not IR" in {
    assert(!m3.isIndividuallyRational())
  }

  "M3 with pM3(a)={i1, i2, i3}" should "be the outcome of the inclusive algorithm" in {
    assert(!m3.isIndividuallyRational())
  }

  "The InclusiveSolver with egaliatarian" should "return M3 with pM3(a)={i1, i2, i3}" in {
    val solver= new InclusiveSolver(pb, Egalitarian)
    val resultR = solver.solve()
    assert(m3.equals(resultR))
  }



}