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
        print("python main.py moons-100 20,12,6,80 -v -d")
        exit()
    
    dataset_file = 'datasets/' + sys.argv[1]
    seeds = list(map(int, sys.argv[2].split(',')))
    verbose = '-v' in sys.argv
    draw = '-d' in sys.argv
    
    dataset = Dataset(dataset_file)
    G = dataset.G
    
    scipy_matrix = nx.to_scipy_sparse_array(G)

    start = time.time()
    T = nx.minimum_spanning_tree(G)
    nx_runtime = (time.time() - start)
    
    T2, ghs_runtime = solve(G, seeds, verbose)

    start_sci_py = time.time()
    scipy_mst = minimum_spanning_tree(scipy_matrix)
    sci_py_runtime = (time.time() - start_sci_py)
    
    print(f'NX Runtime {nx_runtime}')
    print(f'GHS Runtime {ghs_runtime}')
    print(f'SCIPY Runtime {sci_py_runtime}')

    sci_py_to_nx = nx.from_scipy_sparse_array(scipy_mst)
    
    if (draw):
        draw_graph(dataset, T, 'nx')
        draw_graph(dataset, T2, 'ghs')
        draw_graph(dataset, sci_py_to_nx, 'sci-py')