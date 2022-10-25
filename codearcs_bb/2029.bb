; ID: 2029
; Author: _33
; Date: 2007-06-02 02:46:53
; Title: MIRRORIZE v1.1
; Description: A 3D screensaver full of effects

AppTitle "MIRRORIZE v1.1"

	Select Left$(CommandLine$(),2)
		Case "/v"
			End 
		Case "/c"
            End
;		screensaver_config()
;	Default
;		screensaver()
	End Select

Graphics3D 800,600,32

Const FPS = 60                            ; frames per second

Global wait_fps = CreateTimer(FPS)        ; the timer is used to wait until we reach our
                                          ; desired frames per second.

; general purpose physics constants
Const GRAVITY# = .00981 * (100 / FPS)     ; adjusted with the frames per seconds
Const AIR_FRICTION# = 0.01
Const GROUND_FRICTION# = 0.0030

; for collision
Const projectile_type = 1
Const world_type      = 2
Const habitant_type   = 4

Global rox# = 0, rot# = 0, cubesize# = 0
Global dist# = 40

Dim sin_tb#(1440),cos_tb#(1440)
For i=0 To 1440: sin_tb#(i)=Sin(i): cos_tb#(i)=Cos(i): Next

Dim ptr_texture(0)
InitTextures()

cam = CreateCamera()
CameraProjMode cam,1
EntityRadius cam,2
EntityType cam,habitant_type

cam_out = CreateCamera()
CameraProjMode cam_out,1
EntityRadius cam_out,2
EntityType cam_out,habitant_type

ptr_copyscreen = CreateTexture(GraphicsWidth(), GraphicsHeight(), 256)

light1 = CreateLight(3)
LightColor light1, 255,0,0

light2 = CreateLight(3)
LightColor light2, 0,255,0

light3 = CreateLight(3)
LightColor light3, 0,0,255

light4 = CreateLight(3)
LightColor light4, 255,255,255
PositionEntity light4,0,0,0

obj = create_cube(32)
PositionEntity obj,0,0,dist
EntityShininess obj,0.75
EntityColor obj, 128,128,128
EntityAlpha obj,0.60

Global largeroom = create_cube(40)
ScaleEntity largeroom, dist# * 3, dist#, dist# * 3
FlipMesh largeroom
PositionEntity largeroom, 0,0,0
EntityColor largeroom,32,32,32
EntityType largeroom, world_type

Global poutre1 = CreateCylinder(12)
PositionEntity poutre1, dist#, 0, dist#
ScaleEntity poutre1, dist# * 0.125, dist# * 0.8, dist# * 0.125
EntityColor poutre1,32,32,32
EntityTexture poutre1, ptr_texture(11)
EntityType poutre1, world_type

Global poutre2 = CreateCylinder(12)
PositionEntity poutre2, -dist#, 0, dist#
ScaleEntity poutre2, dist# * 0.125, dist# * 0.8, dist# * 0.125
EntityColor poutre2,32,32,32
EntityTexture poutre2, ptr_texture(11)
EntityType poutre2, world_type

Global poutre3 = CreateCylinder(12)
PositionEntity poutre3, dist#, 0, -dist#
ScaleEntity poutre3, dist# * 0.125, dist# * 0.8, dist# * 0.125
EntityColor poutre3,32,32,32
EntityTexture poutre3, ptr_texture(11)
EntityType poutre3, world_type

Global poutre4 = CreateCylinder(12)
PositionEntity poutre4, -dist#, 0, -dist#
ScaleEntity poutre4, dist# * 0.125, dist# * 0.8, dist# * 0.125
EntityColor poutre4,32,32,32
EntityTexture poutre4, ptr_texture(11)
EntityType poutre4, world_type


Global spheer1 = CreateSphere(16)
PositionEntity spheer1,dist#,dist#,dist#
ScaleEntity spheer1,dist# * 0.25,dist# * 0.25,dist# * 0.25
EntityColor spheer1,32,32,32
EntityTexture spheer1, ptr_texture(1)
EntityType spheer1, world_type

Global spheer2 = CreateSphere(16)
PositionEntity spheer2,-dist#,dist#,dist#
ScaleEntity spheer2,dist# * 0.25,dist# * 0.25,dist# * 0.25
EntityColor spheer2,32,32,32
EntityTexture spheer2, ptr_texture(1)
EntityType spheer2, world_type

Global spheer3 = CreateSphere(16)
PositionEntity spheer3,-dist#,-dist#,dist#
ScaleEntity spheer3,dist# * 0.25,dist# * 0.25,dist# * 0.25
EntityColor spheer3,32,32,32
EntityTexture spheer3, ptr_texture(1)
EntityType spheer3, world_type

Global spheer4 = CreateSphere(16)
PositionEntity spheer4,-dist#,dist#,-dist#
ScaleEntity spheer4,dist# * 0.25,dist# * 0.25,dist# * 0.25
EntityColor spheer4,32,32,32
EntityTexture spheer4, ptr_texture(1)
EntityType spheer4, world_type

Global spheer5 = CreateSphere(16)
PositionEntity spheer5,dist#,dist#,-dist#
ScaleEntity spheer5,dist# * 0.25,dist# * 0.25,dist# * 0.25
EntityColor spheer5,32,32,32
EntityTexture spheer5, ptr_texture(1)
EntityType spheer5, world_type

