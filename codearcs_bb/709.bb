; ID: 709
; Author: Jedive
; Date: 2003-05-31 17:21:19
; Title: IniLib
; Description: A library to load and write .ini files

;*************************************************
; INI Access BlitzBasic library
; Written by Javier San Juan Cervera -- Jedive
; www.softiberia.tk
;*************************************************

Function IniGroup(IniHandle,Group$)
SeekFile IniHandle,0
If Group$="" Then Return True
While Not Eof(IniHandle)
	If Lower$(ReadLine$(IniHandle))="["+Lower$(Group$)+"]" Then Return True
Wend
Return False
End Function

;*************************************************

Function ReadIniField$(IniHandle,Key$,DefVal$="")
While Chr$(char)<>"]" And FilePos(IniHandle)>0
	SeekFile IniHandle,FilePos(IniHandle)-1
	char=ReadByte(IniHandle)
	If Chr$(char)<>"]" Then SeekFile IniHandle,FilePos(IniHandle)-1
Wend
ReadLine$(IniHandle)
While Not Eof(IniHandle)
	lin$=Trim$(ReadLine$(IniHandle))
	If Left$(lin$,1)="[" Then Return DefVal$
	If Left$(lin$,1)<>"#" And Left$(lin$,1)<>"" Then If Lower$(Left$(lin$,Instr(lin$,"=",1)-1))=Lower$(Key$) Then Return Right$(lin$,Len(lin$)-Instr(lin$,"=",1))
Wend
Return DefVal$
End Function

;*************************************************

Function WriteIniComment(IniHandle,Comment$)
WriteLine IniHandle,"#"+Comment$
End Function

;*************************************************

Function WriteIniGroup(IniHandle,Group$)
WriteLine IniHandle,"["+Group$+"]"
End Function

;*************************************************

Function WriteIniField(IniHandle,Key$,Value$)
WriteLine IniHandle,Key$+"="+Value$
End Function

;*************************************************
