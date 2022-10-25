; ID: 489
; Author: Vertex
; Date: 2002-11-15 11:56:35
; Title: Play backwards
; Description: A Wav file plays backwards

; ============================================================== 
shWAVE("Test.wav","Test2.wav") 
Musik   = LoadSound("test2.wav") 
Channel = PlaySound(Musik) 
WaitKey : FreeSound Musik : End 
; ============================================================== 

; ============================================================== 
Function shWAVE(FileIn$,FileOut$) 
   ; Local vars to read infos 
   Local StreamIn,Dummy$,Wav_Length,Wav_fmt,Wav_Length2 
   Local Wav_Format,Wav_Kanal,Wav_Sample,Wav_BPS1 
   Local Wav_BPS2,Wav_Length3 
    
   ; Open the file 
   If FileType(FileIn$) <> 1 Then Return 
   StreamIn = ReadFile(FileIn$) 
    
    ; Header 
   Dummy$      = Read_Wav_Bytes$(StreamIn,4) ; Read 'RIFF' 
   Wav_Length  = ReadInt(StreamIn)             ; Read Length 
   Dummy$      = Read_Wav_Bytes$(StreamIn,4) ; Read 'WAVE' 
   Wav_fmt     = Read_Wav_Bytes$(StreamIn,4) ; Read 'fmt' 
   Wav_Length2 = ReadInt(StreamIn)           ; Read Length 
   Wav_Format  = ReadShort(StreamIn)         ; Read Format (0=Mono ; 1=Stereo) 
   Wav_Kanal   = ReadShort(StreamIn)         ; Read Kanal 
   Wav_Sample  = ReadInt(StreamIn)           ; Read Sample e.g. 41MHz 
   Wav_BPS1    = ReadInt(StreamIn)           ; Read Bytes per second 
   Wav_BPS2    = ReadShort(StreamIn)         ; Read Bytes per sample 
   Dummy$      = ReadShort(StreamIn)         ; Read Bits per sample 
   While True 
      Dummy$ = Dummy$ + Chr$(ReadByte(StreamIn)) 
      If Right$(Dummy,4) = "data" Then Exit 
   Wend 
   Wav_Length3 = ReadInt(StreamIn)           ; Rest of bytes    

   ; Create Bank to save the header 
   Header = CreateBank(59) 
   SeekFile StreamIn,0 
   For I = 1 To 58 
      PokeByte Header,I,ReadByte(StreamIn) 
   Next 
   ; Create Bank to save amplitudes 
   Amplitudes = CreateBank(Wav_Length3 + 1) 
   For I = 1 To Wav_Length3 
      PokeByte Amplitudes,I,ReadByte(StreamIn) 
   Next 
    
   ; Close first and open second file 
   CloseFile StreamIn 
   StreamOut = WriteFile(FileOut$) 
    
   ; Save the header 
   For I = 1 To 58 
      WriteByte StreamOut,PeekByte(Header,I) 
   Next 
    
   ; Save amplitudes 
   For I = Wav_Length To 0 Step -1 
      If Wav_Kanal = 1 Then 
         If Wav_BPS2 = 1 Then 
             ; 8 Bit - Mono 
            I = I 
            WriteByte StreamOut,PeekByte(Amplitudes,I) 
         Else 
             ; 16Bit - Mono 
            I = I - 1 
            WriteShort StreamOut,PeekShort(Amplitudes,I) 
         EndIf 
      Else 
         If Wav_BPS2 = 1 Then 
            ; 8Bit - Stereo 
            I = I - 1 
            WriteByte StreamOut,PeekByte(Amplitudes,I) 
            WriteByte StreamOut,PeekByte(Amplitudes,I) 
         Else 
            ; 16Bit - Stereo 
            I = I - 3 
            WriteShort StreamOut,PeekShort(Amplitudes,I) 
            WriteShort StreamOut,PeekShort(Amplitudes,I) 
         EndIf 
      EndIf 
   Next 
   ; Close second file and delete banks 
   CloseFile StreamOut 
   FreeBank Header 
   FreeBank Amplitudes 
End Function 
; ============================================================== 
    
; ==============================================================    
Function Read_Wav_Bytes$(Stream,Number) 
   Local I,Out$,Character 
   For I = 1 To Number 
      Character = ReadByte(Stream) 
      If Character = 0 Then 
         Exit 
      Else 
         Out$ = Out$ + Chr$(Character) 
      EndIf 
   Next 
   Return Out$ 
End Function 
; ==============================================================
