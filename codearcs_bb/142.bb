; ID: 142
; Author: skidracer
; Date: 2001-11-19 01:13:28
; Title: EntityScale
; Description: function to calculates entity's scale

Function EntityScale#(entity,axis) 
    x#=GetMatElement(entity,axis,0) 
    y#=GetMatElement(entity,axis,1) 
    z#=GetMatElement(entity,axis,2) 
    Return Sqr(x*x+y*y+z*z) 
End Function 

Graphics3D 640,480 
sphere=CreateSphere(order) 
ScaleEntity sphere,2,1,1 
RotateEntity sphere,23,-45,99 

DebugLog "ScaleX="+EntityScale(sphere,0) 
DebugLog "ScaleY="+EntityScale(sphere,1) 
DebugLog "ScaleZ="+EntityScale(sphere,2) 

End 
