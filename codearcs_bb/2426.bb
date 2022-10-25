; ID: 2426
; Author: Andy
; Date: 2009-03-05 19:56:43
; Title: Estimating area
; Description: Estimates total visible area from the direction of view

frontarea#=50.0
Toparea#=50.0
sidearea#=50.0
totalarea#=0.0

yaw#=45.0
pitch#=0.0
roll#=0.0

totalarea#=Abs(Sin(yaw#)*sidearea#)+Abs(Cos(yaw#)*(Abs(Sin(pitch#)*toparea#)+Abs(Cos(pitch#)*frontarea#)))

Print "Total: "+totalarea#

WaitKey()
