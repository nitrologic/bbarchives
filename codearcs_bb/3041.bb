; ID: 3041
; Author: Bobysait
; Date: 2013-03-19 18:02:31
; Title: Fast ATan2 (4° precision)
; Description: calculates angle using ATan2 method faster than blitz command

Function Atan2F#(y#, x#)
	If y=0 :If x=0 :Return 0:EndIf:Return 180:EndIf
	Local abs_y#=Abs(y):If(x>=0.0):Return(45+45*(abs_y-x)/(x+abs_y))*Sgn(y):EndIf:Return(135+45*(x+abs_y)/(x-abs_y))*Sgn(y)
End Function







; exemple : check vectors are aligned
Type v
	Field x#,y#
End Type

For n = 1 To 2000
	v.v=New v
	v\x=Rnd(-1,1)
	v\y=Rnd(-1,1)
Next

Local NbAligned=0
Local NbAlignedF=0
Local angle#,angleF#

Local t0 = MilliSecs()
For a.v = Each v
	angle=ATan2(a\y,a\x)
	For b.v=Each v
		If a<>b
			If Abs(angle-ATan2(b\y,b\x))<0.0001
				NbAligned=NbAligned+1
			EndIf
		EndIf
	Next
Next

Local t1 = MilliSecs()
For a.v = Each v
	angleF=Atan2F(a\y,a\x)
	angle=ATan2(a\y,a\x)
	For b.v=Each v
		If a<>b
			If Abs(angleF-Atan2F(b\y,b\x))<4.08
				If Abs(angle-ATan2(b\y,b\x))<0.0001
					NbAlignedF=NbAlignedF+1
				EndIf
			EndIf
		EndIf
	Next
Next

Local t2 = MilliSecs()

Print "NbAligned  = "+NbAligned
Print " time      = "+(t1-t0)
Print ""
Print "NbAlignedF = "+NbAlignedF
Print " time      = "+(t2-t1)
WaitKey
End
