; ID: 3114
; Author: Yasha
; Date: 2014-03-09 15:21:13
; Title: Max Yourself A Scheme In 48 Hours
; Description: a translation/reimplementation of the Haskell

' Max Yourself a Scheme in 48 Hours!
' (a reimplementation from the Haskell)

' single-file "Code Archives Edition"

' If you haven't read the tutorials yet, go back and look at them first!

SuperStrict

Import "TMeta.bmx"			'Get this here: http://www.blitzbasic.com/codearcs/codearcs.php?code=3113
Import "Functional.bmx"		'Get this here: http://www.blitzbasic.com/codearcs/codearcs.php?code=3090

Local env:SchemeEnv
If AppArgs.Length > 1
	env = SchemeREPL.RunFiles(AppArgs[1..])
Else
	env = SchemeREPL.RunREPL()
EndIf


' "Scheme.bmx":
'===============

Type SchemeREPL
	Function Eval:LispVal(env:SchemeEnv, val:LispVal)
		Local ret:LispVal, tc:LispDeferredTailCall
		Repeat
			ret = TailEval(env, val) ; tc = LispDeferredTailCall(ret)
			If tc
				env = tc.env ; val = tc.val ; ret = Null
			EndIf
		Until ret
		Return ret
	End Function
	
	Function EvalMany:LispVal(env:SchemeEnv, vals:LispVal[])
		Local ret:LispVal
		For Local v:LispVal = EachIn vals
			ret = Eval(env, v)
		Next
		Return ret
	End Function
	
	Function TailEval:LispVal(env:SchemeEnv, val:LispVal)
		Global ev:TDelegate = TDelegate.Make(Eval), unVal:RefCell(_:Object) = RefCell.unVal, unCons:RefCell(l:Object, r:RefCell) = RefCell.unCons
		Global unType:RefCell(_:Object, t:Object(_:Object)) = RefCell.unType, unMaybe:RefCell(_:RefCell) = RefCell.unMaybe
		Global isAtom:TDelegate = TDelegate.Make(LispAtom.Is)
		Local A:RefCell = RefCell.Make(), B:RefCell = New A, C:RefCell = New A', D:RefCell = New A
		
		Select val
			Case LispAtom(val)
				Return SchemeEnv.GetVar(env, LispAtom(val).name)
				
			Case LispList(val)
				Select True
					Case unCons(unVal("quote"), B).match(val)
						Return LispVal(ConsList(B._).val)
						
					Case unCons(unVal("if"), unCons(A, unCons(B, unCons(C, Null)))).match(val)
						Local pred:LispVal = Eval(env, LispVal(A._))
						If LispBool(pred) And (LispBool(pred).val = 0) ..
							Then Return LispDeferredTailCall.Make(env, LispVal(C._)) ..
							Else Return LispDeferredTailCall.Make(env, LispVal(B._))
						
					Case unCons(unVal("set!"), unCons(unType(A, LispAtom.Is), unCons(B, Null))).match(val)
						Return SchemeEnv.SetVar(env, LispAtom(A._).name, Eval(env, LispVal(B._)))
						
					Case unCons(unVal("define"), unCons(unType(A, LispAtom.Is), unCons(B, Null))).match(val)
						Return SchemeEnv.DefineVar(env, LispAtom(A._).name, Eval(env, LispVal(B._)))
					Case unCons(unVal("define"), unCons(unType(A, LispDottedList.Is), B)).match(val)
						Local nargs:LispDottedList = LispDottedList(A._), name:Object = nargs.vals.val
						Local f:LispFunc = LispFunc.Make(nargs.vals.nx, nargs.last.ToString(), ConsList(B._), env)
						Return SchemeEnv.DefineVar(env, name.ToString(), f)
					Case unCons(unVal("define"), unCons(unType(A, LispList.Is), B)).match(val)
						Local nargs:LispList = LispList(A._), name:Object = nargs.vals.val
						Local f:LispFunc = LispFunc.Make(nargs.vals.nx, Null, ConsList(B._), env)
						Return SchemeEnv.DefineVar(env, name.ToString(), f)
						
					Case unCons(unVal("lambda"), unCons(unType(A, LispDottedList.Is), B)).match(val)
						Local args:LispDottedList = LispDottedList(A._)
						Return LispFunc.Make(args.vals, args.last.ToString(), ConsList(B._), env)
					Case unCons(unVal("lambda"), unCons(unType(A, LispList.Is), B)).match(val)
						Return LispFunc.Make(LispList(A._).vals, Null, ConsList(B._), env)
					Case unCons(unVal("lambda"), unCons(unType(A, LispAtom.Is), B)).match(val)
						Return LispFunc.Make(Null, B._.ToString(), ConsList(B._), env)
						
					Case unCons(unVal("macro"), B).match(val)
						Global lam:LispAtom = LispAtom.Make("lambda")
						Local ll:LispList = LispList.FromCons(ConsList.Cons(lam, ConsList(B._)))
						Return LispMacro.FromFunc(LispFunc(TailEval(env, ll)))
						
					Case unCons(unVal("load"), unCons(unType(A, LispString.Is), Null)).match(val)
						Local port:LispPort = LispPort.Make("READMODE", LispString(A._).val)
						ConsList.Map(ev.curry(env), SchemeBuiltins._readAll(ConsList.Cons(port, Null)).vals)
						port.stream.Close() ; port.stream = Null
						Return LispBool._True
						
					Case unCons(unVal("if"), RefCell.Any).match(val), ..
					     unCons(unVal("set!"), RefCell.Any).match(val), ..
					     unCons(unVal("load"), RefCell.Any).match(val), ..
					     unCons(unVal("macro"), RefCell.Any).match(val), ..
					     unCons(unVal("define"), RefCell.Any).match(val), ..
					     unCons(unVal("lambda"), RefCell.Any).match(val)
					DebugStop
						badSpecialForm val
						
					Case unCons(A, B).match(val)
						Local func:LispVal = Eval(env, LispVal(A._)), args:ConsList = ConsList(B._)
						If Not LispMacro(func) Then args = ConsList.Map(ev.curry(env), args)
						Return Apply(func, args)
					Case unCons(A, Null).match(val)
						Return Apply(Eval(env, LispVal(A._)), Null)
						
				End Select
				Function badSpecialForm(val:LispVal)
					LispError.Raise "Eval: malformed '" + LispList(val).vals.val.ToString() + "' expression: " + val.ToString()
				End Function
				
			Case LispNum(val), LispString(val), LispBool(val), LispChar(val), LispVector(val)
				Return val
			Case LispDeferredTailCall(val)
				LispError.Raise "Eval: deferred tail calls are not supposed to be used as values"
		End Select
		LispError.Raise "Eval: bad special form " + val.ToString()	'Getting here requires something to go wrong; all match branches return
	End Function
	
	Function Apply:LispVal(op:LispVal, args:ConsList)
		Select op
			Case LispPrimitiveFunc(op)
				Return LispVal(LispPrimitiveFunc(op).f.call(args))
				
			Case LispFunc(op)
				Local f:LispFunc = LispFunc(op), lnth:Int(_:ConsList) = ConsList.Length, fLen:Int = lnth(f.args)
				If (lnth(args) <> fLen And f.vararg = "") Or lnth(args) < fLen Then ..
				   LispError.ArgCount ConsList.Length(f.args), args
				Local newEnv:SchemeEnv = SchemeEnv.Make(f.closure)
				Global bind:TDelegate = TDelegate.Make(SchemeEnv.DefineVar), ev:TDelegate = TDelegate.Make(Eval)
				If f.vararg
					ConsList.ZipWith bind.curry(newEnv), f.args, ConsList.Take(args, fLen)
					bind.call2(f.vararg, ConsList.Drop(args, fLen))
				Else
					ConsList.ZipWith bind.curry(newEnv), f.args, args
				EndIf
				If LispMacro(f)
					Return LispVal(ConsList.Last(ConsList.Map(ev.curry(f.closure), ..
					                                          ConsList.Map(ev.curry(newEnv), f.body))))
				Else
					Local nonTail:ConsList = ConsList.Take(f.body, ConsList.Length(f.body) - 1)
					ConsList.Map(ev.curry(newEnv), nonTail)
					Return LispDeferredTailCall.Make(newEnv, LispVal(ConsList.Last(f.body)))
				EndIf
				
			Default ; LispError.Raise "Apply: cannot apply non-function '" + op.ToString() + "'"
		End Select
	End Function
	
	Function Read:LispVal[](p:SchemeParser, src:String)
		Try
			Local tree:TParseNode = p.Parse(SchemeLexer.Get().ScanString(src))
			Return p.ToLispVals(tree)
		Catch e:ParseError
			Local msg:String = e.ToString(), SRCH:String = "error trying to complete '"
			msg = msg.Replace(SRCH + "(", SRCH + "list").Replace(SRCH + "#", SRCH + "vector")
			msg = msg.Replace(SRCH + "'", SRCH + "quoted form").Replace(SRCH + "`", SRCH + "quasiquoted form")
			LispError.Raise msg
		Catch e:LexError
			LispError.Raise e.ToString()
		End Try
	End Function
	
	Function ReadOne:LispVal(p:SchemeParser, port:LispPort)
		Local vals:LispVal[]
		If port.cached
			vals = port.cached
		Else
			Local s:String ; While Not Eof(port.stream)
				s :+ port.stream.ReadLine() + "~n"
			Wend
			vals = SchemeREPL.Read(p, s)
		EndIf
		port.cached = vals[1..] ; Return vals[0]
	End Function
	
	Function Write:LispVal(p:LispPort, v:LispVal)
		p.stream.WriteLine(v.ToString())
		p.stream.Flush
		Return LispBool._True
	End Function
	
	Function Show:Object(v:LispVal)
		Print "-> " + (v.ToString()) ; Return Null
	End Function
	
	Function RunREPL:SchemeEnv(env:SchemeEnv = Null)
		Global read:TDelegate = TDelegate.Make(Read), eval:TDelegate = TDelegate.Make(EvalMany), write:TDelegate = TDelegate.Make(Show)
		Local q:SchemeParser = New SchemeParser
		If env = Null Then env = SchemeEnv.MakeGlobal()
		Local _main:TDelegate = write.compose(eval.curry(env).compose(read.curry(q)))
		Repeat
			Local in:String = Input("lisp>>> ")
			If in = "quit"
				Exit
			ElseIf in <> ""
				Try
					_main.call in
				Catch e:LispError
					Print e.ToString()
				End Try
			EndIf
		Forever
		Return env
	End Function
	
	Function RunFiles:SchemeEnv(files:String[], env:SchemeEnv = Null)
		Global read:TDelegate = TDelegate.Make(Read), eval:TDelegate = TDelegate.Make(Eval)
		If env = Null Then env = SchemeEnv.MakeGlobal()
		Local ev:TDelegate = eval.curry(env)
		For Local file:String = EachIn files
			Local port:LispPort = LispPort.Make("READMODE", file)
			ConsList.Map(ev, SchemeBuiltins._readAll(ConsList.Cons(port, Null)).vals)
			port.stream.Close() ; port.stream = Null
		Next
		Return env
	End Function
