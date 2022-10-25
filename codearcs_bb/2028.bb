; ID: 2028
; Author: _33
; Date: 2007-06-02 02:07:20
; Title: Display ASCII chart
; Description: ALl it does is display the ASCII chart

Graphics 800,600,32,2
j = 0
For i = 0 To 255
 Write Str$(i) + " = " + Chr$(i) + "   "
j = j + 1: If j = 10 Then Print "": j = 0
Next
WaitKey()
