; ID: 2356
; Author: Warpy
; Date: 2008-11-07 00:22:56
; Title: Regular expressions
; Description: A system for matching regular expressions

Type charset
	Field pattern$
	Field ranges:TList
	
	Method New()
		ranges=New TList
	End Method
	
	Function Create:charset(pattern$)
		If Not pattern Return emptyset
		cs:charset=New charset
		cs.pattern=pattern
		While Len(pattern)
			If Len(pattern)>2 And Chr(pattern[1])="-"
				low=pattern[0]
				high=pattern[2]
				cs.addrange(low,high)
				pattern=pattern[3..]
			Else
				c=pattern[0]
				cs.addrange(c,c)
				pattern=pattern[1..]
			EndIf
		Wend
		Return cs
	End Function
	
	Method addrange(low,high)
		Local range[]=[low,high]
		ranges.addlast range
		pattern:+Chr(low)+"-"+Chr(high)
	End Method
	
	Method match(cr$)
		c=Asc(cr)
		Local range[]
		For range=EachIn ranges
			If c>=range[0] And c<=range[1]
				Return True
			EndIf
		Next
		Return False
	End Method
	
	Method repr$()
		Local range[]
		txt$=""
		For range=EachIn ranges
			If range[0]=range[1]
				txt:+Chr(range[0])
			Else
				txt:+Chr(range[0])+"-"+Chr(range[1])
			EndIf
		Next
		Return txt
	End Method
End Type

Global emptyset:charset=New charset
Global digits:charset=charset.Create("0-9")
Global alphanum:charset=charset.Create("0-9A-Za-z")
Global nonalphanum:charset=New charset
Global nondigits:charset=New charset
Global fullset:charset=New charset

fullset.addrange 0,255

nondigits.addrange 0,47
nondigits.addrange 58,255 
nonalphanum.addrange 0,47
nonalphanum.addrange 58,64
nonalphanum.addrange 91,96
nonalphanum.addrange 123,255



Function listsame(l1:TList,l2:TList)
	If l1.count()<>l2.count() Return False
	
	For o:Object=EachIn l1
		If Not l2.contains(o) Return False
	Next

	Return True
End Function

