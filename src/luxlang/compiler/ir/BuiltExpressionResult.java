package luxlang.compiler.ir;

import luxlang.compiler.ir.values.IRValue;

record BuiltExpressionResult(IRValue value, BasicBlock lastBlock) {

}
