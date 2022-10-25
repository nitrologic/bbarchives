; ID: 1860
; Author: Nicholas
; Date: 2006-11-12 05:03:57
; Title: INI-like configuration reading and writing
; Description: INI-like code

Main code :

Strict

Type TConfig
	Field internalFoundString$
	
	Function create:TConfig()
		Return New TConfig
	EndFunction
		
	Method addConfigByte:Byte(fileName$,searchString$,value:Byte)
		Return addConfigString(fileName$,searchString$,String(value))
	EndMethod
	
	Method addConfigShort:Byte(fileName$,searchString$,value:Short)
		Return addConfigString(fileName$,searchString$,String(value))
	EndMethod
	
	Method addConfigInt:Byte(fileName$,searchString$,value:Int)
		Return addConfigString(fileName$,searchString$,String(value))
	EndMethod
	
	Method addConfigLong:Byte(fileName$,searchString$,value:Long)
		Return addConfigString(fileName$,searchString$,String(value))
	EndMethod
	
	Method addConfigFloat:Byte(fileName$,searchString$,value:Float)
		Return addConfigString(fileName$,searchString$,String(value))
	EndMethod
	
	Method addConfigDouble:Byte(fileName$,searchString$,value:Double)
		Return addConfigString(fileName$,searchString$,String(value))
	EndMethod
	
	Method readConfigString$(fileName$,searchString$,defaultText$)
		If readConfig(fileName$,searchString$)=False
			Return defaultText$
		Else
			Return internalFoundString$
		EndIf
	EndMethod
	
	Method readConfigByte:Byte(fileName$,searchString$,defaultValue:Byte)
		If readConfig(fileName$,searchString$)=False
			Return defaultValue
		Else
			Return Byte(internalFoundString$)
		EndIf
	EndMethod
	
	Method readConfigShort:Short(fileName$,searchString$,defaultValue:Short)
		If readConfig(fileName$,searchString$)=False
			Return defaultValue
		Else
			Return Short(internalFoundString$)
		EndIf
	EndMethod
	
	Method readConfigInt:Int(fileName$,searchString$,defaultValue:Int)
		If readConfig(fileName$,searchString$)=False
			Return defaultValue
		Else
			Return Int(internalFoundString$)
		EndIf
	EndMethod
	
	Method readConfigLong:Long(fileName$,searchString$,defaultValue:Long)
		If readConfig(fileName$,searchString$)=False
			Return defaultValue
		Else
			Return Long(internalFoundString$)
		EndIf
	EndMethod
	
	Method readConfigFloat:Float(fileName$,searchString$,defaultValue:Float)
		If readConfig(fileName$,searchString$)=False
			Return defaultValue
		Else
			Return Float(internalFoundString$)
		EndIf
	EndMethod
	
	Method readConfigDouble:Double(fileName$,searchString$,defaultValue:Double)
		If readConfig(fileName$,searchString$)=False
			Return defaultValue
		Else
			Return Double(internalFoundString$)
		EndIf
	EndMethod
	
	Method addConfigString:Byte(fileName$,searchString$,value$)
	Local inHandle:TStream,outHandle:TStream
	Local outFileName$
	Local temp$
	Local found:Byte
	
		outFileName$="TEMP_"+fileName$
		inHandle=OpenStream(fileName$,True,False)
		If inHandle=Null Then Return False
		
		outHandle=OpenStream(outFileName$,False,True)
		If outHandle=Null
			CloseStream(inHandle)
			Return False
		EndIf
		
		CopyStream(inHandle,outHandle)
		
		CloseStream(inHandle)
		CloseStream(outHandle)
		If Not DeleteFile(fileName$)
			Return False
		EndIf
		
		inHandle=OpenStream(outFileName$,True,False)
		If inHandle=Null Then Return False
		outHandle=OpenStream(fileName$,False,True)
		If outHandle=Null 
			CloseStream(inHandle)
			Return False
		EndIf
	
		If Right$(searchString$,1)<>"=" Then searchString$:+"="
		
		found=False	
		While Eof(inHandle)=False
			temp$=ReadLine$(inHandle)
			If Left$(temp$,Len(searchstring$))=searchString$
				If value$=""
					found=True
				Else
					WriteLine(outHandle,searchstring$+value$)
					found=True
				EndIf
			Else
				WriteLine(outHandle,temp$)
			EndIf
		EndWhile
		
		If found=False
			WriteLine(outHandle,searchstring$+value$)
		EndIf
		
		CloseStream(inHandle)
		CloseStream(outHandle)
		DeleteFile(outFileName$)
		Return True
		
	EndMethod
					
	Method readConfig:Byte(fileName$,searchString$)
	Local handle:TStream
	Local temp$
	Local found$
	Local isFound:Int
	
		isFound=False
		handle=OpenStream(fileName$,True,False)
		If handle
			If Right$(searchString$,1)<>"=" Then searchString$:+"="
	
			While Eof(handle)=False And isFound=False
				temp$=ReadLine$(handle)
				If Left$(temp$,Len(searchstring$))=searchString$
					internalFoundString$=Mid$(temp$,Len(searchString$)+1)
					isFound=True
				EndIf
			EndWhile
			
			CloseStream(handle)
		EndIf
		
		Return isFound
	EndMethod
EndType
							
Test Code :

Strict

Import "TConfig.bmx"

Global config:TConfig=TConfig.create()

Print config.readConfigString("Test.txt","Output","Example")
Print config.addConfigString("Test.txt","p","This is some text")
Print config.addConfigByte("Test.txt","Byte",127)
Print config.readConfigByte("Test.txt","Byte",0)
Print config.addConfigFloat("Test.txt","Float",12.45)
