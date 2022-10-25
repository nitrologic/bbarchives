; ID: 1630
; Author: Mr. Bean
; Date: 2006-03-03 12:47:12
; Title: Mesh-Editor
; Description: A primitively Mesh-Editor

; ID: 1630
; Author: Mr. Bean
; Date: 2006-03-03 12:47:12
; Title: Mesh-Editor
; Description: A primitively Mesh-Editor

Global GX=1024            
Global GY=768                  
Global HX=GX/2                            
Global HY=GY/2                      
Global HHX=GX/4             
Global HHY=GY/4             
Graphics GX,GY                
Dim Vertex  (10000,4)        
Dim Triangle(4000,4)        
Dim RV(10000)               
Dim RT(4000)                
Global NumberV=-1,NumberT=-1       
Global jn=0,AVT=1                           
Global VFL=1                
Global VM=0                 
Global TFL=0                
Global X1,Modus$,Meld$      
Global X2,X3                
Global Y1,TVC,NT=1          
Global Y2,Y3,rectjn         
Global c1,c2,c3,p
HidePointer
pointer=CreateImage(10,10)
SetBuffer ImageBuffer(pointer)
Line 0,0,5,3
Line 0,0,5,4
Line 0,0,5,5
Line 0,0,5,6
Line 5,4,10,4
SetBuffer FrontBuffer()
Modus$="Create Vertices"
Global a10$

Oef()
C#=0.0
Cls



Repeat

If C>1.0 Cls
C=C+0.1
If C>1.1 C=0
DrawImage pointer,MouseX(),MouseY()
If  KeyHit(47) And KeyDown(211) NumberV=NumberV-1
If  KeyHit(20) And KeyDown(211) NumberT=NumberT-1

Line 0,HY,GX,HY                              
Line HX,0,HX,GY                              
Text 5,5,  "Front"                           
Text HX+5,5,"Left"                          
Text 5,HY+5,"Top"                           
                                  
            
                     
Color 255,255,255                            
Text HX+10,HY+5,"Vertex/ices:  "+(NumberV+1) 
Text HX+10,HY+20,"Triangles:    "+(NumberT+1)
Text HX+10,HY+35,"Modus:        "+Modus      

    
If KeyHit(2) ChangeVFL()
If KeyHit(3) ChangeVM()


If VM=1 Then 
VM()
VV()
EndIf

If VFL=1 Then 
VFL()
EndIf


TFL()       
TZ()                           
VZ()          

                                     




Delay 1
Until KeyHit(1)



Cls

If a10=""a10=Input("Name?  ")
a=WriteFile(a10+".ogs")
WriteLine a,NumberV
WriteLine a,NumberT
For I=0 To NumberV
WriteLine a,Vertex(I,1)
WriteLine a,Vertex(I,2)
WriteLine a,Vertex(I,3)


Next
For I=0To NumberT
WriteLine a,Triangle(I,1)
WriteLine a,Triangle(I,2)
WriteLine a,Triangle(I,3)


Next
CloseFile a

Cls
HidePointer
Graphics3D GX,GY

SetBuffer BackBuffer()
cam=CreateCamera()

light=CreateLight()

CameraClsColor cam,50,50,50
Global mesh=CreateMesh()
surf=CreateSurface(mesh)
PositionEntity cam,0,0,0
For I=0 To NumberV




RV(I)=AddVertex(surf,-Vertex(I,1),-Vertex(I,2),-Vertex(I,3))

Next
For I=0 To NumberT

a1=Triangle(I,1)
a2=Triangle(I,2)
a3=Triangle(I,3)
RT(I)=AddTriangle(surf,RV(a1),RV(a2),RV(a3))

Next

Lines()
Repeat

