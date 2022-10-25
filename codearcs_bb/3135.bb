; ID: 3135
; Author: Prym
; Date: 2014-06-29 09:18:41
; Title: Galaxang47 LAN
; Description: Stream operations

; inits ...

strStream48cli = OpenTCPStream ( adresserveur$ , 8080 ) ; , 1 ) ; absolutely needed for a local net/play ??? 





While Not KeyHit ( 27 ) 

		If MULTI = True Then 
			For Nmodele = 0 To 3 
				PositionEntity playerMULTI ( Nmodele ) , Xplayer0MULTI ( Nmodele ) , Yplayer0MULTI ( Nmodele ) , Zplayer0MULTI ( Nmodele ) ; , 1 
				RotateEntity playerMULTI ( Nmodele ) , pitchplayer0MULTI ( Nmodele ) , yawplayer0MULTI ( Nmodele ) , rollplayer0MULTI ( Nmodele ) ; , 1 
			Next 
		EndIf 


;		CaptureWorld 
		UpdateWorld 
		RenderWorld ; tween# 
		Flip 


		If MULTI = True Then 
			Xplayer0MULTI ( numplayer0 ) = EntityX ( player0 , 1 )
			Yplayer0MULTI ( numplayer0 ) = EntityY ( player0 , 1 )
			Zplayer0MULTI ( numplayer0 ) = EntityZ ( player0 , 1 )
			pitchplayer0MULTI ( numplayer0 ) = EntityPitch ( player0 , 1 )
			yawplayer0MULTI ( numplayer0 )   = EntityYaw ( player0 , 1 )
			rollplayer0MULTI ( numplayer0 )  = EntityRoll ( player0 , 1 ) 
			If partie = 2 Then 
				strStream48ser = AcceptTCPStream ( TCP48 ) 
				If strStream48ser <> 0 Then 
					Nmodele = ReadInt ( strStream48ser ) 
					playerID  ( Nmodele ) = ReadInt ( strStream48ser ) 
					numplayer0pris ( Nmodele ) = ReadInt ( strStream48cli ) 
					Xplayer0MULTI ( Nmodele ) = ReadInt ( strStream48ser ) 
					Yplayer0MULTI ( Nmodele ) = ReadInt ( strStream48ser ) 
					Zplayer0MULTI ( Nmodele ) = ReadInt ( strStream48ser ) 
					pitchplayer0MULTI ( Nmodele ) = ReadInt ( strStream48ser ) 
					yawplayer0MULTI ( Nmodele )   = ReadInt ( strStream48ser ) 
					rollplayer0MULTI ( Nmodele )  = ReadInt ( strStream48ser ) 
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
				EndIf 
			ElseIf partie = 1 Then 
	;			strStream48cli = OpenTCPStream ( adresserveur$ , 8080 ) ; ( DottedIP$ ( HOSTplayerID ) , 8080 ) 
	;			If strStream48cli <> 0 Then 
					WriteInt strStream48cli , numplayer0 
					WriteInt strStream48cli , playerID ( numplayer0 ) 
					WriteInt strStream48cli , numplayer0pris ( numplayer0 ) ; 1 ; 
					WriteInt strStream48cli , Xplayer0MULTI ( numplayer0 ) ; EntityX ( player0 , 1 ) ; 
					WriteInt strStream48cli , Yplayer0MULTI ( numplayer0 ) ; EntityY ( player0 , 1 ) ; 
					WriteInt strStream48cli , Zplayer0MULTI ( numplayer0 ) ; EntityZ ( player0 , 1 ) ; 
					WriteInt strStream48cli , pitchplayer0MULTI ( numplayer0 ) ; EntityPitch ( player0 , 1 ) ; 
					WriteInt strStream48cli , yawplayer0MULTI ( numplayer0 ) ; EntityYaw ( player0 , 1 ) ; 
					WriteInt strStream48cli , rollplayer0MULTI ( numplayer0 ) ; EntityRoll ( player0 , 1 ) ; 
					For Nmodele = 0 To 3 
						cubemodele = ReadInt ( strStream48cli ) ; Nmodele = ; cubemodele = sa dépane 
						playerID ( Nmodele ) = ReadInt ( strStream48cli ) 
						numplayer0pris ( Nmodele ) = ReadInt ( strStream48cli ) 
						Xplayer0MULTI ( Nmodele ) = ReadInt ( strStream48cli ) 
						Yplayer0MULTI ( Nmodele ) = ReadInt ( strStream48cli ) 
						Zplayer0MULTI ( Nmodele ) = ReadInt ( strStream48cli ) 
						pitchplayer0MULTI ( Nmodele ) = ReadInt ( strStream48cli ) 
						yawplayer0MULTI ( Nmodele )   = ReadInt ( strStream48cli ) 
						rollplayer0MULTI ( Nmodele )  = ReadInt ( strStream48cli ) 
					Next 
	;				CloseTCPStream strStream48cli 
	;			EndIf 
			EndIf 
			
Wend 





CloseTCPStream strStream48cli 

End 





;;;;
