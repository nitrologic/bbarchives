; ID: 2347
; Author: Yasha
; Date: 2008-10-29 01:04:11
; Title: Retro lighting effects
; Description: Metal Gear Solid-inspired lighting

;===========================================================================================================
;MiniShader.bb
;
;A very simple (and slow) scene postprocessor based on Hectic's Draw3D (z)
;===========================================================================================================

AppTitle "MiniShader example"

Global appheight=768
Global appwidth=1024
Global appdepth=32

;MiniShader Global Variables
;===========================================================================================================
Global MSActive=False
Global MS_xres
Global MS_yres
Global shfac#
Global shf2
Global shf4
Global shxr
Global shyr
Global shres
Global shcentre#
Global FStex,SDtex
Global FSbuffer,SDbuffer
Global FSaleph,FSHueVal,FSSatVal,FSLumVal							;Absolute fullscreen shader values
Global FSblend,FSHueStr#,FSSatStr#,FSLumStr#						;Aleph blending (-1,0,1), HSL strengths (range 0-1)
Global FSRedVal=255,FSGreenVal=255,FSBlueVal=255,FSMasterAleph#=1	;Master colour values for whole shader buffer
Global SDRedVal=255,SDGreenVal=255,SDBlueVal=255,SDAlpha#=0			;Colour values for SD buffer
Global ScreenBlur#,MotionBlur#										;Fake-gauss, motion blur
Global SkipShader

Dim shaderbuffer(0,0,0)
Dim shaderstr#(0,0,0)
;===========================================================================================================

;framelimit=CreateTimer(60)
SeedRnd(MilliSecs())

Graphics3D appwidth,appheight,appdepth,2
SetBuffer BackBuffer()
HidePointer

Include "Includes\Draw3Dz.bb"
;Include "MiniShader.bb"

;Camera
camera=CreateCamera()
PositionEntity camera,0,35,-70

;Setup Draw3D
DrawInit3D(camera)
Origin3D(appwidth,appheight)

;Shader setup
InitMiniShader(appwidth,appheight,12)		;Does not have to cover whole screen. 4 is the highest useful resolution, 8 looks better.
									;12 And 16 look OK and run at reasonable speeds, above 20 is pointless unless you really like
									;blurry (and set high fullscreen blur). Resolution ideally a multiple of 4 (not strictly necessary)
Local fx$[9],skipmode$[1]

sfocus=CreateShader(MS_xres/2,MS_yres/2,0,0,350,0.35,0,0,0,0,1,0,0,0,False)
fx[1]="1. Focus on the centre"
dreamy=CreateShader(MS_xres/2,MS_yres/2,0,0,380,0.1,0,0,0,240,1,0,0,0.4,False)
fx[2]="2. Dreamy/Acid haziness"
glow=CreateShader(MS_xres/2-26,MS_yres/2,50,10,220,0.1,200,42,100,200,1,  0,0.1,0.3,False)
bulb=CreateShader( MS_xres/2-5,MS_yres/2,10,10, 40,0.5,200,36,210,140,1,0.9,  1,  1,False)
fx[3]="3. Lamp post at night"
fx[4]="4. Fog"
fx[5]="5. Bleach bypass?"
nvis=CreateShader(MS_xres/2-200,MS_yres/2-75,400,150,100,0.7,255,107,127,255,1,1,0.8,0.03,False)
fx[6]="6. Night vision"
fx[7]="7. Sepia tint"
fx[8]="8. Grey tint"
torch=CreateShader(MS_xres/2,MS_yres/2,0,0,170,0.7,0,0,0,0,1,0,0,0,True)
fx[9]="9. Local illumination only"
fx[0]="None"

skipmode[0]="Skipmode 0 - no skipping"
skipmode[1]="Skipmode 1 - skip rendering ShObj every second frame"

frameskipping=False
currenteffect=1

;Setup basic scene
Gosub createscene

While Not KeyDown(1)

;Simple movement
MoveEntity camera,0,0,KeyDown(200)-KeyDown(208)
RotateEntity camera,EntityPitch(camera)+MouseYSpeed(),EntityYaw(camera)-MouseXSpeed(),0
MoveMouse 100,100

