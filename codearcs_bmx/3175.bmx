; ID: 3175
; Author: Pineapple
; Date: 2015-01-06 20:58:37
; Title: Serialize/deserialize objects as JSON
; Description: Use reflection to effortlessly serialize and deserialize data

'   --+-----------------------------------------------------------------------------------------+--
'     | This code was originally written by Sophie Kirschner (meapineapple@gmail.com) and it is |  
'     | released as public domain. Please do not interpret that as liberty to claim credit that |  
'     | is not yours, or to sell this code when it could otherwise be obtained for free because |  
'     |                    that would be a really shitty thing of you to do.                    |
'   --+-----------------------------------------------------------------------------------------+--



SuperStrict

Import brl.reflection
Import brl.linkedlist
Import brl.map
Import "wild.bmx"       ' Dependency available here: http://www.blitzmax.com/codearcs/codearcs.php?code=3176
'Import brl.standardio   ' Used by example program



' Usage: Field MyField {json}                                        All other fields in this class without {json} metadata will not be serialized.
Const jsonSerializeMetadataInclude:String = "json"

' Usage: Field MyField {nojson}                                              This field will not be serialized. This has no impact on other fields.
Const jsonSerializeMetadataExclude:String = "nojson"

' Usage: Field MyField {jsonbool}              Compensates for BlitzMax's lack of a boolean primitive: Tag an integer to serialize it as a boolean.
Const jsonSerializeMetadataBoolean:String = "jsonbool"

' Usage: Field MyField {jsonfld="MyField2,MyField3"}         Tag an object to serialize only the listed field(s). Supersedes the object's own tags.
Const jsonSerializeMetadataIncludeSub:String = "jsonfld"

' Usage: Field MyField {nojsonfld="MyField*"}                                         Tag an object to exlude the listed fields from serialization.
Const jsonSerializeMetadataExcludeSub:String = "nojsonfld"

' Usage: Field MyField {jsoncont="MyType"}          Tag a TList or TMap to inform the deserializer what class objects it contains should belong to.
Const jsonSerializeMetadataClassSub:String = "jsoncont"

' For info on what special wildcard characters are allowed in {jsonf} and {nojsonf} tags, please refer to "wild.bmx".



' This is stuff you really shouldn't be touching
Private

Global ListTypeId:TTypeId = TTypeId.ForName( "TList" )
Global MapTypeId:TTypeId = TTypeId.ForName( "TMap" )

