; ID: 249
; Author: MadJack
; Date: 2002-02-27 17:50:12
; Title: Lightmesh and rotation
; Description: Using lightmesh to fakelight a rotating, moving entity

Graphics3D 800, 600, 32, 2

    Dim lights(21,3)
    x# = 0
    y# = 0
    z# = 0
    
    ; setup the fakelight markers and place in array - obviously this could be done differently or with types
    count = 1
    For angle = 0 To 360 Step 36
            ob = CreateSphere()

            lights(count,0) = ob
            lights(count,1) = Rnd(30) ; red
            lights(count,2) = Rnd(30) ; green
            lights(count,3) = Rnd(30) ; blue                    
            
	; position lightmarkers
            EntityColor ob ,lights(count,1)*8,lights(count,2)*8,lights(count,3)*8
            EntityFX ob,1
            PositionEntity ob, Cos(angle)*50,30,Sin(angle)*50

            count = count + 1

    Next

    ; create mesh to be lit
    ship = CreateSphere(32)
    PositionEntity ship,0,-10,0
    ScaleEntity ship ,20,20,20
    RotateMesh ship,90,0,0
    EntityFX ship ,7 ; set flags for fullbright, vertex lighting, flatshaded
            
    camera = CreateCamera()
    CameraRange camera,1,5000
    PositionEntity camera , 0,100,0
    PointEntity camera,ship

    p2 = CreatePivot() ; this is used in lighting process below
    
    
    ; MAIN LOOP
    While Not KeyHit(1) 
    
    x# = (KeyDown(32))-(KeyDown(30)) + EntityX(ship)
    y# = (KeyDown(17))-(KeyDown(45)) + EntityZ(ship)
    
    ; move and turn the object to be lit
    PositionEntity ship,x,0,y    
    TurnEntity ship,1,1,1
        
        ;clear object's vertex color ready for the lightmapping below
        For n=1 To CountSurfaces(ship) 
            surf=GetSurface(ship,n) 
            For v=0 To CountVertices(surf)-1 
                VertexColor surf,v,0,0,0 
            Next 
        Next
    
        For count = 1 To 10 ; 10 lights   

	; get light's basic info
            light = lights(count,0)
            red = lights(count,1) 
            green = lights(count,2) 
            blue = lights(count,3) 
 
            xl# = EntityX(light)
            yl# = EntityY(light)
            zl# = EntityZ(light)
        
            yaw# = EntityYaw(ship)
            pitch# = EntityPitch(ship)
            roll# = EntityRoll(ship)    
        
            EntityParent p2,ship
            PositionEntity p2,xl,yl,zl,1 ; place the pivot at the light's global xyz
            RotateEntity ship,0,0,0 ; reset the meshes rotation
    
            x = EntityX(p2) ; get the pivot's new position
            y = EntityY(p2)
            z = EntityZ(p2)


	 ; do the business
	; a higher range value than 6 will make lights brighter.
	; for larger game worlds, can use something like entitydistance/3 
;	(along with the lights RGB values), to alter brightness
            LightMesh ship,red,green,blue,6,x,y,z   
        
            RotateEntity ship,pitch,yaw,roll ; reset the object's rotation
    
        Next ; next light

    UpdateWorld:    
    RenderWorld :
    Text 0,0,"use a,d,w,x to move sphere about"
    Flip

    Wend

Stop
