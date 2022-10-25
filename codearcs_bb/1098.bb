; ID: 1098
; Author: Vertex
; Date: 2004-06-27 10:15:46
; Title: BlitzHacker
; Description: As your programs are safe?

; --------------------------------------------------------------------------------------------
Include "../blitzui.bb"
Include "../extras/messagebox.bb"
Include "../extras/opensavedialog.bb"

Graphics 430, 433, 32, 2
SetBuffer BackBuffer()
AppTitle "BlitzHacker"
Initialise()
; --------------------------------------------------------------------------------------------

; --------------------------------------------------------------------------------------------
Type functions_t
   Field offset
   Field name$
End Type

Type types_t
   Field offset
   Field name$
End Type

Type globals_t
   Field offset
   Field name$
End Type

Type arrays_t
   Field offset
   Field name$
End Type

Type labels_t
   Field offset
   Field name$
End Type

Const sizeBlitz3D   = 1048576 ; for Blitz3D-Executables only
Const sizeBlitzPlus = 900000  ; for Blitz3D- and BlitzPlus-Executables, but slower for Blitz3D

Global functions.functions_t, types.types_t, globals.globals_t, arrays.arrays_t, labels.labels_t
Global functionCount, typeCount, globalCount, arrayCount, labelCount, mainOffset = 0
Global fileName$
; --------------------------------------------------------------------------------------------

; --------------------------------------------------------------------------------------------
Global winMain      = Window(-1, -1, 430, 433, "BlitzHacker", "0", 0, 0, 0, 0)

Global cmdLoad      = Button(5, 5, 205, 20, "Load...", "0", 1, 0, 0)
Global cmdSave      = Button(215, 5, 205, 20, "Save...", "0", 1, 0, 0)

Global lblFunctions = Label(5, 30, "Functions:", 0)
Global lblTypes     = Label(5, 110, "Types:", 0)
Global lblGlobals   = Label(5, 190, "Globals:", 0)
Global lblArrays    = Label(5, 270, "Arrays:", 0)
Global lblLabels    = Label(5, 350, "Labels:", 0)

Global lstFunctions = ListBox(5, 45, 415, 60, 20, 20, 10, 0)
Global lstTypes     = ListBox(5, 125, 415, 60, 20, 20, 10, 0)
Global lstGlobals   = ListBox(5, 205, 415, 60, 20, 20, 10, 0)
Global lstArrays    = ListBox(5, 285, 415, 60, 20, 20, 10, 0)
Global lstLabels    = ListBox(5, 365, 415, 60, 20, 20, 10, 0)

SendMessage(cmdSave, "BM_DISABLE")
; --------------------------------------------------------------------------------------------

; --------------------------------------------------------------------------------------------
Repeat
   UpdateGUI()
   
   Select app\Event
      Case EVENT_WINDOW
         Select app\WindowEvent
         End Select
      Case EVENT_MENU
         Select app\MenuEvent
         End Select
      Case EVENT_GADGET
         Select app\GadgetEvent
            Case cmdLoad
               result$ = FileDialog("Open File", "", "exe", False)
               UpdateGUI() : Flip
               hackExe(result$)
            Case cmdSave
               result$ = FileDialog("Save Log", "", "txt", True)
               If result$<>"" And result$<>"Cancel" Then
                  stream = WriteFile(result$)
                  If stream = False Then
                     result$ = MessageBox$("Cannot save file", "Error", 1)
                     UpdateGUI() : Flip
                  Else
                     AppTitle "BlitzHacker :: "+getFileName$(fileName$)+" | in progress..."
                     SendMessage(cmdSave, "BM_DISABLE")
                     SendMessage(cmdLoad, "BM_DISABLE")
                     UpdateGUI() : Flip
                     
                     WriteLine stream, "BlitzHacker :: "+fileName
                     WriteLine stream, "Date:       "+CurrentDate$()
                     WriteLine stream, "Time:       "+CurrentTime$()
                     WriteLine stream, "Mainoffset: "+Hex$(mainOffset)
                     WriteLine stream, ""
                  
                     WriteLine stream, "Functions("+functionCount+"):"
                     For functions = Each functions_t
                        WriteLine stream, Hex$(functions\offset)+" :: "+functions\name$
                     Next : WriteLine stream, ""

                     WriteLine stream, "Types("+typeCount+"):"
                     For types = Each types_t
                        WriteLine stream, Hex$(types\offset)+" :: "+types\name$
                     Next : WriteLine stream, ""

                     WriteLine stream, "Globals("+globalCount+"):"
                     For globals = Each globals_t
                        WriteLine stream, Hex$(globals\offset)+" :: "+globals\name$
                     Next : WriteLine stream, ""
      
                     WriteLine stream, "Arrays("+arrayCount+"):"
                     For arrays = Each arrays_t
                        WriteLine stream, Hex$(arrays\offset)+" :: "+arrays\name$
                     Next : WriteLine stream, ""
      
                     WriteLine stream, "Labels("+labelCount+"):"
                     For labels = Each labels_t
                        WriteLine stream, Hex$(labels\offset)+" :: "+labels\name$
                     Next
      
                     CloseFile stream
                     
                     SendMessage(cmdLoad, "BM_ENABLE")
                     SendMessage(cmdSave, "BM_ENABLE")
                     UpdateGUI() : Flip
                     AppTitle "BlitzHacker :: "+getFileName$(fileName$)+" | ready"
                  EndIf
               EndIf   
         End Select
   End Select
   
   ResetEvents()

   Flip
