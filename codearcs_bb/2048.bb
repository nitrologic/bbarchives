; ID: 2048
; Author: Leon Drake
; Date: 2007-06-26 23:14:06
; Title: In game scripting B3d Bplus
; Description: Ported the bmx version to bb

;Rem
;******************************************************************************************
;Ingame Scripting Lib v0\1\20 Blitz+&3d edition
;Written by.Leon Drake a\k\a Landon Ritchie
;
;Free For Commercial Use! Enjoy!!
;
;Few bugs i need To let you know about in the scripting side\
;
;Currently Everything is Case sensitive so when writing scripts keep everything lowercase
;I;ll be fixing that soon\ Also make sure everything is spaced\ My tokenizer is still just 
;a baby so If you put in var3=var1+var2 , it will think that whole block is a value so use
;val3 = val1 + val2
;
;you have To declare all variables And the types\ Example var Int var1
;you must do the same For any functions you wish To use\ var Function myfunc
;
;I havent added the ability To cast parameters To functions yet, so dont put a () After 
;your Function when calling it\ use myfunc instead of myfunc()
;
;note on If statements\ I;m pretty sure i got it To run Right so far If tested it And it
;seems To Return If statements fine\ however curently you can only use math operators Before
;it compares the If\ For example
;
;If myval + myval2 = myval3 Then
;
;works, but\\\
;
;If myval = myval2 + myval3 Then
;
;doesn;t work at the moment\ probably best To assign that stuff Before you call an If, For
;now anyways\ You can use And & Or in your If statement And its Not limited on how complex 
;that part may be\ you can also use Else in your If statement\ so far it works good\
;
;
;
;I;ll try To keep updating this as often as i can , which should be often enough since i;m
;using this code For a 2d rpg engine\
;
;
;Just added the ability To Write Select statements
;
;works pretty much the same as blitz 
;
;usage
;
;Select myval3
;
;Case 100
;
;Case 200
;
;Default
;
;endselect
;
;you can also put Select statements inside other Select statements And it works fine
;
;Select myval3
;
;Case 100
;	Select myval4
;	
;		Case 20
;		
;	endselect
;
;Default
;
;endselect
;
;added For loops \\yet To be upgraded its still very touchy it must be run with a variable
;like so.
;
;For myvar3 = 0 To 100;
;
;Next
;
;you can however Replace the 0 Or the 100 with variables\ doesnt have To start with 0 Or
;End with 100 i was merely using that as an example\
;
;
;Added New Function 
;callfunction scriptname functionname
;
;What this does is allows you To call a Function from a different active script\ meaning you
;have already used runscript on that script\ which makes the script active And its functions
;available\ Use call Function from another script To run a Function from a different script
;using the other scripts variables And what Not\ Also you dont have To worry about 2 scripts
;running using the same variable names i made sure it differentiates them\
;
;Added New Function 
;callvariable scriptname variablename
;
;Just like callfunction this allows you To access the contents of a variable in another 
;active script\ So far its only usage is For variable assignment\ you cant use it in If
;statements Or anything Else like that so you;ll have To pre grab the variable contents 
;Before hand like this.
;
;myvar = callvariable myscript varname
;
;you can also do this If you want To 
;
;myvar = callvariable myscript varname + callvariable myscript varname2
;And that works with all the math operators i;ve added so far\
;
;
;******************************************************************************************
;End Rem
;Import "bin\bbtype\bmx"
;Import "bin\bbvkey\bmx"
;Global declare.TList = New TList
;Global dtokens.TList = New TList
;Global commands.TList = New TList
;Global arguement.TList = New TList
;Global ifbool.TList = New TList
;Global ftokens.TList = New TList
;Global script.TList = New TList

;token types
Const OPERATORS$ = "*/+-=><"


Type script


Field scriptname$,scripthandle$
End Type

Type ifbool


Field bool,boolexp,parentscrpt.script
End Type

Type arguement
	

Field arguementtype$,argvalue$,argnextoperator$,expecting$,parentscrpt.script
End Type
 
Type commands
	
Field commandname$,commandtype
End Type

Type declare


Field dtype$,ddata$,dataarray$[999],dname$,parentscript$,pscript,sindex,parentscrpt.script
End Type

Type dtokens



Field dname$,parentscript$,pscript,index,lline,tokentype,parentfunction.declare,parentscrpt.script
End Type



Type ftokens


Field dname$,parentscript$,pscript,index,lline,tokentype,parentfunction.declare,parentscrpt.script
End Type

loadcommands()

tokenizescript()
tokenizescript("myscript2","ASDFGH","script2.txt")
;processtokens()

runscript()
runscript("myscript2","ASDFGH")

Function runscript.script(scriptname$="myscript",scripthandle$="QWERTYUIOP")
Local tok.dtokens,com.commands,scr.script
scr = getscript.script(scriptname$,scripthandle$)
If scr = Null Then
DebugLog "invalid script name"
Return Null
EndIf
tok = gettoken.dtokens(scr)
Repeat
;For tok.dtokens = each dtokens
DebugLog tok\dname$+" object? "