Const jsonOpenBraceAsc:Int = Asc( "{" )
Const jsonCloseBraceAsc:Int = Asc( "}" )
Const jsonOpenBracketAsc:Int = Asc( "[" )
Const jsonCloseBracketAsc:Int = Asc( "]" )
Const jsonAssignmentAsc:Int = Asc( ":" )
Const jsonDelimiterAsc:Int = Asc( "," )
Const jsonDecimalAsc:Int = Asc( "." )
Const jsonStringAsc:Int = Asc( "~q" )
Const jsonEscapeAsc:Int = Asc( "\" )

Extern
Function bbRefFieldPtr:Byte Ptr( obj:Object, index:Int )
Function bbRefArrayElementPtr:Byte Ptr( sz:Int, array:Object, index:Int )
Function bbRefGetObject:Object( p:Byte Ptr )
Function bbRefGetObjectClass:Int( obj:Object )
Function bbRefGetSuperClass:Int( class:Int )
Function bbRefAssignObject( p:Byte Ptr, obj:Object )
End Extern

Public



' Example code

Rem

Type MyType

    ' Serialize a null reference
    Field MyNull:Object = Null
    
    ' Serialize an integer
    Field MyInt:Int = 8
    
    ' Serialize a boolean
    Field MyBoolean:Int = True {jsonbool}
    
    ' Serialize a floating-point number
    Field MyNumber:Double = 3.14
    
    ' Serialize a string
    Field MyString:String = "hi"
    
    ' Serialize an array of strings
    Field MyArray:String[] = [ "foo", "bar", "foobar" ]
    
    ' Serialize an object
    Field MyObject:MySubType = New MySubType
    
    ' DON'T serialize this field
    Field MyUnserializedVar:String = "don't serialize me!" {nojson}
    
    ' Create a list to be filled with data then serialized
    Field MyList:TList = CreateList()
    
    ' Create a map to be filled with data then serialized
    Field MyMap:TMap = CreateMap()
    
    ' Fill the list and map with data upon initialization
    Method New()
        MyList.addlast( "one" )
        MyList.addlast( "two" )
        MyList.addlast( "three" )
        MyList.addlast( "four" )
        MyMap.insert( "5", "five" )
        MyMap.insert( "6", "six" )
        MyMap.insert( "7", "seven" )
        MyMap.insert( "8", "eight" )
        MyMap.insert( "9,10,11", [ 9:Int, 10:Int, 11:Int ] )
    End Method
    
End Type

Type MySubType

    ' Serialize a string
    Field name:String = "Bob" {json}
    
    ' Serialize a long int
    Field money:Long = 99999999 {json}
    
    ' Serialize a string
    Field power:String = "lots" {json}
    
    ' Serialize an array of ints
    Field favoriteNumbers:Int[] = [ 7, 9, 12 ] {json}
    
    ' DON'T serialize this field, because we're going to put a cyclic reference here
    Field parent:MyType
    
    ' Serialize a list of objects, and inform the deserializer of the class of its contents using metadata
    Field MyObjectList:TList = ListFromArray([ MyListEntry.Create("foo"), MyListEntry.Create("bar") ]) {jsoncont="MyListEntry"}
    
    ' Serialize this field differently to hand the cyclic reference: Include only the specified fields of the referenced object
    Field selfReference1:MySubType = Self {jsonfld="name,money"}
    
    ' Serialize the same thing as above, except inversely defined
    ' (note it's not necessary to explicitly exclude the parent field, as the normal tags already do that)
    Field selfReference2:MySubType = Self {nojsonfld="power,favoriteNumbers,MyObjectList,selfReference*"}
    
End Type

Type MyListEntry

    Function Create:MyListEntry( name:String )
        Local this:MyListEntry = New MyListEntry; this.name = name; Return this
    End Function

    Field name:String
    
End Type

' Create the object to be serialized
Local obj:MyType = New MyType
' Add a cyclic reference
obj.MyObject.parent = obj

' Finally, do the actual serialization
Print "Creating serializer"
Local serializer:jsonSerializer = New jsonSerializer.init()
Print "Serializing json"
Local json:jsonValue = serializer.serializeObject( obj )
Print "Generating string"
Local str:String = json.toString()
Print "Final json:"
Print str

' Now deserialze to get the original object back
Print "~nParsing output"
Local parsedjson:jsonValue = jsonValue.fromString( str )
Print "Creating deserializer"
Local deserializer:jsonDeserializer = New jsonDeserializer.init()
Print "Deserializing output"
Local newobj:MyType = MyType( deserializer.deserializeObject( parsedjson, TTypeId.ForName( "MyType" ) ) )
Print "Deserialized object's MyArray[2] field (should be ~qfoobar~q):"
Print newobj.MyArray[2]


EndRem






' Functions define special handling for types which inherit from Object
' If the absence of a handler function for serialization and deserialization a class is treated 
' as a simple 1:1 correspondence between dict key,value pairs and class field,value pairs.

' Serialize string
Function jsonSerializeString:Object( obj:Object, member:TMember, controller:jsonSerializationController )
    Return jsonValueString.Create( String( obj ) )
End Function

' Serialize linked list
Function jsonSerializeList:Object( obj:Object, member:TMember, controller:jsonSerializationController )
    Local list:TList = TList( obj )
    Local jsonList:jsonValueList = jsonValueList.Create()
    For Local member:Object = EachIn list
        jsonList.add( jsonSerializer( controller ).serializeObject( member ) )
    Next 
    Return jsonList
End Function

' Serialize map
Function jsonSerializeMap:Object( obj:Object, member:TMember, controller:jsonSerializationController )
    Local map:TMap = TMap( obj )
    Local dict:jsonValueDict = jsonValueDict.Create()
    For Local key:Object = EachIn map.Keys()
        dict.add( key, jsonSerializer( controller ).serializeObject( map.ValueForKey( key ) ) )
    Next
    Return dict