Global spheer6 = CreateSphere(16)
PositionEntity spheer6,dist#,-dist#,dist#
ScaleEntity spheer6,dist# * 0.25,dist# * 0.25,dist# * 0.25
EntityColor spheer6,32,32,32
EntityTexture spheer6, ptr_texture(1)
EntityType spheer6, world_type

Global spheer7 = CreateSphere(16)
PositionEntity spheer7,-dist#,-dist#,-dist#
ScaleEntity spheer7,dist# * 0.25,dist# * 0.25,dist# * 0.25
EntityColor spheer7,32,32,32
EntityTexture spheer7, ptr_texture(1)
EntityType spheer7, world_type

Global spheer8 = CreateSphere(16)
PositionEntity spheer8,dist#,-dist#,-dist#
ScaleEntity spheer8,dist# * 0.25,dist# * 0.25,dist# * 0.25
EntityColor spheer8,32,32,32
EntityTexture spheer8, ptr_texture(1)
EntityType spheer8, world_type


flat1 = create_flat()
ScaleEntity flat1, 40,40,1
PositionEntity flat1, 0,0,dist# * 2.5
EntityTexture flat1, ptr_copyscreen

flat2 = create_flat()
ScaleEntity flat2, 40,40,1
PositionEntity flat2, -dist# * 2.5,0,0
EntityTexture flat2, ptr_copyscreen
RotateEntity flat2,0,90,0

flat3 = create_flat()
ScaleEntity flat3, 40,40,1
PositionEntity flat3, dist# * 2.5,0,0
EntityTexture flat3, ptr_copyscreen
RotateEntity flat3,0,-90,0

flat4 = create_flat()
ScaleEntity flat4, 40,40,1
PositionEntity flat4, 0,0,-dist# * 2.5
EntityTexture flat4, ptr_copyscreen
RotateEntity flat4,0,180,0


Global shooting_piv1 = CreatePivot ()
PositionEntity shooting_piv1,60,-38,40
Global shooting_piv2 = CreatePivot ()
PositionEntity shooting_piv2,60,-38,-40
Global shooting_piv3 = CreatePivot ()
PositionEntity shooting_piv3,-60,-38,40
Global shooting_piv4 = CreatePivot ()
PositionEntity shooting_piv4,-60,-38,-40


   Collisions projectile_type, world_type,2,2
   Collisions projectile_type, projectile_type, 1, 2
   Collisions projectile_type, habitant_type, 1, 2

   Collisions habitant_type, world_type, 2, 2

switch_frame = 1
rrange = 2

Global shot_timer = 0, shots = 0, wp = 0

