; ID: 361
; Author: GrahamK
; Date: 2002-07-05 08:57:18
; Title: XML code
; Description: XML loading/parsing/saving

; XML load / parse / save functions

Type sdXMLnodelist
	Field node.sdxmlnode
	Field nextnode.sdxmlnodelist
	Field prevnode.sdxmlnodelist
End Type

; for internal use, do not use in code outside of this file
Type sdXMLworklist
	Field node.sdxmlnode
End Type


Type sdXMLnode
	Field tag$,value$,path$
	Field firstattr.sdXMLattr
	Field lastattr.sdXMLattr	
	Field attrcount,fileid
	Field endtag$
	
	; linkage functionality
	Field firstchild.sdXMLnode
	Field lastchild.sdXMLnode
	Field childcount
	Field nextnode.sdXMLnode
	Field prevnode.sdXMLnode
	Field parent.sdXMLnode
End Type

Type sdXMLattr
	Field name$,value$
	Field sibattr.sdXMLattr
	Field parent.sdxmlnode
End Type

Global SDXMLFILEID

Function sdReadXML.sdXMLnode(filename$)
	infile = ReadFile(filename$)
	SDXMLFILEID=MilliSecs()
	x.sdxmlnode = sdXMLReadNode(infile,Null)
	CloseFile infile
	Return x
End Function

Function sdWriteXML(filename$,node.sdxmlnode,writeroot=False)
	outfile = WriteFile(filename$)
	WriteLine outfile,"<?xml version="+Chr$(34)+"1.0"+Chr$(34)+" ?>"
	sdXMLwriteNode(outfile,node)
	CloseFile outfile
End Function



Function sdXMLOpenNode.sdxmlnode(parent.sdxmlnode,tag$="")
	;gak debuglog "Opening new node"
	x.sdxmlnode = New sdxmlnode
	x\tag$=tag$
	x\fileid = SDXMLFILEID; global indicator to group type entries (allows multiple XML files to be used)
	sdXMLaddNode(parent,x)
	Return x
End Function

Function sdXMLCloseNode.sdxmlnode(node.sdxmlnode)
	;gak debuglog "Closing node ["+node\tag$+"]"
	If node\parent <> Null Then
		;gak debuglog "Returning to parent ["+node\parent\tag$+"]"
	Else
		;gak debuglog "No Parent found"
	End If
	Return node\parent
End Function

; adds node to end of list (need separate function for insert, or mod this on)
Function sdXMLAddNode(parent.sdxmlnode,node.sdxmlnode)
	If parent <> Null
		;gak debuglog "Parent of node = ["+parent\tag$+"]"
		If parent\childcount = 0 Then
			parent\firstchild = node
		Else
			parent\lastchild\nextnode = node
		End If
		node\prevnode = parent\lastchild
		parent\lastchild = node
		parent\childcount = parent\childcount +1
		node\path$ = parent\path$+parent\tag$
	End If
	node\parent = parent
	node\path$=node\path$+"/"
	;gak debuglog "path to ["+node\tag$+"]={"+node\path$+"}"
End Function


Function sdXMLDeleteNode(node.sdxmlnode)
	n.sdxmlnode = node\firstchild
	; delete any children recursively
	While n <> Null
		nn.sdxmlnode= n\nextnode
		sdXMLdeletenode(n)
		n = nn
	Wend

	; delete attributes for this node
	a.sdxmlattr = node\firstattr
	While a <> Null
		na.sdxmlattr = a\sibattr
		Delete a
		a = na
	Wend

	; dec parents child count
	If node\parent <> Null
		node\parent\childcount = node\parent\childcount -1
		
		; heal linkages
		If node\prevnode <> Null Then node\prevnode\nextnode = node\nextnode
		If node\nextnode <> Null Then node\nextnode\prevnode = node\prevnode
		If node\parent\firstchild = node Then node\parent\firstchild = node\nextnode
		If node\parent\lastchild = node Then node\parent\lastchild = node\prevnode
	End If
	; delete this node		
;	;gak debuglog "DELETING:"+node\tag$
	Delete node

End Function


; node functions

Function sdXMLfindNode.sdXMLnode(node.sdxmlnode,path$)
	;gak debuglog "------------- Perfoming Find ("+path$+")------------"

	ret.sdXMLnode = Null
	p=Instr(path$,"/")
	If p > 0 Then 
		tag$=Left$(path$,p-1)
		;gak debuglog "Looking for ["+tag$+"]"
		a.sdxmlnode = node
		While ret=Null And a<>Null 
			;gak debuglog "Checking...["+a\tag$+"]"
			If Lower(tag$)=Lower(a\tag$) Then
				If p=Len(path$) Then
						;gak debuglog "Found..."
						ret = a
				Else
					If a\firstchild <> Null Then
						ret = sdxmlfindnode(a\firstchild,Mid$(path$,p+1))
					End If
				End If
			End If
			a = a\nextnode
		Wend
	End If
	Return ret