End Function

' Deserialize string
Function jsonDeserializeString:Object( obj:Object, member:TMember, controller:jsonSerializationController )
    Local value:jsonValueString = jsonValueString( obj )
    If value
        Return value.get()
    Else
        Return Null
    EndIf
End Function

' Deserialize linked list
Function jsonDeserializeList:Object( obj:Object, member:TMember, controller:jsonSerializationController )
    Local list:jsonValueList = jsonValueList( obj )
    If list
        Local retlist:TList = CreateList()
        For Local value:jsonValue = EachIn list.get()
            retlist.addlast( jsonDeserializer( controller ).deserializeObject( value, jsonDeserializer.jsonValueTypeId( value, member ) ) )
        Next
        Return retlist
    Else 
        Return Null
    EndIf
End Function

' Deserialize map
Function jsonDeserializeMap:Object( obj:Object, member:TMember, controller:jsonSerializationController )
    Local dict:jsonValueDict = jsonValueDict( obj )
    If dict
        Local map:TMap = CreateMap()
        For Local key:String = EachIn dict.get().Keys()
            Local value:jsonValue = jsonValue( dict.get().ValueForKey( key ) )
            map.insert( key, jsonDeserializer( controller ).deserializeObject( value, jsonDeserializer.jsonValueTypeId( value, member ) ) )
        Next
        Return map
    Else
        Return Null
    EndIf
End Function





' jsonSerializer and jsonDeserializer both extend this class
Type jsonSerializationController

    ' Associates functions with TTypeId keys
    Field typeHandlers:TMap = CreateMap()
    
    ' Add a new serializer/deserializer
    Method addTypeHandler( typeid:TTypeId, typeHandlerFunc:Object( obj:Object, member:TMember, controller:jsonSerializationController ) )
        typeHandlers.insert( typeid, jsonTypeHandlerFunc.Create( typeHandlerFunc ) )
    End Method

End Type

' Container for serialization and deserializeation functions
Type jsonTypeHandlerFunc
    ' Reference to handler function
    ' obj - the jsonValue to serialize or Object to deserialize
    ' member - the TField where this value belongs
    ' controller - Reference to jsonSerializer or jsonDeserializer object
    Field func:Object( obj:Object, member:TMember, controller:jsonSerializationController )
    ' Create a new handler
    Function Create:jsonTypeHandlerFunc( func:Object( obj:Object, member:TMember, controller:jsonSerializationController ) )
        Local this:jsonTypeHandlerFunc = New jsonTypeHandlerFunc; this.func = func; Return this
    End Function
    ' Call handler function
    Method handle:Object( obj:Object, member:TMember, controller:jsonSerializationController )
        Return func( obj, member, controller )
    End Method
End Type

