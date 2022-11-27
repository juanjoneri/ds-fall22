from threads import solve
from datasets.dataset_loader import Dataset

import matplotlib.pyplot as plt
import networkx as nx
from scipy.sparse.csgraph import minimum_spanning_tree
import sys
import time


def draw_graph(dataset, T, file_name='plot'):
    pos = dataset.coordinates
    
    tree_edges = set(T.edges())
    edge_color = ['r' if edge in tree_edges else 'k' for edge in dataset.G.edges()]
    
    labels = nx.get_edge_attributes(dataset.G,'weight')
    
    fig = plt.figure()
    nx.draw(dataset.G, pos, with_labels=True, edge_color=edge_color)
    # nx.draw_networkx_edge_labels(dataset.G, pos, edge_labels=labels)
    plt.savefig(f'{file_name}.png')

if __name__ == '__main__':
    
    if len(sys.argv) < 3:
        print("Please run as:")
        print("python main.py moons-100 10 -v -d")
        exit()
    
    dataset_file = 'datasets/' + sys.argv[1]
    seeds = int(sys.argv[2])
    verbose = '-v' in sys.argv
    draw = '-d' in sys.argv
    
    dataset = Dataset(dataset_file)
    G = dataset.G
    
    scipy_matrix = nx.to_scipy_sparse_matrix(G)
    scipy_mst = minimum_spanning_tree(scipy_matrix)
    print(scipy_mst)

#    start = time.time()
#    T = nx.minimum_spanning_tree(G)
#    runtime = (time.time() - start)
#    
#    T2, runtime2 = solve(G, seeds, verbose)
#    
#    print(f'NX Runtime {runtime}')
#    print(f'GHS Runtime {runtime2}')
#    
#    if (draw):
#        draw_graph(dataset, T, 'library')
#        draw_graph(dataset, T2, 'custom')