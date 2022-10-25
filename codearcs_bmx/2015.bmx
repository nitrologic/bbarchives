; ID: 2015
; Author: Leon Drake
; Date: 2007-05-16 22:28:38
; Title: In game scripting (UPDATED!)
; Description: Want to write in game scripts?

Rem
******************************************************************************************
Ingame Scripting Lib v0.1.20
Written by:Leon Drake a.k.a Landon Ritchie
Free for Commercial Use! Enjoy!!

Few bugs i need to let you know about in the scripting side.

Currently Everything is case sensitive so when writing scripts keep everything lowercase
I'll be fixing that soon. Also make sure everything is spaced. My tokenizer is still just 
a baby so if you put in var3=var1+var2 , it will think that whole block is a value so use
val3 = val1 + val2

you have to declare all variables and the types. Example var int var1
you must do the same for any functions you wish to use. var function myfunc

I havent added the ability to cast parameters to functions yet, so dont put a () after 
your function when calling it. use myfunc instead of myfunc()

note on if statements. I'm pretty sure i got it to run right so far if tested it and it
seems to return if statements fine. however curently you can only use math operators before
it compares the IF. for example

if myval + myval2 = myval3 then

works, but...

if myval = myval2 + myval3 then

doesn't work at the moment. probably best to assign that stuff before you call an if, for
now anyways. You can use and & or in your if statement and its not limited on how complex 
that part may be. you can also use else in your if statement. so far it works good.



I'll try to keep updating this as often as i can , which should be often enough since i'm
using this code for a 2d rpg engine.


Just added the ability to write select statements

works pretty much the same as blitz 

usage

select myval3

case 100

case 200

default

endselect

you can also put select statements inside other select statements and it works fine

select myval3

case 100
	select myval4
	
		case 20
		
	end select

default

endselect

added for loops ..yet to be upgraded its still very touchy it must be run with a variable
like so:

for myvar3 = 0 to 100

next

you can however replace the 0 or the 100 with variables. doesnt have to start with 0 or
end with 100 i was merely using that as an example.


Added new function 
callfunction scriptname functionname

What this does is allows you to call a function from a different active script. meaning you
have already used runscript on that script. which makes the script active and its functions
available. Use call function from another script to run a function from a different script
using the other scripts variables and what not. Also you dont have to worry about 2 scripts
running using the same variable names i made sure it differentiates them.

Added new function 
callvariable scriptname variablename
Just like callfunction this allows you to access the contents of a variable in another 
active script. So far its only usage is for variable assignment. you cant use it in if
statements or anything else like that so you'll have to pre grab the variable contents 
before hand like this:

myvar = callvariable myscript varname

you can also do this if you want to 

myvar = callvariable myscript varname + callvariable myscript varname2
and that works with all the math operators i've added so far.


******************************************************************************************
End Rem
Import "bin\bbtype.bmx"
Import "bin\bbvkey.bmx"
Global declare_list:TList = New TList
Global dtokens_list:TList = New TList
Global commands_list:TList = New TList
Global arguement_list:TList = New TList
Global ifbool_list:TList = New TList
Global ftokens_list:TList = New TList
Global script_list:TList = New TList

'token types
Const OPERATORS$ = "*/+-=><"


Type script Extends TBBType

	Method New()
		Add(script_list)
	End Method

	Method After:script()
		Local t:TLink
		t=_link.NextLink()
		If t Return script(t.Value())
	End Method

	Method Before:script()
		Local t:TLink
		t=_link.PrevLink()
		If t Return script(t.Value())
	End Method


Field scriptname$,scripthandle$,scriptdata$
End Type

Type ifbool Extends TBBType

	Method New()
		Add(ifbool_list)
	End Method

	Method After:ifbool()
		Local t:TLink
		t=_link.NextLink()
		If t Return ifbool(t.Value())
	End Method

	Method Before:ifbool()
		Local t:TLink
		t=_link.PrevLink()
		If t Return ifbool(t.Value())
	End Method


Field bool,boolexp,parentscrpt:script
End Type

Type arguement Extends TBBType

	Method New()
		Add(arguement_list)
	End Method

	Method After:arguement()
		Local t:TLink
		t=_link.NextLink()
		If t Return arguement(t.Value())
	End Method

	Method Before:arguement()
		Local t:TLink
		t=_link.PrevLink()
		If t Return arguement(t.Value())
	End Method


Field arguementtype$,argvalue$,argnextoperator$,expecting$,parentscrpt:script
End Type
 
Type commands Extends TBBType

	Method New()
		Add(commands_list)
	End Method

	Method After:commands()
		Local t:TLink
		t=_link.NextLink()
		If t Return commands(t.Value())
	End Method

	Method Before:commands()
		Local t:TLink
		t=_link.PrevLink()
		If t Return commands(t.Value())
	End Method


Field commandname$,commandtype
End Type

Type declare Extends TBBType

	Method New()
		Add(declare_list)
	End Method

	Method After:declare()
		Local t:TLink
		t=_link.NextLink()
		If t Return declare(t.Value())
	End Method

	Method Before:declare()
		Local t:TLink
		t=_link.PrevLink()
		If t Return declare(t.Value())
	End Method


Field dtype$,ddata$,dataarray$[999],dname$,parentscript$,pscript,sindex,parentscrpt:script
End Type

Type dtokens Extends TBBType

	Method New()
		Add(dtokens_list)
	End Method

	Method After:dtokens()
		Local t:TLink
		t=_link.NextLink()
		If t Return dtokens(t.Value())
	End Method

	Method Before:dtokens()
		Local t:TLink
		t=_link.PrevLink()
		If t Return dtokens(t.Value())
	End Method


Field dname$,parentscript$,pscript,index,line,tokentype,parentfunction:declare,parentscrpt:script
End Type



Type ftokens Extends TBBType

	Method New()
		Add(ftokens_list)
	End Method

	Method After:ftokens()
		Local t:TLink
		t=_link.NextLink()
		If t Return ftokens(t.Value())
	End Method

	Method Before:ftokens()
		Local t:TLink
		t=_link.PrevLink()
		If t Return ftokens(t.Value())
	End Method


Field dname$,parentscript$,pscript,index,line,tokentype,parentfunction:declare,parentscrpt:script
End Type

loadcommands()

tokenizescript()
tokenizescript("myscript2","ASDFGH","script2.txt")
processtokens()

runscript()
runscript("myscript2","ASDFGH")

Function runscript(scriptname$="myscript",scripthandle$="QWERTYUIOP")
Local tok:dtokens,com:commands,scr:script
scr = getscript:script(scriptname$,scripthandle$)
If scr = Null Then
If SCRIPTDEBUG = True Then
Notify "invalid script name"
EndIf
Return
EndIf
tok = gettoken:dtokens(scr)
Repeat
'For tok:dtokens = EachIn dtokens_list
Print tok.dname$+" object? "

If tok = Null Then Return

'If tok2 <> Null And tok = tok2 Continue
'For com:commands = EachIn commands_list
com = checkcommands:commands(tok.tokentype)
If com <> Null Then

