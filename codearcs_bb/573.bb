; ID: 573
; Author: skn3[ac]
; Date: 2003-02-04 07:57:45
; Title: Format a number
; Description: Format$("345233","#,###.##")

Print Format$("345233","#,###.##")
WaitKey()
Function Format$(Number,FormatString$)
	MakeFormat$=""
	NumberCount=1
	For I=1 To Len(FormatString$)
		GetFormatChar$=Mid$(FormatString$,I,1)
		Select GetFormatChar$
			Case ","
				MakeFormat$=MakeFormat$+","
			Case "."
				MakeFormat$=MakeFormat$+"."
			Case "#"
				If NumberCount > Len(Number) Then
					GetNumberChar$="0"
				Else
					GetNumberChar$=Mid$(Number,NumberCount,1)
				End If
				NumberCount=NumberCount+1
				MakeFormat$=MakeFormat$+GetNumberChar$
		End Select
	Next
	Return MakeFormat$
End Function
