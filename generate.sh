#!/bin/bash

cd src/main/resources/assets/compactcircuitsmod/blockstates && python3 generate_hub_blockstates.py
cd src/main/resources/assets/compactcircuitsmod/models && python3 generate_hub_block_models.py
