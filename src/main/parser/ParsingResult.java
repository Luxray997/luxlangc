package main.parser;



import main.parser.errors.ParsingError;
import main.parser.nodes.Program;

import java.util.List;


public sealed interface ParsingResult permits ParsingResult.Success, ParsingResult.Failure {
    record Success(Program program) implements ParsingResult { }
    record Failure(List<ParsingError> errors) implements ParsingResult { }

    static ParsingResult success(Program program) {
        return new ParsingResult.Success(program);
    }

    static ParsingResult failure(List<ParsingError> errors) {
        return new ParsingResult.Failure(errors);
    }
}