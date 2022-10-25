; ID: 612
; Author: elias_t
; Date: 2003-03-04 18:14:57
; Title: Gaussian Blur
; Description: A gaussian blur function

;GAUSSIAN BLUR FUNCTION
;by elias_t
;----------------------


;example
Graphics 640,480,32,2

image=LoadImage("frame.png");your image here 

image=gaussian_blur(image,5,5);[3,5,7,9]

DrawImage image,0,0
Flip

WaitKey()
End







;needed arrays
Dim maskData#(0)
Dim texel#(0,0,0)
Dim result#(0,0,0)

;ww and hh values
;should always be uneven numbers, [3,5,7,9]

Function gaussian_blur(input_image,ww,hh)

Width=ImageWidth(input_image)
Height=ImageHeight(input_image)

Dim texel#(Width,Height,2) 
Dim result#(Width,Height,2) 

SetBuffer ImageBuffer (input_image)
LockBuffer ImageBuffer (input_image)

For x=0 To Width-1
 For y=0 To Height-1
  rgb=ReadPixelFast (x,y,ImageBuffer(input_image))
  texel#(x,y,0)=(rgb Shr 16) And $ff;
  texel#(x,y,1)=(rgb Shr 8) And $ff;
  texel#(x,y,2)=rgb And $ff;
 Next
Next


maskWidth#  = ww;
maskHeight# = hh;

Dim maskData#(maskWidth#*maskHeight#);

mult# = 0.0;

For  ym# = 0  To   maskHeight#-1
	For xm# = 0  To  maskWidth#-1
		 cx# = xm# - (maskWidth# - 1) / 2.0;
		 cy# = ym# - (maskHeight# - 1) / 2.0;
		 rt# = cx# * cx# + cy# * cy#;
		 mult# =mult#+ Exp(-0.35 * rt#);
	Next 
Next 

mult# = 1.0 / mult#;

For ym# = 0 To  maskHeight#-1
	For xm# = 0  To  maskWidth#-1
		cx# = xm# - (maskWidth# - 1) / 2.0;
		cy# = ym# - (maskHeight# - 1) / 2.0;
		rt# = cx# * cx# + cy# * cy#;
		maskData#(ym# * maskWidth# + xm#) = mult# * Exp(-0.35 * rt#);
	Next 
Next 


Dim result#(Width,Height,3)

For ym# = 0 To Height-1
	For  xm# = 0 To Width-1
		rr#=0.0;
		gg#=0.0;
		bb#=0.0;
		For yy = 0 To maskHeight# -1
			For xx = 0 To maskWidth#-1  
				If (xm + xx - Floor(maskWidth# / 2.0)<0) Or (ym + yy - Floor(maskHeight# / 2)<0) Or (xm + xx - Floor(maskWidth# / 2.0)>Width) Or (ym + yy - Floor(maskHeight# / 2)>Height)
					rl#=0;
					gl#=0;
					bl#=0;
				Else
					rl#=texel#(xm + xx - Floor(maskWidth# / 2.0),	ym + yy - Floor(maskHeight# / 2),0)
					gl#=texel#(xm + xx - Floor(maskWidth# / 2.0),	ym + yy - Floor(maskHeight# / 2),1)
					bl#=texel#(xm + xx - Floor(maskWidth# / 2.0),	ym + yy - Floor(maskHeight# / 2),2)
				EndIf 
					
				rr# =rr#+rl# * maskData#(xx + yy * maskWidth#);
				gg# =gg#+gl# * maskData#(xx + yy * maskWidth#);
				bb# =bb#+bl# * maskData#(xx + yy * maskWidth#);
			Next
		Next
		result#(xm#,ym#,0)=rr#;
		result#(xm#,ym#,1)=gg#;
		result#(xm#,ym#,2)=bb#;
	Next
Next




For x=0 To Width-1
 For y=0 To Height-1
  WritePixelFast ( x,y,((result#(x,y,0) Shl 16)+(result#(x,y,1) Shl 8)+result#(x,y,2)),ImageBuffer(input_image) )
 Next
Next

UnlockBuffer ImageBuffer(input_image)

SetBuffer BackBuffer()

Dim maskData#(0)
Dim texel#(0,0,0)
Dim result#(0,0,0)

Return input_image

End Function
