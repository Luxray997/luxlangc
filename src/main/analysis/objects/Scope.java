package main.analysis.objects;

import main.parser.nodes.FunctionDeclaration;
import main.parser.nodes.Parameter;
import main.parser.nodes.statements.VariableDeclaration;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope parent;
    private final Map<String, FunctionSymbol> functionSymbols;
    private final Map<String, VariableSymbol> variableSymbols;


    public Scope(Scope parent) {
        this.parent = parent;
        this.functionSymbols = new HashMap<>();
        this.variableSymbols = new HashMap<>();
    }

    // Assumes hasVariable was called and resulted in true
    public VariableSymbol getVariable(String variableName) {
        VariableSymbol variable = variableSymbols.get(variableName);
        if (variable == null) {
            return parent.getVariable(variableName);
        }
        return variable;
    }

    // Assumes hasFunction was called and resulted in true
    public FunctionSymbol getFunction(String functionName) {
        FunctionSymbol function = functionSymbols.get(functionName);
        if (function == null) {
            return parent.getFunction(functionName);
        }
        return function;
    }

    public boolean hasVariable(String variableName) {
        return variableSymbols.containsKey(variableName) || (parent != null && parent.hasVariable(variableName));
    }

    public boolean hasFunction(String functionName) {
        return functionSymbols.containsKey(functionName) || (parent != null && parent.hasFunction(functionName));
    }

    public void addFunction(FunctionDeclaration functionDeclaration) {
        var symbol = FunctionSymbol.from(functionDeclaration);
        functionSymbols.put(symbol.name(), symbol);
    }

    public void addVariable(VariableDeclaration variableDeclaration) {
        var symbol = VariableSymbol.from(variableDeclaration);
        variableSymbols.put(symbol.name(), symbol);
    }

    public void addVariable(Parameter parameter) {
        var symbol = VariableSymbol.from(parameter);
        variableSymbols.put(symbol.name(), symbol);
    }
}