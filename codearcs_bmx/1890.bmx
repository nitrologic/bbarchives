; ID: 1890
; Author: Perturbatio
; Date: 2006-12-26 07:17:13
; Title: Read/Write INI File (BMX)
; Description: Simple INI file reader/writer

SuperStrict

Function SplitString:TList(inString:String, Delim:String)
	Local tempList : TList = New TList
	Local currentChar : String = ""
	Local count : Int = 0
	Local TokenStart : Int = 0
	
	If Len(Delim)<1 Then Return Null
	
	inString = Trim(inString)
	
	For count = 0 Until Len(inString)
		If inString[count..count+1] = delim Then
			tempList.AddLast(inString[TokenStart..Count])
			TokenStart = count + 1
		End If
	Next
	tempList.AddLast(inString[TokenStart..Count])	
	Return tempList
End Function


Type TIniSection
	Field Name:String
	Field Values:TMap
	
	
	Method SetValue(key:String, value:Object)
		Values.Insert(Key, Value)
	End Method
	
	
	Method GetValue:String(Key:String)
		Return String(Values.ValueForKey(Key))
	End Method
	
	
	Method DeleteValue(Key:String)
		Values.Remove(Key)
	End Method
	
	
	Method GetSectionText:String()
		Local result:String = "["+Name+"]~r~n"
		
		For Local s:Object = EachIn Values.keys()
			result = result + String(s) + "=" + String(Values.ValueForKey(s)) + "~r~n"
		Next
		
		Return result+"~r~n"
	End Method
	
	
	Function Create:TIniSection(name:String)
		Local tempSection:TIniSection = New TIniSection
			tempSection.name = name
			tempSection.Values = New TMap
		Return tempSection
	End Function
	
End Type



Type TSectionList
	Field _Sections:TIniSection[]
	
	Method GetSection:TIniSection(sectionName:String)
	
		For Local section:TIniSection = EachIn _Sections
			If section.Name = sectionName Then Return section
		Next
		
		Return Null
		
	End Method
	
	
	Method AddSection:TIniSection(sectionName:String)
		Local currentLength:Int = Len(_Sections)
		
			_Sections = _Sections[..currentLength+1]
			_Sections[currentLength] = TIniSection.Create(sectionName)
		
		Return _Sections[currentLength]
	End Method
	
	
	Method RemoveSection:Int(sectionName:String)
		Local currentLength:Int = Len(_Sections)
		
		For Local i:Int = 0 To currentLength-1
			If _Sections[i].Name = sectionName Then
				If i < currentLength-1 Then
					For Local x:Int = i To currentLength-2
						_Sections[x] = _Sections[x+1]
					Next
				EndIf
				_Sections = _Sections[..currentLength-1]
				
				Return True
				
			EndIf
		Next
		
		Return False
	End Method
	
	
	Function Create:TSectionList()
		Local tempSectionList:TSectionList = New TSectionList
			
		Return tempSectionList
	End Function
	
End Type



Type TPertIni
	Field Filename:String
	Field Loaded:Int
	Field Saved:Int
	Field Sections:TSectionList
	
	
	Method Load:Int()
		Local file:TStream
		Local line:String
		Local tempList:TList
		Local tempArray:Object[]
		Local currentSection:String = ""
		Local error:String
		
		
		If FileType(Filename) = 1 Then

			file:TStream = ReadStream(FileName)
			
			While Not Eof(file)
				
				line = Trim(ReadLine(file))
				
				
				
				If Not (Line[..1] = ";") Then
					
					If Line[..1] = "[" And Line[Len(Line)-1..] = "]" Then
						currentSection = Line[1..Len(Line)-1]
						
						AddSection(currentSection)
					Else
						If Len(currentSection) > 0 And Len(line) > 0 Then
							tempList = SplitString(Line, "=")
							If tempList Then
								tempArray = tempList.ToArray();
								SetSectionValue(currentSection, String(tempArray[0]), String(tempArray[1]))
							EndIf
						Else If Len(Line) > 0 Then
							Return False 'no section header found'
						EndIf
					EndIf
				EndIf
			Wend
			
			CloseStream(file)
		
		EndIf
		
		Return False
	End Method
	
	
	Method Save:Int(Overwrite:Int = False)
		Local file:TStream
		Local ft:Int = FileType(Filename)
		
		If ft = 0  Or (ft = 1 And Overwrite = True) Then
			file:TStream = WriteStream(FileName)
			WriteString(file, GetIniText())
			CloseStream(file)
		Else
			Return False
		EndIf
		
	End Method
	
	
	Method AddSection:TIniSection(sectionName:String)
		Return Sections.AddSection(sectionName)
	End Method
	
	
	Method GetSection:TIniSection(sectionName:String)
		Return Sections.GetSection(sectionName)
	End Method
	
	
	Method SetSectionValue(sectionName:String, key:String, value:String)
		For Local i:Int = 0 To Len(Sections._Sections) -1
			If Sections._Sections[i].name = sectionName Then
				Sections._Sections[i].SetValue(key, value)
				Return
			EndIf
		Next
	End Method
	
	
	Method DeleteSectionValue(sectionName:String, key:String)
		For Local i:Int = 0 To Len(Sections._Sections) -1
			If Sections._Sections[i].name = sectionName Then
				Sections._Sections[i].DeleteValue(key)
				Return
			EndIf
		Next
	End Method
	
	
	Method GetSectionValue:String(sectionName:String, key:String)
		For Local i:Int = 0 To Len(Sections._Sections) -1
			If Sections._Sections[i].name = sectionName Then
				Return Sections._Sections[i].GetValue(key)
			EndIf
		Next
	End Method
	
	
	Method GetIniText:String()
		Local result:String
			For Local section:TIniSection = EachIn Sections._Sections
				 result:+section.GetSectionText()
			Next
		Return result
	End Method
	
	
	Function Create:TPertIni(filename:String)
		Local tempIni:TPertIni = New TPertIni
			tempIni.Filename = filename
			tempIni.Sections:TSectionList = TSectionList.Create()
		Return tempIni
	End Function
End Type


Local ini:TPertIni = TPertIni.Create("test.ini")
	'ini.AddSection("testSection")
	
	'ini.SetSectionValue("testSection", "testValue", "1200")
	'ini.SetSectionValue("testSection", "testValue2", "1300")
	'ini.SetSectionValue("testSection", "testValue3", "1500")
	'ini.SetSectionValue("testSection", "testValue5=4", "1700")
	
ini.Load()
Print ini.GetIniText()
Print "value:  " + ini.getSectionValue("testSection", "testValue2")
Print "value2: " + ini.getSectionValue("testSection2", "testValue2")
'ini.DeleteSectionValue("testSection", "testValue2")
'ini.Save(True)
