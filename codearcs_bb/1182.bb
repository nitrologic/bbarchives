; ID: 1182
; Author: Sweenie
; Date: 2004-10-29 00:44:18
; Title: Rigid Body Simulation
; Description: Simple Rigid Body Simulation (No collisions yet)

Graphics3D 800,600,0,2
SetBuffer BackBuffer()

camera = CreateCamera()
PositionEntity camera,0,0,-10

WireFrame True

Global Cprod_x#
Global Cprod_y#
Global Cprod_z#
Const NEARZERO# = 0.0001

;RigidBody Type

Type RigidBody

 Field Mass#

 Field Com_x# ; Center Of Mass
 Field Com_y# 
 Field Com_z#
 Field Linearvel_x# ; Linear Velocity
 Field Linearvel_y#
 Field Linearvel_z#
 Field Linearacc_x# ; Linear Acceleration
 Field Linearacc_y#
 Field Linearacc_z#
 Field Forcevec_x# ; Force Vector
 Field Forcevec_y#
 Field Forcevec_z#
 Field Forceloc_x# ; Force Location
 Field Forceloc_y#
 Field Forceloc_z#

 Field Rotangle_x# ; Rotation Angle (Orientation)
 Field Rotangle_y#
 Field Rotangle_z#
 Field Angularvel_x# ; Angular Velocity
 Field Angularvel_y#
 Field Angularvel_z#
 Field Angularacc_x# ; Angular Acceleration
 Field Angularacc_y#
 Field Angularacc_z#
 Field Inertia_x# ; Rotational Inertia
 Field Inertia_y#
 Field Inertia_z#
 Field Torque_x# ; eh... Just Torque ;)
 Field Torque_y#
 Field Torque_z#

 Field Coeff# ; Coefficient of restitution / "Bounce Factor" 0.0 = Not elastic, 1.0 = VERY elastic, 1.1 = Too Elastic ;) 
 Field Friction# ; * Not implemented yet *
 Field LinearDamping# 
 Field AngularDamping#

End Type


RB.RigidBody = New RigidBody
RB\Mass#=1.0
RB\Inertia_x#=0.4
RB\Inertia_y#=0.4
RB\Inertia_z#=0.4
RB\LinearDamping# = 0.001
RB\AngularDamping# = 0.01

RB_Mesh = CreateSphere()

While Not KeyHit(1)

UpdateBody RB,0.02

PositionEntity RB_Mesh,RB\Com_x#,RB\Com_y#,RB\Com_z#
RotateEntity RB_Mesh,RB\Rotangle_x#,-RB\Rotangle_y#,RB\Rotangle_z#

If KeyDown(205) Then ; Right
 RB\Forcevec_x# = 1.0
 RB\Forcevec_y# = 0.0
 RB\Forcevec_z# = 0.0
 RB\Forceloc_x# = 0.0
 RB\Forceloc_y# = 25.0
 RB\Forceloc_z# = -25.0
ElseIf KeyDown(203) Then ; Left
 RB\Forcevec_x# = -2.0
 RB\Forcevec_y# = 0.0
 RB\Forcevec_z# = 0.0
 RB\Forceloc_x# = 0.0
 RB\Forceloc_y# = 15.0
 RB\Forceloc_z# = -5.0
Else
 RB\Forcevec_x# = 0.0
 RB\Forcevec_y# = 0.0
 RB\Forcevec_z# = 0.0
EndIf


RenderWorld

Flip

Wend
End


