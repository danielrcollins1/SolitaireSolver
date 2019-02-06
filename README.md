# SolitaireSolver
Computer solver for Klondike solitaire.

This project demonstrates a lightweight, heuristic-based solver for Klondike solitaire across several different
rule parameters (based on those available for MS Solitaire in Windows 7). See parameter permutations below. 
Design is server-based to be extensible to human players or network interaction. Hidden card information is structurally
not accessible to the player. 

Currently results are as follows (N = 100,000 games per ruleset):
- Draw 1, pass 1: won 4.0%
- Draw 3, pass 3: won 7.6%
- Draw 3, pass inf: won 16.5%
- Draw 1, pass inf: won 53.5%

Note that my manual play normally with draw-3, pass-3 stands at a win rate over over 8%, so some room
for computer strategy improvement is clearly still possible. 