End Type

Type SchemeEnv
	Field _local:TMap, _closure:SchemeEnv
	Method Copy:SchemeEnv()
		Local c:SchemeEnv = Make()
		c._local = _local.Copy() ; If _closure Then c._closure = _closure.Copy()
		Return c
	End Method
	Function Make:SchemeEnv(closure:SchemeEnv = Null)
		Local e:SchemeEnv = New SchemeEnv ; e._local = CreateMap() ; e._closure = closure ; Return e
	End Function
	Function MakeGlobal:SchemeEnv()
		Return Make(SchemeBuiltins.prims.Copy())
	End Function
	Function IsBound:Int(env:SchemeEnv, name:String)
		If env = Null Then Return 0
		Return env._local.Contains(name) Or IsBound(env._closure, name)
	End Function
	Function GetVar:LispVal(env:SchemeEnv, name:String)
		If env = Null Then LispError.Raise "cannot get undefined variable '" + name + "'"
		Local val:Object = env._local.ValueForKey(name) ; If val = Null Then val = GetVar(env._closure, name)
		Return LispVal(val)
	End Function
	Function SetVar:LispVal(env:SchemeEnv, name:String, val:LispVal)
		If env = Null Then LispError.Raise "cannot set undefined variable '" + name + "'"
		If Not env._local.Contains(name) Then SetVar env._closure, name, val Else env._local.Insert name, val
		Return val
	End Function
	Function DefineVar:LispVal(env:SchemeEnv, name:String, val:LispVal)
		env._local.Insert(name, val) ; Return val
	End Function
	Function BindVars:SchemeEnv(env:SchemeEnv, bindings:ConsList)
		Global addBinding:TDelegate = TDelegate.Make(_)
		Function _:SchemeEnv(env:SchemeEnv, binding:Object[])
			DefineVar env, String(binding[0]), LispVal(binding[1]) ; Return env
		End Function
		Return SchemeEnv(ConsList.FoldL(addBinding.curry(env), env, bindings))
	End Function
