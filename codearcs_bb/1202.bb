; ID: 1202
; Author: Luke.H
; Date: 2004-11-19 06:21:12
; Title: Cheak if 2 lines cross
; Description: Cheak if 2 lines cross

Graphics 640,480

SetBuffer BackBuffer()

Global Mouse_X#=320
Global Mouse_Y#=200

Global X#,Y#;Not needed


AX1#=90
AY1#=100
AX2#=70
AY2#=60

BX1#=290
BY1#=200
BX2#=270
BY2#=260

While Not KeyDown(1)
Cls

;------Start of loop-------

If LinesCross(AX1#,AY1#,AX2#,AY2#,BX1#,BY1#,BX2#,BY2#) Then

Color 255,255,255

Oval X#-5,Y#-5,10,10


Color 255,0,0
Else
Color 0,255,0
End If

Line AX1#,AY1#,AX2#,AY2#

Line BX1#,BY1#,BX2#,BY2#

If MouseDown(1) Then

Mouse_X#=MouseX()
Mouse_Y#=MouseY()

If Dis(Mouse_X#,Mouse_Y#,AX1#,AY1#)<25 Then
AX1#=Mouse_X#
AY1#=Mouse_Y#
ElseIf Dis(Mouse_X#,Mouse_Y#,AX2#,AY2#)<25 Then
AX2#=Mouse_X#
AY2#=Mouse_Y#
ElseIf Dis(Mouse_X#,Mouse_Y#,BX1#,BY1#)<25 Then
BX1#=Mouse_X#
BY1#=Mouse_Y#
ElseIf Dis(Mouse_X#,Mouse_Y#,BX2#,BY2#)<25 Then
BX2#=Mouse_X#
BY2#=Mouse_Y#
End If

End If

Color 255,255,255
Text 10,10,"Click and hold the mouse to drag the lines"


Flip
Wend
End

Function LinesCross(AX1#,AY1#,AX2#,AY2#,BX1#,BY1#,BX2#,BY2#)


If AX1#<AX2# Then				;\
NAX1#=AX1#:NAX2#=AX2#			;|
NAY1#=AY1#:NAY2#=AY2#			;|
Else:NAX1#=AX2#:NAX2#=AX1#		;| Work out points
NAY1#=AY2#:NAY2#=AY1#:End If	;|
If BX1#<BX2# Then				;|
NBX1#=BX1#:NBX2#=BX2#			;|
NBY1#=BY1#:NBY2#=BY2#			;|
Else:NBX1#=BX2#:NBX2#=BX1#		;|
NBY1#=BY2#:NBY2#=BY1#:End If	;/

MA#=(AY2#-AY1#)/(AX2#-AX1#)		;\ Work out slope
MB#=(BY2#-BY1#)/(BX2#-BX1#)		;/

CA#=NAY1#-(NAX1#*MA#)	;\ Work out y-intercept
CB#=NBY1#-(NBX1#*MB#)	;/

If MA#=MB# Then;Are they Parallel				;\
If CA#=CB# Then;Are they the same				;|
Return 1;Infinite points of intersection		;| Parallel Lines
Else											;|
Return 0;No point of intersection				;|
End If											;|
End If											;/

;---------------------Find out intercept---------------------

If AX1#=AX2# Then			;\
Y#=MB#*AX1#+CB#				;| One vertical line is used
X#=AX1#						;| 
ElseIf BX1#=BX2# Then		;|
Y#=MA#*BX1#+CA#				;|
X#=BX1#						;/

ElseIf AY1#=AY2# Then		;\
Y#=AY1#						;|
X#=(Y#-CB#)/MB#				;| One horizontal line is used
ElseIf BY1#=BY2# Then		;|
Y#=BY1#						;|
X#=(Y#-CA#)/MA#				;/

Else						;\
X#=(CB#-CA#)/(MA#-MB#)		;| normal line is used
Y#=X#*MA#+CA#				;|
End If						;/

;---------------------Cheak line Ranges---------------------

If AX1#<AX2# Then						;\
CheakAX1#=AX1#:CheakAX2#=AX2#			;|
Else:CheakAX1#=AX2#:CheakAX2#=AX1#		;|
End If:If AY1#<AY2# Then				;| Make points smallest first
CheakAY1#=AY1#:CheakAY2#=AY2#			;| by swaping
Else:CheakAY1#=AY2#:CheakAY2#=AY1#		;|
End If:If BX1#<BX2# Then				;|
CheakBX1#=BX1#:CheakBX2#=BX2#			;|
Else:CheakBX1#=BX2#:CheakBX2#=BX1#		;|
End If:If BY1#<BY2# Then				;|
CheakBY1#=BY1#:CheakBY2#=BY2#			;|
Else:CheakBY1#=BY2#:CheakBY2#=BY1#		;|
End If									;/

If CheakAX1#<=X# Then	;\
If CheakAX2#>=X# Then	;|
If CheakAY1#<=Y# Then	;| Cheak intercept point
If CheakAY2#>=Y# Then	;| with line ranges
If CheakBX1#<=X# Then	;|
If CheakBX2#>=X# Then	;|
If CheakBY1#<=Y# Then	;|
If CheakBY2#>=Y# Then	;/
Return 1;The lines are crossing
End If:End If:End If:End If
End If:End If:End If:End If

Return 0;The lines are not crossing

End Function


Function Dis#(X#,Y#,TX#,TY#)
Return Abs(((TX#-X#)*(TX#-X#)+(TY#-Y#)*(TY#-Y#))^0.5)
End Function
