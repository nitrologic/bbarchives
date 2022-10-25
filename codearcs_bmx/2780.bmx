; ID: 2780
; Author: jankupila
; Date: 2010-10-17 07:07:19
; Title: Nice effect
; Description: Nice effect

Graphics 800,600,1
Repeat
    aika=aika+1
    For x=1 To 60
        For y= 1 To 50
            SetColor Cos(aika+y*2.5+x*2.5)*127+127,Sin(aika+y*5)*127+127,Sin(aika+x*5)*127+127
            DrawLine x*10+Sin(aika+y*5)*20,y*10+Sin(aika+x*5)*20,x*10+Sin(aika+y*5)*20+Sin(aika+x*5)*10,y*10+Sin(aika+x*5)*20+Sin(aika+y*5)*10
        Next 
    Next 
    Flip;Cls
If KeyHit(key_escape) Then End
Forever
