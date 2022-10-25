; ID: 2214
; Author: JoshK
; Date: 2008-02-08 20:01:19
; Title: Newton Game Dynamics header converter
; Description: Converts Newton Game Dynamics header file to BlitzMax code

in:TStream=ReadFile("JointLibrary.h")
out:TStream=WriteFile("JointLibrary.bmx")

Type TNewtonFunc Extends TTypeDef
	Global list:TList=New TList

	Method New()
		list.addlast(Self)
		link.remove
	EndMethod
	
EndType

Type TNewtonParameter
	Field name$
	Field ptype$
EndType

Type TTypeDef
	Global list:TList=New TList

	Field link:TLink
	Field name$
	Field returntype$
	Field params:TList=New TList
	Field paramstring$
	
	Method New()
		link=list.addfirst(Self)
	EndMethod
	
	Method EvaluateParams(s$)
		Local sarr$[],parr$[]
		
		sarr=s.split(",")
		
		For n=0 To sarr.length-1
			If Trim(sarr[n])<>""
				
				param:TNewtonParameter=New TNewtonParameter
				params.addlast(param)				
				
				parr=sarr[n].split("")
				param.name=parr[parr.length-1]

				r$=""
				For m=0 To parr.length-2
					r:+parr[m]
					If m<parr.length-2 r:+" "
				Next

				Select r
					Case "int","unsigned" param.ptype="Int"
					Case "dFloat" param.ptype="Float"
					Case "dFloat*","const dFloat*" param.ptype="Byte Ptr"
					Case "void*" param.ptype="Byte Ptr"
					Case "size_t" param.ptype="Int"
					Default
						param.ptype="Byte Ptr"
						For typedef:TTypeDef=EachIn TTypeDef.list
							If typedef.name=r
								param.ptype=""
								param.name=typedef.name
								If typedef.returntype<>"" param.name:+":"+typedef.returntype
								param.name:+"("+typedef.paramstring+")"
								Exit
							EndIf
						Next						
				EndSelect
				If param.name.contains("*")
					param.name=param.name.Replace("*","")
					If param.ptype="Float" param.ptype="Byte Ptr" Else param.ptype="Byte Ptr"
				EndIf
				param.name=param.name.Replace("[]","")
				If param.name="end" param.name="_end"
				If param.name="ptr" param.name="_ptr"
			EndIf
						
		Next
		
		n=0
		s$=""
		For param:TNewtonParameter=EachIn params
			s:+param.name
			If param.ptype<>"" s:+":"
			s:+param.ptype+","
			n:+1
		Next
		If n s=Left(s,s.length-1)
		
		paramstring=s
		
		
	EndMethod
	
EndType


Local sarr:String[]
Local parr:String[]
Local farr:String[]

