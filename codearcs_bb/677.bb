; ID: 677
; Author: ShadowTurtle
; Date: 2003-05-08 16:48:56
; Title: CopyDirectory([From], [To])
; Description: Thats a CopyDirectory Function ^^

Function CopyDirectory(From_$, To_$) 
  Local MyDir, MyFile$ 

  MyDir = ReadDir(From_$) 
  MyFile$ = NextFile$(MyDir) 
  MyFile$ = NextFile$(MyDir) 
  Repeat 
    MyFile$ = NextFile$(MyDir) 
    If MyFile$ = "" Then Exit 

    If FileType(From_$ + "\" + MyFile$) = 2 Then 
      CreateDir To_$ + "\" + MyFile$ 
      CopyDirectory(From_$ + "\" + MyFile$, To_$ + "\" + MyFile$) 
    Else 
      CopyFile From_$ + "\" + MyFile$, To_$ + "\" + MyFile$ 
    End If 
  Forever 
  CloseDir(MyDir)  
End Function
