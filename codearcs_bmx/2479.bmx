; ID: 2479
; Author: Warpy
; Date: 2009-05-13 17:37:17
; Title: filter a list using reflection
; Description: get all objects in a list satisfying a condition

Rem
filter lists!

usage:
the filter function works on any list or tquery object, and takes a condition string and returns a tquery which is a collection of all the objects in the list satisfying the condition
conditions are pretty much as you write them in blitzmax, except I haven't made it do function calls yet

EndRem


'shunting yard algorithm - turns normal notation into RPN for easier parsing
Type shuntingyard
	Field in$
	Field out$
	Field ops$[][]
	Field token$[]
	
	Method parse$()
		While in
			nexttoken()
			Select token[0]
			Case "literal"
				output
			Case "function"
				push
			Case ","
				While ops[0][0]<>"("
					pop
					If token[0]<>"(" output
				Wend
			Case "("
				push
			Case ")"
				While pop()<>"("
					output
				Wend
				If Len(ops) And ops[0][0]="function"
					pop
					output
				EndIf
			Default
				If token[0][..2]="op"
					Local otoken$[]=token
					op=Int(token[0][2..])
					While Len(ops) And ops[0][0][..2]="op" And Int(ops[0][0][2..])<op
						pop
						output
					Wend
					token=otoken
					push
				EndIf
			End Select
		Wend
		While Len(ops)
			pop
			output
		Wend
		Return out
	End Method
	
	Method findnexttoken$()
		i=0
		While i<Len(in) And in[i]=32
			i:+1
		Wend
		in=in[i..]
		i=0
		
		While i<Len(in)
			Select Chr(in[i])
			Case "!","&","|","=","<",">","(",")",","
				If i=0
					t$=Chr(in[0])
					in=in[1..]
					Return t
				Else
					t$=in[..i]
					in=in[i..]
					Return t
				EndIf
			Case " "
				t$=in[..i]
				in=in[i..]
				Return t
			End Select
			i:+1
		Wend
		t$=in
		in=""
		Return t
	End Method
	
	Method nexttoken()
		tok$=findnexttoken()
		'Print "TOK: "+tok
		Select tok
		Case "!","not"
			token=["op2",tok]
		Case "&","|","and","or"
			token=["op3",tok]
		Case "=","<",">"
			token=["op1",tok]
		Case "("
			token=["(","("]
		Case ")"
			token=[")",")"]
		Case ","
			token=[",",","]
		Default
			If Len(in) And Chr(in[0])="("
				token=["function",tok]
				'in=in[1..]
			Else
				token=["literal",tok]
			EndIf
		End Select
		
		'Print token[0]+" : "+token[1]
	End Method
				
	Method pop$()
		token=ops[0]
		ops=ops[1..]
		Return token[0]
	End Method
	
	Method push()
		ops=[token]+ops
	End Method	
	
	Method output()
		out:+token[1]+" "
	End Method
End Type

Function shunt$(in$)
	s:shuntingyard=New shuntingyard
	s.in=in
	Return s.parse()
End Function




'the important bit! call this with a list or a tquery, and a condition
'if you pass in a tquery, the new condition will be added to any previous ones.
Function filter:tquery(o:Object,condition$)
	condition=Trim(shunt(condition))
	'Print condition
	Local bits$[]=condition.split(" ")
	Local stack:TList=New TList
	While i<Len(bits)
		Select bits[i]
		Case "!","not"
			c:tcondition=tcondition(stack.removelast())
			stack.addlast notcondition.Create(c)
		Case "&","and"
			c1:tcondition=tcondition(stack.removelast())
			c2:tcondition=tcondition(stack.removelast())
			stack.addlast andcondition.Create(c1,c2)
		Case "|","or"
			c1:tcondition=tcondition(stack.removelast())
			c2:tcondition=tcondition(stack.removelast())
			stack.addlast orcondition.Create(c1,c2)
		Case "="
			value$=String(stack.removelast())
			fields$=String(stack.removelast())
			'Print fields+" = "+value
			stack.addlast eqcondition.Create(fields,value)
		Case "<"
			value$=String(stack.removelast())
			fields$=String(stack.removelast())
			'Print fields+" = "+value
			stack.addlast ltcondition.Create(fields,Double(value))
		Case ">"
			'Print "EQUAL"
			value$=String(stack.removelast())
			fields$=String(stack.removelast())
			'Print fields+" = "+value
			stack.addlast gtcondition.Create(fields,Double(value))
		Default
			stack.addlast bits[i]
		End Select			
		i:+1
	Wend
	c:tcondition = tcondition(stack.removelast())
	If tquery(o)
		Return tquery(o).addcondition(c)
	ElseIf TList(o)
		q:tquery=New tquery
		q.data=TList(o)
		q.condition=c
		Return q
	EndIf
End Function