If KeyHit(57) Then frameskipping=Not frameskipping
nmode=GetKey()
If nmode<58 And nmode>48
	currenteffect=nmode-48
EndIf
If KeyHit(11) Then currenteffect=0	;Hit 0 to clear

RenderWorld			;Once for 3D scene itself

Select currenteffect
	Case 1		;Spore focus
		ShowShader(sfocus,True):ShowShader(dreamy,False)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,False):ShowShader(torch,False)
		SetFSShader(0,0,0,250,0,0.99,0,0):SetBlur(10,0):SetDotTint(0,0,0,0)
	Case 2		;Dreamy haze
		ShowShader(sfocus,False):ShowShader(dreamy,True)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,False):ShowShader(torch,False)
		SetFSShader(0,0,100,230,0,0.6,0.4,0):SetBlur(10,0.99):SetDotTint(0,0,0,0)
	Case 3		;Lamp post
		ShowShader(sfocus,False):ShowShader(dreamy,False)
		ShowShader(nvis,False):ShowShader(torch,False)
		lampbright=(EntityInView(lamporb,camera) And EntityVisible(camera,lamporb))
		lamprange=100-EntityDistance(lamporb,camera)
		ShowShader(glow,lampbright)
		ShowShader(bulb,lampbright)
		CameraProject(camera,EntityX(lamporb),EntityY(lamporb),EntityZ(lamporb))
		w1=lamprange:w2=lamprange*0.2
		PutShader(glow,ProjectedX()-w1/2,ProjectedY(),w1,1,lamprange*6)
		PutShader(bulb,ProjectedX()-w2/2,ProjectedY(),w2,w2,lamprange/2)
		SetFSShader(0,0,0,250,0,0.5,0.9,0):SetBlur(0,0):SetDotTint(200,200,0,5)
	Case 4		;Fog
		ShowShader(sfocus,False):ShowShader(dreamy,False)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,False):ShowShader(torch,False)
		SetFSShader(0,0,0,255,0,1,0,1):SetBlur(8,0):SetDotTint(0,0,0,0)
	Case 5		;Bleach bypass
		ShowShader(sfocus,False):ShowShader(dreamy,False)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,False):ShowShader(torch,False)
		SetFSShader(0,0,0,150,0,0.6,0.4,-1):SetBlur(8,0):SetDotTint(0,0,0,0)
	Case 6		;Nightvision
		ShowShader(sfocus,False):ShowShader(dreamy,False)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,True):ShowShader(torch,False)
		SetFSShader(0,0,0,120,0,0.4,0.6,0):SetBlur(0,0):SetDotTint(70,150,70,15)
	Case 7		;Sepia
		ShowShader(sfocus,False):ShowShader(dreamy,False)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,False):ShowShader(torch,False)
		SetFSShader(27,30,0,120,1,1,0,0):SetBlur(6,0):SetDotTint(164,109,12,20)
	Case 8		;Grey
		ShowShader(sfocus,False):ShowShader(dreamy,False)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,False):ShowShader(torch,False)
		SetFSShader(0,0,0,120,0,1,0,0):SetBlur(6,0):SetDotTint(127,127,127,10)
	Case 9		;Local illumination
		ShowShader(sfocus,False):ShowShader(dreamy,False)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,False):ShowShader(torch,True)
		CameraProject(camera,EntityX(someguy1),EntityY(someguy1)+5,EntityZ(someguy1))
		PutShader(torch,ProjectedX(),ProjectedY())
		SetFSShader(0,0,0,255,0,0,1,0):SetBlur(6,0):SetDotTint(0,0,0,0)
	Default		;Nothing
		ShowShader(sfocus,False):ShowShader(dreamy,False)
		ShowShader(glow,False):ShowShader(bulb,False)
		ShowShader(nvis,False):ShowShader(torch,False)
		SetFSShader(0,0,0,0,0,0,0,0):SetBlur(0,0):SetDotTint(0,0,0,0)
End Select

UpdateWorld
RenderMiniShader(frameskipping,True)


frames=frames+1
If MilliSecs()-render_time=>1000 Then fps=frames : frames=0 : render_time=MilliSecs()	
Text(0,0,"FPS: "+fps)
Text(0,30,skipmode[frameskipping])
Text(0,60,"Current effect: "+fx[currenteffect])
Text(800,30,"Move with arrow keys+mouse")
Text(800,60,"Keys 1-0 to change effect")