End Type

Type LispError
	Field msg:String
	Function Raise(msg:String)
		Local e:LispError = New LispError ; e.msg = msg ; Throw e
	End Function
	Function ArgCount(expect:Int, got:ConsList)
		Local temp:LispLIst = New LispList ; temp.vals = got	'For printing
		Raise "wrong number of arguments: expected " + expect + ", received actual arguments " + temp.ToString()
	End Function
	Function TypeMismatch(expect:String, got:LispVal)
		Raise "wrong argument type: expected value of type " + expect + ", received actual value " + got.ToString()
	End Function
	Method ToString:String()
		Return "Scheme interpreter error: " + msg
	End Method
End Type


' "SchemeParser.bmx":
'=====================

Type SchemeLexer
	Function Get:TLexer()
		Function R:TLexRule(r:String, a(l:TLexer), res:String = "", m:String = "")
			Return TLexRule.Create(r, a, res, m)
		End Function
		Global Store(_:TLexer) = TLexAction.Store, Mode(_:TLexer) = TLexAction.Mode, Discard(_:TLexer) = TLexAction.Discard
		
		Const SYM:String = "!$%&|*+-/:<=>?^_~~"
		
		Global l:TLexer = TLexer.withRules([..
			R("(\+|-)?[0-9]+", Store, "LispNum"),..	'Simple int
			R("#[bBoOdDxX][0-9a-fA-F]+", Store, "LispNum"),..	'Specific-base int, binary/octal/decimal/hex (style: #xABC12)
			R("(\+|-)?[0-9]*\.[0-9]+([eE]-?[0-9][0-9]*)?", Store, "LispNum"),..	'Float, simple or scientific
			R("(#t|#f)", Store, "LispBool"),..	'Boolean
		..
			R("~q([^~q]|\\~q)*~q", Store, "LispString"),..
			R("#\\([\(\)\[\],\.'`~q#@"+SYM+"]|([a-zA-Z]+))", Store, "LispChar"),..	'Character constant
		..
			R(";[^\n]*\n", Discard),..			'Line comment: ; B3D-style
		..
			R("\(", Store, "lparen"),..		'Punctuation
			R("\)", Store, "rparen"),..
			R("'",  Store, "quote"),..
			R("`",  Store, "backquote"),..
			R(",",  Store, "comma"),..
			R(",@", Store, "splice"),..
			R("\.", Store, "dot"),..
			R("(#)", Store, "hash"),..
		..
			R("[a-z"+SYM+"][a-z0-9@"+SYM+"]*", Store, "LispAtom"),..
		..
		..	'Obvious lex-time errors:
			R("[^[:space:]]", TLexAction.Error, "unrecognised character"),..		'Any other printable character
			R("[0-9]+[a-z_]", TLexAction.Error, "invalid identifier/number")..
		])
		
		l.SetCaseSensitivity False
		l.SetGuardMode True
		Return l
	End Function