tok = processcommands(tok:dtokens,com:commands)
'Exit
EndIf

If tok <> Null Then
tok = tok.After()
EndIf
Until tok = Null Or tok.parentscrpt <> scr

'Next


End Function

Function getscript:script(scriptname$,scripthandle$)
For scr:script = EachIn script_list
If scr.scriptname$ = scriptname$ And scr.scripthandle$ = scripthandle$ Then
Return scr
EndIf
Next
Return Null
End Function




Function gettoken:dtokens(scr:script)


For tok:dtokens = EachIn dtokens_list
If tok.parentscrpt = scr Then
Return tok
EndIf
Next
Return Null
End Function


Function getarguement:arguement(obj:Object)
For arg:arguement = EachIn arguement_list
If arg = obj Then
Return arg
EndIf
Next
Return Null
End Function


Function getifbool:ifbool(obj:Object)
For ifo:ifbool = EachIn ifbool_list
If ifo = obj Then
Return ifo
EndIf
Next
Return Null
End Function

Function processcommands:dtokens(tok:dtokens,com:commands)

Select com.commandtype

Case 0
dtype$ = checktoken$(tok)
	Select dtype$
	
	Case ""
	If SCRIPTDEBUG = True Then
	Notify "Sorry have no idea what this is Line:"+tok.line
	EndIf
	'Print "I dunno what that is"
	Return Null
	
	'Case "function"
	'tok = runfunction(tok)
	'Return tok
	
	Default
	Print "Let's assign a var"
	tok = assignvar(tok)
	Return tok
	
	End Select


Case 1
tok = declarevar(tok)
Return tok


Case 5
tok = runifstate(tok)
Return tok

Case 14
tok = Printfunc(tok)
Return tok


Case 15
tok = runselectstate(tok)
Return tok

Case 21
tok = runforloop(tok)
Return tok

Case 24
tok = callfunction(tok)
Return tok

End Select

End Function


Function callvariable$(tok:dtokens)
Local scr:script,oldtok:dtokens,tokdata$,dec:declare,dnameo$
tok = tok.After()
If tok = Null Then
	If SCRIPTDEBUG = True Then
	Notify "Unexpected End of File"
	EndIf
'Print "unexpected End of file"
Return Null
EndIf
dnameo$ = tok.dname$
tok = tok.After()
If tok = Null Then
'Print "unexpected End of file"
	If SCRIPTDEBUG = True Then
	Notify "Unexpected end of file"
	EndIf
Return Null
EndIf

scr = getscript:script(dnameo$,tok.dname$)
If scr = Null Then
'Print "Source Script does not exist"
	If SCRIPTDEBUG = True Then
	Notify "Source Script does not exist line:"+tok.line
	EndIf
Return Null
EndIf
tok = tok.After()
If tok = Null Then
'Print "unexpected End of file"
	If SCRIPTDEBUG = True Then
	Notify "Unexpected End of File"
	EndIf
Return Null
EndIf
oldtok = tok
tok:dtokens = New dtokens
tok.dname$ = oldtok.dname$
tok.index = 0
tok.line = 0
tok.parentscrpt = scr
dec = finddeclaration:declare(tok)
If dec = Null Then
	If SCRIPTDEBUG = True Then
	Notify "Variable does not exist Line:"+oldtok.line
	EndIf
'Print "Variable does not exist"
Return ""
EndIf
dtokens_list.remove tok
tok.Remove()
tok = oldtok
Return dec.ddata$
End Function




Function callfunction:dtokens(tok:dtokens)
Local scr:script,oldtok:dtokens,dnameo$

tok = tok.After()
If tok = Null Then
'Print "unexpected End of file"
	If SCRIPTDEBUG = True Then
	Notify "Unexpected end of file"
	EndIf
Return Null
EndIf
dnameo$ = tok.dname$
tok = tok.After()
If tok = Null Then
'Print "unexpected End of file"
	If SCRIPTDEBUG = True Then
	Notify "Unexpected end of file "+tok.line
	EndIf
Return Null
EndIf

scr = getscript:script(dnameo$,tok.dname$)
If scr = Null Then
'Print "Source Script does not exist"
	If SCRIPTDEBUG = True Then
	Notify "Source Script does not exist Line:"+tok.line
	EndIf
Return Null
EndIf
tok = tok.After()
If tok = Null Then
'Print "unexpected End of file"
	If SCRIPTDEBUG = True Then
	Notify "Unexpected end of file"
	EndIf
Return Null
EndIf
Print "CALLING FUNCTION "+tok.dname$
oldtok = tok
tok:dtokens = New dtokens
tok.dname$ = oldtok.dname$
tok.index = 0
tok.line = 0
tok.parentscrpt = scr

tok = runfunction(tok)
dtokens_list.remove tok
tok.Remove()
tok = oldtok
Return tok
End Function


Function runforloop:dtokens(tok:dtokens)

Local com:commands,loopvar,valuebegin$,valueend$
Local firstok:dtokens,lastok:dtokens
Local dec:declare,dec2:declare,dec3:declare
tok = tok.After()
dec = finddeclaration:declare(tok)
If dec = Null
'Print "Must be variable in for loop"
	If SCRIPTDEBUG = True Then
	Notify "Missing Variable after For, Line:"+tok.line
	EndIf
Return Null
EndIf
tok = tok.After()
If tok.dname$ <> "=" Then
'Print "Expecting = after For (VAR)"
	If SCRIPTDEBUG = True Then
	Notify "Expecting operator after For Variable, Line:"+tok.line
	EndIf
Return Null
EndIf
tok = tok.After()
dec2 = finddeclaration:declare(tok)
If dec2 = Null Then
Print "not a variable must be a value"
valuebegin$ = tok.dname$
Else
valuebegin$ = dec2.ddata$
EndIf
tok = tok.After()
If tok.dname$ <> "to" Then
'Print "expecting to after (VAR)"
	If SCRIPTDEBUG = True Then
	Notify "Expecting To, Line:"+tok.line
	EndIf
Return Null
EndIf
tok = tok.After()
dec3 = finddeclaration:declare(tok)
If dec3 = Null Then
Print "not a variable must be a value"
valueend$ = tok.dname$
Else
valueend$ = dec3.ddata$
EndIf
tok = tok.After()
firstok = tok
dec.ddata$ = Int(valuebegin$)
Repeat

					'For tok:dtokens = EachIn dtokens_list
					Print tok.dname$+" object? "
					
					If tok = Null Then Return Null
					
					'If tok2 <> Null And tok = tok2 Continue
					'For com:commands = EachIn commands_list
					com = checkcommands:commands(tok.tokentype)
					If com <> Null Then
					
					tok = processcommands(tok:dtokens,com:commands)
					'Exit
					EndIf
					If tok = Null Then Return Null
					If tok <> Null Then
					tok = tok.After()
					EndIf
					If tok.dname$ = "next" And Int(dec.ddata$) < Int(valueend$) Then
					dec.ddata$ = Int(dec.ddata$) + 1
					tok = firstok
					EndIf

					Until tok.dname$ = "next" And Int(dec.ddata$) = Int(valueend$)
			Return tok		
End Function

