from enum import Enum
from edge import Edge, EdgeType
from fragment import Fragment
from typing import Set, Optional

from decorators import message

class NodeState(Enum):
    SLEEPING = 1 # Initial state
    FIND = 2 # While looking for min weight outgoing edge
    FOUND = 3 # At all other times


class Node:    
    def __init__(self, id: int) -> None:
        self.state: NodeState = NodeState.SLEEPING
        self.id: int = id
        self.edges: Set[Edge] = set()
        self.best_edge: Optional[Edge] = None
        self.fragment = Fragment(0)
    
    def __str__(self):
        name = f'({self.id})'
        for edge in self.edges:
            name += f'\n  {edge}'
        return name
    
    def _find_edge(self, neighbor: 'Node') -> Optional[Edge]:
        for edge in self.edges:
            if edge.neighbor.id == neighbor.id:
                return edge
        
        return None
    
    def __hash__(self):
        return hash(self.id)
    
    @property
    def min_basic(self) -> Optional[Edge]:
        basic_edges = [e for e in self.edges if e.type == EdgeType.BASIC]
        if not any(basic_edges):
            return None
        return min(basic_edges)
    
    @property
    def leaf(self) -> bool:
        return not any([e for e in self.edges if e.type == EdgeType.BRANCH])
    
    @property
    def inbound_edge(self) -> Optional[Edge]:
        pass
    
    def awaken(self) -> None:
        if self.state != NodeState.SLEEPING:
            return
        
        min_edge = self.min_basic
        min_edge.type = EdgeType.BRANCH
        self.state = NodeState.FOUND
        min_edge.neighbor.connect(self)

    @message        
    def connect(self, other: 'Node'):
        neighbor_edge = self._find_edge(other)
        
        while self.fragment.level > other.fragment.level:
            # wait until other fragment's level increases
            continue
        
        if self.fragment.level < other.fragment.level:
            # immediately absorve into older fragment
            self.fragment = other.fragment
        else:
            # two fragments of level L form a new fragment of level L+1
            new_fragment = Fragment(self.fragment.level + 1, neighbor_edge.weight)
            self.fragment = new_fragment
            other.fragment = new_fragment
            ## TODO: both nodes should broadcast initiate
            
    @message
    def initiate(self, fragment: Fragment):
        self.fragment = fragment
        self.state = NodeState.FIND
        for edge in self.edges:
            if edge.type == EdgeType.BRANCH:
                edge.neighbor.initiate(fragment)
                
        while True:
            min_edge = self.min_basic
            if min_edge is None:
                break
            
            if min_edge.test():
                break
            else:
                min_edge.type = EdgeType.REJECTED
                
        if self.leaf:
            pass

        
    @message
    def report(self, w: float):
        pass
    
    
    @message
    def change_core(self):
        pass
        
        
                
    @message
    def test(self, other: 'Node'):
        
        while self.fragment.level < other.fragment.level:
            # wait until fragment's level increases
            continue
        
        if self.fragment.core == other.fragment.core:
            self._find_edge(other).type == EdgeType.REJECTED
            return False
        
        elif self.fragment.level >= other.fragment.level:
            return True
            
            
        
        
def add_edge(n1: Node, n2: Node, weight: float) -> None:
    e1, e2 = Edge(weight, n2), Edge(weight, n1)
    n1.edges.add(e1)
    n2.edges.add(e2)
    return e1, e2
    
