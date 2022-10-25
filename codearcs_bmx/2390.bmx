; ID: 2390
; Author: Otus
; Date: 2008-12-28 18:32:42
; Title: XmlToMax
; Description: Convert XML to/from user defined types

SuperStrict

Import BRL.Reflection

Import BaH.LibXml

Type TNode Abstract
	
	Method AddChild(n:TNode) Abstract
	
	Method GetChildren:TList() Abstract
	
	Global typeid:TTypeId = TTypeId.ForName("TNode")
	
End Type

Function XmlToMax:TNode(x:TxmlNode)
	Local t:TTypeId = TTypeId.ForName(x.GetName())
	If Not t Return Null
	
	Local n:TNode = TNode(t.NewObject())
	If Not n Return Null
	
	'Convers attributes to fields
	Local l:TList = x.GetAttributeList()
	If l
		For Local a:TxmlAttribute = EachIn l
			Local f:TField = t.FindField(a.GetName())
			If f Then f.Set n, a.GetValue()
		Next
	End If
	
	'Convert child elements
	l = x.GetChildren()
	If l
		For Local c:TxmlNode = EachIn l
			n.AddChild XmlToMax(c)
		Next
	End If
	
	Return n
End Function

Function MaxToXml:TxmlNode(n:TNode, parent:TxmlNode = Null)
	Local t:TTypeId = TTypeId.ForObject(n)
	
	Local x:TxmlNode
	If parent
		x = parent.AddChild(t.Name())
	Else
		x = TxmlNode.newNode(t.Name())
	End If
	
	'Convert fields to attributes
	For Local f:TField = EachIn t.EnumFields()
		If f.MetaData("xml") Then x.AddAttribute f.Name(), String(f.get(n))
	Next
	
	'Convert child nodes
	For Local c:TNode = EachIn n.GetChildren()
		MaxToXml c, x
	Next
	
	Return x
End Function
