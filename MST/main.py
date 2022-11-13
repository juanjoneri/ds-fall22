from node import Node
from edge import Edge

        
def add_edge(n1: Node, n2: Node, weight: float) -> None:
    e1, e2 = Edge(weight, n2), Edge(weight, n1)
    n1.edges[n2.id] = e1
    n2.edges[n1.id] = e2
    return e1, e2

if __name__ == '__main__':
    node_1 = Node(1)
    node_2 = Node(2)
    node_3 = Node(3)
    node_4 = Node(4)
    node_5 = Node(5)
    node_6 = Node(6)
    
    e1, _ = add_edge(node_1, node_2, 1.1)
    e2, _ = add_edge(node_1, node_3, 1.7)
    add_edge(node_1, node_5, 2.6)
    add_edge(node_3, node_5, 3.8)
    add_edge(node_2, node_4, 3.1)
    add_edge(node_4, node_6, 3.7)
    add_edge(node_5, node_6, 2.1)
    
    node_1._wakeup()
    print(node_1)
    print(node_2)
    print(node_3)
    print(node_4)
    print(node_5)
    print(node_6)
    
    print(node_1.message_queue)