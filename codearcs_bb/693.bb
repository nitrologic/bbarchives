; ID: 693
; Author: kRUZe
; Date: 2003-05-15 14:04:03
; Title: PlasmaText
; Description: I think the title sez it all  =)

;------------------------------------------------------------------------
;	Set up screen resolution and define variables
;------------------------------------------------------------------------
Graphics 320,240,0,1

font=LoadFont("verdana",18,1,0,0)
SetFont font

Global plaswidth=127
Global plasheight=63

Global buffer = CreateImage(plaswidth+3,plasheight+2)	; extra buffer space to account for x,y pixel averaging -1 +1

Global fntheight=12
Global txtx=(plaswidth+1)/2
Global txty=plasheight-fntheight

Dim txt$(128)	; Define max text array
Global t=1

timer=0
d_lay=18

Dim r(4)
Dim g(4)
Dim b(4)

;------------------------------------------------------------------------
;	Main Loop
;------------------------------------------------------------------------

SetBuffer BackBuffer()
SeedRnd MilliSecs()

definetxt()

While Not KeyDown(1)
	Cls
	SetBuffer ImageBuffer(buffer)
	timer=timer+1
	If timer=d_lay
		BlitText()
		timer=0
	EndIf
	UpdatePlasma()
	SetBuffer BackBuffer()
	DrawImage buffer,96,96
	Flip
Wend 
End

;------------------------------------------------------------------------
;	Update Plasma + Draw
;------------------------------------------------------------------------
Function UpdatePlasma()
		LockBuffer ImageBuffer(buffer)
		
		For y=0 To plasheight
			For x=0 To plaswidth

				col=ReadPixelFast(x,y, ImageBuffer(buffer)) And $ffffff	
				r(1) = (col Shr 16) And $FF 
				g(1) = (col Shr 8) And $FF 
				b(1) = col And $FF
			
				col=ReadPixelFast(x-1,y+1, ImageBuffer(buffer)) And $ffffff	
				r(2) = (col Shr 16) And $FF 
				g(2) = (col Shr 8) And $FF 
				b(2) = col And $FF
			
				col=ReadPixelFast(x+1,y+1, ImageBuffer(buffer)) And $ffffff	
				r(3) = (col Shr 16) And $FF 
				g(3) = (col Shr 8) And $FF 
				b(3) = col And $FF
			
				tmp_red = ((r(1)+r(2)+r(3))/3)
				tmp_grn = ((g(1)+g(2)+g(3))/3)
				tmp_blu = ((b(1)+b(2)+b(3))/3)
				
				If tmp_red<0 Then tmp_red=0
				If tmp_grn<0 Then tmp_grn=0
				If tmp_blu<0 Then tmp_blu=0
						
				argb = (tmp_blu Or (tmp_grn Shl 8) Or (tmp_red Shl 16) Or (255 Shl 24))
				
				WritePixelFast x,y,argb, ImageBuffer(buffer)
				
			Next
		Next
		UnlockBuffer ImageBuffer(buffer)

End Function

;---------------------------------------------------------------------------------------------------
Function BlitText()
	
	Color 0,255,0	
		Text txtx,txty,txt$(t),True,True
	
		t=t+1
	If t>8 Then t=1
	
End Function

;---------------------------------------------------------------------------------------------------
Function definetxt()
	txt$(1)=""
	txt$(2)="Plasma Text"
	txt$(3)="Coded"
	txt$(4)="By"
	txt$(5)="Zerosynapse"
	txt$(6)="<end>"
	txt$(7)=""
	txt$(8)=""
End Function
