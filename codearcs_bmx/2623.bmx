; ID: 2623
; Author: Nilium
; Date: 2009-12-03 15:31:53
; Title: Input Class/Lua Script
; Description: Small script for handling input (key presses, releases, hooks, etc.)

' input.bmx
SuperStrict

Import LuGI.Core

Type Input {expose static}
	Method l_KeyDown:Int(key:Int) {rename="KeyDown" bool}
		Return KeyDown(key)
	End Method
	
	Method l_MouseDown:Int(button:Int) {rename="MouseDown" bool}
		Return MouseDown(button)
	End Method
	
	Method l_MouseX:Int()
		Return MouseX()
	End Method
	
	Method l_MouseY:Int()
		Return MouseY()
	End Method
	
	Method l_MouseZ:Int()
		Return MouseZ()
	End Method
	
	Method l_MouseXSpeed:Int()
		Return MouseXSpeed()
	End Method
	
	Method l_MouseYSpeed:Int()
		Return MouseYSpeed()
	End Method
	
	Method l_MouseZSpeed:Int()
		Return MouseZSpeed()
	End Method
End Type




-- input.lua
-- Borrowing values from the keycodes module

Mouse = {
	Left = 1,
	Right = 2,
	Middle = 3,
}

Modifier = {
	Shift = 1,
	Control = 2,
	Option = 4,
	System = 8,
	LeftMouse = 16,
	RightMouse = 32,
	MiddleMouse = 64,
}

Key = {
	Backspace = 8,
	Tab = 9,
	Clear = 12,
	Return = 13, Enter = 13,
	Escape = 27,
	Space = 32,
	PageUp = 33,
	PageDown = 34,
	End = 35,
	Home = 36,
	Left = 37,
	Up = 38,
	Right = 39,
	Down = 40,
	Select = 41,
	Print = 42,
	Execute = 43,
	PrintScreen = 44,
	Insert = 45,
	Delete = 46,
	["0"] = 48,
	["1"] = 49,
	["2"] = 50,
	["3"] = 51,
	["4"] = 52,
	["5"] = 53,
	["6"] = 54,
	["7"] = 55,
	["8"] = 56,
	["9"] = 57,
	A = 65, a = 65,
	B = 66, b = 66,
	C = 67, c = 67,
	D = 68, d = 68,
	E = 69, e = 69,
	F = 70, f = 70,
	G = 71, g = 71,
	H = 72, h = 72,
	I = 73, i = 73,
	J = 74, j = 74,
	K = 75, k = 75,
	L = 76, l = 76,
	M = 77, m = 77,
	N = 78, n = 78,
	O = 79, o = 79,
	P = 80, p = 80,
	Q = 81, q = 81,
	R = 82, r = 82,
	S = 83, s = 83,
	T = 84, t = 84,
	U = 85, u = 85,
	V = 86, v = 86,
	W = 87, w = 87,
	X = 88, x = 88,
	Y = 89, y = 89,
	Z = 90, z = 90,
	Numpad0 = 96,
	Numpad1 = 97,
	Numpad2 = 98,
	Numpad3 = 99,
	Numpad4 = 100,
	Numpad5 = 101,
	Numpad6 = 102,
	Numpad7 = 103,
	Numpad8 = 104,
	Numpad9 = 105,
	NumpadMultiply = 106,
	NumpadPlus = 107,
	NumpadMinus = 109,
	NumpadPeriod = 110,
	NumpadSlash = 111,
	F1 = 112,
	F2 = 113,
	F3 = 114,
	F4 = 115,
	F5 = 116,
	F6 = 117,
	F7 = 118,
	F8 = 119,
	F9 = 120,
	F10 = 121,
	F11 = 122,
	F12 = 123,
	Tilde = 192,
	Minus = 189,
	Equals = 187,
	OpenBracket = 219,
	CloseBracket = 221,
	Backslash = 226,
	Semicolon = 186,
	Quotes = 222,
	Comma = 188,
	Period = 190,
	Slash = 191,
	LeftShift = 160,
	RightShift = 161,
	LeftControl = 162,
	RightControl = 163,
	LeftOption = 164,
	RightOption = 165,
	LeftSystem = 91,
	RightSystem = 92,
	-- typing this wasn't fun.  should've used a regex.
}


local function _stateCheck(self, current_name, previous_name, comp)
	return function(self, ...)
		buttons = {...}
		
		for key,button in next, buttons, nil do
			buttons[button] = comp(self[current_name][button], self[previous_name][button])
		end
		
		return unpack(buttons)
	end
end

local function _downState(self, current_name, previous_name)
	return _stateCheck(self, current_name, previous_name, function(c,p) return c end)
end

