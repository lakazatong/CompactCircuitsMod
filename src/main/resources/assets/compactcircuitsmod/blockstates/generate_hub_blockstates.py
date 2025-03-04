import itertools
import json

states = ['0', '1', '2', '3']
directions = ['north', 'east', 'south', 'west']
rotations = {'north': 0, 'east': 90, 'south': 180, 'west': 270}

blockstate_data = {"variants": {}}

for combo in itertools.product(states, repeat=6):
    state_str = ''.join(combo)
    props = (
        f"front_port_state={combo[0]},"
        f"back_port_state={combo[1]},"
        f"right_port_state={combo[2]},"
        f"left_port_state={combo[3]},"
        f"up_port_state={combo[4]},"
        f"down_port_state={combo[5]}"
    )

    for facing in directions:
        key = f"facing={facing},{props}"
        blockstate_data["variants"][key] = {
            "model": f"compactcircuitsmod:block/hub_block_{state_str}",
            "y": rotations[facing]
        }

with open('hub_block.json', 'w') as f:
    json.dump(blockstate_data, f, indent=4)
