; ID: 2805
; Author: Yasha
; Date: 2011-01-02 07:44:20
; Title: Lambda Calculus
; Description: Lazy-evaluating Lambda Calculus Interpreter

'Lazy Lambda Calculus Interpreter

Framework brl.StandardIO
Import brl.Retro

SuperStrict

Local code:String = LoadText("prog.txt")		'Your code here

'	Working example programs, increasing complexity:
'"let y = 7 ~n let x = 8 in x y"
'"(\x.(\y.x y))5 7"
'"if (- 1 1) (* 2 3) (- 9 2)"
'"do (print 1) (print 2) (print 3)"
'"(\f.f(f 9))((\x.x)(\x.x))"
'"let true = \x y.x in let false = \x y.y in true (false 6(true 1 3)) 8"
'"let true = \x y.x~nlet false = \x y.y~nlet if = \p a b.p a b~nif false (false 1 2) (true 3 4)"
'"let true = \x y.x~nlet false = \x y.y~nlet and = \p q.p q p~nand true false"
'"let t=\x y.x~nlet f=\x y.y~nlet not=\p.p f t~nnot f"
'"let t=\x y.x~nlet f=\x y.y~nlet or=\p q.p p q~nlet and=\p q.p q p~nor (and t f) (and t t)"
'"let t=\x y.x~nlet f=\x y.y~nlet if=\p a b.p a b~nlet not=\p.p f t~n(\p.if p (f p (not p)) p) t"
'"let t=\x y.x~nlet f=\x y.y~nlet zero=\f x.x~nlet succ=\n f x.f(n f x)~nlet is0=\n.n(\x.f)t~nis0 (succ zero)"
'"let t=\x y.x~nlet f=\x y.y~nlet if=\p a b.p a b~nlet not=\p.p f t~nlet Y = \G.(\g.G(g g))(\g.G(g g))~nY (\f p.if p (f (not p)) p) t"
'"let Y = \G.(\g.G(g g))(\g.G(g g))~nY (\f p.if p (f (- p 1)) p) 2"
'"let Y = \G.(\g.G(g g))(\g.G(g g))~nlet F = \f.\n. if(eq? n 0) 1 (* n (f(- n 1)))~nY F 10"

Print code + "~n"

Local e:Expression = Parse(code, Print, GlobalEnv())
If e <> Null
	PrintParseTree e
	Print "~nResult:"
	Print Evaluate(e, DebugLog).ToString()
EndIf

Print "~n...done!"



