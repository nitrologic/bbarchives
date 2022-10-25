; ID: 2284
; Author: Plash
; Date: 2008-07-10 07:58:36
; Title: Type hierarchy
; Description: Gather type hierarchy using reflection

SuperStrict

Framework BRL.Reflection
Import BRL.StandardIO

Type TLowestType
	Field integer:Int
	
End Type

Type TMiddleType Extends TLowestType
	Field additional:String
	
End Type

Type THighestType Extends TMiddleType
	Field pos_x:Float, pos_y:Float
	
End Type

Local obj:THighestType = New THighestType
Local obj_id:TTypeId = TTypeId.ForObject(obj)

  Local superid:TTypeId, lastid:TTypeId = obj_id
	Repeat
	  superid = lastid.SuperType()
		
		If superid <> Null And superid <> ObjectTypeId
		  lastid = superid
			
			Print superid.Name()
				For Local fld:TField = EachIn superid.EnumFields() 
					
					Print "~t" + fld.Name() + ":" + fld.TypeId().Name()
					
				Next
				
		Else
			DebugLog "reached end of type hierarchy"
			Exit
			
		EndIf
		
	Forever

End
