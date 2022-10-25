; ID: 1390
; Author: ozak
; Date: 2005-06-11 13:41:52
; Title: MD2 loader/animator class
; Description: MD2 class

' MD2 model class by Odin Jensen (www.furi.dk)
' Free to use as you please :)
Strict

' MD2 animation frame
Type MD2AnimFrame

	Field Name:String
	Field Vertices:Float[]
	
EndType

' MD2 file header
Type MD2FileHeader

	Field Name:String
	Field Magic:Int
	Field Version:Int
	Field SkinWidth:Int
	Field SkinHeight:Int
	Field FrameSize:Int
	Field NumSkins:Int
	Field NumVertices:Int
	Field NumTexCoords:Int
	Field NumTriangles:Int
	Field NumGLcommands:Int
	Field NumFrames:Int
	Field OffsetSkins:Int
	Field OffsetTexCoords:Int
	Field OffsetTriangles:Int
	Field OffsetFrames:Int
	Field OffsetGLcommands:Int
	Field OffsetEnd:Int
	
EndType

' MD2 main model class
Type MD2Model

	' Frame rate of animation
	Field FPS:Int = 30
	
	' Animation flag
	Field Animate:Int = False
	
	' Current, next, start and end animation frame numbers
	Field CurAnimFrame:Int = 0
	Field NextAnimFrame:Int = 1
	Field StartFrame:Int = 0
	Field EndFrame:Int = 0
	
	' Animation timer values
	Field LastAnimTime:Long=0
	
	' Model frames + face data + uvs
	Field InterpolatedFrame:MD2AnimFrame
	Field Frames:MD2AnimFrame[]
	Field UVs:Float[]
	Field VertIndices:Int[3]	
	Field UVIndices:Int[3]


	
	' Attempt to load model
	Method Load(FileName:String)	
	
		' Open file
		Local FileHandle:TStream = ReadStream("littleendian::" + FileName)
	
		' Read header
		Local Header:MD2FileHeader= New MD2FileHeader
		Header.Magic = Readint(FileHandle)
		Header.Version = Readint(FileHandle)
		Header.SkinWidth = Readint(FileHandle)
		Header.SkinHeight = Readint(FileHandle)
		Header.FrameSize = Readint(FileHandle)
		Header.NumSkins = Readint(FileHandle)
		Header.NumVertices = Readint(FileHandle)
		Header.NumTexCoords = Readint(FileHandle)
		Header.NumTriangles = Readint(FileHandle)
		Header.NumGLcommands = Readint(FileHandle)
		Header.NumFrames = Readint(FileHandle)
		Header.OffsetSkins = Readint(FileHandle)
		Header.OffsetTexCoords = Readint(FileHandle)
		Header.OffsetTriangles = Readint(FileHandle)
		Header.OffsetFrames = Readint(FileHandle)
		Header.OffsetGLcommands = Readint(FileHandle)
		Header.OffsetEnd = Readint(FileHandle)
		
		' Load UVs
		UVs = New Float[Header.NumTexCoords*2]
		SeekStream(FileHandle, Header.OffsetTexCoords)
		For Local i = 0 To Header.NumTexCoords-1
		
			UVs[i*2]=Float ReadShort(FileHandle) / Header.SkinWidth
			UVs[(i*2)+1]=Float ReadShort(FileHandle) / Header.SkinHeight
			
		Next 
			
		' Load faces (triangle indices)
		SeekStream (FileHandle, Header.OffsetTriangles)
		VertIndices = New Int[Header.NumTriangles * 3];
        UVIndices = New Int[Header.NumTriangles * 3];
        Local CurIndex:Int = 0
		For Local i = 0 To Header.NumTriangles-1
		
			VertIndices[CurIndex + 2] = ReadShort(FileHandle)
			VertIndices[CurIndex + 1] = ReadShort(FileHandle)
			VertIndices[CurIndex] = ReadShort(FileHandle)
			UVIndices[CurIndex + 2] = ReadShort(FileHandle)
			UVIndices[CurIndex + 1] = ReadShort(FileHandle)
			UVIndices[CurIndex] = ReadShort(FileHandle)
			
			CurIndex = CurIndex + 3
			
		Next

		' Load animation frames		
		Frames = New MD2AnimFrame[Header.NumFrames]
		InterpolatedFrame = New MD2AnimFrame
		InterpolatedFrame.Vertices = New Float[Header.NumVertices*3]
		SeekStream(FileHandle, Header.OffsetFrames)
		Local Scale:Float [3]
		Local Translate:Float[3]
		Local CurFrame:MD2AnimFrame
		Local Tempx:Int
		Local Tempy:Int
		Local Tempz:Int
		Local Dummy:Int
		
		For Local i = 0 To Header.NumFrames-1
            
           Frames[i] = New MD2AnimFrame
           Frames[i].vertices = New Float[Header.NumVertices * 3]
           CurFrame = Frames[i]     
                                                                           
           Scale[0] = ReadFloat(FileHandle)
	       Scale[1] = ReadFloat(FileHandle)
	       Scale[2] = ReadFloat(FileHandle)
	
	       Translate[0] = ReadFloat(FileHandle)
	       Translate[1] = ReadFloat(FileHandle)
	       Translate[2] = ReadFloat(FileHandle)
	
	       CurFrame.Name = ReadString(FileHandle,16)
                                
           For Local v = 0 To Header.NumVertices-1
                                
           		Tempx = ReadByte(FileHandle)
                Tempy = ReadByte(FileHandle)
                Tempz = ReadByte(FileHandle)

                Dummy = ReadByte(FileHandle)

                CurFrame.Vertices[(v*3)] = Tempx * Scale[0] + Translate[0]
                CurFrame.Vertices[(v*3)+2] = -1 * (Tempy * Scale[1] + Translate[1])
                CurFrame.Vertices[(v*3)+1] = Tempz * Scale[2] + Translate[2]

		   Next
		
		Next	
			
		' Finally, close stream
		CloseStream(FileHandle)	

	EndMethod
	
	' Set animation FPS
	Method SetFPS(FPS:Int)

		Self.FPS = FPS;

	EndMethod
	
	' Toggle animation on/off
	Method ToggleAnim(Enable:Int)

		Animate = Enable

	EndMethod
	
	' Reset animation
	Method ResetAnim()

		CurAnimFrame = StartFrame
        NextAnimFrame = StartFrame+1
        LastAnimTime = 0

	EndMethod
	
	' Set animation range
	Method SetAnimRange(StartFrame:Int, EndFrame:Int)
		 	
		' Set values
		self.StartFrame = StartFrame
        self.EndFrame = EndFrame
       
		' Make sure they're within range
  		If (StartFrame < 0) Then StartFrame = 0
       
	 	If (StartFrame > Frames.length) Then StartFrame = Frames.length
        If (EndFrame < 0) Then EndFrame = 0
  
      	If (EndFrame > Frames.length) Then EndFrame = Frames.length
       
        CurAnimFrame = StartFrame
        NextAnimFrame = StartFrame+1

	EndMethod
	
	' Create interpolated frame (Private function used internally)
	Method CreateInterpolatedFrame()

		' Grab current time
		Local CurTime:Long = MilliSecs()
		
		' Calculate delta time between last frame and now
        Local ElapsedTime:Long = CurTime - LastAnimTime
               
		' Calculate interpolation value from delta time + fps
	    Local T:Float = ElapsedTime / (1000.0 / self.FPS)
		        
		' Calculate next frame to interpolate against
	    NextAnimFrame = (CurAnimFrame + 1)
	
		' Over the edge? Then wrap animation
        If(NextAnimFrame > EndFrame) Then NextAnimFrame = StartFrame
        	
		' Calculate current animation frame
        If(ElapsedTime > (1000.0 / fps)) Then

	       CurAnimFrame = NextAnimFrame                                  
           LastAnimTime = CurTime

	    End If
	        
        ' Go through all vertices of current
		For Local v = 0 To Frames[CurAnimFrame].Vertices.length -1 
        
        	InterpolatedFrame.Vertices[v] = (Frames[CurAnimFrame].Vertices[v] + T * (Frames[NextAnimFrame].Vertices[v] - Frames[CurAnimFrame].Vertices[v]))
        	
    	Next 

	EndMethod
	
	' Render model
	Method Render()
	
		' Grab first frame
		Local Frame:MD2AnimFrame = Frames[StartFrame]
		        
		' If we're animating we are going to create a temporary interpolated frame and use that instead
        If (Animate=True) Then
      
            CreateInterpolatedFrame()
            Frame = InterpolatedFrame

      	End If 

		' Start triangle based rendering (Will optimize soon :)
		glBegin GL_TRIANGLES

			           
		' Go through vertices/uv coords grabbing them from their respective indices
        For Local v = 0 To VertIndices.length-1
			
			glTexCoord2f(UVs[(UVIndices[v]*2)], UVs[(UVIndices[v]*2)+1])
        	glVertex3f(Frame.Vertices[(VertIndices[v]*3)], Frame.Vertices[(VertIndices[v]*3)+1], Frame.Vertices[(VertIndices[v]*3)+2])               
      		
		Next 
        
		' End triangle rendering
		glEnd

	EndMethod	

EndType
