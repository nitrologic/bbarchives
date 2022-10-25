; ID: 438
; Author: DH
; Date: 2002-09-27 23:16:20
; Title: FPS Average Display
; Description: Easy way to get an average FPS call

Global FPS_Oldtime, FPS_Newtime, FPS_Ticks
Global FPS_Current,FPS_Final
Global FPS_SampleRate   = 5  ;Take a sample every N ticks
Global FPS_Samples      = 10 ;Samples to average (res of the average)
Global FPS_BufferIndex  = 1
Global FPS_Font
Dim FPS_Buffer(10)


Function Get_FPS(PosX#=10,PosY#=2)
         if FPS_Font = 0 then
              FPS_Font=LoadFont("New Times Roman",12)
         endif
         FPS_Newtime = MilliSecs()
         FPS_Ticks = FPS_Ticks + 1
         If FPS_Ticks > FPS_SampleRate Then
            FPS_Current = FPS_Newtime - FPS_Oldtime
            If FPS_Current = 0 Then FPS_Current = 1000 Else FPS_Current = 1000/FPS_Current
            FPS_Buffer(FPS_BufferIndex) = FPS_Current
            FPS_BufferIndex = FPS_BufferIndex + 1
            if FPS_BufferIndex > FPS_Samples then
                 For FPS_Count = 1 to FPS_Samples
                     FPS_Master = FPS_Master + FPS_Buffer(FPS_Count)                      
                 next
                 FPS_Final = FPS_Master / FPS_Samples
                 FPS_BufferIndex = 1
            endif
            FPS_Ticks = 0
         EndIf
         FPS_Oldtime = MilliSecs()
         SetFont(FPS_Font)
         Text(PosX#,PosY#,"FPS:"+FPS_Final) 
         ;Return FPS_Final
End Function
