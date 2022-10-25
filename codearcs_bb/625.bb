; ID: 625
; Author: assari
; Date: 2003-03-16 09:07:25
; Title: BlitzPlus File Explorer
; Description: B+ program to populate a treeview gadget with folders and selected files

; B+ program to populate a treeview gadget with folders and files
; Author: Assari
; Created: 16th March 2003
;

Global window=CreateWindow("B+ program to populate a treeview gadget with folders and files",0,0,800,600)
treeview=CreateTreeView( 0,0,ClientWidth(window),ClientHeight(window),window )
SetGadgetLayout treeview,1,2,2,2
root=TreeViewRoot( treeview )

folderlist$=RequestDir("Pls Select Folder")

If folderlist$>"" And Right$(folderlist$,1)="\" Then ;remove any \
   folderlist$=Left$(folderlist$,Len(folderlist$)-1)
EndIf

node=AddTreeViewNode(FolderList$,root)

TrawlFolder(FolderList$,"root",".JPG JPEG .BMP .PNG .ZIP",node)

Repeat
 WaitEvent()
 If EventID()=$803 Then End
Forever

End


Function TrawlFolder(CurrentPath$, CurrentParent$, Pattern$, ParentNode)

    If CurrentParent$="root" Then
       CurrentParent$=CurrentPath$
       xPath$=CurrentPath$
    Else
      xPath$=CurrentPath$+"\"+CurrentParent$
    EndIf

  hDir = ReadDir(xPath$):If hDir=0 Then Return ;no more folders
  
  Repeat

    File$=NextFile(hDir):If File$="" Then Return ;no more files 

    Select FileType(xPath$+"\"+file$)
      Case 2 ;folder
        If Not (file$="." Or Right$(file$,2)="..") Then
          node=AddTreeViewNode(">"+file$,ParentNode)
          TrawlFolder(xPath$,File$,Pattern$, Node)   ;recursion required here
        EndIf 
     Case 1 ;file
        ext$=Upper$(Right$(file$,4)) ;check for extension (last four characters)
        If Instr(pattern$,ext$) Then
           node=AddTreeViewNode(file$,ParentNode)
        EndIf
    End Select

    ;the next two lines are not required
    SetStatusText window, CurrentParent$+"\"+file$ ;just to let the world know we're working
    WaitEvent(1):If EventID()=$103 And EventData()=27 Then Return;Escape route

  Forever 

End Function
