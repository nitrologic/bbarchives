; ID: 2591
; Author: rolow
; Date: 2009-09-28 05:16:11
; Title: Rounding Numbers
; Description: CEIL, FLOOR and INT

; This example modified by RoLoW in September 2009
; is based upon the original Ceil / Floor / Int
; example from Blitz3D for three kinds of rounding.
;
; Resolutions and Divisor:
;      640 x 480 use with 32
;      800 x 600 use with 40
;     1024 x 768 use with 51
;
Global ScreenWide = 800
Global ScreenHigh = 600
Global Divisor = 40
;
Global Origin_X = ScreenWide/2
Global Origin_Y = ScreenHigh/2
;
Graphics ScreenWide, ScreenHigh 
;
SetBuffer BackBuffer() 
Origin Origin_X,Origin_Y
;
MoveMouse Origin_X,Origin_Y : HidePointer 
;
While Not GetKey();press any key
    ;
    Cls 
    ;
    Color 255,255,255
    Text -Origin_X,-Origin_Y,   "         Blitz3D ROUNDING Example"
    Text -Origin_X,-Origin_Y+20,"Demonstrates Functions CEIL, FLOOR and INT"
    ;
    Text -Origin_X,-Origin_Y+60, " CEIL rounds up to the next whole number"
    Text -Origin_X,-Origin_Y+80, "FLOOR rounds down to the next whole number"
    Text -Origin_X,-Origin_Y+100,"  INT rounds to the closest whole number"
    ;
    Text -Origin_X,-Origin_Y+150,"Move mouse around to check different values"
    Text -Origin_X,-Origin_Y+170,"of X and Y on the coordinate system."
    Text -Origin_X,-Origin_Y+210,"Press any key to exit."
    ;
    DrawNumberLineX
    DrawNumberLineY
    ;
    my = MouseY() - Origin_Y
    mx = MouseX() - Origin_X
    ;
    Color 255,100,100
    Line -Origin_X, my, Origin_X-1, my 
    Line mx,-Origin_Y,mx,Origin_Y
    ;
    x# = Float( mx ) / Divisor 
    Color 100,100,0
    Text -225, Divisor*2, " X = " + x 
    Text -225, Divisor*2+20, " Ceil( x ) = " + Ceil( x ) 
    Text -225, Divisor*2+40, " Floor( x ) = " + Floor( x ) 
    Text -225, Divisor*2+60, " Int( x ) = " + Int( x ) 
    ;
    y# = Float( -my ) / Divisor 
    Color 100,100,0
    Text Divisor*2, Divisor*2,    " Y = " + y 
    Text Divisor*2, Divisor*2+20, " Ceil( y ) = " + Ceil( y ) 
    Text Divisor*2, Divisor*2+40, " Floor( y ) = " + Floor( y ) 
    Text Divisor*2, Divisor*2+60, " Int( y ) = " + Int( y ) 
    ;
    Flip 
    ;
Wend 
;
End 
;
Function DrawNumberLineX( ) ; horizontal line with numeric labels 
    ;
    Color 200,200,200 
    Line  -Origin_X, -1, Origin_X-1 , -1
    Line  -Origin_X,  0, Origin_X-1 ,  0
    Line  -Origin_X,  1, Origin_X-1 ,  1
    ;
    For n = -9 To 9 
        xn = -Divisor * n 
        Line xn, -4, xn, 4 
        Color 255,255,255 
        If n <> 0 Then Text xn-10, -20, RSet( -n, 2 )
        If Abs(n) = 9 Then Text xn, 20, "X"
    Next 
    ;
End Function 
;
Function DrawNumberLineY( ) ; vertical line with numeric labels 
    ;
    Color 200,200,200 
    Line -1, -Origin_Y, -1, Origin_Y-1 
    Line  0, -Origin_Y,  0, Origin_Y-1 
    Line  1, -Origin_Y,  1, Origin_Y-1 
    ;
    For n = -7 To 7 
        yn = -Divisor * n 
        Line -4, yn, 4, yn 
        Color 255,255,255 
        If n <> 0 Then Text -30, yn - 6, RSet( n, 2 )
        If Abs(n) = 7 Then Text 20, yn-6, "Y"
    Next 
    ;
End Function