If tok = Null Then Return Null

;If tok2 <> Null And tok = tok2 Continue
;For com.commands = each commands
com = checkcommands.commands(tok\tokentype)
If com <> Null Then

tok = processcommands(tok.dtokens,com.commands)
;Exit
EndIf

If tok <> Null Then
tok = After tok

EndIf
If tok = Null Then Return Null
Until tok = Null Or tok\parentscrpt <> scr

;Next


End Function

Function getscript.script(scriptname$,scripthandle$)
For scr.script = Each script
If scr\scriptname$ = scriptname$ And scr\scripthandle$ = scripthandle$ Then
Return scr
EndIf
Next
Return Null
End Function




Function gettoken.dtokens(scr.script)


For tok.dtokens = Each dtokens
If tok\parentscrpt = scr Then
Return tok
EndIf
Next
Return Null
End Function


Function getarguement.arguement(arg2.arguement)
For arg.arguement = Each arguement
If arg = arg2 Then
Return arg
EndIf
Next
Return Null
End Function


Function getifbool.ifbool(ifo2.ifbool)
For ifo.ifbool = Each ifbool
If ifo = ifo2 Then
Return ifo
EndIf
Next
Return Null
End Function

Function processcommands.dtokens(tok.dtokens,com.commands)

Select com\commandtype

Case 0
dtype$ = checktoken$(tok)
	Select dtype$
	
	Case ""
	DebugLog "I dunno what that is"
	Return Null
	
	;Case "function"
	;tok = runfunction(tok)
	;Return tok
	
	Default
	DebugLog "Let;s assign a var"
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
tok = printfunc(tok)
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


Function callvariable$(tok.dtokens)
Local scr.script,oldtok.dtokens,tokdata$,dec.declare,dnameo$
tok = After tok
If tok = Null Then
DebugLog "unexpected End of file"
Return ""
EndIf
dnameo$ = tok\dname$
tok = After tok
If tok = Null Then
DebugLog "unexpected End of file"
Return ""
EndIf

scr = getscript.script(dnameo$,tok\dname$)
If scr = Null Then
DebugLog "Source Script does not exist"
Return
EndIf
tok = After tok
If tok = Null Then
DebugLog "unexpected End of file"
Return
EndIf
oldtok = tok
tok.dtokens = New dtokens
tok\dname$ = oldtok\dname$
tok\index = 0
tok\lline = 0
tok\parentscrpt = scr
dec = finddeclaration.declare(tok)
If dec = Null Then
DebugLog "Variable does not exist"
Return ""
EndIf
Delete tok

tok = oldtok
Return dec\ddata$
End Function




Function callfunction.dtokens(tok.dtokens)
Local scr.script,oldtok.dtokens,dnameo$

tok = After tok
If tok = Null Then
DebugLog "unexpected End of file"
Return Null
EndIf
dnameo$ = tok\dname$
tok = After tok
If tok = Null Then
DebugLog "unexpected End of file"
Return Null
EndIf

scr = getscript.script(dnameo$,tok\dname$)
If scr = Null Then
DebugLog "Source Script does not exist"
Return Null
EndIf
tok = After tok
If tok = Null Then
DebugLog "unexpected End of file"
Return Null
EndIf
DebugLog "CALLING FUNCTION "+tok\dname$
oldtok = tok
tok.dtokens = New dtokens
tok\dname$ = oldtok\dname$
tok\index = 0
tok\lline = 0
tok\parentscrpt = scr

tok = runfunction(tok)
Delete tok

tok = oldtok
Return tok
End Function


Function runforloop.dtokens(tok.dtokens)

Local com.commands,loopvar,valuebegin$,valueend$
Local firstok.dtokens,lastok.dtokens
Local dec.declare,dec2.declare,dec3.declare
tok = After tok
dec = finddeclaration.declare(tok)
If dec = Null
DebugLog "Must be variable in for loop"
Return Null
EndIf
tok = After tok
If tok\dname$ <> "=" Then
DebugLog "Expecting = after For (VAR)"
Return Null
EndIf
tok = After tok
dec2 = finddeclaration.declare(tok)
If dec2 = Null Then
DebugLog "not a variable must be a value"
valuebegin$ = tok\dname$
Else
valuebegin$ = dec2\ddata$
EndIf
tok = After tok
If tok\dname$ <> "to" Then
DebugLog "expecting to after (VAR)"
Return Null
EndIf
tok = After tok
dec3 = finddeclaration.declare(tok)
If dec3 = Null Then
DebugLog "not a variable must be a value"
valueend$ = tok\dname$
Else
valueend$ = dec3\ddata$
EndIf
tok = After tok
firstok = tok
dec\ddata$ = Int(valuebegin$)
Repeat

					;For tok.dtokens = each dtokens
					DebugLog tok\dname$+" object? "
					
					If tok = Null Then Return Null
					
					;If tok2 <> Null And tok = tok2 Continue
					;For com.commands = each commands
					com = checkcommands.commands(tok\tokentype)
					If com <> Null Then
					
					tok = processcommands(tok.dtokens,com.commands)
					;Exit
					EndIf
					If tok = Null Then Return Null
					If tok <> Null Then
					tok = After tok
					EndIf
					If tok\dname$ = "next" And Int(dec\ddata$) < Int(valueend$) Then
					dec\ddata$ = Int(dec\ddata$) + 1
					tok = firstok
					EndIf

					Until tok\dname$ = "next" And Int(dec\ddata$) = Int(valueend$)
			Return tok		