local function _hitState(self, current_name, previous_name)
	return _stateCheck(self, current_name, previous_name, function(c,p) return (c and not p) end)
end

local function _upState(self, current_name, previous_name)
	return _stateCheck(self, current_name, previous_name, function(c,p) return (not c and p) end)
end

local function _bind(name)
	return function(self)
		return self._input[name]()
	end
end

_G.Input = {
	_input = (type(Input) == "table" and Input._input) or Input,		-- store old Input singleton object (bmx) and wrap it
	keys_previous = {},
	keys_current = {},
	mouse_current = {},
	mouse_previous = {},
	bindings_keys = {
		--[[
		key = {
			closure = function(key, depressed) ... end -> handled
			onPress = true|false
			onRelease = true|false
			priority = number
		}
		]]
	},
	bindings_mouse = { --[[ same model as above ]] },

	-- Get key state

	KeyDown = _downState(self, "keys_current", "keys_previous"),
	KeyHit = _hitState(self, "keys_current", "keys_previous"),
	KeyUp = _upState(self, "keys_current", "keys_previous"),

	MouseDown = _downState(self, "mouse_current", "mouse_previous"),
	MouseHit = _hitState(self, "mouse_current", "mouse_previous"),
	MouseUp = _upState(self, "mouse_current", "mouse_previous"),

	MousePosition = function(self) return self._input:MouseX(), self._input:MouseY(), self._input:MouseZ() end,
	MouseSpeed = function(self) return self._input:MouseXSpeed(), self._input:MouseYSpeed(), self._input:MouseZSpeed() end,
	MouseX = _bind("MouseX"), MouseY = _bind("MouseY"), MouseZ = _bind("MouseZ"),
	MouseXSpeed = _bind("MouseXSpeed"), MouseYSpeed = _bind("MouseYSpeed"), MouseZSpeed = _bind("MouseZSpeed"),


	-- Update

	_runBindings = function(self, binder, current, previous)
		for k,v in next, current, nil do
			if v ~= (previous[k] or false) then
				bindings = binder[k]
				if bindings then
					for bk, binding in next, bindings, nil do
						if ((v and binding.onPress) or (not v and binding.onRelease)) and binding.closure(k, v) then
							break
						end
					end
				end
			end
		end
	end,

	_updateState = function(self, states, buttons, buttonStatus)
		for k,button in next, buttons, nil do
			states[button] = buttonStatus(self._input, button)
		end
	end,

	Update = function(self)
		do local t = self.keys_previous
			self.keys_previous = self.keys_current
			self.keys_current = t
			t = self.mouse_current
			self.mouse_current = self.mouse_previous
			self.mouse_previous = t
		end
		
		self:_updateState(self.keys_current, Key, self._input.KeyDown)
		self:_updateState(self.mouse_current, Mouse, self._input.MouseDown)
		
		self:_runBindings(self.bindings_mouse, self.mouse_current, self.mouse_previous)
		self:_runBindings(self.bindings_keys, self.keys_current, self.keys_previous)
	end,


	-- Bindings

	_setBinding = function(self, binder, button, binding, onPress, onRelease, priority)
		local old_binding = binder[button]
		
		priority = priority or 0
		
		bindings = binder[button]
		if not bindings then
			binder[button] = {}
			bindings = binder[button]
		end
		
		if binding and (onPress or onRelease) then
			table.insert(bindings, {
				closure = binding,
				["onPress"] = onPress,
				["onRelease"] = onRelease,
				["priority"] = priority
			})
			table.sort(bindings,
				function(left, right) return left.priority > right.priority end)
		end
	
		return old_binding and unpack(old_binding)
	end,

	_removeBinding = function(self, binder, button, binding)
		bindings = binder[button]
		if bindings then
			for i,v in ipairs(bindings) do
				if v.closure == binding then
					table.remove(bindings, i)
					return unpack(v)
				end
			end
		end
	end,

	AddKeyBinding = function(self, key, binding, onPress, onRelease, priority)
		return self:_setBinding(self.bindings_keys, key, binding, onPress, onRelease, priority)
	end,

	RemoveKeyBinding = function(self, key, binding)
		return self:_removeBinding(self.bindings_keys, key, binding)
	end,
	
	ClearKeyBindings = function(self, key)
		self.bindings_keys[key] = {}
	end,

	AddMouseBinding = function(self, button, binding, onPress, onRelease, priority)
		return self:_setBinding(self.bindings_mouse, button, binding, onPress, onRelease, priority)
	end,

	RemoveMouseBinding = function(self, button, binding)
		return self:_removeBinding(self.bindings_mouse, button, binding)
	end,
	
	ClearMouseBindings = function(self, button)
		self.bindings_mouse[button] = {}
	end,
}
