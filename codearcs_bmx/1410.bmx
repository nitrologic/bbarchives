; ID: 1410
; Author: Markus Rauch
; Date: 2005-06-26 17:53:25
; Title: Perspective correction to Texture 10.7.2005 !
; Description: Foto to Texture

'Perspektiven Korrektur zu Texture , (C) Markus Rauch 2005

'Der Quelltext ist frei , jedoch möchte ich nicht das daraus
'ein kommerzielles Produkt gemacht wird !
'Also mit diesem Quelltext meine ich , was ihr mit den Texturen macht ist mir egal ;-)

'Foto(perspective correction) to Texture , (C) M.Rauch 2005

'the source is free but you are not permitted to make
'a commercially product from it !
'but you can do anything with your own created textures ;-)

'History:
' 4.7.2005 adding Bezier Interpolation (Hardcore:)
' 8.7.2005 Adding Vector Functions
' 9.7.2005 faster and better GUI with direct Zone access
'10.7.2005 little fix that read all pixel in bezier area :)

'MR 10.07.2005

Strict

Const dirsep:String="\"

AppTitle="Foto (perspective correction) to Texture , M.Rauch 2005 , BlitzMax V1.10"

Local a$,b$
Local width:Int=800,height:Int=600,depth:Int=0,herz:Int=72,gl=0

'Programm Command Line width 1280 height 1024 gl 1
For a$=EachIn AppArgs
 If b$="width" Then width=Int(a$)
 If b$="height" Then height=Int(a$)
 If b$="depth" Then depth=Int(a$)
 If b$="herz" Then herz=Int(a$)
 If b$="gl" Then gl=True
 b$=a$
Next

If gl Then SetGraphicsDriver GLMax2DDriver()  

If GraphicsModeExists(width,height,depth,herz)=True Then
 Graphics width,height,depth,herz
Else
 Graphics 640,480
EndIf