Flip False
;VWait:Flip False

Wend

FreeMiniShader()	;Deinit MiniShader
DrawFree3D()		;Deinit Draw3D
ShowPointer
End



;===========================================================================================================
;
;The includey-bit
;===========================================================================================================

;Shader Object Type
;===========================================================================================================

Type shobj
	Field x,y,tx,ty			;Rect's start position, width and height - in shader pixels?
	Field alpha#			;Alpha value of the shader
	Field hue,sat,lum				;Colour value of the shader
	Field astr#,hstr#,sstr#,lstr#	;Strength of shader
	Field alphablend				;How the alpha merges with other shaders - 1=Min, 2=Max, 3=Avg, 4=Fixed or 0=autodetect
	Field range;#			;Range to fade out around the shader rect
	Field falloff#			;Range 0-1: rate at which shader fades into background. 0=no radius, 1=no fade
	Field visible			;Whether shader is drawn
End Type



;Functions
;===========================================================================================================

Function InitMiniShader(xr%,yr%,shf#)		;Setup - Width of screen (or area to display on), height (ditto.), shader pixel scale

	If MSActive Then FreeMiniShader()
	
	MS_xres=xr			;For best results use screen size
	MS_yres=yr
	shfac#=shf		;Ideally multiple of 4
	MSCam=cam
	shf2=shfac/2
	shf4=shfac/4
	shxr=Ceil(MS_xres/shfac)
	shyr=Ceil(MS_yres/shfac)
	shres=2^(Floor(Log(shxr)/Log(2))+1)		;Floor()+1 rather than Ceil() ensures texture will be bigger than needed even if shfac is a factor of MS_xres
	shcentre#=(shres/2)*shfac
	
	FStex=CreateImage3D(Floor(Log(shxr)/Log(2))+1,2,2,shfac,shfac)
	SDtex=CreateImage3D(Ceil(Log(MS_xres)/Log(2)),2,2)					;Add small blocks last...
	
	FSbuffer=TextureBuffer(PeekInt(GDrawBank,FStex+DRAWBANKTEXTURE))
	SDbuffer=TextureBuffer(PeekInt(GDrawBank,SDtex+DRAWBANKTEXTURE))
	
	LockBuffer(FSbuffer)	;Clear the texture
	For x=0 To shres-1
	For y=0 To shres-1
		WritePixelFast x,y,$00000000,FSbuffer
	Next
	Next
	UnlockBuffer(FSbuffer)

	LockBuffer SDbuffer		;Draw the dot texture
	For x=0 To MS_xres-1
	For y=0 To MS_yres-1
		If (x Mod shfac)>shf4 And (x Mod shfac)<shf2+shf4 And (y Mod shfac)>shf4 And (y Mod shfac)<shf2+shf4
			WritePixelFast x,y,$ffffffff,SDbuffer
		Else
			WritePixelFast x,y,$00000000,SDbuffer
		EndIf
	Next
	Next
	UnlockBuffer SDbuffer
	
	Dim shaderbuffer(shxr,shyr,5)	;0=blendno, 1=Alpha, 2=Hue, 3=Sat, 4=Lum. Ints 0-255. 5=Final ARGB. Int 0-$ffffffff
	Dim shaderstr#(shxr,shyr,5)		;pixr, strengths of above. 5=falloff
	
	MSActive=True
	
End Function

Function FreeMiniShader()			;Delete MiniShader once finished
	
	Delete Each shobj
	FreeImage3D(FStex)
	FreeImage3D(SDtex)
	FreeImage ScreenGrab
	Dim shaderbuffer(0,0,0)
	Dim shaderstr#(0,0,0)
	MSActive=False
	
End Function

Function SetFSShader(h,s,l,a=127,hs#=1,ss#=0.8,ls#=0,blend=0)	;Fullscreen colour effect
	FSHueVal=h :FSSatVal=s :FSLumVal=l							;Hue, saturation, lightness, alpha, strength of hue/sat/lum shift
	FSHueStr=hs:FSSatStr=ss:FSLumStr=ls				;Blend=1: alpha is proportional to lightness. Blend=-1: alpha is proportional to darkness
	FSaleph=a										;Note that blendmodes other than 0 do not work well with motion blur
	FSblend=blend
End Function

Function SetBlur(gblur#=0,mblur#=0)				;Set fullscreen blur and motion blur
	ScreenBlur#=gblur
	MotionBlur#=mblur		;Does not work well if fullscreen blend is not 0
End Function

Function SetFSTint(red,green,blue,alpha=1)		;Set overall RGB tint of shader buffer
	FSRedVal=red
	FSGreenVal=green
	FSBlueVal=blue
	FSMasterAleph#=alpha/255.0
End Function

Function SetDotTint(red,green,blue,alpha)		;Set RGB tint of dots-layer
	SDRedVal=red
	SDGreenVal=green
	SDBlueVal=blue
	SDAlpha#=alpha/255.0
End Function

Function CreateShader(px,py,dx=1,dy=1,r#=50,f#=0.5,a=0,h=0,s=0,l=0,as#=0,hs#=0,ss#=0,ls#=0,v=True)	;New shader object
							;Screen coordinates, size, range, falloff, AHSL value, relative strengths, visibility
	If dx<1 Then dx=1
	If dy<1 Then dy=1

	shader.shobj=New shobj
	shader\x=px/shfac:shader\y=py/shfac
	shader\tx=dx/shfac:shader\ty=dy/shfac
	shader\range=r/shfac:shader\falloff=f

	shader\alpha=a:shader\hue=h:shader\sat=s:shader\lum=l
	shader\astr=as:shader\hstr=hs:shader\sstr=ss:shader\lstr=ls
	shader\visible=v

	Return Handle(shader)

End Function

Function CopyShader(prevshader,newx,newy,v=False)		;Create a copy of an existing shader object at screen coordinates newx,newy
														;Hidden by default
	oldshader.shobj=Object.shobj(prevshader)
	newshader.shobj=New shobj

	newshader\x=newx/shfac			:newshader\y=newy/shfac
	newshader\tx=oldshader\tx		:newshader\ty=oldshader\ty
	newshader\range=oldshader\range	:newshader\falloff=oldshader\falloff

	newshader\alpha=oldshader\alpha	:newshader\astr=oldshader\astr
	newshader\hue=oldshader\hue		:newshader\hstr=oldshader\hstr
	newshader\sat=oldshader\sat		:newshader\sstr=oldshader\sstr
	newshader\lum=oldshader\lum		:newshader\lstr=oldshader\lstr

	newshader\visible=v

	Return Handle(newshader)

End Function

Function FreeShader(shader)			;Delete a shader object
	
	Delete Object.shobj(shader)
	
End Function

Function PutShader(shader,px,py,dx=-1,dy=-1,r#=-1,f#=-1)		;Reposition a shader object at screen coordinates px,py
																;Optionally adjust its scale, range and intensity
	pshader.shobj=Object.shobj(shader)
	pshader\x=px/shfac:pshader\y=py/shfac
	
	If dx>-1 Then pshader\tx=dx/shfac
	If dy>-1 Then pshader\ty=dy/shfac
	If r>-1 Then pshader\range=r/shfac
	If f>-1 Then pshader\falloff=f

End Function

Function ColourShader(shader,a,h,s,l,as#=-1,hs#=-1,ss#=-1,ls#=-1)		;Change the alpha, hue, sat, lum, and relative strengths
;Function ColorShader(shader,a,h,s,l,as#=-1,hs#=-1,ss#=-1,ls#=-1)		;of a specified shader object

	cshader.shobj=Object.shobj(shader)
	cshader\alpha=a
	cshader\hue=h
	cshader\sat=s
	cshader\lum=l
	
	If as>-1 Then cshader\astr=as
	If hs>-1 Then cshader\hstr=hs
	If ss>-1 Then cshader\sstr=ss
	If ls>-1 Then cshader\lstr=ls

End Function

Function ShowShader(shader,show=True)			;True to show a specified shader object, False to hide it

	sshader.shobj=Object.shobj(shader)
	sshader\visible=show
	
End Function

Function UpdateShaders()						;Apply shader objects

For shader.shobj=Each shobj
If shader\visible=True
	For pixx=-shader\range To shader\tx+shader\range
	For pixy=-shader\range To shader\ty+shader\range
		xpos=shader\x+pixx:ypos=shader\y+pixy
		If xpos<=shxr And ypos<=shyr And xpos>-1 And ypos>-1
		
			If pixx>=0 And pixx<=shader\tx
				If pixy>=0 And pixy<=shader\ty
					pixr#=0
					shaderbuffer(xpos,ypos,0)=shaderbuffer(xpos,ypos,0)+1
			;		pixd=shader\alpha
					pixs#=1
				Else
					If pixy<0 Then pixr#=Abs(pixy):Else pixr#=pixy-shader\ty
					shaderbuffer(xpos,ypos,0)=shaderbuffer(xpos,ypos,0)+1
			;		pixd=linpol3(shader\alpha,FSaleph,pixr/shader\range,shader\falloff)
					pixs#=linpol3(1,0,pixr/shader\range,shader\falloff)
				EndIf
			Else
				If pixy>=0 And pixy<=shader\ty
					If pixx<0 Then pixr#=Abs(pixx):Else pixr#=pixx-shader\tx
					shaderbuffer(xpos,ypos,0)=shaderbuffer(xpos,ypos,0)+1
			;		pixd=linpol3(shader\alpha,FSaleph,pixr/shader\range,shader\falloff)
					pixs#=linpol3(1,0,pixr/shader\range,shader\falloff)
				Else
					If pixx<0 Then pixrx#=pixx:Else pixrx#=pixx-shader\tx
					If pixy<0 Then pixry#=pixy:Else pixry#=pixy-shader\ty
					pixr#=Sqr(pixrx*pixrx+pixry*pixry)
					If pixr<=shader\range
						shaderbuffer(xpos,ypos,0)=shaderbuffer(xpos,ypos,0)+1
			;			pixd=linpol3(shader\alpha,FSaleph,pixr/shader\range,shader\falloff)
						pixs#=linpol3(1,0,pixr/shader\range,shader\falloff)
					EndIf
				EndIf
			EndIf
			
			If pixr<=shader\range										;Apply blend and write to "buffer"
				blendno#=shaderbuffer(xpos,ypos,0)
				pixhue=shader\hue:pixsat=shader\sat:pixlum=shader\lum
				pixrs#=pixr/shader\range
				pixd=shader\alpha
				pixastr#=shader\astr*pixs;linpol3(shader\astr,0,pixrs,shader\falloff)
				pixhstr#=shader\hstr;*pixs
				pixsstr#=shader\sstr*pixs
				pixlstr#=shader\lstr*pixs
				
				If blendno>1
					pixaleph=shaderbuffer(xpos,ypos,1)
					pixh=shaderbuffer(xpos,ypos,2)
					pixs=shaderbuffer(xpos,ypos,3)
					pixl=shaderbuffer(xpos,ypos,4)
					
					If pixhue-pixh>127 Then pixhue=pixhue-256
					If pixh-pixhue>127 Then pixhue=pixhue+256
				
					ff#=(1-shader\falloff)/(1-shader\falloff+1-shaderstr(xpos,ypos,5))
					xr#=(1-pixrs)/(1-pixrs+1-shaderstr(xpos,ypos,0))
					
					pixd=linpol2(pixaleph,pixd,xr,ff)
					pixhue=linpol2(shaderbuffer(xpos,ypos,2),pixhue,xr,ff)
					pixsat=linpol2(shaderbuffer(xpos,ypos,3),pixsat,xr,ff)
					pixlum=linpol2(shaderbuffer(xpos,ypos,4),pixlum,xr,ff)
					
					If pixhue < 0 Then pixhue=pixhue+256
					If pixhue>255 Then pixhue=pixhue-256
									
				;	pixastr=linpol2(shaderstr(xpos,ypos,1),pixastr,xr,ff)
				;	pixhstr=linpol2(shaderstr(xpos,ypos,2),pixhstr,xr,ff)
				;	pixsstr=linpol2(shaderstr(xpos,ypos,3),pixsstr,xr,ff)
				;	pixlstr=linpol2(shaderstr(xpos,ypos,4),pixlstr,xr,ff)
					If pixastr<shaderstr(xpos,ypos,1) Then pixastr=shaderstr(xpos,ypos,1)
					If pixhstr<shaderstr(xpos,ypos,2) Then pixhstr=shaderstr(xpos,ypos,2)
					If pixsstr<shaderstr(xpos,ypos,3) Then pixsstr=shaderstr(xpos,ypos,3)
					If pixlstr<shaderstr(xpos,ypos,4) Then pixlstr=shaderstr(xpos,ypos,4)
				EndIf
												 shaderstr(xpos,ypos,0)=pixrs
				shaderbuffer(xpos,ypos,1)=pixd	:shaderstr(xpos,ypos,1)=pixastr
				shaderbuffer(xpos,ypos,2)=pixhue:shaderstr(xpos,ypos,2)=pixhstr
				shaderbuffer(xpos,ypos,3)=pixsat:shaderstr(xpos,ypos,3)=pixsstr
				shaderbuffer(xpos,ypos,4)=pixlum:shaderstr(xpos,ypos,4)=pixlstr
												 shaderstr(xpos,ypos,5)=shader\falloff
			EndIf
			
		EndIf
	Next
	Next
EndIf
Next
End Function

Function RenderShaders(luminance=True)		;luminance=True - Use luminance instead of lightness
LockBuffer BackBuffer()
LockBuffer(FSbuffer)
For x=0 To shxr				;First read from BackBuffer()
For y=0 To shyr
	pixx=x*shfac+shf4:pixy=y*shfac+shf4
	If pixx>=MS_xres Then pixx=MS_xres-1
	If pixy>=MS_yres Then pixy=MS_yres-1
	shbg=ReadPixelFast(pixx,pixy,BackBuffer())

	shred=(shbg And $ff0000) Shr 16
	shgrn=(shbg And $00ff00) Shr 8
	shblu=shbg And $0000ff
	shgry=shred*.3+shgrn*.59+shblu*.11
	BGtint=RGBHSL(shred,shgrn,shblu)		;Grab H and S
	
	aleph=FSaleph
	If FSblend>0 Then aleph=FSaleph*(shgry/255.0)
	If FSblend<0 Then aleph=FSaleph*((255-shgry)/255.0)

	BGhue=((BGtint And $FF0000) Shr 16)*(1-FSHueStr)+FSHueVal*FSHueStr
	BGsat=((BGtint And $FF00) Shr 8)*(1-FSSatStr)+FSSatVal*FSSatStr
	If luminance Then BGlum=shgry*(1-FSLumStr)+FSLumVal*FSLumStr:Else BGlum=(BGtint And $FF)*(1-FSLumStr)+FSLumVal*FSLumStr

	If shaderbuffer(x,y,0)>0
		shaderbuffer(x,y,0)=0	;Clear shader buffer
		aleph=shaderbuffer(x,y,1)*shaderstr(x,y,1)+aleph*(1-shaderstr(x,y,1))
		BGhue=shaderbuffer(x,y,2)*shaderstr(x,y,2)+BGhue*(1-shaderstr(x,y,2))
		BGsat=shaderbuffer(x,y,3)*shaderstr(x,y,3)+BGsat*(1-shaderstr(x,y,3))
		BGlum=shaderbuffer(x,y,4)*shaderstr(x,y,4)+BGlum*(1-shaderstr(x,y,4))
	EndIf
	
	FStint=HSLRGB(BGhue,BGsat,BGlum)
	
	If MotionBlur#>0 Then FStint=(((FStint And $ff0000)Shr 16)*(1-MotionBlur)+((shaderbuffer(x,y,5) And $ff0000)Shr 16)*MotionBlur)Shl 16 Or (((FStint And $ff00)Shr 8)*(1-MotionBlur)+((shaderbuffer(x,y,5) And $ff00)Shr 8)*MotionBlur)Shl 8 Or ((FStint And $ff)*(1-MotionBlur)+(shaderbuffer(x,y,5) And $ff)*MotionBlur); Or (aleph*(1-MotionBlur)+((shaderbuffer(x,y,5) And $ff000000)Shr 24)*MotionBlur)Shl 24

	WritePixelFast x,y,FStint Or (aleph Shl 24),FSbuffer
	shaderbuffer(x,y,5)=FStint Or (aleph Shl 24)
Next
Next

UnlockBuffer BackBuffer()
UnlockBuffer (FSbuffer)

MSRender()	;The actual drawing to screen

End Function

Function RenderMiniShader(FSkip=0,ClearDraw3D=0)	;FSkip=0: Render every frame, =1: Render every ingame frame and redraw shader every other
													;frame, =2: Render every other ingame and shader frame
If ScreenBlur<0 Then ScreenBlur=ScreenBlur			;ClearDraw3D: True to RenderWorld and call Clear3D. False if there is more Draw3D to draw
If MotionBlur<0 Then MotionBlur=MotionBlur

If FSkip>0
	If SkipShader=1
		UpdateShaders()
		MSRender()
		If ClearDraw3D Then RenderWorld:Clear3D()
	Else
		RenderShaders()
		If ClearDraw3D Then RenderWorld:Clear3D()
	EndIf
	
	SkipShader=Not SkipShader
Else
	UpdateShaders()
	RenderShaders()
	If ClearDraw3D Then RenderWorld:Clear3D()
EndIf

End Function

Function MSRender()		;The actual drawing of the shaders

ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph):DrawImage3D(FStex,shcentre+MSXpos,shcentre+MSYpos)

;Fake-Gaussian blur
If ScreenBlur#>0
	ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph*0.5)	:DrawImage3D(FStex,shcentre+ScreenBlur,shcentre+ScreenBlur)
	ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph*0.3)	:DrawImage3D(FStex,shcentre+ScreenBlur,shcentre-ScreenBlur)
	ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph*0.25)	:DrawImage3D(FStex,shcentre-ScreenBlur,shcentre-ScreenBlur)
	ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph*0.2)	:DrawImage3D(FStex,shcentre-ScreenBlur,shcentre+ScreenBlur)
	ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph*0.166)	:DrawImage3D(FStex,shcentre,shcentre+ScreenBlur)
	ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph*0.143)	:DrawImage3D(FStex,shcentre+ScreenBlur,shcentre)
	ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph*0.125)	:DrawImage3D(FStex,shcentre,shcentre-ScreenBlur)
	ColorG3D(FSRedVal,FSGreenVal,FSBlueVal,FSMasterAleph*0.111)	:DrawImage3D(FStex,shcentre-ScreenBlur,shcentre)