Function runselectstate:dtokens(tok:dtokens)
Local valcompare$,valtype$,tempval$,deftok:dtokens,oldtok:dtokens
Local com:commands,selectstates,hasdefaultstate,defaulted=True
tok = tok.After()
Local dec:declare = finddeclaration:declare(tok)
If dec = Null
'Print "Must be variable in select statement"
	If SCRIPTDEBUG = True Then
	Notify "Must be a variable after Select, Line:"+tok.line
	EndIf
Return Null
EndIf
valcompare$ = dec.ddata$
valtype$ = dec.dtype$
tok = tok.After()


Repeat
Print "select debug current token is: "+tok.dname$
	If tok = Null Then
	'Print "unexpected end of file"
	If SCRIPTDEBUG = True Then
	Notify "Unexpected end of file"
	EndIf
	Return Null
	EndIf
	com = checkcommands(tok.tokentype)
	If com = Null Then
	'Print "expected case statement"
	If SCRIPTDEBUG = True Then
	Notify "Expected Case statement, Line:"+tok.line
	EndIf
	Return Null
	EndIf
	Select com.commandtype

	Case 16
		selectstates = 0
		tok = tok.After()
		dec:declare = finddeclaration:declare(tok)
		
	If dec = Null Then
		Print "Must be just a value"
		
		If Instr(tok.dname$,"'",1) <> 0 Then
		tempval$ = Replace(tok.dname$,"'","")
		Else
		tempval$ = tok.dname$
		EndIf
		
			If valcompare$ = tempval$ Then
					defaulted = False
					tok = tok.After()
					Repeat
					'For tok:dtokens = EachIn dtokens_list
					Print tok.dname$+" object? "
					
					If tok = Null Then Return Null
					
					'If tok2 <> Null And tok = tok2 Continue
					'For com:commands = EachIn commands_list
					com = checkcommands:commands(tok.tokentype)
					If com <> Null Then
					
					tok = processcommands(tok:dtokens,com:commands)
					'Exit
					EndIf
					If tok = Null Then Return Null
					If tok <> Null Then
					tok = tok.After()
					EndIf
					If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
					Print "INSIDE SELECT NEXT TOKEN IS :"+tok.dname$
					Until tok.dname$ = "case" Or tok.dname$ = "exit" Or tok.dname$ = "Default" Or tok.dname$ = "EndSelect" And selectstates = 0
					'If tok.dname$ = "else" Then
					'tok = getendstatement(tok)
					'Return tok
					If tok.dname$ = "exit" Then
					Repeat
				
				tok = tok.After()
				
				If tok = Null Then
				Print "expecting end select"
					If SCRIPTDEBUG = True Then
					Notify "Expecting Endselect"
					EndIf
				Return Null
				EndIf
				Print "INSIDE DEFAULT NEXT TOKEN IS: "+tok.dname$
					If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok.dname$ = "endselect" And selectstates = 0

					
					EndIf


			Else
			'defaulted = True
			selectstates = 0
				Repeat
				
				tok = tok.After()
				If tok = Null Then
				Print "expecting end select"
					If SCRIPTDEBUG = True Then
					Notify "Expecting endselect"
					EndIf
				Return Null
				EndIf
				If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok.dname$ = "case" Or tok.dname$ = "default" Or tok.dname$ = "endselect" And selectstates = 0
			EndIf
		
		Else
		Print "found variable now compare case"
		tempval$ = tok.dname$
			If valcompare$ = tempval$ Then
					defaulted = False
					tok = tok.After()
					Repeat
					'For tok:dtokens = EachIn dtokens_list
					Print tok.dname$+" object? "
					
					If tok = Null Then Return Null
					
					'If tok2 <> Null And tok = tok2 Continue
					'For com:commands = EachIn commands_list
					com = checkcommands:commands(tok.tokentype)
					If com <> Null Then
					
					tok = processcommands(tok:dtokens,com:commands)
					'Exit
					EndIf
					If tok = Null Then Return Null
					If tok <> Null Then
					tok = tok.After()
					EndIf
					If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
						Print "INSIDE SELECT NEXT TOKEN IS :"+tok.dname$
				
					Until tok.dname$ = "case" Or tok.dname$ = "exit" Or tok.dname$ = "default" Or tok.dname$ = "endselect" And selectstates = 0
					'If tok.dname$ = "else" Then
					'tok = getendstatement(tok)
					'Return tok
					If tok.dname$ = "exit" Then
					Repeat
				
				tok = tok.After()
				
				If tok = Null Then
				Print "expecting end select"
					If SCRIPTDEBUG = True Then
					Notify "Expecting endselect"
					EndIf
				Return Null
				EndIf
				Print "INSIDE DEFAULT NEXT TOKEN IS: "+tok.dname$
					If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok.dname$ = "endselect" And selectstates = 0

					
					EndIf

					

			Else
			'defaulted = True
			selectstates = 0
				Repeat
				
				tok = tok.After()
				If tok = Null Then
				Print "expecting end select"
					If SCRIPTDEBUG = True Then
					Notify "Expecting endselect"
					EndIf
				Return Null
				EndIf
				If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok.dname$ = "case" Or tok.dname$ = "default" Or tok.dname$ = "endselect" And selectstates = 0
			EndIf
		
		
		EndIf
	
	Case 17
	Print "found default"
	hasdefaultstate = True
	deftok = tok
				selectstates = 0
				Repeat
				
				tok = tok.After()
				
				If tok = Null Then
				'Print "expecting end select"
					If SCRIPTDEBUG = True Then
					Notify "Expecting endselect"
					EndIf
				Return Null
				EndIf
				Print "INSIDE DEFAULT NEXT TOKEN IS: "+tok.dname$
					If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok.dname$ = "case" Or tok.dname$ = "default" Or tok.dname$ = "endselect" And selectstates = 0
				
	If tok.dname$ = "default" Then
	Print "Select Cannot contain multiple Default Cases"
	If SCRIPTDEBUG = True Then
	Notify "Cannot have more than one Default in select statement~n Line:"+tok.line
	EndIf
	Return Null
	EndIf

	Case 18
	Print "OK now im checking the default status here : "+defaulted
	If hasdefaultstate = True And defaulted = True Then
		If deftok = Null Then
		Print "unknown error occured in select statement"
			If SCRIPTDEBUG = True Then
			Notify "unknown error occured in select statement"
			EndIf
		Return Null
		EndIf
	oldtok = tok
	tok = deftok
	selectstates = 0



					
					tok = tok.After()
					Repeat
					'For tok:dtokens = EachIn dtokens_list
					Print tok.dname$+" object? "
					
					If tok = Null Then Return Null
					
					'If tok2 <> Null And tok = tok2 Continue
					'For com:commands = EachIn commands_list
					com = checkcommands:commands(tok.tokentype)
					If com <> Null Then
					
					tok = processcommands(tok:dtokens,com:commands)
					'Exit
					EndIf
					If tok = Null Then Return Null
					If tok <> Null Then
					tok = tok.After()
					EndIf
					If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
					Until tok.dname$ = "case" Or tok.dname$ = "exit" Or tok.dname$ = "default" Or tok.dname$ = "endselect" And selectstates = 0
					'If tok.dname$ = "else" Then
					'tok = getendstatement(tok)
					tok = oldtok
					'Return tok
					If tok.dname$ = "exit" Then
					Repeat
				
				tok = tok.After()
				
				If tok = Null Then
				Print "expecting end select"
					If SCRIPTDEBUG = True Then
					Notify "Expecting endselect"
					EndIf
				Return Null
				EndIf
				Print "INSIDE DEFAULT NEXT TOKEN IS: "+tok.dname$
					If tok.dname$ = "select" Then selectstates = selectstates + 1
					If tok.dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok.dname$ = "endselect" And selectstates = 0

					
					EndIf
	
	Else
	Print "wasnt default returning "+tok.dname$
	Return tok
	
	EndIf
	Print "looks like it went through returning tok: "+tok.dname$
	Return tok
	
	
	End Select
	
