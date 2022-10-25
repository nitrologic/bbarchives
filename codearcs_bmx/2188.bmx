; ID: 2188
; Author: Space Fractal
; Date: 2008-01-18 14:52:58
; Title: UTF8 &lt;-&gt; Unicode converter
; Description: Just a another UTF8 <-> Unicode converter

Function UTF8$(Unicode$)
	Local RESULT$=""
	
	For Local i=1 To Len(Unicode$)
		Local Char$=Mid$(Unicode$,i,1)
		If Asc(Char$)<128
			result$=result$+char$
		ElseIf Asc(Char$)>127 And Asc(Char$)<2048
			Local Bytes$=Right$(Bin$(Asc(Char$)),11)

			Local Byte1$="110"+Left$(Bytes$,5)
			Result$=Result$+Chr(Bin2Int(Byte1$))

			Local Byte2$="10"+Right$(Bytes$, 6)
			Result$=Result$+Chr(Bin2Int(Byte2$))
		Else
			Local Bytes$=Right$(Bin$(Asc(Char$)),16)

			Local Byte1$="1110"+Left$(Bytes$,4)
			Result$=Result$+Chr(Bin2Int(Byte1$))

			Local Byte2$="10"+Mid$(Bytes$, 5, 6)
			Result$=Result$+Chr(Bin2Int(Byte2$))

			Local Byte3$="10"+Right$(Bytes$, 6)
			Result$=Result$+Chr(Bin2Int(Byte3$))		
		EndIf
	Next
	Return Result$
EndFunction

Function Unicode$(UTF8$)
	Local RESULT$=""
	Local UTF$=""
	Local Length=0
	Local Last$=""
	For Local i=1 To Len(UTF8$)
		Local Char$=Mid$(UTF8$,i,1)
		Local B$=Right$(Bin$(Asc(Char$)),8)
		If Length>0
			UTF$=UTF$+Right$(B$,6)
			Length:-1
			If Left$(B$,2)<>"10"
				Length=0
				RESULT$=RESULT$+Last$
				Last$=""
			ElseIf Length=0
				RESULT$=RESULT$+Chr$(Bin2Int(UTF$))
			EndIf
		EndIf
			
		If Length=0
			If Left$(B$,1)="0"
				Result$=Result$+Char$; Length=0
			ElseIf Left$(B$,3)="110"
				Last$=CHAR$
				UTF$=Right$(B$,5)
				Length=1
			ElseIf Left$(B$,4)="1110"
				UTF$=Right$(B$,5)
				Length=2
			ElseIf Left$(B$,4)="11110"
				UTF$=Right$(B$,5)
				Length=3
			EndIf
		EndIf
	Next
	Return RESULT$
EndFunction

' a slow help function ;-), but it do that job
Function Bin2Int:Int(Binary$)
	Local result=0
	Local D=1
	For Local i=Len(Binary$) To 1 Step -1
		If Mid$(Binary$,i,1)="1" Then result=result+d
		D=D+D
	Next
	Return result
EndFunction
