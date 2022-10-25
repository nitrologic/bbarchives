; ID: 651
; Author: Markus Rauch
; Date: 2003-04-14 12:42:55
; Title: Bezier Interpolation in 3D
; Description: Bezier Interpolation in 3D

;Bezier Interpolation BlitzBasic 3D Example from Markus Rauch

;MR 14.04.2003

;--------------------------------------------------------

Graphics3D 800,600,16,0
SetBuffer BackBuffer()

AppTitle " Bezier Interpolation BlitzBasic 3D Example from Markus Rauch"

;--------------------------------------------------------

Global camp=CreatePivot() 
Global cam=CreateCamera(camp) 
CameraZoom cam,1
CameraRange cam,1,10000

PositionEntity camp,0,0,-200

;--------------------------------------------------------

Type Vector3D
 Field x#,y#,z#
 Field Pitch#
 Field Yaw#
 Field Roll#
End Type

Dim ve.Vector3D(100)

ve.Vector3D(0)=New Vector3D
ve.Vector3D(1)=New Vector3D
ve.Vector3D(2)=New Vector3D
ve.Vector3D(3)=New Vector3D

Global VectorMax=4

Global p.Vector3D=New Vector3D

ve(0)\x=-50
ve(0)\y=50
ve(0)\z=0

ve(1)\x=-25
ve(1)\y=0
ve(1)\z=0

ve(2)\x=25
ve(2)\y=50
ve(2)\z=0

ve(3)\x=0 ;Moving
ve(3)\y=0
ve(3)\z=0

While Not KeyHit(1)

 RenderWorld

 ;-------------------- Moving Point (3)

 Local w#

 ve(3)\x=50+Sin(w)*100
 ve(3)\y= 0+Cos(w)*100

 w#=w#+1 :If w>360.0 Then w=w-360.0

 ;--------------------

 Color 255,255,0 

 Local mu#
 Local st#=0.01

 mu=0.0
 Repeat

  Bezier4 p,ve(0),ve(1),ve(2),ve(3),mu ;Only 4 Points

  ;Bezier p,mu  ;All Points

  Plot3D p

  mu=mu+st
  If mu>1.0 Then Exit
 Forever

 ;Show all Points 
 For v=0 To VectorMax-1
  Color 128,128,128
  Text3D ve(v),"V "+V
  Color 255,0,0
  Plot3D ve(v)
 Next

 Flip
Wend
End

;##########################################################################

Function Bezier3(p.Vector3D,p1.Vector3D,p2.Vector3D,p3.Vector3D,mu#) 

 ;MR 13.04.2003

 ;Three control point Bezier interpolation
 ;mu ranges from 0 To 1, start To End of curve

  Local mum1#,mum12#,mu2#

  mu2 = mu * mu
  mum1 = 1 - mu
  mum12 = mum1 * mum1

  p\x = p1\x * mum12 + 2 * p2\x * mum1 * mu + p3\x * mu2
  p\y = p1\y * mum12 + 2 * p2\y * mum1 * mu + p3\y * mu2
  p\z = p1\z * mum12 + 2 * p2\z * mum1 * mu + p3\z * mu2

End Function

Function Bezier4(p.Vector3D,p1.Vector3D,p2.Vector3D,p3.Vector3D,p4.Vector3D,mu#)

 ;MR 13.04.2003

 ;Four control point Bezier interpolation
 ;mu ranges from 0 To 1, start To End of curve

 Local mum1#,mum13#,mu3#

 mum1 = 1.0 - mu
 mum13 = mum1 * mum1 * mum1
 mu3 = mu * mu * mu

 p\x = mum13*p1\x + 3.0*mu*mum1*mum1*p2\x + 3.0*mu*mu*mum1*p3\x + mu3*p4\x
 p\y = mum13*p1\y + 3.0*mu*mum1*mum1*p2\y + 3.0*mu*mu*mum1*p3\y + mu3*p4\y
 p\z = mum13*p1\z + 3.0*mu*mum1*mum1*p2\z + 3.0*mu*mu*mum1*p3\z + mu3*p4\z

End Function

;##########################################################################

Function Bezier(p.Vector3D,mu#) 

 ;MR 14.04.2003

 ;General Bezier curve
 ;Number of control points is n+1
 ;mu 0 bis 1    IMPORTANT, the Last point is Not computed

 Local k,kn,nn,nkn
 Local blend#,muk#,munk#

 Local px#,py#,pz#

 Local n=VectorMax-1

   p\x=0.0
   p\y=0.0
   p\z=0.0
 
   muk = 1.0
   munk = pow(1.0-mu,Float(n))

   For k=0 To n
      nn = n
      kn = k
      nkn = n - k
      blend = muk * munk
      muk =muk * mu
      munk = munk / (1.0-mu)
      While nn => 1 
         blend=blend * nn
         nn=nn-1
         If kn > 1 Then
            blend=blend / Float(kn)
            kn=kn-1
         EndIf
         If nkn > 1 Then
            blend=blend / Float(nkn)
           nkn=nkn-1
         EndIf
      Wend
      p\x=p\x+ve(k)\x * blend
      p\y=p\y+ve(k)\y * blend
      p\z=p\z+ve(k)\z * blend
   Next
  
End Function

;##########################################################################

Function pow#(a#,b#)

 ;MR 14.04.2003

 ;C Like :-)

 Return a^b

End Function

;##########################################################################

Function Plot3D(p.Vector3D)

 ;MR 14.04.2003

 ;cam ist Global und das Handle der Camera

 Local x,y

 CameraProject cam,p\x,p\y,p\z
 x=ProjectedX()
 y=ProjectedY()

 Plot x,y

End Function

;##########################################################################

Function Text3D(p.Vector3D,t$)

 ;MR 14.04.2003

 ;cam ist Global und das Handle der Camera

 Local x,y

 CameraProject cam,p\x,p\y,p\z
 x=ProjectedX()
 y=ProjectedY()

 Text x,y,t$,True,True

End Function

;##########################################################################

Function CubicInterpolate#(y0#,y1#,y2#,y3#,mu#)

 ;MR 13.04.2003

 Local a0#,a1#,a2#,a3#,mu2#

 mu2 = mu*mu
 a0 = y3 - y2 - y0 + y1
 a1 = y0 - y1 - a0
 a2 = y2 - y0
 a3 = y1

 Return (a0*mu*mu2+a1*mu2+a2*mu+a3)

End Function

;##########################################################################
