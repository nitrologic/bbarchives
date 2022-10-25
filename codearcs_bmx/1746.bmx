; ID: 1746
; Author: CS_TBL
; Date: 2006-07-06 08:06:27
; Title: txt2string
; Description: converts a textfile into a string

SuperStrict

DebugLog Chr(10)+Chr(10)+txt2string("C:\Somefiles\somefile.txt","Local MyText$",-1,64)+Chr(10)+Chr(10)

End

Function txt2string$(file$,varname$="Local MyText$",ndx:Int=-1,width:Int=0)

Rem
	txt2string$, by CS_TBL
	
	This function converts a textfile to a string.
	
	command:
	
	parsedtext=txt2string$(file$,varname$,ndx,width)
	
	file$		- the full path of a file (e.g. "C:\My Documents\blahblah.txt")
	varname$	- optional, the name of the variable
	ndx			- optional, if >=0 then the variable is an array, and ndx is its index
	width		- optional, the width of the formatted stringparts, width<1 doesn't format
				- in case of formatting, a line *can* incidentally be one character longer due
				- not being allowing to break escapes
	
	If you want to create a new variable, don't forget to add 'Local ' in the varname!
	(e.g. bla$=txt2string(file$,"Local MyText")
	
EndRem

	If file="" Return "<no file given>"	
	
	Local tmp:TStream=ReadFile(file)
	If Not tmp
		tmp=Null
		Return "<no file found>"
	EndIf
	CloseFile tmp
	
	Local bank:TBank=LoadBank(file)	
	
	If BankSize(bank)=0
		bank=Null
		Return "<no data found>"
	EndIf
	
	Local text$
	Local decent$
	
	Local t:Int,v:Int,w:Int
	
	' make the string content
	
	For t=0 To BankSize(bank)-1
		v=PeekByte(bank,t)		
		Select v
			Case 9
				text:+"~~t"
			Case 10
				text:+"~~n"
			Case 13
				text:+"~~r"
			Case 34
				text:+"~~q"
			Default
				text:+Chr(v)
		End Select	
	Next
	
	' now split-up into something decent
	
	decent:+varname ' the filename
	
	If ndx>=0 decent:+"["+ndx+"]" ' optional array-index
	
	If width<1
		decent:+"=~q"
		decent:+text
	Else
		decent:+"=.."
		For t=0 To Len(text)-1
			If Not (w Mod width) ' newline
				If t>0
					If Asc(Right(decent,1))=126 ' an escape we shouln't break?
						decent:+Mid(text,t+1,1)+Chr(34)+"+.."
					Else ' no? just continue then..
						decent:+Chr(34)+"+.."				
					EndIf
				EndIf
				decent:+Chr(10)+Chr(34)
			EndIf
			decent:+Mid(text,t+1,1)
			w:+1
		Next
	EndIf
		
	decent:+Chr(34) ' and the closing quote!
	
	bank=Null
	Return decent
End Function
