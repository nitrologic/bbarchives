; ID: 3193
; Author: Krischan
; Date: 2015-03-08 14:34:28
; Title: Cubemap to Spheremap conversion
; Description: Cubemap to Spheremap conversion

SuperStrict

Framework brl.glMax2d
Import brl.pixmap
Import brl.PNGloader
Import brl.jpgloader
Import brl.standardio

Local width:Int = 1024
Local Height:Int = width / 2

Graphics width, Height

' load cubemap cross
Local cubemap:TPixmap = LoadPixmap("cubemap1.png")
Local cubesize:Int = cubemap.width / 4

' create output latlon image
Local latlon:TPixmap = CreatePixmap(width, Height, PF_RGB888)

Local ms:Int = MilliSecs()

For Local y:Int = 0 To Height - 1

	For Local x:Int = 0 To width - 1
	
		If KeyHit(KEY_ESCAPE) Then End
		
		' normalize pixmap to lat/lon angles
		Local lat:Float = Normalize(x, 0, width - 1, 0, -360)
		Local lon:Float = Normalize(y, 0, Height - 1, 0, 180)
				
		' 3d coordinates
		Local x3d:Float = Cos(lat) * Sin(lon)
		Local y3d:Float = Cos(lon)
		Local z3d:Float = Sin(lat) * Sin(lon)
								
		' reset help variables
		Local a:Float = 0.0
		Local side:Int = 0
		Local vector:Int = 0

		' which vector is max?
		If Abs(x3d) > a Then a = Abs(x3d) ; vector = 1
		If Abs(y3d) > a Then a = Abs(y3d) ; vector = 2
		If Abs(z3d) > a Then a = Abs(z3d) ; vector = 3
		
		' check which side to read
		If vector = 1 And x3d > 0 Then side = 0 ' +X
		If vector = 1 And x3d < 0 Then side = 1 ' -X
		If vector = 2 And y3d > 0 Then side = 2 ' +Y
		If vector = 2 And y3d < 0 Then side = 3 ' -Y
		If vector = 3 And z3d > 0 Then side = 4 ' +Z
		If vector = 3 And z3d < 0 Then side = 5 ' -Z
		
		' normalize and limit coordinates
		Local xx:Int, yy:Int
		
		Select side
			
			Case 0
					xx = Normalize(z3d / a, 1, -1, 0, cubesize - 1, True)
					yy = Normalize(y3d / a, 1, -1, 0, cubesize - 1, True)

			Case 1
					xx = Normalize(-z3d / a, 1, -1, 0, cubesize - 1, True)
					yy = Normalize(y3d / a, 1, -1, 0, cubesize - 1, True)

			Case 2
					xx = Normalize(x3d / a, 1, -1, 0, cubesize - 1, True)
					yy = Normalize(z3d / a, 1, -1, 0, cubesize - 1, True)

			Case 3
					xx = Normalize(x3d / a, 1, -1, 0, cubesize - 1, True)
					yy = Normalize(-z3d / a, 1, -1, 0, cubesize - 1, True)

			Case 4
					xx = Normalize(-x3d / a, 1, -1, 0, cubesize - 1, True)
					yy = Normalize(y3d / a, 1, -1, 0, cubesize - 1, True)

			Case 5
					xx = Normalize(x3d / a, 1, -1, 0, cubesize - 1, True)
					yy = Normalize(y3d / a, 1, -1, 0, cubesize - 1, True)
								
		End Select
		
														
		' translate side to cubemap cross position
		Local cx:Int, cy:Int
		If side = 0 Then cx = 0 ; cy = 1		' +X
		If side = 1 Then cx = 2 ; cy = 1		' -X
		If side = 2 Then cx = 1 ; cy = 0		' +Y
		If side = 3 Then cx = 1 ; cy = 2		' -Y
		If side = 4 Then cx = 3 ; cy = 1		' +Z
		If side = 5 Then cx = 1 ; cy = 1		' -Z
		
		Local px:Int = Wrap(x - (width * 0.75), 0, width)
							
		' read and write pixel
		WritePixel(latlon, px, y, ReadPixel(cubemap, xx + (cx * cubesize), yy + (cy * cubesize)))
		
	Next
	
Next

ms = MilliSecs() - ms

SavePixmapPNG(latlon, "latlon.png", 0)

' output
While Not AppTerminate()

	If KeyHit(KEY_ESCAPE) Then End
		
	DrawPixmap(latlon, 0, 0)
	DrawText ms, 0, 0
					
	Flip
	
Wend
	
End

Function Normalize:Float(value:Float = 128.0, value_min:Float = 0.0, value_max:Float = 255.0, norm_min:Float = 0.0, norm_max:Float = 1.0, limit:Int = False)

	' normalize	
	Local result:Float=((value-value_min)/(value_max-value_min))*(norm_max-norm_min)+norm_min

	' limit
	If Limit Then
		If result > norm_max Then result = norm_max
		If result < norm_min Then result = norm_min
	EndIf
	
	Return result
	
End Function

Function Wrap:Float(value:Float, minimum:Float, size:Float)

	If value<minimum Then Return value+size Else If value>minimum+size Then Return value-size Else Return value

End Function
