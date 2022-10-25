; ID: 2795
; Author: ToeB
; Date: 2010-12-08 11:08:49
; Title: SimpleREPLAY 1.1
; Description: record 3D entitys

;=======================+ 
;---- SimpleREPLAY -----| 
;-----------------------| 
;--------- by ----------| 
;-----------------------| 
;-------- ToeB ---------| 
;--- Tobias  Hendricks -| 
;=======================+ 

;---------------------------------------- 
; Globals 
;---------------------------------------- 
Global REPLAY_RecordTime 
Global REPLAY_RecordMs 
Global REPLAY_GlobalTime 
Global REPLAY_RecordOn 
Global REPLAY_RecordDataCount 

Global REPLAY_PlayOn 
Global REPLAY_PlayTime# 

Global REPLAY_FrameMS 
Global REPLAY_FrameTime 

Global REPLAY_DemoOn 
Global REPLAY_DemoStream 
Global REPLAY_DemoSeek 

Global REPLAY_EntityCount 

;---------------------------------------- 
; Types 
;---------------------------------------- 
Type REPLAY_Object 
   Field Entity 
   Field Animated 
   Field LastData.REPLAY_Data 
   Field ReplayBank 
   Field Name$, ID 
End Type 


Type REPLAY_Data 
   Field Info.REPLAY_Object 
   Field LastData.REPLAY_Data 
   Field NextData.REPLAY_Data 
   Field XPos#, YPos#, ZPos# 
   Field XRot#, YRot#, ZRot# 
   Field XScal#, YScal#, ZScal# 
   Field Time# 
End Type 

;---------------------------------------- 
; Functions 
;---------------------------------------- 
Function REPLAY_Init( tmpRecordTime=100 ) 

   REPLAY_RecordTime = tmpRecordTime 
   REPLAY_RecordMs = 0 
    
End Function 

  
Function REPLAY_AddEntity( tmpEntity ) 

   If tmpEntity = 0 Then Return 
    
   Local tmpObject.REPLAY_Object = New REPLAY_Object 
    
   tmpObject\Entity = tmpEntity 
   tmpObject\Animated = tmpAnimated 
   tmpObject\ReplayBank = CreateBank( 0 ) 
   tmpObject\Name$ = EntityName( tmpEntity ) 
    
   REPLAY_EntityCount = REPLAY_EntityCount + 1 
    
   Return Handle( tmpObject ) 
    
End Function 



Function REPLAY_Record( ) 

   Local tmpTime = MilliSecs() 
   Local tmpData.REPLAY_Data 
    
   If REPLAY_PlayOn = 1 Then 
      REPLAY_PlayStop( ) 
   EndIf 
    
   If REPLAY_RecordOn = 0 Then 
      REPLAY_GlobalTime = tmpTime 
      REPLAY_RecordMs = 0 
      REPLAY_RecordOn = 1 
      Delete Each REPLAY_Data 
      REPLAY_RecordDataCount = 0 
   EndIf 
    
   If REPLAY_RecordOn = 1 Then 
      If REPLAY_RecordMs <= tmpTime Then 
         For tmpObject.REPLAY_Object = Each REPLAY_Object 
            tmpData = New REPLAY_Data 
            tmpData\Info = tmpObject 
            tmpData\LastData = tmpObject\LastData 
            If tmpData\LastData <> Null Then 
               tmpData\LastData\NextData = tmpData 
            EndIf 
            tmpObject\LastData = tmpData    
            tmpData\Time = tmpTime - REPLAY_GlobalTime 
            tmpData\XPos = EntityX( tmpObject\Entity, 1 )    
            tmpData\YPos = EntityY( tmpObject\Entity, 1 )    
            tmpData\ZPos = EntityZ( tmpObject\Entity, 1 )    
            tmpData\XRot = EntityPitch( tmpObject\Entity, 1 )    
            tmpData\YRot = EntityYaw( tmpObject\Entity, 1 )    
            tmpData\ZRot = EntityRoll( tmpObject\Entity, 1 ) 
            tmpData\XScal = REPLAY_GetEntityScale( tmpObject\Entity, 0 ) 
            tmpData\YScal = REPLAY_GetEntityScale( tmpObject\Entity, 1 ) 
            tmpData\ZScal = REPLAY_GetEntityScale( tmpObject\Entity, 2 ) 
         Next 
         REPLAY_RecordDataCount = REPLAY_RecordDataCount + 1    
         REPLAY_RecordMs = tmpTime + REPLAY_RecordTime 
      EndIf 
   EndIf 
    
