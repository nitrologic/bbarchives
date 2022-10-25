; ID: 895
; Author: Markus Rauch
; Date: 2004-01-25 11:45:02
; Title: AsynInput
; Description: good replace for Input$

;AsynInput from M.Rauch 

;MR 25.01.2004

Graphics 640,480,16,2 
SetBuffer BackBuffer()
AppTitle " AsynInput from M.Rauch" 

Type AsynInputType
 Field X
 Field Y
 Field Width
 Field Height
 Field MaxLenght
 Field Value$
 Field Caption$
 Field CaptionWidth

 Field ForeColor
 Field BackColor
 Field BorderColor
 Field CaptionColor
 Field Enabled
 Field Visible
 Field HasFocus
 Field AsynString$
 Field Transparent
 Field Border
 Field CursorPos
 Field KeyDelay
End Type
Global AsynInput.AsynInputType

;---------------------------------

Global Spieler1$
Global Spieler2$
NamenEingeben

;---------------------------------

MainLoop
End

;-------------------------------------------------------------------------------

Function NamenEingeben()

 Local TextBox.AsynInputType
 Local TextBox1.AsynInputType
 Local TextBox2.AsynInputType

 Local myFont=LoadFont("Tahoma",18,1)
 SetFont myFont

 TextBox =AsynInputNew(10,10+24*0,240,22,0,""       ,"Fertig mit ESC",85)
 TextBox1=AsynInputNew(10,10+24*1,240,22,13,"Herbert","Spieler 1:"    ,85)
 TextBox2=AsynInputNew(10,10+24*2,240,22,13,"Name 2" ,"Spieler 2:"    ,85)

 AsynInputSetTransparent TextBox,1
 AsynInputSetBorder      TextBox,0

 AsynInputSetFocus TextBox1

 FlushKeys
 While Not KeyHit(1) ;ESC
  Cls 
  mx=MouseX()
  my=MouseY()
  md1=MouseDown(1)
  gk=GetKey()

  If Left(TextBox2\Value,4)="Name" Then TextBox2\Value="Name " + Int(Rnd(2,10)) ;spielerei :-)

  AsynInputShow mx,my,md1,gk
  Flip 
 Wend 

 AsynInputNoFocus

 Spieler1$=TextBox1\Value
 Spieler2$=TextBox2\Value

 AsynInputFreeAll
 FlushKeys

 FreeFont myFont

End Function

;-------------------------------------------------------------------------------

Function MainLoop()
 
 Color 255,255,255
 While Not KeyHit(1)  ;ESC
  Cls 
  Text 10,10,Spieler1$
  Text 10,30,Spieler2$
  Flip 
 Wend 

End Function

;-------------------------------------------------------------------------------

Function AsynInputShow(mx,my,md,gk)

 Local A.AsynInputType

 A=AsynInput

 For A.AsynInputType = Each AsynInputType
  If A\Visible=1 Then

   ;----------------------------------------------------- Rand

   If A\Border=1 Then
    If A\Enabled=1 Then Color 0,0,A\BorderColor Else Color 128,128,128
    If A\HasFocus=1 Then Color 0,255,0
    Rect A\X,A\Y,A\Width,A\Height,False
   EndIf ;Border

   ;----------------------------------------------------- Hintergrund

   If A\Transparent=0 Then
    If A\Enabled=1 Then Color 0,0,A\BackColor Else Color 128,128,128
    If A\Border=0 Then Rect A\X,A\Y,A\Width,A\Height,True Else Rect A\X+1,A\Y+1,A\Width-2,A\Height-2,True
   EndIf ;Transparent

   ;----------------------------------------------------- Cursor

   If A\HasFocus=1 Then
    Local cx=A\X+A\CaptionWidth+3+StringWidth(Mid(A\AsynString,1,A\CursorPos))
    Color 255,255,255
    Rect cx,A\Y+1,2,A\Height-2
    AsynInputKeyPress A,gk
   EndIf ;Focus

   ;----------------------------------------------------- Caption

   If A\Enabled=1 Then Color 0,0,A\CaptionColor Else Color 128,128,128
   Text A\X+3,A\Y+A\Height/2-1,A\Caption$,False,True

   ;----------------------------------------------------- Text

   If A\Enabled=1 Then Color 0,0,A\ForeColor Else Color 128,128,128
   If A\HasFocus=0 Then
    Text A\X+A\CaptionWidth+3,A\Y+A\Height/2-1,A\Value$,False,True
   Else
    Text A\X+A\CaptionWidth+3,A\Y+A\Height/2-1,A\AsynString$,False,True
   EndIf

   ;----------------------------------------------------- Focus Test

   If md=1 Then
   If (mx=>A\X And mx<=A\X+A\Width -1) And (my=>A\Y And my<=A\Y+A\Height-1) Then
    If A\HasFocus=0 And A\Enabled=1 Then
     AsynInputNoFocus
     AsynInputSetFocus A
    EndIf ;Focus
   Else
    A\HasFocus=0
    A\Value=A\AsynString ;Übernehmen
   EndIf ;in Rect
   EndIf ;Mouse Down

   ;-----------------------------------------------------

  EndIf ;Visible
 Next

