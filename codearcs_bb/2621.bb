; ID: 2621
; Author: neos300
; Date: 2009-12-01 18:00:21
; Title: Simple INI Getter
; Description: Gets a field from an ini file

Function getinifield(file$, group$, fiield$)
fil = ReadFile(file)
Local p$, ingroup, groupname$,f$,rest$
While Not Eof(fil)
.mix
p = ReadLine(fil)
If Left(p, 2) = "//" Or p = "" Then Goto mix
If Left(p, 1) = "["
ingroup = 1
groupname = Mid(p, 2, Len(p) - 2)
Else
If ingroup = 1
equal = Instr(p, "=")
f = Left(p, equal - 1)
rest = Mid(p, equal + 1)
Else Goto mix
EndIf
EndIf
If groupname = group And fiield = Trim(f) Then Return Trim(rest)
Wend
End Function
