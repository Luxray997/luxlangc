package main.analysis;

import main.parser.nodes.FunctionDeclaration;
import main.parser.nodes.Parameter;
import main.parser.nodes.statements.VariableDeclaration;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope parent;
    private final Map<String, Symbol> symbols;

    public Scope(Scope parent) {
        this.parent = parent;
        this.symbols = new HashMap<>();
    }

    public VariableSymbol getVariable(String variableName) {
        return (VariableSymbol) getSymbol(variableName);
    }

    public FunctionSymbol getFunction(String functionName) {
        return (FunctionSymbol) getSymbol(functionName);
    }

    public boolean hasVariable(String variableName) {
        if (!hasSymbol(variableName)) return false;

        return getSymbol(variableName) instanceof VariableSymbol;
    }

    public boolean hasFunction(String variableName) {
        if (!hasSymbol(variableName)) return false;

        return getSymbol(variableName) instanceof FunctionSymbol;
    }

    public boolean hasSymbol(String symbolName) {
        return symbols.containsKey(symbolName) || (parent != null && parent.hasSymbol(symbolName));
    }

    public Symbol getSymbol(String symbolName) {
        Symbol thisScopeSymbol = symbols.get(symbolName);
        if (thisScopeSymbol == null) {
            return parent.getSymbol(symbolName);
        }
        return thisScopeSymbol;
    }

    public void addFunction(FunctionDeclaration functionDeclaration) {
        Symbol symbol = FunctionSymbol.from(functionDeclaration);
        symbols.put(symbol.name(), symbol);
    }

    public void addVariable(VariableDeclaration variableDeclaration) {
        Symbol symbol = VariableSymbol.from(variableDeclaration);
        symbols.put(symbol.name(), symbol);
    }

    public void addVariable(Parameter parameter) {
        Symbol symbol = VariableSymbol.from(parameter);
        symbols.put(symbol.name(), symbol);
    }
}