End Type

Type SchemeParser Extends TMetaParser Final
	Field grammar:TMap {..
		Prog = "Expr* : @program"..
		Expr = "%LispAtom | %LispNum | %LispBool | %LispChar | %LispString | List | Dotted | Vector | Quoted | QQuote | UnQuote | Splice"..
		List = "%lparen Expr* %rparen : ~ @elems ~"..
		Dotted = "%lparen ! Expr+ %dot Expr %rparen : ~ @elems ~ @last ~"..
		Vector = "%hash ! %lparen Expr* %rparen : ~ ~ @elems ~"..
		Quoted = "%quote ! Expr : ~ @expr"..
		QQuote = "%backquote ! Expr : ~ @expr"..
		UnQuote = "%comma Expr : ~ @expr"..
		Splice = "%splice Expr : ~ @expr"..
	}
	
	Function ToLispVals:LispVal[](ptree:TParseNode)
		If ptree.elem And (ptree.rule = "" Or ptree.rule = "Prog")
			Local vals:LispVal[] = New LispVal[ptree.elem.Length]
			For Local e:Int = 0 Until vals.Length
				vals[e] = ToLispVal(ptree.elem[e])
			Next
			Return vals
		Else
			Return [ToLispVal(ptree)]
		EndIf
	End Function
	
	Function ToLispVal:LispVal(ptree:TParseNode)
		Select ptree.rule
			Case "List"
				If ptree.elem = Null And ptree.term = Null Then Return LispList.Nil		'()
				Local pEl:TParseNode[] = ptree.GetElem("elems").elem, vals:LispVal[] = New LispVal[pEl.Length]
				If pEl = Null Then Return LispList.Make([ToLispVal(ptree.GetElem("elems"))])	'Single-element
				For Local e:Int = 0 Until pEl.Length
					vals[e] = ToLispVal(pEl[e])
				Next
				Return LispList.Make(vals)
				
			Case "Dotted"
				Local pEl:TParseNode[] = ptree.GetElem("elems").elem, vals:LispVal[] = New LispVal[pEl.Length]
				If pEl = Null
					vals = [ToLispVal(ptree.GetElem("elems"))]
				Else
					For Local e:Int = 0 Until pEl.Length
						vals[e] = ToLispVal(pEl[e])
					Next
				EndIf
				Local last:LispVal = ToLispVal(ptree.GetElem("last"))
				Return LispDottedList.Make(vals, last)
				
			Case "Vector"
				Local pEl:TParseNode[] = ptree.GetElem("elems").elem, el:LispVal[] = New LispVal[pEl.Length]
				For Local e:Int = 0 Until el.Length
					el[e] = ToLispVal(pEl[e])
				Next
				Return LispVector.Make(el)
				
			Case "Quoted" ; Return wrap("quote", ToLispVal(ptree.GetElem("expr")))
			Case "QQuote" ; Return wrap("quasiquote", ToLispVal(ptree.GetElem("expr")))
			Case "UnQuote" ; Return wrap("unquote", ToLispVal(ptree.GetElem("expr")))
			Case "Splice" ; Return wrap("unquote-splicing", ToLispVal(ptree.GetElem("expr")))
		End Select
		Function wrap:LispVal(cmd:String, qval:LispVal) Return LispList.Make([LispVal(LispAtom.Make(cmd)), qval]) End Function
		
		Local term:TToken = ptree.term
		If term = Null Then Return LispBool._False
		Select term.tType
			Case "LispAtom" ; Return LispAtom.Make(term.value)
			Case "LispBool" ; Return LispBool.Make(term.value = "#t")
			Case "LispString"
				Local s:String = term.value
				Return LispString.Make(s[1..s.Length - 1].Replace("\n", "~n").Replace("\~q", "~q"))
			Case "LispChar"
				Local ch:String = term.value[2..]
				If ch.Length = 1 Then Return LispChar.Make(ch[0])
				Select ch
					Case "newline" ; LispChar.Make(10)
					Case "space" ; LispChar.Make(32)
					Case "tab" ; LispChar.Make(9)
					Default LispChar.Make(" "[0])'Throw
				End Select
			Case "LispNum" ; Return LispNum.Make(Double(term.value))
		End Select
	End Function
End Type


' "SchemeTypes.bmx":
'====================

Type LispVal
	Function Is:Object(o:Object) Abstract
End Type
Type LispAtom Extends LispVal
	Field name:String
	Function Make:LispAtom(n:String)
		Local a:LispAtom = New LispAtom ; a.name = n.ToLower() ; Return a
	End Function
	Method ToString:String() Return name End Method
	Method Compare:Int(with:Object) Return name.Compare(with) End Method
	Function Is:Object(o:Object) Return LispAtom(o) End Function
End Type
Type LispList Extends LispVal
	Field vals:ConsList
	Global Nil:LispList = LispList.Make(Null)
	Function Make:LispList(vals:LispVal[], _: LispVal = Null)
		Local l:LispList = New LispList ; l.vals = ConsList.FromArray(vals) ; Return l
	End Function
	Function FromCons:LispList(c:ConsList, _:LispVal = Null)
		If c = Null Then Return Nil
		Local l:LispList = New LispList ; l.vals = c ; Return l
	End Function
	Method ToString:String()
		If vals = Null Then Return "()"
		Local show:TDelegate = TDelegate.Make(_show), join:TDelegate = TDelegate.Make(_join)
		Local l2:ConsList = ConsList.Map(show, vals)
		Return "(" + String(ConsList.FoldL(join, l2.val, l2.nx)) +")"
		Function _show:String(o:Object) Return o.ToString() End Function
		Function _join:String(l:String, r:String) Return l + " " + r End Function
	End Method
	Method SendMessage:Object(msg:Object, ctx:Object)
		If msg = RefCell.GetCons Then Return vals Else Return Null
	End Method
	Function Is:Object(o:Object) Return LispList(o) End Function
