; ID: 1215
; Author: Chroma
; Date: 2004-12-01 13:23:42
; Title: Short Dotted IP to Integer IP Converter
; Description: Just trying to get this as small as possible.

Function DotToInt%(ip$)
	off1=Instr(ip$,".")	  :ip1=Left$(ip$,off1-1)
	off2=Instr(ip$,".",off1+1):ip2=Mid$(ip$,off1+1,off2-off1-1)
	off3=Instr(ip$,".",off2+1):ip3=Mid$(ip$,off2+1,off3-off2-1)
	off4=Instr(ip$," ",off3+1):ip4=Mid$(ip$,off3+1,off4-off3-1)
	Return ip1 Shl 24 + ip2 Shl 16 + ip3 Shl 8 + ip4
End Function