If KeyHit(28) SaveBuffer(BackBuffer(),a10+" "+Count+".bmp"):Count=Count+1
If KeyHit(57) e=1-e
If KeyDown(200) MoveEntity cam,0,+1,0
If KeyDown(208) MoveEntity cam,0,-1,0
If KeyDown(205) MoveEntity cam,+1,0,0
If KeyDown(203) MoveEntity cam,-1,0,0
If KeyDown(30 ) MoveEntity cam,0,0,+1
If KeyDown(44 ) MoveEntity cam,0,0,-1
WireFrame e


MoveEntity cam,0,0,+MouseZSpeed()
	mxspd#=MouseXSpeed()*0.25
	myspd#=MouseYSpeed()*0.25
		
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	campitch=campitch+(myspd/2)
	If campitch<-85 Then campitch=-85
	If campitch>85 Then campitch=85
	RotateEntity cam,campitch,EntityYaw(cam)-mxspd,0
RenderWorld
Flip
Until KeyHit(1)
  


End

Function ChangeVFL()

If VFL=0 Then 
VFL=1
Else
VFL=0
EndIf
VM=0:TFL=0

Modus="Create Vertices "


NT=1
End Function

Function ChangeVM()

If VM=0 Then 
VM=1
Else
VM=0
EndIf
VFL=0:TFL=0


Color 255,255,255
Modus="Mark Vertices/Triangles "
NT=1
End Function






Function ChangeM()
For I=0 To NumberV
If Vertex(I,4)=1 Then
Vertex(I,5)=0
Else
Vertex(I,5)=1
EndIf
Next
End Function





Function Line3D(x,y,z,x2,y2,z2)

c1=CreateSphere()
PositionEntity c1,x,y,z
ScaleEntity c1,0.5,0.5,0.5
EntityColor c1,0,0,255

c2=CreateSphere()
PositionEntity c2,x2,y2,z2
ScaleEntity c2,0.5,0.5,0.5
EntityColor c2,0,0,255

p=CreatePivot()
PositionEntity p,x,y,z

le#=EntityDistance# (c1,c2)
le#=le#-(le#/2)

c3=CreateCylinder()
PositionEntity c3,x,le#+y,z
ScaleEntity c3,0.5,le#,0.5
EntityColor c3,0,255,0

EntityParent c3,p

PointEntity p,c2
TurnEntity p,90,0,0


End Function


Function Lines()
For I=0 To NumberT
a1=Triangle(I,1)
a2=Triangle(I,2)
a3=Triangle(I,3)
Line3D(-Vertex(a1,1),-Vertex(a1,2),-Vertex(a1,3),-Vertex(a2,1),-Vertex(a2,2),-vertex(a2,3))
Line3D(-Vertex(a2,1),-Vertex(a2,2),-Vertex(a2,3),-Vertex(a1,1),-Vertex(a1,2),-vertex(a1,3))
Line3D(-Vertex(a2,1),-Vertex(a2,2),-Vertex(a2,3),-Vertex(a3,1),-Vertex(a3,2),-vertex(a3,3))
Next
End Function








Function NeuVertex(a1,a2,a3,a4)
Vertex(a1,1)=a2
Vertex(a1,2)=a3
Vertex(a1,3)=a4
End Function

Function NeuTriangle(a1,a2,a3,a4)
Triangle(a1,1)=a2
Triangle(a1,2)=a3
Triangle(a1,3)=a4
End Function


Function Oef()
a10=CommandLine()
If a10="" a10$=Input("Name?  ")

If a10="" Then Goto endoef
a=ReadFile(a10+".ogs")
If a=0 Goto endoef
a1=ReadLine(a)
a2=ReadLine(a)

For I=0 To a1
a3= ReadLine(a)
a4= ReadLine(a)
a5= ReadLine(a)
NumberV=NumberV+1
NeuVertex(NumberV,a3,a4,a5)
Next

For I=0 To a2
a3=ReadLine(a)
a4=ReadLine(a)
a5=ReadLine(a)
NumberT=NumberT+1
NeuTriangle(NumberT,a3,a4,a5)
Next
.endoef
End Function