End Function 


Function REPLAY_RecordDemo( tmpPath$ ) 
    
   Local tmpObject.REPLAY_Object 
   Local tmpTime = MilliSecs( ) 
   Local tmpID 
    
   If REPLAY_DemoOn = 0 Then 
      REPLAY_RecordStop( ) 
      REPLAY_PlayStop( ) 
      REPLAY_DemoStream = WriteFile( tmpPath$ ) : CloseFile( REPLAY_DemoStream ) 
      REPLAY_DemoStream = OpenFile( tmpPath$ ) 
      If REPLAY_DemoStream <> 0 Then 
         WriteInt( REPLAY_DemoStream, REPLAY_EntityCount ) 
         tmpID = 0 
         For tmpObject = Each REPLAY_Object 
            tmpID = tmpID + 1 : tmpObject\ID = tmpID 
            WriteInt( REPLAY_DemoStream, tmpObject\ID ) 
            WriteString( REPLAY_DemoStream, tmpObject\Name$ ) 
         Next        
         REPLAY_DemoOn = 1 
         REPLAY_DemoSeek = 0 
         REPLAY_RecordOn = 1 
         REPLAY_GlobalTime = tmpTime 
      EndIf 
   EndIf 
    
   If REPLAY_DemoOn = 1 Then 
      If REPLAY_RecordMs <= tmpTime Then 
         For tmpObject.REPLAY_Object = Each REPLAY_Object 
            WriteInt( REPLAY_DemoStream, tmpObject\ID ) 
            WriteInt( REPLAY_DemoStream, tmpTime - REPLAY_GlobalTime ) 
            WriteFloat( REPLAY_DemoStream, EntityX( tmpObject\Entity, 1 ) ) 
            WriteFloat( REPLAY_DemoStream, EntityY( tmpObject\Entity, 1 ) ) 
            WriteFloat( REPLAY_DemoStream, EntityZ( tmpObject\Entity, 1 ) ) 
            WriteFloat( REPLAY_DemoStream, EntityPitch( tmpObject\Entity, 1 ) ) 
            WriteFloat( REPLAY_DemoStream, EntityYaw( tmpObject\Entity, 1 ) ) 
            WriteFloat( REPLAY_DemoStream, EntityRoll( tmpObject\Entity, 1 ) ) 
            WriteFloat( REPLAY_DemoStream, REPLAY_GetEntityScale( tmpObject\Entity, 0 ) ) 
            WriteFloat( REPLAY_DemoStream, REPLAY_GetEntityScale( tmpObject\Entity, 1 ) ) 
            WriteFloat( REPLAY_DemoStream, REPLAY_GetEntityScale( tmpObject\Entity, 2 ) ) 
         Next 
         REPLAY_RecordDataCount = REPLAY_RecordDataCount + 1    
         REPLAY_RecordMs = tmpTime + REPLAY_RecordTime 
      EndIf 
   EndIf 
    
End Function 

Function REPLAY_RecordStop( ) 

   REPLAY_RecordOn = 0 
   REPLAY_DemoOn = 0 
   If REPLAY_DemoStream <> 0 Then 
      CloseFile( REPLAY_DemoStream ) 
      REPLAY_DemoStream = 0 
   EndIf 
    
End Function 