End Function

Function runselectstate.dtokens(tok.dtokens)
Local valcompare$,valtype$,tempval$,deftok.dtokens,oldtok.dtokens
Local com.commands,selectstates,hasdefaultstate,defaulted=True
tok = After tok
Local dec.declare = finddeclaration.declare(tok)
If dec = Null
DebugLog "Must be variable in select statement"
Return Null
EndIf
valcompare$ = dec\ddata$
valtype$ = dec\dtype$
tok = After tok


Repeat
DebugLog "select debug current token is. "+tok\dname$
	If tok = Null Then
	DebugLog "unexpected end of file"
	Return Null
	EndIf
	com = checkcommands(tok\tokentype)
	If com = Null Then
	DebugLog "expected case statement"
	Return Null
	EndIf
	Select com\commandtype

	Case 16
		selectstates = 0
		tok = After tok
		dec.declare = finddeclaration.declare(tok)
		
	If dec = Null Then
		DebugLog "Must be just a value"
		
		If Instr(tok\dname$,";",1) <> 0 Then
		tempval$ = Replace(tok\dname$,";","")
		Else
		tempval$ = tok\dname$
		EndIf
		
			If valcompare$ = tempval$ Then
					defaulted = False
					tok = After tok
					Repeat
					;For tok.dtokens = each dtokens
					DebugLog tok\dname$+" object? "
					
					If tok = Null Then Return Null
					
					;If tok2 <> Null And tok = tok2 Continue
					;For com.commands = each commands
					com = checkcommands.commands(tok\tokentype)
					If com <> Null Then
					
					tok = processcommands(tok.dtokens,com.commands)
					;Exit
					EndIf
					If tok = Null Then Return Null
					If tok <> Null Then
					tok = After tok
					EndIf
					If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
					DebugLog "INSIDE SELECT NEXT TOKEN IS ."+tok\dname$
					Until tok\dname$ = "case" Or tok\dname$ = "exit" Or tok\dname$ = "Default" Or tok\dname$ = "EndSelect" And selectstates = 0
					;If tok\dname$ = "else" Then
					;tok = getendstatement(tok)
					;Return tok
					If tok\dname$ = "exit" Then
					Repeat
				
				tok = After tok
				
				If tok = Null Then
				DebugLog "expecting end select"
				Return Null
				EndIf
				DebugLog "INSIDE DEFAULT NEXT TOKEN IS. "+tok\dname$
					If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok\dname$ = "endselect" And selectstates = 0

					
					EndIf


			Else
			;defaulted = True
			selectstates = 0
				Repeat
				
				tok = After tok
				If tok = Null Then
				DebugLog "expecting end select"
				Return Null
				EndIf
				If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok\dname$ = "case" Or tok\dname$ = "default" Or tok\dname$ = "endselect" And selectstates = 0
			EndIf
		
		Else
		DebugLog "found variable now compare case"
		tempval$ = tok\dname$
			If valcompare$ = tempval$ Then
					defaulted = False
					tok = After tok
					Repeat
					;For tok.dtokens = each dtokens
					DebugLog tok\dname$+" object? "
					
					If tok = Null Then Return Null
					
					;If tok2 <> Null And tok = tok2 Continue
					;For com.commands = each commands
					com = checkcommands.commands(tok\tokentype)
					If com <> Null Then
					
					tok = processcommands(tok.dtokens,com.commands)
					;Exit
					EndIf
					If tok = Null Then Return Null
					If tok <> Null Then
					tok = After tok
					EndIf
					If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
						DebugLog "INSIDE SELECT NEXT TOKEN IS ."+tok\dname$
				
					Until tok\dname$ = "case" Or tok\dname$ = "exit" Or tok\dname$ = "default" Or tok\dname$ = "endselect" And selectstates = 0
					;If tok\dname$ = "else" Then
					;tok = getendstatement(tok)
					;Return tok
					If tok\dname$ = "exit" Then
					Repeat
				
				tok = After tok
				
				If tok = Null Then
				DebugLog "expecting end select"
				Return Null
				EndIf
				DebugLog "INSIDE DEFAULT NEXT TOKEN IS. "+tok\dname$
					If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok\dname$ = "endselect" And selectstates = 0

					
					EndIf

					

			Else
			;defaulted = True
			selectstates = 0
				Repeat
				
				tok = After tok
				If tok = Null Then
				DebugLog "expecting end select"
				Return Null
				EndIf
				If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok\dname$ = "case" Or tok\dname$ = "default" Or tok\dname$ = "endselect" And selectstates = 0
			EndIf
		
		
		EndIf
	
	Case 17
	DebugLog "found default"
	hasdefaultstate = True
	deftok = tok
				selectstates = 0
				Repeat
				
				tok = After tok
				
				If tok = Null Then
				DebugLog "expecting end select"
				Return Null
				EndIf
				DebugLog "INSIDE DEFAULT NEXT TOKEN IS. "+tok\dname$
					If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok\dname$ = "case" Or tok\dname$ = "default" Or tok\dname$ = "endselect" And selectstates = 0
				
	If tok\dname$ = "default" Then
	DebugLog "Select Cannot contain multiple Default Cases"
	Return Null
	EndIf

	Case 18
	DebugLog "OK now im checking the default status here . "+defaulted
	If hasdefaultstate = True And defaulted = True Then
		If deftok = Null Then
		DebugLog "unknown error occured in select statement"
		Return Null
		EndIf
	oldtok = tok
	tok = deftok
	selectstates = 0



					
					tok = After tok
					Repeat
					;For tok.dtokens = each dtokens
					DebugLog tok\dname$+" object? "
					
					If tok = Null Then Return Null
					
					;If tok2 <> Null And tok = tok2 Continue
					;For com.commands = each commands
					com = checkcommands.commands(tok\tokentype)
					If com <> Null Then
					
					tok = processcommands(tok.dtokens,com.commands)
					;Exit
					EndIf
					If tok = Null Then Return Null
					If tok <> Null Then
					tok = After tok
					EndIf
					If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
					Until tok\dname$ = "case" Or tok\dname$ = "exit" Or tok\dname$ = "default" Or tok\dname$ = "endselect" And selectstates = 0
					;If tok\dname$ = "else" Then
					;tok = getendstatement(tok)
					tok = oldtok
					;Return tok
					If tok\dname$ = "exit" Then
					Repeat
				
				tok = After tok
				
				If tok = Null Then
				DebugLog "expecting end select"
				Return Null
				EndIf
				DebugLog "INSIDE DEFAULT NEXT TOKEN IS. "+tok\dname$
					If tok\dname$ = "select" Then selectstates = selectstates + 1
					If tok\dname$ = "endselect" Then 
					selectstates = selectstates - 1
					If selectstates < 0 Then selectstates = 0
					EndIf
				
			
				Until tok\dname$ = "endselect" And selectstates = 0

					
					EndIf
	
	Else
	DebugLog "wasnt default returning "+tok\dname$
	Return tok
	
	EndIf
	DebugLog "looks like it went through returning tok. "+tok\dname$
	Return tok
	
	
	End Select
	