' Recursively turn an arbitrary object and its fields into jsonValue objects which can be encoded as a string
Type jsonSerializer Extends jsonSerializationController
    
    ' Initialize with standard serializers (String, TList, and TMap)
    ' Easy to add your own handlers upon calling init. Example call:
    ' json.init( [ MyFirstTypeId, MySecondTypeId ], [ jsonSerialize1Type, jsonSerialize2Type ] )
    Method init:jsonSerializer( ..
        moreHandlersTypeId:TTypeId[] = Null, ..
        moreHandlersFunc:Object( obj:Object, member:TMember, controller:jsonSerializationController )[] = Null ..
    )
        addTypeHandler( StringTypeId, jsonSerializeString )
        addTypeHandler( ListTypeId, jsonSerializeList )
        addTypeHandler( MapTypeId, jsonSerializeMap )
        If moreHandlersTypeId And moreHandlersFunc
            For Local i:Int = 0 Until moreHandlersTypeId.length
                addTypeHandler( moreHandlersTypeId[i], moreHandlersFunc[i] )
            Next
        EndIf
        Return Self
    End Method
    
    ' Serialize an object
    Method serializeObject:jsonValue( obj:Object, parentField:TField = Null )
        If obj = Null
            Return jsonValueNull.Create()
        Else
            Local typeid:TTypeId = TTypeId.ForObject( obj )
            If typeid.ElementType()
                Return jsonSerialize1DArray( obj, Self )
            ElseIf typeHandlers.Contains( typeid )
                Local handler:jsonTypeHandlerFunc = jsonTypeHandlerFunc( typeHandlers.ValueForKey( typeid ) )
                Return jsonValue( handler.handle( obj, parentField, Self ) )
            Else
                ' look for {jsonf} and {nojsonf} tags belonging to parent field
                Local jsonf:String[] = Null
                Local nojsonf:String[] = Null
                If parentField
                    Local jsonfMeta:String = parentField.MetaData( jsonSerializeMetadataIncludeSub )
                    Local nojsonfMeta:String = parentField.MetaData( jsonSerializeMetadataExcludeSub )
                    If jsonfMeta
                        jsonf = jsonfMeta.split(",")
                    ElseIf nojsonfMeta
                        nojsonf = nojsonfMeta.split(",")
                    EndIf
                EndIf
                ' look for {json} tags
                Local serializeMetadataOnly:Int = False
                For Local member:TField = EachIn typeid.EnumFields()
                    If member.MetaData( jsonSerializeMetadataInclude )
                        serializeMetadataOnly = True
                        Exit
                    EndIf
                Next
                ' build the dict
                Local dict:jsonValueDict = jsonValueDict.Create()
                For Local member:TField = EachIn typeid.EnumFields()
                    Local add:Int = False
                    If nojsonf And metaContains( nojsonf, member.Name() )
                        add = False
                    ElseIf jsonf
                        add = metaContains( jsonf, member.Name() )
                    ElseIf  member.MetaData( jsonSerializeMetadataIncludeSub ) Or ..
                            member.MetaData( jsonSerializeMetadataExcludeSub ) Or ..
                            member.MetaData( jsonSerializeMetadataBoolean ) Or ..
                            member.MetaData( jsonSerializeMetadataClassSub )
                        add = True
                    Else
                        ' {json}
                        Local incl:Int = ( (Not serializeMetadataOnly) Or member.MetaData( jsonSerializeMetadataInclude ) )
                        ' {nojson}
                        Local excl:Int = member.MetaData( jsonSerializeMetadataExclude ) <> Null
                        ' result
                        add = incl And Not excl
                    EndIf
                    If add dict.add( member.Name(), serializeField( obj, member ) )
                Next
                Return dict
            EndIf
        EndIf
    End Method
    Method serializeField:jsonValue( obj:Object, member:TField )
        Local id:TTypeId = member.TypeId()
        Local p:Byte Ptr = bbRefFieldPtr( obj, member._index )
        If id = ByteTypeId
            Return jsonValueInt.Create( (Byte Ptr p)[0] )
        ElseIf id = ShortTypeId
            Return jsonValueInt.Create( (Short Ptr p)[0] )
        ElseIf id = IntTypeId
            If member.MetaData( jsonSerializeMetadataBoolean )
                Return jsonValueBool.Create( (Int Ptr p)[0] )
            Else
                Return jsonValueInt.Create( (Int Ptr p)[0] )
            EndIf
        ElseIf id = LongTypeId
            Return jsonValueInt.Create( (Long Ptr p)[0] )
        ElseIf id = FloatTypeId
            Return jsonValueFloat.Create( (Float Ptr p)[0] )
        ElseIf id = DoubleTypeId
            Return jsonValueFloat.Create( (Double Ptr p)[0] )
        Else
            Return serializeObject( bbRefGetObject( p ), member )
        EndIf
    End Method
    Function metaContains:Int( array:String[], sub:String )
        For Local str:String = EachIn array
            If matchWild( str, sub ) Return True
        Next
        Return False
    End Function
    
    ' Make jsonValueList from 1D array
    Function jsonSerialize1DArray:jsonValueList( obj:Object, controller:jsonSerializationController )
        Local typeid:TTypeId = TTypeId.ForObject( obj )
        Local elementid:TTypeId = typeid.ElementType()
        Local list:jsonValueList = jsonValueList.Create()
        If elementid = ByteTypeId
            Local array:Byte[] = Byte[] obj
            For Local member:Int = 0 Until array.length
                list.add( jsonValueInt.Create( array[ member ] ) )
            Next
        ElseIf elementid = ShortTypeId
            Local array:Short[] = Short[] obj
            For Local member:Int = 0 Until array.length
                list.add( jsonValueInt.Create( array[ member ] ) )
            Next
        ElseIf elementid = IntTypeId
            Local array:Int[] = Int[] obj
            For Local member:Int = 0 Until array.length
                list.add( jsonValueInt.Create( array[ member ] ) )
            Next
        ElseIf elementid = LongTypeId
            Local array:Long[] = Long[] obj
            For Local member:Int = 0 Until array.length
                list.add( jsonValueInt.Create( array[ member ] ) )
            Next
        ElseIf elementid = FloatTypeId
            Local array:Float[] = Float[] obj
            For Local member:Int = 0 Until array.length
                list.add( jsonValueFloat.Create( array[ member ] ) )
            Next
        ElseIf elementid = DoubleTypeId
            Local array:Double[] = Double[] obj
            For Local member:Int = 0 Until array.length
                list.add( jsonValueFloat.Create( array[ member ] ) )
            Next
        ElseIf elementid = StringTypeId
            Local array:String[] = String[] obj
            For Local member:Int = 0 Until array.length
                list.add( jsonValueString.Create( array[ member ] ) )
            Next
        Else
            Local array:Object[] = Object[] obj
            For Local member:Int = 0 Until array.length
                list.add( jsonSerializer( controller ).serializeObject( array[ member ] ) )
            Next
        EndIf
        Return list
    End Function
    
