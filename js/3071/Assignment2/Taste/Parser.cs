/*----------------------------------------------------------------------
Compiler Generator Coco/R,
Copyright (c) 1990, 2004 Hanspeter Moessenboeck, University of Linz
extended by M. Loeberbauer & A. Woess, Univ. of Linz
with improvements by Pat Terry, Rhodes University

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by the 
Free Software Foundation; either version 2, or (at your option) any 
later version.

This program is distributed in the hope that it will be useful, but 
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
for more details.

You should have received a copy of the GNU General Public License along 
with this program; if not, write to the Free Software Foundation, Inc., 
59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

As an exception, it is allowed to write an extension of Coco/R that is
used as a plugin in non-free software.

If not otherwise stated, any source code generated by Coco/R (other than 
Coco/R itself) does not fall under the GNU General Public License.
-----------------------------------------------------------------------*/

using System;

namespace Taste {



public class Parser {
	public const int _EOF = 0;
	public const int _ident = 1;
	public const int _number = 2;
	public const int maxT = 37;

	const bool T = true;
	const bool x = false;
	const int minErrDist = 2;
	
	public Scanner scanner;
	public Errors  errors;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;

const int // types
	  undef = 0, integer = 1, boolean = 2;

	const int // object kinds
	  var = 0, proc = 1, constant =3, array = 4;

