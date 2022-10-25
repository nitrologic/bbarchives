; ID: 2611
; Author: Matt Merkulov
; Date: 2009-11-15 22:08:27
; Title: Sudoku solver
; Description: Automatic solution for Sudoku puzzles

SuperStrict
 
Type LTFinished
 Field N:Int, M:Int
End Type
 
Type LTBackup
 Field N:Int, M:Int
 Field Guess:Int
End Type
 
Type LTBackupChoice
 Field N:Int, M:Int
 Field Guesses:Int[]
 Field Choice:Int
End Type
 
Global GameField:Int[,] = New Int[ 9, 9 ]
Global Guess:Int[,,] = New Int[ 9, 9, 9 ]
Global FinishedQuantity:Int = 0
Global Faults:Int = 0
 
Global Font:TImageFont[] = New TImageFont[ 2 ]
Font[ 0 ] = LoadImageFont( "C:\Windows\Fonts\arial.ttf", 51 )
Font[ 1 ] = LoadImageFont( "C:\Windows\Fonts\arial.ttf", 17 )
 
Graphics 800, 600
 
Global FinishedStack:TList = New TList
Global UndoStack:TList
 
DrawText( "Do you want to use stored puzzle (y/n)?", 0, 0 )
Flip
Local UseStored:Int = 0
 
Repeat
 If KeyHit( KEY_N ) Then Exit
 If KeyHit( KEY_Y ) Then
  UseStored = 1
  Exit
 End If
Forever
 
For Local M:Int = 0 Until 9
 For Local N:Int = 0 Until 9
  Local V:Int = 0
  If UseStored Then ReadData V
  
  If V Then
   Guess[ N, M, V - 1 ] = 1
   GameField[ N, M ] = -1
   AddFinished( N, M )
  Else
   GameField[ N, M ] = -9
   For Local K:Int = 0 Until 9
    Guess[ N, M, K ] = 1
   Next
  End If
 Next
Next
 
DefData  0, 0, 0,   0, 8, 0,   0, 0, 0
DefData  9, 0, 0,   0, 6, 0,   0, 3, 1
DefData  0, 0, 0,   2, 0, 1,   0, 0, 7
 
DefData  0, 6, 0,   0, 0, 0,   0, 2, 0
DefData  7, 3, 0,   5, 0, 0,   9, 8, 0
DefData  1, 0, 0,   0, 0, 2,   0, 0, 0
 
DefData  0, 0, 8,   0, 0, 0,   0, 0, 0
DefData  0, 0, 0,   7, 2, 5,   0, 0, 0
DefData  0, 0, 0,   0, 0, 8,   3, 1, 0
 
Repeat
 Draw()
 
 Local Finished:LTFinished = LTFinished( FinishedStack.First() )
 If Finished Then
  DebugLog Finished.N + ", " + Finished.M
 
  If GameField[ Finished.N, Finished.M ] <> -1 Then
   debuglog GameField[ Finished.N, Finished.M ]
   Faults :+ 1
   RollBackup()
   Continue
  End If
 
  Local N:Int = Finished.N
  Local M:Int = Finished.M
  
  Local Variant:Int = -1
  For Local K:Int = 0 Until 9
   If Guess[ N, M, K ] Then
    Variant = K
    Exit
   End If
  Next
  
  Local QuadrantN:Int = Floor( N / 3 ) * 3
  Local QuadrantM:Int = Floor( M / 3 ) * 3
  For Local K:Int = 0 Until 9
   reemoveVariant( K, M, Variant )
   reemoveVariant( N, K, Variant )
   reemoveVariant( QuadrantN + ( K Mod 3 ), QuadrantM + Floor( K / 3 ), Variant )
  Next
  GameField[ N, M ] = Variant + 1
  'Waitkey
  FinishedQuantity :+ 1
  
  If FinishedQuantity = 81 Then
   Draw()
   Waitkey
   End
  End If
  
  FinishedStack.RemoveFirst()
 Else
  Local MinN:Int = 0
  Local MinM:Int = 0
  Local MinQ:Int = 9
 
  For Local N:Int = 0 Until 9
   For Local M:Int = 0 Until 9
    If GameField[ N, M ] < 0 Then
     Local Quantity:Int = 0
     For Local K:Int = 0 Until 9
      If Guess[ N, M, K ] Then Quantity :+ 1
     Next
     If Quantity < MinQ Then
      MinQ = Quantity
      MinN = N
      MinM = M
     End If
    End If
   Next
  Next
  
  If Not UndoStack Then UndoStack = New TList
  Local BackupChoice:LTBackupChoice = New LTBackupChoice
  BackupChoice.N = MinN
  BackupChoice.M = MinM
  BackupChoice.Guesses = New Int[ 9 ]
  UndoStack.AddFirst( BackupChoice )
  
  Local Variant:Int =  -1
  For Local K:Int = 0 Until 9
   BackupChoice.Guesses[ K ] = Guess[ MinN, MinM, K ] 
   If Guess[ MinN, MinM, K ] And Variant = -1 Then
    Variant = K
   Else
    Guess[ MinN, MinM, K ] = 0
   End If
  Next
  
  AddFinished( MinN, MinM )
  GameField[ MinN, MinM ] = -1
  BackupChoice.Choice = Variant
  
  Draw()
 End If
 
 Flip
 
