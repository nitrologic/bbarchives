; ID: 825
; Author: Tyler
; Date: 2003-11-11 21:24:17
; Title: Cubic Spline Interpolation 3d Update
; Description: 3d sampling of Chroma's Interpolation Code

;-3d Cubic Spline Interpolation-;

;[Updated] - Recursive Math Added For fastest Spline Speed Ever

;[Updated] - 3rd dimension added and having a little fun with the Y axis of the beginning Y value.

;-by Chroma (edited by TogaMario)
;-based on a tutorial at www.gamedev.net

;The object starts at position S and is predicted ahead one second to be at 1.
;The object is then predicted to E and then time reversed one second to 2.
;The spline is then plotted from S to E based on the predicted points of 1 and 2
;and the starting And ending velocities. The object is then moved along the
;spline Until the Next packet arrives and then process starts over.

;S = starting point - time=0
;1 = predicted position after 1 sec
;2 = predicted position reversed from E after 1 sec
;E = ending point

AppTitle "Cubic Spline Interpolation"

Graphics3D 800,600,16,2
SetBuffer BackBuffer()

i = 50

;Main Loop
While Not KeyHit(1)
Cls

i = i - 1
;Cubic Spline Interpolation (I love those words!)
cspline(50,i,50,100,100,100,100,100,100,300,75,-20,-10,0,0)

Flip
Wend
End

;-=-=-=-=-=-=-=-=-=-=-=-=-=-=;
;=Cubic Spline Interpolation=;
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=;
Function cspline(Xold,Yold,Zold,Xvelold,Yvelold,Zvelold,XNew=0,YNew=0,ZNew=0,Xvelnew,Yvelnew,Zvelnew,XAccelNew,YAccelNew,ZAcccelNew,Time=3)

;Init Local Variables
Local x,y,z,x0,y0,z0,x1,y1,z1,x2,y2,z2,x3,y3,z3
Local a,b,c,d,e,f,g,h,i,j,k,l,t#

;Coord#1 is the current object's X,Y (or X,Z if using 3D)
Color 0,255,0
X0=Xold : Y0=Yold : Z0=Zold
Text X0-4,Y0-16,"S"
Rect X0,Y0,3,3,1

;Calc Coord#2
Color 255,255,0
X1 = X0 + XVelOld
Y1 = Y0 + YVelOld
Z1 = Z0 + ZVelOld
Text X1-4,Y1-16,"1"
Rect X1,Y1,3,3,1

;Calc Coord#3 (must calc after Coord#4)
Color 200,100,0
X2=XNew + XVelNew * Time + .5 * XAccelNew * Time^2
Y2=YNew + YVelNew * Time + .5 * YAccelNew * Time^2
Z2=ZNew + ZVelNew * Time + .5 * ZAccelNew * Time^2
Text X2-4,Y2-16,"2"
Rect X2,Y2,3,3,1

;Calc Coord#4
Color 255,0,0
X3=XNew
Y3=YNew
Z3=Znew
;X3=X2 - (XVelNew + XAccelNew * Time)
;Y3=Y2 - (YVelNew + YAccelNew * Time)
Text X3-4,Y3-16,"E"
Rect X3,Y3,3,3,1


;Calc Cubic Spline Points
RecursiveMathA=3*x2
RecursiveMathB=3*x1
RecursiveMathC=6*x1
RecursiveMathD=3*x0
RecursiveMathE=3*y2
RecursiveMathF=3*y1
RecursiveMathG=6*y1
RecursiveMathH=3*y0
RecursiveMathI=3*z2
RecursiveMathJ=3*z1
RecursiveMathK=6*z1
ResurciveMathL=3*z0


A = X3 - RecursiveMathA + RecursiveMathB - x0
B = RecursiveMathA - RecursiveMathC + RecursiveMathD
C = RecursiveMathB - RecursiveMathD
D = x0
E = y3 - RecursiveMathE + RecursiveMathF - y0
F = RecursiveMathE - RecursiveMathG + RecursiveMathH
G = RecursiveMathF - RecursiveMathH
H = y0
I = z3 - RecursiveMathI + RecursiveMathJ - z0
J = RecursiveMathI - RecursiveMathK + RecursiveMathL
K = RecursiveMathJ - RecursiveMathL
L = z0

;Draw the Spline
For t#=0 To 1 Step .001
	x = A*t^3 + B*t^2 + C*t + D
	y = E*t^3 + F*t^2 + G*t + H 
Plot x,y
Next

End Function
