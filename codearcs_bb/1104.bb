; ID: 1104
; Author: Vertex
; Date: 2004-07-08 07:10:39
; Title: Faster Pixelacces
; Description: Like BlitzPlus

Function LockedFormat(buffer = 0) 
   Local bnkFormat, format 
    
   If buffer = 0 Then buffer = GraphicsBuffer() 
   bnkFormat = CreateBank(4) 
   apiRtlMoveMemory bnkFormat, buffer+104, 4 
   format = PeekInt(bnkFormat, 0) 
   FreeBank bnkFormat 
    
   Return format 
End Function 

Function LockedPitch(buffer = 0) 
   Local bnkPitch, pitch 
    
   If buffer=0 Then buffer = GraphicsBuffer() 
   bnkPitch = CreateBank(12) 
   apiRtlMoveMemory bnkPitch, buffer+92, 12 
   pitch = PeekInt(bnkPitch, 0)*PeekInt(bnkPitch, 8)/8 
   FreeBank bnkPitch 
    
   Return pitch 
End Function

Function CopyBufferToBank(bank, buffer = 0) 
   Local bnkInfo, size 
    
   If buffer=0 Then buffer = GraphicsBuffer() 
   bnkInfo = CreateBank(32) 
   apiRtlMoveMemory bnkInfo, buffer+72, 32 
   size = PeekInt(bnkInfo, 20)*PeekInt(bnkInfo, 24)*PeekInt(bnkInfo, 28)/8 
    
   If BankSize(bank)<size Or PeekInt(bnkInfo, 0)=0 Then 
      FreeBank bnkInfo 
      Return False 
   Else 
      apiRtlMoveMemory bank, PeekInt(bnkInfo, 0), size 
      FreeBank bnkInfo 
      Return True 
   EndIf 
End Function 

Function CopyBankToBuffer(bank, buffer = 0) 
   Local bnkInfo, size 
    
   If buffer=0 Then buffer = GraphicsBuffer() 
   bnkInfo = CreateBank(32) 
   apiRtlMoveMemory bnkInfo, buffer+72, 32 
   size = PeekInt(bnkInfo, 20)*PeekInt(bnkInfo, 24)*PeekInt(bnkInfo, 28)/8 
    
   If BankSize(bank)<size Or PeekInt(bnkInfo, 0)=0 Then 
      FreeBank bnkInfo 
      Return False 
   Else 
      apiRtlMoveMemory2 PeekInt(bnkInfo, 0), bank, size 
      FreeBank bnkInfo 
      Return True 
   EndIf 
End Function

; and for fun:
Function BufferWidth(buffer = 0) 
   Local bnkWidth, width 

   If buffer = 0 Then Return GraphicsWidth() 
   bnkWidth = CreateBank(4) 
   apiRtlMoveMemory bnkWidth, buffer+92, 4 
   width = PeekInt(bnkWidth, 0) 
   FreeBank bnkWidth 
    
   Return width 
End Function 

Function BufferHeight(buffer = 0) 
   Local bnkHeight, height 

   If buffer = 0 Then Return GraphicsHeight() 
   bnkHeight = CreateBank(4) 
   apiRtlMoveMemory bnkHeight, buffer+96, 4 
   height = PeekInt(bnkHeight, 0) 
   FreeBank bnkHeight 
    
   Return height 
End Function
