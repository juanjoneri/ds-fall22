from enum import Enum

class EdgeType(Enum):
    BASIC = 1 # Initial state
    BRANCH = 2 # Branch in current fragment
    REJECTED = 3 # Connects to nodes in fragment, but not part of current fragment
    
class Edge:
    def __init__(self, weight: float, neighbor: 'Node') -> None:
        self.type: EdgeType = EdgeType.BASIC
        self.weight: float = weight
        self.neighbor: 'Node' = neighbor
        
    def __eq__(self, other: 'Edge'):
        return self.weight == other.weight
    
    def __lt__(self, other: 'Edge'):
        return self.weight < other.weight
    
    def __hash__(self):
        return hash((self.weight, self.neighbor))
    
    def __str__(self):
        return f'-[{self.type}:{self.weight}]-({self.neighbor.id})'