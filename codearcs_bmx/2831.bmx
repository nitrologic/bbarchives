; ID: 2831
; Author: Warner
; Date: 2011-03-08 04:38:55
; Title: openGL vertex/fragment program
; Description: example of using 1.5 compatible shaders

SuperStrict

Global llen% = 0

SetGraphicsDriver GLGraphicsDriver()
GLGraphics 800,600,0,0,GRAPHICS_BACKBUFFER|GRAPHICS_DEPTHBUFFER 

Global g_pixelProgramID%
Global g_vertexProgramID%
Global time#

'array to hold two triangles that form a square
Global g_triVertices:Float[] = ..
[..
     0.0,0.0,  0.0,0.0,-1.0, -1.0,-1.0, -4.0 ,..
     1.0,0.0,  0.0,0.0,-1.0,  1.0,-1.0, -4.0 ,..
     1.0,1.0,  0.0,0.0,-1.0,  1.0, 1.0, -4.0 ,..
..
     0.0,0.0,  0.0,0.0,-1.0, -1.0,-1.0, -4.0 ,..
     1.0,1.0,  0.0,0.0,-1.0,  1.0, 1.0, -4.0 ,..
     0.0,1.0,  0.0,0.0,-1.0, -1.0, 1.0, -4.0..
]

init() 'setup OPENGL
initShader() 'load and compile shaders

Repeat

	render() 'render square with shaders

Until KeyHit(27) 'ESC=exit

End

'
' init() - Setup OPENGL
'
Function init()

	glewInit()

	glEnable( GL_DEPTH_TEST ) 'enable z-buffer
	glEnable(GL_CULL_FACE) 'enable cull
	glCullFace(GL_BACK) 'set cullmode

	glClearColor( 0.85, 0.7, 0.5, 1.0 ) 'cameraclscolor

	glMatrixMode( GL_PROJECTION ) 'choose projection matrix
	glLoadIdentity() 'reset it
	gluPerspective( 45.0, 640.0 / 480.0, 0.1, 100.0 ) 'create camera view
	
End Function

'
' Load and compile shader
'
Function initShader()

	'
	' Check capabilities
	'
	
	Local Extensions$ = String.FromCString(Byte Ptr(glGetString(GL_EXTENSIONS)))
	Local VPSupport% = Extensions.Find("GL_ARB_vertex_program") > -1
	Local FPSupport% = Extensions.Find("GL_ARB_fragment_program") > -1
	If Not(VPSupport)
		Print "No Vertex Program support."
		End
	End If
	
	If Not(FPSupport)
		Print "No Fragment Program support."
		End 
	End If	

	'
	' Create the vertex program...
	'

    glGenProgramsARB( 1, Varptr g_vertexProgramID )
    glBindProgramARB( GL_VERTEX_PROGRAM_ARB, g_vertexProgramID )
    
    Local shader_assembly$ = readShaderData() 'read from data (DefData) below

    glProgramStringARB( GL_VERTEX_PROGRAM_ARB, GL_PROGRAM_FORMAT_ASCII_ARB, Len(shader_assembly), shader_assembly.ToCString() )
	
    If ( glGetError() <> GL_NO_ERROR )
	Print "ERROR in vert.shad."
	End 
    End If

	'
	' Create the fragment program...
	'

	' Create the vertex program
    glGenProgramsARB( 1, Varptr g_pixelProgramID )
    glBindProgramARB( GL_FRAGMENT_PROGRAM_ARB, g_pixelProgramID )
    
    shader_assembly = readShaderData() 'read shader from data (DEFDATA) below 

    glProgramStringARB( GL_FRAGMENT_PROGRAM_ARB, GL_PROGRAM_FORMAT_ASCII_ARB, Len(shader_assembly), shader_assembly.ToCString() )

    If ( glGetError() <> GL_NO_ERROR )
     Print "ERROR in pix.shad."
     End 
    End If

End Function

'
' readShaderData() - read shader from data (DEFDATA) below
'
Function readShaderData$()
	
	Local out$
	
	Repeat
	
		Local l$
		ReadData l$
		If l$ = "---" Exit
		
		out$ = out$ + l$ + "~r~n"
		
	Forever
	
	Return out$
	