EndIf

If SDalpha>0 Then ColorG3D(SDRedVal,SDGreenVal,SDBlueVal,SDalpha):DrawImage3D(SDtex,MS_xres/2,MS_xres/2)		;Fake dither squares

End Function

Function HSLRGB(H,S,L)		;Converts HSL values to RGB

	hk#=H/255.0
	sk#=S/255.0
	lk#=L/255.0

	If lk<0.5 Then q#=lk*(1+sk):Else q#=lk+sk-(lk*sk)
	p#=2*lk-q

	tR#=hk+0.3333:If tR>1 Then tR=tR-1
	tG#=hk
	tB#=hk-0.3333:If tB<0 Then tB=tB+1

	cR#=p:cG#=p:cB#=p

	If tR<0.1666 Then cR=p+((q-p)*6*tR)
	If tG<0.1666 Then cG=p+((q-p)*6*tG)
	If tB<0.1666 Then cB=p+((q-p)*6*tB)

	If tR<0.5 And tR>=0.1666 Then cR=q
	If tG<0.5 And tG>=0.1666 Then cG=q
	If tB<0.5 And tB>=0.1666 Then cB=q

	If tR<0.6666 And tR>=0.5 Then cR=p+((q-p)*6*(0.6666-tR))
	If tG<0.6666 And tG>=0.5 Then cG=p+((q-p)*6*(0.6666-tG))
	If tB<0.6666 And tB>=0.5 Then cB=p+((q-p)*6*(0.6666-tB))

	R=255*cR
	G=255*cG
	B=255*cB

	Return (R Shl 16) Or (G Shl 8) Or B