Function Parse:Expression(code:String, errFunc(e:String), gEnv:Variable)		'Parse an input string into a tree
	Local e:Expression = Null
	
	Try
		Local s:Source, t:Expression, defs:TList = New TList
		code = Trim(code)
		If code.Length
			s = Source.Make(code)	'Appends one ~n, to ensure we can end easily
			e = Expression.Make(gEnv, False)
			While s.c < s.code.Length
				t = ParseExpression(s, "~n", gEnv, False)	'Note gEnv - updates if vars are defined
				If t <> Null	'Ignore null expressions... they're just blank lines
					If t.isDef
						If e.body.Count() Then LambdaError.Error "Cannot define variable after expression has begun", s.getLine()
						defs.AddFirst(t) ; gEnv = t.env		'Shuffle the environments back round
					Else
						e.AddTerm(t)	'Add as expression term
					End If
				EndIf
			Wend
			
			If e.body.Count() = 0 Then LambdaError.Error "No expression to evaluate!"
			
			For t = EachIn defs		'If any local variables were defined, wrap the expression in their lambda forms
				e.env = t.env ; t.env = t.env.env		'We know that e isn't a lambda, here
				t.body.AddFirst(e) ; e.ttype = Term.isLAM ; e = t
			Next
		EndIf
	Catch err:LambdaError
		If err.line
			errFunc "Error on line " + err.line + ": " + err.MSG
		Else
			errFunc "Error: " + err.MSG
		EndIf
		e = Null
	End Try
	
	Return e
	
	Function ParseExpression:Expression(s:Source, terminator:String, env:Variable, isDef:Int)
		Local e:Expression, c:String, braceLevel:Int = 0, token:String = "", defs:TList = New TList, d:Expression
		
		e = Expression.Make(env, isDef)
		While s.c < s.code.Length
			c = s.getChr()
			
			If braceLevel = 0
				Select c
					Case ";"	'Comment
						While s.c < s.code.Length
							c = s.getChr() ; If c = "~n" Then s.c:-1; Exit	'Backup, the newline might be important
						Wend
					Case terminator
						If token.Length Then e.AddToken(token, s.getLine()) ; token = ""	'Don't forget a token if there was no separator
						If e.body.Count() = 0
							If terminator = "~n" Then Return Null Else LambdaError.Error "Expression must have content", s.getLine()
						Else
							Exit
						EndIf
					Case ")"	'If terminator wasn't )
						LambdaError.Error "Mismatched parentheses", s.getLine()
					Case "="
						LambdaError.Error "Unexpected character: ~q=~q", s.getLine()
					Case "{"
						If token.Length Then e.AddToken(token, s.getLine()) ; token = ""
						braceLevel = 1
					Case "}"
						LambdaError.Error "Mismatched braces", s.getLine()
					Case "("
						If token.Length Then e.AddToken(token, s.getLine()) ; token = ""
						d = ParseExpression(s, ")", e.env, False)		'e.env not env
						If d.isDef
							If e.body.Count() Then LambdaError.Error "Cannot define local variable after expression has begun", s.getLine()
							defs.AddFirst(d) ; e.env = d.env	'Shuffle the environments back round
						Else
							e.AddTerm(d)	'Add as expression term
						End If
					Case "\"
						If token.Length Then e.AddToken(token, s.getLine()) ; token = ""
						If e.body.Count()
							e.AddTerm ParseLambda(s, terminator, e.env, isDef)	'e.env not env
						Else
							e = ParseLambda(s, terminator, env, isDef)		'Simplify...
						EndIf
						Exit	'The expression has to end with the end of the lambda - lambda body already ate the terminator
					Default
						If c[0] > 32			'Build token
							token:+c
						ElseIf token.Length		'Whitespace
							If token = "let"		'Name definition
								If e.body.Count() Then LambdaError.Error "Cannot define variable in middle of expression", s.getLine()
								d = ParseLet(s, terminator, e.env) ; token = ""		'Note: e.env not env
								If d.isDef = 2	'If it's applied to this expression only with "in"
									defs.AddFirst(d) ; e.env = d.env	'Don't exit - doesn't escape
								Else
									e = d ; Exit
								EndIf
							ElseIf token = "in"		'End of name definition
								If isDef
									e.isDef = 2	'Mark that we ended on "in"
									If e.body.Count() = 0 Then LambdaError.Error("Empty definition", s.getLine()) Else Exit
								Else
									LambdaError.Error "~qin~q without ~qlet~q", s.getLine()
								End If
							Else
								e.AddToken(token, s.getLine())	'Unknown term type - check whether it's a value, a variable, or an error
								token = ""
							EndIf
						End If
				End Select
			Else
				If c = "{" Then braceLevel:+1 Else If c = "}" Then bracelevel:-1
				If braceLevel
					token:+c		'Don't add the final } if it reached zero
				Else
					e.AddTerm(Value.Make(token))', e.env))
					token = ""
				EndIf
			EndIf
		Wend
		
		If s.c >= s.code.Length		'Reached the end of input?
			If braceLevel Then LambdaError.Error "Mismatched braces: did not close", s.getLine()
			If terminator[0] > 32 Then LambdaError.Error "Incomplete expression: expecting ~q" + terminator + "~q to close", s.getLine()
		EndIf
		
		For d = EachIn defs		'If any local variables were defined, wrap the expression in their lambda forms
			If e.ttype = Term.isEXP Then e.env = d.env Else e.env.env = d.env		'Lambdas need special treatment
			d.env = d.env.env
			d.body.AddFirst(e) ; d.isDef = e.isDef
			e.ttype = Term.isLAM ; e = d
		Next
		
		Return e
	End Function
	
	Function ParseLambda:Expression(s:Source, terminator:String, env:Variable, isDef:Int)
		Local token:String = "", c:String
		
		While s.c < s.code.Length
			c = s.getChr()
			Select c
				Case ";"	'Comment
					While s.c < s.code.Length
						c = s.getChr() ; If c = "~n" Then s.c:-1; Exit	'Backup, the newline might be important
					Wend
				Case "(", ")", terminator, "{", "}", "\", "="		'Note that newline is OK if parenthesised
					LambdaError.Error "Expecting parameter name; found control character ~q" + c + "~q", s.getLine()
				Case "."
					If token.Length
						Exit
					Else
						LambdaError.Error "Expecting parameter name; found control character ~q" + c + "~q", s.getLine()
					EndIf
				Default
					If c[0] > 32			'Build token
						token:+c
					ElseIf token.Length		'Whitespace
						Exit
					End If
			End Select
		Wend
		Local l:Expression = Expression.Make(Variable.Make(token, env), isDef) ; l.ttype = Term.isLAM
		
		If c <> "."		'If we haven't had the start character yet, skip whitespace
			While s.c < s.code.Length
				c = s.getChr()
				If c[0] > 32
					If c[0] <> 46 Then s.c:-1		'Backup if not dot - the next character is probably important
					Exit
				EndIf
			Wend
		EndIf
		If s.c = s.code.Length Then LambdaError.Error "Body of lambda abstraction not found!", s.getLine()
		
		Local b:Expression
		If c = "."
			b = ParseExpression(s, terminator, l.env, isDef)
			If b.env = l.env		'Only store the whole thing if it's a full lambda
				l.body = b.body ; l.isDef = b.isDef
			Else
				l.body.AddFirst(b)
			EndIf
		Else		'Listing two or more parameters is literally read the same way as nesting the lambdas
			b = ParseLambda(s, terminator, l.env, isDef)
			l.isDef = b.isDef ; l.body.AddFirst b		'Push the lambda as only term
		EndIf
		If l.body = Null Then LambdaError.Error "Expecting body for lambda abstraction", s.getLine()
		
		Return l
	End Function
	
	Function ParseLet:Expression(s:Source, terminator:String, env:Variable)
		Local token:String = "", c:String
		
		While s.c < s.code.Length
			c = s.getChr()
			Select c
				Case ";"	'Comment
					While s.c < s.code.Length
						c = s.getChr() ; If c = "~n" Then s.c:-1; Exit	'Backup, the newline might be important
					Wend
				Case "(", ")", terminator, "{", "}", "\", "."
					LambdaError.Error "Expecting parameter name; found control character ~q" + c + "~q", s.getLine()
				Case "="
					If token.Length
						Exit
					Else
						LambdaError.Error "Expecting parameter name; found control character ~q" + c + "~q", s.getLine()
					EndIf
				Default
					If c[0] > 32			'Build token
						token:+c
					ElseIf token.Length		'Whitespace
						Exit
					End If
			End Select
		Wend
		Local n:Variable = Variable.Make(token, env)
		
		If c <> "="		'If we haven't had the definition character yet, skip whitespace
			While s.c < s.code.Length
				c = s.getChr()
				If c = "=" Then Exit
			Wend
		EndIf
		If s.c = s.code.Length Then LambdaError.Error "Expecting definition for variable ~q" + token + "~q", s.getLine()
		
		Local d:Expression = ParseExpression(s, terminator, env, True)	'The var definition - note its env is not v
		If d = Null Then LambdaError.Error "Expecting definition for ~qlet " + n.name + "~q = ..."
