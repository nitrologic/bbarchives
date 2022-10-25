; ID: 1812
; Author: Proger
; Date: 2006-09-12 03:19:47
; Title: TexturedPoly
; Description: TexturedPoly

Strict
Rem
'SetGraphicsDriver GLMax2DDriver()
Graphics 800,600

Local Image:TImage = LoadImage("clock.png") ' 32x32 256x256 e.t.c
Local TestImage:TImage = LoadImage("sun.png")

Local xyuv:Float[] = [ ..
			100.0,100.0, 0.5, 0.5,..
			100.0, 36.0, .5, 0.0,  ..			
			145.2, 54.0, .85, 0.14 ..			
			]

SetBlend AlphaBlend
SetScale 2,2
SetAlpha .5
'DrawImage TestImage, 0, 0
DrawTexturedPoly Image, xyuv
'DrawImage TestImage, 200, 200

Flip
WaitKey
End Rem

Function DrawTexturedPoly( image:TImage,xyuv#[],frame=0, vertex = -1)
	Local handle_x#,  handle_y#
	GetHandle handle_x#,  handle_y#
	Local origin_x#,  origin_y#
	GetOrigin origin_x#,  origin_y#	
	
	Local D3DDriver:TD3D7Max2DDriver = TD3D7Max2DDriver(_max2dDriver)
	
	Assert Image, "Image not found"
	
	If D3DDriver Then 
		DrawTexturedPolyD3D ..
			D3DDriver,..
			 TD3D7ImageFrame(image.Frame(frame)), ..
			 xyuv, handle_x, handle_y, origin_x,origin_y, vertex*4
		Return
	End If
	Local  OGLDriver:TGLMax2DDriver = TGLMax2DDriver(_max2dDriver)
	If OGLDriver Then
			DrawTexturedPolyOGL ..
				OGLDriver,..
				 TGLImageFrame(image.Frame(frame)), ..
				 xyuv, handle_x, handle_y, origin_x,origin_y,  vertex*4
		Return
	End If
End Function


Function DrawTexturedPolyD3D( Driver:TD3D7Max2DDriver,  Frame:TD3D7ImageFrame,xyuv#[],handlex#,handley#,tx#,ty# , vertex)
	If Driver.islost Return
	If xyuv.length<6 Return
	Local segs=xyuv.length/4
	Local len_ = Len(xyuv)
	
	If vertex > - 1 Then
		segs = vertex / 4
		len_ = vertex
	End If
	Local uv#[] = New Float[segs*6] ' 6
	

	Local c:Int Ptr=Int Ptr(Float Ptr(uv))
	
	Local ii:Int = 0
	For Local i=0 Until len_ Step 4
		Local x# =  xyuv[i+0]+handlex
		Local y# =  xyuv[i+1]+handley
		uv[ii+0] =  x*Driver.ix+y*Driver.iy+tx
		uv[ii+1] =  x*Driver.jx+y*Driver.jy+ty 
		uv[ii+2] =  0  ' *********** THIS IS THE Z-COORDINATE
		c[ii+3] = Driver.DrawColor
		uv[ii+4] =  xyuv[i+2]
		uv[ii+5] =  xyuv[i+3]
		ii:+6
	Next
	Driver.SetActiveFrame Frame
	Driver.device.DrawPrimitive(D3DPT_TRIANGLEFAN,D3DFVF_XYZ| D3DFVF_DIFFUSE | D3DFVF_TEX1,uv,segs,0)
End Function

Function DrawTexturedPolyOGL (Driver:TGLMax2DDriver, Frame:TGLImageFrame, xy#[],handle_x#,handle_y#,origin_x#,origin_y#, vertex) 
	Private
	Global TmpImage:TImage
	Public
	
	If xy.length<6 Return
	
	Local rot#  = GetRotation()
	Local tform_scale_x#, tform_scale_y#
	GetScale tform_scale_x, tform_scale_y
	
	Local s#=Sin(rot)
	Local c#=Cos(rot)
	Local ix= c*tform_scale_x
	Local iy=-s*tform_scale_y
	Local jx= s*tform_scale_x
	Local jy= c*tform_scale_y
	
	glBindTexture GL_TEXTURE_2D, Frame.name
	glEnable GL_TEXTURE_2D
	
	glBegin GL_POLYGON
	For Local i=0 Until Len xy Step 4
		If vertex > -1 And i >= vertex Then Exit
		Local x#=xy[i+0]+handle_x
		Local y#=xy[i+1]+handle_y
		Local u#=xy[i+2]
		Local v#=xy[i+3]
		glTexCoord2f u,v
		glVertex2f x*ix+y*iy+origin_x,x*jx+y*jy+origin_y
	Next
	glEnd
	If Not tmpImage Then tmpImage = CreateImage(1,1)
	DrawImage tmpImage, -100, - 100 ' Chtob zbit' flag texturi
End Function
