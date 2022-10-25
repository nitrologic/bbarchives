; ID: 2364
; Author: Queller
; Date: 2008-11-26 17:10:55
; Title: Gaussian Blur Filter
; Description: A true Gaussian blur filter

'======== Example ========

SuperStrict

Local pix:TPixmap = gaussianblur(LoadPixmap("yourimagehere.png"), 5)
Local gfx:TGraphics = Graphics(pix.width, pix.height)

DrawPixmap(pix, 0, 0)
Flip
WaitMouse()

'====== Example Ends ======

Function gaussianblur:TPixmap(tex:TPixmap, radius:Int)
	If radius <=0 Return tex
	Local texclone:TPixmap = tex.copy()			'clone incoming texture
	Local filter:TGaussianFilter = New TGaussianFilter		'instantiate a new gaussian filter
		filter.radius = radius					'configure it
	Return filter.apply(tex, texclone)
End Function

Type TGaussianFilter

	Field radius:Double
	Field kernel:TKernel
	
	Method apply:TPixmap(src:TPixmap, dst:TPixmap)
		Self.kernel = makekernel(Self.radius)
		Self.convolveAndTranspose(Self.kernel, src, dst, PixmapWidth(src), PixmapHeight(src), True)
		Self.convolveAndTranspose(Self.kernel, dst, src, PixmapHeight(dst), PixmapWidth(dst), True)
		dst = Null
		GCCollect()
		Return src
	End Method

'Make a Gaussian blur kernel.

	Method makekernel:TKernel(radius:Double)
		Local r:Int = Int(Ceil(radius))
		Local rows:Int = r*2+1
		Local matrix:Double[] = New Double[rows]
		Local sigma:Double = radius/3.0
		Local sigma22:Double = 2*sigma*sigma
		Local sigmaPi2:Double = 2*Pi*sigma
		Local sqrtSigmaPi2:Double = Double(Sqr(sigmaPi2))
		Local radius2:Double = radius*radius
		Local total:Double = 0
		Local index:Int = 0

		For Local row:Int = -r To r
			Local distance:Double = Double(row*row)
			If (distance > radius2)
				matrix[index] = 0
			Else
				matrix[index] = Double(Exp(-(distance/sigma22)) / sqrtSigmaPi2)
				total:+matrix[index]
				index:+1
			End If
		Next

		For Local i:Int = 0 Until rows
			matrix[i] = matrix[i]/total			'normalizes the gaussian kernel
		Next 

		Return mkernel(rows, 1, matrix)
	End Method
	
	Function mkernel:TKernel(w:Int, h:Int, d:Double[])
		Local k:TKernel = New TKernel
			k.width = w
			k.height = h
			k.data = d
		Return k
	End Function


	Method convolveAndTranspose(kernel:TKernel, in:TPixmap, out:TPixmap, width:Int, height:Int, alpha:Int)
		Local inba:Byte Ptr = in.pixels
		Local outba:Byte Ptr = out.pixels
		Local matrix:Double[] = kernel.getKernelData()
		Local cols:Int = kernel.getWidth()
		Local cols2:Int = cols/2
		
		For Local y:Int = 0 Until height
			Local index:Int = y
			Local ioffset:Int = y*width
				For Local x:Int = 0 Until width
					Local r:Double = 0, g:Double = 0, b:Double = 0, a:Double = 0
					Local moffset:Int = cols2
						For Local col:Int = -cols2 To cols2
							Local f:Double = matrix[moffset+col]
					If (f <> 0)
						Local ix:Int = x+col
						If ( ix < 0 )
							ix = 0
						Else If ( ix >= width)
							ix = width-1
						End If
						
						Local rgb:Int = (Int Ptr inba)[ioffset+ix]
						a:+f *((rgb Shr 24) & $FF)
						b:+f *((rgb Shr 16) & $FF)
						g:+f *((rgb Shr 8) & $FF)
						r:+f *(rgb & $FF)
					End If
				Next
				Local ia:Int
					If alpha = True Then ia = clamp(Int(a+0.5)) Else ia = $FF
				Local ir:Int =clamp( Int(r+0.5))
				Local ig:Int = clamp(Int(g+0.5))
				Local ib:Int = clamp(Int(b+0.5))
				(Int Ptr outba)[index] =((ia Shl 24) | (ib Shl 16) | (ig Shl 8) | (ir Shl 0))
				index:+height
				Next
		Next
	End Method
End Type

Type TKernel

	Field width:Int
	Field height:Int
	Field data:Double[]
	
	Method getkerneldata:Double[]()
		Return Self.data
	End Method
	
	Method getwidth:Int()
		Return Self.width
	End Method
	
	Method getheight:Int()
		Return Self.height
	End Method

End Type

Function clamp:Int(val:Int)
If val < 0
	Return 0
ElseIf val > 255
	Return 255
Else
	Return val
EndIf
End Function
