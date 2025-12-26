package luxlang.compiler.ir.objects;

import luxlang.compiler.ir.instructions.RegularInstruction;
import luxlang.compiler.ir.instructions.TerminatorInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BasicBlock {
    private final int id;
    private final String name;
    private final List<RegularInstruction> instructions;
    private TerminatorInstruction terminator;

    public BasicBlock(int id, String name) {
        this.id = id;
        this.name = name;
        this.instructions = new ArrayList<>();
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public List<RegularInstruction> instructions() {
        return instructions;
    }

    public TerminatorInstruction terminator() {
        return terminator;
    }

    /**
     * Format:
     *   bb<id>:  ; <name>
     *       <instructions>
     *       <terminator>
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("  bb").append(id).append(":  ; ").append(name).append("\n");

        for (RegularInstruction instruction : instructions) {
            sb.append("    ").append(instruction.serialize()).append("\n");
        }

        if (terminator != null) {
            sb.append("    ").append(terminator.serialize()).append("\n");
        }

        return sb.toString();
    }

    public String label() {
        return "bb" + id;
    }

    public void setTerminator(TerminatorInstruction terminatorInstruction) {
        if (this.terminator == null) {
            this.terminator = terminatorInstruction;
        }
        // This needs to be null checked so that ifs/loops/functions don't overwrite blocks ending with an explicit return
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BasicBlock) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.instructions, that.instructions) &&
                Objects.equals(this.terminator, that.terminator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, instructions, terminator);
    }

    @Override
    public String toString() {
        return "BasicBlock[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "instructions=" + instructions + ", " +
                "terminator=" + terminator + ']';
    }
}
