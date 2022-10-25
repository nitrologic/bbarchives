; ID: 3198
; Author: Matty
; Date: 2015-04-04 09:10:37
; Title: Explosion Generator (non interactive)
; Description: Non Interactive Explosion Generator

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Only two parameters you need!
Const saverenders = False ;change this accordingly NOTE RENDERING IS SLOW!
Global qty = 4 ;number of explosions to render as animated images change this accordingly 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Matt Lloyd Explosion Generator (as used in my Space Battle Game for Android Devices)
;
;4-April-2015 ML 
;	Note/Caveat - this code is not pretty - I fully admit that.  It is not intended to be.  It does its job and that's it.
;
;	This program will generate 2d renders of explosions using blitz3d at a resolution of 512x512.
;
;   No user interaction is required once the program starts......fire and forget.......
;	
;	If you wish to save these renders then you need to change the value of the const at the top of the program (set 'saverenders' to true)
;	
;	Renders are saved as 4096x4096 images (8x8 frames of 512x512 images) into a sub folder called 'erenders' with file naming convention
;	'explosion_currentdatetime_indexnumber.bmp'
;	
;	As far as I am aware 4096x4096 is the largest image that blitz can manage without internally resizing it - so why not go for the maximum resolution!
;
;	
;	Note 2 - It takes a few sequences to warm up before it attempts to render as it tries to make sure that
;	as much of the texture atlas is consumed.
;
;	Note 3  - It exports to bitmap. Yuck.  That can be changed pretty easily.
;
;	Note 4 - I imagine this may fail to render anything on certain PCs if they can't keep up with the frame rate.
;	

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Technically no need to touch anything beyond this point!  All trespassers will be shot and so forth!
HidePointer 
Global camera,ent,pivot,speciallist$
Global explosiontexture
Global cdate$ = Replace(CurrentDate$()," ","_")+"_"+Replace(CurrentTime$(),":","_")
SeedRnd MilliSecs() 
main()
End


Function main()

;miscellaneous vars - you don't need to touch these...
Local box, render, e.explosion, time, time2, time3, frame, f0,f1,f2
Local q, xx#,yy#,zz#,aab,aac,aae,x11,y11

init()

box = CreateCube()
MoveEntity box,0,0,80
EntityAlpha box,0

render = CreateImage(4096,4096); 512*8, 512*8
e.explosion = First explosion
time = MilliSecs()
time2 = 10000
time3 = 10000

