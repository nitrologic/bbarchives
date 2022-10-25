; ID: 1876
; Author: Fabian.
; Date: 2006-12-09 05:24:45
; Title: fmc.ObjectTool
; Description: Code working with objects and its types

Strict
Module fmc.ObjectTool
NoDebug

ModuleInfo "Version: 0.04"
ModuleInfo "Modserver: Fabian"

Function ObjType ( obj:Object )
  Return ( Int Ptr Byte Ptr obj ) [ -2 ]
EndFunction

Function ObjNew:Object ( t )
  Return bbObjectNew ( t )
EndFunction

Function OpenHandle ( obj:Object )
  ( Int Ptr Ptr Varptr obj ) [ 0 ] [ 1 ] :+ 1
  Return ( Int Ptr Varptr obj ) [ 0 ] + 1
EndFunction

Function CloseHandle ( handle )
  ( Int Ptr ( handle - 1 ) ) [ 1 ] :- 1
  If Not ( Int Ptr ( handle - 1 ) ) [ 1 ]
    Local obj:Object
    ( Int Ptr Varptr obj ) [ 0 ] = handle - 1
    bbGCFree obj
  EndIf
EndFunction

Function ObjectForHandle:Object ( handle )
  Local obj:Object
  ( Int Ptr Varptr obj ) [ 0 ] = handle - 1
  Return obj
EndFunction

Function HandleForObject ( obj:Object )
  Return ( Int Ptr Varptr obj ) [ 0 ] + 1
EndFunction

Function RetainObject ( obj:Object )
  ( Int Ptr Ptr Varptr obj ) [ 0 ] [ 1 ] :+ 1
EndFunction

Function ReleaseObject ( obj:Object )
  ( Int Ptr Ptr Varptr obj ) [ 0 ] [ 1 ] :- 1
  If Not ( Int Ptr Ptr Varptr obj ) [ 0 ] [ 1 ]
    bbGCFree obj
  EndIf
EndFunction

Function RetainHandle ( handle )
  ( Int Ptr ( handle - 1 ) ) [ 1 ] :+ 1
EndFunction

Function ReleaseHandle ( handle )
  ( Int Ptr ( handle - 1 ) ) [ 1 ] :- 1
  If Not ( Int Ptr ( handle - 1 ) ) [ 1 ]
    Local obj:Object
    ( Int Ptr Varptr obj ) [ 0 ] = handle - 1
    bbGCFree obj
  EndIf
EndFunction

Function TypeSuper ( t )
  Return ( Int Ptr t ) [ 0 ]
EndFunction

Function TypeName$ ( t )
?Debug
  Return String.FromCString ( ( Byte Ptr Ptr Ptr t ) [ 2 ] [ 1 ] )
?
  Return "#" + t
EndFunction

Function TypeSize ( t )
  Return Max ( ( Int Ptr t ) [ 3 ] - 8 , -1 )
EndFunction

Function TypeExtends ( t , s )
  If Not s
    Return True
  EndIf
  While t
    If t = s
      Return True
    EndIf
    t = ( Int Ptr t ) [ 0 ]
  Wend
EndFunction

Function ObjectType ( )
  Return Int Varptr bbObjectClass
EndFunction

Function StringType ( )
  Return Int Varptr bbStringClass
EndFunction

Function ArrayType ( )
  Return Int Varptr bbArrayClass
EndFunction

Function StringPtr:Short Ptr ( str$ )
  Return Short Ptr Byte Ptr Object str + 2
EndFunction

Private

Extern
  Global bbObjectClass:Byte
  Global bbStringClass:Byte
  Global bbArrayClass:Byte
  Function bbObjectNew:Object ( t )
  Function bbGCFree ( obj:Object )
EndExtern