'Local mfont:TImageFont=LoadImageFont("TAHOMA.TTF",14) ':-(
'Local mfont:TImageFont=LoadImageFont("C:\WINDOWS\Fonts\TAHOMA.TTF",14) '!?
'SetImageFont mfont

SetMaskColor(255,0,255)

Global fy=16

'----------------------------------------------------------------------

'Zones

Type TZone
 Field Mode:Int 'Wo die Zone angezeigt werden soll , 0=immer anzeigen 
 Field Caption$
 Field img:TImage

 Field Visible:Int

 Field x1:Int 'Pos
 Field y1:Int
 Field w:Int 'Breite
 Field h:Int 'Höhe

 Field Tag:String
 Field TagFloat:Float 'Zum merken von Werten

 Field Checkbox:Int '0 1
 Field Checked:Int '0 1

 Field SliderX:Int '0 1
 Field SliderXValue:Float
 Field SliderXMin:Float
 Field SliderXMax:Float

 Field SliderY:Int '0 1
 Field SliderYValue:Float
 Field SliderYMin:Float
 Field SliderYMax:Float

 Field wi:Int 'Winkel für blinkende Farbe

    Function Create:TZone()
     Local I:TZone=New TZone
     Return I
	End Function

End Type

Global Zonen:TList=CreateList()

Const cZoneModeAll:Int=0
Const cZoneModeNormal:Int=1
Const cZoneModeTextureSize:Int=2
Const cZoneModeSelectPoints:Int=3
Const cZoneModeSelectPointsBez:Int=4
Const cZoneModeBlend:Int=5

Global ZoneMode:Int=cZoneModeAll

Global cZoneWeiter:TZone
Global cZoneZurueck:TZone

Global cZoneTextureSizeX:TZone
Global cZoneTextureSizeY:TZone

Global cZoneSelectScale1:TZone
Global cZoneSelectScale2:TZone
Global cZoneSelectScale3:TZone
Global cZoneSelectScale4:TZone

Global cZoneBlendX:TZone
Global cZoneBlendY:TZone
Global cZoneBlendRange1:TZone
Global cZoneBlendRange2:TZone
Global cZoneBlendRange3:TZone
Global cZoneBlendRange4:TZone
Global cZoneBlendRange5:TZone
Global cZoneBlendRange6:TZone

Local ox,oy,oxx,oyy,osp,oxm,oym
osp=4
oyy=32

ox=0
oy=0
oxx=32

'Incbin "Images\PfeilL.bmp"
'Incbin "Images\PfeilR.bmp"

'--- All
cZoneZurueck=ZoneNew(cZoneModeAll,"<<<","Incbin::Images\PfeilL.bmp",ox,oy,oxx,oyy)
ox=ox+oxx+osp
cZoneWeiter=ZoneNew(cZoneModeAll,">>>","Incbin::Images\PfeilR.bmp",ox,oy,oxx,oyy)
ox=ox+oxx+osp
oxm=ox
'--- TextureSize
ox=oxm
cZoneTextureSizeX=ZoneNew(cZoneModeTextureSize,"X","",ox,oy,oxx,oyy)
ox=ox+oxx+osp
cZoneTextureSizeY=ZoneNew(cZoneModeTextureSize,"Y","",ox,oy,oxx,oyy)
ox=ox+oxx+osp
'--- SelectPoints
ox=oxm
cZoneSelectScale1=ZoneNew(cZoneModeSelectPoints,"1x","",ox,oy,oxx,oyy)
ox=ox+oxx+osp
cZoneSelectScale2=ZoneNew(cZoneModeSelectPoints,"2x","",ox,oy,oxx,oyy)
ox=ox+oxx+osp
cZoneSelectScale3=ZoneNew(cZoneModeSelectPoints,"3x","",ox,oy,oxx,oyy)
ox=ox+oxx+osp
cZoneSelectScale4=ZoneNew(cZoneModeSelectPoints,"4x","",ox,oy,oxx,oyy)
ox=ox+oxx+osp
'--- Blend
ox=oxm
cZoneBlendX=ZoneNew(cZoneModeBlend,"X","",ox,oy,oxx,oyy)
ZoneAsCheckbox cZoneBlendX
ox=ox+oxx+osp
cZoneBlendY=ZoneNew(cZoneModeBlend,"Y","",ox,oy,oxx,oyy)
ZoneAsCheckbox cZoneBlendY
ox=ox+oxx+osp

cZoneBlendRange1=ZoneNew(cZoneModeBlend,"R1","",ox,oy,oxx,oyy)
ZoneAsCheckbox cZoneBlendRange1
ox=ox+oxx+osp
cZoneBlendRange2=ZoneNew(cZoneModeBlend,"R2","",ox,oy,oxx,oyy)
ZoneAsCheckbox cZoneBlendRange2,1
ox=ox+oxx+osp
cZoneBlendRange3=ZoneNew(cZoneModeBlend,"R3","",ox,oy,oxx,oyy)
ZoneAsCheckbox cZoneBlendRange3
ox=ox+oxx+osp
cZoneBlendRange4=ZoneNew(cZoneModeBlend,"R4","",ox,oy,oxx,oyy)
ZoneAsCheckbox cZoneBlendRange4
ox=ox+oxx+osp
cZoneBlendRange5=ZoneNew(cZoneModeBlend,"R5","",ox,oy,oxx,oyy)
ZoneAsCheckbox cZoneBlendRange5
ox=ox+oxx+osp
cZoneBlendRange6=ZoneNew(cZoneModeBlend,"R6","",ox,oy,oxx,oyy)
ZoneAsCheckbox cZoneBlendRange6
ox=ox+oxx+osp

cZoneBlendRange1.TagFloat=0.01
cZoneBlendRange2.TagFloat=0.025
cZoneBlendRange3.TagFloat=0.05
cZoneBlendRange4.TagFloat=0.10
cZoneBlendRange5.TagFloat=0.25
cZoneBlendRange6.TagFloat=0.50

'----------------------------------------------------------------------

Type TV3D
 Field x:Float
 Field y:Float
 Field z:Float
 
 Function Create:TV3D()
  Local T:TV3D=New TV3D
  Return T
 End Function

 Method Set(x:Float,y:Float,z:Float=0)
  self.x=x
  self.y=y
  self.z=z
 End Method 

 Method Clr()
  self.x=0
  self.y=0
  self.z=0
 End Method 
 
End Type

Global mx:Float[4,4]  'Matrix
Global mx1:Float[4,4] 'Matrix1
Global mx2:Float[4,4] 'Matrix2

'----------------------------------------------------------------------

MainLoop
EndGraphics()
End

'----------------------------------------------------------------------

Function MainLoop()

 '-------------------------
 '1.DateiDialog 
 '2.Bild Laden
 '3.Bild Zeigen , mit der Maus 4 Punkte markieren und als Linien anzeigen mit Alpha
 '4.Texture Größe wählen
 '5.Bild umrechnen
 '6.Bild anzeigen als Tile
 '7.Bild anzeigen als Tile zum überblenden
 '8.Bild speichern
 '9.Bild nochmal zeigen
 '-------------------------
 
 Const mode_LoadImageDialog	=1
 Const mode_LoadImage		=2
 Const mode_SelectPoints	=3
 Const mode_SelectPointsBez	=4
 Const mode_TextureSize		=5
 Const mode_TransformImage	=6
 Const mode_ShowTiledImage	=7
 Const mode_ShowBlend		=8
 Const mode_SaveAsDialog	=9
 Const mode_ShowAfterSave	=10

 DebugLog "FUNC MainLoop"

 SetClsColor 0,0,0

 SetBlend ALPHABLEND

 'SetLineWidth 3

 Local mode=mode_LoadImageDialog

 Local pix:TPixmap
 Local pix2:TImage
 Local pix3:TImage 'für Blend

 Local filename:String
 Local filenamesave:String

 Local p:TV3D=TV3D.Create() 'für Plot der Bezier Splines

 Local p1:TV3D=TV3D.Create()
 Local p2:TV3D=TV3D.Create()
 Local p3:TV3D=TV3D.Create()
 Local p4:TV3D=TV3D.Create()
 
 'Zwichenpunkte für Bezier4
 Local p1a:TV3D=TV3D.Create()
 Local p1b:TV3D=TV3D.Create()
 Local p2a:TV3D=TV3D.Create()
 Local p2b:TV3D=TV3D.Create()
 Local p3a:TV3D=TV3D.Create()
 Local p3b:TV3D=TV3D.Create()
 Local p4a:TV3D=TV3D.Create()
 Local p4b:TV3D=TV3D.Create()

 Local m:TV3D=TV3D.Create() 'Maus

 Local txx:Float=256
 Local txy:Float=256

 Local pointnr:Int=1
 Local pointnrf:Int=0
 Local pointnrb:Int=0

 Local mu:Float=0 'für Bezier4 Splines

 Local mwheel:Int,md1:Int,mu1:Int,md2:Int,mu2:Int,md3:Int,mu3:Int 'Maus Abfrage

 Local ret:Int
 Local w:Double
 Local scale:Int=1
 Local db:Int=0

 Local BlendX:Int=0
 Local BlendY:Int=0
 Local BlendRange:Float=0

 Local t1 'Timer für konstante Frame Rate

 Local Zone:TZone=Null
 ZoneMode=cZoneModeNormal

 While Not KeyHit(KEY_ESCAPE)

  SetViewport 0,0,GraphicsWidth(),GraphicsHeight() 

  Cls

  t1=MilliSecs()

  '------------------------------------------------------------- Maus Abfrage !

  m.Set MouseX(),MouseY()

  mwheel=MouseZ() 'Speed()

  mu1=0;If md1=1 Then md1=2
  If MouseDown(1)=True  And md1=0           Then md1=1;mu1=0
  If MouseDown(1)=False And md1=2 And mu1=0 Then md1=0;mu1=1

  mu2=0;If md2=1 Then md2=2
  If MouseDown(2)=True  And md2=0           Then md2=1;mu2=0
  If MouseDown(2)=False And md2=2 And mu2=0 Then md2=0;mu2=1

  mu3=0;If md3=1 Then md3=2
  If MouseDown(3)=True  And md3=0           Then md3=1;mu3=0
  If MouseDown(3)=False And md3=2 And mu3=0 Then md3=0;mu3=1

  '--------------

  Zone=ZoneShow(m,md1,md2,md3)

  Select mode
  '--------------------------------------------------------------------------------
  Case mode_LoadImageDialog

   ZoneMode=cZoneModeNormal

   DebugLog "Bild auswählen in "+AppDir 

   filename=RequestFile("Please select a image","Image jpg,jpeg,pcx,tga,bmp,gif,png:jpg,jpeg,pcx,tga,bmp,gif,png;All Files *.*:*",False) ',AppDir+"\") scheiße
   If Len(filename)=0 Then
    DebugLog "Kein Bild ausgewählt , ENDE"
    End
   Else
    mode=mode_LoadImage
   EndIf
   FlushMouse
   FlushKeys

   scale=1

  '--------------------------------------------------------------------------------
  Case mode_LoadImage

   ZoneMode=cZoneModeNormal

   DebugLog "Bild laden"

   pix3=Null
   pix2=Null
   pix=Null
   pix=LoadPixmap(filename) 
   ConvertPixmap pix,PF_RGB888

   If scale>1 Then 
    pix=ResizePixmap(pix,pix.width*scale,pix.height*scale)
   EndIf

   If pix.width>GraphicsWidth() Or pix.height>GraphicsHeight()-32 Then
    pix=ResizePixmap(pix,GraphicsWidth(),GraphicsHeight()-32)
   EndIf

   pointnr=1
   pointnrf=0
   pointnrb=0

   p1.Set 0,0
   p2.Set pix.width-1,0
   p3.Set pix.width-1,pix.height-1
   p4.Set 0,pix.height-1

   mode=mode_SelectPoints

   DebugLog "ab jetzt Punkte wählen"

  '--------------------------------------------------------------------------------
  Case mode_SelectPoints

   ZoneMode=cZoneModeSelectPoints

   'Bei DrawPixMap , eine Pixmap muß ins Fenster passen sonnst kommt ein Fehler !!!

   SetAlpha 1.0
   SetColor 255,255,255

   If pix.width/scale*2<GraphicsWidth() And pix.height/scale*2<GraphicsHeight()-32 Then cZoneSelectScale2.Visible=1 Else cZoneSelectScale2.Visible=0
   If pix.width/scale*3<GraphicsWidth() And pix.height/scale*3<GraphicsHeight()-32 Then cZoneSelectScale3.Visible=1 Else cZoneSelectScale3.Visible=0
   If pix.width/scale*4<GraphicsWidth() And pix.height/scale*4<GraphicsHeight()-32 Then cZoneSelectScale4.Visible=1 Else cZoneSelectScale4.Visible=0

   If cZoneSelectScale2.Visible=1 Then
    cZoneSelectScale1.Visible=1
    DrawText "You can scale the image (before select the 4 points :)",32*6+4*6+10,16-TextHeight("Use,g")/2
   Else
    cZoneSelectScale1.Visible=0
   EndIf

   SetOrigin 0,32
 
   m.y=m.y-32

   DrawPixmap pix,0,32 'Original Bild

   SetAlpha 0.5  
   SetColor 0,191.0+Sin(w)*64.0,255

   VLine p1,p2,False
   VLine p2,p3,False
   VLine p3,p4,False
   VLine p4,p1,False

   If Zone=Null Then
    SetAlpha 1    

    'Zwischenpunkte
    SetColor 128,128,128
    Circle p1a,3
    Circle p1b,3
    Circle p2a,3
    Circle p2b,3
    Circle p3a,3
    Circle p3b,3
    Circle p4a,3
    Circle p4b,3

    If pointnrf=1 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p1,3
    If pointnrf=2 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p2,3
    If pointnrf=3 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p3,3
    If pointnrf=4 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p4,3

    SetColor 255,255,0
    Circle m,3  'Maus
   EndIf

    If (md1=1 And Zone=cZoneSelectScale1) Then scale=1;mode=mode_LoadImage
    If (md1=1 And Zone=cZoneSelectScale2) Then scale=2;mode=mode_LoadImage
    If (md1=1 And Zone=cZoneSelectScale3) Then scale=3;mode=mode_LoadImage
    If (md1=1 And Zone=cZoneSelectScale4) Then scale=4;mode=mode_LoadImage

    If md2=1 Then
     mode=mode_LoadImage
    EndIf

    If Zone=Null Then
    Select pointnr
    Case 1
     p1.x=m.x
     p1.y=m.y
    Case 2
     p2.x=m.x
     p2.y=m.y
    Case 3
     p3.x=m.x
     p3.y=m.y
    Case 4
     p4.x=m.x
     p4.y=m.y
    End Select
    EndIf 'Nicht in Zone

   If md1=1 And Zone=Null Then  
    Select pointnr
    Case 1
     pointnr=2
    Case 2
     pointnr=3
    Case 3
     pointnr=4
    Case 4
     pointnr=5
    Case 5
     'ab jetzt fein Tuning an den Punkten
    End Select
   EndIf

   'Punkt auswählen
   If md1=1 And pointnr=5 And Zone=Null Then  
     pointnrf=0
     If VEntXY(m,p1)<5 Then pointnrf=1
     If VEntXY(m,p2)<5 Then pointnrf=2
     If VEntXY(m,p3)<5 Then pointnrf=3
     If VEntXY(m,p4)<5 Then pointnrf=4
   EndIf
   'bewegen mit gedrückter Maustaste
   If md1=2 And pointnr=5 And Zone=Null Then
    Select pointnrf 'fein tuning
    Case 1
     p1.x=m.x
     p1.y=m.y
    Case 2
     p2.x=m.x
     p2.y=m.y
    Case 3
     p3.x=m.x
     p3.y=m.y
    Case 4
     p4.x=m.x
     p4.y=m.y
    End Select
   EndIf

   If KeyHit(KEY_LEFT)>0 Then
    Select pointnrf 'fein tuning
    Case 1
     p1.x=p1.x-1
    Case 2
     p2.x=p2.x-1
    Case 3
     p3.x=p3.x-1
    Case 4
     p4.x=p4.x-1
    End Select
   EndIf
   If KeyHit(KEY_RIGHT)>0 Then
    Select pointnrf 'fein tuning
    Case 1
     p1.x=p1.x+1
    Case 2
     p2.x=p2.x+1
    Case 3
     p3.x=p3.x+1
    Case 4
     p4.x=p4.x+1
    End Select
   EndIf

   If KeyHit(KEY_UP)>0 Then
    Select pointnrf 'fein tuning
    Case 1
     p1.y=p1.y-1
    Case 2
     p2.y=p2.y-1
    Case 3
     p3.y=p3.y-1
    Case 4
     p4.y=p4.y-1
    End Select
   EndIf
   If KeyHit(KEY_DOWN)>0 Then
    Select pointnrf 'fein tuning
    Case 1
     p1.y=p1.y+1
    Case 2
     p2.y=p2.y+1
    Case 3
     p3.y=p3.y+1
    Case 4
     p4.y=p4.y+1
    End Select
   EndIf

   '-------------- Limit Points !

   Limit p1.x,0,pix.width-1
   Limit p2.x,0,pix.width-1
   Limit p3.x,0,pix.width-1
   Limit p4.x,0,pix.width-1

   Limit p1.y,0,pix.height-1
   Limit p2.y,0,pix.height-1
   Limit p3.y,0,pix.height-1
   Limit p4.y,0,pix.height-1

   '-------------- Berechne zwischen Punkte

   Zwischenpunkt p1a,p1b,p1,p2
   Zwischenpunkt p2a,p2b,p2,p3
   Zwischenpunkt p3a,p3b,p3,p4
   Zwischenpunkt p4a,p4b,p4,p1

   pointnrb=0

   '--------------

   SetOrigin 0,0

   m.y=m.y+32

  '--------------------------------------------------------------------------------
  Case mode_SelectPointsBez

   ZoneMode=cZoneModeSelectPointsBez

   'Bei DrawPixMap , eine Pixmap muß ins Fenster passen sonnst kommt ein Fehler !!!

   SetAlpha 1.0
   SetColor 255,255,255
   DrawText "Move Bezier Points",32*6+4*6+10,16-TextHeight("Move")/2

   'SetViewport 0,32,GraphicsWidth(),GraphicsHeight()-32
   SetOrigin 0,32
 
   m.y=m.y-32

   DrawPixmap pix,0,32 'Original Bild

   SetAlpha 0.5  

   db=0
   For mu=0 To 1 Step 0.025
    SetColor 255*db,255*db,255*db
    db=1-db
    Bezier4(p,p1,p1a,p1b,p2,mu) 
    Circle p,2
    Bezier4(p,p2,p2a,p2b,p3,mu)
    Circle p,2
    Bezier4(p,p3,p3a,p3b,p4,mu)
    Circle p,2
    Bezier4(p,p4,p4a,p4b,p1,mu)
    Circle p,2
   Next

   If Zone=Null Then

    SetAlpha 1    

    'Zwischenpunkte
    If pointnrb=1 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p1a,3
    If pointnrb=2 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p1b,3

    If pointnrb=3 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p2a,3
    If pointnrb=4 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p2b,3

    If pointnrb=5 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p3a,3
    If pointnrb=6 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p3b,3

    If pointnrb=7 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p4a,3
    If pointnrb=8 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p4b,3

    'Normale Punkte
    If pointnrb=9 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p1,3
    If pointnrb=10 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p2,3
    If pointnrb=11 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p3,3
    If pointnrb=12 Then SetColor 0,255,0 Else SetColor 255,0,0
    CircleB p4,3

    SetColor 255,255,0
    Circle m,3 
   EndIf 'Zone

    If md2=1 Then 'Zurück
     mode=mode_SelectPoints
    EndIf

   'Punkt auswählen
   If md1=1 And Zone=Null Then  
     pointnrb=0
     If VEntXY(m,p1a)<5 Then pointnrb=1
     If VEntXY(m,p1b)<5 Then pointnrb=2
     If VEntXY(m,p2a)<5 Then pointnrb=3
     If VEntXY(m,p2b)<5 Then pointnrb=4
     If VEntXY(m,p3a)<5 Then pointnrb=5
     If VEntXY(m,p3b)<5 Then pointnrb=6
     If VEntXY(m,p4a)<5 Then pointnrb=7
     If VEntXY(m,p4b)<5 Then pointnrb=8
     '....
     If VEntXY(m,p1)<5 Then pointnrb=9
     If VEntXY(m,p2)<5 Then pointnrb=10
     If VEntXY(m,p3)<5 Then pointnrb=11
     If VEntXY(m,p4)<5 Then pointnrb=12
   EndIf
   'bewegen mit gedrückter Maustaste
   If md1=2 And Zone=Null Then
    Select pointnrb 
    Case 1
     p1a.x=m.x
     p1a.y=m.y
    Case 2
     p1b.x=m.x
     p1b.y=m.y
    Case 3
     p2a.x=m.x
     p2a.y=m.y
    Case 4
     p2b.x=m.x
     p2b.y=m.y
    Case 5
     p3a.x=m.x
     p3a.y=m.y
    Case 6
     p3b.x=m.x
     p3b.y=m.y
    Case 7
     p4a.x=m.x
     p4a.y=m.y
    Case 8
     p4b.x=m.x
     p4b.y=m.y
    Case 9 '.
     p1.x=m.x
     p1.y=m.y
    Case 10 '..
     p2.x=m.x
     p2.y=m.y
    Case 11 '...
     p3.x=m.x
     p3.y=m.y
    Case 12 '....
     p4.x=m.x
     p4.y=m.y
    End Select
   EndIf

   '--------------

   SetOrigin 0,0

   m.y=m.y+32

  '--------------------------------------------------------------------------------
  Case mode_TextureSize

   ZoneMode=cZoneModeTextureSize

   SetViewport 0,32,GraphicsWidth(),GraphicsHeight()-32
   SetOrigin 0,32

   If KeyHit(KEY_X)>0 Or (md1=1 And Zone=cZoneTextureSizeX) Then txx=txx*2 ; txx=txx Mod 4096;If txx=0 Or txx/2=GraphicsWidth() Then txx=8
   If KeyHit(KEY_Y)>0 Or (md1=1 And Zone=cZoneTextureSizeY) Then txy=txy*2 ; txy=txy Mod 4096;If txy=0 Or txy/2=GraphicsHeight() Then txy=8

   'ToDo ... mit Shift rückwärts ... XY

   If txx>GraphicsWidth()  Then txx=GraphicsWidth()
   If txy>GraphicsHeight() Then txy=GraphicsHeight()

   SetAlpha 0.5
   SetColor 200,255,200
   DrawRect 0,0,txx,txy

   SetAlpha 1.0
   SetColor 255,255,255
   DrawText "Output Texture Size , Press X or Y Key",10,fy
   DrawText "X="+Int(txx)+" x Y="+Int(txy),10,fy*2

   SetOrigin 0,0

  '--------------------------------------------------------------------------------
  Case mode_TransformImage

   ZoneMode=cZoneModeNormal

   DebugLog "umrechnen"

   pix2=TransformImage(pix,txx,txy,p1,p2,p3,p4,p1a,p1b,p2a,p2b,p3a,p3b,p4a,p4b)
   'pix2=LoadImage("Images\Karo.bmp") 'Zum testen
   pix3=CopyImage(pix2)
 
   mode=mode_ShowTiledImage

  '--------------------------------------------------------------------------------
  Case mode_ShowTiledImage

   ZoneMode=cZoneModeNormal

   SetAlpha 1.0
   SetColor 255,255,255
   DrawText "Ready",32*6+4*6+10,16-TextHeight("R")/2

    SetViewport 0,32,GraphicsWidth(),GraphicsHeight()-32
    SetOrigin 0,32

    SetAlpha 1.0
    SetColor 255,255,255
    TileImage pix2,0,0 'Ergebnis

    SetOrigin 0,0

  '--------------------------------------------------------------------------------
  Case mode_ShowBlend

   ZoneMode=cZoneModeBlend

   SetAlpha 1.0
   SetColor 255,255,255
   DrawText "Blending",32*10+4*10+10,16-TextHeight("B")/2

    SetViewport 0,32,GraphicsWidth(),GraphicsHeight()-32
    SetOrigin 0,32

    'XY übergänge berechnen (2 Pass sonnst überkreuzt sich das)

    If md1=1 Then
     If Zone=cZoneBlendRange1 Or Zone=cZoneBlendRange2 Or Zone=cZoneBlendRange3 Or Zone=cZoneBlendRange4 Or Zone=cZoneBlendRange5 Or Zone=cZoneBlendRange6 Then
      BlendX=cZoneBlendX.Checked
      BlendY=cZoneBlendY.Checked
      BlendRange=Zone.TagFloat
      If Zone<>cZoneBlendRange1 Then cZoneBlendRange1.Checked=0
      If Zone<>cZoneBlendRange2 Then cZoneBlendRange2.Checked=0
      If Zone<>cZoneBlendRange3 Then cZoneBlendRange3.Checked=0
      If Zone<>cZoneBlendRange4 Then cZoneBlendRange4.Checked=0
      If Zone<>cZoneBlendRange5 Then cZoneBlendRange5.Checked=0
      If Zone<>cZoneBlendRange6 Then cZoneBlendRange6.Checked=0
      
      pix3=BlendImage(BlendImage(pix2,BlendX,0,BlendRange),0,BlendY,BlendRange)
     EndIf
     If Zone=cZoneBlendX Or Zone=cZoneBlendY Then
      BlendX=cZoneBlendX.Checked
      BlendY=cZoneBlendY.Checked
      BlendRange=0

      If cZoneBlendRange1.Checked=1 Then BlendRange=cZoneBlendRange1.TagFloat
      If cZoneBlendRange2.Checked=1 Then BlendRange=cZoneBlendRange2.TagFloat
      If cZoneBlendRange3.Checked=1 Then BlendRange=cZoneBlendRange3.TagFloat
      If cZoneBlendRange4.Checked=1 Then BlendRange=cZoneBlendRange4.TagFloat
      If cZoneBlendRange5.Checked=1 Then BlendRange=cZoneBlendRange5.TagFloat
      If cZoneBlendRange6.Checked=1 Then BlendRange=cZoneBlendRange6.TagFloat

      pix3=BlendImage(BlendImage(pix2,BlendX,0,BlendRange),0,BlendY,BlendRange)
     EndIf
    EndIf 'click

    SetAlpha 1.0
    SetColor 255,255,255
    TileImage pix3,0,0 'Ergebnis

    SetOrigin 0,0

  '--------------------------------------------------------------------------------
  Case mode_SaveAsDialog

   ZoneMode=cZoneModeNormal

   DebugLog "Save As ..."

   filenamesave=RequestFile("Texture save as ...","Image png:png",True) 
   If Len(filenamesave)=0 Then
    DebugLog "Kein Dateiname zum speichern ausgewählt"
    'abbruch dann Bild wieder zeigen
    mode=mode_ShowBlend
   Else
    DebugLog "Bild speichern "+filenamesave
    Local map:TPixmap
    map=LockImage(pix3)
    ret=SavePixmapPNG(map,filenamesave)  
    UnlockImage pix3
    DebugLog ret

    mode=mode_ShowAfterSave
   EndIf
   'FlushMouse
   FlushKeys

  '--------------------------------------------------------------------------------
  Case mode_ShowAfterSave

   ZoneMode=cZoneModeNormal

    SetViewport 0,32,GraphicsWidth(),GraphicsHeight()-32
    SetOrigin 0,32

    SetAlpha 1.0
    SetColor 255,255,255
    TileImage pix3,0,0 'Ergebnis

    SetAlpha 0.5
    SetColor 255,255,255
    DrawText "Texture saved as",10,fy
    DrawText filenamesave,10,fy*2

    SetOrigin 0,0

  End Select 'Modus

  '--------------

  SetViewport 0,0,GraphicsWidth(),GraphicsHeight() 

  If KeyHit(KEY_SPACE) Or (md1=1 And Zone=cZoneWeiter) Then 'Weiter zum nächsten Schritt
   Select mode
   Case mode_SelectPoints 	; mode=mode+1
   Case mode_SelectPointsBez; mode=mode+1
   Case mode_TextureSize 	; mode=mode+1
   Case mode_ShowTiledImage ; mode=mode+1
   Case mode_ShowBlend		; mode=mode+1
   Case mode_ShowAfterSave 	; mode=mode_LoadImageDialog
   End Select
  EndIf

  '--------------

  If KeyHit(KEY_BACKSPACE) Or (md1=1 And Zone=cZoneZurueck) Then 'Zurück zum letzten Schritt
   Select mode
   Case mode_SelectPoints 	; mode=mode_LoadImageDialog 'Datei Dialog
   Case mode_SelectPointsBez; mode=mode_SelectPoints 'Punkte verschieben
   Case mode_TextureSize 	; mode=mode_SelectPointsBez 'Bezier Punkte verschieben
   Case mode_ShowTiledImage ; mode=mode_TextureSize
   Case mode_ShowBlend		; mode=mode_ShowTiledImage  
   Case mode_ShowAfterSave 	; mode=mode_ShowBlend 'nochmal anzeigen
   End Select
  EndIf

  '-------------- Memory :)

  'SetAlpha 1.0
  'SetColor 255,255,255
  'DrawText "MemAlloced="+MemAlloced(),GraphicsWidth()-200,16-TextHeight("Mem")/2

  '--------------

  w=w+0.5;If w>360 Then w=w-360

  '--------------
  FlushMem

  While Abs(t1-MilliSecs())<10
  Wend

  Flip

 Wend

End Function

'----------------------------------------------------------------------

Function Intp:Float(y1:Float,y2:Float,mu:Float)
 Return y1+(y2-y1)*mu
End Function

'----------------------------------------------------------------------

Function Bezier4(p:TV3D Var,p1:TV3D,p2:TV3D,p3:TV3D,p4:TV3D,mu:Float)

 'MR 01.07.2005

 'Four control point Bezier interpolation
 'mu ranges from 0 To 1, start To End of curve

 Local mum1:Float,mum13:Float,mu3:Float

 mum1 = 1.0 - mu
 mum13 = mum1 * mum1 * mum1
 mu3 = mu * mu * mu

 p.x = mum13*p1.x + 3.0*mu*mum1*mum1*p2.x + 3.0*mu*mu*mum1*p3.x + mu3*p4.x
 p.y = mum13*p1.y + 3.0*mu*mum1*mum1*p2.y + 3.0*mu*mu*mum1*p3.y + mu3*p4.y

 p.z = mum13*p1.z + 3.0*mu*mum1*mum1*p2.z + 3.0*mu*mu*mum1*p3.z + mu3*p4.z

End Function

'----------------------------------------------------------------------

Function Circle(p:TV3D,r)
   DrawOval p.x-r,p.y-r,r*2,r*2
End Function

Function CircleB(p:TV3D,r)

 Local red,green,blue
 GetColor red,green,blue
 SetColor 0,0,0
 DrawOval p.x-r-2,p.y-r-2,r*2+4,r*2+4
 
 SetColor red,green,blue
 DrawOval p.x-r,p.y-r,r*2,r*2
End Function

'##################################################################################################

Function TransformImage:TImage(pix:TPixmap,txx:Float,txy:Float,p1:TV3D,p2:TV3D,p3:TV3D,p4:TV3D,p1a:TV3D,p1b:TV3D,p2a:TV3D,p2b:TV3D,p3a:TV3D,p3b:TV3D,p4a:TV3D,p4b:TV3D)

 'pix=original bild
 'txx,txy Texture größe
 'p1,p2,p3,p4 Punkte im Original Bild (ca. Trapez)
 'p a&b sind die Hilfspunkte (Bezier Help Points) 

 Local x:Float
 Local y:Float
 Local pix2:TImage
 Local map:TPixmap



	'-------------------------

 Local mu:Float=0,p:TV3D=TV3D.Create(),db:Int=0 'Für außen Curven

	x=0
	y=0

 Local Oben:TV3D=TV3D.Create()
 Local Unten:TV3D=TV3D.Create()
 Local Links:TV3D=TV3D.Create()
 Local Rechts:TV3D=TV3D.Create()
	
	Local xx:Float,yy:Float 'Pixel im Quellbild
    Local zz:Float 'höhe
	Local col:Int  'Farbe
 
	Local BlendX:Float
	Local BlendXInv:Float
	Local BlendY:Float
	Local BlendYInv:Float


    '>>>
	DrawPixmap pix,0,0
    '<<<
			
	pix2=CreateImage(txx,txy,PF_RGB888)
	map=LockImage(pix2)

    txx=txx-1 'weil ja von 0 an in die Texture geschrieben wird , z.B. 0 bis (256-1)
    txy=txy-1

	For x=0 To txx
	For y=0 To txy

     BlendX=x/txx '0-1
     BlendY=y/txy '0-1

     BlendXInv=1.0-BlendX '1-0
     BlendYInv=1.0-BlendY '1-0
	
	 Bezier4 Oben ,p1,p1a,p1b,p2,BlendX
	 Bezier4 Unten,p4,p3b,p3a,p3,BlendX

	 Bezier4 Links ,p1,p4b,p4a,p4,BlendY
	 Bezier4 Rechts,p2,p2a,p2b,p3,BlendY
		
	 xx=(Links.x*BlendXInv + Rechts.x*BlendX)
	 yy=( Oben.y*BlendYInv  + Unten.y*BlendY)
			
	 Limit xx,0,pix.width-1
	 Limit yy,0,pix.height-1
	
	 col=ReadPixel(pix,xx,yy)
	 WritePixel map,x,y,col 
     '>>>
     Color 255,255,0
     VPlot Oben
     VPlot Unten
     VPlot Links
     VPlot Rechts
     If (x Mod 8)=0 Or (y Mod 8)=0 Then

	  Color 255,zz,zz 'welcher Bereich ausgelesen wird
	  Plot xx,yy
	 EndIf
	 '<<<
	Next
	Next
    UnlockImage pix2 

    '------------------------
    db=0
    For mu=0 To 1 Step 0.025
     SetColor 255*db,255*db,255*db
     db=1-db
     Bezier4(p,p1,p1a,p1b,p2,mu) 
     Circle p,2
     Bezier4(p,p2,p2a,p2b,p3,mu)
     Circle p,2
     Bezier4(p,p3,p3a,p3b,p4,mu)
     Circle p,2
     Bezier4(p,p4,p4a,p4b,p1,mu)
     Circle p,2
    Next
    '------------------------
	
    '>>>
    SetAlpha 1.0
    SetColor 255,255,255
    DrawText "Press any Key",5,GraphicsHeight()-TextHeight("P")-5
	Flip
	WaitKey
    '<<<
		
	'-------------------------

 Oben=Null
 Unten=Null
 Links=Null
 Rechts=Null

 Return pix2

End Function

'##################################################################################################

Function ZoneShow:TZone(Maus:TV3D,md1,md2,md3)

 Local Hit=0

 Local x1,y1,x2,y2 

 Local Zone:TZone
 Local ZoneClick:TZone=Null

 For Zone=EachIn Zonen

  If Zone.Mode=ZoneMode Or Zone.Mode=0 Then

   If Zone.Visible=1 Then   

    x1=Zone.X1
    y1=Zone.Y1
    x2=x1+Zone.w-1
    y2=y1+Zone.h-1
 
    'Testen ob Maus drüber ist
    If ((Maus.x>=x1 And Maus.x<=x2) And (Maus.y>=y1 And Maus.y<=y2)) Then
     ZoneClick=Zone
     Hit=True
     Zone.wi=Zone.wi+1;If Zone.wi>180 Then Zone.wi=Zone.wi-180
    Else
     Hit=False
     Zone.wi=0
    EndIf

    'Wenn Maus drüber dann Hintergrund füllen  
    If Hit=True Then
     SetAlpha 0.5+Sin(Zone.wi)/2.0 
     SetColor 0,128,0;DrawRect Zone.x1,Zone.y1,Zone.w,Zone.h
    EndIf

    'Wenn Checkbox und gesetzt dann markieren
    If Zone.Checkbox=1 Then
     If Hit=True And md1=1 Then Zone.Checked=1-Zone.Checked
     If Zone.Checked=1 Then
      SetAlpha 0.75
      SetColor 128,128,255;DrawRect Zone.x1,Zone.y1,Zone.w,Zone.h
     EndIf
    EndIf

    'Bild zeigen wenn da
    SetAlpha 1
    SetColor 255,255,255
    If Zone.img<>Null Then DrawImage Zone.img,Zone.X1,Zone.Y1 

    'Wenn Maus drüber dann Rand zeigen in grün
    SetAlpha 1
    If Hit=True Then SetColor 0,255,0 Else SetColor 128,128,128
    mRect x1,y1,x2,y2

    'wenn kein Bild hat dann Text zeigen
    If Zone.img=Null Then   
     SetColor 255,255,255
     Local t$
     t$=Zone.Caption$
     DrawText t$,x1 + Zone.w/2-TextWidth(t$)/2,y1 + Zone.h/2-TextHeight(t$)/2 ',True,True,255,255,255
    EndIf 'kein Bild dann Caption

   EndIf 'Visible

  EndIf 'in Mode Or For All

 Next

 Return ZoneClick

End Function

'##################################################################################################

Function ZoneNew:TZone(Mode,c$,image$,x,y,w,h)

 Local Zone:TZone=TZone.Create()

 Zone.Mode=Mode
 Zone.Caption=c$

 Zone.Visible=1

 Zone.x1=x
 Zone.y1=y
 Zone.w=w
 Zone.h=h

 Zone.Checkbox=0
 Zone.Checked=0

 Zone.SliderX=0
 Zone.SliderXMin=0
 Zone.SliderXMax=0
 Zone.SliderXValue=0

 Zone.SliderY=0
 Zone.SliderYMin=0
 Zone.SliderYMax=0
 Zone.SliderYValue=0

 If Len(image$)>0 Then Zone.img=LoadImage(image$,MASKEDIMAGE)
 Zonen.addlast Zone

 Return Zone

End Function

'##################################################################################################

Function ZoneCaption(Zone:TZone,c$)

  Zone.Caption=c$

End Function

'##################################################################################################

Function ZoneAsCheckbox(Zone:TZone,Value:Int=0)

  Zone.Checkbox=1
  If value Then
   Zone.Checked=1
  Else
   Zone.Checked=0
  EndIf

End Function

'##################################################################################################

Function ZoneAsSliderX(Zone:TZone,Value:Float,ValueMin:Float=0,ValueMax:Float=100)

 Zone.SliderX=1
 Zone.SliderXValue=Value
 Zone.SliderXMin=ValueMin
 Zone.SliderXMax=ValueMax

End Function

'##################################################################################################

'--------------------------------

Function mRect(x1,y1,x2,y2)
 DrawLine x1,y1,x2,y1 'oben
 DrawLine x2,y1,x2,y2 'rechts
 DrawLine x1,y2,x2,y2 'unten
 DrawLine x1,y1,x1,y2 'links
End Function

'--------------------------------

Function Limit(a:Float Var,x:Int ,y:Int )

 If a<x Then a=x
 If a>y Then a=y

End Function

'--------------------------------

Function CopyImage2:TImage(img:TImage)

 Local imgnew:TImage=CreateImage(ImageWidth(img),ImageHeight(img))

 Local x:Int,y:Int
 Local map:TPixmap
 Local mapnew:TPixmap

 map=LockImage(img) 'Read
 mapnew=LockImage(imgnew) 'Write

 For x=0 To PixmapWidth(map)-1
  For y=0 To PixmapHeight(map)-1
   WritePixel mapnew,x,y,ReadPixel(map,x,y) 
  Next
 Next

 UnlockImage img
 UnlockImage imgnew

 Return imgnew

End Function

'--------------------------------

Function CopyImage:TImage(Image:TImage) 
   Local TempPixmap:TPixmap, NewImage:TImage 
   TempPixmap = LockImage(Image) 
   NewImage   = LoadImage(TempPixmap,DYNAMICIMAGE) 
   UnlockImage(Image) 
   Return NewImage 
End Function 

'--------------------------------

Function BlendImage:TImage(img:TImage,BlendX,BlendY,BlendRange:Double)

 Local imgnew:TImage=CreateImage(ImageWidth(img),ImageHeight(img))

 Local x:Double,y:Double'Pixel 
 Local x2:Double,y2:Double'Pixel auf anderer Seite (Mirror)
 Local map:TPixmap 'original Bild
 Local mapnew:TPixmap 'ausgabe Bild
 Local ARGB:Int '32 Bit Alpha und Farbe

 Local Alpha1:Int 'Original Farbe
 Local Red1:Double
 Local Green1:Double
 Local Blue1:Double

 Local Alpha2:Int 'Farbe auf anderer Seite
 Local Red2:Double
 Local Green2:Double
 Local Blue2:Double

 Local Alpha3:Int 'Farbe gemischt
 Local Red3:Double
 Local Green3:Double
 Local Blue3:Double

 Local Blend:Double
 Blend=2.0

 map=LockImage(img) 'Read
 mapnew=LockImage(imgnew) 'Write

 'PixmapFormat

 Local RangeX:Int=0 'Rand Bereich außen in Pixel
 Local RangeY:Int=0

 Local RangeXBlend:Double=0
 Local RangeYBlend:Double=0
 Local RangeBlend:Double=0

 Local RangeXBlendInv:Double=0
 Local RangeYBlendInv:Double=0
 Local RangeBlendInv:Double=0

 If BlendX Then RangeX=PixmapWidth(map)*BlendRange 'Rand errechnen
 If BlendY Then RangeY=PixmapHeight(map)*BlendRange

 For x=0 To PixmapWidth(map)-1
  For y=0 To PixmapHeight(map)-1
   ARGB=ReadPixel(map,x,y)

   If (x<RangeX Or x>(PixmapWidth(map)-1)-RangeX) Or (y<RangeY Or y>(PixmapHeight(map)-1)-RangeY) Then

    RangeXBlend=1.0
    If RangeX>0 Then
     If x<RangeX Then RangeXBlend=X/RangeX
     If x>(PixmapWidth(map)-1)-RangeX Then RangeXBlend=((PixmapWidth(map)-1)-X)/RangeX
    EndIf

    RangeYBlend=1.0
    If RangeY>0 Then
     If y<RangeY Then RangeYBlend=Y/RangeY
     If y>(PixmapHeight(map)-1)-RangeY Then RangeYBlend=((PixmapHeight(map)-1)-Y)/RangeY
    EndIf

    RangeBlend=(RangeXBlend+RangeYBlend)/2.0

    RangeXBlendInv=1.0-RangeXBlend
    RangeYBlendInv=1.0-RangeYBlend
    RangeBlendInv=(RangeXBlendInv+RangeYBlendInv)/2.0

    'andere Seite
    x2=x
    y2=y
    'TEST
    If BlendX=1 Then x2=(PixmapWidth(map)-1)-x
    If BlendY=1 Then y2=(PixmapHeight(map)-1)-y';If BlendX=1 Then x2=y

    Alpha1=ARGB_Alpha(ARGB)
    Red1  =RangeBlend*Float(ARGB_Red(ARGB))
    Green1=RangeBlend*Float(ARGB_Green(ARGB))
    Blue1 =RangeBlend*Float(ARGB_Blue(ARGB))

    ARGB=ReadPixel(map,x2,y2)
    Alpha2=ARGB_Alpha(ARGB)
    Red2  =RangeBlendInv*Float(ARGB_Red(ARGB))
    Green2=RangeBlendInv*Float(ARGB_Green(ARGB))
    Blue2 =RangeBlendInv*Float(ARGB_Blue(ARGB))

    'DebugStop

    Alpha3=Alpha1
    Red3=(Red1+Red2) '/Blend
    Green3=(Green1+Green2) '/Blend
    Blue3=(Blue1+Blue2) '/Blend

    ARGB=ARGB_Color(Alpha3,Red3,Green3,Blue3)   

   Else
    'ARGB=0 'Test um den unberührten Bereich zu sehen
   EndIf

   WritePixel mapnew,x,y,ARGB 
  Next
 Next
 
 UnlockImage img
 UnlockImage imgnew

 Return imgnew

End Function

'--------------------------------

Function ARGB_Alpha:Int(ARGB:Int)

 Return Int((ARGB & $FF000000:Int) / $1000000:Int)

End Function

Function ARGB_Red:Int(ARGB:Int)
 
 Return Int((ARGB & $00FF0000:Int) / $10000:Int)

End Function

Function ARGB_Green:Int(ARGB:Int)

 Return Int((ARGB & $0000FF00:Int) / $100:Int)
 
End Function

Function ARGB_Blue:Int(ARGB:Int)

 Return (ARGB & $000000FF:Int)

End Function

Function ARGB_Color:Int(Alpha:Int,Red:Int,Green:Int,Blue:Int)

 Return (Alpha*$1000000:Int+Red*$10000:Int+Green*$100:Int+Blue)

End Function

'--------------------------------

Function VNORMAL(p1:TV3D,p2:TV3D,p3:TV3D,n:TV3D Var)

 'MR 09.07.2005
  
 'Oberflächen Normale von einer Ebene mit 3 Punkten (Dreieck)
 
  Local a:TV3D=TV3D.Create()
  Local b:TV3D=TV3D.Create()

  VSUB p2,p1,a
  VSUB p3,p1,b
	
  VCROSS a,b,n

  a=Null
  b=Null

  VNORMALIZE n

End Function

'--------------------------------

Function VNORMALIZE(a:TV3D Var)

 'MR 05.07.2005
  
 'gibt Normvector zurück , aufpassen auf überlauf !

 'also gesamt Vector auf länge 1 bringen 

 Local fa:Float
 
 fa = Sqr(VDOT(a, a))
 
 If fa = 0 Then
  a.x = 0.0
  a.y = 0.0
  a.z = 0.0
 Else
  fa = 1.0 / fa
  a.x = a.x * fa
  a.y = a.y * fa
  a.z = a.z * fa
 End If
 
End Function

'--------------------------------

Function VDOT:Float(a:TV3D, b:TV3D)
 
 'MR 05.07.2005

 'Dotprodukt - Skalarprodukt
 
 'berechnet ein Skalarprodukt zweier Vectoren
 
 Return (a.x * b.x + a.y * b.y + a.z * b.z)
 
End Function

'--------------------------------

Function VCROSS(a:TV3D,b:TV3D,c:TV3D Var)
 
 'MR 05.07.2005
  
 'gibt Vectorprodukt zurück
 
 c.x = a.y * b.z - b.y * a.z
 c.y = a.z * b.x - b.z * a.x
 c.z = a.x * b.y - b.x * a.y
 
End Function

'--------------------------------

Function VADD(v1:TV3D, v2:TV3D,vout:TV3D Var) 

 'MR 05.07.2005
 
 '+
 
 vout.x = v1.x + v2.x
 vout.y = v1.y + v2.y
 vout.z = v1.z + v2.z

End Function

'--------------------------------

Function VSUB(v1:TV3D, v2:TV3D,vout:TV3D Var) 

 'MR 05.07.2005
 
 '-
 
 vout.x = v1.x - v2.x
 vout.y = v1.y - v2.y
 vout.z = v1.z - v2.z

End Function

'--------------------------------

Function VMUL(v1:TV3D, v2:TV3D,vout:TV3D Var)

 'MR 05.07.2005
 
 '*
 
 vout.x = v1.x * v2.x
 vout.y = v1.y * v2.y
 vout.z = v1.z * v2.z

End Function

'--------------------------------

Function VDIR(a:TV3D, b:TV3D,vd:TV3D Var)

 'MR 05.07.2005

 Local hyp:Float
 
 VSUB b,a,vd
 
 hyp = Sqr(vd.x * vd.x + vd.y * vd.y + vd.z * vd.z)
 
 If hyp <> 0.0 Then
  vd.x = vd.x / hyp
  vd.y = vd.y / hyp
  vd.z = vd.z / hyp
 Else
  vd.x = 0.0
  vd.y = 0.0
  vd.z = 0.0
 End If
 
 'returns vector in vd
 
End Function

'--------------------------------

Function VENT:Float(a:TV3D, b:TV3D) 
 
 'MR 05.07.2005
  
 'Entfernung

 Local ve:TV3D=TV3D.Create()
 Local e:Float 

 VSUB b, a,ve
  
 e = Sqr(ve.x * ve.x + ve.y * ve.y + ve.z * ve.z)

 ve=Null

 Return e 
 
End Function

'----------------------------------------------------------------------

Function VENTXY:Float(a:TV3D,b:TV3D)
 Local dx:Float,dy:Float
 dx=b.x-a.x 
 dy=b.y-a.y 

 Return Sqr(dx*dx + dy*dy)
End Function

'--------------------------------

Function VCOPY(v:TV3D, vout:TV3D Var) 

 'MR 05.07.2005
 
 '=
 
 vout.x = v.x
 vout.y = v.y
 vout.z = v.z

End Function

'-------------------------------------------------------------------------------------------------

Function VTRANS(a:TV3D Var)

 'MR 09.07.2005

 'überschribt den Vector ! AUFPASSEN ! 

 Local b:TV3D=TV3D.Create()
 
 'DebugLog "vorher A\xyz"

 'DebugLog a.x
 'DebugLog a.y
 'DebugLog a.z

 b.x = a.x * mx[0, 0] + a.y * mx[1, 0] + a.z * mx[2, 0]
 b.y = a.x * mx[0, 1] + a.y * mx[1, 1] + a.z * mx[2, 1]
 b.z = a.x * mx[0, 2] + a.y * mx[1, 2] + a.z * mx[2, 2]

 'ByRef
  
 a.x = b.x
 a.y = b.y
 a.z = b.z

 'DebugLog "nacher A\xyz"

 'DebugLog a.x
 'DebugLog a.y
 'DebugLog a.z

 'DebugLog "Matrix"

 'DebugLog mx[0,0]
 'DebugLog mx[0,1]
 'DebugLog mx[0,2]

 'DebugLog mx[1,0]
 'DebugLog mx[1,1]
 'DebugLog mx[1,2]

 'DebugLog mx[2,0]
 'DebugLog mx[2,1]
 'DebugLog mx[2,2]

 b=Null
 
End Function

'--------------------------------

Function VLine(p1:TV3D,p2:TV3D,draw_last_pixel=True)

 DrawLine p1.x,p1.y,p2.x,p2.y,draw_last_pixel

End Function

'--------------------------------

Function VPlot(p:TV3D)

 Plot p.x,p.y

End Function

'--------------------------------

Function Zwischenpunkt(pa:TV3D Var,pb:TV3D Var,p1:TV3D,p2:TV3D)
 
 '... 0.25 0.75
 '... 0.33 0.67

 pa.x=Intp(p1.x,p2.x,0.33)
 pa.y=Intp(p1.y,p2.y,0.33)
 pa.z=Intp(p1.z,p2.z,0.33)

 pb.x=Intp(p1.x,p2.x,0.67)
 pb.y=Intp(p1.y,p2.y,0.67)
 pb.z=Intp(p1.z,p2.z,0.67)

End Function

'-------------------------------------------------------------------------------------------------

Function MatrixZero()
 
 'MR 09.07.2005
 
 Local i, j
 
 For i = 0 To 3
  For j = 0 To 3
   mx[i, j] = 0.0
  Next 
 Next 
 
End Function

'-------------------------------------------------------------------------------------------------

Function MatrixCreateIdentity()

 'MR 09.07.2005

 Local i 

 For i = 0 To 3
  mx[i, i] = 1.0
 Next 
 
End Function

'-------------------------------------------------------------------------------------------------

Function MatrixCreateTranslate(a:TV3D)
 
 'MR 09.07.2005
 
 MatrixCreateIdentity()
 
 mx[3, 0] = a.x
 mx[3, 1] = a.y
 mx[3, 2] = a.z
 
End Function

'-------------------------------------------------------------------------------------------------

Function MatrixCreateAxisRotate(axis:TV3D,Angle:Float)

 'MR 09.07.2005
 
 Local sqraxis:TV3D=TV3D.Create()
 
 sqraxis.x = sqare(axis.x)
 sqraxis.y = sqare(axis.y)
 sqraxis.z = sqare(axis.z)
 
 Local cosine:Float
 
 cosine = Cos(Angle)
 
 Local sine:Float
 
 sine = Sin(Angle)
 
 Local one_minus_cosine:Float
 
 one_minus_cosine = 1.0 - cosine
 
 MatrixZero()
 
 mx[0, 0] = sqraxis.x + (1.0 - sqraxis.x) * cosine
 mx[0, 1] = axis.x * axis.y * one_minus_cosine + axis.z * sine
 mx[0, 2] = axis.x * axis.z * one_minus_cosine - axis.y * sine
 
 mx[1, 0] = axis.x * axis.y * one_minus_cosine - axis.z * sine
 mx[1, 1] = sqraxis.y + (1.0 - sqraxis.y) * cosine
 mx[1, 2] = axis.y * axis.z * one_minus_cosine + axis.x * sine
 
 mx[2, 0] = axis.x * axis.z * one_minus_cosine + axis.y * sine
 mx[2, 1] = axis.y * axis.z * one_minus_cosine - axis.x * sine
 mx[2, 2] = sqraxis.z + (1.0 - sqraxis.z) * cosine
 
 mx[3, 3] = 1.0
 
 sqraxis=Null

End Function

'-------------------------------------------------------------------------------------------------

Function MatrixCreateScale(a:TV3D)
 
 'MR 09.07.2005

 MatrixZero
 
 mx[0, 0] = a.x
 mx[1, 1] = a.y
 mx[2, 2] = a.z
 
End Function

'-------------------------------------------------------------------------------------------------

Function MatrixMultiply()

 'MR 09.07.2005
 
 'Multipliziert Matrix 1 & 2
 
 Local i,j
 
 For i = 0 To 3
  For j = 0 To 3
   mx[i, j] = mx1[i, 0] * mx2[0, j] + mx1[i, 1] * mx2[1, j] + mx1[i, 2] * mx2[2, j] + mx1[i, 3] * mx2[3, j]
  Next
 Next
 
End Function

'-------------------------------------------------------------------------------------------------

Function MatrixKamera(AchseX:TV3D,AchseY:TV3D,AchseZ:TV3D)
  
 'MR 09.07.2005
  
  Local o:TV3D=TV3D.Create()

  Local ax:TV3D=TV3D.Create()
  Local ay:TV3D=TV3D.Create()
  Local az:TV3D=TV3D.Create()
  
  VDIR AchseX, o,ax
  VDIR AchseY, o,ay
  VDIR AchseZ, o,az
  
  mx[0, 0] = ax.x
  mx[0, 1] = ay.x
  mx[0, 2] = az.x
  mx[0, 3] = 0
  mx[1, 0] = ax.y
  mx[1, 1] = ay.y
  mx[1, 2] = az.y
  mx[1, 3] = 0
  mx[2, 0] = ax.z
  mx[2, 1] = ay.z
  mx[2, 2] = az.z
  mx[2, 3] = 0
  mx[3, 0] = 0
  mx[3, 1] = 0
  mx[3, 2] = 0
  mx[3, 3] = 1

 o=Null

 ax=Null
 ay=Null
 az=Null
  
End Function

'-------------------------------------------------------------------------------------------------

Function Sqare:Float(x:Float)

 'MR 09.07.2005

 Return (x * x)

End Function

'-------------------------------------------------------------------------------------------------