Repeat
	time11 = MilliSecs()
	Cls
	frame = frame + 1
	
	If(MilliSecs()>time And e=Null) Then 
		f0 = f1
		f1 = frame
		f2 = f2+1
		time2 = f1 + (290/17)
		time3 = f1 + (130/17)
		For q = 0 To 2
			If(q>0) Then 
				xx# = Rnd(-10,10)
				yy# = Rnd(-10,10)
				zz# = Rnd(-8,2)
			EndIf 
			aab = 1 - Sgn(Rand(0,20)) 
			aac = 1 - Sgn(Rand(0,2))
			aae = Sgn(Rand(0,20))
	
			createexplosion(box,1,aae,aac,aab,Rnd(0.65,1.35),1,0,0,0,0,0,xx,yy,10+zz);
			If(q=2)
				createexplosion(box,0,0,aac,1,Rnd(0.7,1.05),1,0,0,0,0,0,xx,yy,10+zz);
			EndIf
		Next	
	EndIf 

	If(frame>time3) Then 
		For q = 0 To 2
			If(q<>-1) Then 
				xx# = Rnd(-20,20)
				yy# = Rnd(-20,20)
				zz# = Rnd(-1,1)
			EndIf 
			aac = 1 - Sgn(Rand(0,10))
			createexplosion(box,3,1,aac,0,Rnd(0.8,1.35),   1,0,0,0,0,0,xx,yy,16+zz);
			If(q=0) Then 
				createexplosion(box,0,0,0,1,Rnd(0.6,1.0),1,0,0,0,0,0,xx,yy,16+zz);
			EndIf
		Next
		time3 = frame + 100000
	EndIf

	If(frame>time2) Then 
		For q = 0 To 1
			xx# = Rnd(-20,20)
			yy# = Rnd(-20,20)
			If(q=2) Then 
				xx=xx*1.5
				yy=yy*1.5
			EndIf 
			zz# = Rnd(-2,4)
			aab = 1- Sgn(Rand(0,1))
			createexplosion(box,1,aab,1-aab,0,Rnd(0.5,0.85),   1,0,0,0,0,0,xx,yy,8+zz);
			If(q=0) Then 
				aab = 1- Sgn(Rand(0,1))
				createexplosion(box,0,0,1-aab,aab,Rnd(0.3,0.5),1,0,0,0,0,0,xx,yy,9+zz);
			EndIf 
		Next	
		time2 = frame + 10000
	EndIf 

	updateexplosion()
	RenderWorld
	If(saverenders) Then 
		x11 = (frame - f1) Mod 8
		y11 = ((frame - f1) - x11) / 8
		CopyRect 0,0,512,512,x11*512,y11*512,BackBuffer(),ImageBuffer(render)
		e.explosion = First explosion
		If(e.explosion = Null And (f1-f0)>55 And (f1-f0)<64) Then 
			i11=i11+1
			If(i11>2) Then 
				xr = 3
				yr = 3
				CopyRect xr*512,yr*512,512,512,0,0,ImageBuffer(render),BackBuffer()
				Text 0,0,(f1 - f0)+" Sequence Length"
				Text 256,30,"......Please wait Saving LARGE image......",1,1
				Flip
				SaveImage render,"erenders\explosion_"+cdate$+"_"+i11+".bmp"
				qty = qty - 1
				Color 0,0,0
				SetBuffer ImageBuffer(render)
				Rect 0,0,ImageWidth(render),ImageHeight(render),1
				SetBuffer BackBuffer()		
			EndIf
		EndIf 
	Else
		e.explosion = First explosion
	EndIf
	Color 255,255,255
	Text 0,0,f1 - f0
	If saverenders Then Text 0,15,qty+" renders to go!"
	Flip
	time22 = MilliSecs()-time11
	If(time22<17) Then 
		Delay 17 - time22
	EndIf 
Until KeyHit(1) Or qty<=0
End Function 

Type explosion

	Field originalent
	Field ent
	Field typ$
	Field vx#,vy#,vz#
	Field sx#,sy#,sz#
	Field r#,g#,b#,a#
	Field dp#,dy#,dr#
	Field time#
	Field timestep#
	Field trailcreated
	Field special
	
End Type

Function updateexplosion()

defaulttimestep#=0.025
For e.explosion = Each explosion
	timestep# = e\timestep
	TranslateEntity e\ent,e\vx*(timestep/defaulttimestep),e\vy*(timestep/defaulttimestep),e\vz*(timestep/defaulttimestep),True
	ScaleEntity e\ent,e\sx,e\sy,e\sz
	EntityColor e\ent,e\r,e\g,e\b
	EntityAlpha e\ent,Float(e\a)/255.0
	TurnEntity e\ent,e\dp,e\dy,e\dr
	e\time=e\time+timestep
	updatefunction(e)
	If(e\special=1 And e\time >= 0 And e\time<e\timestep*5) Then
		createexplosion(ent,2,True,False,False,4,1,0,0,0,0,0)
	EndIf
	If(e\special=1 And e\time >= e\timestep*5) Then
		EntityAlpha ent,0
	EndIf
	If(e\special>0 And e\time>e\timestep*Rand(5,10)) Then
		createexplosion(e\ent,0,True,True,False,3,1,0,0,0,0,e\special-1)
		e\special = 0
	EndIf
	If(e\time>=1.0 Or e\a <= 0 Or e\sx<=0.001 Or e\sy<0.001 Or e\sz<0.001 Or (e\r<=0 And e\g<=0 And e\b<=0))
		FreeEntity e\ent
		Delete e
	EndIf
