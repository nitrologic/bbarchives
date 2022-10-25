; ID: 2613
; Author: neos300
; Date: 2009-11-23 16:22:11
; Title: Get vars from a string
; Description: Now anyone can make a scripting language!

Function StrVars$(p$)
Local noleft$,in%,rest$,space%,var$,kip$, retstr$
If Left(p$, 1) = dqt$
	noleft = Right(p$, Len(p) - 1)
	;Print noleft
	in = Instr(noleft, dqt$)
	;Print in
	If in <> 0
		retstr = retstr + Left(noleft, in - 1)
		;Print retstr
		If in <> Len(noleft)
			in = Instr(noleft, "+")
			rest = Mid(noleft, in, Len(noleft))
			;Print rest
			If Left(rest, 1) = "+"
				rest = Right(rest, Len(rest) - 2)
				space = Instr(rest, " ", 1)
				If space <> 0
					var = Left(rest,space-1)
					kip = Mid(rest,space+1)
				EndIf
				;Print var
				;Print kip
				retstr = retstr + GetEnv(var)
				;Print retstr
				If Left(kip, 1) = "+"
					rest = Right(kip, Len(kip) - 1)
					rest = Trim(rest)
					rest = Left(rest, Len(rest) - 1)
					rest = Right(rest, Len(rest) - 1)
					;Print rest
					retstr = retstr + rest
				EndIf
			EndIf
		Else
			Return Left(noleft, Len(noleft) - 1)
		EndIf
	Else
		AddError("No ending " + dqt$ + "!", "StrVars")
	EndIf
Else
	Return GetEnv$(p$)
EndIf
Return retstr
End Function