'		If isREPL Then n.def = d	'This isn't circular if done right
		Local l:Expression = Expression.Make(n, d.isDef)
		If d.ttype = Term.isEXP And d.body.Count() = 1 Then l.body.AddFirst(d.body.First()) Else l.body.AddFirst(d)
		
		Return l
	End Function
End Function

Function PrintParseTree(t:Term, indent:Int = 0, nlev:Int = 0)	'Print a parsed expression tree to output
	Local elem:Term
	
	Select True
		Case Term.isBIN = t.ttype		'Comes before isVAL and isLAM as it has those flags too
			rPrint Builtin(t).name, indent
		Case (Term.isEXP & t.ttype) > 0		'Note that lambdas also have isVal set, so this comes first
			rPrint t.ttype + ": expr " + Expression(t).id + " (level " + nlev + ", " + Expression(t).env.ToString() + "):", indent
			For elem = EachIn Expression(t).body
				PrintParseTree elem, indent + 4, nlev + 1
			Next
			rPrint "(~~expr " + Expression(t).id + " level " + nlev + ")", indent
		Case (Term.isVAL & t.ttype) > 0
			rPrint Value(t).sval, indent
		Case (Term.isVAR & t.ttype) > 0
			rPrint Variable(t).name, indent
	End Select
	
	Function rPrint(txt:String, indent:Int)
		Print RSet("", indent) + txt
	End Function
