; ID: 418
; Author: jfk EO-11110
; Date: 2002-09-06 22:38:29
; Title: 3D Engine
; Description: Simple 3D engine with linear texturemapping (for Blitz 2D)

; Demo: Framework for a simplyfied 3D Engine featuring linear Texturemapping.
; Minor bugs. Supports Quads only. Someone should add Perspective Correction.
; Z-order by Quads and not by Pixel. Goodie: Renderer is using only Integers!
; No "hidden Quads removal" Function implemented yet - depends heavily on usage...
; Send obscene amounts of cash to howaboutpeace@utopia.gov j/k. Enjoy!
; Credits go to Toshi - thx for his Qbasic Texture Mapper (I removed all Floats) and
; to ParanoidPete and to some other PPL who are writing 3D-tutorials all the time.

; PS: Funny: I got "fps: INFINITY" in Fullscreen Mode :) ... Move the Mouse to Zoom in.

 
Graphics 320,240,16,2
SetBuffer BackBuffer()

; -- init texturemapper

texturefile$="texture256.jpg"
If FileType(texturefile$)=1 Then ; if it exists then load it...
 txt=LoadImage(texturefile$); use any 256*256 pixel texture
Else
 txt=CreateImage(256,256)   ; else create one on the fly
 SetBuffer ImageBuffer(txt)
 For i=0 To 255
  Color i,255-i,0
  Line 255-i,0,255,i
  Color 255,255-i,255-i
  Line 0,255-i,i,255
 Next
 SetBuffer BackBuffer()
EndIf
Global imgtxt=ImageBuffer(txt)

Dim Lefttable%(480, 2), Righttable%(480, 2)  ;Scan converter tables (make shure to reserve enough)
Dim Polypoints%(3, 1) ; Array for polygon co-ords, 4 pairs(x,y) co -ords
Global Miny%, Maxy%
Global Pwidth%, Pheight%
 
 
Pwidth = 255 Shl 16   ;original picture width in pixels -1 shl 16
Pheight = 255 Shl 16 ;original picture height in pixels -1 shl 16

; eo init texturemapper