End Function

Function sdXMLDeleteList(nl.sdxmlnodelist)
	While nl <> Null
		na.sdxmlnodelist = nl\nextnode
		Delete nl
		nl = na
	Wend
End Function


Function sdXMLSelectNodes.sdxmlnodelist(node.sdxmlnode,path$,recurse=True)
	root.sdxmlnodelist=Null
	sdxmlselectnodesi(node,path$,recurse)
	prev.sdxmlnodelist=Null
	c = 0
	For wl.sdxmlworklist = Each sdxmlworklist
		c = c + 1
		nl.sdxmlnodelist = New sdxmlnodelist
		nl\node = wl\node
		If prev = Null Then 
			root = nl
			prev = nl
		Else
			prev\nextnode = nl
			nl\prevnode = prev
		End If
		prev = nl
		Delete wl
	Next
	;gak debuglog "XML: "+c+" nodes selected"
	Return root
End Function

; internal selection function, do not use outside this file
Function sdXMLSelectNodesI(node.sdxmlnode,path$,recurse=True)
	wl.sdXMLworklist=Null
	;gak debuglog "------------- Perfoming Select ("+path$+")------------"
	If node = Null Then
		 ;gak debuglog "Search node is null!!!"
	End If
	ret.sdXMLnode = Null
	p=Instr(path$,"/")
	If p > 0 Then 
		tag$=Left$(path$,p-1)
		a.sdxmlnode = node
		While a<>Null 
			;gak debuglog "Looking for {"+path$+"} in {"+a\path$+a\tag$+"/}  {"+Lower(Right$(a\path$+a\tag$+"/",Len(path$)))+"} @"
			If Lower(path$)=Lower(Right$(a\path$+a\tag$+"/",Len(path$))) Then
					wl = New sdXMLworklist
					wl\node = a
					;gak debuglog ">>FOUND"
			End If
			If a\firstchild <> Null And (recurse) Then
				sdXMLSelectNodesI(a\firstchild,path$)
			End If
			a = a\nextnode
		Wend
	End If

End Function

Function sdXMLNextNode.sdXMLnode(node.sdXMLnode)
	Return node\nextnode
End Function

Function sdXMLPrevNode.sdXMLnode(node.sdXMLnode)
	Return node\prevnode
End Function

Function sdXMLAddAttr(node.sdxmlnode,name$,value$)
	;gak debuglog "XML:adding attribute "+name$+"="+value$+" ("+Len(value$)+")"
	a.sdxmlattr = New sdxmlattr
	a\name$ = name$
	a\value$ = value$
	If node\attrcount = 0 Then
		node\firstattr = a
	Else
		node\lastattr\sibattr = a
	End If
	node\lastattr=a
	node\attrcount = node\attrcount + 1
	a\parent = node
End Function


Function sdXMLReadNode.sdxmlnode(infile,parent.sdXMLnode,pushed=False)
	mode = 0
	root.sdxmlnode = Null
	cnode.sdxmlnode = Null
	x.sdXMLnode = Null
	ispushed = False
	done = False
	While (Not done) And (Not Eof(infile))
		c = ReadByte(infile)
		If c<32 Then c=32
		ch$=Chr$(c)
;		;gak debuglog "{"+ch$+"} "+c+" mode="+mode
		Select mode
		  Case 0 ; looking for the start of a tag, ignore everything else
			If ch$ = "<" Then 
				mode = 1; start collecting the tag
			End If
		  Case 1 ; check first byte of tag, ? special tag
		    If ch$ = "?" Or ch$ = "!" Then
		 		mode = 0; class special nodes as garbage & consume
			Else
				If ch$ = "/" Then 
					mode = 2 ; move to collecting end tag
					x\endtag$=ch$
					;gak debuglog "** found end tag"
				Else
					cnode=x
					x.sdXMLnode = sdXMLOpennode(cnode)
					If cnode=Null Then root=x
					x\tag$=ch$
					mode = 3 ; move to collecting start tag
				End If
			End If
		  Case 2 ; collect the tag name (close tag)
			If ch$=">" Then 
				mode = 0 ; end of the close tag so jump out of loop
				;done = True
				x = sdXMLclosenode(x)
			Else 
				x\endtag$ = x\endtag$ + ch$
			End If
		  Case 3 ; collect the tag name 
			If ch$=" " Then 
				;gak debuglog "TAG:"+x\tag$
				mode = 4 ; tag name collected, move to collecting attributes
			Else 
				If ch$="/" Then 
					;gak debuglog "TAG:"+x\tag$
					x\endtag$=x\tag$
					mode = 2; start/end tag combined, move to close
				Else
					If ch$=">" Then
						;gak debuglog "TAG:"+x\tag$
						mode = 20; tag closed, move to collecting value
					Else
						x\tag$ = x\tag$ + ch$
					End If
				End If
			End If
		  Case 4 ; start to collect attributes
		    If Lower(ch$)>="a" And Lower(ch$)<="z" Then 
				aname$=ch$;
			    mode = 5; move to collect attribute name
			Else
				If ch$=">" Then
					x\value$=""
					mode = 20; tag closed, move to collecting value
				Else
					If ch$="/" Then 
						mode = 2 ; move to collecting end tag
						x\endtag$=ch$
						;gak debuglog "** found end tag"
					End If
				End If
			End If
		  Case 5 ; collect attribute name
		    If ch$="=" Then
			  ;gak debuglog "ATT:"+aname$
			  aval$=""
			  mode = 6; move to collect attribute value
			Else
			  aname$=aname$+ch$
			End If
		  Case 6 ; collect attribute value
		    If c=34 Then
				mode = 7; move to collect string value
			Else
				If c <= 32 Then 
					;gak debuglog "ATV:"+aname$+"="+aval$
					sdXMLAddAttr(x,aname$,aval$)
					mode = 4; start collecting a new attribute
				Else
			   		aval$=aval$+ch$
				End If
			End If
		  Case 7 ; collect string value
			If c=34 Then
				;gak debuglog "ATV:"+aname$+"="+aval$
				sdxmlADDattr(x,aname$,aval$)
				mode = 4; go and collect next attribute
			Else
				aval$=aval$+ch$
			End If
		  Case 20 ; COLLECT THE VALUE PORTION
			If ch$="<" Then 
				;gak debuglog "VAL:"+x\tag$+"="+x\value$
				mode=1; go to tag checking
			Else
				x\value$=x\value$+ch$
			End If
		End Select
		
		If Eof(infile) Then done=True
	
	Wend

	Return root