End Function

Function RGBHSL(R,G,B)		;Converts RGB values to HSL

rk#=R/255.0:gk#=G/255.0:bk#=B/255.0

max#=rk:min#=gk
If gk>max Then max=gk
If bk>max Then max=bk
If rk<min Then min=rk
If bk<min Then min=bk

If max=min
	h#=0:s#=0:l#=max
Else
	If max=rk Then h#=(60*(gk-bk)/(max-min)) Mod 360
	If max=gk Then h#=(60*(bk-rk)/(max-min)) + 120
	If max=bk Then h#=(60*(rk-gk)/(max-min)) + 240
	l#=(max+min)/2
	If l#<=0.5 Then s#=(max-min)/(2*l)
	If l#>0.5 Then s#=(max-min)/(2-2*l)
EndIf

hv=(h/360.0)*255:sv=s*255:lv=l*255

HSL=(hv Shl 16) Or (sv Shl 8) Or lv

Return HSL

End Function

Function linpol2#(a#,b#,x#,i#)		;Linear interpolation between a and b as x, but with a second weighting to produce a bent line

	Local c#=a+(1-i)*(b-a)
	Local x1#=x/i
	Local x2#=(x-i)/(1-i)	
	If x<i Then Return a *(1- x1 ) + c * x1: Else Return c *(1- x2 ) + b * x2

