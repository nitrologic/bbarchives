; ID: 392
; Author: poopla
; Date: 2002-08-11 10:52:45
; Title: Level decals
; Description: Will align a sprite to a surfaces collision normals

function decal( sprite, entity)

  x = CountCollisions ( entity )
      For c = 1 To x

     
        PositionEntity sprite, CollisionX#(entity,c), CollisionY#(entity,c), CollisionZ#(entity,c) 
        
        AlignToVector sprite, CollisionNX#(entity,c), CollisionNY#(entity,c), CollisionNZ#(entity,c),3
        
        TurnEntity sprite,180,0,0
        
        MoveEntity sprite,0,0,-.01

       return sprite 
      Next

end function