Next

End Function

Function updatefunction(e.explosion)

If(e\typ="flames")

	If(e\time<0.05) Then
		e\sx = e\sx * (1.0 + ((e\time)/0.05)*0.0125)
		e\sy = e\sy * (1.0 + ((e\time)/0.05)*0.0125)
		e\sz = e\sz * (1.0 + ((e\time)/0.05)*0.0125)
		e\a = e\a * (1.0-e\timestep*15)
		MoveEntity e\ent,e\vx,e\vy,e\vz
	Else
		If(e\time<0.5) Then 
			e\sx = e\sx * (1.05-e\timestep*55)
			e\sy = e\sy * (1.05-e\timestep*55)
			e\sz = e\sz * (1.05-e\timestep*55)
			e\a = e\a * (1.0-e\timestep*5)
		Else
			e\sx = e\sx * (1.05-e\timestep*5)
			e\sy = e\sy * (1.05-e\timestep*5)
			e\sz = e\sz * (1.05-e\timestep*5)
			e\a = e\a * (1.0-e\timestep*25)
		EndIf
	EndIf
EndIf 


If(e\typ="smoke") Then 
	If(e\time<0.15) Then
		e\sx = e\sx *(1.0+(e\time/0.15)*0.1)
		e\sy = e\sy *(1.0+(e\time/0.15)*0.1)
		e\sz = e\sz *(1.0+(e\time/0.15)*0.1)
		e\a = e\a - 0.5
	Else
	If(e\time>0.5)
		e\sx = e\sx *(1.0-((e\time-0.5)/0.5)*0.025)
		e\sy = e\sy *(1.0-((e\time-0.5)/0.5)*0.025)
		e\sz = e\sz *(1.0-((e\time-0.5)/0.5)*0.025)
	EndIf
	e\a=e\a-1.25
EndIf

If(e\time<0.3) Then
	e\vx = e\vx * (1.0 - (e\time/0.3)^2)
	e\vy = e\vy * (1.0 - (e\time/0.3)^2)
	e\vz = e\vz * (1.0 - (e\time/0.3)^2)
EndIf 

EndIf


If(e\typ="sparks")

	e\a = 255.0 - (Float(e\trailcreated) /50.0) *128.0

	e\sx = e\sx * (1.0-e\timestep)*0.925
	e\sx = e\sx * (1.0-e\timestep)*0.925
	e\sx = e\sx * (1.0-e\timestep)*0.925

	MoveEntity e\ent,e\vx,e\vy,e\vz


	If(e\trailcreated<50 )

		;create a spark at our location with an increased time.....
		f.explosion = New explosion
		f\typ = "smoke"
		f\originalent = e\ent
		f\ent = CreateCube()
		EntityTexture f\ent,explosiontexture
		EntityFX f\ent,1+16+32
		f\vx = e\vx*0.0125
		f\vy = e\vy*0.0125
		f\vz = e\vz*0.0125
		f\sx = e\sx*1.5
		f\sy = e\sy*1.5
		f\sz = e\sz*1.5
		PositionEntity f\ent,EntityX(e\ent,True),EntityY(e\ent,True),EntityZ(e\ent,True)
		f\time = 0
		f\timestep = 0.075
		f\r = 128
		f\g = 128
		f\b = 128
		f\a = 40
		f\dp=Rnd(-10,10)
		f\dy=Rnd(-10,10)
		f\dr=Rnd(-10,10)
		e\trailcreated=e\trailcreated+1
	EndIf
EndIf

