; ID: 888
; Author: Techlord
; Date: 2004-01-16 04:33:21
; Title: Project PLASMA FPS 2004: TypeWriter][ Code Wizard
; Description: Generates a set of commonly used object functions from a type structure.

;TypeWriter]|[ CodeWizard v.102804 Project PLASMA FPS 2K4.5 Edition
;Generates a set of object functions from a *.object type files.
;Uses the type name for filename, produces a *.module file

Graphics 800,600,16,2
title$="TypeWriter]|[ CodeWizard v.121104 Project PLASMA FPS 2K4.5 Edition"
AppTitle(title$) 

Const PROPERTY_MAX%=255
Const LINE_MAX%=255

Type property
	Field typeid%;1=byte, 2=integer, 4=Float, 8=String, 16=Type, 32=array
	Field name$	
	Field strip$[2]
	Field subtype$
	Field array%
	Field bytesize%
End Type

Type method ;Function
	Field typeid%
	Field name$
	Field parameters%
	Field parameter.property[PROPERTY_MAX%]
;	Field lines%
;	Field Line$[LINE_MAX%]
End Type

Type typeobject
	Field typeid%
	Field name$
	Field max%
	Field properties%
	Field property.property[PROPERTY_MAX%] ;field
	Field bytesize%
End Type

Function typeWriter(filename$)
	If filename$="" filename$=Input("*.object filename (exclude extension) >")
	file=ReadFile(filename$+".object")
	If file
		
		While Not Eof(file)
			
			dat$=ReadLine(file)
			
			If Left$(Lower(dat$),5)="type "
				typeobject.typeobject=New typeobject
				typeobject\name$=Right$(dat$,Len(dat$)-5)
			EndIf
			
			If Left$(Lower(dat$),6)="field " 
				typeobject\properties%=typeobject\properties+1
				typeobject\property.property[typeobject\properties%]=New property
				typeobject\property[typeobject\properties%]\name$=Right$(dat$,Len(dat$)-6)
				propertyDatatype(typeobject\property[typeobject\properties%])
				propertyStrip(typeobject\property[typeobject\properties%])				
			EndIf 
					
			If Left$(Lower(dat$),4)="max=" 
				typeobject\max%=Right$(dat$,Len(dat$)-4)
			EndIf
						
		Wend
		CloseFile(file)
		
	Else
		;manual input
		Cls
		Locate 0,0
		Color 0,255,255
		Print "Fields require Data Type declarations:"
		Print "Examples: type.type, byte!, integer%, float#, string$, array%[n]"
		Print "Type 'end type' at the Field Prompt to Write Module"
		
		typeobject.typeobject=New typeobject ;first object is object
		
		Color 255,255,0
		typeobject\name$=Input("Type ")
		
		Restore propertydefaults
		For loop = 1 To 2
			Read propertydefault$
			If propertydefault$<>"end" typeobject\properties%=propertyInput(typeobject,propertydefault$)
		Next
		
		Repeat ;get fields
			typeobject\properties%=typeobject\properties+1
			typeobject\property.property[typeobject\properties%]=New property
			typeobject\property[typeobject\properties%]\name$=Input("Field ")
			propertyDatatype(typeobject\property[typeobject\properties%])
			propertyStrip(typeobject\property[typeobject\properties%])								
		Until Lower(typeobject\property[typeobject\properties%]\name$)="end type"
		typeobject\properties=typeobject\properties-1
		typeobject\max%=Input(Upper(typeobject\name$)+"_MAX%=")
	EndIf
	
	typeobjectModuleWrite(First typeobject)
	;typeobjectDump()
	Delete Each property
	Delete Each typeobject
		
End Function

Function typeobjectModuleWrite(this.typeobject)	
	;WRITE FILE
	file=WriteFile(this\name$+".module")
	If file
		Print "Writing "+this\name$+".module"	
		typeobjectDeclarationsWrite(this,file)
		typeobjectTypeobjectWrite(this,file)
		typeobjectStartWrite(this,file)
		typeobjectStopWrite(this,file)
		typeobjectNewWrite(this,file)
		typeobjectDeleteWrite(this,file)
		typeobjectUpdateWrite(this,file)
		typeobjectReadWrite(this,file)
		typeobjectWriteWrite(this,file)
		typeobjectOpenWrite(this,file)
		typeobjectSaveWrite(this,file)
		typeobjectCSVReadWrite(this,file)
		typeobjectCSVHeaderWriteWrite(this,file)
		typeobjectCSVWriteWrite(this,file)
		typeobjectCSVSaveWrite(this,file)
		typeobjectCopyWrite(this,file)
		typeobjectMimicWrite(this,file)
		typeobjectCreateWrite(this,file)
		typeobjectSetWrite(this,file)

		CloseFile(file)
		Print this\name$+".module written sucessfully!"		
;		quit$=Input("Quit! Yes|No? ")
;		If Lower(quit$)="yes" End
;		this\properties=0
;		Goto start
	EndIf
End Function	

	
Function typeobjectDeclarationsWrite(this.typeobject,file)
	WriteLine(file,";============================")
	WriteLine(file,";"+Upper(this\name$+" module"))
	WriteLine(file,";Generated with TypeWriter][")
	WriteLine(file,";============================")
	
	;CONST
	WriteLine(file,"Const "+Upper(this\name$)+"_MAX%="+Str(this\max%))
	;Managmement Code
	If this\max%
		;ID
		WriteLine(file,"Global "+this\name$+"Id."+this\name$+"["+Upper(this\name$)+"_MAX%]")
		WriteLine(file,"Global "+this\name$+"Index.stack=stackIndexCreate("+Upper(this\name$)+"_MAX%"+")") 
		WriteLine(file,"Global "+this\name$+"Available.stack=stackIndexCreate("+Upper(this\name$)+"_MAX%"+")") 
	EndIf	
	WriteLine(file,"")	
End Function		
	
Function typeobjectTypeobjectWrite(this.typeobject,file)
	WriteLine(file,"Type "+this\name$)
	For loop = 1 To this\properties%
		WriteLine(file,"	Field "+this\property[loop]\name$)
	Next
	WriteLine(file,"End Type")
	WriteLine(file,"")
End Function

Function typeobjectStartWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Start()")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function 	
	
Function typeobjectStopWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Stop()")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	For this."+this\name$+"=Each "+this\name$)
	WriteLine(file,"		"+this\name$+"Delete(this)")
	WriteLine(file,"	Next")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function
	
