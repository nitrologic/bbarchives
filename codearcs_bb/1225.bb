; ID: 1225
; Author: Techlord
; Date: 2004-12-06 04:52:12
; Title: ImageMap
; Description: A Simple HTML Client-side Image Map Loader

;ImageMap by Frankie 'TechLord' Taylor 12/06/2004

Const IMAGEMAP_AREA_MAX%=256

Global imagemapCurrent.imagemap

Type imagemap
	;Purpose: Store Image and HotSpots
	Field id%
	Field name$
	Field x#
	Field y#
	Field imgsrc$
	Field image%
	Field hotspots%
	Field hotspot.hotspot[IMAGEMAP_AREA_MAX%]
	Field state%
End Type

Type hotspot
	;Purpose: Stores Coords, target, and command information
	Field id%
	Field typeid%
	Field coord[3]
	Field target$
	Field command$
	Field state%
End Type

Function imagemapLoad.imagemap(imagemapname$,imagemapfilename$,imagemapsrcname$="")
	;Purpose: Parses html client side imagemap file.
	;Parameters:
	;	imagemapname$ - name of imagemap. Can be used for reference purposes.
	;	imagemapfilename$ - html map file name, *.htm extension not required.
	;	imagemapsrcname$ - image file name. Valid map format extension required.	
	;Return:
	imagemapfile%=OpenFile(imagemapfilename+".htm") ;*.map
	If Not imagemapfile% Return Null
	While Not Eof(imagemapfile%)
		imagemapfileline$=Lower(ReadLine(imagemapfile%))
		imagemapfilelinelength%=Len(imagemapfileline$)
		
		For loop% = 1 To imagemapfilelinelength%
			imagemapchar$=Mid$(imagemapfileline$,loop%,1)
		
			Select imagemapchar$
				Case " ","<",">","=",Chr(34),Chr(39)
					;ignor whitespace and tags	
					imagemapword$=nil$			
				Default
					imagemapword$=imagemapword$+imagemapchar$
			End Select
	
			Select imagemapword$
			
				Case "name"
					this.imagemap=New imagemap
					this\imgsrc$=imagemapsrcname$					
					this\image%=LoadImage(this\imgsrc$)
					this\name$=imagemapname$
					this\state%=1

				Case "area" 
					this\hotspots%=this\hotspots%+1
					this\hotspot[this\hotspots%] = New hotspot
					hotspot.hotspot=this\hotspot[this\hotspots%]  ;testing
					
				Case "coords" ;coords define shape, rect by by limitation
					For loop% = loop%+1 To imagemapfilelinelength%
						imagemapchar$=Mid$(imagemapfileline$,loop%,1)
						Select imagemapchar$
							Case "="," "
								hotspotcoord%=0
								imagemapword$=nil$		
							Case ","
								this\hotspot[this\hotspots%]\coord[hotspotcoord%]=imagemapword$
								hotspotcoord%=hotspotcoord%+1
								imagemapword$=nil$
							Case Chr(34),Chr(39);quotes
								If hotspotcoord%>0 Or imagemapword$<>nil$
									this\hotspot[this\hotspots%]\coord[hotspotcoord%]=imagemapword$								
									Exit
								EndIf	
							Default
								imagemapword$=imagemapword$+imagemapchar$	
						End Select
					Next
					
				Case "href" ;command$
					For loop% = loop%+1 To imagemapfilelinelength%
						imagemapchar$=Mid$(imagemapfileline$,loop%,1)
						Select imagemapchar$
							Case "="," "
								imagemapword$=nil$
							Case Chr(34),Chr(39);quotes
								If imagemapword$<>nil$
									this\hotspot[this\hotspots%]\command$=imagemapword$
									Exit
								EndIf	
							Default 
								imagemapword$=imagemapword$+imagemapchar$	
						End Select
					Next
					
				Case "target" 
					For loop% = loop%+1 To imagemapfilelinelength%
						imagemapchar$=Mid$(imagemapfileline$,loop%,1)
						Select imagemapchar$
							Case "="," "
								imagemapword$=nil$
							Case Chr(34),Chr(39);quotes
								If imagemapword$<>nil$
									this\hotspot[this\hotspots%]\target$=imagemapword$
									Exit
								EndIf	
							Default 
								imagemapword$=imagemapword$+imagemapchar$	
						End Select
					Next
						
			End Select
		Next	
	Wend
	Return this
End Function

Function imagemapDestroy(this.imagemap)
	;Purpose: Removes imagemap, hotspots, and image from memory
	;Parameters: imagemap object
	;Return: none
	If this<>Null
		For loop = 1 To hotspots%
			If this\hotspot[loop%]<>Null
				Delete this\hotspot[loop%] 
			EndIf
		Next
		If this\image% FreeImage(this\image)
		Delete this
	EndIf
End Function

Function imagemapUpdate()
	;Purpose: Checks for mouse and hotspot events. called in main loop
	;Parameters: none
	;Return: none
	this.imagemap=imagemapCurrent
	If imagemapCurrent\state%
		
		If this\image DrawBlock(this\image%,this\x,this\y)
		
		;check mouse and hotspot events
		For loop% = 1 To this\hotspots%
			
			If MouseX()>=this\hotspot[loop%]\coord%[0] And MouseX()<this\hotspot[loop%]\coord%[2] 
				If MouseY()>=this\hotspot[loop%]\coord%[1] And MouseY()<this\hotspot[loop%]\coord%[3] 
					
					Color 0,255,0
					
					If MouseDown(1)
						
						Color 255,0,0 ; do something
						 								
					EndIf
					
					Rect this\hotspot[loop%]\coord%[0],this\hotspot[loop%]\coord%[1],this\hotspot[loop%]\coord%[2]-this\hotspot[loop%]\coord%[0],this\hotspot[loop%]\coord%[3]-this\hotspot[loop%]\coord%[1],0
					
				EndIf
			EndIf	
		Next
	EndIf
End Function


;DEMO ==========================================================================================
.MAIN
Graphics 512,256,16,2
SetBuffer(BackBuffer())

;load a imagemap
imagemapCurrent=imagemapLoad("test","test","test.jpg")

While Not KeyDown(1)
	imagemapUpdate()
	Flip()
Wend

imagemapDestroy(imagemapCurrent)

End