Function REPLAY_Play( tmpTimeScale#=1.0 ) 

   Local tmpObject.REPLAY_Object 
   Local tmpData.REPLAY_Data 
   Local tmpAnz, tmpBPos 
   Local tmpPos#, tmpPos1, tmpPos2 
   Local tmpTime = MilliSecs() 
   Local tmpRepTime, tmpInterpolFakt# 
   Local tmpXPos#[2], tmpYPos#[2], tmpZPos#[2] 
   Local tmpXRot#[2], tmpYRot#[2], tmpZRot#[2] 
   Local tmpXScal#[2], tmpYScal#[2], tmpZScal#[2] 
    
   REPLAY_FrameTime = tmpTime - REPLAY_FrameMs 
   REPLAY_FrameMs = tmpTime    
   If REPLAY_FrameTime = tmptime Then REPLAY_FrameTime = 16 
    
   If REPLAY_RecordOn = 1 Then 
      REPLAY_RecordStop( ) 
   EndIf 
    
   If REPLAY_PlayOn = 0 Then 
      tmpAnz = REPLAY_RecordDataCount * 40 
      For tmpObject = Each REPLAY_Object          
         ResizeBank( tmpObject\ReplayBank, tmpAnz ) 
         tmpBPos = 0 
         For tmpData = Each REPLAY_Data 
            If tmpData\Info = tmpObject Then 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 00, tmpData\XPos ) 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 04, tmpData\YPos ) 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 08, tmpData\ZPos ) 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 16, tmpData\XRot ) 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 20, tmpData\YRot ) 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 24, tmpData\ZRot ) 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 28, tmpData\XScal ) 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 32, tmpData\YScal ) 
               PokeFloat( tmpObject\ReplayBank, tmpBPos + 36, tmpData\ZScal ) 
               tmpBPos = tmpBPos + 40 
            EndIf 
         Next 
      Next 
      REPLAY_PlayOn = 1 
      REPLAY_PlayTime = 1 
      REPLAY_Time = 0 
   EndIf 
    
   If REPLAY_PlayOn = 1 Then 
      tmpPos1 = Floor( REPLAY_PlayTime / REPLAY_RecordTime ) 
      tmpPos2 = Ceil( REPLAY_PlayTime / REPLAY_RecordTime ) 
      If tmpPos1 = tmpPos2 Then tmpPos2 = tmpPos2 + 1 
      If tmpPos1 > REPLAY_RecordDataCount Or tmpPos2 > REPLAY_RecordDataCount Then 
         tmpPos1 = REPLAY_RecordDataCount 
         tmpPos2 = REPLAY_RecordDataCount 
      EndIf 
      tmpPos = (REPLAY_PlayTime Mod REPLAY_RecordTime) / REPLAY_RecordTime 
      For tmpObject = Each REPLAY_Object 
         tmpXPos[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 00 ) 
         tmpYPos[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 04 ) 
         tmpZPos[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 08 ) 
         tmpXRot[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 16 ) 
         tmpYRot[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 20 ) 
         tmpZRot[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 24 ) 
         tmpXScal[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 28 ) 
         tmpYScal[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 32 ) 
         tmpZScal[ 0 ] = PeekFloat( tmpObject\ReplayBank, tmpPos1 * 40 + 36 ) 
          
         tmpXPos[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 00 ) 
         tmpYPos[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 04 ) 
         tmpZPos[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 08 ) 
         tmpXRot[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 16 ) 
         tmpYRot[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 20 ) 
         tmpZRot[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 24 ) 
         tmpXScal[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 28 ) 
         tmpYScal[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 32 ) 
         tmpZScal[ 1 ] = PeekFloat( tmpObject\ReplayBank, tmpPos2 * 40 + 36 )          
          
          
         tmpXPos[ 2 ] = tmpXPos[ 0 ] + ( tmpXPos[ 1 ] - tmpXPos[ 0 ] ) * tmpPos 
         tmpYPos[ 2 ] = tmpYPos[ 0 ] + ( tmpYPos[ 1 ] - tmpYPos[ 0 ] ) * tmpPos 
         tmpZPos[ 2 ] = tmpZPos[ 0 ] + ( tmpZPos[ 1 ] - tmpZPos[ 0 ] ) * tmpPos 
         tmpXRot[ 2 ] = tmpXRot[ 0 ] + ( tmpXRot[ 1 ] - tmpXRot[ 0 ] ) * tmpPos 
         tmpYRot[ 2 ] = tmpYRot[ 0 ] + ( tmpYRot[ 1 ] - tmpYRot[ 0 ] ) * tmpPos 
         tmpZRot[ 2 ] = tmpZRot[ 0 ] + ( tmpZRot[ 1 ] - tmpZRot[ 0 ] ) * tmpPos 
         tmpXScal[ 2 ] = tmpXScal[ 0 ] + ( tmpXScal[ 1 ] - tmpXScal[ 0 ] ) * tmpPos 
         tmpYScal[ 2 ] = tmpYScal[ 0 ] + ( tmpYScal[ 1 ] - tmpYScal[ 0 ] ) * tmpPos 
         tmpZScal[ 2 ] = tmpZScal[ 0 ] + ( tmpZScal[ 1 ] - tmpZScal[ 0 ] ) * tmpPos 
          
          
         PositionEntity tmpObject\Entity, tmpXPos[ 2 ], tmpYPos[ 2 ], tmpZPos[ 2 ], 1 
         RotateEntity tmpObject\Entity, tmpXRot[ 2 ], tmpYRot[ 2 ], tmpZRot[ 2 ], 1 
         ScaleEntity tmpObject\Entity, tmpXScal[ 2 ], tmpYScal[ 2 ], tmpXScal[ 2 ], 1 
      Next 
       
      tmpRepTime = REPLAY_RecordDataCount * REPLAY_RecordTime - REPLAY_RecordTime 
      REPLAY_PlayTime = ( REPLAY_PlayTime + Float( REPLAY_FrameTime ) * tmpTimeScale ) Mod tmpRepTime 
      If REPLAY_PlayTime < 0 Then REPLAY_PlayTime = tmpRepTime - REPLAY_RecordTime 

   EndIf 
    