Until KeyHit( 1 ) Or app\Quit = True

Destroy() : End
; --------------------------------------------------------------------------------------------

; --------------------------------------------------------------------------------------------
Function hackExe(file$)
   Local stream, result$, i, buffer$, byte, offset, name$, del
   
   ; find "__MAIN"
   stream = ReadFile(file$)
   If stream = False Then
      result$ = MessageBox$("Cannot open file", "Error", 1)
      UpdateGUI() : Flip
      Return
   EndIf
   SeekFile stream, sizeBlitzPlus
   While Not Eof(stream)
      If ReadByte(stream) = Asc("_") Then
         For i=1 To 5
            buffer$ = buffer$ + Chr$(ReadByte(stream))
         Next
         If buffer$ = "_MAIN" Then
            mainOffset = FilePos(stream)-5
            Exit
         Else
            buffer$ = ""
         EndIf
      EndIf
   Wend
   If mainOffset > 0 Then
      result$ = MessageBox$("Mainoffset at "+Hex$(mainOffset)+", please wait...", "Found Mainoffset", 1)
      Delete Each functions_t : SendMessage(lstFunctions, "LM_RESET")
      Delete Each types_t     : SendMessage(lstTypes, "LM_RESET")
      Delete Each globals_t   : SendMessage(lstGlobals, "LM_RESET")
      Delete Each arrays_t    : SendMessage(lstArrays, "LM_RESET")
      Delete Each labels_t    : SendMessage(lstLabels, "LM_RESET")
      SendMessage(cmdLoad, "BM_DISABLE")
      SendMessage(cmdSave, "BM_DISABLE")
      UpdateGUI() : Flip
      AppTitle "BlitzHacker :: "+getFileName$(file$)+" | in progress..."
      fileName$ = file$
   Else
      result$ = MessageBox$("The file is not a Blitzexecutable!", "Error", 1)
      CloseFile stream
      Return
   EndIf
   
   ; find functions
   functionCount = 0 : buffer$ = ".."
   While Not Eof(stream)
      buffer$ = Right$(buffer$, 1)+Chr$(ReadByte(stream))
      If buffer$ = "_f" Then
         offset = FilePos(stream)-1
         name$  = ""
         While True
            byte = ReadByte(stream)
            If byte = 0 Then
               Exit
            ElseIf byte>47 And byte<123
               name$ = name$+Chr$(byte)
            Else
               name$ = ""
               Exit
            EndIf
         Wend
         
         If name$ = Lower$(name$) And name$<>"" Then
            del = False
            For functions = Each functions_t
               If functions\name$ = name$ Then del = True
            Next
            If del = False Then
               functions = Last functions_t
               functions = New functions_t
               functions\offset = offset
               functions\name$  = name$
               AddListBoxItem(lstFunctions, 0, name$)
               functionCount = functionCount+1
            EndIf
         EndIf
      EndIf
   Wend : UpdateGUI() : Flip
   
   ; find types
   typeCount = 0 : SeekFile stream, mainOffset+5 : buffer$ = ".."
   While Not Eof(stream)
      buffer$ = Right$(buffer$, 1)+Chr$(ReadByte(stream))
      If buffer$ = "_t" Then
         offset = FilePos(stream)-1
         name$  = ""
         While True
            byte = ReadByte(stream)
            If byte = 0 Then
               Exit
            ElseIf byte>47 And byte<123
               name$ = name$+Chr$(byte)
            Else
               name$ = ""
               Exit
            EndIf
         Wend
         
         If name$ = Lower$(name$) And name$<>"" Then
            del = False
            For types = Each types_t
               If types\name$ = name$ Then del = True
            Next
            If del = False Then
               types = Last types_t
               types = New types_t
               types\offset = offset
               types\name$  = name$
               AddListBoxItem(lstTypes, 0, name$)
               typeCount = typeCount+1
            EndIf
         EndIf
      EndIf
   Wend : UpdateGUI() : Flip
   
   ; find globals
   globalCount = 0 : SeekFile stream, mainOffset+5 : buffer$ = ".."
   While Not Eof(stream)
      buffer$ = Right$(buffer$, 1)+Chr$(ReadByte(stream))
      If buffer$ = "_v" Then
         offset = FilePos(stream)-1
         name$  = ""
         While True
            byte = ReadByte(stream)
            If byte = 0 Then
               Exit
            ElseIf byte>47 And byte<123
               name$ = name$+Chr$(byte)
            Else
               name$ = ""
               Exit
            EndIf
         Wend
         
         If name$ = Lower$(name$) And name$<>"" Then
            del = False
            For globals = Each globals_t
               If globals\name$ = name$ Then del = True
            Next
            If del = False Then
               globals = Last globals_t
               globals = New globals_t
               globals\offset = offset
               globals\name$  = name$
               AddListBoxItem(lstGlobals, 0, name$)
               globalCount = globalCount+1
            EndIf
         EndIf
      EndIf
   Wend : UpdateGUI() : Flip
   
   ; find arrays
   arrayCount = 0 : SeekFile stream, mainOffset+5 : buffer$ = ".."
   While Not Eof(stream)
      buffer$ = Right$(buffer$, 1)+Chr$(ReadByte(stream))
      If buffer$ = "_a" Then
         offset = FilePos(stream)-1
         name$  = ""
         While True
            byte = ReadByte(stream)
            If byte = 0 Then
               Exit
            ElseIf byte>47 And byte<123
               name$ = name$+Chr$(byte)
            Else
               name$ = ""
               Exit
            EndIf
         Wend
         
         If name$ = Lower$(name$) And name$<>"" Then
            del = False
            For arrays = Each arrays_t
               If arrays\name$ = name$ Then del = True
            Next
            If del = False Then
               arrays = Last arrays_t
               arrays = New arrays_t
               arrays\offset = offset
               arrays\name$  = name$
               AddListBoxItem(lstArrays, 0, name$)
               arrayCount = arrayCount+1
            EndIf
         EndIf
      EndIf
   Wend : UpdateGUI() : Flip
   
   ; find labels
   labelCount = 0 : SeekFile stream, mainOffset+5 : buffer$ = "...."
   While Not Eof(stream)
      buffer$ = Mid$(buffer$, 2, 3)+Chr$(ReadByte(stream))

      If buffer$ = "_l_2" Then
         offset = FilePos(stream)-1
         name$  = ""
         While True
            byte = ReadByte(stream)
            If byte = 0 Then
               Exit
            ElseIf byte>47 And byte<123
               name$ = name$+Chr$(byte)
            Else
               name$ = ""
               Exit
            EndIf
         Wend

         If name$ = Lower$(name$) And name$<>"" Then
            del = False
            For labels = Each labels_t
               If labels\name$ = name$ Then del = True
            Next
            If del = False Then
               labels = Last labels_t
               labels = New labels_t
               labels\offset = offset
               labels\name$  = name$
               AddListBoxItem(lstLabels, 0, name$)
               labelCount = labelCount+1
            EndIf
         EndIf
      EndIf
   Wend
   
   CloseFile stream
   SendMessage(cmdLoad, "BM_ENABLE")
   SendMessage(cmdSave, "BM_ENABLE")
   AppTitle "BlitzHacker :: "+getFileName$(file$)+" | ready"
End Function

Function getFileName$(path$)
   Local length, i, find
   
   length = Len(path$)
   For i=1 To length
      If Mid$(path$, i, 1) = "\" Then
         find = i
      EndIf
   Next
   
   Return Right$(path$, length-find)
End Function
; --------------------------------------------------------------------------------------------
