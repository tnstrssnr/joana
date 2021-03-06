/**
 * This file is part of the Joana IFC project. It is developed at the
 * Programming Paradigms Group of the Karlsruhe Institute of Technology.
 *
 * For further details on licensing please read the information at
 * http://joana.ipd.kit.edu or contact the authors.
 */
package edu.kit.joana.api.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.IFCType;
import edu.kit.joana.api.lattice.LowHighLattice;
import edu.kit.joana.api.test.util.ApiTestException;
import edu.kit.joana.api.test.util.BuildSDG;
import edu.kit.joana.api.test.util.DumpTestSDG;
import edu.kit.joana.ifc.sdg.core.SecurityNode;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;
import edu.kit.joana.ifc.sdg.lattice.IStaticLattice;
import edu.kit.joana.ifc.sdg.lattice.LatticeUtil;
import edu.kit.joana.ifc.sdg.lattice.WrongLatticeDefinitionException;

/**
 * @author Martin Hecker <martin.hecker@kit.edu>
 */
public class XLSODTests {

	static final boolean outputPDGFiles = false;
	static final boolean outputGraphMLFiles = false;
	
	private static IFCAnalysis buildAnnotateDump(Class<?> clazz, boolean timeSensitivity, IStaticLattice<String> l) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		IFCAnalysis ana = BuildSDG.buldAndUseJavaAnnotations(clazz, BuildSDG.top_concurrent, true, l);
		ana.setTimesensitivity(timeSensitivity);
	
		final String classname = clazz.getCanonicalName();
		
		if (outputPDGFiles) {
			DumpTestSDG.dumpSDG(ana.getProgram().getSDG(), classname + ".pdg");
		}
		if (outputGraphMLFiles) {
			DumpTestSDG.dumpGraphML(ana.getProgram().getSDG(), classname + ".pdg");
		}
		
