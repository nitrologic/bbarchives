; ID: 3053
; Author: Dan
; Date: 2013-05-17 13:42:43
; Title: Load and Display Text using types B3D (b+ need modification)
; Description: load+display text +type

; Reads an File into Type
; And displays it on screen
; Use Arrow keys Up and Down to Browse the text
; Use Arrow keys Left and right to increase/decrease amount of displayed text
; Esc to end

Type Line       ;Type named line for lines in a file
Field number    ;Has number of lines
Field Text$     ;and the text on that line
End Type

Graphics 800,600,0,2     ; 800x600 screen, windowed
SetBuffer = BackBuffer

Global newtime,x,y,a 

filename$ = "textdisplay.bb"           ; Filename to read 

Txt = ReadFile ( filename$ )    ; Open a file to read

a=0
oldtime=MilliSecs()             ; Set timer

While Not Eof(txt)              ; Start of reading the file
a=a+1                           ; Line numbers counter
thisLine.Line = New Line        ; New Type named ThisLine
thisline\number =a              ; Holds the actual line number for later reference
thisline\Text$= ReadLine$ (txt) ; And reads the Text 
Wend 
newtime=(MilliSecs()-oldtime)   ; Calculate the speed of reading
CloseFile txt                   ; Closes the filehandle


x=1              ; Starting point of the Text (1st line)
y=10             ; Display counter (10 lines here)


; Main Loop
    Repeat
       ShowLines(x,y)
       
       Delay 20      ; Saves CPU Usage if you care about it !   
       
       key= GetKey()               ; Check for a keypress
       
       Select key
            
            Case 31                ; Arrow Right
              y=y-1
              If y<1 Then y=1
            Case 30                ; Arrow Left
              y=y+1
              If y>44 Then y=44    ; Max of lines to be displayed
            Case 29                ; Arrow Down
               x=x+y
               If (x)>a Then x=x-y 
            Case 28                ; Arrow up
               x=x-y
               If x-y<0 Then x=1 
       End Select
    
    Flip
    
    Until KeyHit(1)          ; Esc ends 

;End of Main Loop

End                          ; Game Over :)

Function Border()
    Color 255,255,255
    
    Rect 0,0,800,15,1
    Rect 0,585,800,600
    
    Locate 0,585
    Color 255,0,0
    Print "Use Arrows: Up/Down to browse - Left/Right to Dec/Inc lines - Esc ends"
    
    Color 0,0,0
    Locate 0,0
    Print "Starting at ("+x+"/"+a+") And showing ("+y+") lines - Text was readed in ("+newtime+") miliseconds"
    
    Color 255,255,255
End Function

Function ShowLines(Start,Length)
    Cls
    Border
        
    For ThisLine.Line = Each Line
    
        ; Checks if start line number + Length number is in the thisline\number, if yes displays it :
        
        If (thisline\number>=Start And thisline\number<=(Start+Length)-1 )
         Print thisline\number+":"+thisline\Text$
        End If
    
    Next 


End Function
