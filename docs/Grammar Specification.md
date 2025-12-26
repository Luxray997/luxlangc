# Grammar Specification

## Key
- `<>` — Grammar element
- `''` — Terminal symbol
- `()` — Grouping
- `*` — 0 or more
- `+` — 1 or more
- `?` — 0 or 1
- `|` — Or

---

## Grammar

```ebnf
program                : <function_declaration>*

function_declaration   : <type> <identifier> '(' <param_list>? ')' <code_block>

type                   : 'void' | 'byte' | 'ubyte' | 'short' | 'ushort'
                       | 'int' | 'uint' | 'long' | 'ulong'
                       | 'float' | 'double' | 'bool'

param_list             : <type> <identifier> (',' <type> <identifier>)*

code_block             : '{' <statement>* '}'

statement              : <code_block>
                       | ('if' '(' <expression> ')' <statement> ('else' <statement>)?)
                       | ('while' '(' <expression> ')' <statement>)
                       | ('do' <statement> 'while' '(' <expression> ')' ';')
                       | ('for' '(' <for_init>? ';' <expression>? ';' <assignment>? ')' <statement>)
                       | ('return' <expression>? ';')
                       | (<variable_declaration> ';')
                       | (<assignment> ';')

for_init               : <variable_declaration> | <assignment>

variable_declaration   : <type> <identifier> ('=' <expression>)?

expression             : <logical_or>

literal                : <integer_literal>
                       | <floating_point_literal>
                       | 'true'
                       | 'false'

primary                : <literal>
                       | (<identifier> ('(' <argument_list>? ')')?)
                       | ('(' <expression> ')')

unary                  : (('!' | '-' | '~') <unary>) | <primary>

multiplicative         : <unary> (('*' | '/' | '%') <unary>)*

additive               : <multiplicative> (('+' | '-') <multiplicative>)*

relational             : <additive> (('<' | '<=' | '>' | '>=') <additive>)*

equivalence            : <relational> (('==' | '!=') <relational>)*

bitwise_and            : <equivalence> ('&' <equivalence>)*

bitwise_xor            : <bitwise_and> ('^' <bitwise_and>)*

bitwise_or             : <bitwise_xor> ('|' <bitwise_xor>)*

logical_and            : <bitwise_or> ('&&' <bitwise_or>)*

logical_or             : <logical_and> ('||' <logical_and>)*

assignment             : <identifier> '=' <expression>

argument_list          : <expression> (',' <expression>)*

identifier             : <letter> (<letter> | <digit> | '_')*

integer_literal        : <digit>+ <integer_suffix>
                     
integer_suffix         : ('U' | 'u')? ('L' | 'l' | 'S' | 's' | 'B' | 'b')?

floating_point_literal : (<digit>+ <floating_point_suffix>)
                       | (<digit>* '.' <digit>+ <floating_point_suffix>?)

floating_point_suffix  : ('D' | 'd' | 'F' | 'f')

digit                  : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'

letter                 : 'a' | 'b' | 'c' | 'd'
                       | 'e' | 'f' | 'g' | 'h'
                       | 'i' | 'j' | 'k' | 'l'
                       | 'm' | 'n' | 'o' | 'p'
                       | 'q' | 'r' | 's' | 't'
                       | 'u' | 'v' | 'w' | 'x'
                       | 'y' | 'z' | 'A' | 'B'
                       | 'C' | 'D' | 'E' | 'F'
                       | 'G' | 'H' | 'I' | 'J'
                       | 'K' | 'L' | 'M' | 'N'
                       | 'O' | 'P' | 'Q' | 'R'
                       | 'S' | 'T' | 'U' | 'V'
                       | 'W' | 'X' | 'Y' | 'Z'
                       