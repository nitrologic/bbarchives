; ID: 691
; Author: ShadowTurtle
; Date: 2003-05-14 15:07:34
; Title: CreateSyntaxArea(...)
; Description: Syntax Highlighting in B+

wnd = CreateWindow("SyntaxArea Sample", 10, 10, 400, 400, 0, 1) 
butn1 = CreateButton("Exit.", 2, 2, 100, 30, wnd) 

S_Style = CreateSyntaxStyle() 
SyntaxAreaWordsFromFile S_Style, "syntax.txt", 255, 255, 0 

S_Area = CreateSyntaxArea(2, 40, 388, 300, wnd) 
SetSyntaxAreaStyle S_Area, S_Style 

Programm = 1 
While Programm 
  MyEvent = WaitEvent() 
  If EventSource() = butn1 Then Programm = 0 

  SyntaxAreaScanAll S_Area 
Wend 


Type SyntaxAreas 
  Field S_Handle 
  Field S_GadGet 
  Field S_Style.SyntaxStyles 
End Type 

Type SyntaxStyles 
  Field S_Handle 
  Field Color_BG[3] 
  Field Color_StdFont[3] 
  Field S_Char1$ 
  Field S_Char2$ 
  Field S_Char3$ 
  Field Font 
  Field FontName$ 
  Field FontSize 
  Field MaxWordLen 
End Type 

Type SyntaxWord 
  Field S_Style.SyntaxStyles 
  Field Word$ 
  Field Color_StdFont[3] 
End Type 

Function CreateSyntaxArea(x1,y1,x2,y2,g,s=0) 
  nextround=1 
  While nextround = 1 
    newhandle = Rnd(99999) : nextround=0 
    For SA.SyntaxAreas = Each SyntaxAreas 
      If SA\S_Handle = newhandle Then nextround=1 
    Next 
  Wend 

  SA.SyntaxAreas = New SyntaxAreas 
  SA\S_Handle = newhandle 
  SA\S_GadGet = CreateTextArea(x1, y1, x2, y2,g,s) 

  Return SA\S_Handle 
End Function 

Function SetSyntaxAreaStyle(H_Syntax, H_Style) 
  For SA.SyntaxAreas = Each SyntaxAreas 
    If SA\S_Handle = H_Syntax Then 
      For SS.SyntaxStyles = Each SyntaxStyles 
        If SS\S_Handle = H_Style Then 
          SA\S_Style = SS 
          SetTextAreaColor SA\S_GadGet, SS\Color_BG[1], SS\Color_BG[2], SS\Color_BG[3], 1 
          SetTextAreaColor SA\S_GadGet, SS\Color_StdFont[1], SS\Color_StdFont[2], SS\Color_StdFont[3] 
          SS\Font = LoadFont(SS\FontName$, SS\FontSize) 
          SetTextAreaFont SA\S_GadGet, SS\Font 
          FreeFont SS\Font 
        End If 
      Next 
    End If 
  Next 
End Function 

Function CreateSyntaxStyle() 
  .n_round : newhandle = Rnd(99999) : For SS.SyntaxStyles = Each SyntaxStyles 
  If SS\S_Handle = newhandle Then Goto n_round 
  Next  

  SS.SyntaxStyles = New SyntaxStyles 
  SS\S_Handle = newhandle 
  SS\Color_BG[1] = 50 : SS\Color_BG[2] = 50 : SS\Color_BG[3] = 255 
  SS\Color_StdFont[1] = 255 : SS\Color_StdFont[2] = 255 : SS\Color_StdFont[3] = 255 
  SS\FontName$ = "Blitz" 
  SS\FontSize = 20 
  SS\S_Char1$ = "1234567890" 
  SS\S_Char2$ = "´!"+Chr$(34)+"§&/()=?`<,.*'>;:!" 
  SS\S_Char3$ = "qwertzuiopüasdfghjklöäßyxcvbnmQWERTZUIOPÜASDFGHJKLÖÄYXCVBNM$%#" 

  Return SS\S_Handle 
End Function 

Function SyntaxAreaAddWord(H_Style, Word$, C_R, C_G, C_B) 
  For SS.SyntaxStyles = Each SyntaxStyles 
    If SS\S_Handle = H_Style Then 
      SW.SyntaxWord = New SyntaxWord 
      SW\S_Style = SS 
      SW\Word$ = Word$ 
      SW\Color_StdFont[1] = C_R : SW\Color_StdFont[2] = C_G : SW\Color_StdFont[3] = C_B 

      If SS\MaxWordLen < Word$ Then SS\MaxWordLen = Len(Word$) 
    End If 
  Next 
End Function 

Function SyntaxAreaWordsFromFile(H_Style, FileName$, C_R, C_G, C_B) 
  TheFile = ReadFile(FileName$) 
    While Not Eof(TheFile) 
      Word$ = ReadLine$(TheFile) 
      If Instr(Word$, " ") Then Word$ = Mid$(Word$, 1, Instr(Word$, " ")-1) 
      SyntaxAreaAddWord(H_Style, Word$, C_R, C_G, C_B) 
    Wend 
  CloseFile(TheFile) 
End Function 

Function SyntaxAreaScanAll(H_Syntax) 
  For SA.SyntaxAreas = Each SyntaxAreas 
    If SA\S_Handle = H_Syntax Then 
      SyntaxCur = TextAreaCursor(SA\S_GadGet) 
      SyntaxText$ = TextAreaText$(SA\S_GadGet) 

      CurA = SyntaxCur 
      Repeat 
        If (CurA < 1) Or (Not Instr(SA\S_Style\S_Char1$+SA\S_Style\S_Char3$, Mid$(SyntaxText$,CurA,1))>0) Then Exit 
        CurA = CurA - 1 
      Forever 

      mabs = 1 
      If Not Instr(SA\S_Style\S_Char1$+SA\S_Style\S_Char3$, Mid$(SyntaxText$,CurA,1))>0 Then CurA=CurA+1 : mabs = 0 

      CurB = CurA 
      Repeat 
        If (CurB > Len(SyntaxText$)) Or (Not Instr(SA\S_Style\S_Char1$+SA\S_Style\S_Char3$, Mid$(SyntaxText$,CurB,1))>0) Then Exit 
        CurB = CurB + 1 
      Forever 

      Word$ = Mid$(SyntaxText$, CurA+mabs, CurB-CurA-mabs) 

      FormatTextAreaText SA\S_GadGet, SA\S_Style\Color_StdFont[1], SA\S_Style\Color_StdFont[2], SA\S_Style\Color_StdFont[3], 0, CurA-mabs-1, CurB-CurA 
      fWord = 0 
      For SW.SyntaxWord = Each SyntaxWord 
        If SA\S_Style = SW\S_Style Then 
          If Lower(Word$) = Lower(SW\Word$) Then 
            FormatTextAreaText SA\S_GadGet, SW\Color_StdFont[1], SW\Color_StdFont[2], SW\Color_StdFont[3], 0, CurA-mabs-1, CurB-CurA 
            fWord = 1 
          End If 
        End If 
      Next 

      If fWord = 0 Then FormatTextAreaText SA\S_GadGet, SA\S_Style\Color_StdFont[1], SA\S_Style\Color_StdFont[2], SA\S_Style\Color_StdFont[3], 0, SyntaxCur-1,1 
    End If 
  Next 
End Function
