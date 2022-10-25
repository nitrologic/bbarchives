; ID: 3016
; Author: Sonickidnextgen
; Date: 2013-01-13 00:08:18
; Title: Semi-automatic 2 variable replacement tool
; Description: A tool which changes the code around two variables in a file

' The backstory(Look ahead for the actual code):
Rem
	A little while back I started working on a game in BlitzMax,
	I worked on it with full intent of porting it to Monkey later down the road.
	However, I used the ListAddLast command, which doesn't work in Monkey the same way.
	As far as I know, it's not possible to make a wrapper for this, so I decided to convert those bits from my Blitz Max code.
	
	Instead of doing it myself, I made this:
End Rem

' Enable strict or SuperStrict
SuperStrict

' Framework
Framework BRL.Blitz

' Imports:
Import BRL.Stream
Import BRL.StandardIO
Import BRL.FileSystem
Import BRL.Retro
Import BRL.PolledInput

' Globals:
Global ConsoleOutput:Byte = True

' Start the program
Main()

' The main program
Function Main:Int()
	' Read our configuration file(If it doesn't exist, it'll be created)
	Local ConfigData:String[] = ReadConfig("config.txt")

	' Define our original data's string:
	Local Data:String = ConfigData[0]
	
	' Define our new data's string:
	Local NewData:String = ConfigData[1]
	
	' Define our location data:
	Local Files:String = ConfigData[2]
	Local _Files:String[]
	
	' Check to see if our Files variable is set to the AppArgs:
	If (Upper(Files) = "APPARGS") Then
		_Files = AppArgs
	Else
		_Files = [Files]
	EndIf
	
	' Define our recursion variable, and check our recursion setting
	Local Recursive:Int
	
	If (Upper(ConfigData[3]) <> "FALSE" And ConfigData[3] <> "0") Then
		Recursive = 1
	EndIf
	
	' Create our white list object
	Local WhiteList:TList = New TList
	Local WListStr:String
	
	' Check if we have any data coming in
	If (ConfigData[4] <> "") Then
		' Check if we have more than one file whitelisted:
		If (Instr(ConfigData[4], ",")) Then
			' If there's more than one file extension, then do the following:
			
			' Repeat until we're to the last white listed file extension
			Repeat
				' Add the calculated string to the white list:
				WListStr = Left(ConfigData[4], Instr(ConfigData[4], ",")-1)
				WhiteList.AddLast(WListStr)
				
				' Modify ConfigData[4], so it doesn't hold the data we added to the white list.
				ConfigData[4] = Right(ConfigData[4], Len(ConfigData[4]) - Len(WListStr) - 1)
				WListStr = ""
			Until Not Instr(ConfigData[4], ",")
			
			' Find our last file extension, and add it to the white list:
			If (ConfigData[4] <> "") Then
				WListStr = ConfigData[4]
				WhiteList.AddLast(WListStr)
				WListStr = ""
			EndIf
		Else
			' If we only have one entry, add it to the white list.
			WhiteList.AddLast(ConfigData[4])
		EndIf
	Else
		' If we didn't find anything, just add a blank string to the list.
		WhiteList.AddLast("")
	EndIf
	
	Console("Beginning search routine...")
	
	' Grab the file(s) using the extracted config data:
	ReplaceFiles(_Files, Data, NewData, Recursive, True, 0, WhiteList)
	
	' We're done, now wait for as long as needed(If ConsoleOutput is enabled):
	Console("Search routine finished.")
	If (ConsoleOutput = True) Then WaitChar()
End Function