End Type

' Recusively turn jsonValue objects, which can be decoded from a string, into an arbitrary class with the specified fields
Type jsonDeserializer Extends jsonSerializationController
    
    ' Initialize with standard deserializers (String, TList, and TMap)
    ' Easy to add your own handlers upon calling init. Example call:
    ' json.init( [ MyFirstTypeId, MySecondTypeId ], [ jsonDeserialize1Type, jsonDeserialize2Type ] )
    Method init:jsonDeserializer( ..
        moreHandlersTypeId:TTypeId[] = Null, ..
        moreHandlersFunc:Object( obj:Object, member:TMember, controller:jsonSerializationController )[] = Null ..
    )
        addTypeHandler( StringTypeId, jsonDeserializeString )
        addTypeHandler( ListTypeId, jsonDeserializeList )
        addTypeHandler( MapTypeId, jsonDeserializeMap )
        If moreHandlersTypeId And moreHandlersFunc
            For Local i:Int = 0 Until moreHandlersTypeId.length
                addTypeHandler( moreHandlersTypeId[i], moreHandlersFunc[i] )
            Next
        EndIf
        Return Self
    End Method
    
    ' Deserialize an object
    Method deserializeObject:Object( json:jsonValue, typeid:TTypeId, parentField:TField = Null )
        If jsonValueNull( json )
            Return Null
        ElseIf typeHandlers.Contains( typeid )
            Local handler:jsonTypeHandlerFunc = jsonTypeHandlerFunc( typeHandlers.ValueForKey( typeid ) )
            Return handler.handle( json, parentField, Self )
        Else
            Local obj:Object = typeid.NewObject()
            Local dict:jsonValueDict = jsonValueDict( json )
            For Local member:TField = EachIn typeid.EnumFields()
                Local value:jsonValue = jsonValue( dict.get().ValueForKey( member.Name() ) )
                If value
                    Local p:Byte Ptr = bbRefFieldPtr( obj, member._index )
                    Local id:TTypeId = member.TypeId()
                    deserializeMember( value, p, id, member )
                EndIf
            Next
            Return obj
        EndIf
    End Method
    Method deserializeMember( value:jsonValue, p:Byte Ptr, typeid:TTypeId, parentField:TField )
        If typeid.ElementType()
            Local list:jsonValueList = jsonValueList( value )
            If list
                Local arrayLength:Int = list.get().count()
                Local arrayType:TTypeId = typeid.ElementType()
                Local array:Object = typeid.NewArray( arrayLength )
                Local arrayIndex:Int = 0
                For Local element:jsonValue = EachIn list.get()
                    Local p:Byte Ptr = bbRefArrayElementPtr( typeid.ElementType()._size, array, arrayIndex )
                    
                    deserializeMember( element, p, arrayType, parentField )
                    arrayIndex :+ 1
                Next
            Else
                Local array:Object = typeid.NewArray( 0 )
                assignObject( p, array )
            EndIf
 
        ElseIf typeid = ByteTypeId
            Local number:Byte = 0
            If jsonValueBool( value )
                number = jsonValueBool( value ).get()
            ElseIf jsonValueInt( value )
                number = jsonValueInt( value ).get()
            ElseIf jsonValueFloat( value )
                number = jsonValueFloat( value ).get()
            ElseIf jsonValueString( value )
                number = Byte jsonValueString( value ).get()
            EndIf
            (Byte Ptr p)[0] = number
        
        ElseIf typeid = ShortTypeId
            Local number:Short = 0
            If jsonValueBool( value )
                number = jsonValueBool( value ).get()
            ElseIf jsonValueInt( value )
                number = jsonValueInt( value ).get()
            ElseIf jsonValueFloat( value )
                number = jsonValueFloat( value ).get()
            ElseIf jsonValueString( value )
                number = Short jsonValueString( value ).get()
            EndIf
            (Short Ptr p)[0] = number
        
        ElseIf typeid = IntTypeId
            Local number:Int = 0
            If jsonValueBool( value )
                number = jsonValueBool( value ).get()
            ElseIf jsonValueInt( value )
                number = jsonValueInt( value ).get()
            ElseIf jsonValueFloat( value )
                number = jsonValueFloat( value ).get()
            ElseIf jsonValueString( value )
                number = Int jsonValueString( value ).get()
            EndIf
            (Int Ptr p)[0] = number
            
        ElseIf typeid = LongTypeId
            Local number:Long = 0
            If jsonValueBool( value )
                number = jsonValueBool( value ).get()
            ElseIf jsonValueInt( value )
                number = jsonValueInt( value ).get()
            ElseIf jsonValueFloat( value )
                number = jsonValueFloat( value ).get()
            ElseIf jsonValueString( value )
                number = Long jsonValueString( value ).get()
            EndIf
            (Long Ptr p)[0] = number
            
        ElseIf typeid = FloatTypeId
            Local number:Float = 0
            If jsonValueBool( value )
                number = jsonValueBool( value ).get()
            ElseIf jsonValueInt( value )
                number = jsonValueInt( value ).get()
            ElseIf jsonValueFloat( value )
                number = jsonValueFloat( value ).get()
            ElseIf jsonValueString( value )
                number = Float jsonValueString( value ).get()
            EndIf
            (Float Ptr p)[0] = number
            
        ElseIf typeid = DoubleTypeId
            Local number:Double = 0
            If jsonValueBool( value )
                number = jsonValueBool( value ).get()
            ElseIf jsonValueInt( value )
                number = jsonValueInt( value ).get()
            ElseIf jsonValueFloat( value )
                number = jsonValueFloat( value ).get()
            ElseIf jsonValueString( value )
                number = Double jsonValueString( value ).get()
            EndIf
            (Double Ptr p)[0] = number
            
        Else
            assignObject( p, deserializeObject( value, typeid, parentField ) )
            
        EndIf
    End Method
    Function assignObject( p:Byte Ptr, value:Object )
        If value
            Local id:TTypeId = TTypeId.ForObject( value )
            Local class:Int = bbRefGetObjectClass( value )
            While class And class <> id._class
                class = bbRefGetSuperClass( class )
            Wend
        EndIf
        bbRefAssignObject( p, value )
    End Function
    
    ' Determine TypeId that jsonValue should deserialize to when bmax doesn't explitly specify
    ' (e.g. objects within a list)
    Function jsonValueTypeId:TTypeId( value:jsonValue, member:TMember )
        Local jsonc:String
        If member jsonc = member.MetaData( jsonSerializeMetadataClassSub )
        If jsonc
            Return TTypeId.ForName( jsonc )
        ElseIf jsonValueString( value )
            Return StringTypeId
        ElseIf jsonValueList( value )
            Return ListTypeId
        ElseIf jsonValueDict( value )
            Return MapTypeId
        Else
            Return ObjectTypeId
        EndIf
    End Function
     
