; ID: 3222
; Author: _PJ_
; Date: 2015-09-04 07:35:52
; Title: Image Scaling
; Description: Linear Interpolation to Smooth Scale Images

Function ReScaleImage(Image,NewW,NewH)
	Local W=ImageWidth(Image)
	Local H=ImageHeight(Image)
	
	Local ReturnImage=Image
	
	If (NewW<=W) And (NewH<=H)
		ReturnImage=CopyImage(Image)
		ResizeImage ReturnImage,NewW,NewH
	Else
			
		ReturnImage=CreateImage(NewW,NewH)
		
		Local RatioW#=Float(NewW/W)
		Local RatioH#=Float(NewH/H)
		
		Local MaxW=W-1
		Local MaxH=H-1
		
		Local X
		Local Y
		
		Local RGB
		
		Local IBuffer=ImageBuffer(Image)
		Local RBuffer=ImageBuffer(ReturnImage)
		
		;First Pass Populate Reference Points
		LockBuffer IBuffer 
		LockBuffer RBuffer 
		
		For Y=0 To MaxH
			For X=0 To MaxW
				RGB=ReadPixelFast(X,Y,IBuffer)
				WritePixelFast X*RatioW,Y*RatioH,RGB,RBuffer
			Next
		Next
		
		UnlockBuffer IBuffer
		
		;Second Pass Interpolate
		For Y=0 To MaxH
			For X=0 To MaxW
				GradientCells(X*RatioW,Y*RatioH,NewW,NewH,RatioW,RatioH,RBuffer)
			Next
		Next
		
		UnlockBuffer RBuffer
	End If
	
	Return ReturnImage
	
End Function

Function GradientCells(X,Y,BufferW,BufferH,CellW#,CellH#,Buffer)
	Local XIter#
	Local YIter#
	
	Local XMap
	Local YMap
	
	Local GradientStartA
	Local GradientFinishA
	
	Local GradientStartB
	Local GradientFinishB
	
	Local TangentA#
	Local TangentB#
	
	Local XMax=(X+CellW) 
	If XMax>=BufferW Then XMax=BufferW-1
	
	Local YMax=(Y+CellH) Mod BufferH	
	If YMax>=BufferH Then YMax=BufferH-1
	
	Local Pixel%
	
		;Horizontal
	
		GradientStartA=ReadPixelFast(X,Y,Buffer)
		GradientFinishA=ReadPixelFast(XMax,Y,Buffer)
		
		GradientStartB=ReadPixelFast(X,YMax,Buffer)
		GradientFinishB=ReadPixelFast(XMax,YMax,Buffer)
		
		YMap=Y+CellH-1
		
		For XIter=0 To CellW-1
			XMap=X+XIter
			
			Pixel=InterpolatePixel(GradientStartA,GradientFinishA,XIter/CellW)
			WritePixelFast(XMap,Y,Pixel,Buffer)
			
			Pixel=InterpolatePixel(GradientStartB,GradientFinishB,XIter/CellW)
			WritePixelFast(XMap,YMap,Pixel,Buffer)
			
		Next
	
		;Vertical
		GradientStartA=ReadPixelFast(X,Y,Buffer)
		GradientFinishA=ReadPixelFast(X,YMax,Buffer)
		
		GradientStartB=ReadPixelFast(XMax,Y,Buffer)
		GradientFinishB=ReadPixelFast(XMax,YMax,Buffer)
		
		XMap=X+CellW-1
		
		For YIter=0 To CellH-1
			YMap=Y+YIter
			
			Pixel=InterpolatePixel(GradientStartA,GradientFinishA,YIter/CellH)
			WritePixelFast(X,YMap,Pixel,Buffer)
			
			Pixel=InterpolatePixel(GradientStartB,GradientFinishB,YIter/CellH)
			WritePixelFast(XMap,YMap,Pixel,Buffer)
			
		Next
		
	
		;Interior
		XMax=(X+CellW-1)
		YMax=(Y+CellH-1)
		
		For YIter=0 To CellH-1
			
			YMap=Y+YIter
			
			For XIter=0 To CellW-1
				
				XMap=X+XIter
				
				GradientStartA=ReadPixelFast(X,YMap,Buffer)
				GradientFinishA=ReadPixelFast(XMax,YMap,Buffer)
				
				Pixel=InterpolatePixel(GradientStartA,GradientFinishA,XIter/CellW)
				
				WritePixelFast(XMap,YMap,Pixel,Buffer)
				
			Next
		Next
	
End Function

Function InterpolatePixel(RGB1,RGB2,DistanceVector#)
	Local R1=RGB1 And 255
	Local G1=(RGB1 Shr 8) And 255
	Local B1=(RGB1 Shr 16) And 255
	
	Local R2=RGB2 And 255
	Local G2=(RGB2 Shr 8) And 255
	Local B2=(RGB2 Shr 16) And 255
	
	Local R=(R2-R1)
	Local G=(G2-G1)
	Local B=(B2-B1)
	
	R=(R1+(R*DistanceVector))
	G=(G1+(G*DistanceVector))
	B=(B1+(B*DistanceVector))
	
	If R>254 Then R=255
	If R<1 Then R=0
	If G>255 Then G=255
	If G<1 Then G=0
	If B>255 Then B=255
	If G<1 Then G=0
	
	Return  R + (G Shl 8) + (B Shl 16)
	
End Function