Until tok = Null
DebugLog "unexpected end of file"
Return Null


End Function



Function printfunc.dtokens(tok.dtokens)
tok = After tok
Local dec.declare = finddeclaration.declare(tok)
If dec = Null Then
DebugLog "just debuglog value"

Print tok\dname$
;tok = after tok
Else
Print dec\ddata$
EndIf
Return tok

End Function


Function runfunction.dtokens(tok.dtokens)
;tok = after tok
DebugLog "and the magical function name is "+tok\dname$
Local dec.declare = finddeclaration.declare(tok)
Local oldtok.dtokens,com.commands,scr.script
oldtok = tok
scr = tok\parentscrpt
If dec = Null Then
DebugLog "Function does not exist"
Return tok
EndIf
tok = invokefunctiontokens(dec)
Repeat
;For tok.dtokens = each dtokens
DebugLog tok\dname$+" object? "

If tok = Null Then Return Null

;If tok2 <> Null And tok = tok2 Continue
;For com.commands = each commands
com = checkcommands.commands(tok\tokentype)
If tok\dname$ = "return" Then 
tok = oldtok
Return tok
EndIf
If com <> Null Then

tok = processcommands(tok,com)
;Exit
EndIf

If tok <> Null Then
tok = After tok
If tok = Null Then
tok = oldtok
dismissfunctiontokens(dec)
DebugLog "end function returning next token "+tok\dname$
Return tok

EndIf
EndIf
Until tok = Null Or tok\parentscrpt <> scr
tok = oldtok
dismissfunctiontokens(dec)
DebugLog "end function returning next token "+tok\dname$
Return tok

End Function

Function dismissfunctiontokens(dec.declare)

For tok.dtokens = Each dtokens
If tok\parentfunction = dec And tok\parentscrpt = dec\parentscrpt Then
Delete tok

EndIf
Next

End Function