		return ana;
	}

	private static void testSound(Class<?> clazz, IFCType ifcType) throws ClassHierarchyException, ApiTestException,
	IOException, UnsoundGraphException, CancelException {
		testSound(clazz, ifcType, LowHighLattice.INSTANCE);
	}

	private static void testSound(Class<?> clazz, IFCType ifcType, boolean timeSensitivity) throws ClassHierarchyException, ApiTestException,
	IOException, UnsoundGraphException, CancelException {
		// There are leaks, and we're sound and hence report them
		testSound(clazz, ifcType, timeSensitivity, LowHighLattice.INSTANCE);
	}

	private static void testSound(Class<?> clazz, IFCType ifcType, IStaticLattice<String> l) throws ClassHierarchyException, ApiTestException,
	IOException, UnsoundGraphException, CancelException {
		assertTrue(ifcType == IFCType.timingiRLSOD || ifcType == IFCType.iRLSOD);
		testSound(clazz, ifcType, false, l);
	}

	private static void testSound(Class<?> clazz, IFCType ifcType, boolean timeSensitivity, IStaticLattice<String> l) throws ClassHierarchyException, ApiTestException,
	IOException, UnsoundGraphException, CancelException {
		// There are leaks, and we're sound and hence report them
		IFCAnalysis ana = buildAnnotateDump(clazz, timeSensitivity, l);

		Collection<? extends IViolation<SecurityNode>> illegal = ana.doIFC(ifcType);
		assertFalse(illegal.isEmpty());
	}

	private static void testPrecise(Class<?> clazz, IFCType ifcType) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		testPrecise(clazz, ifcType, LowHighLattice.INSTANCE);
	}

	private static void testPrecise(Class<?> clazz, IFCType ifcType, boolean timeSensitivity) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		// There are no leak, and  we're precise enough to find out that there aren't
		testPrecise(clazz, ifcType, timeSensitivity, LowHighLattice.INSTANCE);
	}

	private static void testPrecise(Class<?> clazz, IFCType ifcType, IStaticLattice<String> l) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		assertTrue(ifcType == IFCType.timingiRLSOD || ifcType == IFCType.iRLSOD);
		testPrecise(clazz, ifcType, false, l);
	}

	private static void testPrecise(Class<?> clazz, IFCType ifcType, boolean timeSensitivity, IStaticLattice<String> l) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		// There are no leak, and  we're precise enough to find out that there aren't
		IFCAnalysis ana = buildAnnotateDump(clazz, timeSensitivity, l);

		Collection<? extends IViolation<SecurityNode>> illegal = ana.doIFC(ifcType);
		assertTrue(illegal.isEmpty());
	}

	private static void testTooImprecise(Class<?> clazz, IFCType ifcType) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		testTooImprecise(clazz, ifcType, LowHighLattice.INSTANCE);
	}

	private static void testTooImprecise(Class<?> clazz, IFCType ifcType, boolean timeSensitivity) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		testTooImprecise(clazz, ifcType, timeSensitivity, LowHighLattice.INSTANCE);
	}

	private static void testTooImprecise(Class<?> clazz, IFCType ifcType, IStaticLattice<String> l) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		assertTrue(ifcType == IFCType.timingiRLSOD || ifcType == IFCType.iRLSOD);
		testTooImprecise(clazz, ifcType, false, l);
	}

	private static void testTooImprecise(Class<?> clazz, IFCType ifcType, boolean timeSensitivity, IStaticLattice<String> l) throws ClassHierarchyException, ApiTestException,
			IOException, UnsoundGraphException, CancelException {
		testSound(clazz, ifcType, timeSensitivity);
	}


	@Test
	public void testDe_uni_trier_infsec_core_Setup() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       de.uni.trier.infsec.core.Setup.class, IFCType.LSOD,          false);
		testSound(       de.uni.trier.infsec.core.Setup.class, IFCType.RLSOD,         false);
		testSound(       de.uni.trier.infsec.core.Setup.class, IFCType.iRLSOD);
		testSound(       de.uni.trier.infsec.core.Setup.class, IFCType.timingiRLSOD);

		testSound(       de.uni.trier.infsec.core.Setup.class, IFCType.LSOD,          true);
		testSound(       de.uni.trier.infsec.core.Setup.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testDe_uni_trier_infsec_core_SetupNoLeak() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       de.uni.trier.infsec.core.SetupNoLeak.class, IFCType.LSOD,          false);
		testTooImprecise(de.uni.trier.infsec.core.SetupNoLeak.class, IFCType.RLSOD,         false);
		testPrecise(     de.uni.trier.infsec.core.SetupNoLeak.class, IFCType.iRLSOD);
		testPrecise(     de.uni.trier.infsec.core.SetupNoLeak.class, IFCType.timingiRLSOD);

		testSound(       de.uni.trier.infsec.core.SetupNoLeak.class, IFCType.LSOD,          true);
		testTooImprecise(de.uni.trier.infsec.core.SetupNoLeak.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testPossibilisticLeaks() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.PossibilisticLeaks.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.PossibilisticLeaks.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.PossibilisticLeaks.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.PossibilisticLeaks.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.PossibilisticLeaks.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.PossibilisticLeaks.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testPossibilisticLeaks2() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.PossibilisticLeaks2.class, IFCType.LSOD,          false);
		testSound(       joana.api.testdata.demo.PossibilisticLeaks2.class, IFCType.RLSOD,         false);
		testSound(       joana.api.testdata.demo.PossibilisticLeaks2.class, IFCType.iRLSOD);
		testSound(       joana.api.testdata.demo.PossibilisticLeaks2.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.PossibilisticLeaks2.class, IFCType.LSOD,          true);
		testSound(       joana.api.testdata.demo.PossibilisticLeaks2.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testProbabilisticOKDueToJoin() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testPrecise(     joana.api.testdata.demo.ProbabilisticOKDueToJoin.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.ProbabilisticOKDueToJoin.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.ProbabilisticOKDueToJoin.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.ProbabilisticOKDueToJoin.class, IFCType.timingiRLSOD);

		testPrecise(     joana.api.testdata.demo.ProbabilisticOKDueToJoin.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.ProbabilisticOKDueToJoin.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testProbabilisticLeaks() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.ProbabilisticLeaks.class, IFCType.LSOD,          false);
		testSound(       joana.api.testdata.demo.ProbabilisticLeaks.class, IFCType.RLSOD,         false);
		testSound(       joana.api.testdata.demo.ProbabilisticLeaks.class, IFCType.iRLSOD);
		testSound(       joana.api.testdata.demo.ProbabilisticLeaks.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.ProbabilisticLeaks.class, IFCType.LSOD,          true);
		testSound(       joana.api.testdata.demo.ProbabilisticLeaks.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testProbabilisticOK() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testTooImprecise(joana.api.testdata.demo.ProbabilisticOK.class, IFCType.LSOD,          false); // see comment in test data class
		testPrecise(     joana.api.testdata.demo.ProbabilisticOK.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.ProbabilisticOK.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.ProbabilisticOK.class, IFCType.timingiRLSOD);

		testTooImprecise(joana.api.testdata.demo.ProbabilisticOK.class, IFCType.LSOD,          true); // see comment in test data class
		testPrecise(     joana.api.testdata.demo.ProbabilisticOK.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testProbabilisticSmall() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testPrecise(     joana.api.testdata.demo.Prob_Small.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.Prob_Small.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.Prob_Small.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.Prob_Small.class, IFCType.timingiRLSOD);
		
		testPrecise(     joana.api.testdata.demo.Prob_Small.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.Prob_Small.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testFig2_1() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.Fig2_1.class, IFCType.LSOD,          false);
		testSound(       joana.api.testdata.demo.Fig2_1.class, IFCType.RLSOD,         false);
		testSound(       joana.api.testdata.demo.Fig2_1.class, IFCType.iRLSOD);
		testSound(       joana.api.testdata.demo.Fig2_1.class, IFCType.timingiRLSOD);
		
		testSound(       joana.api.testdata.demo.Fig2_1.class, IFCType.LSOD,          true);
		testSound(       joana.api.testdata.demo.Fig2_1.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testContextSens() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ContextSens.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSens.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.xrlsod.ContextSens.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.ContextSens.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ContextSens.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSens.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testContextSensClassical() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ContextSensClassical.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSensClassical.class, IFCType.RLSOD,         false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSensClassical.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.ContextSensClassical.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ContextSensClassical.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSensClassical.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testContextSensTiming() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ContextSensTiming.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.xrlsod.ContextSensTiming.class, IFCType.RLSOD,         false);
		// both iRLSOD implementations are imprecise since they lack context-sensitivity
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSensTiming.class, IFCType.iRLSOD);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSensTiming.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ContextSensTiming.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.xrlsod.ContextSensTiming.class, IFCType.RLSOD,         true);
		// both iRLSOD implementations are imprecise since they lack context-sensitivity

	}
	
	@Test
	public void testContextSens2() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ContextSens2.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSens2.class, IFCType.RLSOD,         false);
		// both iRLSOD implementations are imprecise since they lack context-sensitivity
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSens2.class, IFCType.iRLSOD);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSens2.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ContextSens2.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ContextSens2.class, IFCType.RLSOD,         true);
		// both iRLSOD implementations are imprecise since they lack context-sensitivity

	}
	
	@Test
	public void testTimeSens() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.TimeSens.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.xrlsod.TimeSens.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.xrlsod.TimeSens.class, IFCType.iRLSOD);
		// timingiRLSOD implementation is imprecise -- see comment in test class
		testTooImprecise(joana.api.testdata.demo.xrlsod.TimeSens.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.TimeSens.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.xrlsod.TimeSens.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testFig2_2() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.Fig2_2.class, IFCType.LSOD,          false);
		testSound(       joana.api.testdata.demo.Fig2_2.class, IFCType.RLSOD,         false);
		testSound(       joana.api.testdata.demo.Fig2_2.class, IFCType.iRLSOD);
		testSound(       joana.api.testdata.demo.Fig2_2.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.Fig2_2.class, IFCType.LSOD,          true);
		testSound(       joana.api.testdata.demo.Fig2_2.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testFig2_3() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.Fig2_3.class, IFCType.LSOD,          false);
		testSound(       joana.api.testdata.demo.Fig2_3.class, IFCType.RLSOD,         false);
		testSound(       joana.api.testdata.demo.Fig2_3.class, IFCType.iRLSOD);
		testSound(       joana.api.testdata.demo.Fig2_3.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.Fig2_3.class, IFCType.LSOD,          true);
		testSound(       joana.api.testdata.demo.Fig2_3.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testFig3_1() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testPrecise(     joana.api.testdata.demo.Fig3_1.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.Fig3_1.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.Fig3_1.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.Fig3_1.class, IFCType.timingiRLSOD);

		testPrecise(     joana.api.testdata.demo.Fig3_1.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.Fig3_1.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testFig3_2() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testPrecise(     joana.api.testdata.demo.Fig3_2.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.Fig3_2.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.Fig3_2.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.Fig3_2.class, IFCType.timingiRLSOD);

		testPrecise(     joana.api.testdata.demo.Fig3_2.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.Fig3_2.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testFig3_3() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.Fig3_3.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.Fig3_3.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.Fig3_3.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.Fig3_3.class, IFCType.timingiRLSOD);
	}
	
	@Test
	public void testLateSecretAccess() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.LateSecretAccess.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.xrlsod.LateSecretAccess.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.xrlsod.LateSecretAccess.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.LateSecretAccess.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.LateSecretAccess.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.xrlsod.LateSecretAccess.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testNoSecret() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.NoSecret.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.xrlsod.NoSecret.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.xrlsod.NoSecret.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.NoSecret.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.NoSecret.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.xrlsod.NoSecret.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testORLSOD1() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD1.class, IFCType.LSOD,          false);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD1.class, IFCType.RLSOD,         false);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD1.class, IFCType.iRLSOD);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD1.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD1.class, IFCType.LSOD,          true);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD1.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testORLSOD2() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD2.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD2.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSOD2.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSOD2.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD2.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD2.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testORLSOD3() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD3.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD3.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSOD3.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSOD3.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD3.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD3.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testORLSOD4() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD4.class, IFCType.LSOD,          false);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD4.class, IFCType.RLSOD,         false);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD4.class, IFCType.iRLSOD);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD4.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD4.class, IFCType.LSOD,          true);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD4.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testORLSOD5a() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD5a.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD5a.class, IFCType.RLSOD,         false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD5a.class, IFCType.iRLSOD);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD5a.class, IFCType.timingiRLSOD);

		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD5a.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD5a.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testORLSOD5b() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD5b.class, IFCType.LSOD,          false);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD5b.class, IFCType.RLSOD,         false);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD5b.class, IFCType.iRLSOD);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD5b.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD5b.class, IFCType.LSOD,          true);
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD5b.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testORLSOD5Secure() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD5Secure.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD5Secure.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSOD5Secure.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSOD5Secure.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.ORLSOD5Secure.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSOD5Secure.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testORLSODImprecise() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSODImprecise.class, IFCType.LSOD,          false);
		testPrecise     (joana.api.testdata.demo.xrlsod.ORLSODImprecise.class, IFCType.RLSOD,         false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.ORLSODImprecise.class, IFCType.iRLSOD);
		// This kind of "embarassing" regression (wrt. LSOD and RLSOD) is due classification of
		// H2 = H; as high, and the fact that the iRLSOD check does not differentiate between
		// the security level of the value read/written at some such a program point, and it's effect on
		// the "timing" it has on subsequent points
		
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSODImprecise.class, IFCType.timingiRLSOD);

		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSODImprecise.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.xrlsod.ORLSODImprecise.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testSwitchManyCases() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(     joana.api.testdata.demo.xrlsod.SwitchManyCases.class, IFCType.LSOD,          false);
		testSound(     joana.api.testdata.demo.xrlsod.SwitchManyCases.class, IFCType.RLSOD,         false);
		testSound(     joana.api.testdata.demo.xrlsod.SwitchManyCases.class, IFCType.iRLSOD);
		testSound(     joana.api.testdata.demo.xrlsod.SwitchManyCases.class, IFCType.timingiRLSOD);

		testSound(     joana.api.testdata.demo.xrlsod.SwitchManyCases.class, IFCType.LSOD,          true);
		testSound(     joana.api.testdata.demo.xrlsod.SwitchManyCases.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testTimingCascade() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(     joana.api.testdata.demo.xrlsod.TimingCascade.class, IFCType.LSOD,          false);
		testSound(     joana.api.testdata.demo.xrlsod.TimingCascade.class, IFCType.RLSOD,         false);
		testSound(     joana.api.testdata.demo.xrlsod.TimingCascade.class, IFCType.iRLSOD);
		testSound(     joana.api.testdata.demo.xrlsod.TimingCascade.class, IFCType.timingiRLSOD);

		testSound(     joana.api.testdata.demo.xrlsod.TimingCascade.class, IFCType.LSOD,          true);
		testSound(     joana.api.testdata.demo.xrlsod.TimingCascade.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testAlmostTimingCascade1() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.AlmostTimingCascade1.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.xrlsod.AlmostTimingCascade1.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.xrlsod.AlmostTimingCascade1.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.AlmostTimingCascade1.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.AlmostTimingCascade1.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.xrlsod.AlmostTimingCascade1.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testAlmostTimingCascade2() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.AlmostTimingCascade2.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade2.class, IFCType.RLSOD,         false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade2.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.AlmostTimingCascade2.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.AlmostTimingCascade2.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade2.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testAlmostTimingCascade3() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(       joana.api.testdata.demo.xrlsod.AlmostTimingCascade3.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade3.class, IFCType.RLSOD,         false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade3.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.AlmostTimingCascade3.class, IFCType.timingiRLSOD);
		
		testSound(       joana.api.testdata.demo.xrlsod.AlmostTimingCascade3.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade3.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void testAlmostTimingCascade4() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {

		testSound(       joana.api.testdata.demo.xrlsod.AlmostTimingCascade4.class, IFCType.LSOD,          false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade4.class, IFCType.RLSOD,         false);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade4.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.xrlsod.AlmostTimingCascade4.class, IFCType.timingiRLSOD);

		testSound(       joana.api.testdata.demo.xrlsod.AlmostTimingCascade4.class, IFCType.LSOD,          true);
		testTooImprecise(joana.api.testdata.demo.xrlsod.AlmostTimingCascade4.class, IFCType.RLSOD,         true);

	}
	
	@Test
	public void nullPointerExceptionImpossibleNoFlow() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testPrecise(     joana.api.testdata.demo.NullPointerExceptionImpossibleNoFlow.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.NullPointerExceptionImpossibleNoFlow.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.NullPointerExceptionImpossibleNoFlow.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.NullPointerExceptionImpossibleNoFlow.class, IFCType.timingiRLSOD);

		testPrecise(     joana.api.testdata.demo.NullPointerExceptionImpossibleNoFlow.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.NullPointerExceptionImpossibleNoFlow.class, IFCType.RLSOD,         true);
	}

	@Test
	public void testMHPSources() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testPrecise(     joana.api.testdata.demo.MHPSources.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.MHPSources.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.MHPSources.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.MHPSources.class, IFCType.timingiRLSOD);

		testPrecise(     joana.api.testdata.demo.MHPSources.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.MHPSources.class, IFCType.RLSOD,         true);
	}

	@Test
	public void testHighDataConflict() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testPrecise(     joana.api.testdata.demo.HighDataConflict.class, IFCType.LSOD,          false);
		testPrecise(     joana.api.testdata.demo.HighDataConflict.class, IFCType.RLSOD,         false);
		testPrecise(     joana.api.testdata.demo.HighDataConflict.class, IFCType.iRLSOD);
		testPrecise(     joana.api.testdata.demo.HighDataConflict.class, IFCType.timingiRLSOD);

		testPrecise(     joana.api.testdata.demo.HighDataConflict.class, IFCType.LSOD,          true);
		testPrecise(     joana.api.testdata.demo.HighDataConflict.class, IFCType.RLSOD,         true);
	}

	@Test
	public void testConflictInCalledMethod1() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod1.class, IFCType.LSOD,          false);
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod1.class, IFCType.RLSOD,         false);
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod1.class, IFCType.iRLSOD);
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod1.class, IFCType.timingiRLSOD);

		testSound(     joana.api.testdata.demo.ConflictInCalledMethod1.class, IFCType.LSOD,          true);
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod1.class, IFCType.RLSOD,         true);
	}

	@Test
	public void testConflictInCalledMethod2() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod2.class, IFCType.LSOD,          false);
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod2.class, IFCType.RLSOD,         false);
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod2.class, IFCType.iRLSOD);
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod2.class, IFCType.timingiRLSOD);

		testSound(     joana.api.testdata.demo.ConflictInCalledMethod2.class, IFCType.LSOD,          true);
		testSound(     joana.api.testdata.demo.ConflictInCalledMethod2.class, IFCType.RLSOD,         true);
	}

	@Test
	public void testThreadStartNoDependencyBug() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException {
		testSound(     joana.api.testdata.demo.ThreadStartNoDependencyBug.class, IFCType.LSOD,          false);
		testSound(     joana.api.testdata.demo.ThreadStartNoDependencyBug.class, IFCType.RLSOD,         false);
		testSound(     joana.api.testdata.demo.ThreadStartNoDependencyBug.class, IFCType.iRLSOD);
		testSound(     joana.api.testdata.demo.ThreadStartNoDependencyBug.class, IFCType.timingiRLSOD);

		testSound(     joana.api.testdata.demo.ThreadStartNoDependencyBug.class, IFCType.LSOD,          true);
		testSound(     joana.api.testdata.demo.ThreadStartNoDependencyBug.class, IFCType.RLSOD,         true);
	}

	@Test
	public void testConflictOtherLattice() throws ClassHierarchyException, ApiTestException, IOException,
			UnsoundGraphException, CancelException, WrongLatticeDefinitionException {
		IStaticLattice<String> l = LatticeUtil.compileBitsetLattice(
			LatticeUtil.loadLattice(
				"L <= A\n" +
				"L <= B\n" +
				"A <= C\n" +
				"B <= C\n" +
				"C <= H\n"
			)
		);

		testSound(joana.api.testdata.demo.ConflictOtherLattice.class, IFCType.LSOD,          false, l);
		testSound(joana.api.testdata.demo.ConflictOtherLattice.class, IFCType.RLSOD,         false, l);
		testSound(joana.api.testdata.demo.ConflictOtherLattice.class, IFCType.iRLSOD, l);
		testSound(joana.api.testdata.demo.ConflictOtherLattice.class, IFCType.timingiRLSOD, l);

		testSound(joana.api.testdata.demo.ConflictOtherLattice.class, IFCType.LSOD,          true, l);
		testSound(joana.api.testdata.demo.ConflictOtherLattice.class, IFCType.RLSOD,         true, l);
	}
}