Until tok = Null
Print "unexpected end of file"
	If SCRIPTDEBUG = True Then
	Notify "Unexpected end of file"
	EndIf
Return Null


End Function



Function Printfunc:dtokens(tok:dtokens)
tok = tok.After()
Local dec:declare = finddeclaration:declare(tok)
If dec = Null Then
Print "just Print value"

Print tok.dname$
'tok = tok.After()
Else
Print dec.ddata$
EndIf
Return tok

End Function


Function runfunction:dtokens(tok:dtokens)
'tok = tok.After()
Print "and the magical function name is "+tok.dname$
Local dec:declare = finddeclaration:declare(tok)
Local oldtok:dtokens,com:commands,scr:script
oldtok = tok
scr = tok.parentscrpt
If dec = Null Then
'Print "Function does not exist"
	If SCRIPTDEBUG = True Then
	Notify "Function does not exist, Line:"+tok.line
	EndIf
Return Null
EndIf
tok = invokefunctiontokens(dec)
Repeat
'For tok:dtokens = EachIn dtokens_list
Print tok.dname$+" object? "

If tok = Null Then Return Null

'If tok2 <> Null And tok = tok2 Continue
'For com:commands = EachIn commands_list
com = checkcommands:commands(tok.tokentype)
If tok.dname$ = "return" Then 
tok = oldtok
Return tok
EndIf
If com <> Null Then

tok = processcommands(tok,com)
'Exit
EndIf

If tok <> Null Then
tok = tok.After()
EndIf
Until tok = Null Or tok.parentscrpt <> scr
tok = oldtok
dismissfunctiontokens(dec)
Print "end function returning next token "+tok.dname$
Return tok

End Function

Function dismissfunctiontokens(dec:declare)

For tok:dtokens = EachIn dtokens_list
If tok.parentfunction = dec And tok.parentscrpt = dec.parentscrpt Then
dtokens_list.remove tok
tok.Remove()
EndIf
Next

End Function

Function invokefunctiontokens:dtokens(dec:declare)
Local firstok:dtokens,gotfirst=False
For fok:ftokens = EachIn ftokens_list
If fok.parentfunction = dec And fok.parentscrpt = dec.parentscrpt Then
tok:dtokens = New dtokens
tok.dname$ = fok.dname$
Print "adding function token "+tok.dname$
tok.index = fok.index
tok.line = fok.line
tok.tokentype = fok.tokentype
tok.parentfunction = fok.parentfunction
tok.parentscrpt = dec.parentscrpt
If gotfirst = False Then
firstok = tok
gotfirst = True
EndIf

EndIf
Next

Return firstok
End Function

Function checktoken$(tok:dtokens)
Print "checking token "+tok.dname$
If tok.parentscrpt = Null Then
Print "This token has no parent script"
EndIf
Local olddec:declare
Local dec:declare = finddeclaration:declare(tok)

If dec = Null Then
Print "Expecting variable Assignment"

Return ""
Else
Print "found the variable of type "+dec.dtype$
Return dec.dtype$

EndIf




End Function


Function checkcommands:commands(data)
Print "checking the commands"
For com:commands = EachIn commands_list
If data = com.commandtype Then 
Return com
EndIf
Next
Return Null
End Function


Function checkspecificcommands:commands(data$)
Print "checking the commands"
For com:commands = EachIn commands_list
If data$ = com.commandname$ Then 
Return com
EndIf
Next
Return Null
End Function


Function runifstate:dtokens(tok:dtokens)
Local olddec:declare,oldtok:dtokens,com:commands
Local dec:declare,arg:arguement,foundcompare=False
Local foundthen = False
Repeat
tok = tok.After()
com = checkcommands(tok.tokentype)
If com <> Null Then
If com.commandtype = 0 Then com = Null
EndIf
If com = Null Then
dec = finddeclaration:declare(tok)

If dec = Null Then

	Print "Not a variable.. must be just an argument"
	arg = checkarg(tok.dname$)

	
Else
    Print "found variable "+dec.dname$
	arg:arguement = New arguement
	arg.arguementtype$ = dec.dtype$
	arg.argvalue$ = dec.ddata$
	

	

EndIf
    Print "lets find the operator"
	tok = tok.After()
	Print "operator given is "+tok.dname$
	check = validateoperator(tok.dname$)
	
		If check <> 0 Then
	
		
	
		arg.argnextoperator$ = tok.dname$
	
			If check = 2 And foundcompare = False Then
			foundcompare = True
			Else If check = 2 And foundcompare = True Then
			Print "Expecting Command"
				If SCRIPTDEBUG = True Then
				Notify "Expecting command Line:"+tok.line
				EndIf
			Return Null
			EndIf 
		
		
		Else
 			com = checkcommands(tok.tokentype)
			If com <> Null Then
			If com.commandtype = 0 Then com = Null
			EndIf
			If com = Null Then
			Print "Expected Operator"
							If SCRIPTDEBUG = True Then
				Notify "Expecting operator Line:"+tok.line
				EndIf
			Return Null
			Else
			Print "looks as though we hit a command. lets back track one token"
			arg.argnextoperator$ = tok.dname$
			tok = tok.Before()
			foundcompare = False
			EndIf
		EndIf
Else
Select com.commandtype

	Case 6
	foundcompare = False
	
	Case 7
	foundcompare = False
	
	Case 8
	foundcompare = False
	foundthen = True
	
	Default
	Print "invalid command"
					If SCRIPTDEBUG = True Then
				Notify "Invalid command Line:"+tok.line
				EndIf
	Return Null
	
End Select
EndIf
Until foundthen = True 
Print "found then"

Print "lets see if the if statement is true"

Print "Print all arguements"
For arg:arguement = EachIn arguement_list
Print "arguement: "+arg.arguementtype$+" value: "+arg.argvalue$+" next operator: "+arg.argnextoperator$
Next
processarguments()
ifboolean = processifbooleans()
Print "if statement is "+ifboolean
killbools()
killargs()

Select ifboolean

Case False
oldtok = tok
tok = getelsestatement:dtokens(tok)
If tok = Null Then
tok = getendstatement(oldtok)
Return tok
Else
tok = tok.After()
Repeat
'For tok:dtokens = EachIn dtokens_list
Print tok.dname$+" object? "

