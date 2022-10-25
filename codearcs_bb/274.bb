; ID: 274
; Author: ford escort
; Date: 2002-03-18 11:35:12
; Title: reformat path in dos 8.3 style
; Description: this function can be usefull to pass long filenames to dos batch  or apps

Dim value(0)
;
;this function return a path in dos 8.3 format
;addin the unfamous ~1 stuff in the path
;
Function shorten$(pa$)
spat$=""
part$=""
For az=1 To Len(pa$)
	Select Mid$(pa$,az,1)
		Case "\"
			If Len(part$)>8
		
	part$=Left$(part$,6)+"~1"+"\"
			Else
				part$=part$+"\"	
			EndIf
			spat$=spat$+part$			
			part$=""
		Case "."
			If Len(part$)>8
				part$=Left$(part$,6)+"~1"+"."
			Else
				part$=part$+"."
			EndIf
			spat$=spat$+part$
			part$=""
		Default
			part$=part$+Mid$(pa$,az,1)
		End Select
Next
If Len(part$)>8
	part$=Left$(part$,6)+"~1"
EndIf
spat$=spat$+part$
If FileType(spat$)=0
For az=1 To Len(spat$)
	If Mid$(spat$,az,2)="~1"
		pos$=pos$+Chr$(az)
	EndIf
Next
Dim value(Len(pos$))
Repeat
value(0)=10
For va=1 To Len(pos$)
	If value(va-1)=10
		value(va)=value(va)+1
		value(va-1)=1
	EndIf
Next 
For ch=1 To Len(pos$)
di$=Left$(spat$,Asc(Mid$(pos$,ch,1))-1)+"~"+Str$(value(ch))+"\"+Right$(spat$,Len(spat$)-(Asc(Mid$(pos$,2,1))+2))
Print di$
Next 
Until FileType(di$)<>0
Return di$
EndIf
Return spat$
End Function
