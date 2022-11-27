from threads import solve
from datasets.dataset_loader import Dataset

import matplotlib.pyplot as plt
import networkx as nx


def draw(dataset, T, file_name='plot'):
    pos = dataset.coordinates
    
    tree_edges = set(T.edges())
    edge_color = ['r' if edge in tree_edges else 'k' for edge in dataset.G.edges()]
    
    labels = nx.get_edge_attributes(dataset.G,'weight')
    
    fig = plt.figure()
    nx.draw(dataset.G, pos, with_labels=True, edge_color=edge_color)
    # nx.draw_networkx_edge_labels(dataset.G, pos, edge_labels=labels)
    plt.savefig(f'{file_name}.png')

if __name__ == '__main__':
    
    dataset = Dataset('datasets/cluster-100')
    G = dataset.G
    
    T = nx.minimum_spanning_tree(G)
    draw(dataset, T, 'library')

    print(G)
    
    T2 = solve(G, 10)
    draw(dataset, T2, 'custom')