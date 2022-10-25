; ID: 3100
; Author: zoqfotpik
; Date: 2014-01-24 12:11:53
; Title: Run Length Encoding
; Description: Run length encoding for game levels

Function writelevel()

	Local levname$
	
	levname$ = Input ("Filename:")
	Local success=CreateFile(levname$)
	If Not success RuntimeError "error creating file"	
	Local file:TStream =WriteFile(levname$)

	Local x = 0  ' set to upper left of array
	Local y = 0
	Local currentrun = 0
	Local currenttile = 0
	
	Print "trying to write file..."
	While y < 127
	currenttile = maparray[x,y]
	x = x + 1
	If x > 127
		y = y + 1
		x = 0
	EndIf
	
	If maparray[x,y] = currenttile
		currentrun = currentrun + 1
	
	Else  ' run is broken
		Print "writing line"
		WriteLine file,currentrun + 1+ "," + currenttile 
		currentrun = 0
	EndIf
	Wend
	WriteLine file,"10000,1"
	CloseFile file
End Function

Function readlevel()
		Local levname$
		Local file:TStream
		Local lines:String[]
		Local line:String
		Local values:String[]
		Local templine:String[]
		Local teststringiter:String
		Local x:Int
		Local y:Int
		Local i:Int
		
		levname$ = Input ("Filename:")
		
		file=ReadFile(levname)

		If Not file RuntimeError "could not open file " + levname
		
		While Not Eof(file)
		        templine = ReadLine(file).split("\n")
				templine = templine[0].split(",")
				Print templine[0]
			    printtilename(Int(templine[0]))
				i = 0
				Local runnumber:Int = Int(templine[0])
		     	Local tiletype:Int = Int(templine[1])
				For i = 0 To runnumber-1
			     	maparray[x,y]=tiletype
				    x = x + 1
				    If x > 127
				    	x = 0
				    	y = y + 1
				    EndIf
				 Next
		Wend
		CloseStream file
End Function
