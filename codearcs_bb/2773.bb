; ID: 2773
; Author: Krischan
; Date: 2010-09-29 08:46:30
; Title: 3D Elite Style Scanner
; Description: Demonstrates the space scanner like in the game

AppTitle "3D Elite Style Scanner"

Graphics3D 800,600,32,2

Const TurnSpeed#    = 4.00      ; cam turn speed
Const RollSpeed#    = 0.50      ; cam roll speed
Const CameraSpeed#  = 1.00      ; cam move speed
Const Objects%      = 2000      ; number of space objects
Const RadarSize%    = 256       ; radar size in pixels

Const PoleWidth#    = 1.0       ; width of poles
Const BlipSize#     = 3.0       ; size of radar blip
Const VertScale#    = 1.0       ; height multiplicator of pole/blip
Const RadarBorder#  = 0.9       ; range within 1.0 to display

; color scheme: blue
Const R1%=  0,G1%=128,B1%=192   ; grid lines color
Const R2%=  0,G2%= 16,B2%= 32   ; scanner background color
Const R3%=  0,G3%=128,B3%=255   ; scanner border glow color

; color scheme: green
;Const R1%=  0,G1%=192,B1%=  0   ; grid lines color
;Const R2%=  0,G2%= 32,B2%=  0   ; scanner background color
;Const R3%=  0,G3%=255,B3%=  0   ; scanner border glow color

Type blip
    
    Field entity%
    Field blip%
    Field pole%
    Field x#,y#,z#
    Field vx#,vy#,vz#
    Field r%,g%,b%
    Field size#
    
End Type

Global WIDTH%=GraphicsWidth()
Global HEIGHT%=GraphicsHeight()
Local TIMER%=CreateTimer(60)

; camera
Local CAM%=CreateCamera()
CameraRange CAM,1,WIDTH+RadarSize

; setup radar
Local RADAR=InitRadar(RadarSize,512,10,CAM)

; setup objects
InitObjects(RADAR,Objects,1000)

MoveMouse WIDTH/2,HEIGHT/2

While Not KeyHit(1)
    
    Local range%=256,visible%
    
    ; move player
    Movement(CAM)
    
    ; RMB = short range radar
    If MouseDown(2) Then range=128
    
    ; update radar
    visible%=UpdateRadar(CAM,RADAR,RadarSize,range,90)
    
    RenderWorld
    
    WaitTimer TIMER
    
    Text 0, 0,"Scanner range....: "+range
    Text 0,15,"Objects / visible: "+Objects+" / "+visible
    
    Flip
    
Wend

End