Function VFL()
If  MouseHit(1)=True Then 
X=MouseX()                
Y=MouseY()                
jn=1                      
EndIf                    

If jn=1 Then                   
If X>HX Then                         
X1=1                          
Else                         
X1=0                         
EndIf                          
If Y>HY Then                      
Y1=1                         
Else                            
Y1=0                              
EndIf                             
If X1=0 And Y1=0 Then        
NumberV=NumberV+1                     
NeuVertex(NumberV,MouseX(),MouseY(),HHY)                                                            
EndIf                                   
If X1=1 And Y1=0 Then                        
                       
NumberV=NumberV+1            
 NeuVertex(NumberV,HHY,MouseY(),MouseX()-HX)                             
EndIf                        
If X1=0 And Y1=1 Then                            
                       
NumberV=NumberV+1            
 NeuVertex(NumberV,MouseX(),HHY,MouseY()-HY)        
EndIf
jn=0                               
EndIf
End Function


Function TFL()                                 
 TVC=0
  For I=0 To NumberV
   If Vertex(I,4)=1  Then
    TVC=TVC+1
   EndIf
  Next
   If TVC<3 Then 
    
    TVC=0
   
     ElseIf TVC>3 Then 
   
      TVC=0
    
   EndIf
  NT=0


 If TVC=3 Then
EndIf 





If KeyHit(48) Then 


   NumberT=NumberT+1
    For I=0 To NumberV
     If Vertex(I,4)=1  Then
      Triangle(NumberT,AVT)=I
      Triangle(NumberT,4)=1
      AVT=AVT+1
       If AVT=4 Then 
        AVT=1
       EndIf
     EndIf
    Next


NumberT=NumberT+1
For I=0 To NumberV
     If Vertex(I,4)=1  Then
      Triangle(NumberT,AVT)=I
      Triangle(NumberT,4)=1
      AVT=AVT+1
       If AVT=4 Then 

        AVT=1
       EndIf
     EndIf
Next


Z=Triangle(NumberT,2)
Triangle(NumberT,2)=Triangle(NumberT,3)
Triangle(NumberT,3)=Z

EndIf
End Function


Function VM()
If MouseHit(1) X3=MouseX():Y3=MouseY():rectjn=1
If Y3<21 Then Y3=20

If MouseDown(1)=True Then
If MouseX()<HX Then
X2=0
Else
X2=1
EndIf
If MouseY()>HY Then
Y2=1
Else
Y2=0
EndIf


Color 0,0,200
Rect X3,Y3,MouseX()-X3,MouseY()-Y3,0 
Color 255,255,255
Else 
rectjn=0

EndIf

If X2=0 And Y2=0 Then
For I=0 To NumberV
If Vertex(I,1)<MouseX() And Vertex(I,1)>X3 And Vertex(I,2)<MouseY()  And Vertex(I,2)>Y3 And KeyDown(29) And rectjn=1 Then
Vertex(I,4)=0
ElseIf Vertex(I,1)<MouseX() And Vertex(I,1)>X3 And Vertex(I,2)<MouseY()  And Vertex(I,2)>Y3 And rectjn=1  Then
Vertex(I,4)=1
EndIf
Next

EndIf





If X2=0 And Y2=1 Then 
For I=0 To NumberV
If Vertex(I,1)<MouseX() And Vertex(I,1)>X3 And Vertex(I,3)<MouseY()-HY And Vertex(I,3)>(Y3-HY) And  KeyDown(29) And rectjn=1 Then
Vertex(I,4)=0
ElseIf Vertex(I,1)<MouseX() And Vertex(I,1)>X3 And Vertex(I,3)<MouseY()-HY And Vertex(I,3)>(Y3-HY) And rectjn=1  Then
Vertex(I,4)=1
EndIf
Next
EndIf





If X2=1 And Y2=0 Then
For I=0 To NumberV

