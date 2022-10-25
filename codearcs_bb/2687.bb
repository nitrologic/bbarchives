; ID: 2687
; Author: slenkar
; Date: 2010-04-01 14:00:43
; Title: keefpersistence
; Description: fast persistence for lots of objects

Strict
Module keef.keefpersistence
Import bah.libxml
Import brl.map
Import brl.reflection




Global object_Map:TMap







	
Function save_obj(i:Object, filename$)
object_map=New Tmap
Global doc:TxmlDoc = TxmlDoc.newdoc("1.0")

Local root:TxmlNode = TxmlNode.newnode("root")
doc.setrootelement(root)
Local thisnode:TxmlNode=root.addchild("object")
serialize_obj(thisnode,i )
doc.saveFile(filename)
EndFunction
	  
Function serialize_obj(node:TxmlNode, i:Object)

Local tid:TTypeId = TTypeId.ForObject(i)
'Print "saving " + tid.Name()
Local thename:String=tid.name()
node.setattribute("type",thename)
Local objRef:String = GetObjRef(i)
			node.setAttribute("ref", objRef)
			object_Map.Insert(objRef, objRef)

If thename="TList"
node.setAttribute("type", theName)
Local listy:TList=TList(i)
For Local ob:Object=EachIn listy
Local tid2:TTypeId = TTypeId.ForObject(ob)
Local thisnode:TxmlNode = node.addchild("listelement")
thisnode.setattribute("type",tid2.name())
serialize_obj(thisnode,ob)
Next
Return
EndIf

If theName.endswith("[]")
				'Local thisnode:TxmlNode = node.addchild("array")
				Local size:Int
				
				size = tid.ArrayLength(i)
				
				node.setAttribute("type", theName)
				node.setAttribute("size", size)

				If size > 0 Then
				saveArray(i, size, node, tid)
				Else
				node.setattribute("value","null")
				End If
EndIf

If thename = "TMap"
					' special case for TMaps
				' They have a Global "nil" object which needs to be referenced properly.
				' We add a specific reference to nil, which we'll use to re-reference when we de-serialize.

				Local ref:String = GetObjRef(New TMap._root)
				If Not object_Map.ValueForKey(ref) Then
					'Local node:TxmlNode = parent.addChild("TMap_nil")
					node.setAttribute("nil", ref)
					object_Map.Insert(ref, ref)
				End If
			
EndIf

For Local f:TField = EachIn tid.EnumFields()
										 ' Print "field name: " + f.Name()
										  
					If f.MetaData("nopersist") Then
						Continue
					End If
				
					Local fieldType:TTypeId = f.TypeId()
					
					Local fieldNode:TxmlNode 
						
					Select fieldType
						Case ByteTypeId
						If f.getint(i)=Null Or f.getint(i)=0
						Continue
						EndIf
						
							fieldnode= node.addChild("field")
							fieldNode.setattribute("value", f.GetInt(i))
						Case ShortTypeId
						If f.getint(i)=Null Or f.getint(i)=0
						Continue
						EndIf
						fieldNode.setattribute("value", f.GetInt(i))
					
						Case IntTypeId
						If f.getint(i)=Null Or f.getint(i)=0
						Continue
						EndIf
						fieldnode= node.addChild("field")
						fieldNode.setattribute("value", f.GetInt(i))
					
						Case LongTypeId
						If f.getlong(i)=Null Or f.getlong(i)=0
						Continue
						EndIf
						fieldnode= node.addChild("field")
						fieldNode.setattribute("value", f.GetLong(i))
						
						Case FloatTypeId
						
						If f.getfloat(i)=Null Or f.getfloat(i)=0
						Continue
						EndIf
						fieldnode= node.addChild("field")
						fieldNode.setattribute("value", f.GetFloat(i))
					
						Case DoubleTypeId
						If f.getdouble(i)=Null Or f.getdouble(i)=0
						Continue
						EndIf
						fieldnode= node.addChild("field")
						fieldNode.setattribute("value", f.GetDouble(i))
					
						Case StringTypeId
						If f.getstring(i)=Null Or f.getstring(i)="0" Or f.getstring(i)=""
						Continue
						EndIf
						fieldnode= node.addChild("field")
							Local s:String = f.GetString(i)
							If s Then
								fieldNode.setattribute("value", s)
							End If
						Default
							If fieldtype.name().EndsWith("[]")
					
			'	Local thisnode:TxmlNode = node.addchild("array")
				Local size:Int
				
				size=fieldType.ArrayLength(f.Get(i))

				If size > 0
				fieldnode= node.addChild("field")
				Fieldnode.setAttribute("size", size)
				fieldnode.setattribute("type",fieldtype.name())
				'saveArray(f, size, fieldnode, fieldType)
				saveArray(f.get(i), size, fieldnode, fieldType)
				End If
							Else
							'Print ("get object from field")
								Local fieldObject:Object = f.Get(i)
							Local fieldRef:String = GetObjRef(fieldObject)
							
								If fieldObject <> Null
								fieldNode = node.addChild("field")
								fieldnode.setattribute("type",fieldtype.name())
								If Not object_Map.Contains(fieldref) Then
									serialize_obj(fieldNode, fieldObject)
									Else
									fieldNode.setAttribute("ref", fieldref)
									End If
								Else
								Continue
								End If
							End If
					End Select
						fieldNode.setAttribute("name", f.Name())
						fieldnode.setAttribute("type",fieldtype.name())
						
					
				Next