'object enumerator type for tquery
Type queryenum
	Field obj:Object
	Field l:TLink
	Field query:tquery
	
	Function Create:queryenum(q:tquery)
		qe:queryenum=New queryenum
		qe.query=q
		qe.l=q.data.firstlink()
		Return qe
	End Function
	
	Method nextobject:Object()
		If obj
			d:Object=obj
			obj=Null
			Return d
		Else
			Return findnext()
		EndIf
	End Method
	
	Method hasnext()
		If obj
			Return True
		Else
			Return findnext()<>Null
		EndIf
	End Method
	
	Method findnext:Object()
		While l
			d:Object=l.value()
			l=l.nextlink()
			If query.contains(d)
				obj=d
				Return obj
			EndIf
		Wend
		Return Null
	End Method
End Type

'represents a condition to be applied to a list of objects
Type TQuery
	Field condition:tcondition
	Field data:TList
	
	Method objectenumerator:queryenum()
		qe:queryenum=queryenum.Create(Self)
		Return qe
	End Method
	
	Method contains(d:Object)
		'If Not data.contains(d) Return false
		If Not condition Return True
		Return condition.appliesto(d)
	End Method
	
	Method addcondition:tquery(c:tcondition)
		qe:tquery=New tquery
		qe.data=data
		If condition
			qe.condition=andcondition.Create(condition,c)
		Else
			qe.condition=c
		EndIf
		Return qe
	End Method
End Type

Type tcondition
	Method appliesto(d:Object) Abstract
End Type

Type andcondition Extends tcondition
	Field c1:tcondition,c2:tcondition
	
	Function Create:andcondition(c1:tcondition,c2:tcondition)
		ac:andcondition=New andcondition
		ac.c1=c1
		ac.c2=c2
		Return ac	
	End Function
	
	Method appliesto(d:Object)
		If c1.appliesto(d) And c2.appliesto(d)
			Return True
		Else
			Return False
		EndIf
	End Method
End Type

Type orcondition Extends tcondition
	Field c1:tcondition,c2:tcondition
	
	Function Create:orcondition(c1:tcondition,c2:tcondition)
		oc:orcondition=New orcondition
		oc.c1=c1
		oc.c2=c2
		Return oc	
	End Function
	
	Method appliesto(d:Object)
		If c1.appliesto(d) Return True
		If c2.appliesto(d) Return True
		Return False
	End Method
End Type

Type notcondition Extends tcondition
	Field c:tcondition
	
	Function Create:notcondition(c:tcondition)
		oc:notcondition=New notcondition
		oc.c=c
		Return oc	
	End Function
	
	Method appliesto(d:Object)
		Return (Not c.appliesto(d))
	End Method
End Type

Type fieldcondition Extends tcondition
	Field fields$[]
	
	Method getfield:Object(d:Object)
		i=0
		While i<Len(fields)
			d=TTypeId.ForObject(d).findField(fields[i]).get(d)
			i:+1
		Wend
		Return d
	End Method
End Type	

Type eqcondition Extends fieldcondition
	Field value$
	
	Function Create:eqcondition(fields$,value$)
		ec:eqcondition=New eqcondition
		ec.fields=fields.split(".")
		ec.value=value
		Return ec
	End Function

	Method appliesto(d:Object)
		'Print " ".join(fields)+" = "+value+"? "+(String(getfield(d))=value)
		Return String(getfield(d))=value
	End Method
End Type

Type ltcondition Extends fieldcondition
	Field value:Double
	Function Create:ltcondition(fields$,value:Double)
		lc:ltcondition=New ltcondition
		lc.fields=fields.split(".")
		lc.value=value
		Return lc
	End Function

	Method appliesto(d:Object)
		Return Double(String(getfield(d)))<value
	End Method
End Type

Type gtcondition Extends fieldcondition
	Field value:Double
	Function Create:gtcondition(fields$,value:Double)
		gc:gtcondition=New gtcondition
		gc.fields=fields.split(".")
		gc.value=value
		Return gc
	End Function

	Method appliesto(d:Object)
		Return Double(String(getfield(d)))>value
	End Method
End Type





'example

Type dude
	Field name$
	Field number
	Function Create:dude(name$,number)
		d:dude=New dude
		d.name=name
		d.number=number
		Return d
	End Function
End Type

l:TList=New TList
l.addlast dude.Create("Jim",1)
l.addlast dude.Create("Mike",2)
l.addlast dude.Create("Bubba",3)

Print "DUDES~n------------"
For d:dude=EachIn l
	Print d.number+": "+d.name
Next
Print "------------~n"

Print "LAZY EVALUATION"
condition$="number=1"
q:tquery=filter(l,condition)
Print "dudes where "+condition
For d:dude=EachIn q
	Print d.name
Next
Print "~n(add 1: Wanda)~n"
l.addlast dude.Create("Wanda",1)
Print "dudes where "+condition
For d:dude=EachIn q
	Print d.name
Next

Print "~n~nnow you try!~n"
While 1
	condition$=Input("condition> ")
	'Print condition
	q:tquery=filter(l,condition)
	For d:dude=EachIn q
		Print d.number+": "+d.name
	Next
Wend
