; ID: 3139
; Author: Prym
; Date: 2014-08-07 10:52:58
; Title: Galaxang48 LAN
; Description: TCP streams

If MULTI = True Then 

			If parentplayer0 = pivotterrainchateau Then 
				Xplayer0MULTI ( numplayer0 ) = EntityX ( player0 , 0 ) ; Xplayer0 
				Yplayer0MULTI ( numplayer0 ) = EntityY ( player0 , 0 )
				Zplayer0MULTI ( numplayer0 ) = EntityZ ( player0 , 0 )
;				pitchplayer0MULTI ( numplayer0 ) = EntityPitch ( player0 , 0 )
;				yawplayer0MULTI ( numplayer0 )   = EntityYaw ( player0 , 0 )
;				rollplayer0MULTI ( numplayer0 )  = EntityRoll ( player0 , 0 ) 
			Else ; If parentplayer0 = 0 , pivotTEMP , ou autres Then 
				Xplayer0MULTI ( numplayer0 ) = EntityX ( player0 , 1 )
				Yplayer0MULTI ( numplayer0 ) = EntityY ( player0 , 1 )
				Zplayer0MULTI ( numplayer0 ) = EntityZ ( player0 , 1 )
;				pitchplayer0MULTI ( numplayer0 ) = EntityPitch ( player0 , 1 )
;				yawplayer0MULTI ( numplayer0 )   = EntityYaw ( player0 , 1 )
;				rollplayer0MULTI ( numplayer0 )  = EntityRoll ( player0 , 1 ) 
			EndIf 
			pitchplayer0MULTI ( numplayer0 ) = EntityPitch ( player0 ) ; , 1 )
			yawplayer0MULTI ( numplayer0 )   = EntityYaw ( player0 ) ; , 1 )
			rollplayer0MULTI ( numplayer0 )  = EntityRoll ( player0 ) ; , 1 ) 
			
			If KeyHit ( 57 ) Stop ; 57 = Espace ; DebugLog verifs

			If partie = 2 Then 

				strStream48ser = AcceptTCPStream ( TCP48 ) 
				DebugLog "TCP48  = " + TCP48 
				DebugLog "strStream48ser = " + strStream48ser 
;				DebugLog "ReadAvail ( strStream48ser ) = " + ReadAvail ( strStream48ser ) 
;				DebugLog "strStream48cli = " + strStream48cli 
;				DebugLog "ReadAvail ( strStream48cli ) = " + ReadAvail ( strStream48cli ) 
				If strStream48ser <> 0 Then 
					If ReadString$ ( strStream48ser ) = "coordscli" Then 
						Nmodele = ReadInt ( strStream48ser ) 
						playerID  ( Nmodele ) = ReadInt ( strStream48ser ) 
						numplayer0pris ( Nmodele ) = ReadInt ( strStream48ser ) 
						Xplayer0MULTI ( Nmodele ) = ReadInt ( strStream48ser ) 
						Yplayer0MULTI ( Nmodele ) = ReadInt ( strStream48ser ) 
						Zplayer0MULTI ( Nmodele ) = ReadInt ( strStream48ser ) 
						pitchplayer0MULTI ( Nmodele ) = ReadInt ( strStream48ser ) 
						yawplayer0MULTI ( Nmodele )   = ReadInt ( strStream48ser ) 
						rollplayer0MULTI ( Nmodele )  = ReadInt ( strStream48ser ) 
					EndIf 
;				EndIf 
					WriteString strStream48ser , "coordsser" 
					For Nmodele = 0 To 3 
						WriteInt strStream48ser , Nmodele 
						WriteInt strStream48ser , playerID ( Nmodele ) 
						WriteInt strStream48ser , numplayer0pris ( Nmodele ) 
						WriteInt strStream48ser , Xplayer0MULTI ( Nmodele ) 
						WriteInt strStream48ser , Yplayer0MULTI ( Nmodele ) 
						WriteInt strStream48ser , Zplayer0MULTI ( Nmodele ) 
						WriteInt strStream48ser , pitchplayer0MULTI ( Nmodele ) 
						WriteInt strStream48ser , yawplayer0MULTI ( Nmodele ) 
						WriteInt strStream48ser , rollplayer0MULTI ( Nmodele ) 
					Next 
					DebugLog "TCP48  = " + TCP48 
					DebugLog "strStream48ser = " + strStream48ser 
