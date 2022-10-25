; ID: 1476
; Author: ImaginaryHuman
; Date: 2005-10-07 00:24:47
; Title: Draw Hollow Circle Pixel by Pixel with Integer Math
; Description: Draws a hollow/unfilled circle plotting each pixel and using only integer math

'Midpoint Circle algorithm

Strict
Graphics 640,480,0

Local xCenter:Int=320
Local yCenter:Int=240
Local radius:Int
Local p,x,y:Int
Repeat
        Cls
        If MouseDown(1)
                xCenter=MouseX()
                yCenter=MouseY()
        EndIf
        radius=Abs(xCenter-MouseX())
        x=0
        y=radius
        Plot xCenter+x,yCenter+y
        Plot xCenter-x,yCenter+y
        Plot xCenter+x,yCenter-y
        Plot xCenter-x,yCenter-y
        Plot xCenter+y,yCenter+x
        Plot xCenter-y,yCenter+x
        Plot xCenter+y,yCenter-x
        Plot xCenter-y,yCenter-x
        p=1-radius
        While x<y
                If p<0
                        x:+1
                Else
                        x:+1
                        y:-1
                EndIf
                If p<0
                        p=p+(x Shl 1)+1
                Else
                        p=p+((x-y) Shl 1)+1
                EndIf
                Plot xCenter+x,yCenter+y
                Plot xCenter-x,yCenter+y
                Plot xCenter+x,yCenter-y
                Plot xCenter-x,yCenter-y
                Plot xCenter+y,yCenter+x
                Plot xCenter-y,yCenter+x
                Plot xCenter+y,yCenter-x
                Plot xCenter-y,yCenter-x
        Wend
        Flip
Until KeyHit(KEY_ESCAPE)
End