End Type

' Base json value type
Type jsonValue

    ' Get json string from object
    Method toString:String()
        Return Null
    End Method
    
    ' Get object from json string
    Function fromString:jsonValue( str:String, low:Int = 0, high:Int = 0 )
        If high = 0 high = str.length
        
        ' Get next occurence of character in same scope, considering quotes, brackets, etc.
        Function nextChar:Int( str:String, char:Int, low:Int, high:Int )
            Local nestBrace:Int = 0, nestBracket:Int = 0, inQuote:Int = False
            Local i:Int = low
            While i < high
                If nestBrace = 0 And nestBracket = 0 And inQuote = False And str[i] = char
                    Return i
                ElseIf str[i] = jsonEscapeAsc
                    i :+ 1
                ElseIf str[i] = jsonStringAsc
                    inQuote = Not inQuote
                ElseIf str[i] = jsonOpenBraceAsc
                    nestBrace :+ 1
                ElseIf str[i] = jsonOpenBracketAsc
                    nestBracket :+ 1
                ElseIf str[i] = jsonCloseBraceAsc
                    nestBrace :- 1
                ElseIf str[i] = jsonCloseBracketAsc
                    nestBracket :- 1
                EndIf
                i :+ 1
            Wend
            Return -1
        End Function
        
        ' Skip whitespace
        While str[ low ] < 32
            low :+ 1
        Wend
        
        ' Parse string
        If str[ low ] = jsonStringAsc
            Return jsonValueString.Create( jsonParseString( str, low, high ) )
            
        ' Parse dict
        ElseIf str[ low ] = jsonOpenBraceAsc
            Local dict:jsonValueDict = jsonValueDict.Create()
            Local i:Int = low+1
            While i < high-1
                Local assign:Int = nextChar( str, jsonAssignmentAsc, i, high-1 )
                Local delim:Int = nextChar( str, jsonDelimiterAsc, i, high-1 )
                Assert assign >= 0, "Syntax error: Missing assignment character"
                Local key:String = jsonParseString( str, i, assign )
                If delim >= 0
                    Local value:jsonValue = jsonValue.fromString( str, assign+1, delim )
                    dict.add( key, value )
                    i = delim+1
                Else
                    Local value:jsonValue = jsonValue.fromString( str, assign+1, high-1 )
                    dict.add( key, value )
                    Exit
                EndIf
            Wend
            Return dict
        
        ' Parse list 
        ElseIf str[ low ] = jsonOpenBracketAsc
            Local list:jsonValueList = jsonValueList.Create()
            Local i:Int = low+1
            While i < high-1
                Local delim:Int = nextChar( str, jsonDelimiterAsc, i, high-1 )
                If delim >= 0
                    list.add( jsonValue.fromString( str, i, delim ) )
                    i = delim+1
                Else
                    list.add( jsonValue.fromString( str, i, high-1 ) )
                    Exit
                EndIf
            Wend
            Return list
        
        ' Parse others
        Else
            Local sub:String = str[ low..high ]
            
            ' Parse number
            If str[ low ] >= 48 And str[ low ] <= 57
                Local isDecimal:Int = False
                For Local i:Int = low Until high
                    If str[i] = jsonDecimalAsc isDecimal = True; Exit
                Next
                If isDecimal
                    Return jsonValueFloat.Create( Double( sub ) )
                Else
                    Return jsonValueInt.Create( Long( sub ) )
                EndIf
            
            ' Parse keywords
            ElseIf sub = "null"
                Return jsonValueNull.Create()
            ElseIf sub = "true"
                Return jsonValueBool.Create( True )
            ElseIf sub = "false"
                Return jsonValueBool.Create( False )
                
            EndIf
        EndIf
        
        Throw "Syntax error: Unexpected character "+Chr( str[ low ] )
    End Function
    
    ' Utility functions for strings
    Function jsonSanitizeString:String( str:String )
        Return str.Replace( "~q", "\~q" )
    End Function
    Function jsonDesanitizeString:String( str:String )
        Return str.Replace( "\~q", "~q" )
    End Function
    Function jsonParseString:String( str:String, low:Int, high:Int )
        Local sub:String = str[ low..high ]
        sub = sub.Trim()
        Local qstart:Int = (sub[0] = jsonStringAsc)
        Local qend:Int = (sub[ sub.length-1 ] = jsonStringAsc) And (sub.length < 2 Or sub[ sub.length-2 ] <> jsonEscapeAsc)
        If qstart Or qend sub = sub[ qstart .. sub.length-qend ]
        Return jsonDesanitizeString( sub )
    End Function
    
