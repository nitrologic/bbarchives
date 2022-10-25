; ID: 1562
; Author: OJay
; Date: 2005-12-13 16:39:49
; Title: 3D fileexplorer
; Description: A funny fileexplorer in 3D

; 3D File-Explorer 

Graphics3D 640,480,16,2 
SetBuffer BackBuffer() 

Global gw#=GraphicsWidth() 
Global gh#=GraphicsHeight() 
MoveMouse gw/2,gh/2 

cam=CreateCamera() 
Global uni=CreateSphere(7,cam) 
MoveEntity cam,0,2,-6 
li=CreateLight() 
RotateEntity li,80,30,30 
ScaleEntity uni,100,100,100 
FlipMesh uni 
EntityOrder uni,1 
texname$="space.jpg" 
If FileType(texname$)=0 
unitex=CreateTexture(128,128) 
SetBuffer TextureBuffer(unitex) 
For i=0 To 10000 
  Color Rand(255), Rand(255), Rand(255) 
  Plot  Rand(128), Rand(128) 
Next 
SetBuffer BackBuffer() 
Else 
unitex=LoadTexture(texname$) 
EndIf 
EntityTexture uni,unitex 

Global fls#=25000 ;max 25000 Files in one Folder 
Dim fb_icon(fls),fb_name$(fls),fb_type(fls),fb_tex(fls),fb_sel(fls) 

Global rheight#=4 ; x space between icons 
Global rwidth#=5  ; y space between icons 
Global fperline#=3 ; number of icon columns 

Global iconw#=2.0 ; icon scaling 
Global iconh#=1.0 
Global icond#=.5 
Global stexx#=1.0 ; texure scaling 
Global stexy#=2.0 
ClearTextureFilters 

font=LoadFont("Tahoma",17) 
SetFont font 


mx100#=(gw/2)*.9 
my100#=(gh/2)*.9 
Global camxi#=5 
Global camyi#=0 
Global camzi#=-9.2 
Global camx#=camxi# 
Global camy#=camyi# 
Global camz#=camzi# 

RefreshDir() 

; -------------------------------------------MAIN------------------------------------------- 
While KeyDown(1)=0 
mx#=MouseX() 
my#=MouseY() 

If my<my100 
  If camy<1 
   sty#=((my100-my)/150)^2 
   camy=camy+sty 
   If camy>1 Then camy=1 
  EndIf 
EndIf 
If my>gh-my100 
  If camy>(0-(Floor(fls/fperline)*rheight)) 
   sty#=((my100-(gh-my))/150)^2 
   camy=camy-sty 
   If camy<(0-(Floor(fls/fperline)*rheight)) Then camy=(0-(Floor(fls/fperline)*rheight)) 
  EndIf 
EndIf 

If mx>gw-mx100 
  If camx<(fperline)*rwidth 
   stx#=(mx100-(gw-mx))/500 
   camx=camx+stx 
   If camx>(fperline)*rwidth Then camx=(fperline)*rwidth 
  EndIf 
EndIf 
If mx<mx100 
  If camx>-rwidth 
   stx#=(mx100-mx)/500 
   camx=camx-stx 
   If camx<-rwidth Then camx=-rwidth 
  EndIf 
EndIf 

If MouseHit(1) 
  oldmhitt=mhitt 
  mhitt=MilliSecs() 
  If (mhitt-oldmhitt)<300 Then 
   ; -----------------------------is dooubleclick! 
   p=CameraPick(cam,mx,my) 
   If p<>0 
    picked_i=-1 
    For i=0 To fls 
     If fb_icon(i)=p 
      picked_i=i 
      Exit 
     EndIf 
    Next 
    If picked_i>-1 
    For roro=0 To 1000 
     dx#=(EntityX(fb_icon(picked_i),1)-EntityX(cam))/10.0;-EntityX(cam) 
     dy#=(EntityY(fb_icon(picked_i),1)-EntityY(cam))/10.0;-EntityY(cam) 
     dz#=(EntityZ(fb_icon(picked_i),1)-EntityZ(cam))/10.0;-EntityZ(cam) 
     PositionEntity cam,EntityX(cam,1)+dx,EntityY(cam,1)+dy,EntityZ(cam,1)+dz,1 
     TurnEntity uni,.1,.1,.1 
     RenderWorld() 
     PathInfo() 
     Flip 
     If dx<.1 And dy<.1 And dz<.1 Then Exit 
    Next 
    placebo=CopyEntity(fb_icon(picked_i)) 
;    FreeEntity fb_icon(picked_i) 
    If fb_type(picked_i)=2 
     ChangeDir(fb_name$(picked_i)) 
    EndIf 
    EntityParent placebo,cam 
    camx#=camxi# 
    camy#=camyi# 
    camz#=camzi# 
    MoveMouse gw/2,gh/2 
    PositionEntity cam,camx,camy,camz,1 
    
    If Instr(fb_name$(picked_i),".")<>0 And fb_name$(picked_i)<>".." And fb_type(picked_i)=1 
     ; check Media Type and probably do something (some examples) 
     ext$=Right$(fb_name$(picked_i), Len(fb_name$(picked_i))-Instr(fb_name$(picked_i),".")) 