End Function 

Function REPLAY_LoadDemo( tmpPath$ ) 

   Local tmpStream 
   Local tmpObjectCount 
   Local tmpID, tmpName$ 
   Local tmpObject.REPLAY_Object 
   Local tmpData.REPLAY_Data 
   Local tmpAktTime 
    
   Delete Each REPLAY_Data 
    
   tmpStream = ReadFile( tmpPath$ ) 
   If tmpStream = 0 Then Return 0 
    
   tmpObjectCount = ReadInt( tmpStream ) 
   For i = 1 To tmpObjectCount 
      tmpID = ReadInt( tmpStream ) 
      tmpName$ = ReadString( tmpStream ) 
      For tmpObject = Each REPLAY_Object 
         If tmpObject\Name$ = tmpName$ Then 
            tmpObject\ID = tmpID : Exit 
         EndIf 
      Next 
   Next 
   REPLAY_RecordDataCount = 0 
   tmpAktTime = -1 
   While Not Eof( tmpStream ) 
      tmpID = ReadInt( tmpStream ) 
      tmpData = New REPLAY_Data 
      tmpData\Time = ReadInt( tmpStream ) 
      tmpData\XPos = ReadFloat( tmpStream ) 
      tmpData\YPos = ReadFloat( tmpStream ) 
      tmpData\ZPos = ReadFloat( tmpStream ) 
      tmpData\XRot = ReadFloat( tmpStream ) 
      tmpData\YRot = ReadFloat( tmpStream ) 
      tmpData\ZRot = ReadFloat( tmpStream ) 
      tmpData\XScal = ReadFloat( tmpStream ) 
      tmpData\YScal = ReadFloat( tmpStream ) 
      tmpData\ZScal = ReadFloat( tmpStream ) 
      For tmpObject = Each REPLAY_Object 
         If tmpObject\ID = tmpID Then 
            tmpData\Info = tmpObject 
            tmpData\LastData = tmpObject\LastData 
            If tmpData\LastData <> Null Then 
               tmpData\LastData\NextData = tmpData 
            EndIf 
            tmpObject\LastData = tmpData    
            Exit 
         EndIf 
      Next 
      If tmpData\Time <> tmpAktTime Then 
         REPLAY_RecordDataCount = REPLAY_RecordDataCount + 1    
         tmpAktTime = tmpData\Time 
      EndIf 
   Wend 
End Function 



Function REPLAY_PlayStop( ) 

   REPLAY_PlayOn = 0 
    
End Function  

Function REPLAY_GetEntityScale#( tmpEntity, tmpAxis=0 ) 
    
   If tmpEntity = 0 Then Return 0.0 

   Local tmpVX#, tmpVY#, tmpVZ# 
    
   If tmpAxis < 0 Then tmpAxis = 0 
   If tmpAxis > 2 Then tmpAxis = 2 
    
   tmpVX = GetMatElement( tmpEntity, tmpAxis, 0 ) 
   tmpVY = GetMatElement( tmpEntity, tmpAxis, 1 ) 
   tmpVZ = GetMatElement( tmpEntity, tmpAxis, 2 ) 
    
   Return ( Sqr( tmpVX*tmpVX + tmpVY*tmpVY + tmpVZ*tmpVZ ) ) 
    
End Function
