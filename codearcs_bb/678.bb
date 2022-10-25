; ID: 678
; Author: WendellM
; Date: 2003-05-09 12:49:05
; Title: Simple Word Wrap
; Description: A simple function to wrap words with the Text command

;Word Wrap function
;Wendell Martin 5/03

Graphics 800,600

a$="jjgyjgjygjygyjgyjgyjygjyg IIHIIHIHIHIHIHIHHIHH This is some text that is to be wrapped.  It's boring to have to write such a function, but one is needed.  I've written one for just about every Basic I've worked with."

;SetFont LoadFont("arial",20);uncomment to test some other font
x=100:y=100
w=225:h=120
Rect x,y,w,h,False;a visual bounding box to make sure wrap is working
WordWrap(a$,x,y,w,h)

WaitKey
End

Function WordWrap(A$,X,Y,W,H,Leading=0)
;Display A$ starting at X,Y - no wider than W and no taller than H (all in pixels).
;Leading is optional extra vertical spacing in pixels
;To Do (if needed): force break if single word is too big to fit on line (currently function will hang if this happens)
	LinesShown=0
	Height=StringHeight(a$)+leading
	While Len(a)>0
		space=Instr(a$," ")
		If space=0 Then space=Len(a$)
		temp$=Left$(a$,space)
		trimmed$=Trim$(temp);we might ignore a final space
		extra=0;we haven't ignored it yet
		;ignore final space if doing so would make a word fit at end of line:
		If (StringWidth (b$+temp$)>W) And (StringWidth (b$+trimmed$)<=W) Then temp=trimmed:extra=1
		If StringWidth (b$+temp$)>W Then;too big, so print what will fit
			Text X,LinesShown * Height + Y,b$
			LinesShown=LinesShown+1
			b$=""
		Else;append it to B$ (which will eventually be printed) and remove it from A$
			b$=b$+temp$
			a$=Right$(a$,Len(a$)-(Len(temp$)+extra))
		EndIf
		If ((LinesShown+1)*Height)>H Then Exit;the next line would be too tall, so leave
	Wend
	If (b$<>"")And((LinesShown+1)<=H) Then Text X,LinesShown*Height+Y,b$;print any remaining text if it'll fit vertically
End Function
