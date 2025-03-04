@echo off

cd src\main\resources\assets\compactcircuitsmod\blockstates && python generate_hub_blockstates.py
cd src\main\resources\assets\compactcircuitsmod\models && python generate_hub_block_models.py