End Type

' Null type
Type jsonValueNull Extends jsonValue
    Function Create:jsonValueNull()
        Return New jsonValueNull
    End Function
    Method toString:String()
        Return "null"
    End Method
    Method get:Object()
        Return Null
    End Method
End Type

' Boolean type
Type jsonValueBool Extends jsonValue
    Field value:Int
    Function Create:jsonValueBool( value:Int = 0:Int )
        Local this:jsonValueBool = New jsonValueBool; this.value = value; Return this
    End Function
    Method toString:String()
        If value Return "true" Else Return "false"
    End Method
    Method get:Int()
        Return value
    End Method
    Method set( value:Int )
        Self.value = value
    End Method
End Type

' Integer type
Type jsonValueInt Extends jsonValue
    Field value:Long
    Function Create:jsonValueInt( value:Long = 0:Long )
        Local this:jsonValueInt = New jsonValueInt; this.value = value; Return this
    End Function
    Method toString:String()
        Return String( value )
    End Method
    Method get:Long()
        Return value
    End Method
    Method set( value:Long )
        Self.value = value
    End Method
End Type

' Float type
Type jsonValueFloat Extends jsonValue
    Field value:Double
    Function Create:jsonValueFloat( value:Double = 0:Double )
        Local this:jsonValueFloat = New jsonValueFloat; this.value = value; Return this
    End Function
    Method toString:String()
        Return String( value )
    End Method
    Method get:Double()
        Return value
    End Method
    Method set( value:Double )
        Self.value = value
    End Method
