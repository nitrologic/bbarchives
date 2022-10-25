; ID: 696
; Author: sswift
; Date: 2003-05-18 02:57:59
; Title: Perlin Noise HeightMap Generator
; Description: This function generates fractal heightmaps for terrains.

; -------------------------------------------------------------------------------------------------------------------
; Perlin Noise Heightmap Generator - Copyright 2003 - Shawn C. Swift
; -------------------------------------------------------------------------------------------------------------------

Graphics 640,480,32,2

Const MAX_HEIGHTMAP_SIZE = 1024

Dim HeightMap#(MAX_HEIGHTMAP_SIZE, MAX_HEIGHTMAP_SIZE)
Dim NoiseMap#(MAX_HEIGHTMAP_SIZE+1, MAX_HEIGHTMAP_SIZE+1)


.Main

	SeedRnd MilliSecs()	


	; Set the size of the heightmap we want to generate.
	HeightMapSize = 1024

	; Generate the heightmap.
	Generate_Heightmap(HeightMapSize, 0.25, 2)

	; Save the heightmap to an image.
	; You'll get more accuracy and more interesting terrains if you save the actual data instead. 
	; But that will quadruple the size of the datafile created.
	; Which is why instead you might consider just figuring out which sets of values produce a nice terrain and then
	; just creating the terrain at runtime when the program starts.  But if you do this, pray Mark never changes the
	; behavior of the rand() function!
		
		; Create an image.
			DestImage = CreateImage(HeightMapSize, HeightMapSize)
			DestBuffer = ImageBuffer(DestImage)
	
		; Write the data to the image.

			LockBuffer(DestBuffer)
		
			For LoopY = 0 To HeightMapSize-1
				For LoopX = 0 To HeightMapSize-1	
					
					; Calculate the color for this pixel.
					Pr = HeightMap#(LoopX, LoopY)
					If Pr < 0   Then Pr = 0
					If Pr > 255 Then Pr = 255 
					
					Pg = Pr
					Pb = Pr
										
					; Convert the color into a longint.	
					NewPixel = Pb Or (Pg Shl 8) Or (Pr Shl 16) Or ($ff000000)
	
					; Store the pixel in the image.
					WritePixelFast LoopX, LoopY, NewPixel, DestBuffer

				Next
			Next	
		
			UnlockBuffer(DestBuffer)
	
			SaveImage(DestImage, "heightmap.bmp")
			FreeImage DestImage 
	
	; All done!
			
End


; -------------------------------------------------------------------------------------------------------------------
; HeightmapSize must ba a power of 2.
; Scale# is the maximum height of the most frequent and smallest bumps in the terrain.
; Multiplier# is how much each successive pass multiplies scale# by.
; -------------------------------------------------------------------------------------------------------------------
Function Generate_Heightmap(HeightMapSize, Scale#, Multiplier#)

	; Set the maximum height of the first noise pass.
	Max_Height# = Scale#

	; Do the first pass seprately from the other passes since we can do it very cheaply.
	
		For Noise_Y = 0 To HeightMapSize
			For Noise_X = 0 To HeightMapSize
				HeightMap#(Noise_X, Noise_Y) = Rnd#(0, Max_Height#)
			Next
		Next

	; Now start with the second highest frequency noise;
	; The second largest noise map with slightly larger bumps than the first pass.
	NoiseMapSize = HeightMapSize/2
	
	; Multiply the maximum height for the start of the second pass.
	Max_Height# = Max_Height# * Multiplier#
		
	Repeat
	
		; Generate a noise map.
		For Noise_Y = 0 To NoiseMapSize
			For Noise_X = 0 To NoiseMapSize
				NoiseMap#(Noise_X, Noise_Y) = Rnd#(0, Max_Height#)
			Next
		Next

		; Calculate the diffrence in scale between the noisemap and the heightmap.		
		ScaleDifference = HeightMapSize / NoiseMapSize
		
		; Calculate how large of steps across the noise map we need to take for each pixel of the heightmap.
		StepSize# = 1.0 / Float(ScaleDifference)

		; Stretch the noise map over the heightmap using bilinear filtering.
		For Noise_Y = 0 To NoiseMapSize-1
			For Noise_X = 0 To NoiseMapSize-1

				N1# = NoiseMap#(Noise_X,   Noise_Y)  
				N2# = NoiseMap#(Noise_X+1, Noise_Y)  
				N3# = NoiseMap#(Noise_X,   Noise_Y+1)
				N4# = NoiseMap#(Noise_X+1, Noise_Y+1)
			
				Hx = Noise_X*ScaleDifference
				Hy = Noise_Y*ScaleDifference
			
				Iy# = 0
				For Height_Y = 0 To ScaleDifference-1

					; Calculate cosine-weighted bilinear average.
	
						ICy# = 1.0 - ((Cos(Iy#*180.0) + 1.0) / 2.0)
					
					Ix# = 0			
					For Height_X = 0 To ScaleDifference-1
				
						; Calculate cosine-weighted bilinear average.
						;
						; Cosine weighting makes the map smoother, removing unsightly diamond artifacts from the
						; bilinear filtering. 
						;
						; Essentially it "pushes" the four corner pixel colors towards the center, reducing the area
						; which is the average of the colors.  So the corner pixels go from a blurry diamond shape
						; to a blurry circle.
						;
						; It is of course, slower to calculate than regular bilinear filtering, though by using a 
						; lookup table one could possibly speed the operation a litle.
													
							ICx# = 1.0 - ((Cos(Ix#*180.0) + 1.0) / 2.0)

							Na# = N1#*(1.0-ICx#)
							Nb# = N2#*ICx#
							Nc# = N3#*(1.0-ICx#)
							Nd# = N4#*ICx#
							
							HeightMap#(Hx+Height_X, Hy+Height_Y) = HeightMap#(Hx+Height_X, Hy+Height_Y) + (Na#+Nb#)*(1.0-ICy#) + (Nc+Nd#)*ICy#
						
						; Calculate bilinear average.

							;Na# = N1#*(1.0-Ix#)
							;Nb# = N2#*Ix#
							;Nc# = N3#*(1.0-Ix#)
							;Nd# = N4#*Ix#
										
							;HeightMap#(Hx+Height_X, Hy+Height_Y) = HeightMap#(Hx+Height_X, Hy+Height_Y) + (Na#+Nb#)*(1.0-Iy#) + (Nc+Nd#)*Iy#
				
						Ix# = Ix# + StepSize#

					Next
					
					Iy# = Iy# + StepSize#	

				Next
		
			Next
			
		Next
		
		; Reduce the frequency of the noise by half. 				
		NoiseMapSize = NoiseMapSize/2
		
		; Increase the maximum height of the noise.
		Max_Height# = Max_Height# * Multiplier#
			
	Until NoiseMapSize <= 1

End Function