Function ReadConfig:String[](Location:String)
	' Create an array holding all 5 return strings
	Local ReturnData:String[5]
	
	' Attempt to open a stream for the configuration.
	Local Stream:TStream = ReadStream(Location)
	
	' If we can't open the stream, create the file(s) we need, then close the program:
	If (Not Stream) Then
		Stream = WriteStream(Location)
		
		Stream.WriteLine("File = AppArgs")
		Stream.WriteLine("Original = " + Chr(34) + "(*1, *2)" + Chr(34))
		Stream.WriteLine("Replaced = " + Chr(34) + "(*2, *1)" + Chr(34))
		Stream.WriteLine("Recursive = True")
		Stream.WriteLine("WhiteList = " + Chr(34) + "txt" + Chr(34))
		
		CloseStream(Stream)

		End
	EndIf
	
	' Define the needed local variables:
	Local InLine:String
	Local File:String = "AppArgs"
	Local Original:String = "(*1, *2)"
	Local Replaced:String = "(*2, *1)"
	Local Recursive:String = "1"
	Local WList:String = "txt"
	
	' Look through each line of the file:
	Repeat
		' Read the current line.
		InLine = Stream.ReadLine()
		
		' Check if we're dealing with a setting, or unneeded text:
		If (Instr(InLine, "=")) Then
			' Apply the settings as needed(I'm too lazy to give this section comments):
			
			If (Instr(Upper(InLine), "FILE") Or Instr(Upper(InLine), "FILES")) Then
				File = Right(InLine, Len(InLine) - Instr(InLine, "="))
				If (Instr(File, Chr(34))) Then
					File = Right(File, Len(File) - Len(Left(File, Instr(File, Chr(34))-1)))
					File = Replace(File, Chr(34), "")
				Else
					File = "AppArgs"
				EndIf
			EndIf
			
			If (Instr(Upper(InLine), "ORIGINAL")) Then
				Local _Original:String = Right(InLine, Len(InLine) - Instr(InLine, "="))
				If (Instr(_Original, Chr(34))) Then
					Original = _Original
					
					Original = Right(Original, Len(Original) - Len(Left(Original, Instr(Original, Chr(34))-1)))
					Original = Replace(Original, Chr(34), "")
				Else
					RuntimeError("Please use quotes on 'Original' & Replaced")
				EndIf
			EndIf
			
			If (Instr(Upper(InLine), "REPLACED")) Then
				Local _Replaced:String = Right(InLine, Len(InLine) - Instr(InLine, "="))
				If (Instr(_Replaced, Chr(34))) Then
					Replaced = _Replaced
					
					Replaced = Right(Replaced, Len(Replaced) - Len(Left(Replaced, Instr(Replaced, Chr(34))-1)))
					Replaced = Replace(Replaced, Chr(34), "")
				Else
					RuntimeError("Please use quotes on 'Replaced' & Original")
				EndIf
			EndIf
			
			If (Instr(Upper(InLine), "RECURSIVE")) Then
				Recursive = Right(InLine, Len(InLine) - Instr(InLine, "="))
				Recursive = Replace(Recursive, " ", "")
				Recursive = Replace(Recursive, Chr(34), "")
			EndIf
			
			If (Instr(Upper(InLine), "WHITELIST")) Then
				If (Instr(InLine, Chr(34))) Then
					WList = Right(InLine, Len(InLine) - Instr(InLine, "="))
					WList = Right(WList, Len(WList) - Instr(WList, Chr(34)))
					WList = Replace(WList, Chr(34), "")
					WList = Replace(WList, " ", "")
				Else
					RuntimeError("Please use quotes with the white list. Example: " + Chr(34) + "txt, xml" + Chr(34))
				EndIf
			EndIf
		EndIf
		
		InLine = ""
	Until Stream.Eof()
	
	' Close the configuration file's stream
	CloseStream(Stream)
	
	' Assign each ID in array to the needed information:
	ReturnData[0] = Original
	ReturnData[1] = Replaced
	ReturnData[2] = File
	ReturnData[3] = Recursive
	ReturnData[4] = WList
	
	' Return the array:
	Return ReturnData
End Function

Function ReplaceFiles:Int(S:String[], Data:String, NewData:String, Recursive:Int=1, SkipDots:Byte=True, Branch:Int=0, WhiteList:TList=Null)
	' A boolean used to check if we've found a file.
	Local FileFound:Byte = False
	
	' The number of files found.
	Local FileCount:Int = 0
	
	For Local File:String = EachIn S
		' If we found nothing, this file, or another EXE file, continue.
		If (File = AppFile Or File = "" Or Right(Lower(File), 4) = ".exe") Then Continue
		
		' Check if the file/directory/other exists:
		If (FileType(File) <> 0) Then
			' Select the file type of the 'File':
			Select FileType(File)
				Case FILETYPE_FILE ' 1
					' Run the ReplaceData function, and if it isn't false, set the FileFound variable to true, and add to the file-count:
					If (ReplaceData(File, Data, NewData, WhiteList)) Then
						FileFound = True
						FileCount :+ 1
					EndIf
				Default ' FILETYPE_DIR
					' Check for recursion:s
					If (Recursive = 1 Or Recursive = 0) Then
						' Nothing to see here:
						If (Branch <> 0) Then
							Console("Branching to more directories... (Branch " + (Branch+1) + " -> Branch " + (Branch+2) + ")")
						Else
							Console("Branching to more directories... (Origin -> Branch " + (Branch+1) + ")")
							Console("Branching to more directories... (Branch " + (Branch+1) + " -> Branch " + (Branch+2) + ")")
						EndIf
						
						' Run this command again:
						ReplaceFiles(LoadDir2(File, SkipDots), Data, NewData, 1-Recursive, SkipDots, Branch+1, WhiteList)
					EndIf
			End Select
		EndIf
	Next
	
	If (Branch <> 0) Then
		Console("")
		Console("Branch " + (Branch+1) + "'s results:")
	Else
		Console("")
		Console("First search branch's results:")
	EndIf
	
	If (FileFound = True) Then
		If (FileCount <> 1) Then
			Console(FileCount + " files have been found.")
		Else
			Console("Only " + FileCount + " file was found.")
		EndIf
	Else
		Console("No files were found...")
	EndIf
	
	Console("")
	If (Branch <> 0) Then
		Console("(Branch " + (Branch+1) + " -> Branch " + (Branch) + ")")
	Else
		Console("(Branch " + (Branch+1) + " -> Origin)")
	EndIf