End Function

Function linpol3#(a#,b#,x#,i#)		;Linear interpolation between a and b as x, but with a second weighting to produce a bent line

	Local c#=a+(1-i)*(b-a)
	Local x2#=(x-i)/(1-i)	
	If x<i Then Return a:Else Return a*(1-x2)+b*x2

End Function
;===========================================================================================================


.createscene

;Scenery
ground=CreateCube():ScaleEntity ground,30,1,30:EntityColor ground,64,64,64:PositionEntity ground,0,-1,0
monolith=CreateCube():ScaleEntity monolith,4,9,1:EntityColor monolith,32,32,32:PositionEntity monolith,-20,9,20:RotateEntity monolith,0,45,0
lampbase=CreateCube():EntityColor lampbase,64,64,64:ScaleEntity lampbase,2.5,1,2.5:PositionEntity lampbase,0,1,0
lamppole=CreateCylinder(8):EntityColor lamppole,64,64,64:ScaleEntity lamppole,1,5,1:PositionEntity lamppole,0,5,0
lamptop=CreateCube():EntityColor lamptop,64,64,64:ScaleEntity lamptop,1.5,0.25,1.5:PositionEntity lamptop,0,10,0
lamporb=CreateCylinder(8):EntityColor lamporb,127,127,127:PositionEntity lamporb,0,11.25,0
firepit=CreateCube():EntityColor firepit,96,96,96:ScaleEntity firepit,3,0.5,3:PositionEntity firepit,0,1,-17
PointEntity camera,lampbase

