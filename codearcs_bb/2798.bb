; ID: 2798
; Author: ThePict
; Date: 2010-12-17 09:38:33
; Title: Widescreen
; Description: 16:9 dimensions calculator

Graphics 800,600,16,2
ClsColor 0,0,255
Color 255,255,0
Oval 100,100,10,10,1
Oval 700,100,10,10,1
Oval 100,437,10,10,1
Oval 700,437,10,10,1
Rect 105,105,600,337,1
Rect 105,100,600,5,1
Rect 100,105,5,337,1
Rect 105,442,600,5,1
Rect 705,105,5,337,1
d#=Input("Enter the stated (diagonal) size of your widescreen TV.")
x#=d#*(Cos(29.35775354279))
y#=d#*(Sin(29.35775354279))
diag$=Left$(Str$(d#),Instr(Str$(d#),".")+1)
width$=Left$(Str$(x#),Instr(Str$(x#),".")+1)
height$=Left$(Str$(y#),Instr(Str$(y#),".")+1)
Color 255,0,0
Line 103,103,707,444
Line 103,103,110,101
Line 103,103,106,110
Line 707,444,700,446
Line 707,444,704,437
Line 100,460,710,460 
Line 100,460,108,457
Line 100,460,108,463
Line 710,460,702,457
Line 710,460,702,463
Line 750,100,750,447
Line 750,100,747,108
Line 750,100,753,108
Line 750,447,747,439
Line 750,447,753,439
Color 255,255,0
Rect 350,260,100,20,1
Color 0,0,0
Rect 350,458,100,4,1
Rect 748,263,4,20,1
Color 0,0,255
Text 400,270,diag$,1,1
Text 400,460,width$,1,1
Text 750,273,height$,1,1
FlushKeys
WaitKey()
End