Type fsa
	Field accepting
	Field transitions:TList
	Field name$
	
	Method New()
		transitions=New TList
	End Method
	
	Method addtransition(symbol$,dest:fsa)
		transitions.addlast transition.Create(symbol,dest)
	End Method
	
	Method matches:TList(symbol$,l:TList=Null)
		If Not l l=New TList
		For t:transition=EachIn transitions
			If t.cs.match(symbol)
				l.addlast t.dest
			EndIf
		Next
		Return l
	End Method
	
	Method evaluate(pattern$,spaces$="")
		Print spaces+name+"?"+pattern
		If Not pattern
			Print "end at "+name
			Return accepting
		EndIf
		symbol$=Chr(pattern[0])
		npattern$=pattern[1..]
		For f:fsa=EachIn matches(symbol)
			If f.evaluate(npattern,spaces+" ") Return True
		Next
		For t:transition=EachIn transitions
			If t.cs=emptyset
				Print spaces+"empty move to "+t.dest.name
				If t.dest.evaluate(pattern,spaces) Return True
			EndIf
		Next
		
		Print spaces+"fail at "+name

		Return False
	End Method
	
	Method emptymoves:TList(l:TList=Null,addself=0)
		If Not l l=New TList
		If addself l.addlast(Self)
		For t:transition=EachIn transitions
			If t.cs=emptyset
				If Not l.contains(t.dest)
					l.addlast t.dest
					t.dest.emptymoves(l)
				EndIf
			EndIf
		Next
		Return l
	End Method
	
	Method repr$()
		txt$=name+"~n"
		If accepting txt:+"accepting~n"
		For t:transition=EachIn transitions
			txt:+"  "+t.repr()+"~n"
		Next
		Return txt
	End Method
	
	Function collapse:tmap(f:fsa,checked:tmap=Null)
		If Not checked checked=New tmap
		If checked.contains(f) Return checked

		ntransitions:tmap=New tmap
		destinations:TList=New TList
		
		For t:transition=EachIn f.transitions
			If Not ntransitions.contains(t.cs.pattern)
				ntransitions.insert t.cs.pattern,New TList
			EndIf
			tl:TList=TList(ntransitions.valueforkey(t.cs.pattern))
			If Not tl.contains(t.dest)
				tl.addlast t.dest
			EndIf
			
			If Not destinations.contains(t.dest)
				destinations.addlast t.dest
			EndIf
		Next
		
		'Rem
		of:fsa=New fsa
		'Print of.name
		For key$=EachIn ntransitions.keys()
			If key
				tl:TList=TList(ntransitions.valueforkey(key))
				tname$=""
				For fp:fsa=EachIn tl
					For f2:fsa=EachIn fp.emptymoves()
						If Not tl.contains(f2)
							tl.addlast f2
						EndIf
					Next
				Next
				For f2:fsa=EachIn tl
					If tname tname:+","
					tname:+f2.name
				Next
				tname="{"+tname+"}"
				'Print "  "+key+" -> "+tname
			EndIf
		Next
		'EndRem
		
		checked.insert f,ntransitions
		
		For f2:fsa=EachIn destinations
			fsa.collapse f2,checked
		Next
		
		Return checked
	End Function
	
	Function powerset:fsa(startnode:fsa)
		Global allnodes:tmap,newnodes:TList
		
		maps:tmap=collapse(startnode)
		
		allnodes:tmap=New tmap
		
		Function findnode:fsa(l:TList,adding=1)
			For l2:TList=EachIn allnodes.keys()
				If listsame(l,l2) Return fsa(allnodes.valueforkey(l2))
			Next
			nf:fsa=New fsa
			nf.name=ziplist(l)
			'Print "new node "+nf.name
			allnodes.insert l,nf
			If adding
				newnodes.addlast l
			EndIf
			Return nf
		End Function
		
		Function ziplist$(l:TList)
			l=l.copy()
			l.sort
			txt$=""
			For f:fsa=EachIn l
				If txt txt:+","
				txt:+f.name
			Next
			Return "{"+txt+"}"
		End Function
		
		outstart:fsa=Null
		newnodes:TList=New TList
		newnodes.addlast startnode.emptymoves(Null,1)
		
		While newnodes.count()
		
			'get the new state we're making. It's a list of the old nodes
			l:TList=TList(newnodes.removefirst()) 
			
			nf:fsa=findnode(l,False)
			If Not outstart outstart=nf
			
			nf.name=ziplist(l)
			'Print "constructing "+nf.name
			
			ntransitions:tmap=New tmap
			
			'for each old node in the list, work out the transitions
			For f:fsa=EachIn l
				If f.accepting nf.accepting=1
			
				'get transition map for this nfsa node
				fnt:tmap=tmap(maps.valueforkey(f))
				
				'for each symbol, add all the destinations
				For key$=EachIn fnt.keys()
					If key
						If Not ntransitions.contains(key)
							ntransitions.insert key,New TList
						EndIf
						otl:TList=TList(fnt.valueforkey(key))
						ntl:TList=TList(ntransitions.valueforkey(key))
						For f2:fsa=EachIn otl
							If Not ntl.contains(f2) ntl.addlast f2
						Next
					EndIf
				Next
			Next
			
			'we now have a full set of transitions, make the node
			For key$=EachIn ntransitions.keys()
				df:fsa=findnode(TList(ntransitions.valueforkey(key)))
				nf.addtransition key,df
			Next
			'Print nf.repr()
		Wend
		
		Print "ALL NODES"
		For f:fsa=EachIn allnodes.values()
			Print f.repr()
		Next
		
		Return outstart
	End Function
	
End Type

Type transition
	Field cs:charset
	Field dest:fsa
	
	Function Create:transition(pattern$,dest:fsa)
		t:transition=New transition
		t.cs=charset.Create(pattern)
		t.dest=dest
		Return t
	End Function
	
	Method repr$()
		Return cs.repr()+" -> "+dest.name
	End Method
End Type


