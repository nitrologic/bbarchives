; ID: 1989
; Author: Matt Merkulov
; Date: 2007-04-16 05:02:27
; Title: Tree-like structure
; Description: Displays changeable tree-like structure with elements as random colored boxes connected by lines

;Tree-like structure demo by Matt Merkulov

;Controls: arrows - move; Ins, PgUp, PgDown - create element; Del - remove

Type element
  ;User fields of element
 Field r,g,b
 ;auxillary element fields
 Field root.element,prev.element,nxt.element,sub.element
End Type

;root element
Global root.element=New element

Global ex,ey,sel.element

Graphics 800,600
branchcreate root,9
sel=root\sub
SetBuffer BackBuffer()
Repeat
 Cls
 ex=0:ey=0
 branchview root
 Flip
 Select WaitKey()
  Case 3;Ins
   sel=einsertin(Null,sel)
  Case 4;Del
   sel2.element=Null
   If sel\prev<>Null Then
    sel2=sel\prev
   ElseIf sel\nxt<>Null Then
    sel2=sel\nxt
   ElseIf sel\root<>root
    sel2=sel\root
   End If
   If sel2<>Null Then eremove sel:sel=sel2
  Case 5;Page Up
   sel=einsertbefore(Null,sel)
  Case 6;Page Down
   sel=einsertafter(Null,sel)
  Case 27;Esc
   Exit
  Case 28;up arrow
   If sel\prev<>Null Then sel=sel\prev
  Case 29;down arrow
   If sel\nxt<>Null Then sel=sel\nxt
  Case 30;right arrow
   If sel\sub<>Null Then sel=sel\sub
  Case 31;left arrow
   If sel\root<>root Then sel=sel\root
 End Select
Forever

;Function for creation of random branch with elements from certain element (k-maximum quantity)
Function branchcreate(e.element,k)
q=Rand(1,k)
;Create q elements
For n=1 To q
 e2.element=einsertin(Null,e)
 ;In 1/3 of cases create branch from current element, decreasing maximum of possible elements on 2
 If Rand(1,3)=1 Then branchcreate e2,k-2
Next
End Function

;Branch displaying (recursion is used)
Function branchview(e.element)
ex=ex+35
e=e\sub
ey1=ey-6
ey2=ey1
While e<>Null
 Line ex-20,ey+10,ex+15,ey+10
 ;Highlighting current element
 If e=sel Then c=127 Else c=0
 ;If element have no color set - set it randomly
 If e\r=0 Then
  e\r=Rand(1,128)
  e\g=Rand(1,128)
  e\b=Rand(1,128)
 End If
 Color c+e\r,c+e\g,c+e\b
 Rect ex,ey,30,20
 Color 255,255,255
 Rect ex,ey,30,20,False
 ey2=ey+10
 ey=ey+25
 branchview e
 e=e\nxt
Wend
Line ex-20,ey1,ex-20,ey2
ex=ex-35
End Function

;Inserton of element after certain
Function einsertafter.element(what.element,afterwhat.element)
;If element is not specified - creating new, else deleting it correctly from group
If what=Null Then what=New element Else epush what
;Connecting new element with previous and next in group
what\prev=afterwhat
what\nxt=afterwhat\nxt
If afterwhat\nxt<>Null Then afterwhat\nxt\prev=what
afterwhat\nxt=what
;Setting the root and returning pointer to the element
what\root=afterwhat\root
Return what
End Function

;Inserton of element before certain
Function einsertbefore.element(what.element,beforewhat.element)
If what=Null Then what=New element Else epush what
what\prev=beforewhat\prev
what\nxt=beforewhat
If beforewhat\prev<>Null Then beforewhat\prev\nxt=what
what\root=beforewhat\root
;If element is placed before first in group - connecting parent with it
If what\prev=Null Then what\root\sub=what
beforewhat\prev=what
Return what
End Function

;Inserton of element in group of certain
Function einsertin.element(what.element,inwhat.element)
If what=Null Then what=New element Else epush what
;Placing element in the beginning of the group
what\prev=Null
If inwhat\sub=Null Then
 what\nxt=Null
Else
 ;If the group is not empty - shifting first element down and connect it to new
 what\nxt=inwhat\sub
 inwhat\sub\prev=what
End If
;Connecting group parent with new element (now first in group)
inwhat\sub=what
what\root=inwhat
Return what
End Function

;Deleting element with all his branches
Function eremove(what.element,care=True)
;Deleting element correctly from group
If care Then epush what
e.element=what\sub
;If the element contains branches inside - deleting 'em using recursion
While e<>Null
 e2.element=e
 e=e\nxt
 ;Elements inside don't needs to be correctly removed from group
 eremove e2,False
Wend
Delete what
End Function

;Auxillary function - correct removing of element from group
Function epush(what.element)
;Connecting previous and nect elements with each other
;If removed element is situated on top of the group - connecting parent element with next
If what\prev<>Null Then what\prev\nxt=what\nxt Else what\root\sub=what\nxt
If what\nxt<>Null Then what\nxt\prev=what\prev
End Function
