from sklearn import datasets
from matplotlib import pyplot
from scipy.spatial import ConvexHull
from scipy.spatial.distance import pdist, squareform

import random
import os
import networkx as nx
import numpy as np

def _build_graph(nodes, neighbors):
    '''
    nodes: coordinates of the nodes as an np array
    edges: list containing each nodes neighbors
    '''
    n = nodes.shape[0]
    G = nx.Graph()
    G.add_nodes_from(list(range(n)))
    
    for id, node_neighbors in enumerate(neighbors):
        for neighbor in node_neighbors:
            if id == neighbor:
                continue
            
            G.add_edge(id, neighbor, weight=random.random())
            
    return G

def _init(nodes, neighbors=5):
    distmat = squareform(pdist(nodes, 'euclidean'))
    neighbors = np.sort(np.argsort(distmat, axis=1)[:, 0:neighbors])
    coordinates = dict(list(enumerate(map(tuple, nodes))))
    return _build_graph(nodes, neighbors), coordinates

def _init_blobs(n_nodes, centers, std):
    nodes, _ = datasets.make_blobs(n_samples=n_nodes, centers=centers, cluster_std=std)
    return _init(nodes)

def _init_rand(n_nodes):
    nodes = np.random.rand(n_nodes, 2)
    return _init(nodes)

def _init_halfmoons(n_nodes, noise):
    nodes, _  = datasets.make_moons(n_samples=n_nodes, noise=noise)
    return _init(nodes)

def _init_circles(n_nodes, noise):
    nodes, _  = datasets.make_circles(n_samples=n_nodes, noise=noise)
    return _init(nodes)

def _plot_graph(G, coordinates, output_file):
    os.makedirs(output_file, exist_ok=True)

    fig = pyplot.figure()
    fig.add_subplot(1,1,1)
    pyplot.axes([0, 0, 1, 1])
    nx.draw(G, coordinates, with_labels=True)
    pyplot.savefig(f'{output_file}/plot.png')

def _save_data(coordinates, edges, output_file):
    os.makedirs(output_file, exist_ok=True)

    coordinates_file = open(f'{output_file}/coordinates.csv', 'w')
    for x, y in coordinates.values():
        coordinates_file.write(f'{x}, {y}\n')
    coordinates_file.close()

    edges_file = open(f'{output_file}/edges.csv', 'w')
    for x, y, data in edges:
        edges_file.write(f'{x}, {y}, {data["weight"]}\n')
    edges_file.close()

def _save_dataset(G, coordinates, edges, output_file):
    _save_data(coordinates, G.edges.data(), output_file)
    _plot_graph(G, coordinates, output_file)

if __name__ == '__main__':

    for size in (10,):
        # Rand
        G, coordinates = _init_rand(size)
        _save_dataset(G, coordinates, G.edges, f'rand-{size}')

        # Circle
        G, coordinates = _init_circles((size//2, size//2), 0.05)
        _save_dataset(G, coordinates, G.edges, f'circle-{size}')

        # Cluster
        G, coordinates = _init_blobs(size, [(0.5, 0.5)], 0.2)
        _save_dataset(G, coordinates, G.edges, f'cluster-{size}')

        # Moon
        G, coordinates = _init_halfmoons(size, 0.1)
        _save_dataset(G, coordinates, G.edges, f'moons-{size}')