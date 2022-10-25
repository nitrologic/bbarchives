; ID: 1851
; Author: Markus Rauch
; Date: 2006-10-25 13:31:33
; Title: Background Image Creator
; Description: Tool for Createing Background Images for Handhelds

Strict

'BlitzMax 1.22

'M.Rauch 25.10.2006

'Programm womit man sich Hintergrundbilder für PSP,Pocket PC oder Handy machen kann

Const NeedX=480
Const NeedY=272

Local a$,b$
Local width:Int=1024,height:Int=768,depth:Int=0,herz:Int=72,gl=0
Global Bild:String="Test.jpg"

'Programm Command Line width 1280 height 1024 gl 1
For a$=EachIn AppArgs
 If b$="width" Then
  width=Int(a$)
 ElseIf b$="height" Then
  height=Int(a$)
 ElseIf b$="depth" Then
  depth=Int(a$)
 ElseIf b$="herz" Then
  herz=Int(a$)
 ElseIf b$="gl" Then
  gl=True
 EndIf
 a$=Lower(a$)
 If Instr(a$,".jpg")=>1 Or Instr(a$,".bmp")=>1 Or Instr(a$,".gif")=>1 Or Instr(a$,".png")=>1 Then
  bild$=a$
 Else
  b$=a$
 EndIf
Next

If gl Then SetGraphicsDriver GLMax2DDriver()  

If GraphicsModeExists(width,height,depth,herz)=True Then
 Graphics width,height,depth,herz
Else
 Graphics 640,480
EndIf

MainLoop()
End

Function MainLoop()

 Local resize:Int=False
 Local startx:Int
 Local starty:Int
 Local startxx:Int=-1
 Local startyy:Int
 Local endx:Int
 Local endy:Int
 Local endxx:Int
 Local endyy:Int
 Local scale:Float=0

 startx=0
 starty=0
 endx=startx+needx-1
 endy=starty+needy-1
 resize=True

 Local mu:Float=0 'für Interpolation
 Local mwheel:Int=0
 Local md1:Int,mu1:Int,md2:Int,mu2:Int,md3:Int,mu3:Int 'Maus Abfrage
 Local mx:Int,my:Int,mz:Int

 '-----------------------------------
 Local pix:TPixmap
 Local img:TImage=LoadImage(Bild)
 If img Then
  pix=LockImage(img,0,True,True)
  ConvertPixmap pix,PF_RGB888
  If pix.width>GraphicsWidth() Or pix.height>GraphicsHeight() Then
   pix=FitPixmap(pix,GraphicsWidth(),GraphicsHeight())
  EndIf
  UnlockImage img
 EndIf
 '-----------------------------------

 While Not KeyHit(KEY_ESCAPE)
  Cls
  If pix Then
   DrawPixmap pix,0,0
   SetColor 255,255,255
   'DrawText bild,12,36
  Else
   SetColor 255,255,255
   DrawText "image not found !? '" + bild + "'",0,0
  EndIf

  mx=MouseX()
  my=MouseY()
  mz=MouseZ()

  mu1=0;If md1=1 Then md1=2
  If MouseDown(1)=True  And md1=0           Then md1=1;mu1=0
  If MouseDown(1)=False And md1=2 And mu1=0 Then md1=0;mu1=1

  mu2=0;If md2=1 Then md2=2
  If MouseDown(2)=True  And md2=0           Then md2=1;mu2=0
  If MouseDown(2)=False And md2=2 And mu2=0 Then md2=0;mu2=1

  mu3=0;If md3=1 Then md3=2
  If MouseDown(3)=True  And md3=0           Then md3=1;mu3=0
  If MouseDown(3)=False And md3=2 And mu3=0 Then md3=0;mu3=1

  If md2=1 Then
   scale=scale+0.05;resize=True;If scale>1.0 Then scale=0;resize=True
  EndIf
  If md3=1 Then
   scale=0;resize=True
  EndIf

  If md1=2 Then
   resize=True
   startx=mx-(needx/2)
   starty=my-(needy/2)
   If startx<0 Then startx=0
   If starty<0 Then starty=0
   endx=startx+needx-1
   endy=starty+needy-1
  EndIf

  If resize=True Then
   resize=False
   startxx=startx-(needx*scale)
   startyy=starty-(needy*scale)
   endxx=endx+(needx*scale)
   endyy=endy+(needy*scale)
  EndIf

  If startxx=>0 And startyy=>0 Then
   SetBlend ALPHABLEND
   SetAlpha 0.25 
   SetColor 128,128,128
   DrawRect startxx,startyy,(endxx-startxx)+1,(endyy-startyy)+1
   SetColor 255,255,0
   mRect startxx,startyy,endxx,endyy
   SetBlend SOLIDBLEND+MASKBLEND
   SetAlpha 1
  Else
   SetColor 255,0,0
   mRect startxx,startyy,endxx,endyy
  EndIf
  
  SetColor 255,255,255
  DrawText "Left Mouse = Area , Right Mouse = Scale ",12,12
  DrawText "ESC = End , S = Save jpg",12,24

  If KeyHit(KEY_S) Then
   If startx>-1 Then
    Save bild,pix,startxx,startyy,endxx,endyy
   EndIf
  EndIf

  Flip
  Delay 20
 Wend

