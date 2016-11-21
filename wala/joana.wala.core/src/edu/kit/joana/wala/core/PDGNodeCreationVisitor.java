/**
 * This file is part of the Joana IFC project. It is developed at the
 * Programming Paradigms Group of the Karlsruhe Institute of Technology.
 *
 * For further details on licensing please read the information at
 * http://joana.ipd.kit.edu or contact the authors.
 */
package edu.kit.joana.wala.core;

import com.ibm.wala.classLoader.IField;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.IR.SSA2LocalMap;
import com.ibm.wala.ssa.SSAArrayLengthInstruction;
import com.ibm.wala.ssa.SSAArrayLoadInstruction;
import com.ibm.wala.ssa.SSAArrayStoreInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.ssa.SSAComparisonInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAConversionInstruction;
import com.ibm.wala.ssa.SSAGetCaughtExceptionInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction.IVisitor;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.ssa.SSALoadMetadataInstruction;
import com.ibm.wala.ssa.SSAMonitorInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SSAPhiInstruction;
import com.ibm.wala.ssa.SSAPiInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.ssa.SSASwitchInstruction;
import com.ibm.wala.ssa.SSAThrowInstruction;
import com.ibm.wala.ssa.SSAUnaryOpInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.analysis.typeInference.TypeInference;

import edu.kit.joana.wala.core.PDGNode.Kind;
import edu.kit.joana.wala.util.PrettyWalaNames;

public final class PDGNodeCreationVisitor implements IVisitor {

	private static final int MAX_CONST_STR = 25;

	private final PDG pdg;
	private final IClassHierarchy cha;
	private final IR ir;
	private final ParameterFieldFactory params;

	private final SymbolTable sym;

	private final boolean ignoreStaticFields;

	public PDGNode lastNode = null;

    private boolean showTypeNameInValue = false;
    private final boolean associateLocalNames;
    private TypeInference typeInf = null;       // Only needed for `showTypeNameInValue = true`

	public PDGNodeCreationVisitor(PDG pdg, IClassHierarchy cha, ParameterFieldFactory params,
			SymbolTable sym, boolean ignoreStaticFields, IR ir, boolean associateLocalNames) {
		this.pdg = pdg;
		this.params = params;
		this.cha = cha;
		this.ir = ir;
		this.sym = sym;
		this.ignoreStaticFields = ignoreStaticFields;
		this.associateLocalNames = associateLocalNames;
	}

    public static PDGNodeCreationVisitor makeWithTypeInf(PDG pdg, IClassHierarchy cha, ParameterFieldFactory params,
            SymbolTable sym, boolean ignoreStaticFields, boolean showTypeNameInValue, TypeInference typeInf, IR ir, boolean associateLocalNames) {
        PDGNodeCreationVisitor visitor = new PDGNodeCreationVisitor(pdg, cha, params, sym, ignoreStaticFields, ir, associateLocalNames);
        visitor.showTypeNameInValue = showTypeNameInValue;
        visitor.typeInf = typeInf;
        return visitor;
    }

