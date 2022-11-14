from threading import Thread
from queue import Queue
from collections import namedtuple
from enum import Enum
from typing import Dict
from time import sleep
from typing import Dict, Optional
 
class MessageType(Enum):
    WAKE_UP = 1
    CONNECT = 2
    INITIATE = 3
    TEST = 4
    ACCEPT = 5
    REJECT = 6
    REPORT = 7
    CHANGE_ROOT = 8

Message = namedtuple('Message', ['type', 'args'])

class EdgeType(Enum):
    BASIC = 1 # Initial state
    BRANCH = 2 # Branch in current fragment
    REJECTED = 3 # Connects to nodes in fragment, but not part of current fragment
    
class Edge:
    def __init__(self, weight: float, neighbor_id: int, neighbor_q: Queue) -> None:
        self.type: EdgeType = EdgeType.BASIC
        self.weight: float = weight
        self.neighbor_id: int = neighbor_id
        self.neighbor_q: Queue = neighbor_q
        
    def __eq__(self, other: 'Edge'):
        return (other is not None) and (self.weight == other.weight)
    
    def __lt__(self, other: 'Edge'):
        return self.weight < other.weight
    
    def __hash__(self):
        return hash(self.weight)
    
    def __str__(self):
        return f'-[{self.type}:{self.weight}]-({self.neighbor_id})'
    
    def _send(self, message: Message) -> None:
        self.neighbor_q.put(message)
        
    def connect(self, *args):
        self._send(Message(MessageType.CONNECT, args))
        
    def initiate(self, *args):
        self._send(Message(MessageType.INITIATE, args))
        
    def test(self, *args):
        self._send(Message(MessageType.TEST, args))
    
    def accept(self, *args):
        self._send(Message(MessageType.ACCEPT, args))
        
    def reject(self, *args):
        self._send(Message(MessageType.REJECT, args))
        
    def report(self, *args):
        self._send(Message(MessageType.REPORT, args))
        
    def change_root(self, *args):
        self._send(Message(MessageType.CHANGE_ROOT, args))

class NodeState(Enum):
    SLEEPING = 1 # Initial state
    FIND = 2 # While looking for min weight outgoing edge
    FOUND = 3 # At all other times

def message(func):
    def wrapper(*args, **kwargs):
        node = args[0]
        node.wake_up()
        func(*args, **kwargs)

    return wrapper

