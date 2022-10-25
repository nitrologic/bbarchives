; ID: 1339
; Author: Berserker [swe]
; Date: 2005-03-28 17:48:56
; Title: Create 2D Terrain EDIT
; Description: Creates a 2D terrain with color wich is stored in a array.

[code]
;This is A edited version of skn3[ac]'s code at http://www.blitzbasic.com/codearcs/codearcs.php?code=356
;Author: Alexander Sällström aka Berserker[swe]
;Info: Creates a 2D landscape with green color. Landscape info is stored in a Array 
;      wich can be useful if you wanna blow bits of the terrain away or something.
;
;Feel free to use and alter as much as you like but i would appreciate if you write my name
;in some Credits or code or whatever =)
;And please email me if you need help or think you can improve this futher!
;My Email is: berserker@sverige.nu

Const ScreenWidth  = 1024
Const ScreenHeight = 0768
Const ScreenDepth  = 0032

Graphics ScreenWidth,ScreenHeight,ScreenDepth,1
SetBuffer BackBuffer()

Dim Landskap(ScreenWidth,ScreenHeight)
Dim LandColor(ScreenWidth,ScreenHeight,3)

Global GameTimer# = CreateTimer(60)

Function CreateMap(width,height)
Local y1,y2,y3,y4,x
  
  ; This little part of code was made by skn3[ac]
  ; Everything in this .bb is mine.
  ; See skn3[ac]'s original here: http://www.blitzbasic.com/codearcs/codearcs.php?code=356
  ;---------------------------------------------------------------------------------------
  y2 = height
  y1 = ((height-100)/2+Rand(50,300))
  For x = 0 To width
    SeedRnd MilliSecs()
    y1 = (y1+Cos(x)*Rand(-(Sin(x)*Rand(5)),(Sin(x)*Rand(5))))
    If y1 > y2 Then 
      y1 = y2
    EndIf
	If y1 < ((height-150)/3) Then 
	  y1 = ((height-150)/3)
	EndIf
  ;---------------------------------------------------------------------------------------

	;My Edit
	;Fills the Array Lanskap(x,y) with the value 1
	;1=something 0=nothing  
	For y3 = y1 To height
      Landskap(x,y3) = 1
    Next

  Next
  ;--------------------------------------------------------	

  ;My Edit
  ;Sets Color To The Landscape
  ;---------------------------------------------------
  For x = 0 To ScreenWidth
    For y4 = 0 To ScreenHeight      
      If Landskap(x,y4) <> 0 Then
        LandColor(x,y4,1) = 0
        LandColor(x,y4,2) = Int((245 + Rnd(-30,10)))
        LandColor(x,y4,3) = Int((26 + Rnd(-5,30)))
      EndIf 
    Next
  Next
  ;---------------------------------------------------

End Function

;Renders Lanscape
Function Render_Landscape()
Local X,Y
    
  For X = 0 To ScreenWidth
    For Y = 0 To ScreenHeight
      If Landskap(X,Y) <> 0 Then
        Color LandColor(X,Y,1),LandColor(X,Y,2),LandColor(X,Y,3):Plot X,Y
      EndIf      
    Next
  Next    
  
End Function

Function Game()
CreateMap(ScreenWidth,ScreenHeight)
  Repeat  
    Cls    
    Render_Landscape()
    Flip
    WaitTimer(GameTimer#)  
  Until KeyHit(1)
End Function

Game 
[/code]
