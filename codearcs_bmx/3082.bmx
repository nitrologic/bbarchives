; ID: 3082
; Author: Pineapple
; Date: 2013-09-25 16:00:47
; Title: Dictnode type, for parsing things
; Description: It's like XML but better; especially useful for external configuration files

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--




SuperStrict

Import pine.BinTree ' http://blitzbasic.com/Community/posts.php?topic=97992 DIRECT: http://dl.dropbox.com/u/10116881/blitz/pine.bintree/pine.bintree.mod.zip
Import brl.filesystem
Import brl.stream
Import brl.retro

Rem

	The dictnode object is relatively straightforward to use. It's very much like XML, but with a lot fewer characters.
	
	---
	
	A node is defined like so:
	
		# node [ ] ;
	
	The hash designates the beginning of a definition, "node" gives a name for the node, the data for the node belongs inside the "[ ]", and the ";" is
	an (optional) termination character. Note - while it's optional here (since the parser is smart enough to just end on a "]") it isn't optional
	in most other places. Also note that whitespace is ALWAYS optional.
	
	---
	
	We can add some information to the node:
	
		# node [
			stuff : "foo" ;
			stuff : "bar" ;
		] ;
	
	This gives the node two values that can be accessed as members of a list with the getvalues("stuff") method, or the first one created with the
	getvalue("stuff") method. Values are always represented with and returned as Strings, so you might need to cast things to Ints or Doubles sometimes.
	Of course, you can call the values whatever you like; "stuff" is only one example. You can even include funky characters! Try using something like
	"S"#"uff" if you're really inclined to put a hash into a value name.
	
	Quotation marks are ALWAYS optional. However, leading and trailing whitespace that is not enclosed in quotes will be disregarded by the parser.
	
	If you want to put quotes in your values without them being parsed as quotes, you have a couple options. You can alter the constant in the dictnode
	type to give the parser the idea that some other character, say "`", should be used as quotes instead. You could also decide on some sequence of
	characters like "\q" and use Replace to turn occurrences of it into quotation marks after all the parsing is done.
	
	---
	
	Nodes can also be nested:
	
		# node [
			# another node [
				foo : "hello" ;
				bar : "world" ;
			] ;
			# so many nodes oh my gosh [
				foobar : hi ;
			] ;
		] ;
	
	You can get a list of nodes with the same name using the method getchildren(), and you can get the first one with some name with getchild(). In
	this example, getchild("another node") would return the first nested dictnode.
	
	You can't play tricks with special characters and quotation marks in node names because I'm too lazy to implement it. If you really, really, really
	want to put open brackets or something in the names of your nodes then the source code is just below here a bit, and you're welcome to put it in
	yourself.
	
	---
	
	Comments!
	
		# node < this is a comment > [
			fo< this is a comment, too. >o : "hello, < this isn't a comment. because it's inside quotation marks. >" ;
			bar: 123< comments can go ANYWHERE >456 ;
		] < except in quotation marks > ;
		<< you can nest comments, too! > this is still a comment! >
		
	You can interrupt anything you like with comments. Note that comments are never terminated by a newline.
	
	---
	
	Finally, you can also include additional files.
	
	node1.txt:
	
		# node1 [
			foo : bar ;
			include : "node2.txt" ;
		] ;
		
	node2.txt:
	
		# node2[
			foobar : "where do we even go from here?" ;
		] ;
		
	The result would be like this:
	
		# node1 [
			foo : bar ;
			#node2 [
				foobar : "where do we even go from here?" ;
			] ;
		] ; 
		
	You can't properly read or include files that aren't all contained within a single root node, sorry.
	
	---
	
	And that's that! Have fun with it. It's been my go-to method of defining game data outside the code for a matter of years now.
	
EndRem



' Example program

Rem

' Simple rectangle type. We'll be reading these from the example file.
Type rect
	' A global list containing all the rects we create.
	Global list:TList=CreateList()
	' Position and dimensions, pretty straightforward here.
	Field x%,y%,width%,height%
	' Create a rectangle using the information in a dictnode.
	Function Create:rect(node:dictnode)
		Local n:rect=New rect
		n.x=Int(node.getvalue("x"))
		n.y=Int(node.getvalue("y"))
		n.width=Int(node.getvalue("width"))
		n.height=Int(node.getvalue("height"))
		list.addlast n
		Return n
	End Function
