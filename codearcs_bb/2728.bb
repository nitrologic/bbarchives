; ID: 2728
; Author: Spencer
; Date: 2010-06-11 00:19:17
; Title: Nonsense VM
; Description: Nonsense VM (Now with more nonsense!)

Const cs = 16383
 

;********************************************************************************************************
;Program "Class"
Type cProgram
    Field ClassName$
    Field c$[cs+1]
    Field LastPosition
End Type
;--------------------------------------------------------------
Function cProgram_Load.cProgram(ClassName$, FilePath$)
    Local n.cProgram = New cProgram
    Local FileStream=ReadFile(FilePath)
    Local p = 0
    Local VarRef$ = ""
    Local Pointer$ = ""
    n\ClassName= Lower(ClassName)
    While Not Eof(FileStream) ;read in code
        n\c[p]=Trim(ReadLine(FileStream))
        p=p+(n\c[p]<>"" And Left(n\c[p],2)<>"//")
    Wend
    CloseFile(FileStream)
    n\LastPosition=p+1
    p=0
    While p < n\LastPosition ;replace var declarations and ref points with position values
        VarRef=Left(Lower(n\c[p]),4)
        If VarRef="%var" Or VarRef="%ref" Then
            Pointer=Lower(Trim(Mid(n\c[p],5)))
            n\c[p]=p
            For tp=0 To n\LastPosition
                If Lower(n\c[tp])=Pointer Then
                    n\c[tp]="@"+p
                EndIf
            Next
        EndIf
        p=p+1
    Wend
    Return n
End Function
;-------------------------------------------------------------- 
Function cProgram_PrintTokens(Name$)
    Local cp.cProgram = cProgram_Get(Name)
    For x=0 To cp\LastPosition
        Print RSet(x,3) + ":" + cp\c[x]
    Next
    Input("Press Enter To Continue")
End Function
;-------------------------------------------------------------- 
Function cProgram_Get.cProgram(ClassName$)
    ClassName = Lower(ClassName)
    For class.cProgram = Each cProgram
        If class\ClassName = ClassName Then
            Return class
        EndIf
    Next
    Input("Class Program '" + ClassName + "' was not loaded or has been collected from memory")
    End
End Function
;End Program "Class"
;********************************************************************************************************


 
;********************************************************************************************************
;Program Instance "Object"
Const STATUS_STOPPED = 0
Const STATUS_RUNNING = 1
Const STATUS_PAUSED  = 2
Const THREADED_ON    = 0
Const THREADED_OFF   = 1
 
Global gCurrentProgram.objProgram
Global gThreadedStatus = THREADED_ON
;-------------------------------------------------------------- 
Type objProgram
    Field Name$
    Field c$[cs+1]
    Field p
	Field LastPosition
    Field Status
End Type
;--------------------------------------------------------------  
Function objProgram_New.objProgram(ClassName$,Name$)
    Local class.cProgram = cProgram_Get(ClassName)
    Local obj.objProgram = New objProgram
    Local p = 0
	obj\Name = Lower(Name)
	For p=0 To class\LastPosition
    	obj\c[p] = class\c[p]
	Next
	obj\LastPosition = class\LastPosition
    obj\Status = STATUS_STOPPED
    Return obj
End Function
;-------------------------------------------------------------- 
Function objProgram_Get.objProgram(Name$)
    Name = Lower(Name)
    For op.objProgram = Each objProgram
        If op\Name = Name Then
            Return op
        EndIf
    Next
    Input("Object Program '" + Name + "' is not loaded")
    End
End Function
;--------------------------------------------------------------  
Function objProgram_Run(Name$)
    Local Program.objProgram = objProgram_Get(Name)
    Program\Status = STATUS_RUNNING
    gCurrentProgram = Program
    While gCurrentProgram\Status = STATUS_RUNNING
        objProgram_Exec()
    Wend
End Function
;--------------------------------------------------------------  
Function objProgram_Invoke(Name$)
    Local op.objProgram = objProgram_Get(Name)
    Local StatusCount = 1 ;default
    op\Status = STATUS_RUNNING
    While StatusCount > 0
        StatusCount = 0
        For rop.objProgram = Each objProgram
            If rop\Status = STATUS_RUNNING Then
                StatusCount = StatusCount + 1
                gCurrentProgram = rop
                objProgram_Exec()
                If gThreadedStatus = THREADED_OFF Then
                    While gThreadedStatus = THREADED_OFF And gCurrentProgram\Status = STATUS_RUNNING
                        objPRogram_Exec()
                    Wend
                EndIf
            EndIf
        Next
    Wend
End Function
;--------------------------------------------------------------  
Function objProgram_Delete(Name$)
    Name = Lower(Name)
    For Program.objProgram = Each objProgram
        If Program\Name = Name Then
            Delete Program
            Return
        EndIf
    Next