If tok = Null Then Return Null

'If tok2 <> Null And tok = tok2 Continue
'For com:commands = EachIn commands_list
com = checkcommands:commands(tok.tokentype)
If com <> Null Then

tok = processcommands(tok:dtokens,com:commands)
'Exit
EndIf

If tok <> Null Then
tok = tok.After()
EndIf
Until tok.dname$ = "endif"
Return tok
EndIf


Case True
tok = tok.After()
Repeat
'For tok:dtokens = EachIn dtokens_list
Print tok.dname$+" object? "

If tok = Null Then Return Null

'If tok2 <> Null And tok = tok2 Continue
'For com:commands = EachIn commands_list
com = checkcommands:commands(tok.tokentype)
If com <> Null Then

tok = processcommands(tok:dtokens,com:commands)
'Exit
EndIf
If tok = Null Then Return Null
If tok <> Null Then
tok = tok.After()
EndIf
Until tok.dname$ = "endif" Or tok.dname$ = "else"
If tok.dname$ = "else" Then
Print "found else going to endif"
tok = getendstatement(tok)
Return tok
EndIf

End Select

End Function


Function getelsestatement:dtokens(tok:dtokens)
Local statescount = 0
Repeat
tok = tok.After()

If tok = Null Then
Print "unepected end of file"
				If SCRIPTDEBUG = True Then
				Notify "Unexpected end of file"
				EndIf
Return Null
EndIf
If tok.dname$ = "if" Then statescount = statescount + 1
If tok.dname$ = "endif" And statescount > 0 Then statescount = statescount - 1
If tok.dname$ = "endif" And statescount = 0 Then Return Null
Until tok.dname$ = "else" And statescount = 0
End Function

Function getendstatement:dtokens(tok:dtokens)
Local statescount=0

Repeat

tok = tok.After()
If tok = Null Then
Print "unexpected end of file"
				If SCRIPTDEBUG = True Then
				Notify "Unexpected end of file"
				EndIf
Return Null
EndIf

If tok.dname$ = "if" Then statescount = statescount + 1
If tok.dname$ = "endif" And statescount > 0 Then statescount = statescount - 1


Until tok.dname$ = "endif" And statescount = 0
Return tok
End Function

Function processarguments()
Local arg:arguement,oldvalue$,oldvaluetype$,arguementbool=False
Local nextoperator$,com:commands,narg:arguement
arg = getarguement(arguement_list.First())

If arg = Null Then
Print "I dont know how the hell it happened but i lost my arguements"
				If SCRIPTDEBUG = True Then
				Notify "Really, some weird way the error is my fault!"
				EndIf
Return False
EndIf
oldvalue$ = arg.argvalue$
oldvaluetype$ = arg.arguementtype$
nextoperator$ = arg.argnextoperator$
Repeat 
nextoperator$ = arg.argnextoperator$
arg = arg.After()
operationtype = validateoperator(nextoperator$)

	Select operationtype
	Case 1
	Print "old value "+oldvalue$
	oldvalue$ = mathoperation$(oldvalue$,arg.argvalue$,oldvaluetype$,arg.arguementtype$,nextoperator$)
	Print "new value "+oldvalue$
	
	Case 2
	Print "before bool argvalue is "+arg.argvalue$+" vs "+oldvalue$
	arguementbool = booloperation(oldvalue$,arg.argvalue$,oldvaluetype$,arg.arguementtype$,nextoperator$)
	Print "bool "+arguementbool
	
	Default
	Print "may be an operative command"
	com = checkspecificcommands(nextoperator$)
	If com = Null Then 
	Print "Somehow it got screwed up"
	Return False		
	EndIf
	Print "Ok it is a command. now to see which one"
	
		Select com.commandtype
		
			Case 6
			Print "found "+com.commandname$
			ifo:ifbool = New ifbool
			ifo.bool = arguementbool
			ifo.boolexp = com.commandtype
			'arg = arg.After()
			
			oldvalue$ = arg.argvalue$
			oldvaluetype$ = arg.arguementtype$
			Print "AFTER AND NEXT OPERATOR IS "+arg.argnextoperator$
			
			Case 7
			Print "found "+com.commandname$
			ifo:ifbool = New ifbool
			ifo.bool = arguementbool
			ifo.boolexp = com.commandtype
			'arg = arg.After()
			oldvalue$ = arg.argvalue$
			oldvaluetype$ = arg.arguementtype$
			
			Case 8
			Print "found "+com.commandname$
			ifo:ifbool = New ifbool
			ifo.bool = arguementbool
			ifo.boolexp = com.commandtype
			arg = Null
			
			
			
			Default
			Print "invalid expression in if statement"
			Return False
			Exit
		End Select
	
	End Select

Until arg = Null


End Function


Function killargs()
For arg:arguement = EachIn arguement_list
arguement_list.remove arg
arg.Remove()
Next
End Function

Function killbools()
For ifo:ifbool = EachIn ifbool_list
ifbool_list.remove ifo
ifo.Remove()
Next
End Function

Function processifbooleans()
Local ifo:ifbool,locbool=True
Local oldifo:ifbool
ifo = getifbool(ifbool_list.First())

If ifo = Null Then
Print "Oh man this must be Stupid to lose the booleans"
Return False
EndIf
oldifo = ifo
If oldifo.bool = False Then locbool = False
Repeat

oldifo = ifo
ifo = ifo.After()

If ifo = Null Then Return locbool
If ifo <> Null Then
Select oldifo.boolexp

Case 6
If ifo.bool = False locbool = False

Case 7
If ifo.bool = True locbool = True

Case 8
Return locbool

Default 
Print "Expecting Expression"
Return False

End Select

EndIf

Until oldifo.boolexp = 8

Return locbool

End Function

