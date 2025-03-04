import itertools
import json
import os

states = {
	'0': 'close',
	'1': 'input_open',
	'2': 'close',
	'3': 'output_open'
}

faces = ['north', 'south', 'east', 'west', 'up', 'down']

folder_name = 'block'

os.makedirs(folder_name, exist_ok=True)

for combo in itertools.product('0123', repeat=6):
	filename = f"hub_block_{''.join(combo)}.json"
	
	data = {
		"parent": "block/cube",
		"textures": {face: f"compactcircuitsmod:block/{states[state]}" for face, state in zip(faces, combo)},
		"elements": [
			{
				"from": [0, 0, 0],
				"to": [16, 16, 16],
				"faces": {face: {"texture": f"#{face}", "tintindex": tintindex} for tintindex, face in enumerate(faces)}
			}
		]
	}

	with open(os.path.join(folder_name, filename), 'w') as f:
		json.dump(data, f, indent=4)
