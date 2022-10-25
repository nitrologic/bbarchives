; ID: 833
; Author: Insane Games
; Date: 2003-11-21 11:33:40
; Title: 221 lines Snake clone
; Description: Just a little game I was working on, since i won't finish (not now anyway) i decided to post the code under GPL

; This game is Copyleft. You can use and change the code, as long as you respect the GPL license.
; Author : Marcio Frayze David (mfdavid@estadao.com.br)
; Insane Games - www.insanegames.tk (BR portuguese only)
; --
; Esse jogos é Copyleft. Você pode usar e alterar o código, desde que respeite a licença GPL.
; Autor : Marcio Frayze David (mfdavid@estadao.com.br)
; Insane Games - www.insanegames.tk

; Constantes usadas para desenhar o mapa
Const tamanhoDoTile = 10	; tamanho do quadrado que representa um tile
Const tamandoDoMapa = 50	; numero de tiles por coluna e por linha
Const vazio  = 0
Const parede = 1
Const playerLocal  = 2
Const playerRemoto = 3

; Outras constantes
Const telaX = 800
Const telaY = 600
Const snakeDelay = 100 		; quando menor, mas rapido o jogo (usada junto com a snakeTimer)

; Constantes para o teclado
Const setaCima     = 200
Const setaBaixo    = 208
Const setaEsquerda = 203
Const setaDireita  = 205


AppTitle "Insane Snake !"
Graphics telaX, telaY, 16, 1
SeedRnd (MilliSecs())
SetBuffer BackBuffer()

Dim mapa(tamandoDoMapa, tamandoDoMapa) ; array contendo todas as infos do mapa !
Global snakeTimer = MilliSecs() ; de tempos em tempos a snake move um tile
Global score = 0
Global highScore = 0