Function UpdateBody(Body.RigidBody,dt#)

 ; *** Linear Dynamics ***

 ;Linear Acceleration
 If Body\Mass#>0 Then
  Body\Linearacc_x# = Body\Forcevec_x# / Body\Mass#
  Body\Linearacc_y# = Body\Forcevec_y# / Body\Mass#
  Body\Linearacc_z# = Body\Forcevec_z# / Body\Mass#
 EndIf

 ;Linear Velocity
 Body\Linearvel_x# = Body\Linearvel_x# + Body\Linearacc_x# * dt#
 Body\Linearvel_y# = Body\Linearvel_y# + Body\Linearacc_y# * dt#
 Body\Linearvel_z# = Body\Linearvel_z# + Body\Linearacc_z# * dt#
 
 ;Center Of Mass
 Body\Com_x# = Body\Com_x# + Body\Linearvel_x# * dt#
 Body\Com_y# = Body\Com_y# + Body\Linearvel_y# * dt#
 Body\Com_z# = Body\Com_z# + Body\Linearvel_z# * dt#
 

 ; *** Rotational Dynamics ***

 ;Torque
 CrossProduct Body\Forceloc_x#,Body\Forceloc_y#,Body\Forceloc_z#,Body\Forcevec_x#,Body\Forcevec_y#,Body\Forcevec_z# 
 Body\Torque_x#=Cprod_x#
 Body\Torque_y#=Cprod_y#
 Body\Torque_z#=Cprod_z#

 ;Angular Acceleration
 Body\Angularacc_x# = Body\Torque_x# / Body\Inertia_x#
 Body\Angularacc_y# = Body\Torque_y# / Body\Inertia_y#
 Body\Angularacc_z# = Body\Torque_z# / Body\Inertia_z#

 ;Angular Velocity
 Body\Angularvel_x# = Body\Angularvel_x# + Body\Angularacc_x# * dt#
 Body\Angularvel_y# = Body\Angularvel_y# + Body\Angularacc_y# * dt#
 Body\Angularvel_z# = Body\Angularvel_z# + Body\Angularacc_z# * dt#

 ;Angles of rotation / Orientation
 Body\Rotangle_x# = Body\Rotangle_x# + Body\Angularvel_x# * dt#
 Body\Rotangle_y# = Body\Rotangle_y# + Body\Angularvel_y# * dt#
 Body\Rotangle_z# = Body\Rotangle_z# + Body\Angularvel_z# * dt#

 ;Damp Linear & Angular Velocity
 Body\Linearvel_x#=Body\Linearvel_x#*(1.0-Body\LinearDamping#)
 Body\Linearvel_y#=Body\Linearvel_y#*(1.0-Body\LinearDamping#)
 Body\Linearvel_z#=Body\Linearvel_z#*(1.0-Body\LinearDamping#)
 Body\Angularvel_x#=Body\Angularvel_x#*(1.0-Body\AngularDamping#)
 Body\Angularvel_y#=Body\Angularvel_y#*(1.0-Body\AngularDamping#)
 Body\Angularvel_z#=Body\Angularvel_z#*(1.0-Body\AngularDamping#)

 ;Reset Velocities If Near Zero Value / *Hack* ;) Not necessary but prevents jittering when Body is considered not moving.
 If Abs(Body\Linearvel_x#)<NEARZERO# Then Body\Linearvel_x#=0.0
 If Abs(Body\Linearvel_y#)<NEARZERO# Then Body\Linearvel_y#=0.0
 If Abs(Body\Linearvel_z#)<NEARZERO# Then Body\Linearvel_z#=0.0
 If Abs(Body\Angularvel_x#)<NEARZERO# Then Body\Angularvel_x#=0.0
 If Abs(Body\Angularvel_y#)<NEARZERO# Then Body\Angularvel_y#=0.0
 If Abs(Body\Angularvel_z#)<NEARZERO# Then Body\Angularvel_z#=0.0


End Function

Function DotProduct#(x1#,y1#,z1#,x2#,y2#,z2#)
	Return (x1#*x2#)+(y1#*y2#)+(z1#*z2#)
End Function

Function CrossProduct(x1#,y1#,z1#,x2#,y2#,z2#)
	Cprod_x#=(y1#*z2#)-(z1#*y2#)
	Cprod_y#=(z1#*x2#)-(x1#*z2#)
	Cprod_z#=(x1#*y2#)-(y1#*x2#)
End Function