	private String tmpName(int var) {
        String type = "";

		if (var < 0) {
			return "v?(" + var + ")";
		} else if (sym.isConstant(var)) {
			String cst = null;

			if (sym.isBooleanConstant(var)) {
				cst = (sym.isTrue(var) ? "true" : "false");
			} else if (sym.isDoubleConstant(var)) {
				cst = sym.getDoubleValue(var) + " d";
			} else if (sym.isFloatConstant(var)) {
				cst = sym.getFloatValue(var) + " f";
			} else if (sym.isIntegerConstant(var)) {
				cst = sym.getIntValue(var) + "";
			} else if (sym.isLongConstant(var)) {
				cst = sym.getLongValue(var) + " l";
			} else if (sym.isNullConstant(var)) {
				cst = "null";
			} else if (sym.isStringConstant(var)) {
				cst = sym.getStringValue(var).replace('"', '\'');
			} else {
				Object obj = sym.getConstantValue(var);
				cst = (obj == null ? "?" : obj.toString());
			}

			//sym.getConstantValue(var).toString();
			if (cst.length() > MAX_CONST_STR) {
				cst = cst.substring(0, MAX_CONST_STR - 4) + "...";
			}

			return "#(" + cst + ")";
		} else {
            if (var <= pdg.getMethod().getNumberOfParameters()) {
                if (this.showTypeNameInValue) {
                    // Append the type name to the variables name
                    assert (this.typeInf != null) : "You have to give a TypeInference in order to use showTypeNameInValue";
                    if (var > 0) {
                        if (this.typeInf.getType(var) != null) {
                            if (this.typeInf.getType(var).getTypeReference() != null) {
                                type = " <" + this.typeInf.getType(var).getTypeReference().getName().toString() + ">";
                            } else type = " <unknown>";
                        } else {
                            type = " <NoneType>";
                        }
                    }
                }

                if (pdg.getMethod().isStatic()) {
                    return "p" + var + type;
                } else if (var == 1){
                    return "this";
                } else {
                    return "p" + (var - 1) + type;
                }
            } else {
                try {
                    final int bcIndex = 0;  // TODO: Get the index
                    String name = pdg.getMethod().getLocalVariableName(bcIndex, var);
                    if ((name != null) && (! name.isEmpty())) {
                        return name + "_" + var;
                    }
                    return "v" + var + type;
                } catch (Exception e) {
                    //System.out.println(e.toString());
                    return "v" + var + type;
                }
            }
        }
	}

	@Override
	public void visitGoto(SSAGotoInstruction instruction) {
		lastNode = pdg.createNode("goto", Kind.NORMAL, PDGNode.DEFAULT_TYPE, PDGNode.DEFAULT_NO_LOCAL);
	}

	@Override
	public void visitArrayLoad(SSAArrayLoadInstruction instruction) {
		String label = tmpName(instruction.getDef()) + " = " + tmpName(instruction.getArrayRef())
			+ "[" + tmpName(instruction.getIndex()) + "]";

		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instruction).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instruction.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(label, Kind.HREAD, instruction.getElementType(), localDefNames);