Repeat

   If shots = 0 Then
      If Rand (1,500) = 200 Then
         shooting_point = 1
         shots = Rand(10,50)
         wp = Rand (1,5)
      EndIf
   Else
      If shot_timer = 0 Then
         Select shooting_point
            Case 1  fire_projectile(shooting_piv1)
            Case 2  fire_projectile(shooting_piv2)
            Case 3  fire_projectile(shooting_piv3)
            Case 4  fire_projectile(shooting_piv4)
         End Select
         shots = shots - 1
         shooting_point = shooting_point + 1 : If shooting_point > 4 Then shooting_point = 1
      EndIf
      shot_timer = shot_timer - 1
   EndIf


   object_handle()

   If switch_frame = Rand(1,3) Then
      show_barcode = Rand(0,1)
      If show_barcode = 1 Then
         SetBuffer TextureBuffer(ptr_copyscreen)
         display_barcode()
         SetBuffer BackBuffer()
      Else
         SetBuffer TextureBuffer(ptr_copyscreen)
         ClsColor 0,0,0
         Cls
         SetBuffer BackBuffer()

      EndIf
   EndIf
   If show_barcode = 0 Then
      CopyRect 0,0,GraphicsWidth(), GraphicsHeight(),-1,-1,BackBuffer(),TextureBuffer(ptr_copyscreen)
   EndIf

   Flip
   UpdateWorld
   WaitTimer wait_fps
   RenderWorld

   PositionEntity light1, sin_tb#(rot) * 20, 0, cos_tb#(rot) * 20 + dist# * 2
   PointEntity light1, obj

   PositionEntity light2, cos_tb#(rot) * 20, sin_tb#(rot) * 20, dist# * 2
   PointEntity light2, obj

   PositionEntity light3, sin_tb#(rot) * 20, cos_tb#(rot) * 20, sin_tb#(rot) * 20 + dist# * 2
   PointEntity light3, obj

   TurnEntity light4,0,-1,0

   If Rand (1,200) = 100 Then
      FreeEntity obj
      Select Rand (1,3)
         Case 1
            obj = create_cube(32)
            PositionEntity obj,0,sin_tb#(rot) * 20,0
         Case 2
            obj = CreateSphere(32)
            PositionEntity obj,0,cos_tb#(rot) * 20,0
         Case 3
            obj = CreateCylinder(32)
            PositionEntity obj,0,sin_tb#(rot) * 20,0
      End Select
      Select Rand(1,2)
         Case 1
            EntityColor obj, 128,128,128
         Case 2
            c1 = Rand (0,1) * 255
            c2 = Rand (0,1) * 255
            c3 = Rand (0,1) * 255
            EntityColor obj, c1,c2,c3
      End Select

      EntityShininess obj,0.75
      EntityAlpha obj,Rnd(0.5,1)

   EndIf

   PointEntity shooting_piv1, obj
   PointEntity shooting_piv2, obj
   PointEntity shooting_piv3, obj
   PointEntity shooting_piv4, obj


   ScaleEntity obj, Abs (sin_tb#(rot)) * cubesize#, Abs (cos_tb#(rot)) * cubesize#, Abs (sin_tb#(rot * 2)) * cubesize#
   TurnEntity obj,0,cos_tb#(rot),sin_tb#(rot * 3)

   rot# = (rot# + 1) : If rot# > 360.0 Then rot# = rot# - 360.0
   rox# = (rox# + 0.1) : If rox# > 360.0 Then rox# = rox# - 360.0


   cubesize# = Abs (sin_tb#(Int(rox# * 3))) * 15

   EntityColor obj, Abs(sin_tb#(rot * 3)) * 255,Abs(sin_tb#(rot * 3)) * 255,Abs(sin_tb#(rot * 3)) * 255


   Select cam1_shot
      Case 1
         PositionEntity cam, (cos_tb#(rot)) * dist# * 2, (sin_tb#(rot)) * dist# * 0.5, (sin_tb#(rot)) * dist# * 1.5

      Case 2
         PositionEntity cam, (sin_tb#(rot)) * dist# * 1.5, (cos_tb#(rot)) * dist#, (sin_tb#(rot * 2)) * dist# * 2
         PointEntity cam, obj
   End Select

   Select cam2_shot
      Case 1
         PositionEntity cam_out, (sin_tb#(rot)) * dist# * 0.5, (cos_tb#(rot)) * dist# * 0.5, (cos_tb#(rot)) * dist# * 2
      Case 2
         PositionEntity cam_out, (cos_tb#(rot * 2)) * dist# * 2, (sin_tb#(rot)) * dist#, (cos_tb#(rot)) * dist# * 1.5
         PointEntity cam_out, cam
   End Select

   ;************************************
   If Rand (1,rrange) = 2 Then 
      switch_frame = Rand(1,3)
      cam1_shot = Rand(1,2)
      cam2_shot = Rand(1,2)
      current_frame = switch_frame
   Else
      switch_frame = 0
   EndIf

   Select switch_frame
      Case 1
         peep_selection = Rand(1,4)
         Select peep_selection
            Case 3  peepy = 0 :peepdir = -2
            Case 2  peepx = GraphicsWidth() * 0.5 :peepdir = -2
            Case 1  peepy = GraphicsHeight() * 0.5 :peepdir = 2
            Case 4  peepx = 0 :peepdir = 2
         End Select
         rrange = Rand(50,500)
      Case 2
         Select Rand(1,2)
            Case 1
               CameraViewport cam_out,0,0,GraphicsWidth() * 0.5,GraphicsHeight()
               CameraViewport cam,GraphicsWidth() * 0.5,0,GraphicsWidth() * 0.5,GraphicsHeight()
            Case 2
               CameraViewport cam,0,0,GraphicsWidth() * 0.5,GraphicsHeight()
               CameraViewport cam_out,GraphicsWidth() * 0.5,0,GraphicsWidth() * 0.5,GraphicsHeight()
         End Select
         rrange = Rand(50,150)
      Case 3
         Select Rand(1,2)
            Case 1
               CameraViewport cam_out,0,0,GraphicsWidth(),GraphicsHeight() * 0.5
               CameraViewport cam,0,GraphicsHeight() * 0.5,GraphicsWidth(),GraphicsHeight() * 0.5
            Case 2
               CameraViewport cam,0,0,GraphicsWidth(),GraphicsHeight() * 0.5
               CameraViewport cam_out,0,GraphicsHeight() * 0.5,GraphicsWidth(),GraphicsHeight() * 0.5
         End Select
         rrange = Rand(50,150)
   End Select

   If current_frame = 1 Then
      CameraViewport cam,0,0,GraphicsWidth(),GraphicsHeight()

      If peep_selection = 1 Or peep_selection = 3 Then
         If peep_selection = 3 Then If peepx < 0 Then peepx = 0 :  peepdir = 2 : peep_selection = 4
         If peep_selection = 1 Then If peepx > (GraphicsWidth() * 0.5) Then peepx = GraphicsWidth() * 0.5 : peepdir = -2 : peep_selection = 2
         CameraViewport cam_out, peepx, peepy, GraphicsWidth() * 0.5, GraphicsHeight() * 0.5
         peepx = peepx + peepdir
      EndIf

      If peep_selection = 2 Or peep_selection = 4 Then
         If peep_selection = 2 Then If peepy < 0 Then peepy = 0 : peepdir = -2 : peep_selection = 3
         If peep_selection = 4 Then If peepy > (GraphicsWidth() * 0.5) Then peepy = GraphicsHeight() * 0.5 : peepdir = 2 : peep_selection = 1
         CameraViewport cam_out, peepx, peepy, GraphicsWidth() * 0.5, GraphicsHeight() * 0.5
         peepy = peepy + peepdir
      EndIf

   EndIf

   ;************************************


Until GetKey() Or MouseXSpeed() Or MouseYSpeed()

End


Function display_barcode()
   x# = GraphicsWidth() - 1
   y# = GraphicsHeight() - 1
   
   p1# = 0.66
   p2# = 0.09
   p3# = 0.25
   bw# = Int (x# / 7.0 + 0.5)
   
   Color 192,192,192 : Rect 0                       , 0                , bw#                           , y# * p1#
   Color 255,255,0   : Rect bw#                     , 0                , bw#                           , y# * p1#
   Color 0,255,255   : Rect bw# * 2                 , 0                , bw#                           , y# * p1#
   Color 0,255,0     : Rect bw# * 3                 , 0                , bw#                           , y# * p1#
   Color 255,0,255   : Rect bw# * 4                 , 0                , bw#                           , y# * p1#
   Color 255,0,0     : Rect bw# * 5                 , 0                , bw#                           , y# * p1#
   Color 0,0,255     : Rect bw# * 6                 , 0                , bw#                           , y# * p1#

   Color 0,0,255     : Rect 0                       , y# * p1#         , bw#                           , y# * p2#
   Color 0,0,0       : Rect bw#                     , y# * p1#         , bw#                           , y# * p2#
   Color 255,0,255   : Rect bw# * 2                 , y# * p1#         , bw#                           , y# * p2#
   Color 0,0,0       : Rect bw# * 3                 , y# * p1#         , bw#                           , y# * p2#
   Color 0,255,255   : Rect bw# * 4                 , y# * p1#         , bw#                           , y# * p2#
   Color 0,0,0       : Rect bw# * 5                 , y# * p1#         , bw#                           , y# * p2#
   Color 192,192,192 : Rect bw# * 6                 , y# * p1#         , bw#                           , y# * p2#

   Color 32,64,96    : Rect 0                       , y# * (p1# + p2#) , x# * 0.18 + 1                 , y# * p3# + 1
   Color 255,255,255 : Rect x# * 0.18               , y# * (p1# + p2#) , x# * 0.18 + 1                 , y# * p3# + 1
   Color 64,0,128    : Rect x# * 0.18 * 2           , y# * (p1# + p2#) , x# * 0.18 + 1                 , y# * p3# + 1
   Color 0,0,0       : Rect x# * 0.18 * 3           , y# * (p1# + p2#) , bw# * 2                       , y# * p3# + 1
   Color 32,32,32    : Rect x# * 0.18 * 3 + 2 * bw# , y# * (p1# + p2#) , bw# * 0.25                    , y# * p3# + 1
   Color 0,0,0       : Rect bw# * 6                 , y# * (p1# + p2#) , bw#                           , y# * p3# + 1


End Function


;----------------------------------------------------------------------------------------
; Object management routines
;----------------------------------------------------------------------------------------
; regroups "maze_info" and "projectile"
Type object_info
   Field object_type        ; 1 = 3D model, 2 = texture
   Field number             ; object position in list of objects (1 to 9 are projectiles) 
   Field ptr                ; this is where we point to the 3D entity
   Field brush              ; points to the brush data

   Field xpos#, ypos#, zpos#
   Field oldx#, oldy#, oldz#; this is used for the rotation of the object (if applicable)
   Field mx, my             ; maze level coordinates

   Field cycle_timer        ; this is used for example to have a rotating object
   Field cycle_increment    ; this is the cycle incrementation value (- or + values work)
   Field cycle_reset        ; this is the cycle reset point
   Field cycle_start        ; this is the cycle start point

   Field life               ; this is the life counter of the bullet in # of frames
                            ;       (ex. 2000 = 40 seconds If life_increment is -1)
   Field life_increment     ; this is used in conjunction with "\life"
                            ;       (use this value as 0 to have infinite life)
   Field life_fade          ; 0 = no fade, 1 = fade in, 2 = fade out, 3 = fade in / out


   Field rotate             ; indicates roughly how the object rotates
                            ;       0 = no rotation
                            ;       1 = full rotate on movement
                            ;       2 = horizontal on movement
                            ;       3 = vertical on movement
                            ;       4 = horizontal on cycle
                            ;       5 = vertical on cycle
                            ;       6 = orbiting cycle ( orbits around xpos# and zpos#)

                            ; ************************ Physics engine / if applicable
   Field use_physics_engine ; 1 = YES, 0 = NO
   Field radius#            ; radius of the object (for collision detection)
   Field Mass#              ; mass of the object
   Field size#              ; size of the object
   Field vx#,vy#,vz#        ; force vectors of the object
   Field Velocity#          ; sum of all velocities

End Type



;----------------------------------------------------------------------------------------
Function fire_projectile(ptr_shooter)
Local xx#, xy#, xz#

   Projectile_in_action = wp

   ; weapon has not yet recharged, so return
  ; If shot_timer > 0 Then Return

   ; everything is fine, emit the projectile
   b.object_info = summon_object.object_info (Projectile_in_action)

   If Projectile_in_action = 1 Then            ; this is the Handball
      shot_timer = 15

   ElseIf Projectile_in_action = 2 Then        ; this is the Poolball
      shot_timer = 35

   ElseIf Projectile_in_action = 3 Then        ; this is the Rubberdisc
      shot_timer = 20

   ElseIf Projectile_in_action = 4 Then        ; this is the Beachball
      shot_timer = 50

   ElseIf Projectile_in_action = 5 Then        ; this is the Amigaball
      shot_timer = 20

   EndIf

   p_piv = CreatePivot()
   PositionEntity p_piv, EntityX(ptr_shooter), EntityY(ptr_shooter), EntityZ(ptr_shooter)
   RotateEntity p_piv, EntityPitch(ptr_shooter), EntityYaw(ptr_shooter), EntityRoll(ptr_shooter)
   MoveEntity p_piv,0,1,5

   PositionEntity b\ptr, EntityX(ptr_shooter), EntityY(ptr_shooter), EntityZ(ptr_shooter)
   EntityType b\ptr,projectile_type

   vectx# = EntityX(p_piv) - EntityX(ptr_shooter)
   vecty# = EntityY(p_piv) - EntityY(ptr_shooter)
   vectz# = EntityZ(p_piv) - EntityZ(ptr_shooter)
   TFormVector vectx#, vecty#, vectz#, p_piv, ptr_camera

   b\Vx# = TFormedX()
   b\Vz# = TFormedZ()
   b\Vy# = TFormedY()

   If  Projectile_in_action = 5 Then
      AlignToVector b\ptr, b\Vx#, b\Vy#, b\Vz#, 1, 1
   Else
      AlignToVector b\ptr, b\Vx#, b\Vy#, b\Vz#, 2, 1
   EndIf

   b\Velocity# = Sqr(b\Vx#^2 + b\Vy#^2 + b\Vz#^2)

   FreeEntity p_piv

End Function 




Function summon_object.object_info (ob_number, use_physics = True, use_life = True)
   x.object_info = New object_info
   x\number = ob_number

   Select ob_number
   Case 1            ; this is the Handball
      x\radius# = 0.25
      x\size# = x\radius# * 2
      x\ptr = CreateSphere(5)
      x\use_physics_engine = use_physics
      x\Mass# = 0.5
      x\rotate = 1
      x\life = 2000
      x\life_increment = -use_life
      ScaleMesh  x\ptr, x\size#, x\size#, x\size#
      EntityRadius x\ptr, x\radius# * 2
      x\brush=CreateBrush()
      BrushTexture x\brush,ptr_texture(23)
      BrushColor x\brush,255,255,255
      PaintEntity x\ptr, x\brush 

   Case 2        ; this is the Poolball
      x\radius# = 1
      x\size# = x\radius# * 2
      x\ptr = CreateSphere(7)
      x\use_physics_engine = True
      x\Mass# = 1.1
      x\rotate = 1
      x\life = 2000
      x\life_increment = -use_life
      ScaleMesh  x\ptr, x\size#, x\size#, x\size#
      EntityRadius x\ptr, x\radius# * 2
      x\brush=CreateBrush()
      BrushTexture x\brush,ptr_texture(22)
      BrushColor x\brush,255,255,255 
      PaintEntity x\ptr, x\brush 

   Case 3        ; this is the Rubberdisc
      x\radius# = 0.5
      x\size# = x\radius# * 2
      x\ptr = CreateCylinder(10) 
      x\use_physics_engine = True
      x\Mass# = 2
      x\rotate = 2
      x\life = 1000
      x\life_increment = -use_life
      ScaleMesh  x\ptr, x\size#, x\size#/5, x\size#
      EntityRadius x\ptr, x\radius# / 2.5, x\size#
      x\brush=CreateBrush()
      BrushTexture x\brush, ptr_texture(25)
      BrushColor x\brush,255,255,255
      PaintEntity x\ptr, x\brush 

   Case 4        ; this is the Beachball
      x\radius# = 2.25
      x\size# = x\radius# * 2
      x\ptr = CreateSphere(10)
      x\use_physics_engine = True
      x\Mass# = 2.5
      x\rotate = 1
      x\life = 2000
      x\life_increment = -use_life
      ScaleMesh  x\ptr, x\size#, x\size#, x\size#
      EntityRadius x\ptr, x\radius# * 2
      x\brush=CreateBrush()
      BrushTexture x\brush,ptr_texture(24)
      BrushColor x\brush,255,255,255
      PaintEntity x\ptr, x\brush 

   Case 5        ; this is the Amigaball
      x\radius# = 0.5
      x\size# = x\radius# * 2
      x\ptr = CreateSphere(6)
      x\use_physics_engine = True 
      x\Mass# = 2.5
      x\rotate = 1
      x\life = 2000
      x\life_increment = -use_life
      ScaleMesh  x\ptr, x\size#, x\size#, x\size#
      EntityRadius x\ptr, x\radius# * 2
      x\brush=CreateBrush()
      BrushTexture x\brush,ptr_texture(21)
      BrushColor x\brush,255,255,255 
      PaintEntity x\ptr, x\brush 

   End Select

   Return x.object_info

End Function



Function object_handle()
Local Hit_Habitants, Hit_World, Hit_Projectiles

   projectilecount = 0

   For b.object_info = Each object_info
      If b\ptr = 0 Then object_deleted = True Else object_deleted = False
      If b.object_info = Null Then object_deleted = True

      If Not object_deleted Then
         Hit_World       = EntityCollided(b\ptr, world_type) 
         Hit_Projectiles = EntityCollided(b\ptr, projectile_type)
         Hit_Habitants   = EntityCollided(b\ptr, habitant_type) 
         If b\number < 10 And b\number > 0 Then projectilecount = projectilecount + 1

         b\OldX# = b\xpos#
         b\OldY# = b\ypos#
         b\OldZ# = b\zpos#
         If b\use_physics_engine <> False Then
            If Hit_Projectiles Then
               b.object_info = collided_with.object_info (b.object_info, Hit_Projectiles)
               b.object_info = apply_physics.object_info (b.object_info, Hit_Habitants, Hit_World, Hit_Projectiles)
            Else
               b.object_info = apply_physics.object_info (b.object_info, Hit_Habitants, Hit_World, Hit_Projectiles)
            EndIf
         EndIf

      EndIf

      If Not object_deleted Then
         b\xpos# = EntityX#(b\ptr, True) 
         b\ypos# = EntityY#(b\ptr, True) 
         b\zpos# = EntityZ#(b\ptr, True)
         XAngleAdjust# = ((b\xpos# - b\OldX#) / b\radius#) * (90.0 / Pi)
         YAngleAdjust# = ((b\ypos# - b\OldY#) / b\radius#) * (90.0 / Pi)
         ZAngleAdjust# = ((b\zpos# - b\OldZ#) / b\radius#) * (90.0 / Pi)

         If b\rotate = 1 Then
            TurnEntity b\ptr, ZAngleAdjust#, 0, -XAngleAdjust#, True
         ElseIf b\rotate = 2 Then 
            TurnEntity b\ptr, 0, ZAngleAdjust# - XAngleAdjust#, 0, True
         EndIf

         If b\life_increment <> 0 Then
            b\life = b\life + b\life_increment
            If b\life = 0
               FreeEntity b\ptr
               FreeBrush b\brush
               Delete b.object_info
            ElseIf b\life < 50 Then
               EntityAlpha b\ptr,b\life * 0.02
            EndIf
         EndIf

      EndIf

   Next

End Function 


Function collided_with.object_info(x.object_info, Entity_Hit )
  ; If x.object_info = Null Then Return Null

   For b.object_info = Each object_info
      If b\use_physics_engine = False Then b\use_physics_engine = True
      If b\life_increment = False Then  b\life_increment = -1

      If b\ptr = Entity_Hit Then
         xx# = x\Vx#
         xy# = x\Vy#
         xz# = x\Vz#
         Velocity# = x\Velocity#

         x\Velocity# = GROUND_FRICTION# * b\Velocity# / x\Mass#
         x\Vx# = (xx# - b\Velocity#)
         x\Vy# = (xy# - b\Velocity#)
         x\Vz# = (xz# - b\Velocity#)

         b\Velocity# = GROUND_FRICTION# * Velocity# / b\Mass#
         b\Vx# = (b\Vx# - x\Velocity#)
         b\Vy# = (b\Vy# - x\Velocity#)
         b\Vz# = (b\Vz# - x\Velocity#)

         Return x.object_info
      EndIf
   Next

   Return x.object_info
End Function


Function apply_physics.object_info(x.object_info, Hit_Habitants, Hit_World, Hit_Projectiles)
      Local Nx#, Ny#, Nz#, NFx#, NFy#, NFz#, VdotN#, Entity_Hit

    ;  If x.object_info = Null Then Return Null

      If Hit_World Or Hit_Habitants Or Hit_Projectiles Then
         Entity_Hit = 1
      Else
         Entity_Hit = 0
      EndIf

      If x\Velocity# > 0 ; Calculate the direction vector. The direction vector has a length of 1. 

         Direction_X# = x\Vx# / x\Velocity#
         Direction_Y# = x\Vy# / x\Velocity#
         Direction_Z# = x\Vz# / x\Velocity#

         ; Compute air friction. ; Air friction is dependent on the speed of the entity, and will prevent it from accelerting forever. 
         x\Velocity# = x\Velocity# - (AIR_FRICTION# * x\Velocity# * x\size# / x\Mass#)

         If (x\Velocity# < 0) Then x\Velocity# = 0

         ; Convert the entity's velocity and direction back into a motion vector.
         x\Vx# = Direction_X# * x\Velocity#
         x\Vy# = Direction_Y# * x\Velocity#
         x\Vz# = Direction_Z# * x\Velocity#

         ; If the entity collided with the level, apply ground friction. 
         If Entity_Hit > 0 ; Compute ground friction. Ground friction is not dependent on the speed of the entity. 
            x\Velocity# = x\Velocity# - (GROUND_FRICTION# * x\Velocity# * x\size# / x\Mass#)
         EndIf 

         ; If the entity collided with the level, make it bounce. 
         If Entity_Hit > 0 Then
            ; Calculate bounce: 
            ; Get the normal of the surface which the entity collided with. 
            Nx# = CollisionNX(x\ptr, 1)
            Ny# = CollisionNY(x\ptr, 1)
            Nz# = CollisionNZ(x\ptr, 1)
            ; Compute the dot product of the entity's motion vector and the normal of the surface collided with. 
            VdotN# = (x\Vx# * Nx# + x\Vy# * Ny# + x\Vz# * Nz#)

            ; Calculate the normal force.
            NFx# = -2.0 * Nx# * VdotN#
            NFy# = -2.0 * Ny# * VdotN#
            NFz# = -2.0 * Nz# * VdotN#

            x\Vx# = x\Vx# + NFx#
            x\Vy# = x\Vy# + NFy#
            x\Vz# = x\Vz# + NFz#

         EndIf 

      EndIf 

      ; Apply gravity:
      If x\use_physics_engine = 1 Then x\Vy# = x\Vy# - GRAVITY# * x\Mass#

      TranslateEntity x\ptr, x\Vx#, x\Vy#, x\Vz#, True

      Return x.object_info
End Function



;----------------------------------------------------------------------------------------
; Setup the textures
;----------------------------------------------------------------------------------------

Function InitTextures()
   Local i,j,x,y,x2,y2,xf#,yf#,r,g,b,r2,g2,b2,texture_number,number_of_textures,number_of_funcs,func$

   Restore texture_data
   Read number_of_textures

   Dim ptr_texture(number_of_textures)

   For i = 1 To number_of_textures
      Read texture_number
      Read number_of_funcs

      For j = 1 To number_of_funcs

         Read func$

         Select Lower$(func$)

         Case "tex"
            Read x,y
            ptr_texture(texture_number) = CreateTexture (x, y, 1 + 2 + 256)
            SetBuffer TextureBuffer (ptr_texture(texture_number))

         Case "fnt"
            Read fn$, x, y, start_char, end_char
            ptr_fnt = LoadFont(fn$, 85, True, False, False)
            SetFont ptr_fnt
            ClsColor 255,0,0
            l = (end_char - start_char) + i
            ch_offset = i - start_char
            For k = i To l
               ptr_texture(i) = CreateTexture (x, y, 256)
               SetBuffer TextureBuffer (ptr_texture(i))
               Cls
               Color 0, 255, 0
               Text 31,29,Chr$(ch_offset + k),True,True
               i = i + 1
            Next
            FreeFont ptr_fnt

         Case "color"
            Read r,g,b
            Color r,g,b

         Case "fcolor"
            Read r,g,b
            ClsColor r,g,b

         Case "fill"
            Read x1,y1,x2,y2
            Rect x1,y1,x2,y2,1

         Case "rect"
            Read x1,y1,x2,y2
            Rect x1,y1,x2,y2,0

         Case "oval"
            Read x1,y1,x2,y2
            Oval x1,y1,x2,y2

         Case "scale"
            Read xf#,yf#
            ScaleTexture ptr_texture(texture_number),xf#,yf#

         Case "gradient"
            Read r,g,b,x,y,r2,g2,b2,x2,y2
            make_gradient(r,g,b,x,y,r2,g2,b2,x2,y2)

         End Select

      Next

   Next

   ptr_texture(22) = create_pox_tex    (000,255,255,000,000,255,.25,.25)
   ptr_texture(23) = create_stripe_tex (2,.25,.25)
   ptr_texture(24) = create_stripe_tex (1,.5,.5)

End Function


Function clear_all_textures()
   For i = 1 To number_of_textures
      FreeEntity ptr_texture(texture_number)
   Next

End Function


Function make_gradient(r1, g1, b1, x1, y1, r2, g2, b2, x2, y2)
   Return
End Function


Function create_pox_tex(red1, green1, blue1, red2, green2, blue2, scale_u#, scale_v#) 
   texture_handle = CreateTexture(32,32,256) 
   SetBuffer TextureBuffer(texture_handle) 
   Color red1,green1,blue1 
   Rect 0,0,32,32
   Color red2,green2,blue2 
   Oval 0,0,16,16,1 
   Oval 16,16,16,16,1 
   ScaleTexture texture_handle, scale_u#, scale_v# 
   Return texture_handle 

End Function 


Function create_stripe_tex(direction,scale_u#,scale_v#)
   If direction = 1 Then h = 1: v = 0
   If direction = 2 Then h = 0: v = 1

   texture_handle = CreateTexture(32,32,256) 
   SetBuffer TextureBuffer(texture_handle)
   Color 255,255,0 
   Rect 0,0,32,32
   Color 0,255,0 
   Rect 8 * h, 8 * v, 32 * v + 8 * h, 8 * v + 32 * h
   Color 255,0,0 
   Rect 16 * h, 16 * v, 32 * v + 8 * h, 8 * v + 32 * h
   Color 0,0,255 
   Rect 24 * h, 24 * v, 32 * v + 8 * h, 8 * v + 32 * h
   ScaleTexture texture_handle,scale_u#,scale_v# 
   Return texture_handle 

End Function 


;----------------------------------------------------------------------------------------
.texture_data
;----------------------------------------------------------------------------------------
Data 30
Data 1, 6,"tex",64,64,"color",64,128,255,"fill",0,0,64,64,"color",255,128,64,"fill",0,0,32,32,"fill",32,32,32,32
Data 2, 6,"tex",64,64,"color",255,0,0,"fill",0,0,64,64,"color",0,255,0,"fill",0,0,32,32,"fill",32,32,32,32
Data 3, 6,"tex",64,64,"color",0,0,255,"fill",0,0,64,64,"color",255,255,0,"fill",0,0,32,32,"fill",32,32,32,32
Data 4, 6,"tex",64,64,"color",255,0,255,"fill",0,0,64,64,"color",0,255,255,"fill",0,0,32,32,"fill",32,32,32,32
Data 5, 5,"tex",64,64,"color",128,64,255,"fill",0,0,64,64,"color",255,128,64,"oval",0,0,64,64
Data 6, 5,"tex",64,64,"color",128,255,64,"fill",0,0,64,64,"color",128,64,255,"oval",0,0,64,64
Data 7, 5,"tex",64,64,"color",0,255,0,"fill",0,0,64,64,"color",255,0,0,"oval",0,0,64,64
Data 8, 5,"tex",64,64,"color",0,0,255,"fill",0,0,64,64,"color",0,255,0,"oval",0,0,64,64
Data 9, 8,"tex",64,64,"color",255,255,255,"fill",0,0,64,64,"color",128,128,128,"fill",0,0,32,32,"color",64,64,64,"fill",32,32,32,32,"scale",2,2
Data 10,1,"tex",64,64
Data 11,7,"tex",64,64,"color",128,160,192,"fill",0,0,64,64,"color",96,128,160,"fill",0,0,32,32,"fill",32,32,32,32,"scale",1,0.1
Data 12,8,"tex",64,64,"color",255,192,160,"fill",0,0,64,64,"color",0,128,255,"rect",0,0,64,64,"rect",1,1,63,63,"rect",2,1,62,63,"scale",1,0.1
Data 13,0
Data 14,0
Data 15,0
Data 16,0
Data 17,2,"tex",64,64,"gradient",0,0,0,0,0,255,255,255,64,64
Data 18,0
Data 19,0
Data 20,0
Data 21,7,"tex",32,32,"color",255,255,255,"fill",0,0,32,32,"color",224,0,0,"fill",0,0,16,16,"fill",16,16,16,16,"scale",.166667,.333334
Data 22,0
Data 23,0
Data 24,0
Data 25,6,"tex",32,32,"color",0,255,0,"fill",0,0,32,32,"color",255,0,255,"fill",0,0,16,16,"fill",16,16,16,16
Data 26,0
Data 27,0
Data 28,0
Data 29,0
Data 30,0
Data 31,0
Data 32,1,"fnt","Courrier New",64,64,32,96

;----------------------------------------------------------------------------------------
; Following are a collection of segment handling routines
;----------------------------------------------------------------------------------------
Function create_cube(segs=1,parent=0)
   mesh=CreateMesh( parent )
   For scnt=0 To 3
      surf=CreateSurface( mesh )
      stx#=-.5
      sty#=stx
      stp#=Float(1)/Float(segs)
      y#=sty
      For a=0 To segs
         x#=stx
         v#=a/Float(segs)
         For b=0 To segs
            u#=b/Float(segs)
            AddVertex(surf,x,y,0.5,u,v)
            x=x+stp
         Next
         y=y+stp
      Next
      For a=0 To segs-1
         For b=0 To segs-1
            v0 = a * (segs + 1) + b
            v1 = v0 + 1
            v2 = (a + 1) * (segs + 1) +  b + 1
            v3 = v2 - 1
            AddTriangle( surf, v0, v1, v2 )
            AddTriangle( surf, v0, v2, v3 )
         Next
      Next
      RotateMesh mesh,0,90,0
   Next
   ;top and bottom
   RotateMesh mesh,90,0,0
   For scnt=0 To 1
      surf=CreateSurface( mesh )
      stx#=-.5
      sty#=stx
      stp#=Float(1)/Float(segs)
      y#=sty
      For a=0 To segs
         x#=stx
         v#=a/Float(segs)
         For b=0 To segs
            u#=b/Float(segs)
            AddVertex(surf,x,y,0.5,u,v)
            x=x+stp
         Next
         y=y+stp
      Next
      For a=0 To segs-1
         For b=0 To segs-1
            v0=a*(segs+1)+b:v1=v0+1
            v2=(a+1)*(segs+1)+b+1:v3=v2-1
            AddTriangle( surf,v0,v1,v2 )
            AddTriangle( surf,v0,v2,v3 )
         Next
      Next
      RotateMesh mesh,180,0,0
   Next

   RotateMesh mesh,90,0,0
   ScaleMesh mesh,2,2,2
   UpdateNormals mesh
   Return mesh
End Function


Function create_flat(segs=1,parent=0)
   mesh=CreateMesh( parent )

   surf=CreateSurface( mesh )
   stx#=-.5
   sty#=stx
   stp#=Float(1)/Float(segs)
   y#=sty
   For a=0 To segs
      x#=stx
      v#=a/Float(segs)
      For b=0 To segs
         u#=b/Float(segs)
         AddVertex(surf,x,y,0.5,u,v)
         x=x+stp
      Next
      y=y+stp
   Next
   For a=0 To segs-1
      For b=0 To segs-1
         v0=a*(segs+1)+b:v1=v0+1
         v2=(a+1)*(segs+1)+b+1:v3=v2-1
         AddTriangle( surf,v0,v1,v2 )
         AddTriangle( surf,v0,v2,v3 )
      Next
   Next

   RotateMesh mesh,180,0,0
   ScaleMesh mesh,2,2,2
   UpdateNormals mesh
   Return mesh

End Function