;Print 
;Color 255,0,0 
;     Print ext$ 
;     WaitKey() 
     Select Upper$(ext$) 
      Case "TXT" 
       ExecFile "notepad.exe "+fb_name$(picked_i) 
      Case "EXE" 
       ExecFile fb_name$(picked_i) 
      Case "HTM",".HTML" 
       ExecFile fb_name$(picked_i) 
      Case "BB" 
       ExecFile fb_name$(picked_i) 
     End Select 
    EndIf 

    RemoveAll() 
    RefreshDir() 


    For al#=1.0 To 0.0 Step -.05 
     EntityAlpha placebo,al# 
     TurnEntity uni,.1,.1,.1 
     RenderWorld() 
     PathInfo() 
     Flip 
    Next 
    FreeEntity placebo 
    EndIf 
   EndIf    
   Goto done 
  Else 
   ; -----------------------------is single click 
   p=CameraPick(cam,mx,my) 
   If p<>0 
    picked_i=-1 
    For i=0 To fls 
     If fb_icon(i)=p 
      picked_i=i 
      Exit 
     EndIf 
    Next 
    If picked_i>-1 
     For i=0 To fls 
      If fb_sel(i)<>0 
       If i<>picked_i 
        fb_sel(i)=0 
        EntityColor fb_icon(i),255,255,255 
       EndIf 
      EndIf 
     Next 
     fb_sel(picked_i)=fb_sel(picked_i)Xor 1 
     If fb_sel(picked_i)=1 
      EntityColor fb_icon(picked_i),75,75,255 
     Else 
      EntityColor fb_icon(picked_i),255,255,255 
     EndIf 
    EndIf 
   EndIf 
  EndIf 
  .done 
EndIf 


If KeyDown(200) 
  camz=camz+.1 
EndIf 
If KeyDown(208) 
  camz=camz-.1 
EndIf 
PositionEntity cam,camx,camy,camz,1 
TurnEntity uni,.1,.1,.1 
RenderWorld() 
PathInfo() 
Flip 
Wend 
;--------------------------------------------eo main ------------------------------------ 

End 

Function RefreshDir() 


; Define what folder to start with ... 
folder$=CurrentDir$() 
myDir=ReadDir(folder$) 
fls=0 
Repeat 
fb_name$(fls)=NextFile$(myDir) 
If fb_name$(fls)<>"." 
  If fb_name$(fls)="" Then Exit 
   folder2$=folder$ 
   If Right$(folder2$,1)<>"\" 
    folder2$=folder2$+"\" 
   EndIf 
   If FileType(folder2$+fb_name$(fls)) = 2 Then 
    fb_type(fls)=2 
   Else 
    fb_type(fls)=1 
   End If 
  fls=fls+1 
EndIf 
Forever 
CloseDir myDir 
fls=fls-1 





;fls#=Rand(10,100) 
;Dim fb_icon(fls),fb_name$(fls),fb_type(fls),fb_tex(fls),fb_sel(fls) 

For i=0 To fls 
fb_icon(i)=CreateCube() 
y=Floor(i/fperline) 
x=i-(y*fperline) 
PositionEntity fb_icon(i),x*rwidth,-(y*rheight),0 
fb_tex(i)=CreateTexture(128,128) 
SetBuffer TextureBuffer(fb_tex(i)) 
If fb_type(i)=2 
  Color 127,127,0 
  Rect 0,0,256,256,1 
Else 
  Color 127,127,127 
  Rect 0,0,256,256,1 
EndIf 
If StringWidth(fb_name$(i))<=126 
Color 255,255,255 
Text 64,0,fb_name$(i),1,0 
Color 0,0,0 
Text 65,1,fb_name$(i),1,0 
Else 
lin$="" 
wchar=1 
ycount=0 
While wchar<=Len(fb_name$(i)) 
  While StringWidth(lin$)<=122 And wchar<=Len(fb_name$(i)) 
   lin$=lin$+Mid$(fb_name$(i),wchar,1) 
   wchar=wchar+1 
  Wend 
  If wchar<Len(fb_name$(i)) 
   lin$=Left$(lin$,Len(lin$)-1) 
   wchar=wchar-1 
  EndIf 
  Color 255,255,255 
  Text 64,0+ycount*StringHeight(fb_name$(i)),lin$,1,0 
  Color 0,0,0 
  Text 65,1+ycount*StringHeight(fb_name$(i)),lin$,1,0 
  lin$="" 
  ycount=ycount+1 
Wend 
EndIf 
SetBuffer BackBuffer() 
ScaleTexture fb_tex(i),stexx,stexy 
EntityTexture fb_icon(i),fb_tex(i) 
EntityPickMode fb_icon(i),2 
ScaleEntity fb_icon(i),iconw,iconh,icond 
EntityFX fb_icon(i),16 
fb_sel(i)=0 
Next 

End Function 

Function RemoveAll() 
For i=0 To fls 
  FreeEntity fb_icon(i) 
  FreeTexture fb_tex(i) 
Next 
End Function 

Function PathInfo() 
Color 127,127,127 
Text 0,0,CurrentDir$() 
Color 255,255,255 
Text 1,1,CurrentDir$() 
End Function