	public SymbolTable   tab;
	public CodeGenerator gen;
  
/*--------------------------------------------------------------------------*/


	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (string msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) { ++errDist; break; }

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	bool StartOf (int s) {
		return set[s, la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}


	bool WeakSeparator(int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) {Get(); return true;}
		else if (StartOf(repFol)) {return false;}
		else {
			SynErr(n);
			while (!(set[syFol, kind] || set[repFol, kind] || set[0, kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}

	
	void AddOp(out Op op) {
		op = Op.ADD; 
		if (la.kind == 3) {
			Get();
		} else if (la.kind == 4) {
			Get();
			op = Op.SUB; 
		} else SynErr(38);
	}

	void Expr(out int type) {
		int type1; Op op; 
		SimExpr(out type);
		if (StartOf(1)) {
			RelOp(out op);
			SimExpr(out type1);
			if (type != type1) SemErr("incompatible types");
			gen.Emit(op); type = boolean; 
		}
	}

	void SimExpr(out int type) {
		int type1; Op op; 
		Term(out type);
		while (la.kind == 3 || la.kind == 4) {
			AddOp(out op);
			Term(out type1);
			if (type != integer || type1 != integer) 
			 SemErr("integer type expected");
			gen.Emit(op); 
		}
	}

	void RelOp(out Op op) {
		op = Op.EQU; 
		switch (la.kind) {
		case 14: {
			Get();
			break;
		}
		case 15: {
			Get();
			op = Op.LSS; 
			break;
		}
		case 16: {
			Get();
			op = Op.GTR; 
			break;
		}
		case 17: {
			Get();
			op = Op.NEQU; 
			break;
		}
		case 18: {
			Get();
			op = Op.LSSEQ; 
			break;
		}
		case 19: {
			Get();
			op = Op.GTREQ; 
			break;
		}
		default: SynErr(39); break;
		}
	}

	void Factor(out int type) {
		int n; Obj obj; string name; 
		type = undef; 
		if (la.kind == 1) {
			Ident(out name);
			obj = tab.Find(name); type = obj.type;
			if (obj.kind == var) {
			if (obj.level == 0) gen.Emit(Op.LOADG, obj.adr);
			else gen.Emit(Op.LOAD, obj.adr);
			             } else SemErr("variable expected"); 
		} else if (la.kind == 2) {
			Get();
			n = Convert.ToInt32(t.val); 
			gen.Emit(Op.CONST, n); type = integer; 
		} else if (la.kind == 4) {
			Get();
			Factor(out type);
			if (type != integer) {
			 SemErr("integer type expected"); type = integer;
			}
			gen.Emit(Op.NEG); 
		} else if (la.kind == 5) {
			Get();
			gen.Emit(Op.CONST, 1); type = boolean; 
		} else if (la.kind == 6) {
			Get();
			gen.Emit(Op.CONST, 0); type = boolean; 
		} else SynErr(40);
	}

	void Ident(out string name) {
		Expect(1);
		name = t.val; 
	}

	void MulOp(out Op op) {
		op = Op.MUL; 
		if (la.kind == 7) {
			Get();
		} else if (la.kind == 8) {
			Get();
			op = Op.DIV; 
		} else SynErr(41);
	}

	void ProcDecl() {
		string name; Obj obj; int adr; 
		Expect(9);
		Ident(out name);
		obj = tab.NewObj(name, proc, undef); obj.adr = gen.pc;
		if (name == "Main") gen.progStart = gen.pc; 
		tab.OpenScope(); 
		Expect(10);
		Expect(11);
		Expect(12);
		gen.Emit(Op.ENTER, 0); adr = gen.pc - 2; 
		while (StartOf(2)) {
			if (la.kind == 31 || la.kind == 32 || la.kind == 33) {
				VarDecl();
			} else {
				Stat();
			}
		}
		Expect(13);
		gen.Emit(Op.LEAVE); gen.Emit(Op.RET);
		gen.Patch(adr, tab.topScope.nextAdr);
		tab.CloseScope(); 
	}

	void VarDecl() {
		string name; int type; int kind = var; int size =1;/*Size is a bad solution, fix later*/ 
		Type(out type);
		Ident(out name);
		if (la.kind == 34) {
			Get();
			Expect(2);
			size = Convert.ToInt32(t.val); 
			Expect(35);
			kind = array; 
		}
		tab.NewObj(name, kind, type, size); 
		while (la.kind == 36) {
			Get();
			Ident(out name);
			tab.NewObj(name, var, type); 
		}
		Expect(21);
	}

	void Stat() {
		int type,type1,type2; string name; Obj obj;
		int adr, adr2, loopstart; 
		int loopUpdate; 
		switch (la.kind) {
		case 1: {
			Ident(out name);
			obj = tab.Find(name); 
			if (la.kind == 20) {
				Get();
				if (obj.kind != var) SemErr("cannot assign to procedure"); 
				Expr(out type);
				if (StartOf(3)) {
					if (la.kind == 21) {
						Get();
					}
					if (type != obj.type) SemErr("incompatible types");
					if (obj.level == 0) gen.Emit(Op.STOG, obj.adr);
					else gen.Emit(Op.STO, obj.adr); 
				} else if (la.kind == 22) {
					if(type != boolean) SemErr("Boolean expression expected");
					gen.Emit(Op.FJMP, 0); adr = gen.pc -2; 
					if (obj.level == 0) gen.Emit(Op.STOG, obj.adr);
					else gen.Emit(Op.STO, obj.adr); 
					Get();
					Expr(out type1);
					Expect(23);
					gen.Emit(Op.JMP,0); adr2 = gen.pc -2;
					gen.Patch(adr,gen.pc); adr = adr2;
					if (obj.level == 0) gen.Emit(Op.STOG, obj.adr);
					else gen.Emit(Op.STO, obj.adr); 
					
					Expr(out type2);
					Expect(21);
					gen.Patch(adr, gen.pc); 
				} else SynErr(42);
			} else if (la.kind == 10) {
				Get();
				Expect(11);
				Expect(21);
				if (obj.kind != proc) SemErr("object is not a procedure");
				gen.Emit(Op.CALL, obj.adr); 
			} else SynErr(43);
			break;
		}
		case 24: {
			Get();
			Expect(10);
			Expr(out type);
			Expect(11);
			if (type != boolean) SemErr("boolean type expected");
			gen.Emit(Op.FJMP, 0); adr = gen.pc - 2; 
			Stat();
			if (la.kind == 25) {
				Get();
				gen.Emit(Op.JMP, 0); adr2 = gen.pc - 2;
				gen.Patch(adr, gen.pc);  //Patch the address to branch to should the boolean check fail
				adr = adr2; 
				Stat();
			}
			gen.Patch(adr, gen.pc); 
			break;
		}
		case 26: {
			Get();
			loopstart = gen.pc; 
			Expect(10);
			Expr(out type);
			Expect(11);
			if (type != boolean) SemErr("boolean type expected");
			gen.Emit(Op.FJMP, 0); adr = gen.pc - 2; 
			Stat();
			gen.Emit(Op.JMP, loopstart); gen.Patch(adr, gen.pc); 
			break;
		}
		case 27: {
			Get();
			Ident(out name);
			Expect(21);
			obj = tab.Find(name);
			if (obj.type != integer) SemErr("integer type expected");
			gen.Emit(Op.READ);
			if (obj.level == 0) gen.Emit(Op.STOG, obj.adr);
			else gen.Emit(Op.STO, obj.adr); 
			break;
		}
		case 28: {
			Get();
			Expr(out type);
			Expect(21);
			if (type != integer) SemErr("integer type expected");
			gen.Emit(Op.WRITE); 
			break;
		}
		case 29: {
			Get();
			Expect(10);
			Stat();
			loopstart = gen.pc; 
			Expr(out type);
			Expect(21);
			if(type != boolean) SemErr("boolean type expected");
			gen.Emit(Op.FJMP, 0); adr = gen.pc -2; //Jump if check fails
			gen.Emit(Op.JMP, 0); adr2 = gen.pc -2; //Jump to loop body
			loopUpdate = gen.pc; 
			Stat();
			gen.Emit(Op.JMP, loopstart); 
			Expect(11);
			gen.Patch(adr2, gen.pc); 
			Stat();
			gen.Emit(Op.JMP, loopUpdate); gen.Patch(adr, gen.pc); 
			break;
		}
		case 12: {
			Get();
			while (StartOf(2)) {
				if (StartOf(4)) {
					Stat();
				} else {
					VarDecl();
				}
			}
			Expect(13);
			break;
		}
		default: SynErr(44); break;
		}
	}

	void Term(out int type) {
		int type1; Op op; 
		Factor(out type);
		while (la.kind == 7 || la.kind == 8) {
			MulOp(out op);
			Factor(out type1);
			if (type != integer || type1 != integer) 
			 SemErr("integer type expected");
			gen.Emit(op); 
		}
	}

	void Taste() {
		string name; 
		Expect(30);
		Ident(out name);
		tab.OpenScope(); 
		Expect(12);
		while (StartOf(5)) {
			if (la.kind == 31 || la.kind == 32 || la.kind == 33) {
				VarDecl();
			} else {
				ProcDecl();
			}
		}
		Expect(13);
		tab.CloseScope();
		if (gen.progStart == -1) SemErr("main function never defined");
		
	}

	void Type(out int type) {
		type = undef; 
		if (la.kind == 31) {
			Get();
			type = integer; 
		} else if (la.kind == 32) {
			Get();
			type = boolean; 
		} else if (la.kind == 33) {
			Get();
			type = constant; 
		} else SynErr(45);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Taste();
		Expect(0);

	}
	
	static readonly bool[,] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, T,x,T,T, T,T,x,T, T,T,x,x, x,x,x},
		{x,T,T,x, T,T,T,x, x,x,x,T, T,T,x,x, x,x,x,x, x,T,x,x, T,T,T,T, T,T,x,T, T,T,x,x, x,x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, T,x,T,T, T,T,x,x, x,x,x,x, x,x,x},
		{x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,x,x, x,x,x}

	};
} // end Parser


public class Errors {
	public int count = 0;                                    // number of errors detected
	public System.IO.TextWriter errorStream = Console.Out;   // error messages go to this stream
	public string errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text

	public virtual void SynErr (int line, int col, int n) {
		string s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "number expected"; break;
			case 3: s = "\"+\" expected"; break;
			case 4: s = "\"-\" expected"; break;
			case 5: s = "\"true\" expected"; break;
			case 6: s = "\"false\" expected"; break;
			case 7: s = "\"*\" expected"; break;
			case 8: s = "\"/\" expected"; break;
			case 9: s = "\"void\" expected"; break;
			case 10: s = "\"(\" expected"; break;
			case 11: s = "\")\" expected"; break;
			case 12: s = "\"{\" expected"; break;
			case 13: s = "\"}\" expected"; break;
			case 14: s = "\"==\" expected"; break;
			case 15: s = "\"<\" expected"; break;
			case 16: s = "\">\" expected"; break;
			case 17: s = "\"!=\" expected"; break;
			case 18: s = "\"<=\" expected"; break;
			case 19: s = "\">=\" expected"; break;
			case 20: s = "\"=\" expected"; break;
			case 21: s = "\";\" expected"; break;
			case 22: s = "\"?\" expected"; break;
			case 23: s = "\":\" expected"; break;
			case 24: s = "\"if\" expected"; break;
			case 25: s = "\"else\" expected"; break;
			case 26: s = "\"while\" expected"; break;
			case 27: s = "\"read\" expected"; break;
			case 28: s = "\"write\" expected"; break;
			case 29: s = "\"for\" expected"; break;
			case 30: s = "\"program\" expected"; break;
			case 31: s = "\"int\" expected"; break;
			case 32: s = "\"bool\" expected"; break;
			case 33: s = "\"const\" expected"; break;
			case 34: s = "\"[\" expected"; break;
			case 35: s = "\"]\" expected"; break;
			case 36: s = "\",\" expected"; break;
			case 37: s = "??? expected"; break;
			case 38: s = "invalid AddOp"; break;
			case 39: s = "invalid RelOp"; break;
			case 40: s = "invalid Factor"; break;
			case 41: s = "invalid MulOp"; break;
			case 42: s = "invalid Stat"; break;
			case 43: s = "invalid Stat"; break;
			case 44: s = "invalid Stat"; break;
			case 45: s = "invalid Type"; break;

			default: s = "error " + n; break;
		}
		errorStream.WriteLine(errMsgFormat, line, col, s);
		count++;
	}

	public virtual void SemErr (int line, int col, string s) {
		errorStream.WriteLine(errMsgFormat, line, col, s);
		count++;
	}
	
	public virtual void SemErr (string s) {
		errorStream.WriteLine(s);
		count++;
	}
	
	public virtual void Warning (int line, int col, string s) {
		errorStream.WriteLine(errMsgFormat, line, col, s);
	}
	
	public virtual void Warning(string s) {
		errorStream.WriteLine(s);
	}
} // Errors


public class FatalError: Exception {
	public FatalError(string m): base(m) {}
}
}