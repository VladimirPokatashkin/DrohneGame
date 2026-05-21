import java_cup.runtime.*;

%%

%class Lexer
%cup
%line
%column
%public


%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
Space          = {LineTerminator} | [ \t\f]
Identifier     = [a-zA-Z_][a-zA-Z0-9_]*
DecLiteral     = 0 | [1-9][0-9]*
HexLiteral     = x[0-9A-F]+

%%
<YYINITIAL> {

  "seisu"        { return symbol(sym.SEISU); }
  "ronri"        { return symbol(sym.RONRI); }
  "rippotai"     { return symbol(sym.RIPPOTAI); }
  "hairetsu"     { return symbol(sym.HAIRETSU); }
  "jigen"        { return symbol(sym.JIGEN); }
  "ruikei"       { return symbol(sym.RUIKEI); }
  "shuki"        { return symbol(sym.SHUKI); }
  "kido"         { return symbol(sym.KIDO); }
  "shushi"       { return symbol(sym.SHUSHI); }
  "sorenara"     { return symbol(sym.SORENARA); }
  "kansu"        { return symbol(sym.KANSU); }

  "shinri"       { return symbol(sym.SHINRI); }
  "uso"          { return symbol(sym.USO); }

  "^_^"          { return symbol(sym.MOVE_UP); }
  "v_v"          { return symbol(sym.MOVE_DOWN); }
  "<_<"          { return symbol(sym.MOVE_LEFT); }
  ">_>"          { return symbol(sym.MOVE_RIGHT); }
  "o_o"          { return symbol(sym.MOVE_FORWARD); }
  "~_~"          { return symbol(sym.MOVE_BACK); }

  "^_0"          { return symbol(sym.SCAN_UP); }
  "v_0"          { return symbol(sym.SCAN_DOWN); }
  "<_0"          { return symbol(sym.SCAN_LEFT); }
  ">_0"          { return symbol(sym.SCAN_RIGHT); }
  "o_0"          { return symbol(sym.SCAN_FORWARD); }
  "~_0"          { return symbol(sym.SCAN_BACK); }

  ">_<"          { return symbol(sym.BREAK_SEQ); }
  "*_*"          { return symbol(sym.GET_POS); }

  "=>"           { return symbol(sym.ARROW); }
  ";"            { return symbol(sym.SEMI); }
  ":"            { return symbol(sym.COLON); }
  ","            { return symbol(sym.COMMA); }
  "="            { return symbol(sym.ASSIGN); }
  "{"            { return symbol(sym.LBRACE); }
  "}"            { return symbol(sym.RBRACE); }
  "["            { return symbol(sym.LBRACKET); }
  "]"            { return symbol(sym.RBRACKET); }
  "("            { return symbol(sym.LPAREN); }
  ")"            { return symbol(sym.RPAREN); }

  "+"            { return symbol(sym.PLUS); }
  "-"            { return symbol(sym.MINUS); }
  "~"            { return symbol(sym.NOT); }
  "^"            { return symbol(sym.AND); }
  "v"            { return symbol(sym.OR); }
  "<"            { return symbol(sym.LESS); }
  ">"            { return symbol(sym.GREATER); }

  {HexLiteral}   { return symbol(sym.HEX_NUMBER, yytext()); }
  {DecLiteral}   { return symbol(sym.DEC_NUMBER, Integer.parseInt(yytext())); }
  {Identifier}   { return symbol(sym.IDENTIFIER, yytext()); }

  {WhiteSpace}   { }
}

[^]              { throw new Error("Illegal character <"+yytext()+"> at line " + yyline); }