End Function
;--------------------------------------------------------------  
Function objProgram_Stop(Name$)
    Name = Lower(Name)
    For Program.objProgram = Each objProgram
        If Program\Name = Name Then
            If PRogram <> gCUrrentProgram Then
                Program\Status = STATUS_STOPPED
                Return
            EndIf
        EndIf
    Next
End Function
;--------------------------------------------------------------  
Function objProgram_Pause(Name$)
    Name = Lower(Name)
    For Program.objProgram = Each objProgram
        If Program\Name = Name Then
            If Program\Status = STATUS_RUNNING Then    
                If Program <> gCurrentProgram Then
                    Program\Status = STATUS_PAUSED 
                    Return
                EndIf
            EndIf
        EndIf      
    Next
End Function
;--------------------------------------------------------------  
Function objProgram_UnPause(Name$)
    Name = Lower(Name)
    For Program.objProgram = Each objProgram
        If Program\Status = STATUS_PAUSED Then
            If Program\Name = Name Then
                If Program <> gCurrentProgram Then
                    Program\Status = STATUS_RUNNING
                    Return
                EndIf
            EndIf
        EndIf      
    Next
End Function
;--------------------------------------------------------------  
Function objProgram_Exec()
    If gCurrentProgram\p > gCurrentProgram\LastPosition Then
        gCurrentProgram\Status = STATUS_STOPPED
        Return
    EndIf
    Select Lower(l(0)) ;Lower(gCurrentProgram\class\c[gCurrentProgram\p])
        Case "class"   : cProgram_Load(s(1),s(2))      :m(3)
        Case "new"     : objProgram_New(s(1),s(2))     :m(3)
        Case "run"     : objProgram_Run(s(1))          :m(2)
        Case "invoke"  : m(2):objProgram_Invoke(s(-1)) ;m(2)     
        Case "focus"   : gThreadedStatus = THREADED_OFF:m(1)
        Case "relax"   : gThreadedStatus = THREADED_ON :m(1)
        Case "delete"  : objProgram_Delete(s(1))       :m(2)
        Case "set"     : u(s(2))                       :m(3)
       Case "print"    : Print s(1)                    :m(2)
        Case "write"   : Write s(1)                    :m(2)
        Case "input"   : u(Input())                    :m(2)
        Case "inc"     : u(f(1)+1)                     :m(2)
        Case "dec"     : u(f(1)-1)                     :m(2)
        Case "add"     : u(f(2)+f(3))                  :m(4)
        Case "sub"     : u(f(2)-f(3))                  :m(4)
        Case "mul"     : u(f(2)*f(3))                  :m(4)
        Case "div"     : u(f(2)/f(3))                  :m(4)
        Case "pwr"     : u(f(2)^f(3))                  :m(4)
        Case "nrt"     : u(f(3)^(1/f(2)))              :m(4)
        Case "sgn"     : u(Sgn(f(2)))                  :m(3)
        Case "invrs"   : u(1/f(1))                     :m(2)
        Case "opp"     : u(-f(1))                      :m(2)
        Case "pos"     : u( Abs(f(1)))                 :m(2)
        Case "neg"     : u(-Abs(f(1)))                 :m(2)
        Case "sin"     : u( Sin(f(2)))                 :m(3)
        Case "cos"     : u( Cos(f(2)))                 :m(3)
        Case "tan"     : u( Tan(f(2)))                 :m(3)
        Case "asin"    : u(ASin(f(2)))                 :m(3)
        Case "acos"    : u(ACos(f(2)))                 :m(3)
        Case "atan"    : u(ATan(f(2)))                 :m(3)
        Case "atan2"   : u(ATan2(f(2),f(3)))           :m(4)
        Case "ifeq"    : u(i(1)+(f(2) =f(3)))          :m(4)
        Case "ifne"    : u(i(1)+(f(2)<>f(3)))          :m(4)
        Case "if$eq"   : u(i(1)+(s(2) =s(3)))          :m(4)
        Case "if$ne"   : u(i(1)+(s(2)<>s(3)))          :m(4)
        Case "ifgt"    : u(i(1)+(f(2)> f(3)))          :m(4)
        Case "ifge"    : u(i(1)+(f(2)>=f(3)))          :m(4)
        Case "jmp"     : gCurrentProgram\p=i(1)    
        Case "jnz"     : If i(1)<>0    Then:gCurrentProgram\p=i(2):Else:m(3):EndIf
        Case "jz"      : If i(1) =0    Then:gCurrentProgram\p=i(2):Else:m(3):EndIf
        Case "jmpgt"   : If f(2)> f(3) Then:gCurrentProgram\p=i(1):Else:m(4):EndIf
        Case "jmpge"   : If f(2)>=f(3) Then:gCurrentProgram\p=i(1):Else:m(4):EndIf
        Case "jmplt"   : If f(2)< f(3) Then:gCurrentProgram\p=i(1):Else:m(4):EndIf
        Case "jmple"   : If f(2)<=f(3) Then:gCurrentProgram\p=i(1):Else:m(4):EndIf
        Case "jmpeq"   : If f(2) =f(3) Then:gCurrentProgram\p=i(1):Else:m(4):EndIf
        Case "jmpne"   : If f(2)<>f(3) Then:gCurrentProgram\p=i(1):Else:m(4):EndIf
        Case "jmp$eq"  : If s(2) =s(3) Then:gCurrentProgram\p=i(1):Else:m(4):EndIf
        Case "jmp$ne"  : If s(2)<>s(3) Then:gCurrentProgram\p=i(1):Else:m(4):EndIf
        Case "end"     : gCurrentProgram\Status = STATUS_STOPPED
            Default        : m(1)
    End Select
