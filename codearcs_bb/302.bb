; ID: 302
; Author: Ice9
; Date: 2002-04-21 14:49:35
; Title: Vertex lighting 
; Description: Lambert shading using multiple lights

Graphics3D 640,480,16
;AmbientLight 0,0,0
For x=1 To 5
	For y= 1 To 5
		For z= 1 To 5
			sphere=CreateSphere( 32 )
			PositionEntity sphere,x*5,y*5,z*5
			ApplyAmbient(sphere,32,32,32)
			ApplyLight(sphere,25,0,0,20,1,255,32,32)
			ApplyLight(sphere,0,0,0,20,1,32,32,255)
			ApplyLight(sphere,12.5,25,0,20,1,32,255,32)				
			ApplyLight(sphere,25,0,15,20,1,32,255,32)
			ApplyLight(sphere,0,0,15,20,1,255,32,32)
			ApplyLight(sphere,12.5,25,15,20,1,32,32,255)
			ApplyLight(sphere,25,0,20,20,1,32,255,255)
			ApplyLight(sphere,0,0,20,20,1,255,255,32)
			ApplyLight(sphere,12.5,25,20,20,1,255,32,255)
			ApplyLight(sphere,12.5,12.5,12.5,20,1,255,255,255)						
			EntityFX sphere,3
		Next
	Next
Next


camera=CreateCamera()
PositionEntity camera,12,12,2
RotateEntity camera,0,0,315

While Not KeyHit(1)		
	UpdateWorld
	RenderWorld
	Flip
Wend


Function ApplyLight(entity,LX#,LY#,LZ#,radius#,flag,Lr,Lg,Lb)
 ;Lambert shading in blitz "by Staton Richardson (Arbitrage)"	
 ;This function is meant for 1 time lighting not realtime	
 ;entity is the entity you wish to light
 ;LX, LY, LZ are the coodinates of the light in world space
 ;Lr, Lg, Lb are the colors of the light
 ;radius is the radius the light affects. Light falls off with distance so half the radius would be half the light
 ;flag tells the function to include the current vertex color for multiple lights 
 
 
 count=CountSurfaces(entity) 
 For n=1 To count
  surf=GetSurface(entity,n) 
  Vcount=CountVertices(surf)-1 
  For v=0 To Vcount          
         xd#=VertexX(surf,v)+EntityX(entity)-LX#
         yd#=VertexY(surf,v)+EntityY(entity)-LY#
         zd#=VertexZ(surf,v)+EntityZ(entity)-LZ#
   mag#=Sqr(xd#*xd# + yd#*yd# + zd#*zd#)
                
   If flag=1
    vcr=VertexRed(surf,v)
    vcg=VertexGreen(surf,v)
    vcb=VertexBlue(surf,v)
   EndIf    
    
   vr=Lr-(Lr/radius)*mag#
   vg=Lg-(Lg/radius)*mag#
   vb=Lb-(Lb/radius)*mag# 
       
   If vr>255 Then vr=255
   If vg>255 Then vg=255
   If vb>255 Then vb=255 
 
   vx#=VertexNX(surf,v)
   vy#=VertexNY(surf,v)
   vz#=VertexNZ(surf,v)
    
   mag#=1/mag#
   LnY#=yd#*mag#*-1
   LnX#=xd#*mag#*-1
   LnZ#=zd#*mag#*-1
    
   Kh#=((vx)*LnX+(vy)*LnY+(vz)*LnZ)
   If kh#>0 
          VertexColor surf,v,vr*Kh#+vcr,vg*Kh#+vcg,vb*Kh#+vcb
   EndIf  
 
  Next   
 Next 
End Function


Function ApplyAmbient(entity,r,g,b)		
	
	count=CountSurfaces(entity)	
	For n=1 To count
		surf=GetSurface(entity,n)	
		Vcount=CountVertices(surf)-1	
		For v=0 To Vcount
        	VertexColor surf,v,r,g,b		
		Next			
	Next

End Function