End Type
Type LispDottedList Extends LispList
	Field last:LispVal
	Function Make:LispList(vals:LispVal[], last:LispVal)
		If LispDottedList(last)
			vals :+ LispVal[](ConsList.ToArray(LispDottedList(last).vals))
			last = LispDottedList(last).last
		ElseIf LispList(last)
			Return LispList.Make(vals + LispVal[](ConsList.ToArray(LispList(last).vals)))
		EndIf
		Local l:LispDottedList = New LispDottedList ; l.vals = ConsList.FromArray(vals) ; l.last = last ; Return l
	End Function
	Function FromCons:LispList(c:ConsList, last:LispVal)
		Local l:LispDottedList = New LispDottedList ; l.vals = c ; l.last = last ; Return l
	End Function
	Method ToString:String()
		Local ret:String = Super.ToString()
		Return ret[..ret.Length - 1] + " . " + last.ToString() + ")"
	End Method
	Function Is:Object(o:Object) Return LispDottedList(o) End Function
End Type
Type LispNum Extends LispVal
	Field val:Double
	Function Make:LispNum(v:Double)
		Local n:LispNum = New LispNum ; n.val = v ; Return n
	End Function
	Method ToString:String()
		If Double(Long(val)) = val Then Return String(Long(val)) Else Return String(val)
	End Method
	Function Is:Object(o:Object) Return LispNum(o) End Function
End Type
Type LispString Extends LispVal
	Field val:String
	Function Make:LispString(v:String)
		Local s:LispString = New LispString ; s.val = v ; Return s
	End Function
	Method ToString:String()
		Return "~q" + (val.Replace("~n", "\n").Replace("~q", "\~q")) + "~q"
	End Method
	Function Is:Object(o:Object) Return LispString(o) End Function
End Type
Type LispBool Extends LispVal
	Field val:Int
	Global _False:LispBool = LispBool.Make(0), _True:LispBool = LispBool.Make(1)
	Function Make:LispBool(v:Int)
		Local b:LispBool = New LispBool ; b.val = (v <> 0) ; Return b
	End Function
	Method ToString:String()
		If val Then Return "#t" Else Return "#f"
	End Method
	Function Is:Object(o:Object) Return LispBool(o) End Function
End Type
Type LispChar Extends LispVal
	Field val:Int
	Function Make:LispChar(v:Int)
		Local c:LispChar = New LispChar ; c.val = v ; Return c
	End Function
	Method ToString:String()
		If val > 32
			Return "#\" + Chr(val)
		Else
			If val = 32 Return "#\space" ElseIf val = 10 Then Return "#\newline" ElseIf val = 9 Then Return "#\tab"
		EndIf
	End Method
	Function Is:Object(o:Object) Return LispChar(o) End Function	
End Type
Type LispVector Extends LispVal
	Field elems:LispVal[]
	Function Make:LispVector(el:LispVal[])
		Local v:LispVector = New LispVector ; v.elems = el ; Return v
	End Function
	Method ToString:String()
		Local s:String = "#("
		For Local v:LispVal = EachIn elems
			s :+ v.ToString() + " "
		Next
		Return s[..s.Length - 1] + ")"
	End Method
	Function Is:Object(o:Object) Return LispVector(o) End Function
End Type

Type LispFunc Extends LispVal
	Field args:ConsList, vararg:String, body:ConsList, closure:SchemeEnv
	Function Make:LispFunc(args:ConsList, vararg:String, body:ConsList, closure:SchemeEnv)
		Local f:LispFunc = New LispFunc
		Function _:String(o:Object) Return o.ToString() End Function ; Global toS:TDelegate = TDelegate.Make(_)
		f.args = ConsList.Map(toS, args)
		f.vararg = vararg ; f.body = body ; f.closure = closure
		Return f
	End Function
	Method ToString:String()
		Local s:String = "(lambda ("
		If args Then s :+ ConsList.FoldL1(TDelegate.Make(_), args).ToString()	'Could be a LispVal or a String
		Function _:String(l:LispVal, r:LispVal)
			Return l.ToString() + " " + r.ToString()
		End Function
		If vararg <> "" Then s :+ " . " + vararg
		Return s + ") ...)"
	End Method
	Function Is:Object(o:Object) Return LispFunc(o) End Function
End Type
Type LispPrimitiveFunc Extends LispVal
	Field f:TDelegate
	Function Make:LispPrimitiveFunc(d:TDelegate)
		Local f:LispPrimitiveFunc = New LispPrimitiveFunc ; f.f = d ; Return f
	End Function
	Method ToString:String() Return "<primitive>" End Method
	Function Is:Object(o:Object) Return LispPrimitiveFunc(o) End Function
End Type