End Function

Function Evaluate:Term(t:Term, errFunc(e:String))	'Evaluate a parsed expression tree (non-recursive)
	Try
		Local eStack:TList = New TList ; eStack.AddFirst(t)		'Use a secondary stack (prevent overflow of call stack)
		
		While (Term(eStack.Last()).ttype & Term.isVAL) = False	'While the bottom of the stack is a var or expression
			Local e:Expression
			t = Term(eStack.RemoveFirst())	'Pop stack
			
			If t.ttype & Term.isVAL		'Unapplied lambdas and literal values
				e = Expression(eStack.First())
				If e = Null Or e.ttype <> Term.isEXP Then LambdaError.Error "Unexpected error - missing expression"
				e.body.AddFirst(t)
					
			Else		'Expressions
				e = Expression(t) ; If e.mutable = False Then e = e.Copy()	'This one might not fire often
				
				Local fst:Term = Term(e.body.First()), snd:Term
				If fst = Null Then LambdaError.Error "Unexpected error - empty expression"
				
				Select fst.ttype
					Case Term.isVAL
						eStack.AddFirst(fst)	'If it's a pure value, just return it
						
					Case Term.isVAR
						LambdaError.Error "Unexpected error - unsubstituted variable ~q" + fst.ToString() + "~q"
						
					Case Term.isEXP				'Still arguments to apply?
						If e.body.FirstLink() <> e.body.LastLink() Then e.body.RemoveFirst() ; eStack.AddFirst(e)
						eStack.AddFirst(fst)
						
					Case Term.isLAM
						Local l:Expression = Expression(fst)
						
						If e.body.FirstLink() = e.body.LastLink()	'No arguments, so just return the lambda
							If l.mutable = False Then l = l.Copy()
							eStack.AddFirst(l)
							
						Else	'Apply the lambda to the argument
							e.body.RemoveFirst() ; snd = Term(e.body.RemoveFirst())
							l = l.Apply(snd)	'Application always creates a copy of the function being applied
							
							If e.body.First() <> Null		'If there are any other arguments in this expression
								eStack.AddFirst(e)	'Put the expression back
							Else
								e.body.AddFirst(l)
							EndIf
							eStack.AddFirst(l)
						End If
					
					Case Term.isBIN
						Local b:Builtin = Builtin(fst)
						
						If e.body.FirstLink() = e.body.LastLink()	'No arguments, so just return the value
							eStack.AddFirst(b)
							
						Else	'Apply the function to the argument
							e.body.RemoveFirst() ; snd = Term(e.body.RemoveFirst())
							snd = b.Apply(snd)	'Store result in snd, which may be a curried copy of itself
							
							If e.body.First() <> Null		'If there are any other arguments in this expression
								eStack.AddFirst(e)	'Put the expression back
							Else
								e.body.AddFirst(snd)
							EndIf
							eStack.AddFirst(snd)
						End If
				End Select
			EndIf
		Wend
		
		Return Term(eStack.RemoveLast())	'Eventual expression value
	Catch err:LambdaError
		If errFunc <> Null Then errFunc "Error: " + err.MSG Else Throw err
	End Try
