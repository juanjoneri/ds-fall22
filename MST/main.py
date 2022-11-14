from threads import NodeThread, Message, MessageType

if __name__ == '__main__':
    node_1 = NodeThread(1)
    node_2 = NodeThread(2)
    node_3 = NodeThread(3)
    node_4 = NodeThread(4)
    node_5 = NodeThread(5)
    node_6 = NodeThread(6)
    
    node_1.add_neighbor(1.1, node_2)
    node_1.add_neighbor(1.7, node_3)
    node_1.add_neighbor(2.6, node_5)
    node_3.add_neighbor(3.8, node_5)
    node_2.add_neighbor(3.1, node_4)
    node_4.add_neighbor(3.7, node_6)
    node_5.add_neighbor(2.1, node_6)
    
    # node_1.in_queue.put(Message(MessageType.WAKE_UP, None))
    node_5.in_queue.put(Message(MessageType.WAKE_UP, None))
    
    node_1.start()
    node_2.start()
    node_3.start()
    node_4.start()
    node_5.start()
    node_6.start()
    
    node_1.join()
    node_2.join()
    node_3.join()
    node_4.join()
    node_5.join()
    node_6.join()
    
    print(node_1.node)
    print(node_2.node)
    print(node_3.node)
    print(node_4.node)
    print(node_5.node)
    print(node_6.node)