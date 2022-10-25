; ID: 3218
; Author: Spencer
; Date: 2015-08-04 00:45:26
; Title: Compile Blitz3D with Blitz3D
; Description: Compile and run source and script files in Blitz3D using Blitzcc.exe

Graphics 640, 480, 0, 2
AppTitle "Blitz3D Game Launcher"

Const SCRIPT_FOLDER$ = "C:\MyGameFolder\scripts\"
Const SOURCE_FOLDER$ = "C:\MyGameFolder\source\"
Const OUTPUT_FILEPATH$ = "C:\MyGameFolder\output.bb"

Const BLITZ_PATH_VAR_NAME$ = "BLITZPATH"
Const BLITZ_PATH_VALUE$ = "C:\MyGameFolder\"
Const BLITZ_COMPILER_PATH$ = BLITZ_PATH_VALUE + "bin\compiler.exe"

Global BLITZ_CMD_TEMPLATE$ = Replace("'" + BLITZ_COMPILER_PATH + "' '{PATH}'","'",Chr(34))
Global NEW_LN$ = Chr(13) + Chr(10)


Function CompileAndRun(CodePath$, ScriptPath$, DestPath$)

    Local FullSource$ = ConcatSource(CodePath,ScriptPath)
    Local Command$ = Replace(BLITZ_CMD_TEMPLATE, "{PATH}", DestPath)
        
    WriteAllText(DestPath,FullSource)
    SetEnv(BLITZ_PATH_VAR_NAME, BLITZ_PATH_VALUE)
    Print GetEnv(BLITZ_PATH_VAR_NAME)
    Print Command
    Input "Press ENTER to run..."
    ExecFile(Command)
    End

End Function


Function ConcatSource$(CodePath$, ScriptPath$)

    Local SourceCodeBlob$ = ConcatFiles(CodePath)
    Local ScriptBlob$ = ConcatFiles(ScriptPath)
    Local FullSource$ = ""
    
    FullSource = SourceCodeBlob
    FullSource = FullSource + String(NEW_LN,2)
    FullSource = FullSource + ";" + String("*",80) + NEW_LN
    FullSource = FullSource + "; SCRIPT SOURCE BEGIN" + NEW_LN
    FullSource = FullSource + ";" + String("*",80) + NEW_LN
    FullSource = FullSource + String(NEW_LN,2)
    FullSource = FullSource + ScriptBlob
    
    Return FullSource

End Function


Function ConcatFiles$(Folder$)

    Local DirStream = ReadDir(Folder)
    Local FileName$ = ""
    Local TextBlob$ = ""
    Local FilePath$ = ""
 
    NextFile(DirStream) ; skip .
    NextFile(DirStream) ; skip ..

    Repeat
    
        FileName = NextFile(DirStream)
    
        If FileName = "" Then
    
            Exit
        Else
            FilePath = Folder + FileName    
            TextBlob = TextBlob + ReadAllText(FilePath)
    
        EndIf
    Forever

    CloseDir(DirStream)
    Return TextBlob

End Function


Function ReadAllText$(Path$)
    
    Local Stream = ReadFile(Path)
    Local Contents$ = ""
    
    While Not Eof(Stream)
        Contents = Contents + ReadLine(Stream) + NEW_LN
    Wend
    
    CloseFile(Stream)
    Return Contents
    
End Function


Function WriteAllText(Path$, Contents$)
    
    Local Stream = WriteFile(Path)
    
    WriteLine(Stream,Contents)
    CloseFile(Stream)
    
End Function



CompileAndRun(SOURCE_FOLDER,SCRIPT_FOLDER,OUTPUT_FILEPATH)