Function typeobjectNewWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"New."+this\name$+"()")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	this."+this\name$+"=New "+this\name$)
	For loop = 1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2 WriteLine(file,"	this\"+this\property[loop]\name$+"=0")
			Case 4 WriteLine(file,"	this\"+this\property[loop]\name$+"=0.0")
			Case 8 WriteLine(file,"	this\"+this\property[loop]\name$+"="+Chr(34)+Chr(34))
			Case 16 WriteLine(file,"	this\"+this\property[loop]\name$+"="+this\property[loop]\subtype$+"New()")
			Case 33,34,36,40,48
				WriteLine(file,"	For loop=1 To "+Str(this\property[loop]\array%))			
				Select this\property[loop]\typeid%
					Case 33,34 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]=0")
					Case 36 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]=0.0")
					Case 40 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]="+Chr(34)+Chr(34))
					Case 48 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]="+this\property[loop]\subtype$+"New()")
				End Select
				WriteLine(file,"	Next")
		End Select		
	Next
	If this\max%
		WriteLine(file,"	this\id%=StackPop("+this\name$+"Index.stack)")
		WriteLine(file,"	"+this\name$+"Id[this\id%]=this")		
	EndIf	
	WriteLine(file,"	Return this")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function
	
Function typeobjectDeleteWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Delete(this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	If this\max%
		WriteLine(file,"	"+this\name$+"Id[this\id]=Null")
		WriteLine(file,"	StackPush("+this\name$+"Index.stack,this\id%)")
	EndIf
	For loop = this\properties% To 1 Step -1
		Select this\property[loop]\typeid%
			Case 1 WriteLine(file,"	this\"+this\property[loop]\name$+"=0")	
			Case 2
				Select Lower(this\property[loop]\strip$[1])
					Case "bank","brush","entity","font","image","sound","texture","timer"
						WriteLine(file,"	Free"+this\property[loop]\strip$[1]+" this\"+this\property[loop]\name$)
				End Select	
			Case 4 WriteLine(file,"	this\"+this\property[loop]\name$+"=0.0")
			Case 8 WriteLine(file,"	this\"+this\property[loop]\name$+"="+Chr(34)+Chr(34))
			Case 16 WriteLine(file,"	"+this\property[loop]\subtype$+"Delete(this\"+this\property[loop]\name$+")")
			Case 33,34,36,40,48
				WriteLine(file,"	For loop=1 To "+Str(this\property[loop]\array%))			
				Select this\property[loop]\typeid%
					Case 33,34 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]=0")
					Case 36 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]=0.0")
					Case 40 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]="+Chr(34)+Chr(34))
					Case 48 WriteLine(file,"		"+this\property[loop]\subtype$+"Delete(this\"+this\property[loop]\strip$[2]+"[loop])")
				End Select
				WriteLine(file,"	Next")
		End Select		
	Next
	WriteLine(file,"	Delete this")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function
	
Function typeobjectUpdateWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Update()")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	For this."+this\name$+"=Each "+this\name$)
	WriteLine(file,"	Next")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function
	
Function typeobjectReadWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Read."+this\name$+"(file)")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	this."+this\name$+"=New "+this\name$)
	For loop = 1 To this\properties
		Select this\property[loop]\typeid%
			Case 1 WriteLine(file,"	this\"+this\property[loop]\name$+"=ReadByte(file)")
			Case 2 WriteLine(file,"	this\"+this\property[loop]\name$+"=ReadInt(file)")
			Case 4 WriteLine(file,"	this\"+this\property[loop]\name$+"=ReadFloat(file)")
			Case 8 WriteLine(file,"	this\"+this\property[loop]\name$+"=ReadLine(file)")
			Case 16 WriteLine(file,"	this\"+this\property[loop]\name$+"="+this\property[loop]\subtype$+"Read(file)")
			Case 33,34,36,40,48
				WriteLine(file,"	For loop=1 To "+Str(this\property[loop]\array%))
				Select this\property[loop]\typeid%
					Case 33 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]=ReadByte(file)")
					Case 34 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]=ReadInt(file)")
					Case 36 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]=ReadFloat(file)")
					Case 40 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]=ReadLine(file)")
					Case 48 WriteLine(file,"		this\"+this\property[loop]\strip$[1]+"[loop]="+this\property[loop]\subtype$+"Read(file)")
				End Select		
				WriteLine(file,"	Next")					
		End Select		
	Next
	WriteLine(file,"	Return this")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function
	
Function typeobjectWriteWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Write(file,this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")
	For loop = 1 To this\properties
		Select this\property[loop]\typeid%
			Case 1 WriteLine(file,"	WriteByte(file,this\"+this\property[loop]\name$+")")
			Case 2 WriteLine(file,"	WriteInt(file,this\"+this\property[loop]\name$+")")
			Case 4 WriteLine(file,"	WriteFloat(file,this\"+this\property[loop]\name$+")")
			Case 8 WriteLine(file,"	WriteLine(file,this\"+this\property[loop]\name$+")")
			Case 16 WriteLine(file,"	"+this\property[loop]\subtype$+"Write(file,this\"+this\property[loop]\name$+")")
			Case 33,34,36,40,48
				WriteLine(file,"	For loop=1 To "+Str(this\property[loop]\array%))			
				Select this\property[loop]\typeid%
					Case 33 WriteLine(file,"		WriteByte(file,this\"+this\property[loop]\strip$[1]+"[loop])")
					Case 34 WriteLine(file,"		WriteInt(file,this\"+this\property[loop]\strip$[1]+"[loop])")
					Case 36 WriteLine(file,"		WriteFloat(file,this\"+this\property[loop]\strip$[1]+"[loop])")
					Case 40 WriteLine(file,"		WriteLine(file,this\"+this\property[loop]\strip$[1]+"[loop])")
					Case 48 WriteLine(file,"		"+this\property[loop]\subtype$+"Write(file,this\"+this\property[loop]\strip$[1]+"[loop])")
				End Select
				WriteLine(file,"	Next")	
		End Select		
	Next
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function
	
Function typeobjectSaveWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Save(filename$="+Chr(34)+"Default"+Chr(34)+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	file=WriteFile(filename$+"+Chr(34)+"."+this\name$+""+Chr(34)+")")
	WriteLine(file,"	For this."+this\name$+"= Each "+this\name$)
	WriteLine(file,"		"+this\name$+"Write(file,this)")
	WriteLine(file,"	Next")
	WriteLine(file,"	CloseFile(file)")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function	
	
Function typeobjectOpenWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Open(filename$="+Chr(34)+"Default"+Chr(34)+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	file=ReadFile(filename+"+Chr(34)+"."+this\name$+""+Chr(34)+")")
	WriteLine(file,"	Repeat")
	WriteLine(file,"		"+this\name$+"Read(file)")
	WriteLine(file,"	Until Eof(file)")
	WriteLine(file,"	CloseFile(file)")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function	

Function typeobjectCSVReadWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"CSVRead(level.level)")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	this."+this\name$+"="+this\name$+"New()")
	csvfields%=1
	For loop = 1 To this\properties
		Select this\property[loop]\typeid%
			Case 1,2,4,8 
				csvfields%=csvfields%+1
				WriteLine(file,"	this\"+this\property[loop]\name$+"=level\csvfield$["+Str$(csvfields%)+"]")
			Case 16 
				;find type expand properties
				csvfields%=typeobjectSubtypeCSVReadWrite(file,this\property[loop],this\property[loop]\strip$[1],csvfields%)	
			Case 33,34,36,40,48
				For loop2 =  1 To this\property[loop]\array%
					array$="["+Str$(loop2)+"]"
					Select this\property[loop]\typeid%
						Case 33,34,36,40
							csvfields%=csvfields%+1
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+array$+"=level\csvfield$["+Str$(csvfields)+"]")
						Case 48
							;find type and expand
							 csvfields%=typeobjectSubtypeCSVReadWrite(file,this\property[loop],this\property[loop]\strip$[2]+array$,csvfields%)	
					End Select				
				Next					
		End Select				
	Next
	If this\max% WriteLine(file,"	"+this\name$+"Id[this\id]=this")		
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function

Function typeobjectSubtypeCSVReadWrite(file,property.property,propertyname$,csvfields%)
	propertyname$=propertyname$+"\"
	For this.typeobject=Each typeobject
		If this\name$=property\subtype$
			For loop = 1 To this\properties%
				Select this\property[loop]\typeid%
					Case 1,2,4,8
						csvfields%=csvfields%+1 
						WriteLine(file,"	this\"+propertyname$+this\property[loop]\name$+"=level\csvfield$["+Str$(csvfields)+"]")
					Case 16 
						;find type expand properties
						csvfields%=typeobjectSubtypeCSVReadWrite(file,this\property[loop],propertyname$+this\property[loop]\strip$[1],csvfields%)
					Case 33,34,36,40,48
						For loop2 =  1 To this\property[loop]\array%
							Select this\property[loop]\typeid%
								Case 33,34,36,40
									csvfields%=csvfields%+1
									WriteLine(file,"	this\"+propertyname$+this\property[loop]\strip$[1]+"["+Str$(loop2)+"]=level\csvfield$["+Str$(csvfields%)+"]")
								Case 48
									;find type and expand	
									csvfields%=typeobjectSubtypeCSVReadWrite(file,this\property[loop],propertyname$+this\property[loop]\strip$[2],csvfields%)
							End Select				
						Next					
				End Select				
			Next
			Return csvfields%
		EndIf
	Next
	Return csvfields%			
End Function

Function typeobjectCSVHeaderWriteWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"CSVHeaderWrite(file,this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
		Select Right$(this\name$,1)
			Case "y"
				subtypeplural$=Left(this\name$,Len(this\name$)-1)+"ies"
			Case "s","h"
				subtypeplural$=this\name$+"es"
			Default
				subtypeplural$=this\name$+"s"	
		End Select
		WriteLine(file,"	WriteLine(file,"+Chr(34)+subtypeplural$+","+Chr(34)+"+"+typeobjectCSVHeader(this)+")")	
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function	

Function typeobjectCSVWriteWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"CSVWrite(file,this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	WriteLine(file,"+Chr(34)+this\name$+","+Chr(34)+"+"+typeobjectCSVParameters$(this)+")")
	WriteLine(file,"End Function")
	WriteLine(file,"")		
End Function

Function typeobjectCSVSaveWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"CSVSave(file%)")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	"+this\name$+"CSVHeaderWrite(file%,this."+this\name$+")")
	WriteLine(file,"	For this."+this\name$+"= Each "+this\name$)
	WriteLine(file,"		"+this\name$+"CSVWrite(file%,this)")
	WriteLine(file,"	Next")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function	
	
Function typeobjectCSVSave2Write(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"CSVSave2(filename$="+Chr(34)+"Default"+Chr(34)+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	file=WriteFile(filename$+"+Chr(34)+"."+this\name$+""+Chr(34)+")")
	WriteLine(file,"	"+this\name$+"CSVHeaderWrite(file,this."+this\name$+")")
	WriteLine(file,"	For this."+this\name$+"= Each "+this\name$)
	WriteLine(file,"		"+this\name$+"CSVWrite(file,this)")
	WriteLine(file,"	Next")
	WriteLine(file,"	CloseFile(file)")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function

Function typeobjectCSVOpenWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Open(filename$="+Chr(34)+"Default"+Chr(34)+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	file=ReadFile(filename+"+Chr(34)+"."+this\name$+""+Chr(34)+")")
	WriteLine(file,"	Repeat")
	WriteLine(file,"		"+this\name$+"Read(file)")
	WriteLine(file,"	Until Eof(file)")
	WriteLine(file,"	CloseFile(file)")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function		

;databaseSQL(database,"SELECT * FROM user")
;databaseConnect(database)
;databaseQuery(database)

Function typeobjectDatabaseSQLTableWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"DatabaseSQLTable."+this\name$+"(this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	;CREATE TABLE `this\name$` (`id` TINYINT (3) UNSIGNED DEFAULT '0' AUTO_INCREMENT, PRIMARY KEY(`id`));
	;add a field
	For loop = 1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2,4,8	;database\sql$="+Chr$(34)+"ALTER TABLE `this\name$` ADD `this\property[loop]\` TEXT;"+Chr$(34)
			Case 16
			Case 33,34,36,40,48
				Case 33
				Case 34
				Case 40
				Case 48
				Select this\property[loop]\typeid%
				End Select
		End Select
	Next
	WriteLine(file,"End Function")
	WriteLine(file,"")	
End Function

Function typeobjectDatabaseSQLQueryWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"DatabaseSQLQuery."+this\name$+"(this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	For loop = 1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2,4,8	;SELECT * FROM 'this\name$'	
			Case 16
			Case 33,34,36,40,48
				Case 33
				Case 34
				Case 40
				Case 48
				Select this\property[loop]\typeid%
				End Select
		End Select
	Next
	WriteLine(file,"End Function")
	WriteLine(file,"")	
End Function

Function typeobjectDatabaseSQLInsertWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"DatabaseSQLInsert."+this\name$+"(this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	For loop = 1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2,4,8		;INSERT INTO test (id, Field1, Field2, Field3, Field4, Field5, Field6, Field7, Field8, Field9, Field10) VALUES (Null, 1, Null, Null, Null, Null, Null, Null, Null, Null, Null)	
			Case 16
			Case 33,34,36,40,48
				Case 33
				Case 34
				Case 40
				Case 48
				Select this\property[loop]\typeid%
				End Select
		End Select
	Next
	WriteLine(file,"End Function")
	WriteLine(file,"")	
End Function

Function typeobjectDatabaseSQLDeleteWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"DatabaseSQLDelete."+this\name$+"(this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	For loop = 1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2,4,8 ;DELETE FROM 'this\name$' WHERE 'id' = value;
			Case 16
			Case 33,34,36,40,48
				Case 33
				Case 34
				Case 40
				Case 48
				Select this\property[loop]\typeid%
				End Select
		End Select
	Next
	WriteLine(file,"End Function")
	WriteLine(file,"")	
End Function

Function typeobjectDatabaseSQLUpdateWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"DatabaseSQLUpdate."+this\name$+"(this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	For loop = 1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2,4,8 ;UPDATE test SET name='Frank' WHERE id=1;
			Case 16
			Case 33,34,36,40,48
				Case 33
				Case 34
				Case 40
				Case 48
				Select this\property[loop]\typeid%
				End Select
		End Select
	Next
	WriteLine(file,"End Function")
	WriteLine(file,"")	
End Function
	
Function typeobjectCopyWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Copy."+this\name$+"(this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	copy."+this\name$+"=New "+this\name$)
	For loop = 1 To this\properties
		Select this\property[loop]\typeid%
			Case 1,2,4,8 WriteLine(file,"	copy\"+this\property[loop]\name$+"=this\"+this\property[loop]\name$)
			Case 16 WriteLine(file,"	copy\"+this\property[loop]\name$+"="+this\property[loop]\subtype$+"Copy(this\"+this\property[loop]\name$+")")
			Case 33,34,36,40,48
				WriteLine(file,"	For loop=1 To "+Str(this\property[loop]\array%))	
				Select this\property[loop]\typeid%
					Case 33 WriteLine(file,"		copy\"+this\property[loop]\strip$[1]+"[loop]=this\"+this\property[loop]\strip$[1]+"[loop]")
					Case 34,36,40 WriteLine(file,"		copy\"+this\property[loop]\strip$[1]+"[loop]=this\"+this\property[loop]\strip$[1]+"[loop]")
					Case 48 WriteLine(file,"		copy\"+this\property[loop]\strip$[1]+"[loop]="+this\property[loop]\subtype$+"Copy(this\"+this\property[loop]\strip$[2]+"[loop])")
				End Select
				WriteLine(file,"	Next")	
		End Select
	Next
	If this\max%
		WriteLine(file,"	copy\id%=StackPop("+this\name$+"Index.stack)")
		WriteLine(file,"	"+this\name$+"Id[copy\id%]=copy")		
	EndIf		
	WriteLine(file,"	Return copy")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function	
	
Function typeobjectMimicWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Mimic(mimic."+this\name$+",this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	For loop = 1 To this\properties
		Select this\property[loop]\typeid%
			Case 1,2,4,8 WriteLine(file,"	mimic\"+this\property[loop]\name$+"=this\"+this\property[loop]\name$)
			Case 16 WriteLine(file,"	"+this\property[loop]\subtype$+"Mimic(mimic\"+this\property[loop]\name$+",this\"+this\property[loop]\name$+")")
			Case 33,34,36,40,48		
				WriteLine(file,"	For loop=1 To "+Str(this\property[loop]\array%))	
				Select this\property[loop]\typeid%
					Case 33 WriteLine(file,"		mimic\"+this\property[loop]\strip$[1]+"[loop]=this\"+this\property[loop]\strip$[1]+"[loop]")
					Case 34,36,40 WriteLine(file,"		mimic\"+this\property[loop]\strip$[1]+"[loop]=this\"+this\property[loop]\strip$[1]+"[loop]")
					Case 48 WriteLine(file,"		"+this\property[loop]\subtype$+"Mimic(mimic\"+this\property[loop]\strip$[2]+"[loop],this\"+this\property[loop]\strip$[2]+"[loop])")
				End Select				
				WriteLine(file,"	Next")
		End Select		
	Next
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function
	
Function typeobjectCreateWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Create."+this\name$+"("+typeobjectParameters$(this)+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	WriteLine(file,"	this."+this\name$+"="+this\name$+"New()")
	For loop = 1 To this\properties
		Select this\property[loop]\typeid%
			Case 1,2,4,8,16 WriteLine(file,"	this\"+this\property[loop]\name$+"="+this\property[loop]\name$)
			Case 33,34,36,40,48	
				For loop2 =  1 To this\property[loop]\array%
					Select this\property[loop]\typeid%
						Case 33,34
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+"["+Str(loop2)+"]="+stripper(this\property[loop]\strip$[1],"%")+Str(loop2)+"%")
						Case 36
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+"["+Str(loop2)+"]="+stripper(this\property[loop]\strip$[1],"#")+Str(loop2)+"#")
						Case 40	
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+"["+Str(loop2)+"]="+stripper(this\property[loop]\strip$[1],"$")+Str(loop2)+"$")
						Case 48
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+"["+Str(loop2)+"]="+this\property[loop]\strip$[2]+Str(loop2)+"."+this\property[loop]\subtype$)
					End Select		
					Next				
			End Select		
	Next
	WriteLine(file,"	Return this")
	WriteLine(file,"End Function")
	WriteLine(file,"")
End Function

Function typeobjectManagerCreateWrite(this.typeobject,file)
	If this\max%
		WriteLine(file,"Function "+this\name$+"ManagerCreate(this."+this\name$+")")
		WriteLine(file,"	;Purpose:")
		WriteLine(file,"	;Parameters:")
		WriteLine(file,"	;Return:")	
		WriteLine(file,"	manager.manager=managerCreate("+this\name$+"ObjectID%,this\id%,this\typeid%,"+Upper(this\name$)+"_MAX%)")
		WriteLine(file,"	module.module=moduleID(managerModuleAdd(manager,manager\typeid%,this\id%,this\typeid%,"+Upper(this\name$)+"_MAX%))")
		WriteLine(file,"	For loop = 1 To module\count%")
		WriteLine(file,"		this."+this\name$+"="+this\name$+"Copy(this)")
		WriteLine(file,"		stackPush(module\available%,this\id%)")					
		WriteLine(file,"	Next")
		WriteLine(file,"End Function")
	EndIf
End Function	

Function typeobjectDestroyWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Destroy(this."+this\name$+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")
	WriteLine(file,"End Function")	
End Function
	
Function typeobjectSetWrite(this.typeobject,file)
	WriteLine(file,"Function "+this\name$+"Set(this."+this\name$+","+typeobjectParameters$(this)+")")
	WriteLine(file,"	;Purpose:")
	WriteLine(file,"	;Parameters:")
	WriteLine(file,"	;Return:")	
	For loop = 1 To this\properties
		Select this\property[loop]\typeid%
			Case 1,2,4,8,16 WriteLine(file,"	this\"+this\property[loop]\name$+"="+this\property[loop]\name$)
			Case 33,34,36,40,48 
				For loop2 = 1 To this\property[loop]\array%
					Select this\property[loop]\typeid%
						Case 33,34
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+"["+Str(loop2)+"]="+stripper(this\property[loop]\strip$[1],"%")+Str(loop2)+"%")
						Case 36
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+"["+Str(loop2)+"]="+stripper(this\property[loop]\strip$[1],"#")+Str(loop2)+"#")
						Case 40	
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+"["+Str(loop2)+"]="+stripper(this\property[loop]\strip$[1],"$")+Str(loop2)+"$")
						Case 48
							WriteLine(file,"	this\"+this\property[loop]\strip$[1]+"["+Str(loop2)+"]="+this\property[loop]\strip$[2]+Str(loop2)+"."+this\property[loop]\subtype$)
					End Select		
				Next				
		End Select		
	Next
	WriteLine(file,"End Function")
End Function	

Function typeobjectResetWrite(this.typeobject,file)
End Function
	
Function typeobjectParameters$(this.typeobject)
	For loop=1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2,4,8 pars$=pars$+this\property[loop]\name$  
			Case 16  pars$=pars$+this\property[loop]\name$
			Case 33,34,36,40,48
				For loop2 = 1 To this\property[loop]\array%
					Select this\property[loop]\typeid%
						Case 33,34
							pars$=pars$+this\property[loop]\strip$[2]+Str(loop2)+"%"
						Case 36
							pars$=pars$+this\property[loop]\strip$[2]+Str(loop2)+"#"						
						Case 40						
							pars$=pars$+this\property[loop]\strip$[2]+Str(loop2)+"$"					
						Case 48
							pars$=pars$+this\property[loop]\strip$[2]+Str(loop2)+"."+this\property[loop]\subtype$
					End Select
					If loop2<this\property[loop]\array% pars$=pars$+","					
				Next		
			End Select
		If loop<this\properties% pars$=pars$+","
	Next
	Return pars$
End Function 

Function typeobjectCSVParameters$(this.typeobject)
	delimiter$="+"+Chr$(34)+","+Chr$(34)+"+"
	For loop=1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2,4
				pars$=pars$+"Str(this\"+this\property[loop]\name$+")"
			Case 8 
				pars$=pars$+"this\"+this\property[loop]\name$		
			Case 16 
				;find type and expand prperties
				pars$=pars$+typeobjectSubtypeCSVParameter$(this\property[loop],this\property[loop]\strip$[1],delimiter$)
			Case 33,34,36,40,48
				For loop2 = 1 To this\property[loop]\array%
					array$="["+Str(loop2)+"]"					
					Select this\property[loop]\typeid%
						Case 33,34,36
							pars$=pars$+"Str(this\"+this\property[loop]\strip$[1]+array$+")"					
						Case 40
							pars$=pars$+"this\"+this\property[loop]\strip$[1]+array$
						Case 48
							;find type and expand prperties
							pars$=pars$+typeobjectSubtypeCSVParameter$(this\property[loop],this\property[loop]\strip$[2]+array$,delimiter$)
					End Select
					If loop2<this\property[loop]\array% pars$=pars$+delimiter$
				Next	
			End Select
		If loop<this\properties% pars$=pars$+delimiter$
	Next
	Return pars$
End Function 

Function typeobjectSubtypeCSVParameter$(property.property,propertyname$,delimiter$)
	propertyname$=propertyname$+"\"
	For this.typeobject=Each typeobject
		If this\name$=property\subtype$

			For loop = 1 To this\properties%
				Select this\property[loop]\typeid%
					Case 1,2,4
						pars$=pars$+"Str(this\"+propertyname$+this\property[loop]\name$+")"
					Case 8 
						pars$=pars$+"this\"+propertyname$+this\property[loop]\name$	
					Case 16 
						;find type and expand prperties
						pars$=pars$+typeobjectSubtypeCSVParameter$(this\property[loop],propertyname$+this\property[loop]\strip$[1],delimiter$)
					Case 33,34,36,40,48
						For loop2 = 1 To this\property[loop]\array%
							array$="["+Str(loop2)+"]"
							Select this\property[loop]\typeid%
								Case 33,34,36
									pars$=pars$+"Str(this\"+propertyname$+this\property[loop]\strip$[1]+array$+")"					
								Case 40
									pars$=pars$+"this\"+propertyname$+this\property[loop]\strip$[1]+array$
								Case 48
									;find type and expand prperties
									pars$=pars$+typeobjectSubtypeCSVParameter$(this\property[loop],propertyname$+this\property[loop]\strip$[2]+array$,delimiter$)
							End Select
							If loop2<this\property[loop]\array% pars$=pars$+delimiter$
						Next	
				End Select
				If loop<this\properties% pars$=pars$+delimiter$		
			Next
			Return pars$
		EndIf
	Next	
End Function 

Function typeobjectCSVHeader$(this.typeobject)
	delimiter$="+"+Chr$(34)+","+Chr$(34)+"+"
	For loop=1 To this\properties%
		Select this\property[loop]\typeid%
			Case 1,2,4,8
				pars$=pars$+Chr$(34)+this\property[loop]\name$+Chr$(34)
			Case 16
				pars$=pars$+typeobjectSubtypeCSVHeader$(this\property[loop],this\property[loop]\strip$[1],delimiter$)
			Case 33,34,36,40,48
				For loop2 = 1 To this\property[loop]\array%
					array$="["+Str(loop2)+"]"
					Select this\property[loop]\typeid%
						Case 33,34,36,40 
							pars$=pars$+Chr$(34)+ this\property[loop]\strip$[1]+array$+ Chr$(34)
						Case 48	
							pars$=pars$+typeobjectSubtypeCSVHeader$(this\property[loop],this\property[loop]\strip$[2]+array$,delimiter$)					
					End Select
					If loop2<this\property[loop]\array% pars$=pars$+delimiter$
				Next
		End Select
		If loop<this\properties% pars$=pars$+delimiter$
	Next
	Return pars$
End Function 

Function typeobjectSubtypeCSVHeader$(property.property,propertyname$,delimiter$)
	propertyname$=propertyname$+"\"
	For this.typeobject=Each typeobject
		If this\name$=property\subtype$
			For loop=1 To this\properties%
				Select this\property[loop]\typeid%
					Case 1,2,4,8
						pars$=pars$+Chr$(34)+propertyname$+this\property[loop]\name$+Chr$(34)
					Case 16
						pars$=pars$+typeobjectSubtypeCSVHeader$(this\property[loop],propertyname$+this\property[loop]\strip$[1],delimiter$)
					Case 33,34,36,40,48
						For loop2 = 1 To this\property[loop]\array%
							array$="["+Str(loop2)+"]"
							Select this\property[loop]\typeid%
								Case 33,34,36,40 
									pars$=pars$+Chr$(34)+propertyname$+this\property[loop]\strip$[1]+array$+ Chr$(34)
								Case 48	
									pars$=pars$+typeobjectSubtypeCSVHeader$(this\property[loop],propertyname$+this\property[loop]\strip$[2]+array$,delimiter$)					
							End Select
							If loop2<this\property[loop]\array% pars$=pars$+delimiter$	
						Next
				End Select
				If loop<this\properties% pars$=pars$+delimiter$
			Next
			Return pars$
		EndIf	
	Next		
End Function

Function typeobjectDump()
	For this.typeobject = Each typeobject
		DebugLog "Type="+this\name$
		DebugLog "Typeid="+Str(this\typeid%)
		DebugLog "Max="+Str(this\max%)
		DebugLog "Properties="+Str(this\properties%)
		For loop = 1 To Str(this\properties%)
			DebugLog "	Property["+Str(loop)+"]"
			DebugLog "	Name="+this\property[loop]\name$ ;field			
			DebugLog "	typeid="+Str(this\property[loop]\typeid) ;field			
			DebugLog "	Strip="+this\property[loop]\strip$[1]
			DebugLog "	Subtype="+Str(this\property[loop]\Subtype)
			DebugLog "	Array="+Str(this\property[loop]\Array)
		Next	
		;DebugLog bytesize%		
		DebugLog "----------------------------------------------------------------------------------"
	Next
End Function 

Function typeobjectInclude(filename$)
	file=ReadFile(filename$+".object")

	If file
		this.typeobject=New typeobject
		this\name$=filename$
		While Not Eof(file)
			dat$=ReadLine(file)
			If Lower$(Left$(dat$,6))="field "
				this\properties%=this\properties%+1
				this\property.property[this\properties%]=New property
				this\property[this\properties%]\name$=Right$(dat$,Len(dat$)-6)
				propertyDatatype(this\property[this\properties%])
				propertyStrip(this\property[this\properties%])
			EndIf	
		Wend
		CloseFile(file) 
	EndIf
End Function

Function propertyInput(typeobject.typeobject,propertyname$)
	If 	Input(Lower("Field "+propertyname$+" y/n?"))="y"
		typeobject\properties%=typeobject\properties+1
		typeobject\property.property[typeobject\properties%]=New property
		typeobject\property[typeobject\properties%]\name$=propertyname$
		propertyDatatype(typeobject\property[typeobject\properties%])
		propertyStrip(typeobject\property[typeobject\properties%])			
	EndIf
End Function

Function propertyStrip(this.property)
	Select this\typeid%
		Case 2 this\strip$[1]=stripper(this\name$,"%")
		Case 4 this\strip$[1]=stripper(this\name$,"#")
		Case 8 this\strip$[1]=stripper(this\name$,"$")
		Case 16 this\strip$[1]=stripper(this\name$,".")
		Case 33,34,36,40,48 
			this\strip$[1]=stripper(this\name$,"[");array
			Select this\typeid%
				Case 33 this\strip$[2]=stripper(this\strip$[1],"!")
				Case 34 this\strip$[2]=stripper(this\strip$[1],"%")
				Case 36 this\strip$[2]=stripper(this\strip$[1],"#")
				Case 40 this\strip$[2]=stripper(this\strip$[1],"$")
				Case 48	this\strip$[2]=stripper(this\strip$[1],".");type
			End Select
	End Select
End Function

Function propertyDatatype(this.property)
	For loop=Len(this\name$) To 1 Step -1
		char$=Mid(this\name$,loop,1)
		If getelements% elements$=char$+elements$		
		Select char$
			Case "]" ;array
				getelements%=True 
			Case "[" ;array
				this\typeid%=32			 
				;get element value
				this\array%=Right(elements$,Len(elements$)-1)
				getelements%=False								
			Case "." ;type
				this\typeid%=this\typeid%+16
				If this\array
					this\subtype$=stripper$(Right(this\name$,Len(this\name$)-loop),"[")
				Else
					this\subtype$=Right(this\name$,Len(this\name$)-loop)
				EndIf
				;If no typeobject created for this subtype, create one
				For typeobject.typeobject = Each typeobject
					If typeobject\name$=this\subtype$ typeobjectcreated%=True					
				Next
				If Not typeobjectcreated% typeobjectInclude(this\subtype$)
				Return 	
			Case "$" ;string
				this\typeid%=this\typeid%+8
				Return
			Case "#" ;float	
				this\typeid%=this\typeid%+4			
			Case "%" ;integer
				this\typeid%=this\typeid%+2
				Return			
			Case "!" ;byte
				this\typeid%=this\typeid%+1
				this\name%=Replace(this\name,"!","%")
				Return
		End Select
	Next
End Function

Function stripper$(txt$,char$,additive%=1) 
	;use stripper$("apple.apple",".")
	;this function will remove all character after "." to include "."
	;the additive value can be use to strip more characters 
	;After "." (ie +1) Or less characters Before "." (ie -1). 
	;A value of 0 will strip To the "."
	For loop=Len(txt$) To 1 Step -1
		If Mid(txt$,loop,1)=char$ Return Left(txt$,loop-additive%)
	Next	
End Function

.propertydefaults
Data "id%","typeid%","end" ;parentid%, entity%, state%, actionid%