End Function

; write out an XML node (and children)
Function sdXMLWriteNode(outfile,node.sdxmlnode,tab$="")
;	;gak debuglog "Writing...."+node\tag$+".."
	s$="<"+node\tag$
	a.sdxmlattr = node\firstattr
	While a<>Null
;		;gak debuglog "Writing attr ["+a\name$+"]=["+a\value$+"]"
		s$ = s$+" "+Lower(a\name$)+"="+Chr$(34)+a\value$+Chr$(34)
		a = a\sibattr
	Wend
	
	If node\value$="" And node\childcount = 0 Then
		s$=s$+"/>"
		et$=""
	Else
		s$=s$+">"+node\value$
		et$="</"+node\tag$+">"
	End If
	
	WriteLine outfile,sdXMLcleanStr$(tab$+s$)
	n.sdxmlnode = node\firstchild
	While n <> Null
		sdXMLwriteNode(outfile,n,tab$+"  ")
		n = n\nextnode
	Wend
	
	If et$<> "" Then WriteLine outfile,sdXMLcleanStr$(tab$+et$)

End Function




; remove non-visible chars from the output stream
Function sdXMLCleanStr$(s$)
	a$=""
	For i = 1 To Len(s$)
		If Asc(Mid$(s$,i,1))>=32 Then a$ = a$ +Mid$(s$,i,1)
	Next
	Return a$

End Function

; attribute functions
; return an attribute of a given name
Function sdXMLFindAttr.sdXMLattr(node.sdxmlnode,name$)
	ret.sdXMLattr = Null
	If node <> Null Then 
		a.sdxmlattr = node\firstattr
		done = False
		While ret=Null And a<>Null 
			If Lower(name$)=Lower(a\name$) Then
				ret = a
			End If
			a = a\sibattr
		Wend
	End If
	Return ret
End Function

; return an attribute value as a string
Function sdXMLAttrValueStr$(node.sdxmlnode,name$,dflt$="")
	ret$=dflt$
	a.sdxmlattr = sdXMLfindattr(node,name$)
	If a <> Null Then ret$=a\value$
	Return ret$
End Function

; return an attribute value as an integer
Function sdXMLAttrValueInt(node.sdxmlnode,name$,dflt=0)
	ret=dflt
	a.sdxmlattr = sdXMLfindattr(node,name$)
	If a <> Null Then ret=a\value
	Return ret
End Function

; return an attribute value as a float
Function sdXMLAttrValueFloat#(node.sdxmlnode,name$,dflt#=0)
	ret#=dflt#
	a.sdxmlattr = sdXMLfindattr(node,name$)
	If a <> Null Then ret#=a\value
	Return ret
End Function

;x.sdxmlnode = sdReadXML("test.xml")
;sdwritexml("test2.xml",x)

;f.sdxmlnode = sdxmlfindnode(x,"BB3D/NODE/MESH/")
;If f <> Null Then
;	;gak debuglog "FOUND!!!"
;	sdxmldeletenode(f)	
;End If

;sdwritexml("test3.xml",x)

;nl.sdxmlnodelist = sdxmlselectnodes(x,"/VERTEX/POS/")
;While nl <> Null;
;	;gak debuglog "Found....."+nl\node\tag$
;	nl=nl\nextnode
;Wend
;sdxmldeleteList(nl);



;sdxmldeletenode(x)