Until KeyHit( KEY_ESCAPE )
 
 
 
Function CenterText( Text:String, X:Int, Y:Int, Font:TImageFont )
 SetImageFont( Font )
 DrawText( Text, X - TextWidth( Text ) / 2, Y - TextHeight( Text ) / 2 )
End Function
 
 
 
Function reemoveVariant( N:Int, M:Int, Variant:Int )
 If Guess[ N, M, Variant ] Then
  GameField[ N, M ] :+ 1
  Guess[ N, M, Variant ] = 0
  If GameField[ N, M ] = -1 Then AddFinished( N, M )
  
  If UndoStack Then
   Local Backup:LTBackup = New LTBackup
   Backup.N = N
   Backup.M = M
   Backup.Guess = Variant
   UndoStack.AddFirst( Backup )
  End If
 End If
End Function
 
 
 
Function RollBackup()
 If Not UndoStack Then RuntimeError( "This board has no solution!" )
 If UndoStack.Count() = 0 Then RuntimeError( "This board has no solution!" )
 Repeat
  Local Backup:LTBackup = LTBackup( UndoStack.First() )
  If Backup Then
   If GameField[ Backup.N, Backup.M ] > 0 Then
    FinishedQuantity :- 1
    GameField[ Backup.N, Backup.M ] = 0
   End If
   GameField[ Backup.N, Backup.M ] :- 1
   Guess[ Backup.N, Backup.M, Backup.Guess ] = 1
   UndoStack.RemoveFirst()
   Draw()
  Else
   Local BackupChoice:LTBackupChoice = LTBackupChoice( UndoStack.First() )
 
   Local N:Int = BackupChoice.N
   Local M:Int = BackupChoice.M
   Guess[ N, M, BackupChoice.Choice ] = 0
   
   For Local K:Int = BackupChoice.Choice + 1 Until 9
    If BackupChoice.Guesses[ K ] Then
     GameField[ N, M ] = -1
     Guess[ N, M, K ] = 1
     BackupChoice.Choice = K
     FinishedStack.Clear()
     AddFinished( N, M )
     draw()
     Return
    End If
   Next
   
   For Local K:Int = 0 Until 9
    Guess[ BackupChoice.N, BackupChoice.M, K ] = BackupChoice.Guesses[ K ]
   Next
  
   UndoStack.RemoveFirst()
   RollBackup()
  End If
 Forever
End Function
 
 
 
Function AddFinished( N:Int, M:Int )
 Local Finished:LTFinished = New LTFinished
 Finished.N = N
 Finished.M = M
 FinishedStack.AddLast( Finished )
End Function
 
 
 
Function Draw()
 Cls
 
 For Local N:Int = 0 To 9
  Local V:Int = 0
  If ( N Mod 3 ) = 0 Then V = 1
  DrawRect 32 + N * 60 - V * 2, 30, 1 + V * 4, 544
  DrawRect 30, 32 + N * 60 - V * 2, 544, 1 + V * 4
 Next
 
 For Local N:Int = 0 Until 9
  For Local M:Int = 0 Until 9
   If GameField[ M, N ] > 0 Then
    CenterText( GameField[ M, N ], 62 + M * 60, 62 + N * 60, Font[ 0 ] )
   Else
    For Local K:Int = 0 Until 9
     If Guess[ M, N, K ] Then CenterText( K + 1, 42 + M * 60 + ( K Mod 3 ) * 20, 42 + N * 60 + Floor( K / 3 ) * 20, Font[ 1 ] )
    Next
   End If
  Next
 Next
 
 SetImageFont( Font[ 0 ] )
 DrawText( FinishedQuantity, 800 - TextWidth( FinishedQuantity ), 0 )
 SetColor 255, 0, 0
 If Faults Then DrawText( Faults, 800 - TextWidth( Faults ), 50 )
 SetColor 255, 255, 255
 
 Flip
End Function
