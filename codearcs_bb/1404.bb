; ID: 1404
; Author: Cygnus
; Date: 2005-06-20 18:19:06
; Title: Shortcut Finder
; Description: Find the REAL file from a windows .LNK (shortcut) file!

Function getshortcut$(file$)
Local EXT$=".MP3"
Local ret$,ft,ln$,lnv,ok,ook,filename$,fl,lnb
FT=FileType(file$)
If FT=1 Then
	FL=ReadFile(File$)
	Repeat
		LNb=ReadByte(FL)
		ln$=ln$+Chr$(lnb)
	Until Eof(fl)
	LNV=Len(LN$)-1
	Repeat:OK=0
		LNV=LNV-1:If LNV<1 Then LNV=1:OK=2
		If Mid$(LN$,LNV,1)=":" And Mid$(LN$,LNV+1,1)="\" Then
			ook=ok:ok=1:filename$=Mid$(LN$,LNV-1)
			eol=Instr(Upper$(filename$),ext$):If eol>0 Then filename$=Mid$(filename$,1,eol+3)
			If FileType(filename$)<>1 Then ok=ook
		EndIf
	Until OK<>0
EndIf
If FileType(filename$)=1 Then ret$=filename$
Return ret$
End Function