End Function 

;-------------------------------------------------------------------------------

Function AsynInputKeyPress(A.AsynInputType,gk)

 If KeyDown(28) Then ;Return
  A\Value=A\AsynString
  A\HasFocus=0
  Return
 EndIf

 If gk => 32 And gk <= 255 And Len(A\AsynString) < A\MaxLenght Then
  A\AsynString =Mid(A\AsynString,1,A\CursorPos)+Chr(gk)+Mid(A\AsynString,A\CursorPos+1)   
  A\CursorPos=A\CursorPos+1
  Return
 EndIf

 If MilliSecs()-A\KeyDelay>150 Then

   If KeyDown(211)=True Then ;Entfernen
    If Len(A\AsynString) > 0 Then
     A\AsynString =Mid(A\AsynString,1,A\CursorPos)+Mid(A\AsynString,A\CursorPos+2)
     A\KeyDelay=MilliSecs()   
    EndIf 
   End If 

   If KeyDown(14)=True Then ;BackSpace
    If Len(A\AsynString) > 0 And A\CursorPos>0 Then
     A\CursorPos=A\CursorPos-1
     A\AsynString =Mid(A\AsynString,1,A\CursorPos)+Mid(A\AsynString,A\CursorPos+2)
     A\KeyDelay=MilliSecs()   
    EndIf 
   End If 

   If KeyDown(203)=True Then ;Links
    If A\CursorPos>0 Then
     A\CursorPos=A\CursorPos-1
     A\KeyDelay=MilliSecs()   
    EndIf
   EndIf

   If KeyDown(205)=True Then ;Rechts
    If A\CursorPos<Len(A\AsynString) Then
     A\CursorPos=A\CursorPos+1
     A\KeyDelay=MilliSecs()   
    EndIf
   EndIf

 EndIf ;Tasten erlaubt

End Function 

;-------------------------------------------------------------------------------

Function AsynInputNew.AsynInputType(x,y,width,height,MaxLenght,Value$,Caption$,CaptionWidth)

 AsynInput.AsynInputType = New AsynInputType
 AsynInput\x=x
 AsynInput\y=y
 AsynInput\width=width
 AsynInput\height=height
 AsynInput\MaxLenght=MaxLenght
 AsynInput\Value$=Value$
 AsynInput\Caption=Caption$
 AsynInput\CaptionWidth=CaptionWidth

 AsynInput\ForeColor   =RGB(255,255,255)
 AsynInput\BackColor   =RGB( 64, 64, 64)
 AsynInput\BorderColor =RGB(128,128,128)
 AsynInput\CaptionColor=RGB(255,255,255)

 AsynInput\Enabled=1
 AsynInput\Visible=1
 AsynInput\HasFocus=0
 AsynInput\AsynString$=""
 AsynInput\Transparent=0
 AsynInput\Border=1
 AsynInput\KeyDelay=0

 Return AsynInput

End Function 

;-------------------------------------------------------------------------------

Function AsynInputFreeAll()
 For AsynInput.AsynInputType = Each AsynInputType
  AsynInputFree AsynInput
 Next
End Function 

;-------------------------------------------------------------------------------

Function AsynInputFree(A.AsynInputType)
 Delete a
End Function 

;-------------------------------------------------------------------------------

Function AsynInputSetBorder(A.AsynInputType,Value)
 If Value=0 Then
  A\Border=0
 Else
  A\Border=1
 EndIf
End Function 

;-------------------------------------------------------------------------------

Function AsynInputSetTransparent(A.AsynInputType,Value)
 If Value=0 Then
  A\Transparent=0
 Else
  A\Transparent=1
 EndIf
End Function 

;-------------------------------------------------------------------------------

Function AsynInputSetFocus(A.AsynInputType)

 AsynInputNoFocus

 If A\HasFocus=0 And A\Visible=1 And A\Enabled=1 And A\MaxLenght>0 Then
  A\HasFocus=1
  A\AsynString=A\Value
  A\CursorPos=Len(A\AsynString)
  A\KeyDelay=MilliSecs()
  Return 1
 Else
  Return 0
 EndIf

End Function 

;-------------------------------------------------------------------------------

Function AsynInputNoFocus()
 For AsynInput.AsynInputType = Each AsynInputType
  If AsynInput\HasFocus=1 Then
   AsynInput\HasFocus=0
   AsynInput\Value=AsynInput\AsynString ;Übernehmen
  EndIf
 Next
End Function 

;-------------------------------------------------------------------------------

Function RGB(r,g,b)

 Return ((r * $10000) Or (g * $100) Or b)

End Function

;-------------------------------------------------------------------------------