Function invokefunctiontokens.dtokens(dec.declare)
Local firstok.dtokens,gotfirst=False
For fok.ftokens = Each ftokens
If fok\parentfunction = dec And fok\parentscrpt = dec\parentscrpt Then
tok.dtokens = New dtokens
tok\dname$ = fok\dname$
DebugLog "adding function token "+tok\dname$
tok\index = fok\index
tok\lline = fok\lline
tok\tokentype = fok\tokentype
tok\parentfunction = fok\parentfunction
tok\parentscrpt = dec\parentscrpt
If gotfirst = False Then
firstok = tok
gotfirst = True
EndIf

EndIf
Next

Return firstok
End Function

Function checktoken$(tok.dtokens)
DebugLog "checking token "+tok\dname$
If tok\parentscrpt = Null Then
DebugLog "This token has no parent script"
EndIf
Local olddec.declare
Local dec.declare = finddeclaration.declare(tok)

If dec = Null Then
DebugLog "Expecting variable Assignment"
Return ""
Else
DebugLog "found the variable of type "+dec\dtype$
Return dec\dtype$

EndIf




End Function


Function checkcommands.commands(Data1)
DebugLog "checking the commands"
For com.commands = Each commands
If Data1 = com\commandtype Then 
Return com
EndIf
Next
Return Null
End Function


Function checkspecificcommands.commands(Data1$)
DebugLog "checking the commands"
For com.commands = Each commands
If Data1$ = com\commandname$ Then 
Return com
EndIf
Next
Return Null
End Function


Function runifstate.dtokens(tok.dtokens)
Local olddec.declare,oldtok.dtokens,com.commands
Local dec.declare,arg.arguement,foundcompare=False
Local foundthen = False
Repeat
tok = After tok
com = checkcommands(tok\tokentype)
If com <> Null Then
If com\commandtype = 0 Then com = Null
EndIf
If com = Null Then
dec = finddeclaration.declare(tok)

If dec = Null Then

	DebugLog "Not a variable\\ must be just an argument"
	arg = checkarg(tok\dname$)

	
Else
    DebugLog "found variable "+dec\dname$
	arg.arguement = New arguement
	arg\arguementtype$ = dec\dtype$
	arg\argvalue$ = dec\ddata$
	

	

EndIf
    DebugLog "lets find the operator"
	tok = After tok
	DebugLog "operator given is "+tok\dname$
	check = validateoperator(tok\dname$)
	
		If check <> 0 Then
	
		
	
		arg\argnextoperator$ = tok\dname$
	
			If check = 2 And foundcompare = False Then
			foundcompare = True
			Else If check = 2 And foundcompare = True Then
			DebugLog "Expecting Command"
			Return Null
			EndIf 
		
		
		Else
 			com = checkcommands(tok\tokentype)
			If com <> Null Then
			If com\commandtype = 0 Then com = Null
			EndIf
			If com = Null Then
			DebugLog "Expected Operator"
			Return Null
			Else
			DebugLog "looks as though we hit a command\ lets back track one token"
			arg\argnextoperator$ = tok\dname$
			tok = Before tok
			foundcompare = False
			EndIf
		EndIf
Else
Select com\commandtype

	Case 6
	foundcompare = False
	
	Case 7
	foundcompare = False
	
	Case 8
	foundcompare = False
	foundthen = True
	
	Default
	DebugLog "invalid command"
	Return Null
	
End Select
EndIf
Until foundthen = True 
DebugLog "found then"

DebugLog "lets see if the if statement is true"

DebugLog "debuglog all arguements"
For arg.arguement = Each arguement
DebugLog "arguement. "+arg\arguementtype$+" value. "+arg\argvalue$+" next operator. "+arg\argnextoperator$
Next
processarguments()
ifboolean = processifbooleans()
DebugLog "if statement is "+ifboolean
killbools()
killargs()

Select ifboolean

Case False
oldtok = tok
tok = getelsestatement.dtokens(tok)
If tok = Null Then
tok = getendstatement(oldtok)
Return tok
Else
tok = After tok
Repeat
;For tok.dtokens = each dtokens
DebugLog tok\dname$+" object? "

If tok = Null Then Return Null

;If tok2 <> Null And tok = tok2 Continue
;For com.commands = each commands
com = checkcommands.commands(tok\tokentype)
If com <> Null Then

tok = processcommands(tok.dtokens,com.commands)
;Exit
EndIf

If tok <> Null Then
tok = After tok
EndIf
Until tok\dname$ = "endif"
Return tok
EndIf


Case True
tok = After tok
Repeat
;For tok.dtokens = each dtokens
DebugLog tok\dname$+" object? "

If tok = Null Then Return Null

;If tok2 <> Null And tok = tok2 Continue
;For com.commands = each commands
com = checkcommands.commands(tok\tokentype)
If com <> Null Then

tok = processcommands(tok.dtokens,com.commands)
;Exit
EndIf
If tok = Null Then Return Null
If tok <> Null Then
tok = After tok
EndIf
Until tok\dname$ = "endif" Or tok\dname$ = "else"
If tok\dname$ = "else" Then
DebugLog "found else going to endif"
tok = getendstatement(tok)
Return tok
EndIf

