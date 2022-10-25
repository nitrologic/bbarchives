; ID: 2878
; Author: BlitzSupport
; Date: 2011-08-08 14:52:47
; Title: Aspect Ratio Helper Thing
; Description: Aspect ratio listing/lookup

' Updated as I needed some sorting options to output a couple of lists...

SuperStrict

' Sort modes...

Const ASPECT_NAME:Int = 1
Const ASPECT_RATIO:Int = 2
Const ASPECT_WIDTH:Int = 3

Type Resolution

	' See license for the required text file at the end of
	' "www.hi-toro.com/blitz/misc/computer_resolutions.txt" --
	' information from Wikipedia under Creative Commons
	' Attribution-ShareAlike License, hence separate file.

	' (This source code is still in the public domain.)

	Global ResList:TList = CreateList ()
	Global SortMode:Int = ASPECT_WIDTH

	Field name:String
	Field width:Int
	Field height:Int
	Field aspectreadable:String
	Field aspectnumerical:String ' Stored as string as float will be inaccurate
	
	Method New ()
		ListAddLast ResList, Self
	End Method
	
	Method Compare:Int (obj:Object)

		Select SortMode
			Case ASPECT_NAME
				Return Self.AspectName () > Resolution (obj).AspectName ()
			Case ASPECT_RATIO
				Return Self.AspectRatio () > Resolution (obj).AspectRatio ()
			Case ASPECT_WIDTH
				Return Self.AspectWidth () > Resolution (obj).AspectWidth ()
			Default
				Return 0
		End Select

	End Method
	
	Method AspectName:String ()
		Return name
	End Method
	
	Method AspectWidth:Int ()
		Return width
	End Method
	
	Method AspectHeight:Int ()
		Return height
	End Method
	
	' This is called AspectRatio as this is what most people would quote, eg. "16:9"...
	
	Method AspectRatio:String ()
		Return aspectreadable
	End Method
	
	' This is the float version, for calculations (returns width part of ratio, to be compared to 1.0 for height)...
	
	Method Aspect:Float ()
		Return Float (aspectnumerical)
	End Method

	' Just a dumb listing...
	
	Function ListAspectInfo:Int ()
		SortList ResList
		For Local res:Resolution = EachIn Resolution.ResList
			Print res.AspectName () + ": " + res.AspectWidth () + " x " + res.AspectHeight () + " (" + res.AspectRatio () + ", or " + res.Aspect () + " as float)"
		Next
	End Function
	
	Function ListRatios:Int ()
		SortList ResList
		For Local res:Resolution = EachIn Resolution.ResList
			Print res.AspectRatio () + "~t~t" + res.AspectWidth () + " x " + res.AspectHeight ()
		Next
	End Function
	
	Function ListResolutions:Int ()
		SortList ResList
		For Local res:Resolution = EachIn Resolution.ResList
			Print res.AspectWidth () + " x " + res.AspectHeight () + "~t~t" + res.AspectRatio ()
		Next
	End Function
	
	Function AspectFromRes:String (width:Int, height:Int)
		
		For Local res:Resolution = EachIn Resolution.ResList
			If res.width = width And res.height = height Then Return res.AspectName ()
		Next
		
		Return "[Unknown aspect ratio]"
		
	End Function
	
	Function ResFromAspect:String (aspect:String)
		
		For Local res:Resolution = EachIn Resolution.ResList
			If Left (res.AspectName (), Len (aspect)) = aspect Then Return String (res.width) + " x " + String (res.height)
		Next
		
		Return "[Unknown aspect ratio]"
		
	End Function

	Function ResObjFromRes:Resolution (width:Int, height:Int)
		
		For Local res:Resolution = EachIn Resolution.ResList
			If res.width = width And res.height = height Then Return res
		Next
		
		Return Null
		
	End Function
	
	Function ResObjFromAspectName:Resolution (aspect:String)
		
		For Local res:Resolution = EachIn Resolution.ResList
			If Left (res.AspectName (), Len (aspect)) = aspect Then Return res
		Next
		
		Return Null
		
	End Function
	
	Function ReadResolutions:Int ()
	
		Local res:Resolution
		
		Local file:TStream = ReadFile ("computer_resolutions.txt")
		
		If file
		
			While Not Eof (file)
				
				Local in:String = ReadLine (file)
				
				If in = "" Then Exit
				
				Local char:Int
				Local index:Int = 0
				Local chunk:String = ""
		
				' One line split by tabs. I seem to be missing the last element
				' somehow, but it's only the pixel count, ie. width * height...
				
				Local chunkcount:Int = 0
				
				While index < Len (in)
					
					char = in [index]
					
					Select char
					
						Case 9 ' Tab
						
							chunkcount = chunkcount + 1
							
							Select chunkcount
								Case 1
									res = New Resolution
									res.name = chunk
								Case 2
									res.width = Int (chunk)
								Case 3
									' "x"
								Case 4
									res.height = Int (chunk)
								Case 5
									res.aspectreadable = chunk
								Case 6
									res.aspectnumerical = chunk + ":1.0"
							End Select
		
							chunk = ""
							
						Default
						
							chunk = chunk + Chr (char)
					
					End Select
					
					index = index + 1
					
				Wend
				
			Wend
			
			CloseFile file
			
			Return True
			
		EndIf
	
	End Function

End Type

' D E M O . . .

' IMPORTANT: Must read the list from "computer_resolutions.txt" first, using Resolution.ReadResolutions ()!

If Resolution.ReadResolutions () = False Then Notify "Can't read resolutions file!"; End

Print ""
Print "Aspect list..."
Print ""

Resolution.ListAspectInfo

Print ""
Print "Aspect names from resolutions..."
Print ""

Print "1024 x 768:~t~t" + Resolution.AspectFromRes (1024, 768)
Print "640  x 400:~t~t" + Resolution.AspectFromRes (640, 400)
Print "1280 x 1024:~t~t" + Resolution.AspectFromRes (1280, 1024)
Print "512  x 123:~t~t" + Resolution.AspectFromRes (512, 123)
Print "1920 x 1080:~t~t" + Resolution.AspectFromRes (1920, 1080)

Print ""
Print "Resolutions from aspect names..."
Print ""

' Note that strings can be partial, but only first in list will be returned. For
' example, VGA is listed as "VGA, MCGA (in monochome), Sun-1 color"...

Print "VGA:~t~t" + Resolution.ResFromAspect ("VGA") + " [Same as ~qVGA, MCGA (in monochome), Sun-1 color~q: " + Resolution.ResFromAspect ("VGA, MCGA (in monochome), Sun-1 color") + "]"
Print "HD 1080:~t" + Resolution.ResFromAspect ("HD 1080")
Print "SXGA:~t~t" + Resolution.ResFromAspect ("SXGA")
Print "FAKEGA:~t" + Resolution.ResFromAspect ("FAKEGA")
Print "WVGA:~t~t" + Resolution.ResFromAspect ("WVGA")

Print ""
Print "Get Resolution objects..."
Print ""

Local res1:Resolution = Resolution.ResObjFromRes (640, 480)
Local res2:Resolution = Resolution.ResObjFromAspectName ("720p")

If res1 And res2
	Print res1.AspectName ()
	Print res2.AspectRatio ()
EndIf

Print ""
Print "Sorted by resolution, resolution listed first..."
Print ""

Resolution.SortMode = ASPECT_WIDTH
Resolution.ListResolutions

Print ""
Print "Sorted by ratio, ratio listed first..."
Print ""

Resolution.SortMode = ASPECT_RATIO
Resolution.ListRatios
