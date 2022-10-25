; ID: 1168
; Author: Kekskiller
; Date: 2004-09-26 14:20:08
; Title: DrawTranslucidImage
; Description: draws a picture with transpareny-parameters

;TranslucidImage - by Max "Kekskiller" Beutling
;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
;DrawTranslucidImage(Bank,MaskRed,MaskGreen,MaskBlue,X,Y,Transparency#,CenterX=0,CenterY=0)
; - draws an image form a bank
;
;LoadTranslucidImage(File$)
; - loads an image file in to a bank
;
;ConvertImageToTranslucid(Picture)
; - converts an image to a image bank

Graphics 640,480,16,1
SetBuffer BackBuffer()
ClsColor 255,255,255

Temporary=LoadTranslucidImage("testmonster.bmp") ;<- name of the image

Global TranslucidDown=0,Translucid#=0
Global bwx,BW,oldZeit

Repeat
Cls
mx=MouseX()
my=MouseY()
Select TranslucidDown
 Case 0
  Translucid#=Translucid#+0.01
  If Translucid#>=1 Then TranslucidDown=1:Translucid#=1
 Case 1
  Translucid#=Translucid#-0.01
  If Translucid#<=0 Then TranslucidDown=0:Translucid#=0
End Select
DrawTranslucidImage(Temporary,255,255,255,mx,my,Translucid#,1,1)
bwx=bwx+1:If MilliSecs()>oldZeit+999 Then BW=bwx:bwx=0:oldZeit=MilliSecs()
Color 0,0,0
Text 1,1,BW
Flip
Until KeyHit(1)

End

Function DrawTranslucidImage(bank,mred,mgre,mblu,ox,oy,t#,centerx=0,centery=0)
out=BackBuffer()
pw=PeekByte(bank,0)
ph=PeekByte(bank,1)
gw=GraphicsWidth()-1
gh=GraphicsHeight()-1
If centerx=1 Then ox=ox-pw/2
If centery=1 Then oy=oy-ph/2
z=2
LockBuffer out
For y=0 To ph
 For x=0 To pw
  If ox+x>=0 And oy+y>=0 And ox+x<=gw And oy+y<=gh Then

   ired=PeekByte(bank,z):z=z+1
   igre=PeekByte(bank,z):z=z+1

   iblu=PeekByte(bank,z):z=z+1

   If ired<>mred Or igre<>mgre Or iblu<>mblu Then
    opx=ReadPixelFast(ox+x,oy+y,out)

    ored=ExtractRed(opx)
    ogre=ExtractGreen(opx)
    oblu=ExtractBlue(opx)

    If ored<ired Then
     fred=ored+Degree#(ored,ired)*t#
    ElseIf ored>ired Then
     fred=ired+Degree#(ored,ired)*t#
    ElseIf ored=ired Then
     fred=ored
    EndIf

    If ogre<igre Then
     fgre=ogre+Degree#(ogre,igre)*t#
    ElseIf ogre>igre Then
     fgre=igre+Degree#(ogre,igre)*t#
    ElseIf ogre=igre Then
     fgre=ogre
    EndIf

    If oblu<iblu Then
     fblu=oblu+Degree#(oblu,iblu)*t#
    ElseIf oblu>iblu Then
     fblu=iblu+Degree#(oblu,iblu)*t#
    ElseIf oblu=iblu Then
     fblu=oblu
    EndIf

    fpx=fred*$10000+fgre*$100+fblu
    WritePixelFast ox+x,oy+y,fpx,out
   EndIf

  Else
   z=z+3
  EndIf
 Next
Next
UnlockBuffer out
End Function

Function LoadTranslucidImage(file$)
picture=LoadImage(file$)
pw=ImageWidth(picture)-1
ph=ImageHeight(picture)-1
bnk=CreateBank(2+((pw+1)*(ph+1))*3)
in=ImageBuffer(picture)
PokeByte bnk,0,pw
PokeByte bnk,1,ph
z=2
LockBuffer in
For y=0 To ph
 For x=0 To pw
  ipx=ReadPixelFast(x,y,in)
  ired=ExtractRed(ipx)
  igre=ExtractGreen(ipx)
  iblu=ExtractBlue(ipx)
  PokeByte bnk,z,ired:z=z+1
  PokeByte bnk,z,igre:z=z+1
  PokeByte bnk,z,iblu:z=z+1
 Next
Next
UnlockBuffer in
FreeImage picture
Return bnk
End Function

Function ConvertImageToTranslucid(picture)
pw=ImageWidth(picture)-1
ph=ImageHeight(picture)-1
bnk=CreateBank(2+((pw+1)*(ph+1))*3)
in=ImageBuffer(picture)
PokeByte bnk,0,pw
PokeByte bnk,1,ph
z=2
LockBuffer in
For y=0 To ph
 For x=0 To pw
  ipx=ReadPixelFast(x,y,in)
  ired=ExtractRed(ipx)
  igre=ExtractGreen(ipx)
  iblu=ExtractBlue(ipx)
  PokeByte bnk,z,ired:z=z+1
  PokeByte bnk,z,igre:z=z+1
  PokeByte bnk,z,iblu:z=z+1
 Next
Next
UnlockBuffer in
Return bnk
End Function

Function Degree#(v1#,v2#)
Local bigger,out#
If v2#>v1# bigger=2
If v1#>v2# bigger=1
Select bigger
 Case 0:out#=0
 Case 1:out#=v1#-v2#
 Case 2:out#=v2#-v1#
End Select
Return out#
End Function

Function ExtractRed(col)
o=(col And $FF0000)/$10000
Return o
End Function

Function ExtractGreen(col)
o=(col And $FF00)/$100
Return o
End Function

Function ExtractBlue(col)
o=col And $FF
Return o
End Function
