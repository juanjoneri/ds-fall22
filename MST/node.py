from collections import deque, namedtuple
from enum import Enum
from edge import Edge, EdgeType
from typing import Dict, Optional, Deque

class NodeState(Enum):
    SLEEPING = 1 # Initial state
    FIND = 2 # While looking for min weight outgoing edge
    FOUND = 3 # At all other times
    
class MessageType(Enum):
    CONNECT = 1
    TEST = 2
    REPORT = 3

Message = namedtuple('Message', ['type', 'args'])

def procedure(func):
    def wrapper(*args, **kwargs):
        node = args[0]
        func(*args, **kwargs)
        # Currently causing an infinite loop
        # if any(node.message_queue):
        #     next_message = node.message_queue.pop()
        #     print(f'message {next_message} left in the queue')
        #     match next_message.type:
        #         case MessageType.CONNECT:
        #             node.connect(*next_message.args)
        #         case MessageType.TEST:
        #             node.test(*next_message.args)
        #         case MessageType.REPORT:
        #             node.report(*next_message.args)
    
    return wrapper

def message(func):
    @procedure
    def wrapper(*args, **kwargs):
        node = args[0]
        node._wakeup()
        func(*args, **kwargs)

    return wrapper

class Node:    
    def __init__(self, id: int) -> None:
        self.id: int = id
        
        self.state: NodeState = NodeState.SLEEPING # SN
        self.edges: Dict[int, Edge] = {}
        self.level: int = 0 # LN
        self.identity: float = 0 # FN
        
        self.best_edge: Optional[Edge] = None
        self.best_weight: float = float('inf')
        self.test_edge: Optional[Edge] = None
        self.in_branch: Optional[Edge] = None
        self.find_count: int = 0
        
        self.message_queue: Deque[Message] = deque()
        
    
    def __str__(self):
        name = f'({self.id})'
        for edge in self.edges.values():
            name += f'\n  {edge}'
        return name
    
    def __hash__(self):
        return hash(self.id)
    
    
    @property
    def min_basic(self) -> Optional[Edge]:
        """Adjacent BASIC edge of minimum weight"""
        basic_edges = [e for e in self.edges.values() if e.type == EdgeType.BASIC]
        if not any(basic_edges):
            return None
        return min(basic_edges)
    
    @procedure
    def _wakeup(self) -> None:
        if self.state != NodeState.SLEEPING:
            return
        
        min_edge = self.min_basic
        min_edge.type = EdgeType.BRANCH
        self.state = NodeState.FOUND
        min_edge.neighbor.connect(self.id, level=0)

    @message
    def connect(self, neighbor_id: int, level: int):   
        edge = self.edges[neighbor_id]
        
        if level < self.level:
            edge.type = EdgeType.BRANCH
            edge.neighbor.initiate(self.id, self.level, self.identity, self.state)
            if self.state == NodeState.FIND:
                self.find_count += 1
        elif edge.type == EdgeType.BASIC:
            self.message_queue.append(Message(MessageType.CONNECT, (neighbor_id, level)))
        else:
            edge.neighbor.initiate(self.id, self.level + 1, edge.weight, NodeState.FIND)
            
    @message
    def initiate(self, neighbor_id: int, level: int, identity: float, state: NodeState):
        self.level = level
        self.identity = identity
        self.state = state
        self.in_branch = self.edges[neighbor_id]
        
        for i in self.edges.keys() - {neighbor_id}:
            edge = self.edges[i]
            if edge.type == EdgeType.BRANCH:
                edge.neighbor.initiate(self.id, level, identity, state)
                if state == NodeState.FIND:
                    self.find_count += 1
            
        if state == NodeState.FIND:
            self._test()
              
  
    @procedure
    def _test(self):
        test_edge = self.min_basic
        if test_edge is not None:
            self.test_edge = test_edge
            test_edge.neighbor.test(self.id, self.level, self.identity)
        else:
            self._report()
        

    @message
    def test(self, neighbor_id: int, level: int, identity: float):
        edge = self.edges[neighbor_id]
        
        if level > self.level:
            self.message_queue.append(Message(MessageType.TEST, (neighbor_id, level, identity)))
        elif level != self.level:
            edge.neighbor.accept(self.id)
        else:
            if edge.type == EdgeType.BASIC:
                edge.type = EdgeType.REJECTED
            
            if self.test_edge != edge:
                edge.neighbor.reject(self.id)
            else:
                self._test()         
        
    
    @message
    def accept(self, neighbor_id: int):
        edge = self.edges[neighbor_id]
        
        self.test_edge = None
        if edge.weight < self.best_weight:
            self.best_edge = edge
            self.best_weight = edge.weight
            
        self._report()
        
    
    @message
    def reject(self, neighbor_id: int):
        edge = self.edges[neighbor_id]
        
        if edge.type == EdgeType.BASIC:
            edge.type = EdgeType.REJECTED
            self._test()
    
    
    @procedure
    def _report(self):
        if (self.find_count == 0) and (self.test_edge is None):
            self.state = NodeState.FOUND
            self.in_branch.report(self.id, self.best_weight)
    
    @message
    def report(self, neighbor_id: int, weight: float):
        if self.in_branch != self.edges[neighbor_id]:
            self.find_count -= 1
            if weight < self.best_weight:
                self.best_weight = weight
                self.best_edge = self.edges[neighbor_id]
                self._report()
            elif self.state == NodeState.FIND:
                self.message_queue.append(Message(MessageType.REPORT, (neighbor_id, weight)))
            elif weight > self.best_weight:
                self._change_root()
            elif weight == self.best_weight == float('inf'):
                print("HALT")
            
    
    @procedure
    def _change_root(self):
        if self.best_edge.type == EdgeType.BRANCH:
            self.best_edge.neighbor.change_root()
        else:
            self.best_edge.neighbor.connect(self.id, self.level)
            self.best_edge.type = EdgeType.BRANCH
    
    @message
    def change_root(self):
        self._change_root()
        
        
def add_edge(n1: Node, n2: Node, weight: float) -> None:
    e1, e2 = Edge(weight, n2), Edge(weight, n1)
    n1.edges[n2.id] = e1
    n2.edges[n1.id] = e2
    return e1, e2
    
