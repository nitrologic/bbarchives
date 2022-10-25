; ID: 1894
; Author: Nebula
; Date: 2007-01-02 02:41:49
; Title: Painter Inspiration
; Description: Surface generator

;
; Painter Inspiration. Crom Design 2007
;


Graphics 800,600,16,2
SetBuffer BackBuffer()

Dim genmap(100,100)

SeedRnd MilliSecs()

ms = MilliSecs()
subdivide(0,0,32,32)
myms = MilliSecs()-ms

drawmap
Flip
ClsColor 0,0,0
;
While KeyDown(1) = False
	If draw = False Then
		Cls
		drawmap()
		draw=True
		Color 0,0,0
		Rect 0,580,800,20,True
		Color 255,255,255
		Text 0,580,"Alt+printscreen - Painter Inspiration / paint ontop (crom design) - [Cursor right]"
		Flip
	End If
	;
	If KeyDown(205) = True
		ms = MilliSecs()
		For x=0 To 100:For y=0 To 100:genmap(x,y) = 0 Next:Next
		subdivide(0,0,32,32)
		myms = MilliSecs()-ms
		draw=False
	End If
	;
	Delay 100
Wend
End
;
Function drawmap()
	;
	For x=0 To 32
	For y=0 To 32
		;DebugLog genmap(x,y)+50
		Color genmap(x,y)+50,genmap(x,y)+50,genmap(x,y)+50
		Rect x*16,y*16,16,16,True
	Next:Next
	;
End Function
;
Function SubDivide(x1,y1,x2,y2); 
  If (x2-x1<2) And (y2-y1<2) Then Return; 
  dist=(x2-x1+y2-y1);
  hdist=dist / 2;  
  midx=(x1+x2) / 2;
  midy=(y1+y2) / 2;  
  c1=Genmap(x1,y1);
  c2=Genmap(x2,y1); 
  c3=Genmap(x2,y2); 
  c4=Genmap(x1,y2); 
  If Genmap(midx,y1)=0 Then Genmap(midx,y1)=((c1+c2+Rand(dist)-hdist) / 2); 
  If Genmap(midx,y2)=0 Then Genmap(midx,y2)=((c4+c3+Rand(dist)-hdist) / 2); 
  If Genmap(x1,midy)=0 Then Genmap(x1,midy)=((c1+c4+Rand(dist)-hdist) / 2); 
  If Genmap(x2,midy)=0 Then Genmap(x2,midy)=((c2+c3+Rand(dist)-hdist) / 2); 
  genmap(midx,midy) = ((c1+c2+c3+c4+Rand(dist)-hdist) / 4); 
  SubDivide(x1,y1,midx,midy); 
  SubDivide(midx,y1,x2,midy); 
  SubDivide(x1,midy,midx,y2); 
  SubDivide(midx,midy,x2,y2); 
End Function