End Select

End Function


Function getelsestatement.dtokens(tok.dtokens)
Local statescount = 0
Repeat
tok = After tok

If tok = Null Then
DebugLog "unepected end of file"
Return Null
EndIf
If tok\dname$ = "if" Then statescount = statescount + 1
If tok\dname$ = "endif" And statescount > 0 Then statescount = statescount - 1
If tok\dname$ = "endif" And statescount = 0 Then Return Null
Until tok\dname$ = "else" And statescount = 0
End Function

Function getendstatement.dtokens(tok.dtokens)
Local statescount=0

Repeat

tok = After tok
If tok = Null Then
DebugLog "unepected end of file"
Return Null
EndIf

If tok\dname$ = "if" Then statescount = statescount + 1
If tok\dname$ = "endif" And statescount > 0 Then statescount = statescount - 1


Until tok\dname$ = "endif" And statescount = 0
Return tok
End Function

Function processarguments()
Local arg.arguement,arg2.arguement,oldvalue$,oldvaluetype$,arguementbool=False
Local nextoperator$,com.commands,narg.arguement
arg2.arguement = First arguement
arg = getarguement(arg2)

If arg = Null Then
DebugLog "I dont know how the hell it happened but i lost my arguements"
Return False
EndIf
oldvalue$ = arg\argvalue$
oldvaluetype$ = arg\arguementtype$
nextoperator$ = arg\argnextoperator$
Repeat 
nextoperator$ = arg\argnextoperator$
arg = After arg
operationtype = validateoperator(nextoperator$)

	Select operationtype
	Case 1
	DebugLog "old value "+oldvalue$
	oldvalue$ = mathoperation$(oldvalue$,arg\argvalue$,oldvaluetype$,arg\arguementtype$,nextoperator$)
	DebugLog "new value "+oldvalue$
	
	Case 2
	DebugLog "before bool argvalue is "+arg\argvalue$+" vs "+oldvalue$
	arguementbool = booloperation(oldvalue$,arg\argvalue$,oldvaluetype$,arg\arguementtype$,nextoperator$)
	DebugLog "bool "+arguementbool
	
	Default
	DebugLog "may be an operative command"
	com = checkspecificcommands(nextoperator$)
	If com = Null Then 
	DebugLog "Somehow it got screwed up"
	Return False		
	EndIf
	DebugLog "Ok it is a command\ now to see which one"
	
		Select com\commandtype
		
			Case 6
			DebugLog "found "+com\commandname$
			ifo.ifbool = New ifbool
			ifo\bool = arguementbool
			ifo\boolexp = com\commandtype
			;arg = After arg
			
			oldvalue$ = arg\argvalue$
			oldvaluetype$ = arg\arguementtype$
			DebugLog "AFTER AND NEXT OPERATOR IS "+arg\argnextoperator$
			
			Case 7
			DebugLog "found "+com\commandname$
			ifo.ifbool = New ifbool
			ifo\bool = arguementbool
			ifo\boolexp = com\commandtype
			;arg = After arg
			oldvalue$ = arg\argvalue$
			oldvaluetype$ = arg\arguementtype$
			
			Case 8
			DebugLog "found "+com\commandname$
			ifo.ifbool = New ifbool
			ifo\bool = arguementbool
			ifo\boolexp = com\commandtype
			arg = Null
			
			
			
			Default
			DebugLog "invalid expression in if statement"
			Return False
			Exit
		End Select
	
	End Select

Until arg = Null


End Function


Function killargs()
For arg.arguement = Each arguement
Delete arg

Next
End Function

Function killbools()
For ifo.ifbool = Each ifbool
Delete ifo
Next
End Function

Function processifbooleans()
Local ifo.ifbool,ifo2.ifbool,locbool=True
Local oldifo.ifbool
ifo2 = First ifbool
ifo = getifbool(ifo2)

If ifo = Null Then
DebugLog "Oh man this must be Stupid to lose the booleans"
Return False
EndIf
oldifo = ifo
If oldifo\bool = False Then locbool = False
Repeat

oldifo = ifo
ifo = After ifo

If ifo = Null Then Return locbool
If ifo <> Null Then
Select oldifo\boolexp

Case 6
If ifo\bool = False locbool = False

Case 7
If ifo\bool = True locbool = True

Case 8
Return locbool

Default 
DebugLog "Expecting Expression"
Return False

End Select

EndIf

Until oldifo\boolexp = 8

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

End Function


Function validateoperator(Data1$)
Local validated = False
Local expect = 0

If Instr(OPERATORS$,Data1$,1) <> 0 Then
Select Data1$
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


Function checkarg.arguement(Data1$)

If Instr(Data1$,";",1) <> 0 Then
arg.arguement = New arguement
arg\arguementtype$ = "string"
arg\argvalue$ = Replace(Data1$,";","")
Return arg
EndIf