End Function

Type Source
	Field code:String
	Field multiLine:Int
	Field c:Int
	
	Function Make:Source(code:String)
		Local s:Source = New Source
		s.c = 0; s.multiLine = code.Contains("~n")	'Don't give line numbers for a single-line expression
		s.code = code + "~n"
		Return s
	End Function
	
	Method getChr:String()
		c:+1
		Return Chr(code[c - 1])
	End Method
	
	Method getLine:Int()	'Get the line number of the current character
		Local i:Int, l:Int = 1	'Start on line 1
		For i = 0 To c - 1
			If code[i] = 10 Then l:+1	'Count the newline characters before c
		Next
		Return l * multiLine
	End Method
End Type

Type Term Abstract
	Const isVAL:Int = 1, isVAR:Int = 2, isEXP:Int = 4, isLAM:Int = 1 + 4, isBIN:Int = 1 + 4 + 8
	Field ttype:Int
	Field env:Variable		'Argument, or evaluation context (depending on code)
End Type

Type Value Extends Term
	Field sval:String, lval:Long, dval:Double
	
	Function Make:Value(token:String)', env:Variable)	'Make a value literal object
		Local v:Value = New Value
		v.ttype = Term.isVAL
		v.sval = token ; v.lval = token.ToLong() ; v.dval = token.ToDouble()
		'v.env = env		'Err... does this still do anything? I forget
		Return v
	End Function
	
	Method ToString:String()
		Return sval
	End Method
End Type

Type Variable Extends Term
	Global uIDCount:Long
	Field name:String, uniqueID:Long, def:Term
	
	Method New()
		ttype = Value.isVAR
	End Method
	
	Function Make:Variable(name:String, env:Variable)
		Local v:Variable = New Variable
		v.name = name
		v.env = env
		v.uniqueID = uIDCount		'Do we even need this? Don't think so
		uIDCount:+1		'Honestly I can't be bothered to come up with a "more permanent" solution than this
		Return v
	End Function
	
	Method ToString:String()
		Return "var ~q" + name + "~q[" + String.FromLong(uniqueID) + "]"
	End Method
	
	Function GetByName:Variable(name:String, env:Variable)
		While env <> Null
			If env.name = name Then Return env
			env = env.env
		Wend
		Return Null
	End Function
	
	Function GetByUID:Variable(uID:Long, env:Variable)
		While env <> Null
			If env.uniqueID = uID Then Return env
			env = env.env
		Wend
		Return Null
	End Function
End Type

