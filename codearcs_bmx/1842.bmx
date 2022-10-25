; ID: 1842
; Author: agent4125
; Date: 2006-10-19 03:43:37
; Title: TileImage2
; Description: TileImage with Scale and Rotation

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




'========================  TEST  ========================


Graphics 640, 480

Test()

Function Test ()

    Local fileName$ = "test_scale.png"

    Local imgTile:Timage = LoadImage(fileName$)
    If imgTile = Null Then RuntimeError("Can't load image: " + fileName)

    Local imageSize% = Max(ImageHeight(imgTile), ImageWidth(imgTile))
    Local baseScale# = 50.0 / imageSize%
    Local wiggleScale# = baseScale# / 4.0

    Local timer:TTimer = CreateTimer(60)

    Local rot# = 0.0
    Local x# = 0.0
    Local y# = 0.0
    Local frame# = 0.0

    While Not KeyHit(KEY_ESCAPE)

        WaitTimer(timer)

        ' zoom in/out
        frame :+ 1
        Local scale# = baseScale# + Sin(frame) * wiggleScale#
        SetScale scale#, scale#

        ' rotation
        rot# :+ 1
        SetRotation rot#

        ' scroll
        x# :- 2.0
        y# :- 0.5

        ' render
        Cls
        TileImage2(imgTile, x#, y#)
        Flip

    Wend

EndFunction
