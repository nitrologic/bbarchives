; ID: 3182
; Author: Blitzplotter
; Date: 2015-01-24 03:28:30
; Title: File Loading Progress Bar
; Description: One file writes an Int (1-100) to a .dat file, the other reads the .dat file and presents a progress bar. Run both the files from the same directory

; File 1:---------------------


; Write Progress by Blitzplotter - 24 Jan 15
;
; Writes to a count of 0 to 100 to a progress file
;
; WriteProgress.bb
;

Graphics 300,130,16,2

AppTitle "Write progress data"

Print "Press Any Key once you have compiled"
Print "and ran the LoadProgress_Main.bb file....."
Print " "
Print "Left click in this window to:"
Print "  "
Print "... Press Any Key....."

;reset the counter

progress_filename$="./progress_file_Int.dat"

output = WriteFile(progress_filename$)

WriteInt output,1

CloseFile(output)

WaitKey

counter% = 1

While progress_counter<=100
	
	counter=counter+1
	
	output = WriteFile(progress_filename$)
	
	WriteInt(output,counter)

	CloseFile(output)
		
	progress_counter=progress_counter+1
	
	oldTime=MilliSecs()
	While MilliSecs() < oldTime + 90
		;Text 20,20,"Still waiting!: "+MilliSecs+" oldtime: "+oldTime
	Wend 
	
	Print "Progress Counter: "+progress_counter
	
Wend

;write out the current counter

output = WriteFile(progress_filename$)

WriteInt output,1

CloseFile(output)

End


;==============================================
; Separate file 2:---
;==============================================

; Load Progress by Blitzplotter - 24 Jan 15
;
; LoadProgress_Main.bb
;
; Reads a local file progress_file_Int.dat
; and updates a progress bar with whatever
; is stored there
;
; Note: Compile and run the WriteProgress.bb first



Graphics 800,200,32,2

AppTitle "Loading Progress Monitor"


Counter%=10

Text 100,200,"This is the file load progress executable...."

;open the progress file for reading

progress_filename$="./progress_file_Int.dat"



;Loop around until progress read in as 100 percent

Text 200,80,"0"
Text 500,80, "100"

.keep_reading_file

filename = ReadFile (progress_filename$)	;load the config file



If filename <> 0								;only continue if the file exists
	;While Not Eof (filename)				;keep reading until the end of the file
		;If 1stlineone=0
		
	Counter% = ReadInt(filename)	
		
		Text 100,600,"Progress Counter at: "+Counter%
		
		CloseFile(filename)	
		
Else
	Text 100,300,"DEBUG: Progress_File_Locked, trying again soon...."

EndIf




;Text 360,50,"File Loading Progress: "+Counter%

oldTime=MilliSecs()
While MilliSecs() < oldTime + 10
	;Text 20,20,"Still waiting!: "+MilliSecs+" oldtime: "+oldTime
Wend 
	
VWait(50)

;make a little progress bar how_thick pixels tall

how_thick=20

For thickness=1 To how_thick
	Line 200,100+thickness,200+(Counter%*3),100+thickness
Next

If Counter% < 100 Then Goto keep_reading_file

Text 200,140,"Finished Loading.... Press Any Key to Continue "

WaitKey

End
