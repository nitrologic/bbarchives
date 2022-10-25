; ID: 1477
; Author: ImaginaryHuman
; Date: 2005-10-07 00:27:02
; Title: Draw Hollow Ellipse Pixel by Pixel with Integer Math
; Description: Draws a hollow/unfilled ellipse one pixel at a time with integer math

'Midpoint ellipse algorithm

Strict
Graphics 640,480,0

Local xCenter:Int=320
Local yCenter:Int=240
Local Rx,Ry:Int
Local p,px,py,x,y:Int
Local Rx2,Ry2,twoRx2,twoRy2:Int
Local pFloat:Float
Repeat
        Cls
        If MouseDown(1)
                xCenter=MouseX()
                yCenter=MouseY()
        EndIf
        Rx=Abs(xCenter-MouseX())
        Ry=Abs(yCenter-MouseY())
        DrawText String(Rx)+" x "+String(Ry),20,20
        Rx2=Rx*Rx
        Ry2=Ry*Ry
        twoRx2=Rx2 Shl 1
        twoRy2=Ry2 Shl 1
        'Region 1
        x=0
        y=Ry
        Plot xCenter+x,yCenter+y
        Plot xCenter-x,yCenter+y
        Plot xCenter+x,yCenter-y
        Plot xCenter-x,yCenter-y
        pFloat=(Ry2-(Rx2*Ry))+(0.25*Rx2)
        p=Int(pFloat)
        If pFloat Mod 1.0>=0.5 Then p:+1
        px=0
        py=twoRx2*y
        While px<py
                x:+1
                px:+twoRy2
                If p>=0
                        y:-1
                        py:-twoRx2
                EndIf
                If p<0 Then p:+Ry2+px Else p:+Ry2+px-py
                Plot xCenter+x,yCenter+y
                Plot xCenter-x,yCenter+y
                Plot xCenter+x,yCenter-y
                Plot xCenter-x,yCenter-y
        Wend
        'Region 2
        pFloat=(Ry2*(x+0.5)*(x+0.5))+(Rx2*(y-1.0)*(y-1.0))-(Rx2*(Float(Ry2)))
        p=Int(pFloat)
        If pFloat Mod 1.0>=0.5 Then p:+1
        While y>0
                y:-1
                py:-twoRx2
                If p<=0
                        x:+1
                        px:+twoRy2
                EndIf
                If p>0 Then p:+Rx2-py Else p:+Rx2-py+px
                Plot xCenter+x,yCenter+y
                Plot xCenter-x,yCenter+y
                Plot xCenter+x,yCenter-y
                Plot xCenter-x,yCenter-y
        Wend
        Flip
Until KeyHit(KEY_ESCAPE)
End