End Type

' String type
Type jsonValueString Extends jsonValue
    Field value:String
    Function Create:jsonValueString( value:String = "" )
        Local this:jsonValueString = New jsonValueString; this.value = value; Return this
    End Function
    Method toString:String()
        Return "~q" + jsonSanitizeString( value ) + "~q"
    End Method
    Method get:String()
        Return value
    End Method
    Method set( value:String )
        Self.value = value
    End Method
End Type

' List type
Type jsonValueList Extends jsonValue
    Field value:TList
    Function Create:jsonValueList( value:TList = Null )
        If value = Null value = CreateList()
        Local this:jsonValueList = New jsonValueList; this.value = value; Return this
    End Function
    Method toString:String()
        Local content:String = "", first:Int = True
        For Local member:jsonValue = EachIn value
            If first first = False Else content :+ ","
            content :+ member.toString()
        Next
        Return "[" + content + "]"
    End Method
    Method get:TList()
        Return value
    End Method
    Method set( value:TList )
        Self.value = value
    End Method
    
    Method add:TLink( value:jsonValue )
        Return Self.value.addlast( value )
    End Method
End Type

' Dict type
Type jsonValueDict Extends jsonValue
    Field value:TMap
    Function Create:jsonValueDict( value:TMap = Null )
        If value = Null value = CreateMap()
        Local this:jsonValueDict = New jsonValueDict; this.value = value; Return this
    End Function
    Method toString:String()
        Local content:String = "", first:Int = True
        For Local key:Object = EachIn value.Keys()
            If first first = False Else content :+ ","
            Local member:Object = value.ValueForKey( key )
            content :+ "~q" + jsonSanitizeString( String( key ) ) + "~q:" + jsonValue( member ).toString()
        Next
        Return "{" + content + "}"
    End Method
    
    Method get:TMap()
        Return value
    End Method
    Method set( value:TMap )
        Self.value = value
    End Method
    Method add( key:Object, value:jsonValue )
        Self.value.insert( key, value )
    End Method
End Type