If(e\sx<=0) Then e\sx=0.001
If(e\sy<=0) Then e\sy=0.001
If(e\sz<=0) Then e\sz=0.001
If(e\a>255) Then e\a=255
If(e\a<0) Then e\a=0
If(e\a>255) Then e\a=255
If(e\r>255) Then e\r=255
If(e\g>255) Then e\g=255
If(e\b>255) Then e\b=255
If(e\r<0) Then e\r=0
If(e\g<0) Then e\g=0
If(e\b<0) Then e\b=0


End Function 


Function createexplosion(ent,n,hasflames=True,hassparks=True,hassmoke=True,scale#=1.0,size#=1.0,offsetx#=0,offsety#=0,offsetz#=0,starttime#=0.0,special=0,posx#=0,posy#=0,posz#=0)

If(explosiontexture=0) Then
	texsize = 256
	explosiontexture = CreateTexture(texsize,texsize,1+4+2)
	SetBuffer TextureBuffer(explosiontexture)

	pxx# = Float(texsize/2)-1
	pyy# = Float(texsize/2)-1

	rr# = 0.1
	
	For px = 0 To texsize-1
		For py = 0 To texsize-1
			WritePixel px,py,0
		Next
	Next
	
	aa = ((texsize/2)-4)*8
	For jj = 0 To aa
		For angle = 0 To 3600
			myx# = pxx + rr*Cos(Float(angle)/10.0)
			myy# = pyy + rr*Sin(Float(angle)/10.0)
			col = Rand(250,255)
			alpha = (col - (rr*Float(0.16))^2) 
			If(alpha<0) Then alpha = 0
			If(alpha>255) Then alpha = 255
			col = alpha
			WritePixel Int(myx),Int(myy),alpha Shl 24 Or col Shl 16 Or col Shl 8 Or col 
		Next
		rr=rr+0.1
	Next
	SetBuffer BackBuffer()
EndIf


;get bounding box....
xw# = MeshWidth(ent)*size
yw# = MeshHeight(ent)*size
zw# = MeshDepth(ent)*size

TFormPoint -xw/2,-yw/2,-zw/2,ent,0
x0# = TFormedX()
y0# = TFormedY()
z0# = TFormedZ()

TFormPoint +xw/2,+yw/2,+zw/2,ent,0
x1# = TFormedX()
y1# = TFormedY()
z1# = TFormedZ()


;cube = CreateCube()
If(x0<x1) Then xa# = x0 Else xa# = x1
If(y0<y1) Then ya# = y0 Else ya# = y1
If(z0<xz) Then za# = z0 Else za# = z1

xstep# = Abs(x1-x0) / 6.0
ystep# = Abs(y1-y0) / 6.0
zstep# = Abs(z1-z0) / 6.0

cx# = (offsetx)*(xw)+(x0+x1)/2.0
cy# = (offsety)*(yw)+(y0+y1)/2.0
cz# = (offsetz)*(zw)+(z0+z1)/2.0


;flames
For nn = 1 To n+1
If(hasflames)
For i=-1 To 1
	x# = cx+xstep*i*scale*0.5
	For j=-1 To 1
		y# = cy+ystep*j*scale*0.5
		For k=-1 To 1
			If(i=0 And j=0 And k=0) Then 
			Else

			z#=cz+zstep*k*scale*0.5
			e.explosion = New explosion
			e\typ = "flames"
			e\originalent = ent
			e\ent = CreateCube()
			RotateMesh e\ent,45,45,45
			aaa = CreateCube()
			AddMesh(aaa,e\ent)
			FreeEntity aaa
			EntityTexture e\ent,explosiontexture
			EntityFX e\ent,1+16+32

			e\vx = scale*Rnd(-1.5,1.5)
			e\vy = scale*Rnd(-1.5,1.5)
			e\vz = scale*Rnd(-1.5,1.5)
			aad# = Rnd(0.975,1.01)
			e\sx = 7.0*scale*aad
			e\sy = 7.0*scale*aad
			e\sz = 7.0*scale*aad
			PositionEntity e\ent,x+posx,y+posy,z+posz

			e\r = 255
			e\g = 110
			e\b = 64
			e\a = 32
			
			e\dp=Rnd(-4,4)
			e\dy=Rnd(-4,4)
			e\dr=Rnd(-4,4)
			e\time = starttime
			e\timestep = 0.005
			EntityBlend e\ent,3
			EndIf	
		Next
	Next
Next
EndIf
;smoke
If(hassmoke) 
bstep = Abs(xstep)
If(Abs(ystep)>bstep) Then bstep = Abs(ystep)
If(Abs(zstep)>bstep) Then bstep = Abs(zstep)
For i=-1 To 1
	x# = cx+xstep*i*scale
	For j=-1 To 1
		y# = cy+ystep*j*scale
		For k=-1 To 1
			z#=cz+zstep*k*scale
			e.explosion = New explosion
			e\typ = "smoke"
			e\originalent = ent
			e\ent = CreateCube()
			EntityTexture e\ent,explosiontexture
			EntityFX e\ent,16+1+32

			sc#=Rand(0.7,1.2)
			e\sx = 5.0*scale*sc
			e\sy = 5.0*scale*sc
			e\sz = 5.0*scale*sc
		
			rad# = scale * Rnd(0,2)
			angle1# = Rnd(360)
			angle2#= Rnd(360)

			e\vx= 2.5*rad*Cos(angle1)
			e\vy= 2.5*rad*Sin(angle1)
			e\vz= 2.5*rad*Cos(angle2)

			rad = rad*bstep*0.5
			PositionEntity e\ent,posx+cx+rad*Cos(angle1),posy+cy+rad*Sin(angle1),posz+cz+rad*Cos(angle2)
			
			e\time = starttime
			e\timestep = 0.0075
			grey=Rand(-8,128)
			e\r = 48+grey
			e\g = 48+grey
			e\b = 48+grey

			e\a = 32

			e\dp=Rnd(-2,2)
			e\dy=Rnd(-2,2)
			e\dr=Rnd(-2,2)
	
		Next
	Next
Next

EndIf
Next
;sparks
If(hassparks) 
For i=-1 To 1
	x# = cx+xstep*i*Rnd(12,20)*scale
	For j=-1 To 1
		y# = cy+ystep*j*Rnd(12,20)*scale
		For k=-1 To 1
			If(i=0 And j=0 And k=0) Then
			Else
			z#=cz+zstep*k*Rnd(12,20)*scale
			e.explosion = New explosion
			e\typ = "sparks"
			e\originalent = ent
			e\ent = CreateCube()
			EntityTexture e\ent,explosiontexture
			EntityFX e\ent,1+16+32
			EntityBlend e\ent,3
			rad# = scale * Rnd(0,2)
			angle1# = Rnd(360)
			angle2#= Rnd(360)

			e\vx= 1.05*rad*Cos(angle1)
			e\vy= 1.05*rad*Sin(angle1)
			e\vz= 1.05*rad*Cos(angle2)
			aad# = Rnd(0.95,1.05)
			e\sx = scale*1.4*aad
			e\sy = scale*1.4*aad
			e\sz = scale*1.4*aad
			PositionEntity e\ent,x+posx,y+posy,z+posz
			e\time = starttime+0.9975
			e\timestep = 0.000125
			e\r = 255
			e\g = 128
			e\b = 32
			e\a = 255
	
			e\dp=Rnd(-12,12)
			e\dy=Rnd(-1.5,1.5)
			e\dr=Rnd(-1,1)
			If(Rand(0,100)<70) Then 
			e\special = special
			If(e\special>0) Then
				speciallist=speciallist+" " +e\special
			EndIf
			special = 0
			EndIf
			EndIf
		Next
	Next
Next
EndIf

End Function 

Function init()
Graphics3D 512,512,0,6
camera=CreateCamera()
CameraClsColor camera,0,0,0
AmbientLight(255,255,255)
MoveEntity camera,0,0,00
SetBuffer BackBuffer()
End Function