Type Expression Extends Term
	Field body:TList		'List of terms that makes up the expression
	Field isDef:Int			'1|2 if this is a var definition (helpful for rearranging things), 2 if it ended on "in"
	Field mutable:Int		'True if this is safe to evaluate in place
	Field inScope:Int		'True if this expression is in its original location and can have substitutions made
	
	Field id:Int		'Debug purposes only - provides a recognisable ID, as all functions are nameless
	Global uniquerefid:Int	'Similarly
	
	Method New()
		ttype = isEXP
		body = New TList
		mutable = False
		inScope = True
	End Method
	
	Function Make:Expression(env:Variable, isDef:Int)
		Local e:Expression = New Expression
		e.env = env
		e.isDef = isDef
		uniquerefid:+1 ; e.id = uniquerefid		'DEBUG - safe to remove if not desired
		Return e
	End Function
	
	Method AddToken(t:String, lNo:Int = 0)	'Undetermined token that may be a variable name, a value, or an error
		Local v:Variable = Variable.GetByName(t, env)
		If v = Null
			If t.ToLong() Or t.ToDouble()	'Nonzero number
				AddTerm(Value.Make(t))', env))
			ElseIf t.Contains("0")	'First char is 0, or is $/%/-/. and then 0
				If t[0] = 48 Or ((t[0] = 36 Or t[0] = 37 Or t[0] = 45 Or t[0] = 46) And t[1] = 48)
					AddTerm(Value.Make(t))', env))
				Else
					LambdaError.Error "Unrecognised variable name: ~q" + t + "~q", lNo
				EndIf
			Else
				LambdaError.Error "Unrecognised variable name: ~q" + t + "~q", lNo
			EndIf
		Else		'Variable, either defined or builtin
			If v.def <> Null And v.def.ttype = Term.isBIN Then AddTerm(v.def) Else AddTerm(v)
		End If
	End Method
	
	Method AddTerm(t:Term)
		body.AddLast(t)
	End Method
	
	Method Copy:Expression()		'Perform a shallow copy of the expression object and term list
		Local c:Expression = New Expression
		c.ttype = ttype ; c.mutable = True ; c.inScope = inScope ; c.isDef = isDef
		c.env = env ; c.body = body.Copy()
		c.id = id	'Debug line (safe to remove)
		Return c
	End Method
	
	Method Apply:Expression(arg:Term)		'This is now where substitution happens
		Local l:Expression
		
		l = Copy()	'l must always be unique at this step or errors could result
		If arg.ttype & Term.isEXP And arg.ttype <> Term.isBIN
			arg = Expression(arg).Copy()	'Make sure it's a copy
			Expression(arg).inScope = False
		EndIf
		
		l.ttype = Term.isEXP
		l.Subst(l.env, arg)		'Replace all references to l.env with arg within the body and nested expressions
		Return l
	End Method
	
	Method Subst(v:Variable, t:Term)		'Go through the termlist and replace a variable with an argument
		Local elem:Term, newBody:TList = New TList
		
		For elem = EachIn body
			If elem.ttype = Term.isVAR
				If Variable(elem).uniqueID = v.uniqueID Then elem = t
			ElseIf elem.ttype & Term.isEXP And elem.ttype <> Term.isBIN
				Local sub:Expression = Expression(elem).Copy()	'Copy every expression term regardless, for safety
				If sub.inScope Then sub.Subst(v, t) ; elem = sub
			EndIf
			
			newBody.AddLast(elem)	'Building a new list is cleaner than editing the old one in-place
		Next
		
		body = newBody
	End Method
	
	Method ToString:String()
		If ttype = Term.isLAM Then Return "Lambda " + id + " (" + env.ToString() + ")"	'This is actually enough to ID a lambda
		Return "Expression " + id + " (" + env.ToString() + ")"	'For an expr, not so much, but meh
	End Method
End Type

Type Builtin Extends Term		'Builtin functionality for extra speed or convenience (or IO, side-effects, etc.)
	Field arity:Int, aCount:Int, name:String	'Note that builtin functions may not have optional parameters
	Field applied:Term[], lazy:Int
	Field func:Term(args:Term[])		'The BlitzMax function to call
	
	Method New()
		ttype = Term.isBIN
		aCount = 0
	End Method
	
	Function Make:Builtin(func:Term(args:Term[]), arity:Int, name:String = "", lazy:Int = False)
		Local b:Builtin = New Builtin
		b.arity = arity
		b.func = func
		b.applied = New Term[arity]
		b.name = name		'This is only important for printing the parse tree or similar tasks
		b.lazy = lazy
		Return b
	End Function
	
	Method Copy:Builtin()
		Local c:Builtin = New Builtin
		c.arity = arity ; c.func = func ; c.name = name
		c.aCount = aCount ; c.lazy = lazy
		c.applied = applied[..]
		Return c
	End Method
	
	Method Apply:Term(arg:Term)		'Note that this creates a copy every time it's incompletely applied
		If arg.ttype & Term.isEXP Then arg = Expression(arg).Copy()
		If aCount < arity - 1	'Incomplete application
			Local b:Builtin = Copy()
			b.applied[aCount] = arg
			b.aCount:+1
			Return b
		Else		'Complete - evaluate instead
			Local args:Term[] = applied[..], i:Int
			args[aCount] = arg
			If lazy = False
				For i = 0 To arity - 1
					args[i] = Evaluate(args[i], Null)
				Next
			EndIf
			Return func(args)
		EndIf
	End Method
