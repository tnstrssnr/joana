package tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.lattice.BuiltinLattices;
import edu.kit.joana.ifc.orlsod.ClassicCDomOracle;
import edu.kit.joana.ifc.orlsod.ORLSODChecker;
import edu.kit.joana.ifc.orlsod.PathBasedORLSODChecker;
import edu.kit.joana.ifc.orlsod.ProbInfComputer;
import edu.kit.joana.ifc.orlsod.ThreadModularCDomOracle;
import edu.kit.joana.ifc.orlsod.TimimgClassificationChecker;
import edu.kit.joana.ifc.sdg.graph.SDG;
import edu.kit.joana.ifc.sdg.graph.SDGEdge;
import edu.kit.joana.ifc.sdg.graph.SDGNode;
import edu.kit.joana.ifc.sdg.graph.SDGSerializer;
import edu.kit.joana.ifc.sdg.graph.slicer.graph.CFG;
import edu.kit.joana.ifc.sdg.graph.slicer.graph.threads.PreciseMHPAnalysis;
import edu.kit.joana.ifc.sdg.mhpoptimization.CSDGPreprocessor;
import edu.kit.joana.ifc.sdg.mhpoptimization.PruneInterferences;
import edu.kit.joana.ifc.sdg.util.BytecodeLocation;
import edu.kit.joana.ifc.sdg.util.sdg.ReducedCFGBuilder;
import edu.kit.joana.util.Util;

public class ORLSODExperimentTiming {

	@Test
	public void doORLSOD1() throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		doConfigTiming(new ORLSODExperiment.StandardTestConfig("example/bin", "Lorlsod/ORLSOD1", "orlsod1", 1, 2, 2));
	}

	@Test
	public void doORLSOD2() throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		doConfigTiming(new ORLSODExperiment.StandardTestConfig("example/bin", "Lorlsod/ORLSOD2", "orlsod2", 1, 2, 0));
	}

	@Test
	public void doORLSOD3() throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		doConfigTiming(new ORLSODExperiment.StandardTestConfig("example/bin", "Lorlsod/ORLSOD3", "orlsod3", 1, 2, 0));
	}
	@Test
	public void doNoSecret() throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		doConfigTiming(new ORLSODExperiment.StandardTestConfig("example/bin", "Lorlsod/NoSecret", "noSecret", 0, 2, 0));
	}

	@Test
	public void doLateSecretAccess() throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		doConfigTiming(new ORLSODExperiment.StandardTestConfig("example/bin", "Lorlsod/LateSecretAccess", "lateSecAccess", 1, 2, 0));
	}

	
	private static void doConfigTiming(ORLSODExperiment.TestConfig cfg) throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		SDG sdg = JoanaRunner.buildSDG(cfg.progDesc.classPath, cfg.progDesc.mainClass);
		CSDGPreprocessor.preprocessSDG(sdg);
		CFG redCFG = ReducedCFGBuilder.extractReducedCFG(sdg);
		Util.removeCallCallRetEdges(redCFG);
		DomExperiment.export(redCFG, DomExperiment.joanaGraphExporter(), cfg.outputFiles.dotFile);
		PreciseMHPAnalysis mhp = PreciseMHPAnalysis.analyze(sdg);
		PruneInterferences.pruneInterferences(sdg, mhp);
		PrintWriter pw = new PrintWriter(cfg.outputFiles.pdgFile);
		SDGSerializer.toPDGFormat(sdg, pw);
		pw.close();
		Map<SDGNode, String> srcAnn = new HashMap<SDGNode, String>();
		for (SDGNode src : cfg.srcSelector.select(sdg)) {
			srcAnn.put(src, BuiltinLattices.STD_SECLEVEL_HIGH);
			System.out.println(String.format("srcAnn(%s) = %s", src, BuiltinLattices.STD_SECLEVEL_HIGH));
		}
		Assert.assertEquals(cfg.expectedNoSources, srcAnn.keySet().size());
		Map<SDGNode, String> snkAnn = new HashMap<SDGNode, String>();
		for (SDGNode snk : cfg.snkSelector.select(sdg)) {
			snkAnn.put(snk, BuiltinLattices.STD_SECLEVEL_LOW);
			System.out.println(String.format("snkAnn(%s) = %s", snk, BuiltinLattices.STD_SECLEVEL_LOW));
		}
		Assert.assertEquals(cfg.expectedNoSinks, snkAnn.keySet().size());
		ThreadModularCDomOracle tmdo = new ThreadModularCDomOracle(sdg);
		ProbInfComputer probInf = new ProbInfComputer(sdg, tmdo);
		TimimgClassificationChecker<String> checker = new TimimgClassificationChecker<>(sdg, BuiltinLattices.getBinaryLattice(), srcAnn, snkAnn, probInf, mhp, tmdo);
		int noVios = checker.check();
		Assert.assertEquals(cfg.expectedNoViolations, noVios);
	}

	@Test
	public void testORLSOD5a() throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		doConfigTiming(new ORLSODExperiment.StandardTestConfig("example/bin", "Lorlsod/ORLSOD5a", "orlsod5a", 1, 2, 6));
	}

	@Test
	public void testORLSODSecure() throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		doConfigTiming(new ORLSODExperiment.StandardTestConfig("example/bin", "Lorlsod/ORLSOD5Secure", "orlsod5secure", 1, 2, 0));
	}

	
	@Test
	public void testPost_Fig2_3() throws ClassHierarchyException, IOException, UnsoundGraphException, CancelException {
		doConfigTiming(new ORLSODExperiment.StandardTestConfig("example/bin", "Lpost16/Fig2_3", "post_fig2_3", 1, 2, 2));
	}

}