; populate scene with dummy objects
Function InitObjects(radar%,number%=100,range#=500.0)
    
    Local i%,b.blip
    
    For i=1 To number
        
        b.blip = New blip
        b\entity=CreateCone(16)
        b\blip=CreateSphere(6,radar)
        b\pole=CreateCube(radar)
        b\x=Rnd(-range,range)
        b\y=Rnd(-range,range)
        b\z=Rnd(-range,range)
        b\vx=Rnd(-0.1,0.1)
        b\vy=Rnd(-0.1,0.1)
        b\vz=Rnd(-0.1,0.1)
        b\r=Rand(255)
        b\g=Rand(255)
        b\b=Rand(255)
        b\size=2
        
        ; align "ship" to its flight vector
        AlignToVector b\entity,b\vx,b\vy,b\vz,2,1
        
        ; create the entity itself
        PositionEntity b\entity,b\x,b\y,b\z
        EntityFX b\entity,1
        EntityColor b\entity,b\r,b\g,b\b
        ScaleEntity b\entity,b\size,b\size*2,b\size
        EntityAutoFade b\entity,1,range
        
        ; creates a radar blip
        EntityFX b\blip,1
        EntityOrder b\blip,-100
        EntityColor b\blip,b\r,b\g,b\b
        
        ; creates a blip pole
        EntityFX b\pole,1
        EntityOrder b\pole,-100
        EntityColor b\pole,b\r,b\g,b\b
        
    Next
    
End Function

; simple spaceship movement
Function Movement(cam%,sensitivity#=1.0)
    
    Local roll#,cz#,tx#,ty#,multi%=1
    
    ; arrows = move / SHIFT or LMB = Turbo
    cz=(KeyDown(200)-KeyDown(208))*CameraSpeed
    roll=(KeyDown(203)-KeyDown(205))*RollSpeed
    If KeyDown(42) Or KeyDown(54) Or MouseDown(1) Then multi=10
    
    tx=Normalize(MouseX(),0,WIDTH , 1,-1)
    ty=Normalize(MouseY(),0,HEIGHT,-1, 1)
    
    If ty<0 Then ty=(Abs(ty)^sensitivity)*-1 Else ty=ty^sensitivity
    If tx<0 Then tx=(Abs(tx)^sensitivity)*-1 Else tx=tx^sensitivity
    
    TurnEntity cam,ty*TurnSpeed,tx*TurnSpeed,roll*TurnSpeed
    MoveEntity cam,0,0,cz*multi
	
End Function

; creates a nice elite stype radar sprite at screen bottom
Function InitRadar(scale#,size%=512,quads%=8,parent%=False,sharp#=1.0,blur#=50.0)
    
    Local RADAR%=CreateQuad()
    EntityParent RADAR,parent
    Local tex1%=CreateGridTexture(size,quads)
    Local tex2%=CreateGlowTexture(size,sharp,blur)
    
    ; apply textures to radar quad
    EntityFX RADAR,1
    ScaleEntity RADAR,scale,scale,scale
    PositionEntity RADAR,0,-HEIGHT+(RadarSize*0.75),WIDTH
    TextureBlend tex1,2
    TextureBlend tex2,3
    EntityTexture RADAR,tex1,0,1
    EntityTexture RADAR,tex2,0,2
    EntityBlend RADAR,1
    
    ; create centered ship marker (you!)
    Local CENTER%=CreateCone(3,1,RADAR)
    RotateMesh CENTER,30,0,0
    PositionEntity CENTER,0,0,0
    ScaleEntity CENTER,1.0/32,1.0/32,1.0/32
    EntityFX CENTER,1
    EntityColor CENTER,255,255,0
    
    EntityOrder CENTER,-20
    EntityOrder RADAR,-10
    
    Return RADAR
    
End Function

; updates the radar scene
Function UpdateRadar(cam%,radar%,scale#=128.0,range#=256.0,angle#=60.0)
    
    Local b.blip
    Local d#
    Local rx#,ry#,rz#
    
    Local halfscale#=scale/2.0
    Local doublescale#=scale*2.0
    Local ps#=PoleWidth/scale
    Local bs#=BlipSize/halfscale
    Local div#=((scale/2.0)/range)*RadarBorder
    
    Local visible%=0
    
    For b.blip = Each blip
        
        ; dumb object movement
        b\x=b\x+b\vx
        b\y=b\y+b\vy
        b\z=b\z+b\vz
        PositionEntity b\entity,b\x,b\y,b\z
        
        ; get distance to object
        d=EntityDistance(b\entity,cam)
        
        ; within scanner range?
        If d<range Then
            
            visible=visible+1
            
            ; show blip and its pole
            ShowEntity b\pole
            ShowEntity b\blip
            
            ; transform position
            TFormPoint (b\x,b\y,b\z,0,cam)
            rx=-TFormedX()/halfscale*div
            rz=TFormedZ()/halfscale*div
            ry=TFormedY()*div
            
            ; reposition/scale blip
            PositionEntity b\blip,0,0,0
            TranslateEntity b\blip,rx,rz,ry/scale*VertScale
            ScaleEntity b\blip,bs,bs,bs
            
            ; reposition/scale pole
            PositionEntity b\pole,0,0,0
            ScaleEntity b\pole,ps,ps,ry/doublescale*VertScale
            TranslateEntity b\pole,rx,rz,ry/doublescale*VertScale
            
        Else
            
            ; hide blip and its pole
            HideEntity b\pole
            HideEntity b\blip
            
        EndIf
        
    Next
    
    ; radar always points to cam
    RotateEntity radar,-angle,180,0
    
    Return visible
    
End Function

; simple quad creation
Function CreateQuad(r%=255,g%=255,b%=255,a#=1.0)
    
    Local mesh%,surf%,v1%,v2%,v3%,v4%
    
    mesh=CreateMesh()
    surf=CreateSurface(mesh)
    
    v1=AddVertex(surf,-1,1,0,1,0)
    v2=AddVertex(surf,1,1,0,0,0)
    v3=AddVertex(surf,-1,-1,0,1,1)
    v4=AddVertex(surf,1,-1,0,0,1)
    
    VertexColor surf,v1,r,g,b,a
    VertexColor surf,v3,r,g,b,a
    VertexColor surf,v2,r,g,b,a
    VertexColor surf,v4,r,g,b,a
    
    AddTriangle(surf,0,1,2)
    AddTriangle(surf,3,2,1)
    
    FlipMesh mesh
    
    Return mesh
    
End Function

; create procedural glowing border texture
Function CreateGlowTexture(size%=512,sharpness#=1.0,blur#=50.0)
    
    Local strength#=512*1.0/size*sharpness
    Local tex%=CreateTexture(size,size,3)
    Local tb%=TextureBuffer(tex)
    
    Local x%,y%,i#,j%,col%,rgb%,rc%,gc%,bc%
    
    LockBuffer tb
    
    For x=0 To size-1
        
        For y=0 To size-1
            
            ; clear colors
            rgb=0*$1000000+0*$10000+0*$100+0
            WritePixelFast x,y,rgb,tb
            
        Next
        
    Next
    
    For j=1 To (size/2)-2
        
        ; exponential falloff
        col=j*strength/Exp((((size/2)-2)-j)*(strength/blur))
        
        If col>224 Then col=Normalize(col,224,255,224,0)
        If col<0 Then col=0
        
        ; multiply RGB with brightness
        rc=R3*col Shr 8
        gc=G3*col Shr 8
        bc=B3*col Shr 8
        
        rgb=255*$1000000+rc*$10000+gc*$100+bc
        
        For i=0 To 360 Step 0.05
            
            WritePixelFast ((size-2)/2.0)+(Sin(i)*j),((size-2)/2.0)+(Cos(i)*j),rgb,tb
            
        Next
        
    Next
    
    UnlockBuffer tb
    
    Return tex
    
End Function

; normalize a value
Function Normalize#(value#=128.0,vmin#=0.0,vmax#=255.0,nmin#=0.0,nmax#=1.0)
    
    Return ((value#-vmin#)/(vmax#-vmin#))*(nmax#-nmin#)+nmin#
    
End Function

; create radar grid texture
Function CreateGridTexture(size%=512,quads%=16)
    
    Local x%,y%,j%,i#,rgb%,col%,r%,g%,b%
    Local steps%=size/quads
    
    Local tex%=CreateTexture(size,size,3+8)
    Local buffer%=TextureBuffer(tex)
    
    SetBuffer buffer
    
    ; background color
    Color R2,G2,B2
    Rect 0,0,size,size,1
    
    ; create grid
    Color R1,G1,B1
    For x=0 To quads-1
        For y=0 To quads-1
            Rect x*steps,y*steps,steps+1,steps+1,0
        Next
    Next
    
    Color 255,255,255
    
    LockBuffer buffer
    
    ; create glowing border and delete pixels outside the circle
    For j=(size/2)-6 To size
        
        For i=0 To 360 Step 0.05
            
            x=((size-2)/2.0)+(Sin(i)*j)
            y=((size-2)/2.0)+(Cos(i)*j)
            
            col=0
            If j<=(size/2) Then col=Normalize(j,(size/2)-6,(size/2),255,0)
            
            ; multiply RGB with brightness
            r=R3*col Shr 8
            g=G3*col Shr 8
            b=B3*col Shr 8
            
            rgb=0*$1000000+r*$10000+g*$100+b
            
            If x>=0 And x<size And y>=0 And y<size Then
				
                WritePixelFast x,y,rgb,buffer
                
            EndIf
            
        Next
        
    Next
	
    UnlockBuffer buffer
    
    SetBuffer BackBuffer()
    
    Return tex
    
End Function