While Not in.Eof()

	s$=ReadCLine(in)
	If Trim(s)="" Continue
	
	'Notify s
	
	'If s.contains("NewtonCreateCollisionFromSerialization") Notify s
	
	sarr=s.split("")
	Select sarr[0]
	
		Case "typedef"
			If s.contains("(")
				td:TTypeDef=New TTypeDef
				'Return type
				td.name=sarr[2]
				
				parr=td.name.split(")")
				td.name=parr[0]
				
				parr=s.split("(")
				params$=parr[2]
				
				'Notify params
				
				params=params.Replace("(","")
				params=params.Replace(")","")
				params=params.Trim()
				
				td.name=td.name.Replace("(","")
				td.name=td.name.Replace(")","")
				td.name=td.name.Replace("*","")
				td.name=td.name.Trim()
				
				'Notify td.name
				Select sarr[1]
					Case "void" td.returntype=""
					Case "int","unsigned" td.returntype="Int"
					Case "void*" td.returntype="Byte Ptr"
					Case "dFloat" td.returntype="Float"
					Case "dFloat*" td.returntype="Byte Ptr"
					Default
						td.returntype="Byte Ptr"
				EndSelect
				
				'Notify params
				td.EvaluateParams params
				'Notify td.paramstring
				
				'td.name:+"("+td.paramstring+")"
				'
				'Notify td.name
				'Print td.name
								
				'If td.name.contains("*")
				'	td.name=td.name.Replace("*","")
				'	td.returntype="Byte Ptr"
				'EndIf
			EndIf
			
		Case "NEWTON_API","JOINTLIBRARY_API"
			func:TNewtonFunc=New TNewtonFunc
			
			'Function name
			farr=sarr[2].split("(")
			func.name=farr[0]		
			
			'Print func.name
			
			'Return type
			Select sarr[1]
				Case "void" func.returntype=""
				Case "int","unsigned" func.returntype="Int"
				Case "void*" func.returntype="Byte Ptr"
				Case "dFloat" func.returntype="Float"
				Case "dFloat*" func.returntype="Byte Ptr"
				Default
					func.returntype="Byte Ptr"
			EndSelect
			If func.name.contains("*")
				func.name=func.name.Replace("*","")
				func.returntype="Byte Ptr"
			EndIf
			
			'Parse parameters
			sarr=s.split("(")
			s=sarr[1]
			sarr=s.split(")")
			s=sarr[0]
			
			func.EvaluateParams(s)
			
			Rem
			sarr=s.split(",")
	
			For n=0 To sarr.length-1
				If Trim(sarr[n])<>""
				
					param:TNewtonParameter=New TNewtonParameter
					func.params.addlast(param)				
				
					parr=sarr[n].split("")
					param.name=parr[parr.length-1]
					'Notify "!"+func.name+"!"
					
					r$=""
					For m=0 To parr.length-2
						r:+parr[m]
						If m<parr.length-2 r:+" "
					Next
					
					Select r
						Case "int","unsigned" param.ptype="Int"
						Case "dFloat" param.ptype="Float"
						Case "dFloat*" param.ptype="Byte Ptr"
						Case "void*" param.ptype="Byte Ptr"
						Default param.ptype="Byte Ptr"
					EndSelect
					If param.name.contains("*")
						param.name=param.name.Replace("*","")
						param.ptype="Byte Ptr"
					EndIf
					param.name=param.name.Replace("[]","")
					If param.name="end" param.name="_end"
				EndIf
							
			Next
			EndRem
			
	EndSelect
	
Wend

in.close()

out.WriteLine "Import Pub.Win32"
out.WriteLine "Strict"
out.WriteLine ""

For func:TNewtonFunc=EachIn TNewtonFunc.list
	
	s="Global "+func.name
	If func.returntype<>"" s:+":"+func.returntype
	s:+"("
	
	Rem
	n=0
	For param:TNewtonParameter=EachIn func.params
		s:+param.name
		If param.ptype<>"" s:+":"
		s:+param.ptype+","
		n:+1
	Next
	If n s=Left(s,s.length-1)
	EndRem
	
	s:+func.paramstring
		
	s:+") ~qc~q"
	
	out.WriteLine s
	
Next

out.WriteLine ""
out.WriteLine "Function InitNewton:Int()"
out.WriteLine "	Global hLib:Int"
out.WriteLine ""
out.WriteLine "	Select hLib"
out.WriteLine "		Case -1 Return"
out.WriteLine "		Case 0 'Continue"
out.WriteLine "		Default Return hLib"	
out.WriteLine "	EndSelect"
out.WriteLine "	hLib=LoadLibraryA(~qNewton.dll~q)"
out.WriteLine "	If Not hLib"
out.WriteLine "		hLib=-1"
out.WriteLine "		Return"
out.WriteLine "	EndIf"
out.WriteLine ""

For func:TNewtonFunc=EachIn TNewtonFunc.list
	out.WriteLine "	"+func.name+"=GetProcAddress(hLib,~q"+func.name+"~q)"
Next

out.WriteLine ""
out.WriteLine "	Return hLib"
out.WriteLine "EndFunction"

out.close()

End


Function ReadCLine:String(stream:TStream)
	s$=""
	Local sarr:String[]
	Repeat
		If stream.Eof() Return
		c$=Trim(stream.ReadLine())
		sarr=c.split("//")
		c=sarr[0]
		c=Trim(c)
		s:+c
		If Right(c,1)=";"
			Return Left(s,s.length-1)
		EndIf
	Forever
EndFunction