Function booloperation(destvar$,sourcevar$,desttype$,sourcetype$,operator$)
Select operator$

 Case "="
 	Select desttype$
	
	Case "string"
		Select sourcetype$
		
			Case "string"
				If destvar$ = sourcevar$ Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If destvar$ = sourcevar$ Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If destvar$ = sourcevar$ Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "int"
		Select sourcetype$
		
			Case "string"
				If destvar$ = sourcevar$ Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) = Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) = Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "float"
		Select sourcetype$
		
			Case "string"
				If destvar$ = sourcevar$ Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Float(destvar$) = Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) = Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select	
	Default
	Return False

	End Select

 Case ">"
 	Select desttype$
	
	Case "string"
		Select sourcetype$
		
			Case "string"
				If Len(destvar$) > Len(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) > Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) > Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "int"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) > Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) > Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) > Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "float"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) > Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Float(destvar$) > Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) > Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select	
	Default
	Return False

	End Select

 Case ">="
 	Select desttype$
	
	Case "string"
		Select sourcetype$
		
			Case "string"
				If Len(destvar$) >= Len(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) >= Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) >= Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "int"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) >= Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) >= Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) >= Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "float"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) >= Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Float(destvar$) >= Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) >= Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select	
	Default
	Return False

	End Select

 Case "<"
 	Select desttype$
	
	Case "string"
		Select sourcetype$
		
			Case "string"
				If Len(destvar$) < Len(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) < Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) < Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "int"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) < Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) < Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) < Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "float"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) < Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Float(destvar$) < Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) < Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select	
	Default
	Return False

	End Select

 Case "<="
 	Select desttype$
	
	Case "string"
		Select sourcetype$
		
			Case "string"
				If Len(destvar$) <= Len(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) <= Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <= Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "int"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) <= Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) <= Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <= Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "float"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) <= Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Float(destvar$) <= Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <= Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select	
	Default
	Return False

	End Select

 Case "<>"
 	Select desttype$
	
	Case "string"
		Select sourcetype$
		
			Case "string"
				If Len(destvar$) <> Len(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) <> Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <> Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "int"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) <> Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) <> Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <> Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "float"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) <> Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Float(destvar$) <> Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <> Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select	
	Default
	Return False

	End Select

 Case "><"
 	Select desttype$
	
	Case "string"
		Select sourcetype$
		
			Case "string"
				If Len(destvar$) <> Len(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) <> Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <> Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "int"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) <> Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Int(destvar$) <> Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <> Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select
	
	Case "float"
		Select sourcetype$
		
			Case "string"
				If Int(destvar$) <> Int(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
				
			Case "int"
				If Float(destvar$) <> Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf			
			
			Case "float"
				If Float(destvar$) <> Float(sourcevar$) Then
				Return True
				Else
				Return False
				EndIf
						
			Default
			Return False
		
		End Select	
	Default
	Return False

	End Select


 Default
 Return False




End Select
End Function

Function mathoperation$(destvar$,sourcevar$,desttype$,sourcetype$,operator$)

Select operator$

	Case "+"
		Select sourcetype$
			
			Case "string"
				Select desttype$
				
					Case "string"
					destvar$ = destvar$ + sourcevar$
					Return destvar$
					
					Case "int" 
					destvar$ = destvar$ + sourcevar$
					Return destvar$
					
					Case "float"
					destvar$ = destvar$ + sourcevar$
					Return destvar$
					
					Default
					Return ""
				
				End Select
			
			Case "int"
				Select desttype$
				
					Case "string"
					destvar$ = destvar$ + Int(sourcevar$)
					Return destvar$
					
					Case "int" 
					destvar$ = Int(destvar$) + Int(sourcevar$)
					Return destvar$
					
					Case "float"
					destvar$ = Float(destvar$) + Int(sourcevar$)
					Return destvar$
					
					Default
					Return ""
				
				End Select			
			Case "float"
				Select desttype$
				
					Case "string"
					destvar$ = destvar$ + Float(sourcevar$)
					Return destvar$
					
					Case "int" 
					destvar$ = Int(destvar$) + Float(sourcevar$)
					Return destvar$
					
					Case "float"
					destvar$ = Float(destvar$) + Float(sourcevar$)
					Return destvar$
					
					Default
					Return ""
				
				End Select			
			Default
			Return ""
		
		End Select
	
	
	Case "-"
		Select sourcetype$
			
			Case "string"
				Select desttype$
				
					Case "string"
					destvar$ = Replace(destvar$,sourcevar$,1)
					Return destvar$
					
					Case "int"
					destvar$ = Replace(destvar$,sourcevar$,1)
					Return destvar$
					
					Case "float"
					destvar$ = Replace(destvar$,sourcevar$,1)
					Return destvar$
					
					Default
					Return ""
				
				End Select
			
			Case "int"
				Select desttype$
				
					Case "string"
					destvar$ = Replace(destvar$,sourcevar$,1)
					Return destvar$
					
					Case "int"
					destvar$ = Int(destvar$) - Int(sourcevar$)
					Return destvar$
					
					Case "float"
					destvar$ = Float(destvar$) - Int(sourcevar$)
					Return destvar$
					
					Default
					Return ""
				
				End Select			
			Case "float"
				Select desttype$
				
					Case "string"
					destvar$ = Replace(destvar$,sourcevar$,1)
					Return destvar$
					
					Case "int"
					destvar$ = Int(destvar$) - Float(sourcevar$)
					Return destvar$
					
					Case "float"
					destvar$ = Float(destvar$) - Float(sourcevar$)
					Return destvar$
					
					Default
					Return ""
				
				End Select			
			Default
			Return ""
		
		End Select	
	Case "/"
		Select sourcetype$
			
			Case "string"
				Select desttype$
				
					Case "string"
					Return destvar$
					
					Case "int"
					Return destvar$
					
					Case "float"
					Return destvar$
					
					Default
					Return ""
				
				End Select
			
			Case "int"
				Select desttype$
				
					Case "string"
					Return destvar$
					
					Case "int"
					destvar$ = Int(destvar$) / Int(sourcevar$)
					Return destvar$
					
					Case "float"
					destvar$ = Float(destvar$) / Int(sourcevar$)
					Return destvar$
					
					Default
					Return ""
				
				End Select			
			Case "float"
				Select desttype$
				
					Case "string"
					Return destvar$
					
					Case "int"
					destvar$ = Int(destvar$) / Float(sourcevar$)
					Return destvar$
					
					Case "float"
					destvar$ = Float(destvar$) / Float(sourcevar$)
					Return destvar$
					
					Default
					Return ""
				
				End Select			
			Default
			Return ""
		
		End Select
	
	Case "*"
		Select sourcetype$
			
			Case "string"
				Select desttype$
				
					Case "string"
					Return destvar$
					
					Case "int"
					Return destvar$
					
					Case "float"
					Return destvar$
					
					Default
					Return ""
				
				End Select
			
			Case "int"
				Select desttype$
				
					Case "string"
					Return destvar$
					
					Case "int"
					destvar$ = Int(destvar$) * Int(sourcevar$)
					Return destvar$
					
					Case "float"
					destvar$ = Float(destvar$) * Int(sourcevar$)
					Return destvar$
					
					Default
					Return ""
				
				End Select			
			Case "float"
				Select desttype$
				
					Case "string"
					Return destvar$
					
					Case "int"
					destvar$ = Int(destvar$) * Float(sourcevar$)
					Return destvar$
					
					Case "float"
					destvar$ = Float(destvar$) * Float(sourcevar$)
					Return destvar$
					
					Default
					Return ""
				
				End Select			
			Default
			Return ""
		
		End Select
	
	Default
	Return ""

End Select

EndFunction


Function validateoperator(data$)
Local validated = False
Local expect = 0

If Instr(OPERATORS$,data$,1) <> 0 Then
Select data$
 Case "="
 Return 2

 Case ">"
 Return 2

 Case ">="
 Return 2

 Case "<"
 Return 2

 Case "<="
 Return 2

 Case "<>"
 Return 2

 Case "><"
 Return 2

 Default
 Return 1

End Select
Else
Return 0
EndIf

End Function


Function checkarg:arguement(data$)

If Instr(data$,"'",1) <> 0 Then
arg:arguement = New arguement
arg.arguementtype$ = "string"
arg.argvalue$ = Replace(data$,"'","")
Return arg
EndIf

If Instr(data$,".",1) <> 0 Then
arg:arguement = New arguement
arg.arguementtype$ = "float"
arg.argvalue$ = data$
Return arg
EndIf

arg:arguement = New arguement
arg.arguementtype$ = "int"
arg.argvalue$ = data$
Return arg




End Function

Function assignvar:dtokens(tok:dtokens)
Local olddec:declare,oldtok:dtokens,com:commands
Local dec:declare = finddeclaration:declare(tok)
oldtok = tok
Print "assinging var data to "+tok.dname$
tok = tok.After()

If tok = Null Then
	Print "may possibly be just a function"
	If dec.dtype$ = "function" Then
	Print "found function running script"
	tok = tok.Before()
	tok = runfunction(tok)
	Return tok
	Else
	Print "Unexprected end of file"
					If SCRIPTDEBUG = True Then
				Notify "Unexpected end of file"
				EndIf
	Return Null
	EndIf
EndIf

Print oldtok.dname$
Print "next field after "+oldtok.dname$+" is "+tok.dname$
If tok.dname$ = "=" Then
Print "found ="
olddec = dec

Select olddec.dtype$

Case "int"
Repeat

tok = tok.After()
If tok.line = oldtok.line Then

		Print "found integer next "+tok.dname$
		Select tok.dname$
		
		
		Case "+"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Int(olddec.ddata$)+Int(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Int(olddec.ddata$) + Int(callvariable$(tok))
		tok = tok.After()
		tok = tok.After()
		
		Print olddec.dname$+" int = "+olddec.ddata$
		
		End Select 
		
		EndIf
		Else
		olddec.ddata$ = Int(olddec.ddata$)+Int(dec.ddata$)
		Print olddec.dname$+" int = "+olddec.ddata$
		EndIf
		
		Case "-"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Int(olddec.ddata$)-Int(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Int(olddec.ddata$) - Int(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 
		EndIf
		Else
		olddec.ddata$ = Int(olddec.ddata$)-Int(dec.ddata$)
		EndIf
		
		Case "*"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
				com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Int(olddec.ddata$)*Int(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Int(olddec.ddata$) * Int(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 
		EndIf
		Else
		olddec.ddata$ = Int(olddec.ddata$)*Int(dec.ddata$)
		EndIf
		
		Case "/"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Int(olddec.ddata$)/Int(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Int(olddec.ddata$) / Int(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 	
		EndIf	
		Else
		olddec.ddata$ = Int(olddec.ddata$)/Int(dec.ddata$)
		EndIf
		
		Default
		Print "found numerical expression of "+tok.dname$
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Int(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Int(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 		
		EndIf		
		Print olddec.dname$+" int = "+olddec.ddata$
		Else
		olddec.ddata$ = Int(dec.ddata$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		EndIf
		
		End Select
EndIf
Until tok.line > oldtok.line


Case "float"
Repeat
tok = tok.After()
If tok.line = oldtok.line Then

		Select tok.dname$
		
		
		Case "+"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Float(olddec.ddata$)+Float(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Float(olddec.ddata$) + Float(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 		
		EndIf
		Else
		olddec.ddata$ = Float(olddec.ddata$)+Float(dec.ddata$)
		EndIf
		
		Case "-"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Float(olddec.ddata$)-Float(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Float(olddec.ddata$) - Float(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 		
		EndIf
		Else
		olddec.ddata$ = Float(olddec.ddata$)-Float(dec.ddata$)
		EndIf
		
		Case "*"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Float(olddec.ddata$)*Float(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Float(olddec.ddata$) * Float(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 		
		EndIf
		Else
		olddec.ddata$ = Float(olddec.ddata$)*Float(dec.ddata$)
		EndIf
		
		Case "/"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Float(olddec.ddata$)/Float(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Float(olddec.ddata$) / Float(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 		
		EndIf
		Else
		olddec.ddata$ = Float(olddec.ddata$)/Float(dec.ddata$)
		EndIf
		
		Default
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = Int(tok.dname$)
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = Int(callvariable$(tok))
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 		
		EndIf		
		Else
		olddec.ddata$ = Float(dec.ddata$)
		EndIf
		End Select
EndIf
Until tok.line > oldtok.line


Case "string"
Print "assinging string data to "+olddec.dname$
Repeat
tok = tok.After()
Print tok.dname$+" line is "+tok.line+" "+oldtok.dname$+" line is "+oldtok.line
If tok.line = oldtok.line Then
		Select tok.dname$
		
		
		Case "+"
		tok = tok.After()
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = olddec.ddata$ + tok.dname$
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = olddec.ddata$ + callvariable$(tok)
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 		
		EndIf
		Else
		olddec.ddata$ = olddec.ddata$+dec.ddata$
		EndIf
		olddec.ddata$ = Replace(olddec.ddata$,"'","")
		
		Default
		dec = finddeclaration:declare(tok)
		If dec = Null Then
		com = checkcommands(tok.tokentype)
		If com <> Null Then
		
		Select com.commandtype
		
		Case 0
		olddec.ddata$ = tok.dname$
		Print olddec.dname$+" int = "+olddec.ddata$
		
		
		Case 25
		olddec.ddata$ = callvariable$(tok)
		Print olddec.dname$+" int = "+olddec.ddata$
		tok = tok.After()
		tok = tok.After()
		
		End Select 		
		EndIf
		Else
		olddec.ddata$ = dec.ddata$
		EndIf
		olddec.ddata$ = Replace(olddec.ddata$,"'","")
		Print olddec.ddata$
		End Select
EndIf
Until tok.line > oldtok.line


End Select
Print "next item is "+tok.dname$
Return tok.Before() 
Else
If dec.dtype$ = "function" Then
Print "found function running script"
tok = tok.Before()
tok = runfunction(tok)
Return tok
Else
Print "expecting operator"
				If SCRIPTDEBUG = True Then
				Notify "Expecting operator Line:"+tok.line
				EndIf
Return Null
EndIf
EndIf

End Function

Function declarevar:dtokens(tok:dtokens)
Local oldtok:dtokens
dec:declare = New declare
tok = tok.After()
If tok = Null Then
Print "Expecting VAR type"
				If SCRIPTDEBUG = True Then
				Notify "Expecting Var type"
				EndIf
Return Null
EndIf
dec.dtype$ = tok.dname$
dec.parentscrpt = tok.parentscrpt
tok = tok.After()
If tok = Null Then
Print "Expecting Var Name"
				If SCRIPTDEBUG = True Then
				Notify "Expecting Var name"
				EndIf
Return
EndIf
dec.dname$ = tok.dname$

oldtok = tok
If dec.dtype$ = "function" Then
addfunctiontokens(dec,tok)
Print "added new function "+dec.dname$
EndIf

Print "Found Variable type "+dec.dtype$+" var name "+dec.dname$
Return tok
End Function


Function addfunctiontokens(dec:declare,tok:dtokens)
Local fok:ftokens
Local killtok:dtokens
tok = findfunction(dec,tok)

killtok = tok

tok = tok.After()
Print "about to kill token "+killtok.dname$
dtokens_list.remove killtok
killtok.Remove()

dtokens_list.remove killtok
Repeat

fok:ftokens = New ftokens
fok.dname$ = tok.dname$
fok.index = tok.index
fok.line = tok.line
fok.tokentype = tok.tokentype
fok.parentfunction = dec
fok.parentscrpt = tok.parentscrpt
killtok = tok
tok = tok.After()
Print "about to kill token "+killtok.dname$
dtokens_list.remove killtok
killtok.Remove()

If tok = Null Then
Print "unexpected end of file"
				If SCRIPTDEBUG = True Then
				Notify "Unexpected end of file"
				EndIf
Return
EndIf
Until tok.dname$ = "endfunction"
dtokens_list.remove tok
tok.Remove()

End Function


Function findfunction:dtokens(dec:declare,tok:dtokens)
Local foundfunc = False
Local killtok:dtokens
Repeat
tok = tok.After()
If tok.dname$ = "function" Then
tok = tok.After()
If tok.dname$ = dec.dname$ Then
killtok = tok.Before()
dtokens_list.remove killtok
killtok.Remove()
Return tok
EndIf
EndIf



Until tok = Null
Return Null
End Function


Function tokenizescript(scriptname$="myscript",shandle$="QWERTYUIOP",scriptfile$="script.txt")
Local templine$,toffset,temptoken$
Local Stream:TStream = OpenFile(scriptfile$)
Local index = 0,gotstring = False,gotendstring = False
Local line = 0
scr:script = New script
scr.scriptname$ = scriptname$
scr.scripthandle$ = shandle$
While Not Eof(Stream)
templine$ = ReadLine(Stream)
line = line + 1
Print "line: "+line
'Print "len: "+Len(templine$)
For tempfor = 1 To Len(templine$)
'Print tempfor
If Mid$( templine$,tempfor,1 ) = "'" Then
If gotstring = False Then
gotstring = True
Else
gotstring = False
gotendstring = True
EndIf
EndIf
If gotstring = False
If Mid$( templine$,tempfor,1 ) <> " " And tempfor <> Len(templine$) And gotendstring = False Then
temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )
Else
temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )
tok:dtokens = New dtokens
tok.dname$ = Trim(temptoken$)
tok.index = index
tok.line = line
tok.parentscrpt = scr
If tok.parentscrpt = Null Then
Print "Uh oh this has no parent script"
End
EndIf
index = index + 1
For com:commands = EachIn commands_list
If Lower(tok.dname$) = Lower(com.commandname$) Then
tok.tokentype = com.commandtype


EndIf

Next

tok.index = index
index = index + 1
temptoken$ = ""
gotendstring = False
EndIf
Else
temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )
EndIf
Next

Wend
CloseFile(Stream)

For tok:dtokens = EachIn dtokens_list
Print tok.dname$
Next

End Function








Function tokenizescriptin$(scr:script,viewing=True)
Local templine$,toffset,temptoken$
'Local Stream:TStream = OpenFile(scriptfile$)
Local index = 0,gotstring = False,gotendstring = False
Local line = 0
'scr:script = New script
'scr.scriptname$ = scriptname$
'scr.scripthandle$ = shandle$
'While Not Eof(Stream)
templine$ = scr.scriptdata$
line = line + 1
Print "line: "+line
'Print "len: "+Len(templine$)
For tempfor = 1 To Len(templine$)
'Print tempfor
If Mid$( templine$,tempfor,1 ) = "'" Then
If gotstring = False Then
gotstring = True
Else
gotstring = False
gotendstring = True
EndIf
EndIf
If gotstring = False
If Mid$( templine$,tempfor,1 ) <> " " And tempfor <> Len(templine$) And gotendstring = False And Mid$( templine$,tempfor,2 ) <> "~n" Then
temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )
'Print "char - "+Mid$( templine$,tempfor,1 )
Else
temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )
tok:dtokens = New dtokens
tok.dname$ = Trim(temptoken$)
Print tok.dname$
tok.index = index
tok.line = line
tok.parentscrpt = scr
If Mid$( templine$,tempfor,2 ) = "~n" Then line = line + 1
If tok.parentscrpt = Null Then
Print "Uh oh this has no parent script"
End
EndIf
index = index + 1
For com:commands = EachIn commands_list
If Lower(tok.dname$) = Lower(com.commandname$) Then
tok.tokentype = com.commandtype
'Select com.commandtype

'Case 0
'Replace(scriptdata$,tok.dname$,"\cf4 "+tok.dname$)

'Default
'Replace(scriptdata$,tok.dname$,"\cf3 "+tok.dname$)



'End Select

EndIf

Next

tok.index = index
index = index + 1
temptoken$ = ""
gotendstring = False
EndIf
Else
temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )
EndIf
Next

'Wend
'CloseFile(Stream)

For tok:dtokens = EachIn dtokens_list
Print tok.dname$
Next
'scriptdata$ = "{\rtf1\ansi\ansicpg1252\deff0\deflang1033{\fonttbl{\f0\fswiss\fcharset0 Arial;}}"+"{\colortbl ;\red0\green0\blue255;\red0\green255\blue0;\red255\green255\blue0;\red0\green0\blue0;}"+scriptdata$
Return scriptdata$
End Function




Function loadcommands()
Local tempfor,templine$,temptoken$
Local Stream:TStream = OpenFile("commands.txt")
While Not Eof(Stream)
templine$ = ReadLine(Stream)
For tempfor = 1 To Len(templine$)
If Mid$( templine$,tempfor,1 ) <> " " Then
temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )
'Print temptoken$
Else
'temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )

com:commands = New commands
com.commandtype = Int(temptoken$)
com.commandname$ = Trim(Replace(templine$,temptoken$,"")) 
'Print com.commandtype
Print com.commandname$
temptoken$ = ""
Exit
EndIf

Next
Wend
CloseFile(Stream)


End Function




Function finddeclaration:declare(tok:dtokens)
Local foundit = False
For dec:declare = EachIn declare_list
	If dec.dname$ = tok.dname$ And tok.parentscrpt = dec.parentscrpt Then
	Return dec
	foundit = True
	EndIf
	
	Next

Return Null
End Function




Function colorizescript(listgadget:TGadget)
Local com:commands,tok:dtokens,abool:Int,aboolcursor
Local keyword:String,myword:String
Local textlen:Int,cursorpos:Int
	For com:commands = EachIn commands_list
	If com.commandtype <> 0 Then
	keyword = keyword + com.commandname$+"|"
	EndIf
	Next
	
Print "COMMANDS ARE :"+keyword

textlen = TextAreaLen( listgadget )

Print "length of text is "+textlen
cursorpos = 0
For tempfor = 0 To textlen
'If abool = False
If TextAreaText(listgadget,tempfor,1) <> " " And Instr(TextAreaText(listgadget,tempfor,1),"~n") = 0 Then
'If Instr(TextAreaText(listgadget,tempfor,1),"~n") <> 0 Then Print "FOUND SPACER at "+myword
myword = myword + TextAreaText(listgadget,tempfor,1)
'EndIf

If TextAreaText(listgadget,tempfor,1) = "'" And abool = False Then
abool = True
'myword = ""
aboolcursor = tempfor

Else If TextAreaText(lis
