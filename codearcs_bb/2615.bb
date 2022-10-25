; ID: 2615
; Author: Blitzplotter
; Date: 2009-11-28 15:26:43
; Title: Make steps in JV-ODE
; Description: Function to make a game board or stairs

; note this function call & function requires the JV-ODE Demo
;
; available from here:  http://jv-ode.devcode.co.uk/
;
; typical function call:
; Add game board
make_game_board(3,3) ; pass same values for stairs without gaps
;


Function make_game_board(x,y)
	
	;make some stairs - bit of an evolution of the static objects with the demo code with ODE
	
	cubex = 50;  the width of a cube
	cubey = 20;  the height of a cube
	cubez = 50;  the zed of a cube
	
	rotate_degrees = 1; rotate the cubes by {x} degrees
	
	SeedRnd (MilliSecs()) 
	
	For madsteps=1 To 25  ;note to generate a flat gameboard of 10 by 10 set this to 1 to 1
                          ;and pass 10,10 to the function	
		For cubeswide=1 To x
			
			For cubeszed=1 To y
				
			;SeedRnd (MilliSecs()) 
				cred= Rand(1,250) 
				cgreen = Rand(1,250) 
				cblue = Rand(1,250)  
				
				
			;add a cube
				ode.ODEGeom=New ODEGeom
				ode\geom=dCreateBox(Space,cubex,cubey,cubez)
				;dGeomSetPosition(ode\geom,(cubeswide*10)-15,(10*madsteps),(cubeszed*(10)+((x*10)*madsteps))-260)
				dGeomSetPosition(ode\geom,(cubeswide*cubex)-15,(cubey*madsteps),(cubeszed*(cubez)+((x*cubez)*madsteps))-260)
				dGeomSetRotation(ode\geom,rotate_degrees,0,0)
				ode\mesh=CreateCube()
				ScaleMesh ode\mesh,cubex/2,cubey/2,cubez/2
				EntityColor ode\mesh,cred,cgreen,cblue
				
			Next
			
		Next
		
	Next
	
End Function
