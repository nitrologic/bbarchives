; ID: 2131
; Author: Subirenihil
; Date: 2007-10-29 21:10:48
; Title: Intersections
; Description: Get intersections between line segments and arcs

Graphics 1280,1024,32,1
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Type pt
	Field x#,y#
End Type

Type ln
	Field p1.pt,p2.pt
End Type

Type arc
	Field c.pt,r#,a1#,a2#
End Type

p1.pt=New pt
p1\x#=Rnd#(0,1280)
p1\y#=Rnd#(0,1024)

l1.ln=New ln
If l1\p1=Null Then l1\p1=New pt
If l1\p2=Null Then l1\p2=New pt
l1\p1\x#=Rnd#(0,1280)
l1\p1\y#=Rnd#(0,1024)
l1\p2\x#=640;Rnd#(0,1280)
l1\p2\y#=512;Rnd#(0,1024)

l2.ln=New ln
If l2\p1=Null Then l2\p1=New pt
If l2\p2=Null Then l2\p2=New pt
l2\p1\x#=Rnd#(0,1280)
l2\p1\y#=Rnd#(0,1024)
l2\p2\x#=Rnd#(0,1280)
l2\p2\y#=Rnd#(0,1024)

a1.arc=New arc
If a1\c=Null Then a1\c.pt=New pt
a1\c\x#=640;Rnd#(0,1280)
a1\c\y#=512;Rnd#(0,1024)
a1\r#=Rnd#(50,250)
a1\a1#=Rnd#(0,360)
a1\a2#=Rnd#(0,360)

a2.arc=New arc
If a2\c=Null Then a2\c.pt=New pt
a2\c\x#=Rnd#(0,1280)
a2\c\y#=Rnd#(0,1024)
a2\r#=Rnd#(50,250)
a2\a1#=Rnd#(0,360)
a2\a2#=Rnd#(0,360)

Local tmp1.pt
Local tmp2.ln
Local tmp3.ln
Repeat
	l1\p1\x#=MouseX()
	l1\p1\y#=MouseY()
	If tmp1 <> Null Delete tmp1
	If tmp2 <> Null Delete tmp2\p1:Delete tmp2\p2:Delete tmp2
	If tmp3 <> Null Delete tmp3\p1:Delete tmp3\p2:Delete tmp3
	Cls
	tmp1.pt=IntersectionLineLine(l1,l2)
	tmp2.ln=IntersectionLineArc(l1,a1)
	DrawArc a1
	DrawArc a2
	DrawLn l1
	DrawLn l2
	DrawPt p1
	
	DrawPt tmp1
	If tmp2<>Null Then DrawPt tmp2\p1
	If tmp2<>Null Then DrawPt tmp2\p2
	If tmp3<>Null Then DrawPt tmp3\p1
	If tmp3<>Null Then DrawPt tmp3\p2
	Flip
Until KeyHit(1)
End

Function DrawPt(p.pt)
	If p <> Null
		Color 255,0,0
		Line p\x-5,p\y,p\x+5,p\y
		Line p\x,p\y-5,p\x,p\y+5
	EndIf
End Function

Function DrawLn(l.ln)
	If l <> Null
		If l\p1 <> Null And l\p2 <> Null
			Color 0,255,0
			Line l\p1\x,l\p1\y,l\p2\x,l\p2\y
		EndIf
	EndIf
End Function

Function DrawArc(a.arc)
	If a <> Null
		If a\c <> Null
			If a\a1>=a\a2 Then a\a2=a\a2+360
			LockBuffer GraphicsBuffer()
			asteps#=Floor#(((a\r*6.2831853072)*(a\a2-a\a1))/360)+1
			For a1=0 To asteps
				angle#=a\a1#+((a1/asteps#)*(a\a2-a\a1))
				WritePixel a\c\x#+a\r#*Cos#(angle#),a\c\y#-a\r#*Sin#(angle#),255
			Next
			UnlockBuffer GraphicsBuffer()
		EndIf
	EndIf
End Function

