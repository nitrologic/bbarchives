; ID: 52
; Author: Reaper
; Date: 2001-09-24 19:59:27
; Title: Question$()
; Description: A more versatile input routine...


;           *****************************************
;           *  Question Function  * By Nick Stacey  *
;           *****************************************
;
;        A more versatile version of the input command
;
;
;Format  a$=Question(r,g,b,x,y,question,minlength,maxlength,r2,g2,b2,x2,y2)
;        where rgb,x&y are colour and coords of question
;		 and r2,b2,g2,x2 & y2 are color and coords of typed response
;		 minlength and maxlength are then minimum/maximum length of returned string
;		 optional format of
;		 a$=Question(r,g,b,x,y,question$,0,0,0,0,0,0,0)
;		 which means the function will behave more like a normal input and typed
;		 response will appear straight after question.
;
;		 Feel free to use and/or modify this code as much as required
;

Function Question$(r,g,b,x,y,question$,minlength,maxlength,r2,g2,b2,x2,y2)
Frontbuff = FrontBuffer() : setbb =0
If GraphicsBuffer() <>FrontBuff Then SetBuffer FrontBuffer():setbb=1
oldr=ColorRed() :oldg=ColorGreen():oldb=ColorBlue()
GetColor -1,-1
br= ColorRed(): bg=ColorGreen(): bb=ColorBlue()
Color br,bg,bb : l=Len(question$)
Rect x,y,(FontWidth()*l),FontHeight(),1
Color r,g,b
Text x,y,question$
value = 0 : a$=""
If r2=0 And b2=0 And g2=0 And x2=0 And y2=0 Then 
	r2=r:g2=g:b2=b:y2=y: x2=x+(FontWidth()*Len(question$)) 
EndIf
Color r2,g2,b2
l=Len(a$)
While (Not KeyHit(28)) Or l <minlength
	While Not value
		value = GetKey()
	Wend
	l = Len(a$)
	If KeyHit(14) Then
		If l >0 Then 
			a$=Left$(a$,l-1)
			Color br,bg,bb
			Rect x2,y2,(FontWidth()*l),FontHeight(),1
			Color r2,g2,b2
			Text x2,y2,a$
		EndIf
		Delay 50
		FlushKeys
		Delay 50
	EndIf
	If value >=32 And value <= 126 And (l < maxlength Or maxlength =0) Then
		a$=a$+Chr$(value):Delay 10
		If typ=1 Then 
			Text x+(FontWidth()*(Len(question$))),y,a$
		Else
			Text x2,y2,a$
		EndIf
	EndIf
	value=0
Wend
Delay 10
FlushKeys
Color oldr,oldg,oldb
If setbb =1 Then SetBuffer BackBuffer()
Return a$ 
End Function