EndFunction



	  
Function saveArray(arrayObject:Object, size:Int, node:TxmlNode, typeId:TTypeId)
									   
		Local elementType:TTypeId = typeId.ElementType()
		
		Select elementType
			Case ByteTypeId, ShortTypeId, IntTypeId, LongTypeId, FloatTypeId, DoubleTypeId

				Local content:String = ""
				
				For Local i:Int = 0 Until size
				
				Local aObj:Object = typeId.GetArrayElement(arrayObject, i)
				'Print String(aobj)
				content=String(aobj)
				Local content2:Int=content.toint()
				If Not content2=0
				Local thisnode:TxmlNode=node.addchild("arrayelement")
				thisnode.addattribute("pos",String(i))
				thisnode.addattribute("value",String(aobj))
				EndIf
				Next
		
			Default

				Local content:String = ""
				
			
				
			
				
					Select elementType
						Case StringTypeId
						For Local i:Int = 0 Until size
							Local aObj:Object = typeId.GetArrayElement(arrayObject, i)
					
						content = String(aobj)
						If Not content = "0"
						Local thisnode:TxmlNode = node.addchild("arrayelement")
						thisnode.addattribute("pos", String(i))
						thisnode.addattribute("value", content)
						EndIf
						Next
						
						Default
							For Local i:Int = 0 Until size
							Local aObj:Object = typeId.GetArrayElement(arrayObject, i)
							If aobj<>Null
							Local thisnode:TxmlNode = node.addchild("arrayelement")
							thisnode.addattribute("pos", String(i))
							thisnode.addattribute("type", typeId.elementType().Name())
							'If  typeId.elementType().Name().endswith("[]")
							'Local thisnode2:TxmlNode=TxmlNode(thisnode.getfirstchild())
							'serialize_obj(thisNode2, aObj)
							
							'Else
							serialize_obj(thisNode, aObj)
							'EndIf
							
							EndIf
							Next
					End Select
			
		End Select
		
	

EndFunction






Function load_object:Object(filename$)
object_map=New tmap
				' Print "loading object " + filename
	Local doc:TxmlDoc = TxmlDoc.parseFile(filename)
Local obby:Object

		If doc
		'Print "doc is not null"
			Local root:TxmlNode = doc.GetRootElement()
			For Local thisnode:TxmlNode=EachIn root.getchildren()
			obby:Object = DeSerializeObject("", thisnode,doc)
			doc.Free()
			doc = Null
			Next
		Else
		'Print "doc is null"
		End If
		Local arr:Int[] = Int[] obby
		For Local x:Int = 0 To arr.length - 1
		'Print arr[x]
		Next
Return obby
EndFunction

		
Function DeSerializeObject:Object(text:String, node:TxmlNode = Null,doc:TxmlDoc)
'If node.getattribute("type")="map_square"
'DebugLog "deserializing map square"
'EndIf
'DebugLog "argh fuck "+node.getname()
Local obj:Object 
'Local nodelist:TList = node.getchildren()
	Local ref:String = Node.getAttribute("ref")
										If ref 
										Local objRef:Object = object_Map.ValueForKey(ref)
											If objRef
											
												Return objref
											EndIf
											EndIf

Local objType:TTypeId = TTypeId.ForName(node.getattribute("type"))

If node.getattribute("type").endswith("[]")
'If node.getattribute("type")="map_square[]"
'DebugLog "creating array in deserialize object "+node.getattribute("type")
'EndIf
obj = Create_array(node, doc)
'If node.getattribute("type")="map_square[]"
'If obj=Null
'DebugLog "returning null array"
'Else
'DebugLog "returning full array"
'EndIf
'EndIf

Else

If node.getattribute("type")="TList"
'Print "deserializing list:"
Local listy:TList= New TList
obj=Object(listy)
object_Map.Insert(node.getAttribute("ref"), obj)

Local listy2:TList=node.getchildren()

If listy2<>Null
For Local c:TxmlNode=EachIn listy2

If c.getattribute("type")="String"
listy.addlast(c.getattribute("value"))
Else
listy.addlast(deSerializeObject("", c,doc))
EndIf

Next
EndIf
Return obj