End Type


Type LambdaError
	Field msg:String
	Field line:Int
	
	Function Error(msg:String, line:Int = 0)
		Local err:LambdaError = New LambdaError
		err.line = line
		err.msg = msg
		Throw err
	End Function
End Type


Function GlobalEnv:Variable()		'This is the place to add user-defined functions
	Global gEnv:Variable
	
	If gEnv <> Null Then Return gEnv	'Cache this so we don't rebuild the same list every time
	
	gEnv = AddBuiltin("*", Multiply, 2, gEnv)		'Names can be pretty much anything - operators are mostly fine
	gEnv = AddBuiltin("-", Subtract, 2, gEnv)
	gEnv = AddBuiltin("eq?", Equality, 2, gEnv)
	gEnv = AddBuiltin("+", lAdd, 2, gEnv)
	gEnv = AddBuiltin("if", lIf, 3, gEnv, True)
	gEnv = AddBuiltin("print", lPrint, 1, gEnv)		'Build a one-way list on gEnv
	gEnv = AddBuiltin("do", lDo, 1, gEnv)
	
	gEnv = Variable.Make("Global Top Level", gEnv)	'Outermost level
	
	Return gEnv
	
	'Use this to add functions - all must have this signature
	Function AddBuiltin:Variable(name:String, func:Term(args:Term[]), arity:Int, env:Variable, lazy:Int = False)
		Local v:Variable = Variable.Make(name, env)
		v.def = Builtin.Make(func, arity, name, lazy)
		Return v
	End Function
	
	'Some simple ones
	Function Multiply:Term(args:Term[])		'Multiply two integers (for factorial demo)
		Return Value.Make(String.FromLong(Value(args[0]).lval * Value(args[1]).lval))
	End Function
	
	Function Subtract:Term(args:Term[])		'Difference of two integers (for factorial demo)
		Return Value.Make(String.FromLong(Value(args[0]).lval - Value(args[1]).lval))
	End Function
	
	Function Equality:Term(args:Term[])		'Compare two integers (for factorial demo)
		Return Value.Make(String.FromLong(Value(args[0]).lval = Value(args[1]).lval))
	End Function
	
	Function lAdd:Term(args:Term[])			'Sum of two integers (for fibonacci demo)
		Return Value.Make(String.FromLong(Value(args[0]).lval + Value(args[1]).lval))
	End Function
	
	Function lIf:Term(args:Term[])			'A definition of If that accepts and returns an int like in BlitzMax
		Local pred:Term = Evaluate(args[0], Null)	'Note that If is lazy and therefore evaluates args only now
		If Value(pred).lval
			Return Evaluate(args[1], Null)
		Else
			Return Evaluate(args[2], Null)
		EndIf
	End Function
	
	Function lPrint:Term(args:Term[])		'Print a value to output
		Print Value(args[0]).sval
		Return args[0]	'Just returns itself
	End Function
	
	Function lDo:Term(args:Term[])		'Execute a a list of expressions imperatively
		Global this:Term
		If this = Null Then this = Builtin.Make(lDo, 1)
		Return this		'Since the argument was already evaluated by Apply, all it has to do is return itself
	End Function		'and it can continue to execute any number of commands
End Function
