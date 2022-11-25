from threads import solve

import matplotlib.pyplot as plt
import networkx as nx


def draw(G, T, file_name='plot'):
    pos = nx.spring_layout(G, seed=1)
    
    tree_edges = set(T.edges())
    edge_color = ['r' if edge in tree_edges else 'k' for edge in G.edges()]
    
    labels = nx.get_edge_attributes(G,'weight')
    
    fig = plt.figure()
    nx.draw(G, pos, with_labels=True, edge_color=edge_color)
    nx.draw_networkx_edge_labels(G, pos, edge_labels=labels)
    plt.savefig(f'{file_name}.png')

if __name__ == '__main__':
    
    G = nx.Graph()
    G.add_nodes_from(range(1, 7))
    G.add_edge(1, 2, weight=1.1)
    G.add_edge(1, 3, weight=1.7)
    G.add_edge(1, 5, weight=2.6)
    G.add_edge(3, 5, weight=3.8)
    G.add_edge(2, 4, weight=3.1)
    G.add_edge(4, 6, weight=3.7)
    G.add_edge(5, 6, weight=2.1)
    
    T = nx.minimum_spanning_tree(G)
    draw(G, T, 'library')
    
    T2 = solve(G, 2)
    draw(G, T2, 'custom')