End Function

Function Save(Name:String,pix:TPixmap,x1:Int,y1:Int,x2:Int,y2:Int)

 Local NameNeu:String
 NameNeu="bg"+StripDir(StripExt(Lower(Name)))+".jpg"

 Local x:Int,y:Int
 Local xr:Int,yr:Int
 Local pixbg:TPixmap=CreatePixmap(needx,needy,PF_RGB888)
 Local c:Int

 For x=0 To needx-1
 For y=0 To needy-1
  xr=Intp(Float(x1),Float(x2),Float(x)/Float(needx-1))
  yr=Intp(Float(y1),Float(y2),Float(y)/Float(needy-1))
  Limit xr,0,PixmapWidth(pix)-1
  Limit yr,0,PixmapHeight(pix)-1
  c=ReadPixel(pix,xr,yr)
  WritePixel pixbg,x,y,c
 Next
 Next

 SavePixmapJPeg pixbg,NameNeu,80  '<- noch bugy ? wird im IE und VB falsch oder gar nicht angezeigt

 Cls
 DrawPixmap pixbg,0,0
 SetColor 255,255,255
 DrawText "Saved as "+NameNeu,12,12
 DrawText "Click Mouse",12,24
 Flip
 WaitMouse
 FlushMouse

End Function

Function FitPixmap:TPixmap(pix:TPixmap,w:Float,h:Float,Zoom:Int=True)

 'MR 10.07.2005

 'Fit a Pixmap to width,height with correct ratio

 If pix=Null Then Return Null

 Local f1:Float,f2:Float
 Local pw:Float,ph:Float

 pw=pix.width
 ph=pix.height

 f1 = 1.0
 f2 = 1.0

 If Zoom Then      
  If pw <> w Then 'with ZOOM <>
   f1 = w / pw
  End If
  If ph <> h Then
   f2 = h / ph
  End If
 Else
  If pw > w Then 'without ZOOM > 
   f1 = w / pw
  End If
  If ph > h Then
   f2 = h / ph
  End If
 EndIf
     
 If f2 < f1 Then f1 = f2

 pix=ResizePixmap(pix,f1*pw,f1*ph)
 Return pix

End Function

Function mRect(x1,y1,x2,y2)
 DrawLine x1,y1,x2,y1 'oben
 DrawLine x2,y1,x2,y2 'rechts
 DrawLine x1,y2,x2,y2 'unten
 DrawLine x1,y1,x1,y2 'links
End Function

Function Intp:Float(y1:Float,y2:Float,mu:Float)
 Return y1+(y2-y1)*mu
End Function

Function Limit(a:Int Var,x:Int ,y:Int )

 If a<x Then a=x
 If a>y Then a=y

End Function
