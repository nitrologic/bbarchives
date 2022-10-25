; ID: 2170
; Author: ImaginaryHuman
; Date: 2007-12-21 04:33:45
; Title: Blobby Object Plasma
; Description: A plasma effect created with realtime blobby objects

'Set these to what you want
Const Objects:Int=60
Const ScrWidth:Int=1024
Const ScrHeight:Int=768
Const RotationSpeed:Float=0.2

'Open a screen and draw a blobby energy field with squared falloff
Graphics ScrWidth,ScrHeight,32
For Local Radius:Float=1 Until 512 Step 0.5
    Local Energy:Float=(Radius*Radius)/1024.0
    SetColor Energy,Energy,Energy
    DrawOval Radius/2.0,Radius/2.0,512-Radius,512-Radius     'Try DrawRect
Next

'Turn it into an image/texture
Local Image:TImage=CreateImage(512,512)
GrabImage(Image,0,0)

'Set up the positions and movement directions/speeds/angles of objects
Local XPos:Float[Objects],YPos:Float[Objects]
Local XMove:Float[Objects],YMove:Float[Objects]
Local Rotation:Float[Objects]
Local Obj:Int
For Obj=0 Until Objects
    XPos[Obj]=Rand(0,ScrWidth)
    Ypos[Obj]=Rand(0,ScrHeight)
    XMove[Obj]=(RndFloat()-0.5)*4
    YMove[Obj]=(RndFloat()-0.5)*4
    Rotation[Obj]=RndFloat()*360
Next

'Additive blending to combine blob fields
SetBlend LIGHTBLEND

'Do a demo
Repeat
    Cls

    For Obj=0 Until Objects
        'Move the objects
        XPos[Obj]:+XMove[Obj]
        YPos[Obj]:+YMove[Obj]
        
        'Bounce the objects off the screen edges
        If XPos[Obj]<0 or XPos[Obj]>ScrWidth Then XMove[Obj]=-XMove[Obj]
        If YPos[Obj]<0 or YPos[Obj]>ScrHeight then YMove[Obj]=-YMove[Obj]
        
        'Rotate the objects around their corners
        Rotation[Obj]:+RotationSpeed
        
        'Set object color based on coords/angles
        SetColor 1024-(Rotation[Obj] Mod 256),512-XPos[Obj],512-YPos[Obj]
        
        'Draw the blobby object
        SetRotation Rotation[Obj]
        DrawImage Image,XPos[Obj],YPos[Obj]
    Next

    Flip 1
Until KeyHit(KEY_ESCAPE)