Function IntersectionLineLine.pt(l1.ln,l2.ln)
	If l1 <> Null And l2 <> Null
		If l1\p1 <> Null And l1\p2 <> Null And l2\p1 <> Null And l2\p2 <> Null
			Local x1#=l1\p1\x,y1#=l1\p1\y,x2#=l1\p2\x,y2#=l1\p2\y
			Local x3#=l2\p1\x,y3#=l2\p1\y,x4#=l2\p2\x,y4#=l2\p2\y
			Local x#,y#,ab#,am#,bb#,bm#
		
			If x1<>x2
				am=(y2-y1)/(x2-x1)
				ab=y1-am*x1
		
				If x3<>x4
					bm=(y4-y3)/(x4-x3)
					bb=y3-bm*x3
					If am<>bm
						x=(bb-ab)/(am-bm)
						y=am*x+ab
					Else
						Return Null
					EndIf
				Else
					x=x3
					y=am*x+ab
					If y3=y4 And (x=x3 Or y=y3) Then Return Null
				EndIf
			Else
				x=x1
				If x3<>x4
					bm=(y4-y3)/(x4-x3)
					bb=y3-bm*x3
					y=bm*x+bb
					If y1=y2 And (x=x1 Or y=y1) Then Return Null
				Else
					Return Null
				EndIf
			EndIf
		
			If Abs(x1-x2)<Abs(x1-x)+Abs(x2-x) Then Return Null
			If Abs(y1-y2)<Abs(y1-y)+Abs(y2-y) Then Return Null
			If Abs(x3-x4)<Abs(x3-x)+Abs(x4-x) Then Return Null
			If Abs(y3-y4)<Abs(y3-y)+Abs(y4-y) Then Return Null
			
			r.pt=New pt
			r\x=x
			r\y=y
			Return r
		Else
			Return Null
		EndIf
	Else
		Return Null
	EndIf
End Function

Function IntersectionLineArc.ln(l1.ln,a1.arc)
	If l1 <> Null And a1 <> Null
		If l1\p1 <> Null And l1\p2 <> Null And a1\c <> Null
			Local rtn.ln=Null
			Local x#
			Local y#
			Local x1#=l1\p1\x
			Local y1#=l1\p1\y
			Local x2#=a1\c\x
			Local y2#=a1\c\y
			Local f#=l1\p2\x-l1\p1\x
			Local g#=l1\p2\y-l1\p1\y
			Local r#=a1\r
			Local t#
			Local root#
			root = r*r*(f*f+g*g)-(f*(y2-y1)-g*(x2-x1))*(f*(y2-y1)-g*(x2-x1))
			
			If root<0.0
				Return Null
			ElseIf root = 0.0
				rtn.ln=New ln
				rtn\p1=New pt
				rtn\p2=Null
				
				t = (f*(x2-x1)+g*(y2-y1))/(f*f+g*g)
				If t>=0 And t<=1 Then
					rtn\p1\x = x1+f*t
					rtn\p1\y = y1+g*t
					ang#=ATan2#(x2-rtn\p1\x,y2-rtn\p1\y):If ang<0 Then ang=360+ang
					ang=(ang+90) Mod 360
					If a1\a1>ang# Or a1\a2<ang# Then Delete rtn\p1
				Else
					Delete rtn\p1
				EndIf
				If rtn\p1=Null And rtn\p2=Null Then Delete rtn
				Return rtn
			ElseIf 0.0 < root
				root#=Sqr#(root)
				rtn.ln=New ln
				rtn\p1=New pt
				rtn\p2=New pt
		
				t = ((f*(x2-x1)+g*(y2-y1))-root)/(f*f+g*g)
				If t>=0 And t<=1 Then
					rtn\p1\x = x1+f*t
					rtn\p1\y = y1+g*t
					ang#=ATan2#(x2-rtn\p1\x,y2-rtn\p1\y):If ang<0 Then ang=360+ang
					ang=(ang+90) Mod 360
					If a1\a1>ang# Or a1\a2<ang# Then Delete rtn\p1
				Else
					Delete rtn\p1
				EndIf
		
				t = ((f*(x2-x1)+g*(y2-y1))+root)/(f*f+g*g)
				If t>=0 And t<=1 Then
					rtn\p2\x = x1+f*t
					rtn\p2\y = y1+g*t
					ang#=ATan2#(x2-rtn\p2\x,y2-rtn\p2\y):If ang<0 Then ang=360+ang
					ang=(ang+90) Mod 360
					If a1\a1>ang# Or a1\a2<ang# Then Delete rtn\p2
				Else
					Delete rtn\p2
				EndIf
				If rtn\p1=Null And rtn\p2=Null Then Delete rtn
				Return rtn
			EndIf
		Else
			Return Null
		EndIf
	Else
		Return Null
	EndIf
End Function