;Sky and lights
skyorb=CreateSphere(32)
EntityColor skyorb,128,192,255
ScaleEntity skyorb,200,200,200
camorb=CreateSphere(32)
EntityAlpha camorb,0
ScaleEntity camorb,90,90,90
FlipMesh skyorb
FlipMesh camorb

light1=CreateLight()
PositionEntity light1,-150,100,0
PointEntity light1,ground

;Characters
someguy1=CreatePivot():PositionEntity someguy1,-15,0,-5:RotateEntity someguy1,0,45,0
someguytorso=CreateCube(someguy1):ScaleEntity someguytorso,1,1.5,0.5:PositionEntity someguytorso,0,4.5,0
someguylleg=CreateCube(someguy1):ScaleEntity someguylleg,0.4,1.5,0.5:PositionEntity someguylleg,-0.6,1.5,0
someguyrleg=CreateCube(someguy1):ScaleEntity someguyrleg,0.4,1.5,0.5:PositionEntity someguyrleg,0.6,1.5,0
someguyhead=CreateSphere(8,someguy1):ScaleEntity someguyhead,0.6,0.6,0.6
PositionEntity someguyhead,0,6.7,0
someguy2=CopyEntity(someguy1):PositionEntity someguy2,10,0,0:RotateEntity someguy2,0,-30,0
For n=1 To CountChildren(someguy1)
	EntityColor GetChild(someguy1,n),0,0,255
	EntityColor GetChild(someguy2,n),255,0,0
Next

;Pickery
EntityPickMode ground,2,True:EntityPickMode monolith,2,True

EntityType camera,1
EntityType camorb,2
Collisions 1,2,2,2

Return