End Function

'
' render() - render a square using shaders
'
Function Render()

     glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT )
	 
     glEnable( GL_VERTEX_PROGRAM_ARB ) 'enable shaders
	glBindProgramARB( GL_VERTEX_PROGRAM_ARB, g_vertexProgramID )
	
     glEnable( GL_FRAGMENT_PROGRAM_ARB )
	glBindProgramARB( GL_FRAGMENT_PROGRAM_ARB, g_pixelProgramID )

	time :+ 0.001 'counter (global)
		
	Local ff#[] = New Float[4] 'create array
	ff[0] = time 'insert into first value of array
	ff[1] = 0
	ff[2] = 0
	ff[3] = 0
     glProgramLocalParameter4fvARB(GL_FRAGMENT_PROGRAM_ARB,0,ff) 'pass (4-float) array to fragment shader (/program)
	
	glInterleavedArrays( GL_T2F_N3F_V3F, 0, g_triVertices ) 'set triangle array
	glDrawArrays( GL_TRIANGLES, 0, g_triVertices.length/8 ) 'draw triangle array

	glDisable( GL_FRAGMENT_PROGRAM_ARB ) 'disable shaders
	glDisable( GL_VERTEX_PROGRAM_ARB )

	Flip
End Function


#ShaderData

DefData "!!ARBvp1.0" 'calculation pro vertex
DefData ""
DefData "# Constant Parameters"
DefData "PARAM mvp[4] = { state.matrix.mvp }; # Model-view-projection matrix" 'read default openGL renderview matrix
DefData ""
DefData "# Per-vertex inputs"
DefData "ATTRIB inPosition = vertex.position;" 'read position
DefData "ATTRIB inColor    = vertex.color;" 'read color
DefData "ATTRIB inTexCoord = vertex.texcoord;" 'read uv coordinate
DefData ""
DefData "# Per-vertex outputs"
DefData "OUTPUT outPosition = result.position;" 'set output xyz
DefData "OUTPUT outColor    = result.color;" 'set output color
DefData "OUTPUT outTexCoord = result.texcoord;" 'set output uv
DefData ""
DefData "DP4 outPosition.x, mvp[0], inPosition;   # Transform the x component of the per-vertex position into clip-space"
DefData "DP4 outPosition.y, mvp[1], inPosition;   # Transform the y component of the per-vertex position into clip-space"
DefData "DP4 outPosition.z, mvp[2], inPosition;   # Transform the z component of the per-vertex position into clip-space"
DefData "DP4 outPosition.w, mvp[3], inPosition;   # Transform the w component of the per-vertex position into clip-space"
DefData ""
DefData "MOV outColor, inColor;       # Pass the color through unmodified"
DefData "MOV outTexCoord, inTexCoord; # Pass the texcoords through unmodified"
DefData ""
DefData "END"
DefData "---"

DefData "!!ARBfp1.0" 'calculation pro pixel/fragment
DefData "PARAM c = program.local[0];" 'Data passed from line 125 (array Local ff#)
DefData "TEMP colorX;" 'internal variable
DefData ""
DefData "MOV colorX, fragment.texcoord[0].x;" 'read u-coordinate into colorX
DefData "ADD colorX, colorX, fragment.texcoord[0].y;" 'add v-coordinate to colorX
DefData "ADD colorX, colorX, c.x;" 'add input from main program (time counter from BMX code)
DefData "MUL colorX, colorX, 25.0;" 'multiply by 25
DefData "FRC colorX, colorX;" 'leave only fractional part (x MOD 1)
DefData "MUL colorX, colorX, 2.0;" 'multiply by two
DefData "FLR colorX, colorX;" 'leave only integer part (x = floor(x))
DefData ""
DefData "MOV result.color, colorX;" 'move colorX into result color (rgba)
DefData ""
DefData "MOV result.color.a, 1;" ' set alpha to 1.0
DefData ""
DefData "END"
DefData "---"
