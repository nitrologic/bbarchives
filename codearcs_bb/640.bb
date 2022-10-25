; ID: 640
; Author: ShadowTurtle
; Date: 2003-04-03 11:44:17
; Title: GadGetKey_TextArea
; Description: Scan key in a TextArea field and save this in a String.

wnd = CreateWindow("GadGet Key - Test", 10, 10, 400, 400, 0, 1) 
butn1 = CreateButton("Exit.", 2, 2, 100, 30, wnd) 
textlabel = CreateLabel("Please press a key in textarea.", 120, 2, 200, 20, wnd) 

textarea = CreateTextArea(2, 40, 388, 300, wnd) 

Programm = 1 
While Programm 
  MyEvent = WaitEvent() 
  If EventSource() = butn1 Then Programm = 0 

  If EventSource() = textarea Then 
    I$ = GadGetKey_TextArea$(textarea) 
    SetGadgetText textlabel, "You have lost pressed: " + I$ 
  End If 
Wend 


Type ScanGadGetKey 
  Field GadGetHandle 
  Field LostGadGetText$ 
  Field NewGadGetText$ 
  Field GadGetType 
End Type 

Function GadGetKey_TextArea$(objhandle) 
  Local SGGK.ScanGadGetKey 
  SGGK = Null 
  For ScanGadGetKey.ScanGadGetKey = Each ScanGadGetKey 
    If ScanGadGetKey\GadGetHandle = objhandle Then SGGK = ScanGadGetKey 
  Next 

  If SGGK = Null Then 
    SGGK.ScanGadGetKey = New ScanGadGetKey 
    SGGK\GadGetHandle = objhandle 
    SGGK\LostGadGetText$ = TextAreaText$(objhandle) 
    SGGK\GadGetType = 0 
    Return GadGetKey_TextArea$(objhandle) 
  Else 
    SGGK\LostGadGetText$ = SGGK\NewGadGetText$ 
    SGGK\NewGadGetText$ = TextAreaText$(SGGK\GadGetHandle) 
    For GGKT = 1 To Len(SGGK\NewGadGetText$) 
      If Not (Mid$(SGGK\NewGadGetText$, GGKT, 1) = Mid$(SGGK\LostGadGetText$, GGKT, 1)) Then 
        Return Mid$(SGGK\NewGadGetText$, GGKT, 1) 
      End If 
    Next 
  End If 
End Function
