; ID: 2763
; Author: TWH
; Date: 2010-09-07 14:49:22
; Title: Pixmap edges and normals
; Description: calcs smoothed normals of black and white pixmap

'written by torbjoern haugen 7.september 2010
'based on pseudo-code from "Mathematics and Physics for Programmmers by Danny Kodicek"
'expects the bitmap to be black and white
type TimageNormals
	field originalImage:TPixmap
	field edgemap:int[] 'is pixel a edge? true/false
	field normals:vec2[]
	field smoothedNormals:vec2[]
	field width:int, height:int
	field grayLimit:int = $ff010101''$fffffffa
	
	field vecZero:vec2 = vec2.create( 0,0 )
	field vecU:vec2 = vec2.create( 0,-1)
	field vecD:vec2 = vec2.create( 0, 1)
	field vecL:vec2 = vec2.create(-1, 0)
	field vecR:vec2 = vec2.create( 1, 0)
	field vecUL:vec2 = vec2.create(-1,-1)
	field vecUR:vec2 = vec2.create( 1,-1)
	field vecDL:vec2 = vec2.create(-1, 1)
	field vecDR:vec2 = vec2.create( 1, 1)

	method findEdges(pixmap:TPixmap)
		originalImage = pixmap
		width = pixmap.width
		height = pixmap.height
		edgemap = new int[width*height]
		normals = new vec2[width*height]
		smoothedNormals = new vec2[width*height]

		_findEdges()
		_calcNormals()
		_smoothNormals()
	end method

	'save edge pixels in edgemap
	method _findEdges()
		local i:int
		local j:int
		for i=1 until originalImage.width-1
		for j=1 until originalImage.height-1
			local u_pixel:int = originalImage.readpixel(i,j-1)
			local d_pixel:int = originalImage.readpixel(i,j+1)
			local l_pixel:int = originalImage.readpixel(i-1,j)
			local r_pixel:int = originalImage.readpixel(i+1,j)
			local current_pixel:int = originalImage.readpixel(i,j)
			
			if current_pixel< grayLimit and (u_pixel>grayLimit or d_pixel>grayLimit or l_pixel>grayLimit or r_pixel>grayLimit)
				edgemap[i*height+j] = 1
			else
				edgemap[i*height+j] = 0
			endif
		next
		next
	end method
	
	'for each edge, look at its neighbours and decide normal
	method _calcNormals()
		local i:int
		local j:int
		
		for i=1 until originalImage.width-1
		for j=1 until originalImage.height-1		
			if edgemap[i*height+j] 
				local v:vec2 = new vec2
				v.x = 0; v.y = 0;
				local neigh:int = 0 'holds count of which edge neighbours this pixel has
				
				local hasU:int = false
				local hasD:int = false
				local hasL:int = false
				local hasR:int = false
				
				if originalImage.readpixel(i,j-1) > grayLimit
					hasU = true
					neigh:+1
				endif
				
				if originalImage.readpixel(i,j+1) > grayLimit
					hasD = true
					neigh:+1
				endif
				
				if originalImage.readpixel(i-1,j) > grayLimit
					hasL = true
					neigh:+1
				endif
				
				if originalImage.readpixel(i+1,j) > grayLimit
					hasR = true
					neigh:+1
				endif

				'We want the normal to point away from neighbour pixels
				if(neigh=0)
					v.x=0; v.y=0; 'no normal
				else if neigh=1 'has 1 neighbour, normal is minus vec to neighbour
					if(hasL) v = vecR
					if(hasR) v = vecL
					if(hasU) v = vecD
					if(hasD) v = vecU
				else if neigh=2
					if     (hasD and hasU) 
						v = vecZero
					else if(hasL and hasR)
						v = vecZero
					else if(hasD and hasR) 
						v = vecUL
					else if(hasD and hasL)
						v = vecUR
					else if(hasU and hasR)
						v = vecDL
					else if(hasU and hasL)
						v = vecDR
					else 'has U and D or L and R
						local avgx# = hasL + -hasR
						local avgy# = hasD + -hasU
						v.x = -avgx; v.y = -avgy;
					endif
				else if neigh=3 'left||right and up||down
					' v = the non-heighbour vector
					v.x = 0; v.y = 0;
					if(hasL) v.x :+ 1
					if(hasR) v.x :- 1
					if(hasU) v.y :+ 1
					if(hasD) v.y :- 1
				endif
				
				if neigh > 0
					local length:int = sqr(v.x*v.x+v.y*v.y)
					v.x :/ length
					v.y :/ length
				endif
				
				normals[i*height+j] = v	
			endif
		next		
		next
	end method
	
	method _smoothNormals()
		local i:int
		local j:int
		for i=1 until originalImage.width-1
		for j=1 until originalImage.height-1	
			if edgemap[i*height+j]
				local current_norm:vec2 = normals[i*height+j]
				local u_norm:vec2 = normals[i*height+(j-1)]
				local d_norm:vec2 = normals[i*height+(j+1)]
				local l_norm:vec2 = normals[(i-1)*height+j]
				local r_norm:vec2 = normals[(i+1)*height+j]
				
				local ul_norm:vec2 = normals[(i-1)*height+(j-1)]
				local ur_norm:vec2 = normals[(i+1)*height+(j-1)]
				local dl_norm:vec2 = normals[(i-1)*height+(j+1)]
				local dr_norm:vec2 = normals[(i+1)*height+(j+1)]
				
				local count:int = 1
				local avgx#=0
				local avgy#=0
				avgx :+ current_norm.x; avgy :+ current_norm.y
				if(edgemap[i*height+(j-1)])
					avgx :+ u_norm.x; avgy :+ u_norm.y
					count :+1
				endif
				
				if(edgemap[i*height+(j+1)])
					avgx :+ d_norm.x; avgy :+ d_norm.y
					count :+1
				endif
				
				if(edgemap[(i-1)*height+j])
					avgx :+ l_norm.x; avgy :+ l_norm.y
					count :+1
				endif
				
				if(edgemap[(i+1)*height+j])
					avgx :+ r_norm.x; avgy :+ r_norm.y
					count :+1
				endif
				
				'Up+Left or right
				if(edgemap[(i-1)*height+(j-1)])
					avgx :+ ul_norm.x; avgy :+ ul_norm.y
					count :+1
				endif
				
				if(edgemap[(i+1)*height+(j-1)])
					avgx :+ ur_norm.x; avgy :+ ur_norm.y
					count :+1
				endif
				'Down+left or right
				if(edgemap[(i-1)*height+(j+1)])
					avgx :+ dl_norm.x; avgy :+ dl_norm.y
					count :+1
				endif
				
				if(edgemap[(i+1)*height+(j+1)])
					avgx :+ dr_norm.x; avgy :+ dr_norm.y
					count :+1
				endif
				
				avgx :/ count
				avgy :/ count
				
				
				smoothedNormals[i*height+j] = vec2.create(avgx,avgy)
			
			endif
		next
		next	
	end method
end type


type vec2
	field x#,y#
	
	function create:vec2(x#,y#)
		local tmp:vec2 = new vec2
		tmp.x = x; tmp.y = y
		return tmp
	end function 
	
end type

function unpackARGB(argb:int, a% var, r% var, g% var, b% var)
	a = argb shr 24 & $ff
	r = (argb shr 16) & $ff
	g = (argb shr 8) & $ff
	b = argb & $ff
end function