;					DebugLog "ReadAvail ( strStream48ser ) = " + ReadAvail ( strStream48ser ) 
;					DebugLog "strStream48cli = " + strStream48cli 
;					DebugLog "ReadAvail ( strStream48cli ) = " + ReadAvail ( strStream48cli ) 
;				CloseTCPStream strStream48ser 
				EndIf 
		
			ElseIf partie = 1 Then 
			
;				strStream48cli = OpenTCPStream ( adresserveur$ , 8080 ) ; ( DottedIP$ ( HOSTplayerID ) , 8080 ) 
;				DebugLog "TCP48  = " + TCP48 
;				DebugLog "strStream48ser = " + strStream48ser 
;				DebugLog "ReadAvail ( strStream48ser ) = " + ReadAvail ( strStream48ser ) 
				DebugLog "strStream48cli = " + strStream48cli 
				DebugLog "ReadAvail ( strStream48cli ) = " + ReadAvail ( strStream48cli ) 
				If strStream48cli <> 0 ; And ReadAvail ( strStream48cli ) = 0 Then 
					WriteString strStream48cli , "coordscli" 
					WriteInt strStream48cli , numplayer0 
					WriteInt strStream48cli , playerID ( numplayer0 ) 
					WriteInt strStream48cli , numplayer0pris ( numplayer0 ) ; 1 ; 
					WriteInt strStream48cli , Xplayer0MULTI ( numplayer0 ) ; EntityX ( player0 , 1 ) ; 
					WriteInt strStream48cli , Yplayer0MULTI ( numplayer0 ) ; EntityY ( player0 , 1 ) ; 
					WriteInt strStream48cli , Zplayer0MULTI ( numplayer0 ) ; EntityZ ( player0 , 1 ) ; 
					WriteInt strStream48cli , pitchplayer0MULTI ( numplayer0 ) ; EntityPitch ( player0 , 1 ) ; 
					WriteInt strStream48cli , yawplayer0MULTI ( numplayer0 ) ; EntityYaw ( player0 , 1 ) ; 
					WriteInt strStream48cli , rollplayer0MULTI ( numplayer0 ) ; EntityRoll ( player0 , 1 ) ; 
					If ReadString$ ( strStream48cli ) = "coordsser" Then 
						For Xmodele = 0 To 3 
							Nmodele = ReadInt ( strStream48cli ) ; Nmodele = cubemodele = : sa dépane 
							playerID ( Nmodele ) = ReadInt ( strStream48cli ) 
							numplayer0pris ( Nmodele ) = ReadInt ( strStream48cli ) 
							Xplayer0MULTI ( Nmodele ) = ReadInt ( strStream48cli ) 
							Yplayer0MULTI ( Nmodele ) = ReadInt ( strStream48cli ) 
							Zplayer0MULTI ( Nmodele ) = ReadInt ( strStream48cli ) 
							pitchplayer0MULTI ( Nmodele ) = ReadInt ( strStream48cli ) 
							yawplayer0MULTI ( Nmodele )   = ReadInt ( strStream48cli ) 
							rollplayer0MULTI ( Nmodele )  = ReadInt ( strStream48cli ) 
						Next 
					EndIf 
;					DebugLog "TCP48  = " + TCP48 
;					DebugLog "strStream48ser = " + strStream48ser 
;					DebugLog "ReadAvail ( strStream48ser ) = " + ReadAvail ( strStream48ser ) 
					DebugLog "strStream48cli = " + strStream48cli 
					DebugLog "ReadAvail ( strStream48cli ) = " + ReadAvail ( strStream48cli ) 
				EndIf 
		
			EndIf 
			
		EndIf
