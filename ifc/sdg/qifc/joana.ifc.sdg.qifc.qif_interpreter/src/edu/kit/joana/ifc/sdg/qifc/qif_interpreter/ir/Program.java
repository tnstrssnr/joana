package edu.kit.joana.ifc.sdg.qifc.qif_interpreter.ir;

import com.ibm.wala.ipa.callgraph.CallGraph;
import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.annotations.IFCAnnotation;
import edu.kit.joana.api.sdg.SDGCall;
import edu.kit.joana.api.sdg.SDGFormalParameter;
import edu.kit.joana.api.sdg.SDGMethod;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.ifc.sdg.graph.SDG;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;
import edu.kit.joana.ui.annotations.Level;
import edu.kit.joana.wala.core.SDGBuilder;

import java.util.Collection;
import java.util.Optional;

public class Program {

	private final SDGProgram sdgProg;
	private final SDG sdg;
	private final String className;
	private final SDGBuilder builder;
	private final CallGraph cg;
	private final IFCAnalysis ana;
	private final Method entryMethod;


	public Program(SDGProgram sdgProg, SDG sdg, String className, SDGBuilder builder, CallGraph cg, IFCAnalysis ana) {
		this.sdgProg = sdgProg;
		this.sdg = sdg;
		this.className = className;
		this.builder = builder;
		this.cg = cg;
		this.ana = ana;
		this.entryMethod = Method.getEntryMethodFromProgram(this);
	}

	/**
	 * Only our entrypoint method should be called from main (and the default contructor). So we can use this function
	 * to find our entrypoint
	 * @param m a method
	 * @return true iff the method m is called in the main method
	 */
	public boolean isCalledFromMain(JavaMethodSignature m) {
		Collection<SDGCall> callsToMethod = sdgProg.getCallsToMethod(m);
		return callsToMethod.stream().anyMatch(c -> c.getOwningMethod().getSignature().getMethodName().equals("main"));
	}

	public String getLevelForParam(SDGMethod method, int param) {
		SDGFormalParameter paramNode = method.getParameters().stream().filter(p -> p.getIndex() == param).findAny().get();
		Optional<IFCAnnotation> annotation = this.ana.getSources().stream().filter(a -> a.getProgramPart().equals(paramNode)).findAny();

		if (annotation.isPresent()) {
			return annotation.get().getLevel1();
		}

		// default for un-annotated parameters
		return Level.HIGH;
	}

	// ----------------------------- getters + setters ----------------------------------------------

	public CallGraph getCg() {
		return cg;
	}

	public SDGBuilder getBuilder() {
		return builder;
	}

	public SDGProgram getSdgProg() {
		return sdgProg;
	}

	public Method getEntryMethod() {
		return entryMethod;
	}

	public SDG getSdg() {
		return sdg;
	}

	public String getClassName() {
		return className;
	}
}
