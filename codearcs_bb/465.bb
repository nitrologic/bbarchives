; ID: 465
; Author: Ziltch
; Date: 2002-10-18 07:16:06
; Title: Displacement Mapping
; Description: Using a texture to modify the height of a mesh's polygons.

; Vetex displacment mapping function and example code
; ADAmor Ziltch 2002

Graphics3D 800,600
SetBuffer BackBuffer()

light=CreateLight(2)
PositionEntity light,-1000,50,-10
LightColor     light,255,255,40
light2=CreateLight(2)
PositionEntity light2,1000,50,-10
LightColor     light,55,55,255
light3=CreateLight(2)
PositionEntity light2,0,50,1000
LightColor     light,220,210,55
AmbientLight   125,125,7

Global you = CreatePivot()
PositionEntity you,0,00,-300
cam = CreateCamera(you)
camerarange    cam,1,5000

.createscene

;--cube grid
Global cubegrid = createsquare(51)
PositionEntity cubegrid,0,300,1000
ScaleEntity    cubegrid,2000,200,2000
entitycolor    cubegrid,50,50,150
rotateEntity   cubegrid ,180,0,0

cubegridtex =  gridtex(256,32,12, 70,230,10, 120,50,250)
EntityTexture  cubegrid,cubegridtex,0,1

;--bump plane
Global bumpplane = createsquare(100)
PositionEntity bumpplane ,0,-500,1000
ScaleEntity    bumpplane ,2000,200,2000
entitycolor    bumpplane ,150,50,150

bumptex=CreateTexture(256,256)
spot(bumptex,256,25)
EntityTexture bumpplane,bumptex,0,0

;--bump ball
Global bumpball = createsphere(30)
PositionEntity bumpball ,0,0,1000
ScaleEntity    bumpball ,90,90,90
entitycolor    bumpball ,150,50,150

entityTexture bumpball,cubegridtex,0,0

PointEntity you,cubegrid
rotateentity you,15,0,0

setfont LoadFont("Arial",14,False,False,False)

BumpAlready = false
While Not KeyHit(1)

    spd =  MouseZ()+2  ; mousewheel is speed
    moveyou(spd)

    If KeyHit(57)then
		  if (not BumpAlready) Then   ;space for bumpmap
        displace(cubegrid,cubegridtex,256,20)
        displace(bumpplane,bumptex,256,20)
        displace(bumpball,cubegridtex,256,5)
        BumpAlready = true
      else
  		  freeentity cubegrid
	  	  freeentity bumpplane
  		  freeentity bumpball
  		  goto createscene
      end if
    end if

    TurnEntity cubegrid,0,.1,0
    TurnEntity bumpball,-.1,.1,.1
    UpdateNormals cubegrid
 
    RenderWorld
    color 250,250,100
    text 10,10," Hit the SPACEBAR for displacment. Arrows,home,end,endpgup,pgdwn with ctrl for movement. Mouse wheel for speed.  W for Wireframe.  Look in code for more keys"
    Flip
Wend