class Node:    
    def __init__(self, id: int, in_queue: Queue) -> None:
        self.id: int = id
        self.in_queue = in_queue
        
        self.state: NodeState = NodeState.SLEEPING # SN
        self.edges: Dict[int, Edge] = {}
        self.level: int = 0 # LN
        self.identity: float = 0 # FN
        
        self.best_edge: Optional[Edge] = None
        self.best_weight: float = float('inf')
        self.test_edge: Optional[Edge] = None
        self.in_branch: Optional[Edge] = None
        self.find_count: int = 0
    
    def __str__(self):
        name = f'(id:{self.id}, level:{self.level}, identity:{self.identity}, in_q:{self.in_queue.qsize()})'
        for edge in self.edges.values():
            name += f'\n  {edge}'
            if edge == self.in_branch:
                name += '*'
        return name
    
    def __hash__(self):
        return hash(self.id)
    
    @property
    def min_basic(self) -> Optional[Edge]:
        basic_edges = [e for e in self.edges.values() if e.type == EdgeType.BASIC]
        if any(basic_edges):
            return min(basic_edges)
        
    def wake_up(self) -> None:
        if self.state != NodeState.SLEEPING:
            return
        
        self._wake_up()
    
    def _wake_up(self) -> None:
        print(f'{self.id} woke up')
        min_edge = self.min_basic
        min_edge.type = EdgeType.BRANCH
        self.level = 0
        self.state = NodeState.FOUND
        self.find_count = 0
        min_edge.connect(self.id, 0)
        
    @message
    def connect(self, neighbor_id: int, level: int):   
        edge = self.edges[neighbor_id]
        
        if level < self.level:
            edge.type = EdgeType.BRANCH
            edge.initiate(self.id, self.level, self.identity, self.state)
            if self.state == NodeState.FIND:
                self.find_count += 1
        elif edge.type == EdgeType.BASIC:
            self.in_queue.put(Message(MessageType.CONNECT, (neighbor_id, level)))
        else:
            edge.initiate(self.id, self.level + 1, edge.weight, NodeState.FIND)
            
    @message
    def initiate(self, neighbor_id: int, level: int, identity: float, state: NodeState):
        self.level = level
        self.identity = identity
        self.state = state
        self.in_branch = self.edges[neighbor_id]
        self.best_edge = None
        self.best_weight = float('inf')
        
        for i in self.edges.keys() - {neighbor_id}:
            edge = self.edges[i]
            if edge.type == EdgeType.BRANCH:
                edge.initiate(self.id, level, identity, state)
                if state == NodeState.FIND:
                    self.find_count += 1
            
        if state == NodeState.FIND:
            self._test()
            
    def _test(self):
        test_edge = self.min_basic
        if test_edge != None:
            self.test_edge = test_edge
            test_edge.test(self.id, self.level, self.identity)
        else:
            self.test_edge = None
            self._report()
            
    @message
    def test(self, neighbor_id: int, level: int, identity: float):
        edge = self.edges[neighbor_id]
        
        if level > self.level:
            self.in_queue.put(Message(MessageType.TEST, (neighbor_id, level, identity)))
        elif identity != self.identity:
            edge.accept(self.id)
        else:
            if edge.type == EdgeType.BASIC:
                edge.type = EdgeType.REJECTED
            
            if self.test_edge != edge:
                edge.reject(self.id)
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
            
    def _report(self):
        if (self.find_count == 0) and (self.test_edge == None):
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
            self.in_queue.put(Message(MessageType.REPORT, (neighbor_id, weight)))
        elif weight > self.best_weight:
            self._change_root()
        elif weight == self.best_weight == float('inf'):
            print("HALT")
                
    def _change_root(self):
        if self.best_edge.type == EdgeType.BRANCH:
            self.best_edge.change_root()
        else:
            self.best_edge.connect(self.id, self.level)
            self.best_edge.type = EdgeType.BRANCH
            
    @message
    def change_root(self):
        self._change_root()


class NodeThread(Thread):
    
    def __init__(self, id: int) -> None:
        super().__init__()
        self.in_queue: Queue = Queue()
        self.node: Node = Node(id, self.in_queue)
        
    def add_neighbor(self, weight: float, neighbor: 'NodeThread') -> None:
        if neighbor.node.id not in self.node.edges:
            self.node.edges[neighbor.node.id] = Edge(weight, neighbor.node.id, neighbor.in_queue)
            neighbor.add_neighbor(weight, self)
    
    def run(self):
        exit = 0
        while True:
            sleep(0.1)
            if not self.in_queue.empty():
                exit = 0
                message = self.in_queue.get()
                print(f'{self.node.id} processing {message.type}:{message.args}')
                
                match message.type:
                    case MessageType.WAKE_UP:
                        self.node.wake_up()
                    case MessageType.CONNECT:
                        self.node.connect(*message.args)
                    case MessageType.INITIATE:
                        self.node.initiate(*message.args)
                    case MessageType.TEST:
                        self.node.test(*message.args)
                    case MessageType.ACCEPT:
                        self.node.accept(*message.args)
                    case MessageType.REJECT:
                        self.node.reject(*message.args)
                    case MessageType.REPORT:
                        self.node.report(*message.args)
                    case MessageType.CHANGE_ROOT:
                        self.node.change_root(*message.args)
                
                self.in_queue.task_done()
            elif exit < 10:
                exit += 1
            else:
                break