Dim zbuffer(10000) ; maximum of 10k Quads (don't worry, would be too slow anyway :) )
 
; Read in a mesh
Restore building
Read anz
Dim xwww(anz),ywww(anz),zwww(anz)
Dim xw(anz),yw(anz),zw(anz)
 
For i=0 To anz
 Read xwww(i)
 Read ywww(i)
 Read zwww(i)
Next

a=0

alpha=1
beta=1
gamma=1

zoom=-500 ; better alter "mausy#" to zoom in (see below)


; MMMMMMMMMMMMMMMMMMMMMMMMMmmmmmain

While KeyDown(1)=0

 Miny% = 32767  ;set initial smallest y co-ord of polygon After rotation
 Maxy% = 0      ;set initial largest y co-ord of polygon After rotation

 ; calc frames per second
 tt=MilliSecs()
 fps#=1000.0/(tt-ttold)
 fpsadd#=fpsadd#+fps#
 fpscount=fpscount+1
 If fpscount>19 Then
  fpsreal#=fpsadd#/20
  fpsadd#=0
  fpscount=0
 EndIf
 ttold=tt
 ; eo calc fps

 ; Cls
 Color 0,0,0
 Rect 0,0,320,240,1

 a=a+1.0 ; automatic rotation...
 If a>359.9 Then a=0
 alpha=a
 beta=a
 gamma=a+a Mod 360

 mausy#=0.1+(MouseY()/50.0)
 ; the renderer is working with rotated Copies of the original Points to prevent degeneration by Floating Inaccuracy
 For i=0 To anz
  ; rotate pitch, roll and yaw
  xl1#=zwww(i)*Sin(gamma)+xwww(i)*Cos(gamma)
  yl1#=ywww(i)
  zl1#=zwww(i)*Cos(gamma)-xwww(i)*Sin(gamma)

  xl2#=xl1
  yl2#=yl1*Cos(beta)-zl1*Sin(beta)
  zl2#=yl1*Sin(beta)+zl1*Cos(beta)
 
  xl3#=(yl2*Sin(alpha)+xl2*Cos(alpha))
  yl3#=(yl2*Cos(alpha)-xl2*Sin(alpha))
  zl3#=(zl2)


  ; Projecting 3D to 2D
  If yloc# - Zoom <> 0 Then yloc = Int(yl3 ) * 200 / (zl3 - Zoom)
  If xloc# - Zoom <> 0 Then xloc = Int(xl3 ) * 200 / (zl3 - Zoom)

  xw(i)=((mausy#)*xloc) +160
  yw(i)=((mausy#)*yloc) +120
  zw(i)=(zl3+256) ; remember this for Z-sorting
 Next

 ; z-sorting...
 For i=0 To 10000
  zbuffer(i)=-1 ; wipe out old zbuffer info (could be optimized)
 Next
 For i=0 To anz-3 Step 4
  If zw(i)>=0 ; clip Quads behind Camera
   zwmax=zw(i)
   If zwmax<zw(i+1) Then zwmax=zw(i+1)
   If zwmax<zw(i+2) Then zwmax=zw(i+2)
   If zwmax<zw(i+3) Then zwmax=zw(i+3)
   While zbuffer(zwmax)<>-1 And zwmax<10000 ; find next free slot
    zwmax=zwmax+1
   Wend
   zbuffer(zwmax)=i
  EndIf
 Next

 LockBuffer ImageBuffer(txt)
 LockBuffer BackBuffer()

 For i2=10000 To 0 Step -1 ; reading quads in z-order from far to near
  i=zbuffer(i2)

  If i>-1 And i< anz-2 ; if it isn't -1 then it's a Quad Point 1 ID
   ; Mapping...
   GetPolygonPoints(i)
   FindSmallLargeY()
   ; Send polygon points To the scan converter
   X1% = Polypoints%(0, 0)
   Y1% = Polypoints%(0, 1)
   X2% = Polypoints%(1, 0)
   Y2% = Polypoints%(1, 1)
   ScanConvert(X1%, Y1%, X2%, Y2%, 1)     ;scan top of picture
   X1% = Polypoints%(1, 0)
   Y1% = Polypoints%(1, 1)
   X2% = Polypoints%(2, 0)
   Y2% = Polypoints%(2, 1)
   ScanConvert(X1%, Y1%, X2%, Y2%, 2)   ;scan Right of picture
   X1% = Polypoints%(2, 0)
   Y1% = Polypoints%(2, 1)
   X2% = Polypoints%(3, 0)
   Y2% = Polypoints%(3, 1)
   ScanConvert(X1%, Y1%, X2%, Y2%, 3)  ;scan bottom of picture
   X1% = Polypoints%(3, 0)
   Y1% = Polypoints%(3, 1)
   X2% = Polypoints%(0, 0)
   Y2% = Polypoints%(0, 1)
   ScanConvert(X1%, Y1%, X2%, Y2%, 4)    ;scan Left of picture

   TextureMap()
   ; eo mapping
  EndIf
 Next

 UnlockBuffer BackBuffer()
 UnlockBuffer ImageBuffer(txt)

 Color 255,255,255
 Text 0,0,"fps "+fpsreal#

 Flip 0
Wend

End





 
; --- texture mapping functions

Function GetPolygonPoints(ilocal%) ; initially read in a rectangle
 For Count% = 0 To 3
  Polypoints%(Count%, 0) = xw(ilocal%+Count%)
  Polypoints%(Count%, 1) = yw(ilocal%+Count%)
 Next
End Function

Function FindSmallLargeY()
 For Count% = 0 To 3
  Ycoord% = Polypoints%(Count%, 1)

  If Ycoord% < Miny% Then       ; is this the New lowest y co-ord?
   Miny% = Ycoord%             ; Yes...
  End If

  If Ycoord% > Maxy% Then       ; is this the New highest y co-ord?
   Maxy% = Ycoord%             ; Yes...
  End If
 Next
End Function

Function ScanConvert (X1%, Y1%, X2%, Y2%, Pside)

 ; This procedure takes as defined by X1%,Y1%,x2,Y2%.
 ; It also takes a var telling it which side of the picture we are
 ; mapping.  The var are 1,2,3,4 for top,Right,bottom,Left. This routine decides
 ; which ;side; of the polygon the Line is on, And Then calls the
 ; appropriate routine.

 If Y2% < Y1% Then
  temp%=X1% : X1%=X2% : X2%=temp%
  temp%=Y1% : Y1%=Y2% : Y2%=temp%
  Lineheight% = (Y2% - Y1%)
  ScanLeftSide(X1%, X2%, Y1%, Lineheight%, Pside)
 Else
  Lineheight% = (Y2% - Y1%)
  ScanRightSide(X1%, X2%, Y1%, Lineheight%, Pside)
 End If
End Function

Function ScanLeftSide (X1%, X2%, Ytop%, Lineheight%, Pside)

 ; This procedure calculates the x points for the Left side of the
 ; polygon. It also calculates the x,y co-ords of the picture For the Left
 ; side of the polygon.

 Lineheight% = Lineheight% + 1       ; prevent divide by zero
 Xadd = (X2% - X1%) Shl 16
 Xadd = Xadd / Lineheight%

 
 If Pside = 1 Then
  Px = Pwidth% - 1
  Py = 0
  Pxadd = -Pwidth%  / Lineheight%
  Pyadd = 0
 End If
 If Pside = 2 Then
  Px = Pwidth%
  Py = Pheight%
  Pxadd = 0
  Pyadd = -Pheight%  / Lineheight%
 End If
 If Pside = 3 Then
  Px = 0
  Py = Pheight%
  Pxadd = Pwidth%  / Lineheight%
  Pyadd = 0
 End If
 If Pside = 4 Then
  Px = 0
  Py = 0
  Pxadd = 0
  Pyadd = Pheight%  / Lineheight%
 End If

 x = X1% Shl 16
 For y% = 0 To Lineheight%
  Ytopy%=Ytop%+y%
  If Ytopy%<0 Then Ytopy%=0
  Lefttable(Ytopy%, 0) = x Sar 16    ;polygon x
  Lefttable(Ytopy%, 1) = Px          ;picture x
  Lefttable(Ytopy%, 2) = Py          ;picture y
  x = x + Xadd                       ;Next polygon x
  Px = Px + Pxadd                    ;Next picture x
  Py = Py + Pyadd                    ;Next picture y
 Next
End Function

Function ScanRightSide (X1%, X2%, Ytop%, Lineheight%, Pside)

 ; This procedure calculates the x points For the Right side of the ;
 ; polygon. It also calculates the x,y co-ords of the picture For the
 ; Right side of the polygon.

 Lineheight% = Lineheight% + 1    ; No divide by zero
 Xadd = (X2% - X1%) Shl 16
 Xadd = Xadd / Lineheight%


 If Pside = 1 Then
  Px = 0
  Py = 0
  Pxadd = Pwidth% / Lineheight%
  Pyadd = 0
 End If
 If Pside = 2 Then
  Px = Pwidth%
  Py = 0
  Pxadd = 0
  Pyadd = Pheight% / Lineheight%
 End If
 If Pside = 3 Then
  Px = Pwidth%
  Py = Pheight%
  Pxadd = -Pwidth% / Lineheight%
  Pyadd = 0
 End If
 If Pside = 4 Then
  Px = 0
  Py = Pheight%
  Pxadd = 0
  Pyadd = -Pheight% / Lineheight%
 End If

 x = X1% Shl 16
 For y% = 0 To Lineheight%
  Ytopy%=Ytop%+y%
  If Ytopy%<0 Then Ytopy%=0
  Righttable(Ytopy%, 0) = x Sar 16   ;polygon x
  Righttable(Ytopy%, 1) = Px         ;picture x
  Righttable(Ytopy%, 2) = Py         ;picture y
  x = x + Xadd                       ;Next polygon x
  Px = Px + Pxadd                    ;Next picture x
  Py = Py + Pyadd                    ;Next picture y
 Next 
End Function





Function TextureMap()
; This is the actual mapping routine. Only linerar mapping, no perspective correction :/
; It takes the co-ords that have been calculated by the scan converter
; And 'traces' across the original picture in between them looking at
; the pixel Color And Then plotting a pixel in that Color in the
; current position within the polygon.

; If you were able to optimze this in speed or accuracy then please let us know.

 For y% = Miny% To Maxy%
  If y>0 And y<=239 ; clipping y
   Polyx1% = Lefttable((y%), 0)    ;get Left polygon x
   Px1 = Lefttable(y%, 1)          ;get Left picture x
   Py1 = Lefttable(y%, 2)          ;get Left picture y  

   Polyx2% = Righttable((y%), 0)   ;get Right polygon x
   Px2 = Righttable(y%, 1)         ;get Right picture x
   Py2 = Righttable(y%, 2)         ;get Right picture y
   Linewidth% = Polyx2% - Polyx1%  ;what is the width of this polygon Line
   Linewidth%=Linewidth% Or 1      ;prevent divide by zero
   Pxadd = ((Px2 - Px1)) / Linewidth% ;squash picture xdist into poly xdist
   Pyadd = ((Py2 - Py1)) / Linewidth% ;squash picture ydist into poly ydist

   For x% = Polyx1% To Polyx2%
     If x>0 And x<=319 ; clipping x
      Col%=ReadPixelFast((Px1 Shr 16),(Py1 Shr 16),imgtxt)
      WritePixelFast x%,y%,Col%
     EndIf
     Px1 = Px1 + Pxadd        ; move x picture co-ord
     Py1 = Py1 + Pyadd        ; move y picture co-ord
   Next
  EndIf
 Next
End Function

; eo  texture mapping functions


.building
; a simple cube mesh: 24 Points (unshared Vertices) and 6 Quads (must be clockwise):
; (Of course you could implement a Loading Function as well)
Data 23  ; number of pts -1
;    x   y   z

Data -100,-100,-100
Data -100,100,-100
Data -100,100,100
Data -100,-100,100

Data -100,-100,-100
Data  -100,-100,100
Data  100,-100, 100
Data 100,-100, -100

Data -100,100,-100
Data  100,100,-100
Data  100,100, 100
Data -100,100, 100

Data 100,-100,-100
Data 100,-100,100
Data 100,100,100
Data 100,100,-100
 
Data -100,100,100
Data 100,100,100
Data 100,-100,100
Data -100,-100,100

Data -100,100,-100
Data -100,-100,-100
Data 100,-100,-100
Data 100,100,-100
