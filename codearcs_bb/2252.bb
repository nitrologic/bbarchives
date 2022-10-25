; ID: 2252
; Author: Spencer
; Date: 2008-05-12 17:25:46
; Title: Blitz3D Split String Function
; Description: Split / UBound

;********************************************************************************
;---------;---------;---------;---------;---------;---------;---------;---------;
                                                                                ;
Dim gs_StrAr$(1)                                                                ;
                                                                                ;
;----------------------------------------------------------------------         ;
                                                                                ;
Type t_StrAry                                                                   ;
                                                                                ;
    Field i_Pos                                                                 ;
    Field s_Val$                                                                ;
                                                                                ;
End Type                                                                        ;
                                                                                ;
;----------------------------------------------------------------------         ;
                                                                                ;
Function fn_Split(ls_Strng$, ls_Split$, lb_Flush )                              ;
                                                                                ;
    Local li_sPos   = 1                                                         ;
    Local li_ePos   = 0                                                         ;
    Local ls_Token$ = ""                                                        ;
    Local tmp.t_StrAry = Last t_StrAry                                          ;
    Local li_TknCt  = 0                                                         ;
    Local li_SplLn  = Len(ls_Split)                                             ;
    Local li_StrLn  = Len(ls_Strng)                                             ;
                                                                                ;
    If lb_Flush Then                                                            ;
                                                                                ;
        fn_Flush()                                                              ;
                                                                                ;
    ElseIf tmp <> Null Then                                                     ;
                                                                                ;
        li_TknCt = tmp\i_Pos +1                                                 ;
                                                                                ;
    EndIf                                                                       ;
                                                                                ;
                                                                                ;
    If Right(ls_Strng,li_SplLn) <> ls_Split Then                                ;
                                                                                ;
        ls_Strng = ls_Strng + ls_Split                                          ;
                                                                                ;
    EndIf                                                                       ;
                                                                                ;
    Repeat                                                                      ;
                                                                                ;
        li_ePos = Instr(ls_Strng,ls_Split,li_sPos)                              ;
        ls_Token = Mid( ls_Strng, li_sPos, li_ePos - li_sPos )                  ;
                                                                                ;
        n.t_StrAry = New t_StrAry                                               ;
        n\i_Pos = li_TknCt                                                      ;
        n\s_Val = ls_Token                                                      ;
                                                                                ;
        li_TknCt = li_TknCt + 1                                                 ;
        li_sPos  = li_ePos + li_SplLn                                           ;
                                                                                ;
    Until li_sPos > li_StrLn                                                    ;
                                                                                ;
End Function                                                                    ;
                                                                                ;
;----------------------------------------------------------------------         ;
                                                                                ;
Function fn_Flush()                                                             ;
                                                                                ;
    For s.t_StrAry = Each t_StrAry                                              ;
                                                                                ;
        Delete s                                                                ;
                                                                                ;
    Next                                                                        ;
                                                                                ;
End Function                                                                    ;
                                                                                ;
;----------------------------------------------------------------------         ;
                                                                                ;
Function fi_UBound()                                                            ;
                                                                                ;
    Local Tmp.t_StrAry = Last t_StrAry                                          ;
                                                                                ;
    Return Tmp\i_Pos                                                            ;
                                                                                ;
End Function                                                                    ;
                                                                                ;
;----------------------------------------------------------------------         ;
                                                                                ;
Function fn_ToDim()                                                             ;
                                                                                ;
    Local tmp.t_StrAry = Last t_StrAry                                          ;
    Local li_Count = tmp\i_Pos + 1                                              ;
    Local li_tPos  = 0                                                          ;
                                                                                ;
    Dim gs_StrAr$(li_Count)                                                     ;
                                                                                ;
    For s.t_StrAry = Each t_StrAry                                              ;
                                                                                ;
        gs_StrAr(li_tPos) = s\s_Val                                             ;
        li_tPos = li_tPos + 1                                                   ;
                                                                                ;
    Next                                                                        ;
                                                                                ;
End Function                                                                    ;
                                                                                ;
;----------------------------------------------------------------------         ;
                                                                                ;
Function fs_GetEl$(li_Elemt)                                                    ;
                                                                                ;
    Local tmp.t_StrAry = Last t_StrAry                                          ;
    Local li_LastEl = tmp\i_Pos                                                 ;
                                                                                ;
    If li_Elemt <= li_LastEl Then                                               ;
                                                                                ;
        For s.t_StrAry = Each t_StrAry                                          ;
                                                                                ;
           If s\i_Pos = li_Elemt Then                                           ;
                                                                                ;
              Return  s\s_Val                                                   ;
                                                                                ;
           EndIf                                                                ;
                                                                                ;
        Next                                                                    ;
                                                                                ;
        Return ""                                                               ;
                                                                                ;
    Else                                                                        ;
                                                                                ;
        Return ""                                                               ;
                                                                                ;
    EndIf                                                                       ;
                                                                                ;
End Function                                                                    ;
                                                                                ;
;---------;---------;---------;---------;---------;---------;---------;---------;
;********************************************************************************







;---------;---------;---------;---------;---------;---------;---------;---------;

	;---- TEST ONE ----;

	Cls
	Locate 0, 0
	Input("Press Enter to execute TEST ONE...")
	Print
	Print "Test One"
	Print
	
	fn_Split("11;12;13;14;15;16;17;18;19;20;" , ";" , True )
	fn_ToDim()
	
	Print "Element #9 After Array Conversion:" + gs_StrAr(9)
	Print
	Print
	Input("TEST ONE COMPLETE")
	
	
;---------;---------;---------;---------;---------;---------;---------;---------;	
	
	;---- TEST TWO ----;
	
	Cls
	Locate 0, 0
	Input("Press Enter to execute TEST TWO...")
	Print
	Print "Test Two"
	Print
	
	fn_Split("100;200;3000000;40;cool beans!",";",False)
	
	For x.t_StrAry = Each t_StrAry
	
		Print RSet(x\i_Pos,4) + " :" + x\s_Val  
	
	Next
	
	Print
	Print "UBound is :" + fi_UBound()
	Print "Element #2 from Stack :" + fs_GetEl(2)
	Print

	fn_ToDim()
	
	Print "Element #2 from Array after Conversion :" + gs_StrAr(2)
	Print
	Print	
	Input("TEST TWO COMPLETE  [Enter to EXIT]")
	

;---------;---------;---------;---------;---------;---------;---------;---------;
End