If Vertex(I,3)<MouseX()-HX And Vertex(I,3)>(X3-HX) And Vertex(I,2)<MouseY() And Vertex(I,2)>Y3 And KeyDown(29) And rectjn=1 Then
Vertex(I,4)=0
ElseIf Vertex(I,3)<MouseX()-HX And Vertex(I,3)>(X3-HX) And Vertex(I,2)<MouseY() And Vertex(I,2)>Y3 And rectjn=1 Then
Vertex(I,4)=1
EndIf
Next
EndIf





If KeyHit(157) Then
ChangeM()
EndIf



For I=0 To NumberT
TVC=0
a=Triangle(I,1)
If Vertex(a,4)=1 TVC=TVC+1
a=Triangle(I,2)
If Vertex(a,4)=1 TVC=TVC+1
a=Triangle(I,3)
If Vertex(a,4)=1 TVC=TVC+1
If TVC=3  Then
Triangle(I,4)=1
Else 
Triangle(I,4)=0
EndIf
Next
End Function


Function VV()
For I=0 To NumberV
If Vertex(I,4)=1 Then
If KeyHit(71) jn1=1
If KeyHit(79) jn2=1
If KeyHit(72) jn3=1
If KeyHit(80) jn4=1
If KeyHit(73) jn5=1
If KeyHit(81) jn6=1


If  jn1=1    Then

Vertex(I,1)=Vertex(I,1)+1

EndIf

If  jn2=1  Then 

Vertex(I,1)=Vertex(I,1)-1

EndIf

If  jn3=1  Then 

Vertex(I,2)=Vertex(I,2)+1

EndIf

If jn4=1  Then 

Vertex(I,2)=Vertex(I,2)-1

EndIf

If  jn5=1  Then 

Vertex(I,3)=Vertex(I,3)+1

EndIf

If  jn6=1  Then 

Vertex(I,3)=Vertex(I,3)-1

EndIf

Color 255,255,255
End If
Next
End Function



Function TZ()
For I=0 To NumberT+1

a=Triangle (I,1)
a1=Triangle(I,2)
a2=Triangle(I,3)                   
VX =Vertex(a,1)                     
VY =Vertex(a,2)
VX1=Vertex(a1,1)
VY1=Vertex(a1,2)
VX2=Vertex(a2,1)
VY2=Vertex(a2,2)                        
OX =Vertex(a,1)                         
OY =Vertex(a,3) 
OX1=Vertex(a1,1)
OY1=Vertex(a1,3)
OX2=Vertex(a2,1)
OY2=Vertex(a2,3)                      
SX =Vertex(a,3)                           
SY =Vertex(a,2)
SX1=Vertex(a1,3)
SY1=Vertex(a1,2)
SX2=Vertex(a2,3)
SY2=Vertex(a2,2)                        
Mk =Triangle(I,4)  

If Mk=1 Color 200,0,0   Else Color 0,200,200         
           
Line VX,VY,VX1,VY1
Line VX,VY,VX2,VY2
Line VX1,VY1,VX2,VY2

                          
Origin 0,HY                      
Line OX,OY,OX1,OY1
Line OX,OY,OX2,OY2
Line OX1,OY1,OX2,OY2 
                            
Origin HX,0                          
Line SX,SY,SX1,SY1
Line SX,SY,SX2,SY2
Line SX1,SY1,SX2,SY2
                       
Origin 0,0                   
Color 255,255,255                       
Next      
End Function

Function VZ()
For I=0 To NumberV                   
VX =Vertex(I,1)                     
VY =Vertex(I,2)                        
OX =Vertex(I,1)                          
OY =Vertex(I,3)                      
SX =Vertex(I,3)                          
SY =Vertex(I,2)                       
Mk2=Vertex(I,4)               
If Mk2=1 Color 200,0,0             
Rect VX,VY,2,2                           
Origin 0,512                      
Rect OX,OY,2,2                              
Origin 640,0                          
Rect SX,SY,2,2                       
Origin 0,0                   
Color 255,255,255                       
Next
End Function
