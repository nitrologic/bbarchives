; ID: 2216
; Author: Nebula
; Date: 2008-02-17 12:49:03
; Title: Radar example (dim)
; Description: Radar

AppTitle "Sound. Radar. (dutch english american)"

Graphics 640,480,16,2
SetBuffer BackBuffer()

Global size = 100

Dim coldim(1,size * 2,size * 2,1)
Dim radarbalk(2) 

radarbalk(0) = CreateImage(size * 2,size * 2)
radarbalk(1) = CreateImage(size * 2,size * 2)

makecolmap 0,1500 ; 
makecolmap 1,200 ; 

While KeyDown(1) = False
	;
	Cls
	;
	tekenradar 0	,100		,0	, an
	Oval size+100-10,size-10,20,20
	Oval 100,0,size*2,size*2,False
	Text 100,200,"Onbekende geluiden."
	Text 100,220,"Onherkenbare geluiden."
	Text 100,240,"Unfamiliar sounds."
	Text 100,260,"Unrecognisable sounds."
	


	tekenradar 1	,200+100	,0	, an
	Oval size+300-10,size-10,20,20	
	Oval 200+100,0,size*2,size*2,False
	Text 200+100,200,"Bekende geluiden."
	Text 200+100,220,"Herkenbare geluiden."
	Text 200+100,240,"Familiar sounds.."
	Text 200+100,260,"Recognisable geluiden."

	;
	an = an + 1 : If an > 360 Then an = 0
	;
	Oval 50,320,20,20,True
	Text 80,320,"Het herkenbare of onherkenbare geluids uitzend punt."
	Text 80,340,"The recognisable or unrecognisable sound send point."
	Rect 50,360,2,2,True
	Text 80,360,"Beweegpunt, attentiepunt."
	Text 80,380,"Movementpoint. attentionpoint."
	Text 80,420,"Please check the spelling and translations and definitions."
	Flip
Wend
End

Function tekenradar(radar,xz,yz,an)
	DrawBlock radarbalk(radar) ,xz	,yz
	x = Cos(an) * size
	y = Sin(an) * size
	Color 50,255,50
	Line size + xz	,size + yz	,x + size + xz,y + size + yz
	raakt1 = straalpuntcol(an,size,radar,radar)
End Function

Function makecolmap(m,a)
	;
	For x = 0 To size * 2
	For y = 0 To size * 2
		;
		If Rand(a) = 1 Then coldim(m,x,y,0) = 1 
		;
	Next:Next
	;
End Function
;
Function straalpuntcol(an,size,im,m)	
	;
	SetBuffer ImageBuffer(radarbalk(im))
	Color 10,250,10
	;
	For i=0 To size
		aa = Cos(an) * i + size
		bb = Sin(an) * i + size
		If coldim (m,aa,bb,0) = 1 Then
			z#= ColorGreen()-i*1.9
			If z < 0 Then z=0
			Color ColorRed(),z,ColorBlue()
			Rect aa,bb,2,2
			a=a+1
		End If
	Next
	;
	an=an-45
	Color 0,0,0
	For i=0 To size
		aa = Cos(an) * i + size
		bb = Sin(an) * i + size
		If coldim (m,aa,bb,0) = 1 Then
			Rect aa,bb,2,2
			a=a+1
		End If
	Next
	;
	;
	SetBuffer BackBuffer()
	Return a
End Function
