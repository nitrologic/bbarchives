; ID: 2119
; Author: Nebula
; Date: 2007-10-15 14:15:07
; Title: World Time clocks
; Description: 5 clocks showing world times

; Error at PMAM

Graphics 640,480,16,2
SetBuffer BackBuffer()

;ruler...
Global absval# = 100
Global stepval# = ((360/absval))
Global cnt# = 0

Type klokwijzer
	Field x#,y#
	Field maxwaarde#
	Field minwaarde#
	Field stapwaarde#
	Field wijzerlengte#
	Field wijzerlokatie#
	Field waarde#

End Type

;initiate
Global wijzer.klokwijzer = New klokwijzer
nieuwewijzer(wijzer,20,20,60,1,10)

Aseconde.klokwijzer = New klokwijzer
nieuwewijzer	(Aseconde		,300	,200,60,1,40)
Aminuut.klokwijzer = New klokwijzer
nieuwewijzer	(Aminuut		,300	,200,60,1,60)
Auur.klokwijzer = New klokwijzer
nieuwewijzer	(Auur			,300	,200,12,1,30)
;

GBseconde.klokwijzer = New klokwijzer
GBminuut.klokwijzer 	= New klokwijzer
GBuur.klokwijzer 	= New klokwijzer
nieuwewijzer gbseconde			,200,200,60,1,40
nieuwewijzer gbminuut			,200,200,60,1,60
nieuwewijzer gbuur				,200,200,12,1,30
gbuur\waarde=11
gbminuut\waarde=0
gbseconde\waarde=0


NYseconde.klokwijzer = New klokwijzer
NYminuut.klokwijzer 	= New klokwijzer
NYuur.klokwijzer 	= New klokwijzer
nieuwewijzer nyseconde			,100,200,60,1,40
nieuwewijzer nyminuut			,100,200,60,1,60
nieuwewijzer nyuur				,100,200,12,1,30
nyuur\waarde=6
nyminuut\waarde=0
nyseconde\waarde=0

GLseconde.klokwijzer = New klokwijzer
GLminuut.klokwijzer 	= New klokwijzer
GLuur.klokwijzer 	= New klokwijzer
nieuwewijzer GLseconde			,400,200,60,1,40
nieuwewijzer GLminuut			,400,200,60,1,60
nieuwewijzer GLuur				,400,200,12,1,30
GLuur\waarde=1
glminuut\waarde=0
glminuut\waarde=0

MKseconde.klokwijzer = New klokwijzer
MKminuut.klokwijzer 	= New klokwijzer
MKuur.klokwijzer 	= New klokwijzer
nieuwewijzer MKseconde			,500,200,60,1,40
nieuwewijzer MKminuut			,500,200,60,1,60
nieuwewijzer MKuur				,500,200,12,1,30
MKuur\waarde=2
MKminuut\waarde=0
MKminuut\waarde=0



abc = CreateTimer(1)

Local bmap = CreateImage(640,480)
SetBuffer ImageBuffer(bmap)

Color 50,50,100
For i = 0 To 100
	klok auur,aminuut,aseconde
	klok gbuur,gbminuut,gbseconde
	klok nyuur,nyminuut,nyseconde
	klok gluur,glminuut,glseconde
	klok MKuur,MKminuut,MKseconde
Next
;
Color 255,255,255
auur\waarde 	= Int(Mid(CurrentTime(),1,2))-12
aminuut\waarde 	= Int(Mid(CurrentTime(),4,2))
aseconde\waarde = Int(Mid(CurrentTime(),7,2))

gbuur\waarde = auur\waarde-1
gbminuut\waarde = aminuut\waarde
gbseconde\waarde = aseconde\waarde

nyuur\waarde = auur\waarde-6
nyminuut\waarde = aminuut\waarde
nyseconde\waarde = aseconde\waarde

gluur\waarde = auur\waarde+1
glminuut\waarde = aminuut\waarde
glseconde\waarde = aseconde\waarde

MKuur\waarde = auur\waarde+2
MKminuut\waarde = aminuut\waarde
MKseconde\waarde = aseconde\waarde



;
SetBuffer BackBuffer()
;
While KeyDown(1) = False
	Cls
	;
	DrawBlock bmap,0,0
	Text 0	+25	,100  	,"New York"
	Text 100+25	,100	,"London"
	Text 200+25	,100	,"Amsterdam"
	Text 300+25	,100	,"Athene"
	Text 400+25 ,100	,"Moskou"
	;
	WaitTimer abc
	klok auur,aminuut,aseconde
	klok gbuur,gbminuut,gbseconde
	klok nyuur,nyminuut,nyseconde
	klok gluur,glminuut,glseconde
	klok MKuur,MKminuut,MKseconde

	;			
	;
	Text 0,20,aseconde\waarde
	Text 0,40,aminuut\waarde
	Text 0,60,(auur\waarde)
	;
	Flip
Wend
End

Function klok(uur.klokwijzer,minuut.klokwijzer,seconde.klokwijzer)
	;
	tekenwijzer seconde
	tekenwijzer minuut
	tekenwijzer uur
	klokdraai(seconde,1)
	;
	If seconde\waarde+1 > seconde\maxwaarde Then klokdraai minuut,1 : seconde\waarde=seconde\minwaarde-1
	zetwijzerlokatie seconde
	If minuut\waarde+1 >  minuut\maxwaarde Then klokdraai uur,1 : minuut\waarde=minuut\minwaarde-1
	zetwijzerlokatie minuut
	If uur\waarde+1 > uur\maxwaarde Then klokdraai uur,1 : uur\waarde=uur\minwaarde-1
	zetwijzerlokatie uur
	;
End Function

;new
Function nieuwewijzer(wijzer.klokwijzer,x#,y#,maxwaarde#,minwaarde#,wijzerlengte#)
	wijzer\maxwaarde = maxwaarde#
	wijzer\minwaarde = minwaarde#
	wijzer\stapwaarde = ((360/wijzer\maxwaarde)) ;* stap
	wijzer\wijzerlengte = wijzerlengte#
	wijzer\wijzerlokatie = 0
	wijzer\x = x
	wijzer\y = y
End Function

Function zetwijzerlokatie(wijzer.klokwijzer)
	wijzer\wijzerlokatie = wijzer\waarde
End Function

;draw
Function tekenwijzer(wijzer.klokwijzer)
	Oval wijzer\x-5,wijzer\y-5,10,10,True
	x2# = Cos(((wijzer\wijzerlokatie)*wijzer\stapwaarde)-90)*wijzer\wijzerlengte 
	y2# = Sin(((wijzer\wijzerlokatie)*wijzer\stapwaarde)-90)*wijzer\wijzerlengte 
	x1# = wijzer\x
	y1# = wijzer\y
	Line wijzer\x,wijzer\y,x1+x2,y1+y2
End Function

;turn
Function klokdraai(wijzer.klokwijzer,waarde#)
	wijzer\waarde# = wijzer\waarde + 1
	If wijzer\waarde# > wijzer\maxwaarde Then wijzer\waarde = wijzer\minwaarde
	If wijzer\waarde# < wijzer\minwaarde Then wijzer\waarde = wijzer\maxwaarde
End Function
