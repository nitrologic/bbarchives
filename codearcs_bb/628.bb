; ID: 628
; Author: Markus Rauch
; Date: 2003-03-18 15:16:12
; Title: copy the Desktop to Blitz Image and show it in 2D or 3D Window
; Description: Desktop to Blitz Image

; Blitz Basic 3D Example to get the desktop in blitz image and window :-)

; You can make a screensaver with it ;-)

; MR 19.03.2003

;--------------------------------------------------------------------------- automatik Blitz window on start 

;your Desktop width x height
Const dwidth# =1600 
Const dheight#=1200

;your Blitz Window
Const swidth# =800
Const sheight#=600

;to find the Window
AppTitle "BlitzSaver"

;--------------------------------------------------------------------------- find Blitz Start Window Handle and HIDE it !

Global bhWnd=FindBlitzWindow("BlitzSaver")

;ShowWindow commands
Const SW_HIDE = 0
Const SW_SHOW = 5

ShowWindow bhwnd,SW_HIDE

;--------------------------------------------------------------------------- copy desktop to bank

Global ddc=DesktopDC()

Global dbank=CreateBank(swidth*sheight*4)

Local x,y,c
Local fx#,fy#

fx=dwidth /swidth
fy=dheight/sheight

For x=0 To swidth-1
 For y=0 To sheight-1
  c=GetPixel(ddc,x*fx,y*fx)
  PokeInt dbank,(x*4)+(swidth*y*4),c
 Next
Next

ReleaseDC 0,ddc

;--------------------------------------------------------------------------- Show Start Blitz Window !

ShowWindow bhwnd,SW_SHOW

;--------------------------------------------------------------------------- Make the real Blitz Window :-)

Graphics3D swidth,sheight,16,1 ;<- note it is 3D but you can also use simple 2D 
SetBuffer BackBuffer()

Global cam=CreateCamera()
CameraClsMode cam,False,True 
MoveEntity cam,0,0,-25

Global cube=CreateCube()
EntityColor cube,0,255,0
ScaleMesh cube,0.5,0.5,0.5
ScaleMesh cube,10,10,10
EntityAlpha cube,0.5

AmbientLight 16,16,16 

Global light=CreateLight()
LightRange light,50
MoveEntity light,-25,25,-50
PointEntity light,cube

;--------------------------------------------------------------------------- Copy bank to image

Global imgDesktop=CreateImage(swidth,sheight)

LockBuffer ImageBuffer(imgDesktop) 
For x=0 To ImageWidth(imgDesktop)-1
 For y=0 To ImageHeight(imgDesktop)-1
  c=PeekInt(dbank,(x*4)+(swidth*y*4)) 
  Color 0,0,c  
  WritePixelFast x,y,ARGB(ColorBlue(),ColorGreen(),ColorRed()),ImageBuffer(imgDesktop) 
 Next
Next
UnlockBuffer ImageBuffer(imgDesktop) 

;######################################################################

FlushKeys
FlushMouse

Local ti#

While Not KeyHit(1)

 ti=MilliSecs()

 DrawBlock imgDesktop,0,0

 TurnEntity cube,-1,1,0
 RenderWorld
 
 ;50 FPS = each frame need 20 ms = (1/50)*1000 = 0.02 sec
 While Abs(MilliSecs()-ti)<20.0  
 Wend

 Flip
Wend
FlushKeys
FlushMouse

End

;######################################################################

Function FindBlitzWindow(title$) 

 ;MR 18.03.2003

 ;API Call

 ;not testet for Blitz2D+ !

 Local hWnd=FindWindow("Blitz Runtime Class",title$) 

 Return hWnd

End Function 

;######################################################################

Function DesktopDC()

 ;MR 18.03.2003

 ;API Call

 Local dc=GetDC(0) 

 Return dc

End Function

;######################################################################

Function ARGB(r,g,b)

 ;Return ((128 * $1000000) Or (r * $10000) Or (g * $100) Or b)
 Return ((r * $10000) Or (g * $100) Or b)

End Function

;###################################################################### USERLIB

;put this once in ..\userlibs\user32.decls
;.lib "user32.dll"
;FindWindow%( class$,Text$ ):"FindWindowA"
;GetDC%(hWnd% ):"GetDC"
;ReleaseDC (hWnd%,hDC%):"ReleaseDC"
;ShowWindow%(hWnd%,nCmdShow%):"ShowWindow"

;put this once in ..\userlibs\Gdi32.decls
;.lib "Gdi32.dll"
;GetPixel%(hdc%,X%,Y%):"GetPixel"

;add on user libs restart blitz ide !

;######################################################################