End Function
;--------------------------------------------------------------
;Helper functions...    
Function GetValue$(rp)
    Local Token$ = gCurrentProgram\c[rp]
    Local FirstCharacter$=Left(Token,1)
    If FirstCharacter="@" Then
        Return GetValue(Mid(Token,2))
    ElseIf FirstCharacter=Chr(34) Then
         Return Mid(Token,2,Len(Token)-2)
    Else
        Return Token
    EndIf
End Function
;--------------------------------------------------------------
;Token set and get functions  
Function u(v$)
    Local Pointer = Mid(gCurrentProgram\c[gCurrentProgram\p+1],2)
    gCurrentProgram\c[Pointer]=v
End Function
 
Function l$(o)
    Return gCurrentProgram\c[gCurrentProgram\p+o]
End Function
 
Function f#(o)
    Return Float(GetValue(gCurrentProgram\p+o))
End Function
 
Function s$(o)
    Return GetValue(gCurrentProgram\p+o)
End Function
 
Function i(o)
    Return Int(GetValue(gCurrentProgram\p+o))
End Function
 
Function m(o)
    gCurrentProgram\p=gCurrentProgram\p+o
End Function
;-------------------------------------------------------------- 
;End Program Instance "Object"
;********************************************************************************************************

Function Main()
    cProgram_Load("main","main.c")
    objProgram_New("main","main")
    cProgram_PrintTokens("main")
    objProgram_Run("main")
End Function

BuildDemoFiles() ; for demo only 
Main()
Input("Demo Complete")

;********************************************************************************************************
;Function for building demo files
Function BuildDemoFiles()
	
	Local main_c = WriteFile("main.c")
		WriteLine(main_c,"%var x")
		WriteLine(main_c,"")
		WriteLine(main_c,"class")
		WriteLine(main_c,"foo")
		WriteLine(main_c,"foo.c")
		WriteLine(main_c,"")
		WriteLine(main_c,"new")
		WriteLine(main_c,"foo")
		WriteLine(main_c,"myFoo")
		WriteLine(main_c,"")
		WriteLine(main_c,"set")
		WriteLine(main_c,"x")
		WriteLine(main_c,"0")
		WriteLine(main_c,"")
		WriteLine(main_c,"invoke")
		WriteLine(main_c,"myFoo")
		WriteLine(main_c,"")
		WriteLine(main_c,"%ref loop")
		WriteLine(main_c,"    inc")
		WriteLine(main_c,"    x")
		WriteLine(main_c,"    focus")
		WriteLine(main_c,"        write")
		WriteLine(main_c,"        "+Chr(34)+"from main x=" + Chr(34))
		WriteLine(main_c,"        print")
		WriteLine(main_c,"        x")
	    WriteLine(main_c,"    relax")
	    WriteLine(main_c,"jmplt")
		WriteLine(main_c,"loop")
		WriteLine(main_c,"x")
		WriteLine(main_c,"100")
	CloseFile(main_c)

	Local foo_c = WriteFile("foo.c")
		WriteLine(foo_c,"%var x")
		WriteLine(foo_c,"set")
		WriteLine(foo_c,"x")
		WriteLine(foo_c,"0")
		WriteLine(foo_c,"%ref loop")
		WriteLine(foo_c,"    inc")
		WriteLine(foo_c,"    x")
		WriteLine(foo_c,"    focus")
		WriteLine(foo_c,"        write")
		WriteLine(foo_c,"        "+Chr(34) + "From Foo x=" + Chr(34))
		WriteLine(foo_c,"        print")
		WriteLine(foo_c,"        x")
		WriteLine(foo_c,"    relax")
		WriteLine(foo_c,"jmplt")
		WriteLine(foo_c,"loop")
		WriteLine(foo_c,"x")
		WriteLine(foo_c,"50")		
	CloseFile(foo_c)

End Function
