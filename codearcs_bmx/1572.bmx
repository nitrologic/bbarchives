; ID: 1572
; Author: bradford6
; Date: 2005-12-21 21:48:29
; Title: Particle Arrays
; Description: Particles using an array

Graphics 1024,768,32 
' Declarations

SeedRnd(MilliSecs()) 
AutoMidHandle(True) 
'-------------------------------------------------------------------------

' create a particle (programmatically, you can uncomment the loadimage line if you have a better particle image


SetBlend ALPHABLEND 

Local sz:Int =16 

Global particle_image:timage = CreateImage(sz,sz) 

	Local alpha:Float = 1.0 
	
	For i = 1 To sz/2 
	
			DrawOval (sz/2)-i,(sz/2)-i,i*2,i*2 
			alpha:* 0.65
			
			
			SetColor Rnd(100,200),Rnd(100,200),Rnd(200,255) 
			
			SetAlpha alpha 
	
	Next 
	
	GrabImage(particle_image,0,0) ; Cls 
'-------------------------------------------------------------------


Type Tparticle 

		Field x:Float,y:Float 
		
		Field xrange:Int,yrange:Int
		
		Field vel:Float 
		
		Field angle:Float 
		
		Field image:Timage 
		
		Field life 
		
		Field is_alive 
		
		Field alpha:Float 
		
		Field scale:Float 
		
		Field pred,pgreen,pblue 
		
		
		Method Spawn(px,py,pvel,plife,pscale,pangle,pxrange,pyrange) 
		
			is_alive = True 
			x = Rnd(px-(pxrange/2),px+(pxrange/2))
			y = Rnd(py-(pyrange/2),py+(pyrange/2))

			
			
			
			vel = pvel 
			xrange = pxrange
			yrange = pyrange
			
			life = plife
			
			scale = pscale 
			
			angle = pangle
			
			alpha = Rnd(1.0,3.0) 
			pred = Rnd(0,255) 
			pgreen = Rnd(0,255) 
			pblue = Rnd(0,255) 
		
		End Method 
		
		Method Update() 
		
			life:-1			
			
			If life <0 Then is_alive = False 			
			
			If is_alive = True 
				pcount:+1
				
				vel:*1.02		
				
				'x:+(vel*Sin(angle-90)) 
				
				'y:+(vel*Cos(angle-90)) 
				x=x+(vel*Cos(angle-90)) 
				y=y+(vel*Sin(angle-90)) 				
				
					SetBlend LIGHTBLEND 'ALPHABLEND 
					alpha:*.99
				
					scale:*.999
								
				SetAlpha alpha 
				SetColor pred,pgreen,pblue 
				SetRotation angle 
				SetScale(scale,scale) 
				DrawImage image,x,y 
							
			EndIf 
			
		
		
		
		End Method 
		 
		
		 

End Type 
'-------------------------------------------------------------------

Local part_array:Tparticle[10000] 
Local spawn_delay:Int = 20 
Global pcount:Int 
Global part_counter:Int 
Global plength:Int = Len part_array 
Print plength 
 

 

 

For i = 1 To plength-1 
	part_array[i] = New Tparticle 
	part_array[i].image = particle_image
	
	part_array[i].life = Rnd(800,1000) 
	part_array[i].scale = 1
	
	part_array[i].is_alive =False 
	part_array[i].alpha = 1
	
Next

' * * * <MAIN_LOOP>
Local ymouse:Int,xmouse:Int

Repeat

	Cls
	
	xmouse:Int = MouseX()
	ymouse:Int = MouseY()
	
	Local spawnx = GraphicsWidth()/2
	Local spawny = 32
	
	
	
	spawn_delay:-1
	
	
	If spawn_delay<0 'And MouseDown (1) 
	
		For pp = 1 To 64
			
			For i = 1 To plength-1 
				
				If part_array[i].is_alive = False 
				
					Local pvel:Float = Rnd (pp/8,pp/4) 
				
					Local life:Int = Rnd (75,100) 
				
					Local sc:Float = Rnd (1.1,2.5) 
				
					Local ang:Float = Rnd(176,184)

					Local xrange:Int = 600
					
					Local yrange:Int = 16
				
				
					part_array[i].spawn(spawnx,spawny,pvel,life,sc,ang,xrange,yrange) 
				
				
					spawn_delay = 0
				
				
					Exit 
			
				EndIf 
		
			Next 
		
		Next 
		
	EndIf 
	
	
	For	i = 1 To plength-1 
	
		part_array[i].update()
		
	Next
	 
	
	SetColor 255,255,0 
	SetAlpha 1 
	SetTransform(1,1,1) 
	DrawText("ps: " +pcount,0,0) 
	pcount = 0
	
	Flip

Until KeyDown(KEY_ESCAPE) 
End

 

' * * * </MAIN_LOOP>

'-------------------------------------------------------------------

'-------------------------------------------------------------------

'-------------------------------------------------------------------

'-------------------------------------------------------------------

'-------------------------------------------------------------------