End Type

' Read the root dictnode object from a file.
Global path$="dictnode_test.txt"
Local root:dictnode=dictnode.parsefile(path)
If Not root Then Print "Couldn't read file "+path;End

' Dump it to the console, just to show how pretty it is.
Print root.dump()

' Now go through all the children of the root node and turn them into rectangle objects.
For Local node:dictnode=EachIn root.children
	If Lower(node.name)="rectangle" Then rect.Create(node)
Next

' Finally, display the result!
Graphics 256,256
For Local r:rect=EachIn rect.list
	DrawRect r.x,r.y,r.width,r.height
Next
Repeat
	Flip
	Delay 100
	If KeyDown(27) Or AppTerminate() Then End
Forever

EndRem



' Node type.
Type dictnode
	' The name of the node, as defined after the "#" and before the "[".
	Field name$=""
	' The path to the file the node was read from. This can be useful if your dictnode file is listing off file paths and you want them to be
	' relative the dictnode file they were defined in. This becomes really useful when you're including lots of files.
	Field path$
	' Positions in the file stream where the node was defined (the "#"), where the body started (the "["), and where it ended (the "]").
	Field nodestartpos%,nodebodypos%,nodebodyend%
	' BinTree containing all the values as Strings and linked to their names as keys
	Field values:BinTree=CreateTree()
	' BinTree containing all the children dictnode objects and linked to their names as keys
	Field children:BinTree=CreateTree()
	' Consts tell the parser all about special characters
	Const nodedefine%	=Asc("#")
	Const nodeopen%	=Asc("[")
	Const nodeclose%	=Asc("]")
	Const lineend%	=Asc(";")
	Const assignment%	=Asc(":")
	Const quote%		=Asc("~q")
	Const commentopen%	=Asc("<")
	Const commentclose%	=Asc(">")
	' Returns true if there's at least one value with the specific key, false otherwise.
	Method hasvalue%(key$)
		Return TreeContains(values,key)
	End Method
	' Returns true if there's at least one child node with the specific key, false otherwise.
	Method haschild%(key$)
		Return TreeContains(children,key)
	End Method
	' Returns the first occurence of a value with a specific key.
	Method getvalue$(key$)
		Return String(TreeFind(values,key))
	End Method
	' Returns the first child node with a specific key.
	Method getchild:dictnode(key$)
		Return dictnode(TreeFind(children,key))
	End Method
	' Returns a list of all values with a specific key.
	Method getvalues:TList(key$)
		Return TreeFindAll(values,key)
	End Method
	' Returns a list of all child nodes with a specific key.
	Method getchildren:TList(key$)
		Return TreeFindAll(children,key)
	End Method
	' Like ToString() except badass, you could write this string to a file if you wanted to save the dictnode in addition to just reading it.
	Const tabstr$="    "
	Method dump$(tabs$="")
		Local str$=tabs+"#"+name+"[~n"
		Local ttabs$=tabs+tabstr
		For Local valnode:BinNode=EachIn TreeNodes(values)
			For Local val$=EachIn valnode.values()
				str:+ttabs+valnode.key+": ~q"+val+"~q;~n"
			Next
		Next
		For Local d:dictnode=EachIn children
			str:+d.dump(ttabs)
		Next
		str:+tabs+"];~n"
		Return str
	End Method
	' Takes a file and spits out its root dictnode.
	Function parsefile:dictnode(path$)
		Local f:TStream=ReadFile(path)
		If Not f Then Return Null
		Local node:dictnode=parse(f,path)
		CloseFile f
		Return node
	End Function
	' Takes a stream and spits out the root dictnode.
	Function parse:dictnode(f:TStream,path$,immediatelydefined%=False)
		
		' Make a new dictnode object.
		Local n:dictnode=New dictnode
		Local char@,incomment%=0
		n.path=path
		
		' Look for the "#".
		If Not immediatelydefined Then
			Repeat
				char=ReadByte(f)
				If char=commentopen
					incomment:+1
				ElseIf char=commentclose
					incomment=Max(0,incomment-1)
				ElseIf incomment=0 And char=nodedefine
					Exit
				EndIf
				If Eof(f) Then 
					DebugLog " dictnode: Encountered unexpected end-of-file while looking for node definition."
					Return Null
				EndIf
			Forever
		EndIf
		n.nodestartpos=StreamPos(f)
		
		' Now look for the "[".
		Repeat
			char=ReadByte(f)
			If char=commentopen
				incomment:+1
			ElseIf char=commentclose
				incomment=Max(0,incomment-1)
			ElseIf incomment=0 
				If char=nodeopen Then
					Exit
				Else
					n.name:+Chr(char)
				EndIf
			EndIf
			If Eof(f) Then 
				DebugLog " dictnode: Encountered unexpected end-of-file while looking for node opening."
				Return Null
			EndIf
		Forever
		n.name=Trim(n.name)
		If Not n.name Then DebugLog " dictnode: Encountered a node without a name. That could get a mite confusing."
		n.nodebodypos=StreamPos(f)
		
		' Read the values and the children until "]".
		Local value$[2],valon%=0
		Local inquote%=0,hitquote%=-1
		Local hitnotwhitespace%=0
		Repeat
			char=ReadByte(f)
			If inquote
				If char=quote
					inquote=Not inquote
					hitquote=value[valon].length
				Else
					value[valon]:+Chr(char)
				EndIf
			ElseIf char=commentopen
				incomment:+1
			ElseIf char=commentclose
				incomment=Max(0,incomment-1)
			ElseIf char=quote
				inquote=Not inquote
				hitquote=value[valon].length
			ElseIf incomment=0 ' This is where the real magic happens.
				If char=nodedefine Then
					Local child:dictnode=parse(f,path,True)
					If child Then
						TreeInsert n.children,child.name,child
					Else
						DebugLog " dictnode: Encountered node definition but there was an error reading the child node."
						DebugLog " dictnode: Encoutered with node: "+n.tostring()
						Exit
					EndIf
				ElseIf char=assignment
					value[0]=TrimRightString(value[0],hitquote)
					valon=1
					hitnotwhitespace=0
					hitquote=-1
				ElseIf char=lineend
					If Lower(value[0])="include" Then
						Local cpath$=ExtractDir(path)+"/"+value[1]
						Local child:dictnode=parsefile(cpath)
						If child Then
							TreeInsert n.children,child.name,child
						Else
							DebugLog " dictnode: Failed to read child dictnode from included file ~q"+cpath+"~q."
							DebugLog " dictnode: Encoutered with node: "+n.tostring()
						EndIf
					Else
						If valon Then TreeInsert n.values,Trim(value[0]),TrimRightString(value[1],hitquote)
					EndIf
					value[0]=Null;value[1]=Null;valon=0
					hitnotwhitespace=0
				ElseIf char=nodeclose
					Exit
				Else
					If hitnotwhitespace Or hitquote>=0 Or (Not IsWhiteSpace(char)) Then
						hitnotwhitespace=1
						value[valon]:+Chr(char)
					EndIf
				EndIf
			EndIf
			If Eof(f) Then 
				DebugLog " dictnode: Encountered unexpected end-of-file while looking for node closing."
				DebugLog " dictnode: Encoutered with node: "+n.tostring()
				Exit
			EndIf
		Forever
		If valon Then TreeInsert n.values,Trim(value[0]),TrimRightString(value[1],hitquote)
		n.nodebodyend=StreamPos(f)
		
		' And now return the dictnode, of course.
		Return n
		
		' A really specific function that gets rid of trailing whitespace but also considering if and where there was a
		' final, closing quotation mark.
		Function TrimRightString$(str$,hitquote%)
			For Local i%=str.length-1 To 0 Step -1
				If i=hitquote-1 Or Not(IsWhitespace(str[i])) Then
					Return Left(str,i+1)
				EndIf
			Next
		End Function
	End Function
End Type

' This function just returns whether a given character is whitespace or not.
Private
Const whitespace_space%=Asc(" ")
Const whitespace_newl%=Asc("~n")
Const whitespace_return%=Asc("~r")
Const whitespace_tab%=Asc("	")
Function IsWhitespace%(char%)
	Return char=whitespace_space Or char=whitespace_newl Or char=whitespace_return Or char=whitespace_tab
End Function
