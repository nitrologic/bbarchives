; ID: 2962
; Author: Madk
; Date: 2012-07-24 10:25:20
; Title: XML Parser &amp; Saver
; Description: Capable of efficiently reading and writing XML data. This is not a wrapper.

' 	--+-----------------------------------------------------------------------------------------+--
'	  | This code was originally written by Sophie Kirschner (meapineapple@gmail.com) and it is |  
' 	  | released as public domain. Please do not interpret that as liberty to claim credit that |  
' 	  | is not yours, or to sell this code when it could otherwise be obtained for free because |  
'	  |                    that would be a really shitty thing of you to do.                    |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

Import pine.BinTree
Import brl.stream


' example program
' loads your xml file named 'example_input.xml', parses it, and then writes it as 'example_output.xml'
Rem 
Local f:TStream=ReadFile("example_input.xml")
Local n:xmlnode=xmlnode.read(f)
CloseFile f
f=WriteFile("example_output.xml")
n.write f
CloseFile f
End
EndRem 


Rem
bbdoc: XML Node type
about: children:BinTree and attributes:BinTree are two fields containing all the children nodes and the attributes, respectively.
They can be iterated through using EachIn. (For a:xmlattribute=EachIn node.attributes; For n:xmlnode=EachIn node.children)
TreeFind:Object(tree:BinTree,key$) will return the first value in the tree with the given key.
TreeFindAll:TList(tree:BinTree,key$) will return a TList containing all the values in the tree with the given tree.
See the BinTree documentation for more detailed info and how to iterate through keys, nodes containing key/value pairs, manipulation, etc.
EndRem 
Type xmlnode
	Field name$
	Field children:BinTree=CreateTree()
	Field attributes:BinTree=CreateTree()
	Rem
	bbdoc: Returns a new xmlnode.
	EndRem 
	Function Create:xmlnode(name$)
		Local n:xmlnode=New xmlnode
		n.name=name
		Return n
	End Function
	Rem
	bbdoc: Adds a new child.
	EndRem 
	Method addchild(n:xmlnode)
		Assert n,"Cannot assign a nonexistent xmlnode as a child."
		children.insert n.name,n
	End Method
	Rem
	bbdoc: Adds a new attribute.
	returns: The created xmlattribute object.
	EndRem 
	Method addattribute:xmlattribute(name$,value$)
		Local a:xmlattribute=New xmlattribute
		a.name=name
		a.value=value
		attributes.insert name,a
		Return a
	End Method
	Rem
	bbdoc: Removes a child.
	EndRem 
	Method removechild(n:xmlnode)
		Assert a,"Attempted to remove nonexistent XML node."
		children.removevalue(n,n.name)
	End Method
	Rem
	bbdoc: Removes an attribute.
	EndRem 
	Method removeattribute(a:xmlattribute)
		Assert a,"Attempted to remove nonexistent XML attribute."
		attributes.removevalue(a,a.name)
	End Method
	Rem
	bbdoc: Returns the value assocated with some attribute name.
	EndRem 
	Method getvalue$(name$)
		Local val:xmlattribute=xmlattribute(attributes.find(name))
		If Not val Return ""
		Return val.value
	End Method
	Rem
	bbdoc: Returns the attribute assocated with the given name.
	EndRem 
	Method getattribute:xmlattribute(name$)
		Return xmlattribute(attributes.find(name))
	End Method
	Rem
	bbdoc: Returns the first encountered child node with the given name.
	EndRem 
	Method getchild:xmlnode(name$)
		Return xmlnode(children.find(name))
	End Method
	Rem
	bbdoc: Returns a list of all child nodes with the given name.
	EndRem 
	Method getchildren:TList(name$)
		Return children.findall(name)
	End Method
	Rem
	bbdoc: Read an XML Node (along with any children) from a stream.
	EndRem 
	Function read:xmlnode(f:TStream)
		Local n:xmlnode=New xmlnode
		While ReadByte(f)<>opentag
			Assert Not Eof(f),"Encountered unexpected end-of-file."
		Wend
		Local nb%
		Local tag$=""
		Repeat
			nb=ReadByte(f)
			If nb=fintag Then Exit
			tag:+Chr(nb)
		Forever
		Assert Len(tag),"Encountered illegal tag: <>"
		If Asc(Right(tag,1))=closetag Then ' has no children
			tag=Left(tag,tag.length-1)
		ElseIf Asc(Left(tag,1))=closetag Then ' is a closing tag
			Return Null
		Else
			Local clist:TList=CreateList()
			Repeat
				Local c:xmlnode=read(f)
				If c Then
					clist.addfirst c
				Else
					Exit
				EndIf
			Forever
			For Local c:xmlnode=EachIn clist
				n.children.insert c.name,c
			Next
		EndIf
		n.parsetag tag
		Assert n.name,"Encountered nameless tag."
		
		Rem
		?debug
		DebugLog "Read xmlnode: "+n.name
		Local attrstr$=""
		For Local a:xmlattribute=EachIn n.attributes
			attrstr:+a.name+" = ~q"+a.value+"~q; "
		Next
		Local chldstr$=""
		For Local c:xmlnode=EachIn n.children
			chldstr:+c.name+"; "
		Next
		DebugLog "Attributes: "+attrstr
		DebugLog "Children: "+chldstr
		?
		EndRem
		
		Return n
	End Function
	Rem
	bbdoc: Write an XML Node (and all its children) to a stream.
	EndRem 
	Method write(f:TStream,prefix$="")
		Assert f,"Stream does not exist."
		WriteString f,prefix
		WriteByte f,opentag
		WriteString f,name
		For Local a:xmlattribute=EachIn attributes
			WriteByte f,space
			WriteString f,a.name
			WriteByte f,equals
			WriteByte f,quote
			WriteString f,a.value
			WriteByte f,quote
		Next
		If children.isempty() Then
			WriteByte f,space
			WriteByte f,closetag
			WriteByte f,fintag
			WriteByte f,newl
		Else
			WriteByte f,fintag
			WriteByte f,newl
			For Local n:xmlnode=EachIn children
				n.write f,prefix+Chr(tab)
			Next
			WriteString f,prefix
			WriteByte f,opentag
			WriteByte f,closetag
			WriteString f,name
			WriteByte f,fintag
			WriteByte f,newl
		EndIf
	End Method
	
	' private stuff that you shouldn't need to touch
	Const opentag%=Asc("<")
	Const closetag%=Asc("/")
	Const fintag%=Asc(">")
	Const space%=Asc(" ")
	Const tab%=Asc("	")
	Const newl%=Asc("~n")
	Const equals%=Asc("=")
	Const quote%=Asc("~q")
	Function iswhitespace%(c%)
		Return (c=space) Or (c=tab) Or (c=newl)
	End Function
	Method parsetag(str$)
		Local x%=0
		Local on$="",spaces%=0
		Local inquotes%=0
		Local lastwasspace%=1
		Local lasta:xmlattribute
		While x<str.length
			If inquotes=0 And iswhitespace(str[x]) Then
				If Not lastwasspace
					If spaces=0 Then 
						name=on
						on=""
						spaces=1
					EndIf
					lastwasspace=1
				EndIf
			ElseIf inquotes=0 And str[x]=equals
				Local a:xmlattribute=New xmlattribute
				a.name=on
				attributes.insert a.name,a
				lasta=a
				on=""
				lastwasspace=0
			ElseIf str[x]=quote
				If inquotes
					Assert lasta,"Encountered malformed tag."
					lasta.value=on
					on=""
					lasta=Null
				EndIf
				inquotes=Not inquotes
				lastwasspace=0
			ElseIf lasta And inquotes
				on:+Chr(str[x])
				lastwasspace=0
			ElseIf Not lasta
				on:+Chr(str[x])
				lastwasspace=0
			EndIf
			x:+1
		Wend
		If Not spaces Then
			name=on
		EndIf
	End Method
	
End Type

Rem
bbdoc: XML Node Attribute type
EndRem 
Type xmlattribute
	Field name$
	Field value$
End Type