Type LispDeferredTailCall Extends LispVal
	Field env:SchemeEnv, val:LispVal
	Function Make:LispDeferredTailCall(env:SchemeEnv, val:LispVal)
		Local tc:LispDeferredTailCall = New LispDeferredTailCall
		tc.env = env ; tc.val = val ; Return tc
	End Function
	Function Is:Object(o:Object) Return LispDeferredTailCall(o) End Function
End Type

Type LispPort Extends LispVal
	Field stream:TStream, cached:LispVal[]
	Global StdIn:LispPort = LispPort.FromStream(StandardIOStream), StdOut:LispPort = LispPort.FromStream(StandardIOStream)
	Function Make:LispPort(mode:String, path:String)
		Local p:LispPort = New LispPort
		Select mode
			Case "READMODE" ; p.stream = ReadStream(path)
			Case "WRITEMODE" ; p.stream = WriteStream(path)
		End Select
		If p.stream = Null Then LispError.Raise "Unable to open file '" + path + "'"
		Return p
	End Function
	Function FromStream:LispPort(str:TStream)
		Local p:LispPort = New LispPort
		p.stream = str ; Return p
	End Function
	Method Delete()
		If stream Then stream.Close()
	End Method
	Method ToString:String() Return "<IO port>" End Method
	Function Is:Object(o:Object) Return LispPort(o) End Function
End Type

Type LispMacro Extends LispFunc
	Function FromFunc:LispMacro(f:LispFunc)
		Local m:LispMacro = New LispMacro
		m.args = f.args ; m.vararg = f.vararg ; m.body = f.body ; m.closure = f.closure
		Return m
	End Function
	Method ToString:String()
		Return "(macro" + Super.ToString()[7..]
	End Method
	Function Is:Object(o:Object) Return LispMacro(o) End Function
End Type


' "SchemeBuiltins.bmx":
'=======================

