; ID: 698
; Author: maverick69
; Date: 2003-05-21 13:10:09
; Title: BMPFont
; Description: Some functions  for creating and using Bitmap Fonts

;--------------------------------------------------------------------------;
; BMPFont v1.0                                                             ;
;--------------------------------------------------------------------------;
; Latest Changes: 20.05.2003                                               ;
;--------------------------------------------------------------------------;
;                                                                          ;
; Font = BMPFont_Load(File$,Width,Height)                                  ;
;                                                                          ;
; File$ <-------------- Name of Bitmap Font                                ;
; Width <-------------- Width of a single Char in pixel                    ;
; Height <------------- Height of a single Char in pixel                   ;
;                                                                          ;
;--------------------------------------------------------------------------;
;                                                                          ;
; BMPFont_Print(X,Y,Txt$,Center_X,Center_Y,Font)                           ;
;                                                                          ;
; X <------------------ X Position                                         ;
; Y <------------------ Y Position                                         ;
; Txt$ <--------------- String to print                                    ;
; Center_X <----------- true/false - to center on X-Position               ;
; Center_Y <----------- true/false - to center on Y-Position               ;
; Font <--------------- font loaded with BMPFont_Load                      ;
;                                                                          ;
;--------------------------------------------------------------------------;
;                                                                          ;
; succ = BMPFont_Create(FontName$,FontSize,Width,Height,File$)             ;
;                                                                          ;
; Returns true/false if file was saved                                     ;
;                                                                          ;
; FontName$ <---------- String, TrueType Font Filename, ex.: "Arial"       ;
; FontSize <----------- Fontheight in pixel                                ;
; Width <-------------- Size of width a single char should use             ;
; Height <------------- Size of height a single char should use            ;
; File$ <-------------- String, Filenmae where BMP should be saved to      ;
;                                                                          ;
;--------------------------------------------------------------------------;
; Simple Example code                                                      ;
;--------------------------------------------------------------------------;
; BMPFONT_Create("Arial",30,30,30,"test.bmp")                              ;
; font=BMPFONT_Load("test.bmp",30,30)                                      ;
; ClsColor 0,0,120                                                         ;
; Repeat                                                                   ;
; Cls                                                                      ;
; 	BMPFont_Print(50,50,"TEST",0,0,font)                                   ;
;	Flip                                                                   ;
; Until KeyHit(1)                                                          ;
; End                                                                      ;
;--------------------------------------------------------------------------;


Function BMPFont_Load(File$,Width,Height)
	font = 0
	font = LoadAnimImage(file$,width,height,0,127-31)
	If font=0 Then font=False
	Return font
End Function

Function BMPFont_Print(X,Y,Txt$,Center_X,Center_Y,Font) 
  If Font 
    lang=Len(txt$) 
    w=ImageWidth(Font) 
    h=ImageHeight(Font) 
    width=w*lang 
    If center_x Then x=x-(width/2) 
    If center_y Then y=y-(h/2) 
    For i=1 To lang 
      DrawImage Font,x,y,(Asc(Mid(txt$,i,1)))-32 
      x=x+w 
    Next 
  EndIf    
End Function 

Function BMPFont_Create(FontName$,FontSize,Width,Height,File$)
	font = LoadFont(FontName$,FontSize)
	SetFont font

	myFont = CreateImage (width*16,height*6)
	SetBuffer ImageBuffer(myFont)
	frame=0
	
	x = width/2
	y = height/2

	For i=32 To 127
		Text x,y,Chr(i),True,True
		x = x + width
		If x>width*16 Then x=width/2 : y=y+height
	Next
	
	Return SaveImage (myFont,File$)
End Function
