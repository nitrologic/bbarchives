; ID: 3087
; Author: zoqfotpik
; Date: 2013-11-16 09:27:57
; Title: Image Recursion
; Description: Image Recursion Demo

' recur
Graphics 640,480
Global textstring$="So here's this.  Just a little something... it starts out slow and picks up.  I'm sure I'm not the only person to come up with this.  The basic idea is video recursion, like you get if you point a video camera at the video display for that camera-- forms appear and twist and turn into whirlpools of color.  The concept was described in Godel, Escher, Bach by Douglas Hofstader..."
windowgrab=CreateImage(640,480)
timer = CreateTimer(30)
DrawRect 213,213,213,213
GrabImage windowgrab, 0,0
While KeyDown(key_escape)=0 And ticks < 2170
SetRotation 0
tileimage2 windowgrab,0,0
ticks = ticks + 1
SetAlpha 255
SetBlend alphablend
SetRotation Cos(ticks)*ticks/10
SetScale (1-Sin(ticks)+.1)*.5,(1-Cos(ticks)+.1)*.5
tileimage2 windowgrab,Sin(ticks)*640,Cos(ticks)*640
SetScale .5,.5
SetColor 200,200,200
SetRotation Sin(ticks)*ticks/10
tileimage2 windowgrab,0,0
SetScale 1,1
SetColor 255,255,255
SetHandle 160,160
SetColor 100,0,100
SetRotation 0
SetColor 0,0,0
SetScale 2,2
SetHandle 0,0
SetScale 8,8
SetColor 0,0,0
DrawText textstring$, 220-ticks*15,220
SetColor Rand(128)+128,Rand(128)+128,Rand(128)+128
DrawText textstring$, 213-ticks*15,213

SetScale 1,1
Print ticks
WaitTimer timer
Delay 10
GrabImage windowgrab, 0,0
Flip
Wend

Function TileImage2 (image:TImage, x:Float=0# ,y:Float=0#, frame:Int=0)
    Local scale_x#, scale_y#
    GetScale(scale_x#, scale_y#)
    Local viewport_x%, viewport_y%, viewport_w%, viewport_h%
    GetViewport(viewport_x, viewport_y, viewport_w, viewport_h)
    Local origin_x#, origin_y#
    GetOrigin(origin_x, origin_y)
    Local handle_X#, handle_y#
    GetHandle(handle_X#, handle_y#)
    Local image_h# = ImageHeight(image)
    Local image_w# = ImageWidth(image)
    Local w#=image_w * Abs(scale_x#)
    Local h#=image_h * Abs(scale_y#)
    Local ox#=viewport_x-w+1
    Local oy#=viewport_y-h+1
    origin_X = origin_X Mod w
    origin_Y = origin_Y Mod h
    Local px#=x+origin_x - handle_x
    Local py#=y+origin_y - handle_y
    Local fx#=px-Floor(px)
    Local fy#=py-Floor(py)
    Local tx#=Floor(px)-ox
    Local ty#=Floor(py)-oy
    If tx>=0 tx=tx Mod w + ox Else tx = w - -tx Mod w + ox
    If ty>=0 ty=ty Mod h + oy Else ty = h - -ty Mod h + oy
    Local vr#= viewport_x + viewport_w, vb# = viewport_y + viewport_h
    SetOrigin 0,0
    Local iy#=ty
    While iy<vb + h ' add image height to fill lower gap
        Local ix#=tx
        While ix<vr + w ' add image width to fill right gap
            DrawImage(image, ix+fx,iy+fy, frame)
            ix=ix+w
        Wend
        iy=iy+h
    Wend
    SetOrigin origin_x, origin_y
End Function