Function displace(mesh,texture,texsize#=256,amp#=1,wrap=false)

  For sc = 1 To CountSurfaces(mesh)

    surf = GetSurface(mesh,sc)
    If surf = 0 Then
      RuntimeError "Cant find surface to displace with"
      End
    End If
    maxvert = CountVertices(surf)

    For vc = 0 To maxvert-1

       bx#  = VertexX(surf,vc)
       by#  = VertexY(surf,vc)
       bz#  = VertexZ(surf,vc)
       bnx# = VertexNX(surf,vc)
       bny# = VertexNY(surf,vc)
       bnz# = VertexNZ(surf,vc)
       bu#  = VertexU(surf,vc)
       bv#  = VertexV(surf,vc)
       
       If wrap Then
         If (bu = 1)  Then bu = 0
         If (bv =0)  Then bu = 0 : bv = 0
         If (bv =1)  Then bu = 0 : bv = 1
       End If
       
       tx# = bu*texsize
       ty# = bv*texsize
       gettexcol(Texture,tx,ty)
       Cr#=numcolR      ; based off red channel  as this seems to look best

       If (cr > 0) Then
         bxx# = bx + bnx * (Cr/255) * (amp/10)
         byy# = by + bny * (Cr/255) * (amp/10)
         bzz# = bz + bnz * (Cr/255) * (amp/10)
         VertexCoords surf,vc,bxx,byy,bzz
       End If
    Next
  Next
  UpdateNormals mesh
End Function

Global numcolR#,numcolG,numcolB
Function numcolor(num#)
;convert number to r g b values
  numcolR=num  Shr 16 And %11111111
  numcolG=num Shr 8 And %11111111
  numcolB=num And %11111111
End Function

Function gettexcol(tex,x,y)
; get results from numcolR, numcolG, numcolB
  SetBuffer TextureBuffer(tex)
  LockBuffer TextureBuffer(tex)
  numcolor(ReadPixelFast(x,y))
  UnlockBuffer TextureBuffer(tex)
  SetBuffer BackBuffer()
End Function




;------ VVVV  these functions are just to create an example bdisplacement map    VVVV

Function createsquare(segs#=5,parent=0)

    mesh=CreateMesh( parent )
    surf=CreateSurface( mesh )

    l# =-.5
    b# = -.5
    tvc= 0

		;create all the vertices first
    Repeat
		  u# = l + .5
		  v# = b + .5
      AddVertex surf,l,0,b,u,v
      tvc=tvc + 1
      l = l + 1/segs
      If l > .5 Then
        l = -.5
        b = b + 1/segs
      End If
    Until b > .5

    vc# =0

    ;create polys
    vc# =0
    Repeat

      AddTriangle (surf,vc,vc+segs+1,vc+segs+2)
      AddTriangle (surf,vc,vc+segs+2,vc+1)

      vc = vc + 1
      tst# =  ((vc+1) /(segs+1)) -Int ((vc+1) /(segs+1))

      If (vc > 0) And (tst=0) Then
        vc = vc + 1
      End If

    Until vc=>tvc-segs-1
    UpdateNormals mesh
    Return mesh

End Function


Function moveyou(spd=1)

    If  KeyDown( 205 ) Then
      If KeyDown(157) Then
        MoveEntity you,spd,0,0    ; ctrl -> straff right
      Else 
       TurnEntity you,0,-2,0      ; -> turn right
      End If
    End If
    If  KeyDown( 203 ) Then
      If KeyDown(157) Then
        MoveEntity you,-spd,0,0   ; ctrl <- straff left
      Else 
        TurnEntity you,0,2,0      ; <- turn left
      End If
    End If
    If  KeyDown( 199 ) Then
      If KeyDown(157) Then
        TurnEntity you,0,0,2      ; ctrl home roll left
      Else
        TurnEntity you,-2,0,0     ; home  pitch left
      End If
    End If

    If  KeyDown( 207 ) Then 
      If KeyDown(157) Then
        TurnEntity you,0,0,-2     ; ctrl end roll right
      Else
        TurnEntity you,2,0,0      ; end roll right
      End If
    End If

    If  MouseDown(1) Or KeyDown( 200 )  Then MoveEntity you,0,0,spd  ; up arrow forward
    If MouseDown(2) Or KeyDown( 208 ) Then MoveEntity you,0,0,-spd   ; down arrow back
    If KeyDown(201) Then MoveEntity you,0,spd,0  ; pgup raise
    If KeyDown(209) Then MoveEntity you,0,-spd,0 ; pgdown lower

    If KeyHit( 17) Then wf = Not wf : WireFrame wf  ;w for wireframe

    If KeyHit(68) Then  ; F10 for snapshot
       If sscnt = 0 Then sscnt = 1000
       sscnt = sscnt + 1
       SaveBuffer(FrontBuffer(),"snapshot"+Right(Str(sscnt),3)+".bmp")
    End If

    If KeyHit(36) Then joy = Not joy   ; j key for joystick
    If joy Then
      jyaw#=-JoyXDir()
      jpitch#=-JoyYDir()
      TurnEntity you,jpitch,jyaw,0
      If JoyDown(7) Then MoveEntity you,0,0,spd
      If JoyDown(8) Then MoveEntity you,0,0,-spd

    End If
    
End Function
Global WF,joy


Function  spot(tex,texsize,numspots,clstrue=true)

	SetBuffer  TextureBuffer(tex)

  if clstrue then
	  color 0,0,0
    Rect 0,0,texsize,texsize,1
  end if
	
	lockbuffer TextureBuffer(tex)
  for sc = 1 to numspots
    spotsize=Rand(25,64): if spotsize >60 then spotsize=spotsize*2
    rx=Rand(spotsize+1,texsize-spotsize)
    ry=Rand(spotsize+1,texsize-spotsize)
    For a# = 1 To spotsize-1 step 1
		  i# = 0
			while i < 360
			  sx=sin(i)*a+rx
			  sy=cos(i)*a+ry
				i = i + .5
			  rc=readpixel(sx,sy,texturebuffer(tex))
			
  			numcolor(rc)
  		  nr#=(abs (sin((a-1)/4))*(spotsize-a)*2+numcolR*.9)mod 255

        nc=Colornum(nr,numcolG+1,numcolB+1)
        writepixelfast sx,sy,nc
      wend

    Next
	next

	unlockbuffer TextureBuffer(tex)
	SetBuffer BackBuffer()
	
End Function



Function colornum(r,g,b)
 return ((r Shl 16) + (g Shl 8) + b)
End Function



Function gridtex(s=256,st=128,width=16,colr=0,colg=0,colb=0,backcolr=0,backcolg=0,backcolb=0)
  tex=CreateTexture(s,s)
  SetBuffer TextureBuffer(tex)
  Color backcolr,backcolg,backcolb
  Rect 0,0,s,s,1
  a = 0
  i#=s/260
  Repeat
    Color colr,colg,colb
    For w= 0 To width-1
      Line a+w,0,a+w,s
      Line 0,a+w,s,a+w
    Next
    a = a + st
  Until a => s
  SetBuffer BackBuffer()
  Return tex

End Function