;Criando Type para o player
Type Player				 ; Info da posicao do player
	Field posX			 ; em qual linha o player esta
	Field posY 			 ; em qual coluna o player esta
	Field posInicialX	 ; em inicial do player (para quando morrer voltar a esta posição
	Field posInicialY	 ; em inicial do player (para quando morrer voltar a esta posição
	Field corR			 ; usado para diferenciar o player local e remoto
	Field corG			 ; usado para diferenciar o player local e remoto
	Field corB			 ; usado para diferenciar o player local e remoto
	Field direcao		 ; qual direção esta indo (setaCima, setaBaixo, setaEsquerda ou setaDireita)
	Field direcaoInicial
End Type

; Criando player local
novoPlayer(2, 3, 210, 50, 50)

; Preenchendo o mapa com os tiles
iniciaMapa()

; =======[ Funções para desenhar o mapa ]=======	
Function iniciaMapa()
	; Fazendo paredes em volta
	For i = 1 To tamandoDoMapa
		mapa(i,1)  = parede  ; parede de cima
		mapa(i,tamandoDoMapa) = parede ; parede de baixo
		mapa(1,i)  = parede  ; parede da esquerda
		mapa(tamandoDoMapa,i) = parede ; parede da direita
	Next
	
	; Obstaculos aleatorios
	For i=1 To Rand(8,32)
		posXobst = Rand(10,tamandoDoMapa-10)
		posYobst = Rand(10,tamandoDoMapa-10)
		mapa(posXobst, posYobst) = parede
	Next
End Function

Function reiniciaMapa()
	; Apaga tudo
	For x = 1 To tamandoDoMapa
		For y = 1 To tamandoDoMapa
			mapa(x,y) = vazio
		Next
	Next

	; Desenha mapa
	iniciaMapa()
	
	PlayerInfo.Player = First Player
	PlayerInfo\posX = PlayerInfo\posInicialX
	PlayerInfo\posY = PlayerInfo\posInicialY
	PlayerInfo\direcao = PlayerInfo\direcaoInicial
	score = 0
End Function

Function printMap()
	For x = 1 To tamandoDoMapa
		For y = 1 To tamandoDoMapa
			printTile(x,y)
		Next
	Next
End Function

Function printTile(posicaoX, posicaoY)
	; Vamos ver que tipo de tile que é, e de acordo com o tipo mudandos a cor
	Select mapa(posicaoX, posicaoY)
		Case parede
			; cinza escuro
			Color 100,100,100
		Case vazio
			; branco
			Color 255,255,255
		Case playerLocal
			; verde escuro
			Color 50,150,50
		Case playerRemoto
			; vermelho escuro
			Color 150,50,50
	End Select		
	
	; Colocando na posicao correta e centralizando
	; Preciso fazer algo aqui para centralizar o mapa na tela
	posicaoX = (posicaoX * tamanhoDoTile)
	posicaoY = (posicaoY * tamanhoDoTile)
	Rect posicaoX, posicaoY, tamanhoDoTile, tamanhoDoTile
End Function

; =======[ Funções para controlar o player ]=======
Function novoPlayer(posX, posY, corR, corG, corB, direcao = 205, tipoPlayer = playerLocal)
	PlayerInfo.Player = New Player 	  ;Cria um novo player
	PlayerInfo\posX = posX
	PlayerInfo\posY = posY
	PlayerInfo\posInicialX = posX
	PlayerInfo\posInicialY = posY	
	PlayerInfo\direcaoInicial = direcao
	PlayerInfo\corR = corR
	PlayerInfo\corG = corG
	PlayerInfo\corB = corB
	PlayerInfo\direcao = direcao
	mapa(PlayerInfo\posX, PlayerInfo\posY) = playerLocal
End Function

; Verifica pra que sentido a cobra ta indo e move ela para o proximo tile (se possivel)
Function moveSnake()
	If MilliSecs() > snakeTimer + snakeDelay
		snakeTimer = MilliSecs()
		PlayerInfo.Player = First Player		 ; pega o player local
		
		moveX=0
		moveY=0
		Select PlayerInfo\direcao
			Case setaCima
				moveY = -1
			Case setaBaixo
				moveY = 1
			Case setaEsquerda
				moveX = -1
			Case setaDireita
				moveX = 1
		End Select				
			
		; Se nao tiver colidindo, move o player para o proximo tile !				
		If ( Not (verificaColisao(PlayerInfo\posX + moveX, PlayerInfo\posY + moveY) )) Then
			PlayerInfo\posX = PlayerInfo\posX + moveX
			PlayerInfo\posY = PlayerInfo\posY + moveY
			score = score + 10
			If highScore < score Then highScore = score
			Else
				reiniciaMapa()
		EndIf
		
		mapa(PlayerInfo\posX, PlayerInfo\posY) = playerLocal
	EndIf
End Function

Function teclado()
	PlayerInfo.Player = First Player		 ; pega o player local

	If KeyHit(setaBaixo) Then 
		; Não deixa ela voltar pra trás
		If (PlayerInfo\direcao <> setaCima) Then PlayerInfo\direcao = setaBaixo
	EndIf
	
	If KeyHit(setaCima) Then 
		If (PlayerInfo\direcao <> setaBaixo) Then PlayerInfo\direcao = setaCima
	EndIf
	
	If KeyHit(setaEsquerda) Then 
		If (PlayerInfo\direcao <> setaDireita) Then PlayerInfo\direcao = setaEsquerda
	EndIf

	If KeyHit(setaDireita) Then 
		If (PlayerInfo\direcao <> setaEsquerda) Then PlayerInfo\direcao = setaDireita
	EndIf
End Function

; =======[ Sistema de colisão ]=======
Function verificaColisao(tileX, tileY)
	ok = False
	If mapa(tileX, tileY) <> vazio Then ok = True
	
	Return ok
End Function

; =======[ Funções de layout, etc ]=======
Function imprimeTextos()
	Color 255,255,255
	Text 5,550,"Score : " + score
	Text 5,565,"HighScore : " + highScore
End Function

;===================================================
; ================[ Main Looping ]=================
;===================================================

While Not KeyHit(1)
	Cls
	teclado()
	moveSnake()
	printMap()

	imprimeTextos()
	Flip()
Wend