Type SchemeBuiltins
	Global unCons:RefCell(l:Object, r:RefCell) = RefCell.unCons, unVal:RefCell(_:Object) = RefCell.unVal, ..
	       unType:RefCell(_:Object, t:Object(_:Object)) = RefCell.unType, unMaybe:RefCell(_:RefCell) = RefCell.unMaybe
	
	Function _add:LispNum(l:LispNum, r:LispNum) Return LispNum.Make(l.val + r.val) End Function
	Function _sub:LispNum(l:LispNum, r:LispNum) Return LispNum.Make(l.val - r.val) End Function
	Function _mul:LispNum(l:LispNum, r:LispNum) Return LispNum.Make(l.val * r.val) End Function
	Function _div:LispNum(l:LispNum, r:LispNum) Return LispNum.Make(l.val / r.val) End Function
	Function _mod:LispNum(l:LispNum, r:LispNum) Return LispNum.Make(l.val Mod r.val) End Function
	
	Function _numEq:LispBool(l:LispNum, r:LispNum) Return LispBool.Make(l.val = r.val) End Function
	Function _numLt:LispBool(l:LispNum, r:LispNum) Return LispBool.Make(l.val < r.val) End Function
	Function _numGt:LispBool(l:LispNum, r:LispNum) Return LispBool.Make(l.val > r.val) End Function
	Function _numNe:LispBool(l:LispNum, r:LispNum) Return LispBool.Make(l.val <> r.val) End Function
	Function _numLe:LispBool(l:LispNum, r:LispNum) Return LispBool.Make(l.val <= r.val) End Function
	Function _numGe:LispBool(l:LispNum, r:LispNum) Return LispBool.Make(l.val >= r.val) End Function
	
	Function _boolAnd:LispBool(l:LispBool, r:LispBool) Return LispBool.Make(l.val And r.val) End Function
	Function _boolOr:LispBool(l:LispBool, r:LispBool) Return LispBool.Make(l.val Or r.val) End Function
	
	Function _strEq:LispBool(l:LispString, r:LispString) Return LispBool.Make(l.val = r.val) End Function
	Function _strLt:LispBool(l:LispString, r:LispString) Return LispBool.Make(l.val.Compare(r.val) < 0) End Function
	Function _strGt:LispBool(l:LispString, r:LispString) Return LispBool.Make(l.val.Compare(r.val) > 0) End Function
	Function _strLe:LispBool(l:LispString, r:LispString) Return LispBool.Make(l.val.Compare(r.val) <= 0) End Function
	Function _strGe:LispBool(l:LispString, r:LispString) Return LispBool.Make(l.val.Compare(r.val) >= 0) End Function
	
	Function NumericOp:LispVal(op:TDelegate, a:ConsList)
		Global isNum:TDelegate = TDelegate.Make(UnpackNum)
		Select True
			Case a = Null, a.nx = Null	'Fewer than 2 args
				LispError.ArgCount 2, a
			Default
				Return LispVal(ConsList.FoldL1(op, ConsList.Map(isNum, a)))
		End Select
	End Function
	
	Function BinaryOp:LispVal(unpack:TDelegate, op:TDelegate, args:ConsList)
		If ConsList.Length(args) <> 2 Then LispError.ArgCount 2, args
		Local l:LispVal = LispVal(unpack.call(args.val))
		Local r:LispVal = LispVal(unpack.call(args.nx.val))
		Return LispVal(op.call2(l, r))
	End Function
	
	Function UnpackNum:LispVal(v:LispVal)
		If LispNum(v) Then Return v Else LispError.TypeMismatch "Number", v
	End Function
	Function UnpackStr:LispVal(v:LispVal)
		If LispString(v) Then Return v Else LispError.TypeMismatch "String", v
	End Function
	Function UnpackBool:LispVal(v:LispVal)
		If LispBool(v) Then Return v Else LispError.TypeMismatch "Boolean", v
	End Function
	
	Function Car:LispVal(a:ConsList)
		If ConsList.Length(a) <> 1 Then LispError.ArgCount 1, a
		Local val:LispVal = LispVal(a.val), RET:RefCell = RefCell.Make()
		Select True
			Case unType(unCons(RET, RefCell.Any), LispDottedList.Is).match(val), ..
			     unType(unCons(RET, RefCell.Any), LispList.Is).match(val)
					Return LispVal(RET._)
			Default ; LispError.TypeMismatch "List", val
		End Select
	End Function
	Function Cdr:LispVal(a:ConsList)
		If ConsList.Length(a) <> 1 Then LispError.ArgCount 1, a
		Local val:LispVal = LispVal(a.val), RET:RefCell = RefCell.Make()
		Select True
			Case unType(unCons(RefCell.Any, RET), LispDottedList.Is).match(val)
				Return LispDottedList.FromCons(ConsList(RET._), LispDottedList(val).last)
			Case unType(unCons(RefCell.Any, Null), LispDottedList.Is).match(val)
				Return LispDottedList(val).last
			Case unType(unCons(RefCell.Any, unMaybe(RET)), LispList.Is).match(val)
				Return LispList.FromCons(ConsList(RET._))
			Default ; LispError.TypeMismatch "List", val
		End Select
	End Function
	Function Cons:LispVal(args:ConsList)
		If ConsList.Length(args) <> 2 Then LispError.ArgCount 2, args
		Local l:LispVal = LispVal(args.val), r:LispVal = LispVal(args.nx.val)
		Select r
			Case Null
				Return LispList.Make([l])
			Case LispDottedList(r)
				Local dl:LispDottedList = LispDottedList(r)
				Return LispDottedList.FromCons(ConsList.Cons(l, dl.vals), dl.last)
			Case LispList(r)
				Return LispList.FromCons(ConsList.Cons(l, LispList(r).vals))
			Default
				Return LispDottedList.Make([l], r)
		End Select
	End Function
	
	Function EqvP:LispVal(args:ConsList)
		If ConsList.Length(args) <> 2 Then LispError.ArgCount 2, args
		Local l:LispVal = LispVal(args.val), r:LispVal = LispVal(args.nx.val)
		If l = r Then Return LispBool._True
		
		Select True
			Case LispBool(l) And LispBool(r)     ;    Return LispBool.Make(LispBool(l).val = LispBool(r).val)
			Case LispNum(l) And LispNum(r)       ;    Return LispBool.Make(LispNum(l).val = LispNum(r).val)
			Case LispString(l) And LispString(r) ;    Return LispBool.Make(LispString(l).val = LispString(r).val)
			Case LispAtom(l) And LispAtom(r)     ;    Return LispBool.Make(LispAtom(l).name = LispAtom(r).name)
			Case LispList(l) And LispList(r)
				Global eqP:TDelegate = TDelegate.Make(eqvPair), zipEq:TDelegate = TDelegate.Make(ConsList.ZipWith).curry(eqP)
				Function eqvPair:LispVal(l:LispVal, r:LispVal)
					Return EqvP(ConsList.Cons(l, ConsList.Cons(r, Null)))
				End Function
				If ConsList.Length(LispList(l).vals) <> ConsList.Length(LispList(r).vals) Then Return LispBool._False
				Local ret:LispVal = LispVal(ConsList.FoldL(eqP, LispBool._True, ..
				                            ConsList(zipEq.call2(LispList(l).vals, LispList(r).vals))))
				If LispDottedList(l) And LispDottedList(r) Then ..
				   ret = eqvPair(ret, eqvPair(LispDottedList(l).last, LispDottedList(r).last))
				Return ret
				
			Case LispVector(l) And LispVector(r)
				Local lv:LispVector = LispVector(l), rv:LispVector = LispVector(r), ret:Int = 1
				If lv.elems.Length <> rv.elems.Length Then Return LispBool._False
				For Local e:Int = 0 Until lv.elems.Length
					ret = ret & LispBool(EqvP(ConsList.Cons(lv.elems[e], ConsList.Cons(rv.elems[e], Null)))).val
				Next
				Return LispBool.Make(ret)
				
			Default ; Return LispBool._False
		End Select
	End Function
	
	Function _apply:LispVal(a:ConsList)
		If ConsList.Length(a) <> 2 Then LispError.ArgCount 2, a
		Return SchemeREPL.Apply(LispVal(a.val), a.nx)
	End Function
	Function _makePort:LispVal(s:String, a:ConsList)
		If ConsList.Length(a) <> 1 Then LispError.ArgCount 1, a
		If Not LispString(a.val) Then LispError.TypeMismatch "String", LispVal(a.val)
		Return LispPort.Make(s, LispString(a.val).val)
	End Function
	Function _closePort:LispVal(a:ConsList)
		If ConsList.Length(a) <> 1 Then LispError.ArgCount 1, a
		Local p:LispPort = LispPort(a.val) ; If Not p Then LispError.TypeMismatch "Port", LispVal(a.val)
		If p.stream Then p.stream.Close() ; p.stream = Null
		Return LispList.Nil
	End Function
	Function _read:LispVal(a:ConsList)
		If ConsList.Length(a) > 1 Then LispError.ArgCount 1, a
		Local p:LispPort = LispPort(a.val) ; If a And Not p Then LispError.TypeMismatch "Port", LispVal(a.val)
		Return SchemeREPL.ReadOne(New SchemeParser, p)
	End Function
	Function _write:LispVal(a:ConsList)
		Local ln:Int = ConsList.Length(a)
		If ln > 2 Or ln < 1 Then LispError.ArgCount 2, a
		Local p:LispPort = LispPort.StdOut
		If ln = 2 Then p = LispPort(a.nx.val) ; If a And Not p Then LispError.TypeMismatch "Port", LispVal(a.val)
		Return SchemeREPL.Write(p, LispVal(a.val))
	End Function
	Function _readContents:LispString(a:ConsList)
		If ConsList.Length(a) <> 1 Then LispError.ArgCount 1, a
		Local p:LispPort = LispPort(a.val) ; If Not p Then LispError.TypeMismatch "Port", LispVal(a.val)
		Local s:String ; While Not Eof(p.stream)
			s :+ p.stream.ReadLine() + "~n"
		Wend
		Return LispString.Make(s)
	End Function
	Function _readAll:LispList(a:ConsList)
		Return LispList.Make(SchemeREPL.Read(New SchemeParser, _readContents(a).val))
	End Function
	Function _print:LispVal(a:ConsList)
		If ConsList.Length(a) <> 1 Then LispError.ArgCount 1, a
		Local s:String = a.val.ToString()
		If LispString(a.val) Then Print s[1..s.Length - 1] Else Print s
		Return LispList.Nil
	End Function
	
	Global prims:SchemeEnv = SchemeBuiltins._init()
	Function _init:SchemeEnv()
		Local prims:SchemeEnv = SchemeEnv.Make()
		Function addPrim(env:SchemeEnv, name:String, f:TDelegate)
			SchemeEnv.DefineVar env, name, LispPrimitiveFunc.Make(f)
		End Function
		
		Local numericBinop:TDelegate = TDelegate.Make(NumericOp)
		addPrim prims, "+", numericBinop.curry(TDelegate.Make(_add))
		addPrim prims, "-", numericBinop.curry(TDelegate.Make(_sub))
		addPrim prims, "*", numericBinop.curry(TDelegate.Make(_mul))
		addPrim prims, "/", numericBinop.curry(TDelegate.Make(_div))
		addPrim prims, "mod", numericBinop.curry(TDelegate.Make(_mod))
		
		Local binop:TDelegate = TDelegate.Make(SchemeBuiltins.BinaryOp)
		Local numBoolBinop:TDelegate = binop.curry(TDelegate.Make(UnpackNum))
		Local strBoolBinop:TDelegate = binop.curry(TDelegate.Make(UnpackStr))
		Local boolBoolBinop:TDelegate = binop.curry(TDelegate.Make(UnpackBool))
		
		addPrim prims, "=", numBoolBinop.curry(TDelegate.Make(_numEq))
		addPrim prims, "<", numBoolBinop.curry(TDelegate.Make(_numLt))
		addPrim prims, ">", numBoolBinop.curry(TDelegate.Make(_numGt))
		addPrim prims, "/=", numBoolBinop.curry(TDelegate.Make(_numNe))
		addPrim prims, "<=", numBoolBinop.curry(TDelegate.Make(_numLe))
		addPrim prims, ">=", numBoolBinop.curry(TDelegate.Make(_numGe))
		addPrim prims, "&&", boolBoolBinop.curry(TDelegate.Make(_boolAnd))
		addPrim prims, "||", boolBoolBinop.curry(TDelegate.Make(_boolOr))
		addPrim prims, "string=?", strBoolBinop.curry(TDelegate.Make(_strEq))
		addPrim prims, "string<?", strBoolBinop.curry(TDelegate.Make(_strLt))
		addPrim prims, "string>?", strBoolBinop.curry(TDelegate.Make(_strGt))
		addPrim prims, "string<=?", strBoolBinop.curry(TDelegate.Make(_strLe))
		addPrim prims, "string>=?", strBoolBinop.curry(TDelegate.Make(_strGe))
		
		addPrim prims, "car", TDelegate.Make(Car)
		addPrim prims, "cdr", TDelegate.Make(Cdr)
		addPrim prims, "cons", TDelegate.Make(Cons)
		
		addPrim prims, "eqv?", TDelegate.Make(EqvP)
		addPrim prims, "eq?", TDelegate.Make(EqvP)	'eq? and equal? are allowed to be the same as eqv?, so they are
		addPrim prims, "equal?", TDelegate.Make(EqvP)
		
		Local mp:TDelegate = TDelegate.Make(_makePort), cp:TDelegate = TDelegate.Make(_closePort)
		addPrim prims, "open-input-file", mp.curry("READMODE")
		addPrim prims, "open-output-file", mp.curry("WRITEMODE")
		addPrim prims, "close-input-port", cp
		addPrim prims, "close-output-port", cp
		addPrim prims, "read", TDelegate.Make(_read)
		addPrim prims, "write", TDelegate.Make(_write)
		addPrim prims, "read-all", TDelegate.Make(_readAll)
		addPrim prims, "read-contents", TDelegate.Make(_readContents)
		addPrim prims, "print", TDelegate.Make(_print)
		addPrim prims, "apply", TDelegate.Make(_apply)
		
		Return prims
	End Function
End Type