		ParameterField field = params.getArrayField(instruction.getElementType());
		pdg.addFieldRead(field, lastNode);
	}

	@Override
	public void visitArrayStore(SSAArrayStoreInstruction instruction) {
		String label = tmpName(instruction.getArrayRef()) + "[" + tmpName(instruction.getIndex())
			+ "] = " + tmpName(instruction.getValue());

		lastNode = pdg.createNode(label, Kind.HWRITE, instruction.getElementType(), PDGNode.DEFAULT_NO_LOCAL);

		ParameterField field = params.getArrayField(instruction.getElementType());
		pdg.addFieldWrite(field, lastNode);
	}

	@Override
	public void visitBinaryOp(SSABinaryOpInstruction instr) {
		assert instr.getNumberOfUses() == 2;

		String label = tmpName(instr.getDef()) + " = " + tmpName(instr.getUse(0)) + " " +  PrettyWalaNames.op2str(instr.getOperator()) + " "
			+ tmpName(instr.getUse(1));
		// The description of IR.getLocalNames() suggests that it is enough to call 
		// ir.getLocalNames(instr.iindex, instr.getDef()).
		// In fact, though, in Bytecode after a binary op, the result has not been written to any variable yet.
		//
		// Example: for an Java Source Code line such as: "x = y + z", translated to: "v1 = binop(v2,v3, add)", 
		// after thi binop v1 is NOT YET associacted with with x.
		//
		// So what we do instead is to query v1's association at the end of the basic block.
		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(label, Kind.EXPRESSION, PDGNode.DEFAULT_TYPE, localDefNames);
	}

	@Override
	public void visitUnaryOp(SSAUnaryOpInstruction instr) {
		String label = tmpName(instr.getDef()) + " = " + PrettyWalaNames.op2str(instr.getOpcode()) + "("
			+ tmpName(instr.getUse(0)) + ")";

		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(label, Kind.EXPRESSION, PDGNode.DEFAULT_TYPE, localDefNames);
	}

	@Override
	public void visitConversion(SSAConversionInstruction instr) {
		String label = tmpName(instr.getDef()) + " = CONVERT " + instr.getFromType().getName() + " to "
			+ instr.getToType().getName() + " " + tmpName(instr.getUse(0));

		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(label, Kind.EXPRESSION, instr.getToType(), localDefNames);
	}

	@Override
	public void visitComparison(SSAComparisonInstruction instr) {
		assert instr.getNumberOfUses() == 2;

		final String label = tmpName(instr.getDef()) + " = " + tmpName(instr.getUse(0)) + " "
			+ PrettyWalaNames.op2str(instr.getOperator()) + " " + tmpName(instr.getUse(1));

		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(label, Kind.EXPRESSION, TypeReference.Boolean, localDefNames);
	}

	@Override
	public void visitConditionalBranch(SSAConditionalBranchInstruction instr) {
		String label = "if (" + tmpName(instr.getUse(0)) + " " + PrettyWalaNames.op2str(instr.getOperator())
			+ " " + tmpName(instr.getUse(1)) + ")";

		lastNode = pdg.createNode(label, Kind.PREDICATE, TypeReference.Boolean, PDGNode.DEFAULT_NO_LOCAL);
	}

	@Override
	public void visitSwitch(SSASwitchInstruction instruction) {
		String label = "switch " + tmpName(instruction.getUse(0));

		lastNode = pdg.createNode(label, Kind.PREDICATE, PDGNode.DEFAULT_NO_TYPE, PDGNode.DEFAULT_NO_LOCAL);
	}

	@Override
	public void visitReturn(SSAReturnInstruction instr) {
		String label = "return";

		if (!instr.returnsVoid()) {
			label += " " + tmpName(instr.getResult());
		}

		lastNode = pdg.createNode(label, Kind.NORMAL, pdg.exit.getTypeRef(), PDGNode.DEFAULT_NO_LOCAL);

		pdg.addReturn(lastNode);
	}

	@Override
	public void visitGet(SSAGetInstruction instr) {
		final int dest = instr.getDef();
		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		
		final FieldReference fRef = instr.getDeclaredField();
		final IField ifield = cha.resolveField(fRef);
		final TypeReference type = instr.getDeclaredFieldType();

		if (ifield != null && !(ignoreStaticFields && instr.isStatic())) {
			ParameterField field = params.getObjectField(ifield);

			String label = tmpName(dest) + " = ";
			if (instr.isStatic()) {
				label += PrettyWalaNames.simpleTypeName(fRef.getDeclaringClass()) + "." + fRef.getName();
			} else {
				label += tmpName(instr.getRef()) + "." + fRef.getName();
			}

			lastNode = pdg.createNode(label, Kind.HREAD, type, localDefNames);

			pdg.addFieldRead(field, lastNode);
		} else {
			/**
			 * As wala ignores some classes during the analysis
			 * (see jSDGExclusions.txt) there may be fields that cannot be
			 * resolved. So we have to ignore them in our analysis.
			 */
			String label = tmpName(dest) + " = " + (instr.isStatic() ? "" : tmpName(instr.getRef()) + ".")
				+ fRef.getName();

			lastNode = pdg.createNode(label, Kind.EXPRESSION, type, localDefNames);
		}
	}

	@Override
	public void visitPut(SSAPutInstruction instr) {
		final int val = instr.getVal();

		final FieldReference fRef = instr.getDeclaredField();
		final IField ifield = cha.resolveField(fRef);
		final TypeReference type = instr.getDeclaredFieldType();

		if (ifield != null && !(ignoreStaticFields && instr.isStatic())) {
			ParameterField field = params.getObjectField(ifield);

			String label = null ;
			if (instr.isStatic()) {
				label = PrettyWalaNames.simpleTypeName(fRef.getDeclaringClass()) + "." + fRef.getName();
			} else {
				label = tmpName(instr.getRef()) + "." + fRef.getName();
			}

			label += " = " + tmpName(val);
			// SSAPutInstruction does not define a value, so we do not record
			// the names of the corresponding (val's) local variables.
			// TODO: maybe change this?
			lastNode = pdg.createNode(label, Kind.HWRITE, type, PDGNode.DEFAULT_NO_LOCAL);

			pdg.addFieldWrite(field, lastNode);
		} else {
			/**
			 * As wala ignores some classes during the analysis
			 * (see jSDGExclusions.txt) there may be fields that cannot be
			 * resolved. So we have to ignore them in our analysis.
			 */
			String label = (instr.isStatic() ? "" : tmpName(instr.getRef()) + ".") + fRef.getName()
				+ " = " + tmpName(val);
			
			// SSAPutInstruction does not define a value, so we do not record
			// the names of the corresponding (val's) local variables.
			// TODO: maybe change this?
			lastNode = pdg.createNode(label, Kind.EXPRESSION, type, PDGNode.DEFAULT_NO_LOCAL);
		}
	}

	@Override
	public void visitInvoke(SSAInvokeInstruction instr) {
		final MethodReference tgt = instr.getDeclaredTarget();

		String label = (instr.isStatic() ? "" : tmpName(instr.getReceiver()) + "." )
			+ tgt.getSelector().getName().toString() + "()";

		if (instr.hasDef()) {
			label = tmpName(instr.getDef()) + " = " + label;
		}

		final TypeReference type = instr.getDeclaredResultType();
		lastNode = pdg.createNode(label, Kind.CALL, type, PDGNode.DEFAULT_NO_LOCAL);
		lastNode.setUnresolvedCallTarget(tgt.getSignature().toString());
		final PDGNode[] in = new PDGNode[instr.getNumberOfParameters()];

		final StringBuffer extLabel = new StringBuffer(label.substring(0, label.length() - 1));

		for (int i = 0; i < instr.getNumberOfParameters(); i++) {
			final int val = instr.getUse(i);
			final String valLabel = tmpName(val);

			if (instr.isStatic() || i > 0) {
				// do not append this pointer to normal param list
				extLabel.append(valLabel);
				if (i + 1 < instr.getNumberOfParameters()) {
					extLabel.append(", ");
				}
			}

			if (instr.isStatic()) {
				final TypeReference pType = instr.getDeclaredTarget().getParameterType(i);
				// parameter index 0 is reserved for the this pointer. static method params start at 1.
				PDGNode actIn = pdg.createNode("param " + (i + 1) + " [" + valLabel + "]", PDGNode.Kind.ACTUAL_IN, pType, PDGNode.DEFAULT_NO_LOCAL);
				in[i] = actIn;
			} else {
				final TypeReference tref;
				if (i == 0) {
					tref = instr.getDeclaredTarget().getDeclaringClass();
				} else {
					tref = instr.getDeclaredTarget().getParameterType(i - 1);
				}

				// parameter index 0 is reserved for the this pointer. static method params start at 1.
				PDGNode actIn = pdg.createNode((i == 0 ? "this" : "param " + i) + " [" + valLabel + "]", PDGNode.Kind.ACTUAL_IN, tref, PDGNode.DEFAULT_NO_LOCAL);
				in[i] = actIn;
			}
		}

		extLabel.append(')');
		lastNode.setLabel(extLabel.toString());

		PDGNode retOut = null;
		if (instr.getNumberOfReturnValues() == 1) {
			retOut = pdg.createNode("ret 0", PDGNode.Kind.ACTUAL_OUT, type, PDGNode.DEFAULT_NO_LOCAL);
		} else if (instr.getNumberOfReturnValues() > 1) {
			throw new IllegalStateException("Currently not supported - method has more then a single return value: " + tgt);
		}

		final PDGNode excOut = pdg.createNode("ret _exception_", PDGNode.Kind.ACTUAL_OUT, TypeReference.JavaLangException, PDGNode.DEFAULT_NO_LOCAL);
		final PDGCallReturn out = new PDGCallReturn(retOut, excOut);

		pdg.addCall(lastNode, in, out);
	}

	@Override
	public void visitNew(SSANewInstruction instr) {
		final TypeReference tref = instr.getConcreteType();
		final String label = tmpName(instr.getDef()) + " = new " + PrettyWalaNames.simpleTypeName(tref);

		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(label, Kind.NEW, tref, localDefNames);
	}

	@Override
	public void visitArrayLength(SSAArrayLengthInstruction instr) {
		final String label = tmpName(instr.getDef()) + " = " + tmpName(instr.getArrayRef()) + ".length";

		lastNode = pdg.createNode(label, Kind.EXPRESSION, TypeReference.Int, PDGNode.DEFAULT_NO_LOCAL);
	}

	@Override
	public void visitThrow(SSAThrowInstruction instruction) {
		final String label = "throw " + tmpName(instruction.getException());

		lastNode = pdg.createNode(label, Kind.NORMAL, TypeReference.JavaLangException, PDGNode.DEFAULT_NO_LOCAL);
	}

	@Override
	public void visitMonitor(SSAMonitorInstruction instr) {
		int ref = instr.getRef();

		String label = (instr.isMonitorEnter() ? "MONITORENTER " : "MONITOREXIT ") + tmpName(ref);

		lastNode = pdg.createNode(label, Kind.SYNCHRONIZATION, PDGNode.DEFAULT_TYPE, PDGNode.DEFAULT_NO_LOCAL);
	}

	@Override
	public void visitCheckCast(SSACheckCastInstruction instr) {
		String label = tmpName(instr.getDef()) + " = CHECKCAST " + tmpName(instr.getVal());

		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(label, Kind.EXPRESSION, PDGNode.DEFAULT_TYPE, localDefNames);
	}

	@Override
	public void visitInstanceof(SSAInstanceofInstruction instr) {
		String label = tmpName(instr.getDef()) + "=" + tmpName(instr.getRef()) + " INSTANCEOF "
			+ PrettyWalaNames.simpleTypeName(instr.getCheckedType());

		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(label, Kind.EXPRESSION, TypeReference.Boolean, localDefNames);
	}

	@Override
	public void visitPhi(SSAPhiInstruction instr) {
		StringBuffer sb = new StringBuffer();
		sb.append("PHI " + tmpName(instr.getDef()) + " = ");

		int[] uses = new int[instr.getNumberOfUses()];
		for (int i = 0; i < uses.length; i++) {
			uses[i] = instr.getUse(i);
			sb.append(tmpName(uses[i]));
			if (i < uses.length - 1) {
				sb.append(", ");
			}
		}
		
		final String[] localDefNames;
		if (this.associateLocalNames) {
			final int lastInstructionOfBB = ir.getBasicBlockForInstruction(instr).getLastInstructionIndex();
			localDefNames = ir.getLocalNames(lastInstructionOfBB, instr.getDef());
		} else {
			localDefNames = PDGNode.DEFAULT_NO_LOCAL;
		}
		lastNode = pdg.createNode(sb.toString(), Kind.PHI, PDGNode.DEFAULT_NO_TYPE, localDefNames);
	}

	@Override
	public void visitPi(SSAPiInstruction instruction) {
		throw new UnsupportedOperationException("We do not treat pi-instructions.");
	}

	@Override
	public void visitGetCaughtException(SSAGetCaughtExceptionInstruction instr) {
		String label = tmpName(instr.getDef()) + " = catch <exc>";

		lastNode = pdg.createNode(label, Kind.EXPRESSION, TypeReference.JavaLangException, PDGNode.DEFAULT_NO_LOCAL);
	}

	@Override
	public void visitLoadMetadata(SSALoadMetadataInstruction instr) {
		String label = tmpName(instr.getDef()) + " = metadata " + instr.getToken() + "->" + instr.getType();

		lastNode = pdg.createNode(label, Kind.EXPRESSION, instr.getType(), PDGNode.DEFAULT_NO_LOCAL);
	}

}