EndIf 
				
	If node.getattribute("type") = "TMap" 

					' special case for TMaps
					' They have a Global "nil" object which needs to be referenced properly.
			
					Local attr:String = node.getAttribute("nil")
					If attr Then
						object_Map.Insert(attr, New TMap._root)
					End If
				End If

obj=objType.NewObject()
object_Map.Insert(node.getAttribute("ref"), obj)

Local listy:TList=node.getchildren()

	If listy <> Null Then
					For Local fieldNode:TxmlNode = EachIn listy
		If fieldNode.GetName() = "field"	
	Local fieldObj:TField = objType.FindField(fieldnode.getAttribute("name"))
							If fieldobj<>Null
							Local fieldType:String = fieldNode.getAttribute("type")
							Select fieldType
								Case "Byte", "Short", "Int"
									fieldObj.SetInt(obj, fieldNode.getattribute("value").toInt())
								'If node.getattribute("type")="map_square"
								'DebugLog fieldNode.getattribute("value")+" "+ fieldNode.getattribute("name")
								
								 
								'EndIf
								Case "Long"
									fieldObj.SetLong(obj, fieldNode.getattribute("value").toLong())
								Case "Float"
									fieldObj.SetFloat(obj, fieldNode.getattribute("value").toFloat())
								Case "Double"
									fieldObj.SetDouble(obj, fieldNode.getattribute("value").toDouble())
								Case "String"
									fieldObj.SetString(obj, fieldnode.getattribute("value"))
								Default
								
										Local ref:String = fieldNode.getAttribute("ref")
										If ref Then
											Local objRef:Object = object_Map.ValueForKey(ref)
											If objRef Then
											
												fieldObj.Set(obj, objRef)
											Else
											fieldObj.Set(obj, DeSerializeObject("", fieldNode,doc))
											End If
										Else
											fieldObj.Set(obj, DeSerializeObject("", fieldNode,doc))
										End If
										
								EndSelect
								EndIf
			EndIf
			Next
			EndIf					
	EndIf							
'If node.getattribute("type")="map_square"
'DebugLog "returning obj"
'EndIf
Return obj
	End Function

Function Create_array:Object(node:TxmlNode, doc:TxmlDoc)

'If node.getattribute("type")="map_square[]"
'DebugLog "Create array function"
'EndIf		 

Local objType:TTypeId = TTypeId.ForName(node.getAttribute("type"))
		
				Local size:Int = node.getAttribute("size").toInt()
				Local obj:Object = objType.NewArray(size)
				If node.getattribute("type")="map_square[]"
				If obj=Null
				DebugLog "array object is null"
				EndIf
				EndIf
				
				If obj<>Null
				
				'If node.getattribute("type")="map_square[]"
				'DebugLog "array object is not null:"
				'EndIf
				
				If size > 0 
				
				If node.getattribute("type")="map_square[]"
				DebugLog "array size is bigger than zero:"+size
				EndIf
				
					Local arrayElementType:TTypeId = objType.ElementType()
					'Print "array is: "+arrayelementtype.name()
					Select arrayElementType
						Case ByteTypeId, ShortTypeId, IntTypeId, LongTypeId, FloatTypeId, DoubleTypeId
						'Print"array element is int"
							Local arraylist:TList = node.getChildren()
							Local arraynode:TxmlNode
							If arraylist<>Null
							For arraynode = EachIn arraylist
							'Print arraynode.getAttribute("value")
							'Print arraynode.getAttribute("pos")
							
							If Int arraynode.getAttribute("value") <> 0
							objType.SetArrayElement(obj, Int arraynode.getAttribute("pos") , Object arraynode.getAttribute("value"))
								
							EndIf
							Next
							EndIf
						Default
							Local arrayList:TList = node.getChildren()
							
							If arrayList
								
								Local i:Int
								For Local arrayNode:TxmlNode = EachIn arrayList
		
									Select arrayElementType
										Case StringTypeId
										If arrayNode.Getattribute("value")<>""
											objType.SetArrayElement(obj, Int arraynode.getAttribute("pos"), arrayNode.Getattribute("value"))
										EndIf
										Default
										
									
											objType.SetArrayElement(obj, Int arraynode.getAttribute("pos"), DeSerializeObject("", arrayNode, doc))
									End Select
		
									i:+ 1
								Next
							EndIf
					End Select
				EndIf
				End If
						  Return obj
EndFunction

	Function GetObjRef:String(obj:Object)
		Return Base36(Int(Byte Ptr(obj)))
	End Function

	Function Base36:String( val:Int )
		Local buf:Short[6]
		For Local k:Int=5 To 0 Step -1
			Local n:Int=(val Mod 36) + 48
			If n > 57 n:+ 7
			buf[k]=n
			val = val / 36
		Next
		Return String.FromShorts( buf,6 )
	End Function
