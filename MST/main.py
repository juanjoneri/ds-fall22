from ghs import solve
from datasets.dataset_loader import Dataset

import matplotlib.pyplot as plt
import networkx as nx
from scipy.sparse.csgraph import minimum_spanning_tree
import sys
import time


def draw_graph(dataset, T, file_name='plot', weights=False):
    pos = dataset.coordinates
    
    tree_edges = set(T.edges())
    edge_color = ['r' if edge in tree_edges else 'k' for edge in dataset.G.edges()]
    
    labels = nx.get_edge_attributes(dataset.G,'weight')
    
    fig = plt.figure()
    nx.draw(dataset.G, pos, with_labels=True, edge_color=edge_color)
    if (weights):
        nx.draw_networkx_edge_labels(dataset.G, pos, edge_labels=labels)
    
    plt.savefig(f'{file_name}.png')


def compute_weight(G):
    weight = 0
    for edge in G.edges.data():
        x, y, data = edge
        weight += data['weight']
    
    return weight

def timed(func):
    def wrapper(*args, **kwargs):
        start = time.time()
        response = func(*args, **kwargs)
        runtime = (time.time() - start)
        return response, runtime

    return wrapper


@timed
def solve_nx(G):
    return nx.minimum_spanning_tree(G)

@timed
def solve_scipy(G):
    return minimum_spanning_tree(nx.to_scipy_sparse_array(G))
    

if __name__ == '__main__':
    
    if len(sys.argv) < 3:
        print('Please run as:')
        print('python main.py moons-100 20,12,6,80 -v -d -w')
        exit()
    
    dataset_file = 'datasets/' + sys.argv[1]
    seeds = list(map(int, sys.argv[2].split(',')))
    verbose = '-v' in sys.argv
    draw = '-d' in sys.argv
    weights = '-w' in sys.argv
    
    dataset = Dataset(dataset_file)
    G = dataset.G
    
    nx_mst, nx_runtime = solve_nx(G)    
    ghs_mst, ghs_runtime = solve(G, seeds, verbose)
    scipy_mst, scipy_runtime = solve_scipy(G)
    scipy_mst = nx.from_scipy_sparse_array(scipy_mst)
    
    print(f'NX Runtime {nx_runtime:.3f}s')
    print(f'GHS Runtime {ghs_runtime:.3f}s')
    print(f'SCIPY Runtime {scipy_runtime:.3f}s')
    
    nx_weight = compute_weight(nx_mst)
    ghs_weight = compute_weight(ghs_mst)
    scipy_weight = compute_weight(scipy_mst)
    
    print(f'NX Weight {nx_weight}')
    print(f'GHS Weight {ghs_weight}')
    print(f'SCIPY Weight {scipy_weight}')
    
    # Check that the solutions match
    if not (nx_weight == ghs_weight == scipy_weight):
        print('Weights do not match!')
    
    if (draw):
        draw_graph(dataset, nx_mst, 'nx', weights)
        draw_graph(dataset, ghs_mst, 'ghs', weights)
        draw_graph(dataset, scipy_mst, 'sci-py', weights)