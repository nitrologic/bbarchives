; ID: 1762
; Author: jfk EO-11110
; Date: 2006-07-24 18:17:49
; Title: Quick  Media Viewer
; Description: Drop any file onto Media Viewer Icon

;
; Simple and quick multimedia viewer, provided by CSP Games
;
; What is it good for?
;
; This viewer can quickly preview (or "prehear") several formats such as:
; 3ds,x,b3d,md2,bsp (q3),wav,mp3,ogg,mid,mod,it,s3m,bmp,jpg,pcx,tga,png.
; The viewer provides no controls (such as pause, rewind etc.), but a simple and quick "what is it"
; functionality.
;
; Unlike windows preview this viewer will also eat pictures with an incorrect 
; extension (eg. a *.jpg that was renamed to *.bmp, or a *.ogg that was renamed to a *.wav)
; Simply compile this to an exe using Blitz3D, then make a link to it in a corner of your desktop.
; Now simply drag mediafiles to this link icon. To end the preview simply click the window,
; or hit a key. Multiple viewer windows are possible.
;
; Movies are not supported by now, but they usually are no problem. Feel free to add more formats.
;

app$="CSP simple media viewer"
ex$="Hit ESC or LMB to exit"
cmd$=Lower$(CommandLine$())

If cmd$="" Then End
If FileType(cmd$)<>1 Then End



mi$=""
l=Len(cmd$)-1
While (l>0) And mi$<>"."
 mi$=Mid$(cmd$,l,1)
 f$=Right$(cmd$,Len(cmd$)-l)
 l=l-1
Wend




If f$="3ds" Or f$="x" Or f$="b3d"
 Graphics3D 640,480,32,2
 SetBuffer BackBuffer()
 AppTitle app$
 m=LoadMesh(cmd$)
 surfs=CountSurfaces(m)
 If m<>0
  light=CreateLight()
  RotateEntity light,45,45,45
  camera=CreateCamera()
  CameraRange camera,0.1,16000
  TranslateEntity camera,0,MeshHeight(m)*2.0,-(MeshDepth(m)+MeshWidth(m))
  PointEntity camera,m
  While KeyDown(1)=0 And MouseDown(1)=0
   TurnEntity m,0,1,0
   sp#=MouseYSpeed()
   If MouseDown(2) Then TurnEntity m,sp#/10.0,0,0
   RenderWorld()
   Text 0, 0,cmd$
   Text 0,16,"Surfaces: "+surfs
   Text 0,32,"Tris rendered: "+TrisRendered()
   Text 0,48,ex$
   Flip
  Wend
  End
 EndIf
EndIf



If f$="md2"
 Graphics3D 640,480,32,2
 SetBuffer BackBuffer()
 AppTitle app$
 m=LoadMD2(cmd$)
 If m<>0
  AnimateMD2 m,1,0.1
  light=CreateLight()
  RotateEntity light,45,45,45
  camera=CreateCamera()
  CameraRange camera,0.1,16000
  PointEntity camera,m
  While KeyDown(1)=0 And MouseDown(1)=0
   PositionEntity camera,0,MouseY(),-MouseY()
   PointEntity camera,m
   TurnEntity m,0,1,0
   UpdateWorld()
   RenderWorld()
   Text 0, 0,cmd$
   Text 0,16,"Tris rendered: "+TrisRendered()
   Text 0,32,ex$
   Flip
  Wend
  End
 EndIf
EndIf


If f$="bsp"
Print "loading bsp"
 Graphics3D 640,480,32,2
 SetBuffer BackBuffer()
 AppTitle app$
 m=LoadBSP(cmd$)
 If m<>0
  light=CreateLight()
  RotateEntity light,45,45,45
  camera=CreateCamera()
  CameraRange camera,0.1,16000
  PointEntity camera,m
  While KeyDown(1)=0 And MouseDown(1)=0
   PositionEntity camera,0,MouseY()*10.0,-MouseY()*10.0
   PointEntity camera,m
   TurnEntity m,0,1,0
   UpdateWorld()
   RenderWorld()
   Text 0, 0,cmd$
   Text 0,16,"Tris rendered: "+TrisRendered()
   Text 0,32,ex$
   Flip
  Wend
  End
 EndIf
 Print "failure."
EndIf







If f$="wav" Or f$="mp3" Or f$="ogg"
 Graphics 640,200,32,2
 AppTitle app$
 SetBuffer BackBuffer()
 s=LoadSound(cmd$)
 If s<>0
  LoopSound s
  chn=PlaySound(s)
  Print "Playing: "+cmd$
  Print ex$
  While KeyDown(1)=0 And MouseDown(1)=0
   Delay 10
  Wend
  FreeSound s
 EndIf
 End
EndIf



If f$="mid" Or f$="mod" Or f$="it" Or f$="s3m"
 Graphics 640,200,32,2
 AppTitle app$
 Print "Playing: "+cmd$
 Print ex$
 SetBuffer BackBuffer()
 s=PlayMusic(cmd$)
 If s<>0
  While KeyDown(1)=0 And MouseDown(1)=0
   Delay 10
  Wend
 EndIf
 End
EndIf


If f$="bmp" Or f$="jpg" Or f$="pcx" Or f$="tga" Or f$="png"
 b=LoadImage(cmd$)
 If b<>0
  w=ImageWidth(b)
  h=ImageHeight(b)
  Graphics w,h,32,2
  AppTitle app$
  SetBuffer BackBuffer()
  b=LoadImage(cmd$)
  DrawBlock b,0,0
  Text 0, 0,cmd$
  Text 0,16,""+ImageWidth(b)+" * "+ImageHeight(b)+" Pixels"
  Text 0,32,ex$
  Flip
  While KeyDown(1)=0 And MouseDown(1)=0
   Delay 10
  Wend
  EndIf
 End
EndIf





WaitKey()
End