End Function

' Decided to add to the LoadDir command for the recursion system:
Function LoadDir2:String[](File:String, SkipDots:Byte=True)
	If (Right(File, 1)<>"/" Or Right(File, 1)<>"\") Then
		If (Instr(File, "/")) Then
			File :+ "/"
		ElseIf (Instr(File, "\")) Then
			File :+ "\"
		EndIf
	EndIf
	
	Local Dir:String[] = LoadDir(File, SkipDots)
	
	Local ID:Int
	For Local S:String = EachIn Dir
		Dir[ID] = File + S
		ID :+ 1
	Next
	
	Return Dir
End Function

Function ReplaceData:Byte(File:String, Data:String, NewData:String, WhiteList:TList=Null)
	' Start our in and out streams:
	
	' Nothing to see here:
	If (WhiteList) Then
		Local WListResponse:Byte
		For Local WListString:String = EachIn WhiteList
			If (WListString <> "") Then
				If (Left(WListString, 1) = ".") Then
					If (Instr(File, WListString)) Then
						WListResponse = True
						Exit
					EndIf
				Else
					If (Instr(File, "." + WListString)) Then
						WListResponse = True
						Exit
					EndIf
				EndIf
			Else
				WListResponse = True
				Exit
			EndIf
		Next
		
		If (WListResponse = False) Then Return False
	EndIf
	
	' Open the 'InStream' for our file:
	Local InStream:TStream = ReadStream(File)
	
	If (Not InStream) Then RuntimeError("Unable to open '" + File + "'")
	
	' If this file has "_Replaced" in it, skip it.
	If (Instr(File, "_Replaced")) Then CloseStream(InStream) ; Return False
	
	' Open the 'OutStream'(This also adds an _Replaced to our filename)
	Local OutStream:TStream = WriteStream(Replace(File, Left(StripDir(File), Instr(StripDir(File), ".")), Left(StripDir(File), Instr(StripDir(File), ".")-1) + "_Replaced."))
	
	' Our Inline and Outline variables:
	Local InLine:String, OutLine:String
	
	' Variables holding the first, last, splitting characters for our variables:
	Local VarBeginner:String = "("
	Local VarSplitter:String = ","
	Local VarEnder:String = ")"

	' Grab the needed data:
	VarBeginner = Left(Right(Left(Data, Instr(Data, "*1")), 2), 1)
	VarSplitter = Left(Replace(Right(Data, Len(Data) - Instr(Data, "*1") - 1), " ", ""), 1)
	VarEnder = Left(Replace(Right(Data, Len(Data) - Instr(Data, "*2") - 1), " ", ""), 1)
	
	' Loop until the end of the 'InStream'
	Repeat
		If (InStream.Eof()) Then Exit
		
		' Read each line in the file:
		InLine = InStream.ReadLine()
		
		' Too annoying to explain, I don't suggest looking into this:
		If (Instr(InLine, Left(Data, Instr(Data, VarBeginner)))) Then
			Local Var1:String = Right(InLine, Len(InLine) - Instr(InLine, Left(Data, Instr(Data, VarBeginner))) + Len(Left(Data, Instr(Data, VarBeginner))))
			Var1 = Left(Var1, Instr(Var1, VarSplitter) - 1)
			Var1 = Right(Var1, Len(Var1) - Instr(Var1, VarBeginner))
			
			Local Var2:String = Replace(Right(InLine, Len(InLine) - Instr(InLine, Var1+VarSplitter)+Len(Var1)), " ", "")
			Var2 = Left(Var2, Instr(Var2, VarEnder) - 1)
			Var2 = Right(Var2, Len(Var2) - Instr(Var2, Var1+",") + 1)
			Var2 = Replace(Var2, Var1+VarSplitter, "")
			
			Local FinalData:String = Replace(Replace(NewData, "*1", Var1), "*2", Var2)
			
			OutLine = InLine
			OutLine = Replace(OutLine, Replace(Replace(Data, "*1", Var1), "*2", Var2), FinalData)
		Else
			OutLine = InLine
		EndIf
		
		' Write the edited, or non-edited line to the 'OutStream'
		OutStream.WriteLine(OutLine)
		
		InLine = ""
		OutLine = ""
	Forever
	
	' Close the in and out streams:
	CloseStream(InStream)
	CloseStream(OutStream)
	
	' Return True:
	Return True
End Function

Function Console:Int(S:String)
	' If we have it enabled, print:
	If (ConsoleOutput = True) Then Print(S)
End Function
