; ID: 2848
; Author: JoshK
; Date: 2011-05-08 14:18:48
; Title: Program settings with reflection
; Description: This code will handle settings loading and saving automatically, with a nice interface for accessing the values in your program.

SuperStrict

Import brl.reflection
Import brl.stream

Type TSettings
	
	Field greeting:String="Hello"
	Field color:Int[]=[255,128,64]
	
	Method Save:Int(url:Object)
		Local typeid:TTypeId
		Local f:TField
		Local stream:TStream
		Local o:Object
		Local otypeid:TTypeId
		Local etypeid:TTypeId
		Local n:Int
		Local element:Object
		Local value:String
		
		stream=WriteStream(url)
		If Not stream Return False
		
		typeid=TTypeId.ForObject(Self)
		
		For f=EachIn typeid.enumfields()
			
			Select f._typeid
			
			Case IntTypeId
				stream.WriteLine f.name()+"="+f.GetInt(Self)
			
			Case FloatTypeId,DoubleTypeId
				stream.WriteLine f.name()+"="+f.GetFloat(Self)
			
			Case StringTypeId
				stream.WriteLine f.name()+"=~q"+f.getstring(Self)+"~q"
			
			Default
				If f._typeid.Extendstype(ArrayTypeId)
					o=f.Get(Self)
					otypeid=TTypeId.ForObject(o)
					value=""
					For n=0 To otypeid.ArrayLength(o)-1
						element=otypeid.GetArrayElement(o,n)
						If n>0 value:+","
						value:+String(element)	
					Next
					stream.WriteLine f.name()+"="+value
				EndIf
			EndSelect
			
		Next
		stream.close()
		
		Return True
	EndMethod
	
	Method Load:Int(url:Object)
		Local map:TMap=New TMap
		Local p:Int
		Local key:String
		Local value:String
		Local typeid:TTypeId
		Local stream:TStream
		Local s:String
		Local f:TField
		Local fieldid:TTypeId
		Local o:Object
		Local otypeid:TTypeId
		Local sarr:String[]
		Local n:Int
		
		stream=ReadStream(url)
		If Not stream Return False
		
		'Load pairs into map
		While Not stream.Eof()
			s=stream.ReadLine()
			p=s.Find("=")
			If p>-1
				key=s[..p]
				value=s[p+1..]
				map.insert key,value
			EndIf
		Wend
		stream.close()
		
		typeid=TTypeId.ForObject(Self)
		For f=EachIn typeid.enumfields()
			key=f.name()
			If map.contains(key)
				value=String(map.valueforkey(key))
				
				Select f._typeId
				
				Case StringTypeId
					value=value[1..]
					value=value[..value.length-1]
					f.setstring(Self,value)
				
				Case FloatTypeId
					f.setstring(Self,Float(value))
				
				Case DoubleTypeId
					f.setstring(Self,Double(value))
				
				Case IntTypeId
					f.setstring(Self,Int(value))
				
				Default
					If f._typeid.Extendstype(ArrayTypeId)
						o=f.Get(Self)
						otypeid=TTypeId.ForObject(o)
						sarr=value.split(",")
						sarr=sarr[..otypeid.ArrayLength(o)]
						For n=0 To otypeid.ArrayLength(o)-1
							otypeid.SetArrayElement(o,n,sarr[n])
						Next
					EndIf
				
				EndSelect
			EndIf
		Next
		
		Return True
	EndMethod
	
EndType

Local settings:TSettings=New TSettings
settings.Save("settings.cfg")
settings.color=[0,0,0]
settings.Load("settings.cfg")
Print settings.greeting
Print settings.color[0]+", "+settings.color[1]+", "+settings.color[2]
End
