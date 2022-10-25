; ID: 2814
; Author: JoshK
; Date: 2011-01-19 20:46:04
; Title: Best INI file reader/writer
; Description: Fast access to INI properties

SuperStrict

Import brl.map
Import brl.stream
Import brl.retro

Type TIni
	
	Field map:TMap=New TMap
	
	Method SetKey(key:String,value:String,section:String="General")
		Local submap:TMap
		
		submap:TMap=TMap(map.valueforkey(section))
		If Not submap
			submap=New TMap
			map.insert section,submap
		EndIf
		
		submap.insert key,value
	EndMethod
	
	Method GetKey:String(key:String,defaultvalue:String="",section:String="General")
		Local submap:TMap
		
		submap:TMap=TMap(map.valueforkey(section))
		If Not submap Return defaultvalue
		If Not submap.contains(key) Return defaultvalue
		Return String(submap.valueforkey(key))
	EndMethod

	Method ContainsKey:Int(key:String,section:String="General")
		Local submap:TMap
		
		submap:TMap=TMap(map.valueforkey(section))
		If Not submap Return False
		If Not submap.contains(key) Return False
		Return True
	EndMethod
	
	Method Save:Int(url:Object)
		Local stream:TStream
		Local section:String
		Local submap:TMap
		Local key:String
		Local started:Int=False
		
		stream=WriteStream(url)
		If Not stream Return False
		For section=EachIn map.keys()
			submap=TMap(map.valueforkey(section))
			If Not submap.isempty()
				If started stream.WriteLine("")
				stream.WriteLine("["+section+"]")
				For key=EachIn submap.keys()
					stream.WriteLine key+"=~q"+String(submap.valueforkey(key))+"~q"
				Next
				started=True
			EndIf
		Next
		stream.close()
		Return True
	EndMethod
	
	Function Create:TIni()
		Local ini:TIni=New TIni
		Return ini
	EndFunction
	
	Function Load:TIni(url:Object)
		Local ini:TIni
		Local stream:TStream
		Local s:String
		Local section:String
		Local sarr:String[]
		Local key:String
		Local value:String
		
		stream=ReadStream(url)
		If Not stream Return Null
		ini=New TIni
		
		While Not stream.Eof()
			s=stream.ReadLine().Trim()
			If s
				If s[0]=Asc(";") Continue
				If s[0]=Asc("[") And s[s.length-1]=Asc("]")
					section=Mid(s,2,s.length-2)
				Else
					sarr=s.split("=")
					If sarr.length=2
						key=sarr[0].Trim()
						value=sarr[1].Trim()
						If value<>"" And key<>""
							If value[0]=34 And value[value.length-1]=34
								value=Mid(value,2,value.length-2)
							EndIf
							ini.setkey(key,value,section)
						EndIf
					EndIf
				EndIf
			EndIf
		Wend
		stream.close()
		
		Return ini
	EndFunction
	
EndType