Global numnodes=0
Function compile:fsa[](pattern$,starts:fsa[],spaces$="")
	Print spaces+"compile: "+pattern
	

	Local bits$[]
	Local ends:fsa[],ostarts:fsa[],nends:fsa[]
	bits=splitpipes(pattern)
	
	'note that starts is not really a set of starting nodes for the whole machine,
	'but in fact the previous set of finals.

	If Len(bits)=1
		While Len(pattern)
			ostarts=starts
			symbol$=Chr(pattern[0])
			'Print spaces+">"+symbol
			nends=Null
			Select symbol
			Case "(" 'start brackets
				Print spaces+"brackets"
				inparens=1
				i=1
				While inparens
					Select Chr(pattern[i])
					Case "("
						inparens:+1
					Case ")"
						inparens:-1
					End Select
					i:+1
				Wend
				bit$=pattern[1..i-1]
				ends=compile(bit,starts,spaces+"  ")
				pattern=pattern[i..]
				
			Case "["
				Print spaces+"squares"
				i=1
				While Chr(pattern[i])<>"]"
					i:+1
				Wend
				bit$=pattern[0..i+1]
				'Print spaces+bit
				pattern=pattern[i+1..]
				nf:fsa=New fsa
				For f:fsa=EachIn starts
					f.addtransition bit,nf
				Next
				ends=[nf]
			Case "\"
				symbol=Chr(pattern[1])
				Print "special character "+symbol
				Local cs:charset
				Select symbol
				Case "d" 'digit
					cs=digits
				Case "D"
					cs=nondigits
				Case "w"
					cs=alphanum
				Case "W"
					cs=nonalphanum
				Default
					cs=charset.Create(symbol)
				End Select
				Print cs.repr()
				mf:fsa=New fsa
				mf.name="m"
				nf:fsa=New fsa
				numnodes:+1
				nf.name=String(numnodes)
				For f:fsa=EachIn starts
					tr:transition=New transition
					tr.cs=cs
					tr.dest=mf
					f.transitions.addlast tr
				Next
				mf.addtransition "",nf
				ends=[mf]
				nends=[nf]
				pattern=pattern[2..]
			Case "."
				Print "any character"
				mf:fsa=New fsa
				nf:fsa=New fsa
				mf.name="m"
				numnodes:+1
				nf.name=String(numnodes)
				For f:fsa=EachIn starts
					tr:transition=New transition
					tr.cs=fullset
					tr.dest=mf
					f.transitions.addlast tr
				Next
				mf.addtransition "",nf
				ends=[mf]
				nends=[nf]
				pattern=pattern[1..]
			Default 'normal character
				Print spaces+"character: "+symbol
				mf:fsa=New fsa
				mf.name="m"
				nf:fsa=New fsa
				numnodes:+1
				nf.name=String(numnodes)
				For f:fsa=EachIn starts
					f.addtransition symbol,mf
				Next
				mf.addtransition "",nf
				ends=[mf]
				nends=[nf]
				pattern=pattern[1..]
				
			End Select
			
			If Len(pattern)
				op$=Chr(pattern[0])
			Else
				op$=""
			EndIf
			Select op
			Case "*" 'kleene star closure - none or more times
				fin:fsa=New fsa
				For f:fsa=EachIn ends
					f.addtransition "",fin
				Next
				For f:fsa=EachIn starts
					f.addtransition "",fin
					fin.addtransition "",f
					If nends
						For f2:fsa=EachIn nends
							f.addtransition "",f2
						Next
					EndIf
				Next
				fin2:fsa=New fsa
				fin.addtransition "",fin2
				ends=[fin2]
				pattern=pattern[1..]
			Case "?" 'zero or one times
				fin:fsa=New fsa
				For f:fsa=EachIn starts
					f.addtransition "",fin
					If nends
						For f2:fsa=EachIn nends
							f.addtransition "",f2
						Next
					EndIf
				Next
				For f:fsa=EachIn ends
					f.addtransition "",fin
				Next
				ends=[fin]
				pattern=pattern[1..]
			Case "+" 'one or more times
				fin:fsa=New fsa
				For f:fsa=EachIn ends
					f.addtransition "",fin
				Next
				For f:fsa=EachIn starts
					fin.addtransition "",f
				Next
				ends=[fin]
				pattern=pattern[1..]
			End Select
			If nends
				ends=nends
			EndIf
			starts=ends
		Wend
	Else
		Print spaces+"pipes"
		For bit$=EachIn bits
			ends:+compile(bit,starts,spaces+"  ")
		Next
	EndIf
	
	Return ends
End Function

Function splitpipes$[](pattern$)
	'Print pattern
	inparens=0
	i=0
	Local bits$[0]
	starti=0
	While i<Len(pattern)
		Select Chr(pattern[i])
		Case "\"
			i:+1
		Case "("
			inparens:+1
		Case ")"
			inparens:-1
		Case "|"
			If Not inparens
				bits:+[pattern[starti..i]]
				starti=i+1
			EndIf
		End Select
		i:+1
	Wend
	If starti<Len(pattern)
		bits=bits+[pattern[starti..]]
	EndIf
	
	Return bits
End Function

start:fsa=New fsa
start.name="s"
're$="(([1-9]+[0-9]*)|0)(.[0-9]+)?"
're$="[a-z]*(,? [a-z]*)*"
re$=".*"
're$=Input("re> ")
For f:fsa=EachIn compile(re,[start])
	f.accepting=1
Next
start=fsa.powerset(start)


in$=""
While in<>"quit"
	in$=Input(">")
	If start.evaluate(in)
		Print "Yes"
	Else
		Print "No"
	EndIf
Wend