If Instr(Data1$,"\",1) <> 0 Then
arg.arguement = New arguement
arg\arguementtype$ = "float"
arg\argvalue$ = Data1$
Return arg
EndIf

arg.arguement = New arguement
arg\arguementtype$ = "int"
arg\argvalue$ = Data1$
Return arg




End Function

Function assignvar.dtokens(tok.dtokens)
Local olddec.declare,oldtok.dtokens,com.commands
Local dec.declare = finddeclaration.declare(tok)
oldtok = tok
DebugLog "assinging var data to "+tok\dname$
tok = After tok

If tok = Null Then
	DebugLog "may possibly be just a function"
	If dec\dtype$ = "function" Then
	DebugLog "found function running script"
	tok = Before tok
	tok = runfunction(tok)
	Return tok
	Else
	DebugLog "Unexprected end of file"
	Return Null
	EndIf
EndIf

DebugLog oldtok\dname$
DebugLog "next field after "+oldtok\dname$+" is "+tok\dname$
If tok\dname$ = "=" Then
DebugLog "found ="
olddec = dec

Select olddec\dtype$

Case "int"

Repeat 

tok = After tok
If tok\lline = oldtok\lline Then

		DebugLog "found integer next "+tok\dname$
		Select tok\dname$
		
		
		Case "+"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Int(olddec\ddata$)+Int(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Int(olddec\ddata$) + Int(callvariable$(tok))
		tok = After tok
		tok = After tok
		
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		End Select 
		
		EndIf
		Else
		olddec\ddata$ = Int(olddec\ddata$)+Int(dec\ddata$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		EndIf
		
		Case "-"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Int(olddec\ddata$)-Int(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Int(olddec\ddata$) - Int(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 
		EndIf
		Else
		olddec\ddata$ = Int(olddec\ddata$)-Int(dec\ddata$)
		EndIf
		
		Case "*"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
				com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Int(olddec\ddata$)*Int(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Int(olddec\ddata$) * Int(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 
		EndIf
		Else
		olddec\ddata$ = Int(olddec\ddata$)*Int(dec\ddata$)
		EndIf
		
		Case "/"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Int(olddec\ddata$)/Int(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Int(olddec\ddata$) / Int(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 	
		EndIf	
		Else
		olddec\ddata$ = Int(olddec\ddata$)/Int(dec\ddata$)
		EndIf
		
		Default
		DebugLog "found numerical expression of "+tok\dname$
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Int(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Int(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 		
		EndIf		
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		Else
		olddec\ddata$ = Int(dec\ddata$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		EndIf
		
		End Select
EndIf
Until tok\lline > oldtok\lline


Case "float"
Repeat
tok = After tok
If tok\lline = oldtok\lline Then

		Select tok\dname$
		
		
		Case "+"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Float(olddec\ddata$)+Float(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Float(olddec\ddata$) + Float(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 		
		EndIf
		Else
		olddec\ddata$ = Float(olddec\ddata$)+Float(dec\ddata$)
		EndIf
		
		Case "-"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Float(olddec\ddata$)-Float(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Float(olddec\ddata$) - Float(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 		
		EndIf
		Else
		olddec\ddata$ = Float(olddec\ddata$)-Float(dec\ddata$)
		EndIf
		
		Case "*"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Float(olddec\ddata$)*Float(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Float(olddec\ddata$) * Float(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 		
		EndIf
		Else
		olddec\ddata$ = Float(olddec\ddata$)*Float(dec\ddata$)
		EndIf
		
		Case "/"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Float(olddec\ddata$)/Float(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Float(olddec\ddata$) / Float(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 		
		EndIf
		Else
		olddec\ddata$ = Float(olddec\ddata$)/Float(dec\ddata$)
		EndIf
		
		Default
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = Int(tok\dname$)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = Int(callvariable$(tok))
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 		
		EndIf		
		Else
		olddec\ddata$ = Float(dec\ddata$)
		EndIf
		End Select
EndIf
Until tok\lline > oldtok\lline


Case "string"
DebugLog "assinging string data to "+olddec\dname$
Repeat
tok = After tok
DebugLog tok\dname$+" line is "+tok\lline+" "+oldtok\dname$+" line is "+oldtok\lline
If tok\lline = oldtok\lline Then
		Select tok\dname$
		
		
		Case "+"
		tok = After tok
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = olddec\ddata$ + tok\dname$
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = olddec\ddata$ + callvariable$(tok)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 		
		EndIf
		Else
		olddec\ddata$ = olddec\ddata$+dec\ddata$
		EndIf
		olddec\ddata$ = Replace(olddec\ddata$,";","")
		
		Default
		dec = finddeclaration.declare(tok)
		If dec = Null Then
		com = checkcommands(tok\tokentype)
		If com <> Null Then
		
		Select com\commandtype
		
		Case 0
		olddec\ddata$ = tok\dname$
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		
		
		Case 25
		olddec\ddata$ = callvariable$(tok)
		DebugLog olddec\dname$+" int = "+olddec\ddata$
		tok = After tok
		tok = After tok
		
		End Select 		
		EndIf
		Else
		olddec\ddata$ = dec\ddata$
		EndIf
		olddec\ddata$ = Replace(olddec\ddata$,";","")
		DebugLog olddec\ddata$
		End Select
EndIf
Until tok\lline > oldtok\lline


End Select
DebugLog "next item is "+tok\dname$
Return Before tok 
Else
If dec\dtype$ = "function" Then
DebugLog "found function running script"
tok = Before tok
tok = runfunction(tok)
Return tok
Else
DebugLog "expecting operator"
Return Null
EndIf
EndIf

End Function

Function declarevar.dtokens(tok.dtokens)
Local oldtok.dtokens
dec.declare = New declare
tok = After tok
If tok = Null Then
DebugLog "Expecting VAR type"
Return Null
EndIf
dec\dtype$ = tok\dname$
dec\parentscrpt = tok\parentscrpt
tok = After tok
If tok = Null Then
DebugLog "Expecting Var Name"
Return
EndIf
dec\dname$ = tok\dname$

oldtok = tok
If dec\dtype$ = "function" Then
addfunctiontokens(dec,tok)
DebugLog "added new function "+dec\dname$
EndIf

DebugLog "Found Variable type "+dec\dtype$+" var name "+dec\dname$
Return tok
End Function


Function addfunctiontokens(dec.declare,tok.dtokens)
Local fok.ftokens
Local killtok.dtokens
tok = findfunction(dec,tok)

killtok = tok

tok = After tok
DebugLog "about to kill token "+killtok\dname$
Delete killtok



Repeat

fok.ftokens = New ftokens
fok\dname$ = tok\dname$
fok\index = tok\index
fok\lline = tok\lline
fok\tokentype = tok\tokentype
fok\parentfunction = dec
fok\parentscrpt = tok\parentscrpt
killtok = tok
tok = After tok
DebugLog "about to kill token "+killtok\dname$
Delete killtok


If tok = Null Then
DebugLog "unexpected end of file"
Return
EndIf
Until tok\dname$ = "endfunction"
Delete tok


End Function


Function findfunction.dtokens(dec.declare,tok.dtokens)
Local foundfunc = False
Local killtok.dtokens
Repeat
tok = After tok
If tok\dname$ = "function" Then
tok = After tok
If tok\dname$ = dec\dname$ Then
killtok = Before tok
Delete killtok

Return tok
EndIf
EndIf



Until tok = Null
Return Null
End Function


Function tokenizescript(scriptname$="myscript",shandle$="QWERTYUIOP",scriptfile$="script.txt")
Local templine$,toffset,temptoken$
Local Stream = OpenFile(scriptfile$)
Local index = 0,gotstring = False,gotendstring = False
Local Line = 0
scr.script = New script
scr\scriptname$ = scriptname$
scr\scripthandle$ = shandle$
While Not Eof(Stream)
templine$ = ReadLine(Stream)
Line = Line + 1
DebugLog "line. "+Line
;debuglog "len. "+Len(templine$)
For tempfor = 1 To Len(templine$)
;debuglog tempfor
If Mid$( templine$,tempfor,1 ) = ";" Then
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
tok.dtokens = New dtokens
tok\dname$ = Trim(temptoken$)
tok\index = index
tok\lline = Line
tok\parentscrpt = scr
If tok\parentscrpt = Null Then
DebugLog "Uh oh this has no parent script"
End
EndIf
index = index + 1
For com.commands = Each commands
If Lower(tok\dname$) = Lower(com\commandname$) Then
tok\tokentype = com\commandtype


EndIf

Next

tok\index = index
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

For tok.dtokens = Each dtokens
DebugLog tok\dname$
Next

End Function



Function loadcommands()
Local tempfor,templine$,temptoken$
Local Stream = OpenFile("commands.txt")
While Not Eof(Stream)
templine$ = ReadLine(Stream)
For tempfor = 1 To Len(templine$)
If Mid$( templine$,tempfor,1 ) <> " " Then
temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )
;debuglog temptoken$
Else
;temptoken$ = temptoken$ + Mid$( templine$,tempfor,1 )

com.commands = New commands
com\commandtype = Int(temptoken$)
com\commandname$ = Trim(Replace(templine$,temptoken$,"")) 
;debuglog com\commandtype
DebugLog com\commandname$
temptoken$ = ""
Exit
EndIf

Next
Wend
CloseFile(Stream)


End Function





Function finddeclaration.declare(tok.dtokens)
Local foundit = False
For dec.declare = Each declare
	If dec\dname$ = tok\dname$ And tok\parentscrpt = dec\parentscrpt Then
	Return dec
	foundit = True
	EndIf
	
	Next

Return Null
End Function
